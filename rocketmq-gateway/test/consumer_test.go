package test

import (
	"context"
	"fmt"
	"github.com/apache/rocketmq-client-go/v2"
	"github.com/apache/rocketmq-client-go/v2/consumer"
	"github.com/apache/rocketmq-client-go/v2/primitive"
	"log"
	"rocketmq-gateway/config"
	"testing"
)

func TestConsumer(t *testing.T) {
	// 1. 加载配置
	cfg := config.LoadConfig("../config.yaml")
	nameservers := cfg.RocketMQ.Nameservers
	groupName := cfg.RocketMQ.Producer.GroupName

	// 2. 创建消费者
	c, err := rocketmq.NewPushConsumer(
		consumer.WithNameServer(nameservers),
		consumer.WithGroupName(groupName),
		consumer.WithConsumeFromWhere(consumer.ConsumeFromFirstOffset),
	)
	if err != nil {
		log.Fatalf("创建消费者失败: %v", err)
	}

	// 3. 订阅主题和 Tag
	err = c.Subscribe("asr_transfer_topic", consumer.MessageSelector{
		Type:       consumer.TAG,
		Expression: "tag_asr_transfer_txt",
	}, func(ctx context.Context, msgs ...*primitive.MessageExt) (consumer.ConsumeResult, error) {
		for _, msg := range msgs {
			fmt.Printf("收到消息: topic=%s tag=%s body=%s\n",
				msg.Topic, msg.GetTags(), string(msg.Body))
		}
		return consumer.ConsumeSuccess, nil
	})
	if err != nil {
		log.Fatalf("订阅主题失败: %v", err)
	}

	// 4. 启动消费者
	if err = c.Start(); err != nil {
		log.Fatalf("启动消费者失败: %v", err)
	}
	log.Println("消费者已启动，持续监听消息...")
	// 5. 阻塞主线程，保持消费者运行
	select {} // 无限阻塞，直到手动终止
}
