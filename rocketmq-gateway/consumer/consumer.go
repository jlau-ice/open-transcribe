package consumer

import (
	"bytes"
	"context"
	"fmt"
	"github.com/apache/rocketmq-client-go/v2"
	"net/http"

	"github.com/apache/rocketmq-client-go/v2/consumer"
	"github.com/apache/rocketmq-client-go/v2/primitive"
)

var c rocketmq.PushConsumer

// Init 初始化消费者
func Init(nameServer, group, topic, pythonCallback string) error {
	var err error
	c, err = rocketmq.NewPushConsumer(
		consumer.WithGroupName(group),
		consumer.WithNameServer([]string{nameServer}),
	)
	if err != nil {
		return err
	}

	// 订阅消息
	err = c.Subscribe(topic, consumer.MessageSelector{}, func(ctx context.Context, msgs ...*primitive.MessageExt) (consumer.ConsumeResult, error) {
		for _, msg := range msgs {
			fmt.Printf("Got MQ message: %s\n", string(msg.Body))

			// 回调 Python
			_, _ = http.Post(pythonCallback, "text/plain", bytes.NewBuffer(msg.Body))
		}
		return consumer.ConsumeSuccess, nil
	})
	if err != nil {
		return err
	}

	return c.Start()
}

// Shutdown 关闭消费者
func Shutdown() {
	if c != nil {
		_ = c.Shutdown()
	}
}
