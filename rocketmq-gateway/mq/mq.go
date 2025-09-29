package mq

import (
	"context"
	"fmt"
	"log"
	"sync"

	"github.com/apache/rocketmq-client-go/v2"
	"github.com/apache/rocketmq-client-go/v2/consumer"
	"github.com/apache/rocketmq-client-go/v2/primitive"
	"github.com/apache/rocketmq-client-go/v2/producer"
)

// Subscriber stream 接口，用于向外推送消息
type Subscriber interface {
	Send(topic, body, msgId string) error
	Close()
}

// Broker 管理
type Broker struct {
	nameServer string
	producer   rocketmq.Producer
	consumers  map[string]rocketmq.PushConsumer // keyed by topic:group
	subsMu     sync.RWMutex
	subs       map[string]map[string]Subscriber // topic -> clientId -> Subscriber
	mu         sync.RWMutex // 保护 consumers map
	stats      *Stats       // 统计信息
}

// Stats 统计信息
type Stats struct {
	MessagesSent     int64
	MessagesReceived int64
	SubscribersCount int64
	ErrorsCount      int64
	mu               sync.RWMutex
}

func NewBroker(nameServer string) *Broker {
	return &Broker{
		nameServer: nameServer,
		consumers:  make(map[string]rocketmq.PushConsumer),
		subs:       make(map[string]map[string]Subscriber),
		stats:      &Stats{},
	}
}

// InitProducer 启动 producer
func (b *Broker) InitProducer() error {
	log.Printf("🔧 Initializing producer with name server: %s", b.nameServer)
	
	p, err := rocketmq.NewProducer(
		producer.WithNameServer([]string{b.nameServer}),
		producer.WithRetry(2),
		producer.WithQueueSelector(producer.NewManualQueueSelector()),
	)
	if err != nil {
		return fmt.Errorf("failed to create producer: %w", err)
	}
	
	if err := p.Start(); err != nil {
		return fmt.Errorf("failed to start producer: %w", err)
	}
	
	b.producer = p
	log.Println("✅ Producer initialized successfully")
	return nil
}

func (b *Broker) ShutdownProducer() {
	if b.producer != nil {
		log.Println("🔄 Shutting down producer...")
		if err := b.producer.Shutdown(); err != nil {
			log.Printf("❌ Error shutting down producer: %v", err)
		} else {
			log.Println("✅ Producer shut down successfully")
		}
	}
}

// Send 同步发送
func (b *Broker) Send(topic, body string) (string, error) {
	if b.producer == nil {
		b.stats.mu.Lock()
		b.stats.ErrorsCount++
		b.stats.mu.Unlock()
		return "", fmt.Errorf("producer not initialized")
	}
	
	msg := primitive.NewMessage(topic, []byte(body))
	res, err := b.producer.SendSync(context.Background(), msg)
	if err != nil {
		b.stats.mu.Lock()
		b.stats.ErrorsCount++
		b.stats.mu.Unlock()
		return "", fmt.Errorf("failed to send message: %w", err)
	}
	
	b.stats.mu.Lock()
	b.stats.MessagesSent++
	b.stats.mu.Unlock()
	
	return res.String(), nil
}

// SubscribeRegister：注册订阅客户端（clientId）用于接收消息
func (b *Broker) SubscribeRegister(topic, clientId string, sub Subscriber) {
	b.subsMu.Lock()
	defer b.subsMu.Unlock()
	if _, ok := b.subs[topic]; !ok {
		b.subs[topic] = make(map[string]Subscriber)
	}
	b.subs[topic][clientId] = sub
	
	b.stats.mu.Lock()
	b.stats.SubscribersCount++
	b.stats.mu.Unlock()
}

// Unregister 取消订阅
func (b *Broker) Unregister(topic, clientId string) {
	b.subsMu.Lock()
	defer b.subsMu.Unlock()
	if m, ok := b.subs[topic]; ok {
		if s, ok2 := m[clientId]; ok2 {
			s.Close()
			delete(m, clientId)
			
			b.stats.mu.Lock()
			b.stats.SubscribersCount--
			b.stats.mu.Unlock()
		}
		if len(m) == 0 {
			delete(b.subs, topic)
		}
	}
}

// StartConsumerForTopic：若尚未为 topic 启动 consumer，则启动并订阅，消费到的消息会分发到注册的 subscribers
func (b *Broker) StartConsumerForTopic(group, topic string) error {
	key := topic + ":" + group
	
	b.mu.Lock()
	defer b.mu.Unlock()
	
	// 检查是否已经存在该 consumer
	if _, exists := b.consumers[key]; exists {
		log.Printf("🔄 Consumer for topic %s and group %s already exists", topic, group)
		return nil
	}
	
	log.Printf("🔧 Starting consumer for topic: %s, group: %s", topic, group)
	
	c, err := rocketmq.NewPushConsumer(
		consumer.WithGroupName(group),
		consumer.WithNameServer([]string{b.nameServer}),
		consumer.WithConsumeFromWhere(consumer.ConsumeFromLastOffset),
		consumer.WithConsumerModel(consumer.Clustering),
	)
	if err != nil {
		return fmt.Errorf("failed to create consumer: %w", err)
	}

	err = c.Subscribe(topic, consumer.MessageSelector{}, func(ctx context.Context, msgs ...*primitive.MessageExt) (consumer.ConsumeResult, error) {
		for _, msg := range msgs {
			body := string(msg.Body)
			msgId := msg.MsgId
			log.Printf("📥 Received message from RocketMQ - Topic: %s, MsgId: %s", topic, msgId)
			
			b.stats.mu.Lock()
			b.stats.MessagesReceived++
			b.stats.mu.Unlock()
			
			b.broadcast(topic, body, msgId)
		}
		return consumer.ConsumeSuccess, nil
	})
	if err != nil {
		return fmt.Errorf("failed to subscribe to topic %s: %w", topic, err)
	}
	
	if err := c.Start(); err != nil {
		return fmt.Errorf("failed to start consumer: %w", err)
	}
	
	b.consumers[key] = c
	log.Printf("✅ Consumer started successfully for topic: %s, group: %s", topic, group)
	return nil
}

func (b *Broker) broadcast(topic, body, msgId string) {
	b.subsMu.RLock()
	defer b.subsMu.RUnlock()
	if m, ok := b.subs[topic]; ok {
		for _, sub := range m {
			_ = sub.Send(topic, body, msgId) // 忽略单个失败
		}
	}
}

// ShutdownAll 关闭全部 consumer
func (b *Broker) ShutdownAll() {
	b.mu.Lock()
	defer b.mu.Unlock()
	
	log.Printf("🔄 Shutting down %d consumers...", len(b.consumers))
	
	for key, c := range b.consumers {
		log.Printf("🔄 Shutting down consumer: %s", key)
		if err := c.Shutdown(); err != nil {
			log.Printf("❌ Error shutting down consumer %s: %v", key, err)
		} else {
			log.Printf("✅ Consumer %s shut down successfully", key)
		}
	}
	
	log.Println("✅ All consumers shut down")
}

// GetStats 获取统计信息
func (b *Broker) GetStats() Stats {
	b.stats.mu.RLock()
	defer b.stats.mu.RUnlock()
	return Stats{
		MessagesSent:     b.stats.MessagesSent,
		MessagesReceived: b.stats.MessagesReceived,
		SubscribersCount: b.stats.SubscribersCount,
		ErrorsCount:      b.stats.ErrorsCount,
	}
}

// ResetStats 重置统计信息
func (b *Broker) ResetStats() {
	b.stats.mu.Lock()
	defer b.stats.mu.Unlock()
	b.stats.MessagesSent = 0
	b.stats.MessagesReceived = 0
	b.stats.SubscribersCount = 0
	b.stats.ErrorsCount = 0
}
