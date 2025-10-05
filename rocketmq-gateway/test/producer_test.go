package test

import (
	"context"
	"fmt"
	"github.com/apache/rocketmq-client-go/v2"
	"github.com/apache/rocketmq-client-go/v2/primitive"
	"github.com/apache/rocketmq-client-go/v2/producer"
	"testing"
	"time"
)

func TestProducer(*testing.T) {
	// 创建生产者
	p, err := rocketmq.NewProducer(
		producer.WithGroupName("testProducerGroup"),
		producer.WithNameServer([]string{"127.0.0.1:9876"}), // NameServer 地址
	)
	if err != nil {
		panic(err)
	}

	// 启动生产者
	if err := p.Start(); err != nil {
		panic(err)
	}
	defer p.Shutdown()

	// 发送 10 条消息
	for i := 0; i < 10; i++ {
		msg := &primitive.Message{
			Topic: "asr_transfer_topic", // 主题
			Body:  []byte(fmt.Sprintf("Hello RocketMQ %d", i)),
		}
		msg.WithTag("tag_asr_transfer_txt") // 设置 tag

		res, err := p.SendSync(context.Background(), msg)
		if err != nil {
			fmt.Printf("发送消息失败: %v\n", err)
		} else {
			fmt.Printf("发送成功: result=%s\n", res.String())
		}
		time.Sleep(time.Second)
	}
}
