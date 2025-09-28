package mq

import (
	"context"
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
	consumers  map[string]rocketmq.PushConsumer // keyed by topic group? we keep simple
	subsMu     sync.RWMutex
	subs       map[string]map[string]Subscriber // topic -> clientId -> Subscriber
}

func NewBroker(nameServer string) *Broker {
	return &Broker{
		nameServer: nameServer,
		consumers:  make(map[string]rocketmq.PushConsumer),
		subs:       make(map[string]map[string]Subscriber),
	}
}

// InitProducer 启动 producer
func (b *Broker) InitProducer() error {
	p, err := rocketmq.NewProducer(
		producer.WithNameServer([]string{b.nameServer}),
		producer.WithRetry(2),
	)
	if err != nil {
		return err
	}
	if err := p.Start(); err != nil {
		return err
	}
	b.producer = p
	return nil
}

func (b *Broker) ShutdownProducer() {
	if b.producer != nil {
		_ = b.producer.Shutdown()
	}
}

// Send 同步发送
func (b *Broker) Send(topic, body string) (string, error) {
	msg := primitive.NewMessage(topic, []byte(body))
	res, err := b.producer.SendSync(context.Background(), msg)
	if err != nil {
		return "", err
	}
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
}

// Unregister 取消订阅
func (b *Broker) Unregister(topic, clientId string) {
	b.subsMu.Lock()
	defer b.subsMu.Unlock()
	if m, ok := b.subs[topic]; ok {
		if s, ok2 := m[clientId]; ok2 {
			s.Close()
			delete(m, clientId)
		}
		if len(m) == 0 {
			delete(b.subs, topic)
		}
	}
}

// StartConsumerForTopic：若尚未为 topic 启动 consumer，则启动并订阅，消费到的消息会分发到注册的 subscribers
func (b *Broker) StartConsumerForTopic(group, topic string) error {
	key := topic + ":" + group
	// 简化，不做重复启动校验；实际可用 map+锁管理
	c, err := rocketmq.NewPushConsumer(
		consumer.WithGroupName(group),
		consumer.WithNameServer([]string{b.nameServer}),
	)
	if err != nil {
		return err
	}

	err = c.Subscribe(topic, consumer.MessageSelector{}, func(ctx context.Context, msgs ...*primitive.MessageExt) (consumer.ConsumeResult, error) {
		for _, msg := range msgs {
			body := string(msg.Body)
			msgId := msg.MsgId
			b.broadcast(topic, body, msgId)
		}
		return consumer.ConsumeSuccess, nil
	})
	if err != nil {
		return err
	}
	if err := c.Start(); err != nil {
		return err
	}
	b.consumers[key] = c
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
	for _, c := range b.consumers {
		_ = c.Shutdown()
	}
}
