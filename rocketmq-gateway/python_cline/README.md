RocketMQ Gateway Python 客户端示例
=============================================

这是一个用于测试 RocketMQ Gateway gRPC 服务的 Python 客户端示例。它展示了如何使用 gRPC 协议进行消息的生产（发送）和消费（订阅）。

🚀 结构概览
---------------------

| 文件名 | 描述 |
| :--- | :--- |
| generate_python_grpc.py | 用于生成 Python gRPC 代码的脚本。 |
| proto/mq.proto | Protocol Buffers 定义文件，描述了 RocketMQ Gateway 的服务和消息结构。 |
| mq_pb2.py | （生成文件）Proto 消息定义的 Python 实现。 |
| mq_pb2_grpc.py | （生成文件）gRPC 客户端和服务端的 Python 接口实现。 |
| test_python_client.py | 综合测试脚本，包含发送和订阅功能的完整示例（多线程）。 |
| test_producer.py | 独立生产者示例，仅用于测试消息发送功能。 |
| test_consumer.py | 独立消费者示例，仅用于测试消息持续订阅功能。 |

🛠️ 环境准备
---------------------

在运行示例之前，您需要准备以下环境：

1.  Python 3.x
2.  Protocol Buffers 编译器 (protoc)：用于将 .proto 文件编译成 Python 代码。
3.  Python gRPC 依赖：

    pip install grpcio grpcio-tools

4.  RocketMQ Gateway 服务：确保您的 RocketMQ Gateway 服务已经在 localhost:50051 端口运行。

⚙️ 第一步：生成 Python gRPC 代码
-------------------------------------

您需要使用 generate_python_grpc.py 脚本，将 proto/mq.proto 文件转换为 Python 可以直接调用的 gRPC 代码。

运行以下命令：

python generate_python_grpc.py

执行成功后，将会在当前目录下生成两个核心文件：

-   mq_pb2.py
-   mq_pb2_grpc.py

❗ 注意：如果您遇到 protoc 找不到或权限问题，请确保它已正确安装并添加到系统环境变量中。

🧪 第二步：运行客户端示例
-------------------------------------

所有示例脚本都默认连接到 localhost:50051 端口，并使用 test_topic 和 python_test_group 进行测试。

### 1. 综合测试（生产 + 消费）

test_python_client.py 在一个脚本中同时启动一个消费者线程和发送多个消息的主线程。

python test_python_client.py

预期效果：脚本启动后会先订阅，然后发送 5 条消息。您应该能立即看到消费者线程接收并打印出这些消息。

### 2. 独立生产者（发送消息）

test_producer.py 脚本专注于发送消息。

python test_producer.py

预期效果：脚本将发送 5 条消息到 test_topic，并打印出每条消息的发送结果（成功或失败）。

### 3. 独立消费者（持续订阅）

test_consumer.py 脚本会启动一个线程，持续监听来自 RocketMQ 的消息。

python test_consumer.py

预期效果：脚本会一直运行并等待消息。您需要通过运行 test_producer.py 或其他生产者向 test_topic 发送消息，才能看到此脚本输出收到的消息内容。按 Ctrl+C 退出程序。

💡 故障排除
---------------------

| 问题描述 | 解决方法 |
| :--- | :--- |
| ImportError: No module named mq_pb2 | 确保您已运行 python generate_python_grpc.py 成功生成 mq_pb2.py 和 mq_pb2_grpc.py 文件。 |
| gRPC错误: <_InactiveRpcError of RPC that terminated with status code 14 ...> | 这通常表示连接失败。请检查 RocketMQ Gateway 服务是否在 localhost:50051 端口上运行，并且网络连接正常。 |
| protoc: not found | 请安装 Protocol Buffers 编译器 (protoc) 并确保其路径已添加到您的系统环境变量中。 |