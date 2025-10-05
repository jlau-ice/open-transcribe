# RocketMQ Gateway Python客户端使用指南

## 概述
Apache RocketMQ 官方在 Windows 平台 并未提供 Python SDK，导致 Python 客户端无法直接与 RocketMQ 交互。
为解决这一限制，本项目实现了一个 gRPC 网关 (RocketMQ Gateway)：
- 网关由 Go 语言编写，负责与 RocketMQ 原生客户端交互；
- Python 客户端通过 gRPC 接口即可方便地 发送消息 与 订阅消息；
- 屏蔽了底层 RocketMQ SDK 的依赖，让 Python 应用在 Windows / Linux 等平台都能无差别使用。

## 功能特性

- ✅ **发送消息**: 同步发送消息到指定的Topic
- ✅ **订阅消息**: 实时接收指定Topic和Tag的消息
- ✅ **流式推送**: 使用gRPC流式接口实现实时消息推送
- ✅ **标签过滤**: 支持按Tag过滤消息

## 项目结构

```
rocketmq-gateway/
├── config
│   └── config.go
├── config.yaml
├── proto                # proto 定义及生成代码
│   ├── mq.proto
│   ├── mq.pb.go
│   └── mq_grpc.pb.go
├── mq                   # MQ 封装层
│   ├── consumer.go
│   └── producer.go
├── rpc              # gRPC 服务实现
│   └── service.go
├── python_cline         # Python gRPC 客户端 & demo
│   ├── README.md
│   ├── generate_python_grpc.py
│   ├── mq.proto
│   ├── mq_pb2.py
│   ├── mq_pb2_grpc.py
│   ├── test_consumer.py
│   ├── test_producer.py
│   └── test_python_client.py
├── test                  # 测试
│   ├── consumer_test.go
|   ├── producer_test.go
|   └── service_test.go
├── main.go              # 入口
├── go.mod
├── go.sum
└── README.md
```

## 快速开始

### 1. 启动Go服务

```bash
# 确保RocketMQ正在运行
# 修改config.yaml中的nameservers配置
go mod tidy
# 启动服务
go run main.go
```

### 2. 生成Python gRPC代码

```bash
# 安装protoc（如果还没有安装）
# Windows: choco install protoc
# 或下载: https://github.com/protocolbuffers/protobuf/releases

# 生成Python代码
python generate_python_grpc.py
```

### 3. 运行Python客户端测试

```bash
python test_python_client.py
```

## API使用说明

### 发送消息

```python
import grpc
import mq_pb2
import mq_pb2_grpc

# 连接到服务
with grpc.insecure_channel('localhost:50051') as channel:
    stub = mq_pb2_grpc.RocketMQGatewayStub(channel)
    
    # 发送消息
    request = mq_pb2.SendRequest(
        topic="your_topic",
        body=b"your_message_content",
        tags="your_tag"  # 可选
    )
    
    response = stub.SendMessage(request)
    if response.success:
        print(f"消息发送成功: {response.msgId}")
    else:
        print(f"发送失败: {response.error}")
```

### 订阅消息

```python
import grpc
import mq_pb2
import mq_pb2_grpc

# 连接到服务
with grpc.insecure_channel('localhost:50051') as channel:
    stub = mq_pb2_grpc.RocketMQGatewayStub(channel)
    
    # 订阅消息
    request = mq_pb2.SubscribeRequest(
        topic="your_topic",
        consumerGroup="your_consumer_group",
        tags="your_tag"  # 可选，支持标签过滤
    )
    
    # 接收消息流
    for response in stub.Subscribe(request):
        print(f"收到消息: {response.body.decode('utf-8')}")
        print(f"Topic: {response.topic}")
        print(f"Message ID: {response.msgId}")
```

## 配置说明

### config.yaml

```yaml
server:
  listen_port: 50051  # gRPC服务端口

rocketmq:
  nameservers:
    - "127.0.0.1:9876"  # RocketMQ NameServer地址
  producer:
    group_name: "GO_GATEWAY_PRODUCER_GROUP"
    send_retry_times: 3
```

## 注意事项

1. **Consumer Group**: 订阅消息时必须指定唯一的Consumer Group名称
2. **标签过滤**: 支持RocketMQ的标签表达式，如 `"tag1 || tag2"`
3. **连接管理**: 订阅连接会一直保持，直到客户端断开
4. **错误处理**: 建议在生产环境中添加适当的错误处理和重连机制

## 故障排除

### 常见问题

1. **protoc未找到**: 安装Protocol Buffers编译器
2. **连接被拒绝**: 检查Go服务是否正在运行
3. **RocketMQ连接失败**: 检查config.yaml中的nameservers配置
4. **消息未收到**: 检查Topic和Tag是否正确，Consumer Group是否唯一

### 调试建议

1. 查看Go服务日志
2. 使用RocketMQ控制台检查Topic和Consumer Group状态
3. 验证网络连接和防火墙设置

## 扩展功能

可以考虑添加的功能：
- 异步消息发送
- 消息确认机制
- 批量消息处理
- 连接池管理
- 监控和指标收集
