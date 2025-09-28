package main

import (
	"context"
	"fmt"
	"log"
	"net"
	pb "rocketmq-gateway/proto"
	"sync/atomic"

	"rocketmq-gateway/mq"

	"google.golang.org/grpc"
)

type srv struct {
	pb.UnimplementedRocketMQGatewayServer
	broker *mq.Broker
	// client id 自增生成
	ctr uint64
}

// SendMessage rpc 实现
func (s *srv) SendMessage(ctx context.Context, req *pb.SendRequest) (*pb.SendResponse, error) {
	res, err := s.broker.Send(req.Topic, req.Body)
	if err != nil {
		return nil, err
	}
	return &pb.SendResponse{Result: res}, nil
}

// Subscribe rpc 实现：服务端流
func (s *srv) Subscribe(req *pb.SubscribeRequest, stream pb.RocketMQGateway_SubscribeServer) error {
	clientId := req.ClientId
	if clientId == "" {
		clientId = fmt.Sprintf("cli-%d", atomic.AddUint64(&s.ctr, 1))
	}

	// 定义 Subscriber 实现，写到 stream
	sub := &streamSubscriber{
		stream: stream,
		done:   make(chan struct{}),
	}
	// 注册
	s.broker.SubscribeRegister(req.Topic, clientId, sub)

	// 确保对应 topic 有 consumer 在跑（使用默认 group）
	_ = s.broker.StartConsumerForTopic("gateway-group", req.Topic)

	// 等待直到客户端断开或发生错误
	<-sub.done

	// 注销
	s.broker.Unregister(req.Topic, clientId)
	return nil
}

// streamSubscriber 实现 mq.Subscriber
type streamSubscriber struct {
	stream pb.RocketMQGateway_SubscribeServer
	done   chan struct{}
}

func (s *streamSubscriber) Send(topic, body, msgId string) error {
	// 发送到 gRPC stream（非阻塞设计可加队列）
	err := s.stream.Send(&pb.MessageResponse{
		Topic: topic,
		Body:  body,
		MsgId: msgId,
	})
	if err != nil {
		// 若发送失败（客户端断开），关闭 done 通知 Subscribe 返回
		close(s.done)
		return err
	}
	return nil
}

func (s *streamSubscriber) Close() {
	select {
	case <-s.done:
		// already closed
	default:
		close(s.done)
	}
}

func main() {
	cfg := LoadConfig()

	broker := mq.NewBroker(cfg.RocketMQ.NameServer)
	if err := broker.InitProducer(); err != nil {
		log.Fatalf("init producer failed: %v", err)
	}
	defer broker.ShutdownProducer()
	defer broker.ShutdownAll()

	lis, err := net.Listen("tcp", fmt.Sprintf(":%d", cfg.Server.Port))
	if err != nil {
		log.Fatalf("listen err: %v", err)
	}
	grpcServer := grpc.NewServer()
	pb.RegisterRocketMQGatewayServer(grpcServer, &srv{broker: broker})
	fmt.Printf("gRPC gateway listening on :%d\n", cfg.Server.Port)
	if err := grpcServer.Serve(lis); err != nil {
		log.Fatalf("serve err: %v", err)
	}
}
