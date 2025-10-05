package mq

import (
	"context"
	"fmt"
	"github.com/apache/rocketmq-client-go/v2"
	"github.com/apache/rocketmq-client-go/v2/consumer"
	"github.com/apache/rocketmq-client-go/v2/primitive"
	"log"
)

type ConsumerService struct {
	consumer rocketmq.PushConsumer
}

func NewConsumer(nameservers []string, groupName string, topic string, tag string, handler func(string) error) (*ConsumerService, error) {
	c, err := rocketmq.NewPushConsumer(
		consumer.WithNameServer(nameservers),
		consumer.WithGroupName(groupName),
		consumer.WithConsumeFromWhere(consumer.ConsumeFromFirstOffset),
	)
	if err != nil {
		return nil, fmt.Errorf("创建消费者失败: %w", err)
	}

	// 注册回调
	err = c.Subscribe(topic, consumer.MessageSelector{
		Type:       consumer.TAG,
		Expression: tag,
	}, func(ctx context.Context, msgs ...*primitive.MessageExt) (consumer.ConsumeResult, error) {
		for _, msg := range msgs {
			body := string(msg.Body)
			log.Printf("收到消息: topic=%s tag=%s body=%s\n", msg.Topic, msg.GetTags(), body)

			// 调用外部处理函数
			if handler != nil {
				if err := handler(body); err != nil {
					log.Printf("处理消息失败: %v", err)
					return consumer.ConsumeRetryLater, nil
				}
			}
		}
		return consumer.ConsumeSuccess, nil
	})
	if err != nil {
		return nil, fmt.Errorf("订阅主题失败: %w", err)
	}

	return &ConsumerService{consumer: c}, nil
}

func (cs *ConsumerService) Start() error {
	return cs.consumer.Start()
}

func (cs *ConsumerService) Shutdown() error {
	return cs.consumer.Shutdown()
}
