#!/usr/bin/env python3
"""
Python客户端持续订阅脚本
用于测试RocketMQ Gateway的gRPC订阅功能，持续监听消息。
"""

import grpc
import time
import threading
import sys
import os

# 配置导入路径
sys.path.append('python_grpc')

try:
    # 导入生成的protobuf文件
    import mq_pb2
    import mq_pb2_grpc
except ImportError:
    print("❌ 导入错误：请确保您已运行 'python generate_python_grpc.py' 生成 gRPC Python 代码。")
    exit(1)

# gRPC 服务器地址
SERVER_ADDRESS = 'localhost:50051'
# 订阅的 Topic
TOPIC = "asr_transfer_topic"
# 消费者组
CONSUMER_GROUP = "python_long_running_consumer"
# Tag 过滤
TAGS = "tag_asr_transfer_txt"


def subscribe_messages_forever():
    """
    持续订阅消息的函数。
    它会一直运行，直到发生 gRPC 错误或程序被中断。
    """
    print(f"📥 尝试连接到 gRPC 服务器: {SERVER_ADDRESS}")

    # 使用 with 语句确保 channel 在退出时关闭
    with grpc.insecure_channel(SERVER_ADDRESS) as channel:
        stub = mq_pb2_grpc.RocketMQGatewayStub(channel)

        # 订阅请求
        request = mq_pb2.SubscribeRequest(
            topic=TOPIC,
            consumerGroup=CONSUMER_GROUP,
            tags=TAGS
        )

        try:
            print(f"🔄 **开始持续订阅**，Topic: '{TOPIC}', Group: '{CONSUMER_GROUP}', Tags: '{TAGS}'")
            # Subscribe 方法返回一个迭代器，会阻塞直到收到消息或连接中断
            for response in stub.Subscribe(request):
                print("-" * 30)
                print(f"📨 收到新消息 ({time.strftime('%Y-%m-%d %H:%M:%S')}):")
                print(f"   Topic: {response.topic}")
                print(f"   Message ID: {response.msgId}")
                # 尝试解码消息体，如果是非 UTF-8 编码，可能会失败
                try:
                    body_text = response.body.decode('utf-8')
                    print(f"   Body (UTF-8): {body_text}")
                except UnicodeDecodeError:
                    print(f"   Body (Raw): {response.body!r} (无法以 UTF-8 解码)")

            # 如果循环自然退出 (例如，服务器关闭连接)
            print("❗ gRPC 订阅流已关闭 (可能是服务器端断开连接)。")

        except grpc.RpcError as e:
            # 捕获 gRPC 错误，如连接失败、超时等
            print(f"❌ 严重 gRPC 订阅错误发生: {e}")
            details = e.details()
            status_code = e.code()
            print(f"   状态码: {status_code.name}")
            print(f"   详情: {details}")
        except Exception as e:
            print(f"❌ 发生未知错误: {e}")
        finally:
            print("⏳ 尝试在 5 秒后重新连接...")
            time.sleep(5)
            # 这里的简单实现没有重连逻辑，实际应用中应该加入循环重试


def main():
    """主函数，设置并运行持续订阅"""
    print("🚀 RocketMQ Gateway Python客户端持续订阅测试")
    print("=" * 60)

    # 启动订阅线程
    # 使用守护线程，这样主线程退出时它也会退出
    subscribe_thread = threading.Thread(target=subscribe_messages_forever)
    subscribe_thread.daemon = True
    subscribe_thread.start()

    print("\n---")
    print(f"✅ 持续订阅线程已在后台启动。")
    print("📢 请通过其他客户端向 'asr_transfer_topic' 发送消息。")
    print("---")
    print("💡 按 Ctrl+C 退出程序。")
    print("-" * 60 + "\n")

    # 主线程保持运行状态，等待订阅线程工作
    try:
        while True:
            time.sleep(1)  # 避免 CPU 空转
    except KeyboardInterrupt:
        print("\n👋 收到中断信号 (Ctrl+C)，程序退出。")


if __name__ == "__main__":
    main()