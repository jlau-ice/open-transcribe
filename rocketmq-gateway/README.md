# RocketMQ Gateway

[![Go Version](https://img.shields.io/badge/Go-1.23.2-blue.svg)](https://golang.org/)
[![License](https://img.shields.io/badge/License-MIT-green.svg)](LICENSE)
[![RocketMQ](https://img.shields.io/badge/RocketMQ-Client-blue.svg)](https://rocketmq.apache.org/)

一个通用的RocketMQ Gateway工具，通过gRPC解决Python在Windows上使用RocketMQ SDK不兼容的问题。

## 🚀 特性

- **gRPC接口**: 提供标准的gRPC服务，支持跨语言调用
- **消息发送**: 支持向RocketMQ主题发送消息
- **流式订阅**: 支持通过gRPC流订阅RocketMQ消息
- **健康检查**: 内置健康检查接口，支持服务监控
- **配置灵活**: 支持配置文件和环境变量
- **错误处理**: 完善的错误处理和重试机制
- **日志记录**: 详细的日志记录和监控

## 🎯 解决的问题

- **Python兼容性**: 解决Python RocketMQ SDK在Windows上的兼容性问题
- **跨语言通信**: 通过gRPC实现跨语言的消息队列访问
- **统一接口**: 提供统一的消息队列访问接口
- **部署简化**: 简化消息队列客户端的部署和配置

## 📋 系统要求

- Go 1.23.2+
- RocketMQ 4.0+
- 网络访问RocketMQ NameServer

## 🛠️ 安装和运行

### 1. 克隆项目

```bash
git clone <repository-url>
cd rocketmq-gateway
```

### 2. 安装依赖

```bash
go mod tidy
```

### 3. 配置

复制并修改配置文件：

```bash
cp application.yml.example application.yml
```

编辑 `application.yml`:

```yaml
rocketmq:
  nameserver: "127.0.0.1:9876"  # RocketMQ NameServer地址
  topic: "TestTopic"            # 默认主题
  group: "TestGroup"            # 默认消费者组
  retry: 2                      # 重试次数
server:
  host: "0.0.0.0"               # 服务监听地址
  port: 8080                    # 服务端口
python:
  callback: "http://127.0.0.1:5000/mq_callback"  # Python回调地址
log:
  level: "info"                 # 日志级别
```

### 4. 环境变量配置

支持通过环境变量覆盖配置：

```bash
export RMC_SERVER_PORT=8080
export RMC_ROCKETMQ_NAMESERVER=127.0.0.1:9876
export RMC_PYTHON_CALLBACK=http://127.0.0.1:5000/mq_callback
```

### 5. 运行服务

```bash
go run .
```

服务启动后会显示：

```
🚀 Starting RocketMQ Gateway...
✅ Config loaded successfully
📋 Configuration summary:
   - Server: 0.0.0.0:8080
   - RocketMQ: 127.0.0.1:9876
   - Python Callback: http://127.0.0.1:5000/mq_callback
🔧 Initializing producer with name server: 127.0.0.1:9876
✅ Producer initialized successfully
🌐 gRPC gateway listening on :8080
🎯 Available services:
   - SendMessage: Send messages to RocketMQ topics
   - Subscribe: Subscribe to RocketMQ topics via streaming
```

## 📖 API文档

### gRPC服务定义

```protobuf
service RocketMQGateway {
  rpc SendMessage (SendRequest) returns (SendResponse);
  rpc Subscribe(SubscribeRequest) returns (stream MessageResponse);
  rpc HealthCheck (HealthCheckRequest) returns (HealthCheckResponse);
}
```

### 消息类型

#### SendRequest
```protobuf
message SendRequest {
  string topic = 1;
  string body = 2;
}
```

#### SendResponse
```protobuf
message SendResponse {
  string result = 1;
}
```

#### SubscribeRequest
```protobuf
message SubscribeRequest {
  string topic = 1;
  string client_id = 2;
}
```

#### MessageResponse
```protobuf
message MessageResponse {
  string topic = 1;
  string body = 2;
  string msg_id = 3;
}
```

#### HealthCheckRequest/Response
```protobuf
message HealthCheckRequest {
  string service = 1;
}

message HealthCheckResponse {
  enum ServingStatus {
    UNKNOWN = 0;
    SERVING = 1;
    NOT_SERVING = 2;
    SERVICE_UNKNOWN = 3;
  }
  ServingStatus status = 1;
  string message = 2;
}
```

## 🐍 Python客户端使用

### 1. 安装Python客户端

```bash
cd python_client
pip install -r requirements.txt
python generate_proto.py
```

### 2. 基本使用

```python
from client import RocketMQGatewayClient
import json

# 创建客户端
client = RocketMQGatewayClient("localhost", 8080)

# 连接
if client.connect():
    # 发送消息
    message = {
        "task_id": "task_001",
        "audio_file": "sample.wav",
        "language": "zh-CN"
    }
    result = client.send_message("audio_processing", message)
    print(f"消息发送结果: {result}")
    
    # 订阅消息
    def on_message(topic, body, msg_id):
        print(f"收到消息: {body} from {topic}")
    
    client.subscribe("audio_processing_results", on_message)
    
    # 保持运行
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        client.disconnect()
```

详细使用说明请参考 [Python客户端文档](python_client/README.md)。

## 🔧 使用场景

### 1. 语音转写服务

```python
# Python端发送转写任务
audio_task = {
    "task_id": "audio_001",
    "audio_file": "meeting.wav",
    "language": "zh-CN"
}
client.send_message("audio_processing", audio_task)

# Python端接收转写结果
def handle_transcription(topic, body, msg_id):
    result = json.loads(body)
    print(f"转写完成: {result['transcription']}")

client.subscribe("transcription_results", handle_transcription)
```

### 2. 微服务通信

```python
# 服务A发送事件
event = {
    "event_type": "user_created",
    "user_id": "12345",
    "timestamp": "2024-01-01T00:00:00Z"
}
client.send_message("user_events", event)

# 服务B订阅事件
def handle_user_event(topic, body, msg_id):
    event = json.loads(body)
    if event["event_type"] == "user_created":
        # 处理用户创建事件
        pass

client.subscribe("user_events", handle_user_event)
```

### 3. 批量数据处理

```python
# 发送批量任务
for i in range(100):
    task = {
        "batch_id": f"batch_{i}",
        "data": f"processing item {i}"
    }
    client.send_message("batch_processing", task)

# 接收处理结果
def handle_batch_result(topic, body, msg_id):
    result = json.loads(body)
    print(f"批次 {result['batch_id']} 处理完成")

client.subscribe("batch_results", handle_batch_result)
```

## 📊 监控和日志

### 健康检查

```bash
# 使用grpcurl检查健康状态
grpcurl -plaintext localhost:8080 mq.RocketMQGateway/HealthCheck
```

### 日志级别

支持以下日志级别：
- `debug`: 详细调试信息
- `info`: 一般信息（默认）
- `warn`: 警告信息
- `error`: 错误信息

### 监控指标

服务提供以下监控信息：
- 消息发送成功率
- 订阅连接数
- 错误统计
- 性能指标

## 🚀 部署

### Docker部署

```dockerfile
FROM golang:1.23.2-alpine AS builder
WORKDIR /app
COPY . .
RUN go mod tidy && go build -o rocketmq-gateway .

FROM alpine:latest
RUN apk --no-cache add ca-certificates
WORKDIR /root/
COPY --from=builder /app/rocketmq-gateway .
COPY --from=builder /app/application.yml .
CMD ["./rocketmq-gateway"]
```

### Docker Compose

```yaml
version: '3.8'
services:
  rocketmq-gateway:
    build: .
    ports:
      - "8080:8080"
    environment:
      - RMC_ROCKETMQ_NAMESERVER=rocketmq:9876
      - RMC_PYTHON_CALLBACK=http://python-service:5000/mq_callback
    depends_on:
      - rocketmq
```

## 🔒 安全考虑

- 使用TLS加密gRPC通信（生产环境推荐）
- 配置防火墙规则限制访问
- 使用认证和授权机制
- 定期更新依赖包

## 🤝 贡献

欢迎贡献代码！请遵循以下步骤：

1. Fork 项目
2. 创建功能分支 (`git checkout -b feature/AmazingFeature`)
3. 提交更改 (`git commit -m 'Add some AmazingFeature'`)
4. 推送到分支 (`git push origin feature/AmazingFeature`)
5. 创建 Pull Request

## 📝 许可证

本项目采用 MIT 许可证 - 查看 [LICENSE](LICENSE) 文件了解详情。

## 🆘 支持

如果您遇到问题或有建议，请：

1. 查看 [Issues](../../issues) 页面
2. 创建新的 Issue
3. 联系维护者

## 🔄 更新日志

### v1.0.0
- 初始版本发布
- 支持基本的消息发送和订阅功能
- 提供Python客户端
- 支持健康检查
- 完善的错误处理和日志记录

---

**注意**: 这是一个通用工具，适用于解决Python在Windows上使用RocketMQ SDK的兼容性问题。如果您有特定的使用场景或需求，欢迎提出Issue或贡献代码。
