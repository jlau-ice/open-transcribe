# service/rocketmq_gateway.py
import yaml
import json
import logging
import grpc
import threading
import time
from datetime import datetime

from proto import mq_pb2, mq_pb2_grpc
# 使用你实际的 ASR 服务
from service.whisperx_services import synthesize

logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)


class RocketMQService:
    _instance = None  # 单例实例

    def __init__(self, config_path="config.yml"):
        # 加载配置
        with open(config_path, 'r', encoding='utf-8') as f:
            config = yaml.safe_load(f)
            self.rocketmq_config = config['rocketmq']

        # 建立 gRPC 连接
        self.channel = grpc.insecure_channel(self.rocketmq_config['grpc_server'])
        self.stub = mq_pb2_grpc.RocketMQGatewayStub(self.channel)

        # 启动订阅线程
        self._subscribe_thread = threading.Thread(target=self._subscribe_loop, daemon=True)
        self._subscribe_thread.start()
        logger.info("✅ gRPC RocketMQ 客户端已启动 (消费者+生产者)")

    # ---------------- 单例方法 ----------------
    @classmethod
    def get_instance(cls, config_path="config.yml"):
        if cls._instance is None:
            cls._instance = RocketMQService(config_path)
        return cls._instance

    # ---------------- 消费逻辑 ----------------
    def _subscribe_loop(self):
        """循环订阅消息"""
        request = mq_pb2.SubscribeRequest(
            topic=self.rocketmq_config['topic'],
            consumerGroup=self.rocketmq_config['consumer_group'],
            tags=self.rocketmq_config.get('tag', "")
        )
        while True:
            try:
                logger.info(f"📥 开始订阅: topic={request.topic}, group={request.consumerGroup}, tags={request.tags}")
                for response in self.stub.Subscribe(request):
                    try:
                        message_data = json.loads(response.body.decode("utf-8"))
                        self.message_callback(message_data)
                    except Exception as e:
                        logger.error(f"❌ 消息处理失败: {e}")
            except grpc.RpcError as e:
                logger.error(f"❌ gRPC 错误: {e}, 5秒后重试...")
                time.sleep(5)

    def message_callback(self, message_data):
        """消费到消息后的处理逻辑"""
        audio_url = message_data.get("filePath")
        logger.info(f"📨 收到新消息: {message_data}")
        if audio_url:
            self.process_audio(audio_url, message_data)

    # ---------------- 业务处理 ----------------
    def process_audio(self, audio_url, message_data):
        """处理音频 -> ASR 转写 -> 结果回传"""
        try:
            start_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")
            logger.info(f"🎙️ 开始处理语音转写, 时间: {start_time}")
            result_text = synthesize(audio_url=audio_url, message_data=message_data)

            logger.info(f"ASR 处理结果: {result_text}")
            self.send_result(result_text, message_data, start_time)
        except Exception as e:
            logger.error(f"❌ 处理音频时出错: {e}")

    # ---------------- 生产逻辑 ----------------
    def send_result(self, result_text, original_message_data, start_time):
        """把结果发回 MQ"""
        try:
            end_time = datetime.now().strftime("%Y-%m-%d %H:%M:%S")

            result_data = {
                "audioId": original_message_data.get("id"),
                "result_text": result_text,
                "status": "success",
                "startTime": start_time,
                "endTime": end_time
            }

            result_json = json.dumps(result_data, ensure_ascii=False)

            request = mq_pb2.SendRequest(
                topic=self.rocketmq_config.get('send_topic', 'asr_result_topic'),
                body=result_json.encode("utf-8"),
                tags="tag_asr_transfer_result"
            )

            response = self.stub.SendMessage(request)
            if response.success:
                logger.info(f"✅ 结果已发送到 {request.topic}, msgId={response.msgId}")
            else:
                logger.error(f"❌ 发送失败: {response.error}")
        except Exception as e:
            logger.error(f"❌ 发送结果消息时出错: {e}")
