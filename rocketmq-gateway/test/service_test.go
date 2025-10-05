package test

import (
	"context"
	"fmt"
	"log"
	"testing"
	"time"

	"google.golang.org/grpc"
	"google.golang.org/grpc/credentials/insecure"

	mqproto "rocketmq-gateway/proto"
)

func TestService(t *testing.T) {
	// 连接到gRPC服务器
	conn, err := grpc.Dial("localhost:50051", grpc.WithTransportCredentials(insecure.NewCredentials()))
	if err != nil {
		log.Fatalf("连接失败: %v", err)
	}
	defer conn.Close()

	client := mqproto.NewRocketMQGatewayClient(conn)

	// 测试发送消息
	fmt.Println("📤 测试发送消息...")
	sendResp, err := client.SendMessage(context.Background(), &mqproto.SendRequest{
		Topic: "test_topic",
		Body:  []byte("Hello from Go test client!"),
		Tags:  stringPtr("test_tag"),
	})
	if err != nil {
		log.Printf("发送消息失败: %v", err)
	} else {
		fmt.Printf("✅ 消息发送成功: %s\n", sendResp.MsgId)
	}

	// 测试订阅消息
	fmt.Println("📥 测试订阅消息...")
	stream, err := client.Subscribe(context.Background(), &mqproto.SubscribeRequest{
		Topic:         "test_topic",
		ConsumerGroup: "go_test_group",
		Tags:          stringPtr("test_tag"),
	})
	if err != nil {
		log.Printf("订阅失败: %v", err)
		return
	}

	// 在goroutine中接收消息
	go func() {
		for {
			resp, err := stream.Recv()
			if err != nil {
				log.Printf("接收消息失败: %v", err)
				return
			}
			fmt.Printf("📨 收到消息: %s (ID: %s)\n", string(resp.Body), resp.MsgId)
		}
	}()

	// 等待一段时间
	fmt.Println("⏳ 等待消息...")
	time.Sleep(10 * time.Second)
	fmt.Println("✅ 测试完成")
}

func stringPtr(s string) *string {
	return &s
}
