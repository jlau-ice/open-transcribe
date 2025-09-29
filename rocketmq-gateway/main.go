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
	"google.golang.org/grpc/codes"
	"google.golang.org/grpc/status"
)

type srv struct {
	pb.UnimplementedRocketMQGatewayServer
	broker *mq.Broker
	// client id 自增生成
	ctr uint64
}

// SendMessage rpc 实现
func (s *srv) SendMessage(ctx context.Context, req *pb.SendRequest) (*pb.SendResponse, error) {
	if req.Topic == "" {
		return nil, status.Error(codes.InvalidArgument, "topic cannot be empty")
	}
	if req.Body == "" {
		return nil, status.Error(codes.InvalidArgument, "message body cannot be empty")
	}

	log.Printf("📤 Sending message to topic: %s, body: %s", req.Topic, req.Body)
	
	res, err := s.broker.Send(req.Topic, req.Body)
	if err != nil {
		log.Printf("❌ Failed to send message: %v", err)
		return nil, status.Errorf(codes.Internal, "failed to send message: %v", err)
	}
	
	log.Printf("✅ Message sent successfully: %s", res)
	return &pb.SendResponse{Result: res}, nil
}

// Subscribe rpc 实现：服务端流
func (s *srv) Subscribe(req *pb.SubscribeRequest, stream pb.RocketMQGateway_SubscribeServer) error {
	if req.Topic == "" {
		return status.Error(codes.InvalidArgument, "topic cannot be empty")
	}

	clientId := req.ClientId
	if clientId == "" {
		clientId = fmt.Sprintf("cli-%d", atomic.AddUint64(&s.ctr, 1))
	}

	log.Printf("🔔 Client %s subscribing to topic: %s", clientId, req.Topic)

	// 定义 Subscriber 实现，写到 stream
	sub := &streamSubscriber{
		stream: stream,
		done:   make(chan struct{}),
	}
	
	// 注册订阅
	s.broker.SubscribeRegister(req.Topic, clientId, sub)

	// 确保对应 topic 有 consumer 在跑（使用默认 group）
	if err := s.broker.StartConsumerForTopic("gateway-group", req.Topic); err != nil {
		log.Printf("❌ Failed to start consumer for topic %s: %v", req.Topic, err)
		s.broker.Unregister(req.Topic, clientId)
		return status.Errorf(codes.Internal, "failed to start consumer: %v", err)
	}

	log.Printf("✅ Client %s subscribed successfully to topic: %s", clientId, req.Topic)

	// 等待直到客户端断开或发生错误
	<-sub.done

	// 注销
	s.broker.Unregister(req.Topic, clientId)
	log.Printf("🔚 Client %s unsubscribed from topic: %s", clientId, req.Topic)
	
	return nil
}

// HealthCheck 健康检查实现
func (s *srv) HealthCheck(ctx context.Context, req *pb.HealthCheckRequest) (*pb.HealthCheckResponse, error) {
	// 检查RocketMQ连接状态
	if s.broker == nil {
		log.Printf("🏥 Health check failed: Broker not initialized")
		return &pb.HealthCheckResponse{
			Status:  pb.HealthCheckResponse_NOT_SERVING,
			Message: "Broker not initialized",
		}, nil
	}

	// 获取统计信息
	stats := s.broker.GetStats()
	
	log.Printf("🏥 Health check requested for service: %s", req.Service)
	log.Printf("📊 Current stats - Sent: %d, Received: %d, Subscribers: %d, Errors: %d", 
		stats.MessagesSent, stats.MessagesReceived, stats.SubscribersCount, stats.ErrorsCount)
	
	return &pb.HealthCheckResponse{
		Status:  pb.HealthCheckResponse_SERVING,
		Message: "RocketMQ Gateway is healthy",
	}, nil
}

// streamSubscriber 实现 mq.Subscriber
type streamSubscriber struct {
	stream pb.RocketMQGateway_SubscribeServer
	done   chan struct{}
}

func (s *streamSubscriber) Send(topic, body, msgId string) error {
	// 发送到 gRPC stream（非阻塞设计可加队列）
	log.Printf("📨 Forwarding message to client - Topic: %s, MsgId: %s, Body: %s", topic, msgId, body)
	
	err := s.stream.Send(&pb.MessageResponse{
		Topic: topic,
		Body:  body,
		MsgId: msgId,
	})
	if err != nil {
		log.Printf("❌ Failed to send message to client: %v", err)
		// 若发送失败（客户端断开），关闭 done 通知 Subscribe 返回
		close(s.done)
		return err
	}
	
	log.Printf("✅ Message forwarded successfully to client - MsgId: %s", msgId)
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
	log.Println("🚀 Starting RocketMQ Gateway...")
	
	cfg := LoadConfig()
	log.Printf("📋 Configuration loaded - Server: :%d, RocketMQ: %s", cfg.Server.Port, cfg.RocketMQ.NameServer)

	broker := mq.NewBroker(cfg.RocketMQ.NameServer)
	if err := broker.InitProducer(); err != nil {
		log.Fatalf("❌ Failed to initialize producer: %v", err)
	}
	defer broker.ShutdownProducer()
	defer broker.ShutdownAll()
	log.Println("✅ Producer initialized successfully")

	lis, err := net.Listen("tcp", fmt.Sprintf(":%d", cfg.Server.Port))
	if err != nil {
		log.Fatalf("❌ Failed to listen on port %d: %v", cfg.Server.Port, err)
	}
	
	grpcServer := grpc.NewServer()
	pb.RegisterRocketMQGatewayServer(grpcServer, &srv{broker: broker})
	
	log.Printf("🌐 gRPC gateway listening on :%d", cfg.Server.Port)
	log.Println("🎯 Available services:")
	log.Println("   - SendMessage: Send messages to RocketMQ topics")
	log.Println("   - Subscribe: Subscribe to RocketMQ topics via streaming")
	
	if err := grpcServer.Serve(lis); err != nil {
		log.Fatalf("❌ gRPC server failed: %v", err)
	}
}
