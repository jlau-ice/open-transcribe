package test

import (
	"context"
	"fmt"
	"testing"
	"time"

	"github.com/apache/rocketmq-client-go/v2"
	"github.com/apache/rocketmq-client-go/v2/consumer"
	"github.com/apache/rocketmq-client-go/v2/primitive"
)

func TestConsumer(t *testing.T) {
	c, _ := rocketmq.NewPushConsumer(
		consumer.WithGroupName("testGroup"),
		consumer.WithNameServer([]string{"127.0.0.1:9876"}),
		consumer.WithConsumeFromWhere(consumer.ConsumeFromFirstOffset),
	)

	// 订阅主题 + tag
	err := c.Subscribe("asr_transfer_topic", consumer.MessageSelector{
		Type:       consumer.TAG,
		Expression: "tag_asr_transfer_txt",
	}, func(ctx context.Context, msgs ...*primitive.MessageExt) (consumer.ConsumeResult, error) {
		for _, msg := range msgs {
			fmt.Printf("消费消息: topic=%s, tag=%s, body=%s\n",
				msg.Topic, msg.GetTags(), string(msg.Body))
		}
		return consumer.ConsumeSuccess, nil
	})
	if err != nil {
		fmt.Println("订阅失败:", err)
		return
	}

	_ = c.Start()
	fmt.Println("消费者启动成功，等待消息...")
	time.Sleep(time.Hour)
	_ = c.Shutdown()
}
