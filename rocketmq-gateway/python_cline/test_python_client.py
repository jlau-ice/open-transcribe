#!/usr/bin/env python3
"""
Python客户端测试脚本
用于测试RocketMQ Gateway的gRPC订阅功能
"""

import grpc
import time
import threading
from concurrent import futures

# 导入生成的protobuf文件
# 注意：你需要先运行 generate_python_grpc.py 生成Python的gRPC代码

import sys
import os
sys.path.append('python_grpc')

try:
    import mq_pb2
    import mq_pb2_grpc
except ImportError:
    print("❌ 请先生成Python的gRPC代码:")
    print("   python generate_python_grpc.py")
    exit(1)


def test_send_message():
    """测试发送消息"""
    print("📤 测试发送消息...")
    
    # 连接到gRPC服务器
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = mq_pb2_grpc.RocketMQGatewayStub(channel)
        
        # 发送消息
        request = mq_pb2.SendRequest(
            topic="test_topic",
            body=b"Hello from Python client!",
            tags="test_tag"
        )
        
        try:
            response = stub.SendMessage(request)
            if response.success:
                print(f"✅ 消息发送成功! Message ID: {response.msgId}")
            else:
                print(f"❌ 消息发送失败: {response.error}")
        except grpc.RpcError as e:
            print(f"❌ gRPC错误: {e}")


def test_subscribe_messages():
    """测试订阅消息"""
    print("📥 测试订阅消息...")
    
    # 连接到gRPC服务器
    with grpc.insecure_channel('localhost:50051') as channel:
        stub = mq_pb2_grpc.RocketMQGatewayStub(channel)
        
        # 订阅消息
        request = mq_pb2.SubscribeRequest(
            topic="test_topic",
            consumerGroup="python_test_group",
            tags="test_tag"
        )
        
        try:
            print("🔄 开始订阅，等待消息...")
            for response in stub.Subscribe(request):
                print(f"📨 收到消息:")
                print(f"   Topic: {response.topic}")
                print(f"   Message ID: {response.msgId}")
                print(f"   Body: {response.body.decode('utf-8')}")
                print("---")
        except grpc.RpcError as e:
            print(f"❌ 订阅错误: {e}")


def main():
    """主函数"""
    print("🚀 RocketMQ Gateway Python客户端测试")
    print("=" * 50)
    
    # 在后台线程中启动订阅
    subscribe_thread = threading.Thread(target=test_subscribe_messages)
    subscribe_thread.daemon = True
    subscribe_thread.start()
    
    # 等待一下让订阅建立
    time.sleep(2)
    
    # 发送一些测试消息
    for i in range(5):
        test_send_message()
        time.sleep(1)
    
    # 等待订阅线程运行
    print("⏳ 等待消息接收...")
    time.sleep(10)
    
    print("✅ 测试完成")


if __name__ == "__main__":
    main()
