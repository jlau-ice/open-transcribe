package main

import (
	"google.golang.org/grpc"
	"log"
	"net"
	"rocketmq-gateway/mq"
	mqproto "rocketmq-gateway/proto"
	"rocketmq-gateway/service"
)

func main() {
	// 初始化 Producer
	if err := mq.InitProducer(); err != nil {
		log.Fatalf("初始化生产者失败: %v", err)
	}
	defer mq.ShutdownProducer()

	// 启动 gRPC 服务
	lis, err := net.Listen("tcp", ":50051")
	if err != nil {
		log.Fatalf("监听失败: %v", err)
	}
	grpcServer := grpc.NewServer()
	mqproto.RegisterRocketMQGatewayServer(grpcServer, &service.RocketMQGatewayServerImpl{})
	log.Println("🚀 gRPC 服务已启动，监听端口 50051")
	if err := grpcServer.Serve(lis); err != nil {
		log.Fatalf("启动 gRPC 服务失败: %v", err)
	}
	//// 初始化生产者
	//if err := mq.InitProducer(); err != nil {
	//	log.Fatal(err)
	//}
	//defer func() {
	//	mq.ShutdownProducer()
	//	log.Println("生产者已关闭")
	//}()
	//
	//// 捕获 Ctrl+C 信号
	//quit := make(chan os.Signal, 1)
	//signal.Notify(quit, syscall.SIGINT, syscall.SIGTERM)
	//
	//// 开始交互式输入
	//reader := bufio.NewReader(os.Stdin)
	//fmt.Println("🚀 RocketMQ Producer 已启动，输入消息按回车发送，Ctrl+C 或 Ctrl+D 退出")
	//for {
	//	fmt.Print("> ")
	//
	//	text, err := reader.ReadString('\n')
	//	if err != nil {
	//		if err.Error() == "EOF" {
	//			fmt.Println("\n输入流已关闭，程序退出...")
	//			return
	//		}
	//		log.Printf("读取输入失败: %v", err)
	//		continue
	//	}
	//
	//	text = strings.TrimSpace(text)
	//	if text == "" {
	//		continue
	//	}
	//
	//	_, err = mq.SendMessage("asr_transfer_topic", "tag_asr_transfer_txt", text)
	//	if err != nil {
	//		log.Printf("发送失败: %v", err)
	//	} else {
	//		log.Printf("消息已发送: %s", text)
	//	}
	//
	//	// 检查是否有退出信号
	//	select {
	//	case <-quit:
	//		fmt.Println("\n收到退出信号，程序退出...")
	//		return
	//	default:
	//	}
	//}
}
