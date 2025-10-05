package service

import (
	"context"
	"log"

	"github.com/apache/rocketmq-client-go/v2"
	"github.com/apache/rocketmq-client-go/v2/consumer"
	"github.com/apache/rocketmq-client-go/v2/primitive"

	"rocketmq-gateway/config"
	"rocketmq-gateway/mq"
	mqproto "rocketmq-gateway/proto"
)

func strPtr(s string) *string {
	return &s
}

type RocketMQGatewayServerImpl struct {
	mqproto.UnimplementedRocketMQGatewayServer
}

// SendMessage RPC 实现
func (s *RocketMQGatewayServerImpl) SendMessage(ctx context.Context, req *mqproto.SendRequest) (*mqproto.SendResponse, error) {
	msgID, err := mq.SendMessage(req.Topic, req.GetTags(), string(req.Body))
	if err != nil {
		return &mqproto.SendResponse{
			Success: false,
			Error:   strPtr(err.Error()),
		}, nil
	}
	return &mqproto.SendResponse{
		MsgId:   msgID,
		Success: true,
	}, nil
}

// Subscribe 实现流式订阅功能
func (s *RocketMQGatewayServerImpl) Subscribe(req *mqproto.SubscribeRequest, stream mqproto.RocketMQGateway_SubscribeServer) error {
	cfg := config.LoadConfig("config.yaml")
	nameservers := cfg.RocketMQ.Nameservers

	// 处理 tags 指针
	tagExpr := ""
	if req.Tags != nil {
		tagExpr = *req.Tags
	}
	log.Printf("订阅: topic=%s, group=%s, tags=%s", req.Topic, req.ConsumerGroup, tagExpr)

	// 创建消费者
	c, err := rocketmq.NewPushConsumer(
		consumer.WithNameServer(nameservers),
		consumer.WithGroupName(req.ConsumerGroup),
		consumer.WithConsumeFromWhere(consumer.ConsumeFromFirstOffset),
	)
	if err != nil {
		log.Printf("创建消费者失败: %v", err)
		return err
	}

	// 订阅主题
	err = c.Subscribe(req.Topic, consumer.MessageSelector{
		Type:       consumer.TAG,
		Expression: tagExpr,
	}, func(ctx context.Context, msgs ...*primitive.MessageExt) (consumer.ConsumeResult, error) {
		for _, msg := range msgs {
			resp := &mqproto.MessageResponse{
				Topic: msg.Topic,
				Body:  msg.Body,
				MsgId: msg.MsgId,
			}
			// 发送给 gRPC 流
			if err := stream.Send(resp); err != nil {
				log.Printf("发送给客户端失败: %v", err)
				return consumer.ConsumeRetryLater, nil
			}
			log.Printf("推送消息给客户端: topic=%s msgId=%s", msg.Topic, msg.MsgId)
		}
		return consumer.ConsumeSuccess, nil
	})
	if err != nil {
		log.Printf("订阅主题失败: %v", err)
		return err
	}

	// 启动消费者
	if err := c.Start(); err != nil {
		log.Printf("启动消费者失败: %v", err)
		return err
	}
	log.Printf("消费者已启动，监听 topic=%s group=%s", req.Topic, req.ConsumerGroup)

	// 阻塞直到客户端断开
	<-stream.Context().Done()

	// 关闭消费者
	if err := c.Shutdown(); err != nil {
		log.Printf("关闭消费者失败: %v", err)
	}
	log.Println("消费者已关闭")
	return nil
}
