package mq

import (
	"context"
	"fmt"
	"github.com/apache/rocketmq-client-go/v2"
	"github.com/apache/rocketmq-client-go/v2/primitive"
	"github.com/apache/rocketmq-client-go/v2/producer"
	"log"
	"rocketmq-gateway/config"
)

var (
	globalProducer rocketmq.Producer
)

// InitProducer 在项目启动时调用，初始化全局生产者
func InitProducer() error {
	cfg := config.LoadConfig("config.yaml")
	nameServers := cfg.RocketMQ.Nameservers
	groupName := cfg.RocketMQ.Producer.GroupName
	log.Println("初始化生产者")
	if len(nameServers) == 0 || groupName == "" {
		return fmt.Errorf("NameServer 或 GroupName 未配置")
	}

	p, err := rocketmq.NewProducer(
		producer.WithGroupName(groupName),
		producer.WithNameServer(nameServers),
	)
	if err != nil {
		return fmt.Errorf("创建生产者失败: %w", err)
	}

	if err := p.Start(); err != nil {
		return fmt.Errorf("启动生产者失败: %w", err)
	}

	globalProducer = p
	return nil
}

// ShutdownProducer 项目退出时调用
func ShutdownProducer() {
	if globalProducer != nil {
		if err := globalProducer.Shutdown(); err != nil {
			log.Printf("关闭生产者失败: %v", err)
		}
	}
}

// SendMessage 发送消息
func SendMessage(topic, tag, message string) (string, error) {
	if globalProducer == nil {
		return "", fmt.Errorf("生产者未初始化")
	}

	msg := &primitive.Message{
		Topic: topic,
		Body:  []byte(message),
	}
	msg.WithTag(tag)

	res, err := globalProducer.SendSync(context.Background(), msg)
	if err != nil {
		return "", fmt.Errorf("发送消息失败: %w", err)
	}
	log.Printf("发送成功: topic=%s, tag=%s, msgID=%s", topic, tag, res.MsgID)
	return res.MsgID, nil
}

//func SendMessage(topic string, tag string, message string) (string, error) {
//	// 1. 加载配置
//	cfg := config.LoadConfig("../config.yaml")
//	nameServers := cfg.RocketMQ.Nameservers
//	groupName := cfg.RocketMQ.Producer.GroupName
//	if len(nameServers) == 0 || groupName == "" {
//		log.Fatal("NameServer 或 GroupName 未配置")
//	}
//	// 2. 创建生产者
//	p, err := rocketmq.NewProducer(
//		producer.WithGroupName(groupName),
//		producer.WithNameServer(nameServers),
//	)
//	if err != nil {
//		log.Fatalf("创建生产者失败: %v", err)
//	}
//
//	// 3. 启动生产者
//	if err := p.Start(); err != nil {
//		log.Fatalf("启动生产者失败: %v", err)
//	}
//	defer func() {
//		if err := p.Shutdown(); err != nil {
//			log.Printf("关闭生产者失败: %v", err)
//		}
//	}()
//
//	msg := &primitive.Message{
//		Topic: topic,
//		Body:  []byte(message),
//	}
//	msg.WithTag(tag)
//	res, err := p.SendSync(context.Background(), msg)
//	if err != nil {
//		log.Printf("发送消息失败: %v", err)
//	} else {
//		log.Printf("发送成功: result=%s", res.String())
//	}
//	return res.MsgID, nil
//}
