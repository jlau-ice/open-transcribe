import grpc
import mq_pb2
import mq_pb2_grpc

def run():
    # 连接 Go 的 gRPC 服务
    channel = grpc.insecure_channel("localhost:50051")
    stub = mq_pb2_grpc.RocketMQGatewayStub(channel)

    # 构造请求
    request = mq_pb2.SendRequest(
        topic="asr_transfer_topic",
        body=b"Hello from Python",
        tags="tag_asr_transfer_txt"
    )

    # 调用 RPC
    response = stub.SendMessage(request)

    print("发送结果:")
    print("  success =", response.success)
    print("  msgId   =", response.msgId)
    print("  error   =", response.error if response.error else "None")

if __name__ == "__main__":
    run()
