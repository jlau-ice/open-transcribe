package producer

import (
	"context"
	"fmt"
	"github.com/apache/rocketmq-client-go/v2"
	"github.com/apache/rocketmq-client-go/v2/primitive"
	"github.com/apache/rocketmq-client-go/v2/producer"
)

var p rocketmq.Producer

// Init 初始化生产者
func Init(nameServer string) error {
	var err error
	p, err = rocketmq.NewProducer(
		producer.WithNameServer([]string{nameServer}),
		producer.WithRetry(2),
	)
	if err != nil {
		return err
	}
	return p.Start()
}

// Shutdown 关闭生产者
func Shutdown() {
	if p != nil {
		_ = p.Shutdown()
	}
}

// SendMessage 发送消息
func SendMessage(topic, body string) (string, error) {
	msg := primitive.NewMessage(topic, []byte(body))
	res, err := p.SendSync(context.Background(), msg)
	if err != nil {
		return "", err
	}
	return fmt.Sprintf("SendResult: %s", res.String()), nil
}
