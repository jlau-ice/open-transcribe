# service/rocketmq_service.py
import yaml
from rocketmq.client import PushConsumer, ConsumeStatus, Producer, Message
import json
import asyncio
from service.asr_services import synthesize
from utils.thread_pool import get_executor

class RocketMQService:
    def __init__(self, config_path="config.yaml"):
        # 加载配置文件
        with open(config_path, 'r', encoding='utf-8') as f:
            config = yaml.safe_load(f)
            self.rocketmq_config = config['rocketmq']
        
        # 获取线程池执行器
        self.executor = get_executor(config_path)
        
        # 初始化消费者
        self.consumer = PushConsumer(self.rocketmq_config['consumer_group'])
        self.consumer.set_namesrv_addr(self.rocketmq_config['nameserver'])
        
         # 初始化生产者（用于发送结果）
        self.producer = Producer(self.rocketmq_config.get('producer_group', 'asr_producer_group'))
        self.producer.set_namesrv_addr(self.rocketmq_config['nameserver'])

        # 注册消息监听器
        self.consumer.subscribe(
            self.rocketmq_config['topic'], 
            self.rocketmq_config['tag'], 
            self.message_callback
        )
    
    def message_callback(self, msg):
        """
        消息回调处理函数
        """
        try:
            # 解析消息内容
            message_body = msg.body.decode('utf-8')
            message_data = json.loads(message_body)
            
            # 提取音频URL
            audio_url = message_data.get('audio_url')
            if audio_url:
                # 异步处理音频转换
                asyncio.get_event_loop().run_in_executor(
                    self.executor, 
                    self.process_audio, 
                    audio_url, 
                    message_data
                )
                
            return ConsumeStatus.CONSUME_SUCCESS
        except Exception as e:
            print(f"处理消息时出错: {e}")
            return ConsumeStatus.RECONSUME_LATER

    def process_audio(self, audio_url, message_data):
        """
        异步处理音频转换
        """
        try:
            # 调用ASR服务处理音频
            result_text = synthesize(text=audio_url)
            
            # 处理结果
            print(f"ASR处理结果: {result_text}")

            # 处理结果并发送到结果主题
            self.send_result(result_text, message_data)
            # 这里可以将结果发送到其他主题或者存储到数据库
            # self.send_result(result_text, message_data.get('request_id'))
        except Exception as e:
            print(f"处理音频时出错: {e}")

     # 发送结果到消息队列       
    def send_result(self, result_text, original_message_data):
        """
        将处理结果发送到结果回传的消息主题
        
        Args:
            result_text (str): ASR处理结果文本
            original_message_data (dict): 原始消息数据
        """
        try:
            # 重新组装结果数据
            result_data = {
                "request_id": original_message_data.get("request_id"),
                "audio_url": original_message_data.get("audio_url"),
                "result_text": result_text,
                "status": "success",
                "timestamp": original_message_data.get("timestamp")
            }
            
            # 生成JSON格式的消息体
            result_json = json.dumps(result_data, ensure_ascii=False)
            
            # 创建消息对象
            msg = Message(self.rocketmq_config.get('send_topic', 'asr_result_topic'))
            msg.set_keys(original_message_data.get("request_id", ""))
            msg.set_tags("asr_result")
            msg.set_body(result_json)
            
            # 发送消息
            self.producer.send_sync(msg)
            print(f"结果已发送到主题 {self.rocketmq_config.get('send_topic', 'asr_result_topic')}: {result_text}")
            
        except Exception as e:
            print(f"发送结果消息时出错: {e}")
    def start(self):
        """
        启动消费者
        """
        self.consumer.start()
        print("RocketMQ消费者已启动")
    
    def shutdown(self):
        """
        关闭消费者
        """
        self.consumer.shutdown()
        print("RocketMQ消费者已关闭")
