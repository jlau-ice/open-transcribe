# service/asr_services.py
import yaml
import tempfile
import os
from whisper import load_model
import logging
from utils.minio_utils import download_file_from_minio, MinIOException

# 初始化日志
logging.basicConfig(level=logging.INFO)
logger = logging.getLogger(__name__)

# 全局Whisper模型实例
whisper_model = None

class ASRException(Exception):
    """ASR处理异常"""
    def __init__(self, message: str, error_code: str = "ASR_ERROR"):
        self.message = message
        self.error_code = error_code
        super().__init__(self.message)

def _get_whisper_model():
    """获取Whisper模型实例"""
    global whisper_model
    if whisper_model is None:
        try:
            logger.info("正在加载Whisper模型...")
            # 从配置文件读取模型参数
            config_path = "config.yaml"
            with open(config_path, 'r', encoding='utf-8') as f:
                config = yaml.safe_load(f)
                model_type = config.get('whisper', {}).get('model_type', 'base')

            whisper_model = load_model(model_type)  # 可根据需要选择不同大小的模型
            logger.info(f"Whisper模型加载完成,模型类型：{model_type}")
        except Exception as e:
            raise ASRException(f"加载Whisper模型失败: {str(e)}", "MODEL_LOAD_ERROR")
    return whisper_model

def transcribe_audio(file_path: str) -> str:
    """使用Whisper模型进行语音转写"""
    try:
        logger.info(f"开始语音转写: {file_path}")

        # 获取Whisper模型
        model = _get_whisper_model()

        # 执行转写
        result = model.transcribe(file_path,initial_prompt="以下是普通话的句子，这是一段会议记录。")
        text = result["text"]

        logger.info(f"语音转写完成，转写文本长度: {len(text)}")
        return text

    except Exception as e:
        raise ASRException(f"语音转写失败: {str(e)}", "TRANSCRIBE_ERROR")

def synthesize(audio_url: str, config_path="config.yaml") -> str:
    """
    根据传入的音频文件下载地址，下载音频文件到本地临时文件，
    然后调用Whisper模型进行语音转写，返回转写结果。
    下载到本地的临时文件在转换成功后会被删除。

    Args:
        audio_url (str): 音频文件的下载地址（MinIO存储地址）
        config_path (str): 配置文件路径

    Returns:
        str: 语音转写结果

    Raises:
        ASRException: ASR处理过程中出现的各类异常
    """
    temp_dir = None
    temp_file_path = None

    try:
        # 创建临时目录
        temp_dir = tempfile.mkdtemp(prefix="asr_")
        logger.info(f"创建临时目录: {temp_dir}")

        # 使用MinIO工具下载音频文件
        temp_file_path = download_file_from_minio(audio_url, temp_dir, config_path)

        # 执行语音转写
        transcribed_text = transcribe_audio(temp_file_path)

        return transcribed_text

    except MinIOException as e:
        # 将MinIO异常转换为ASR异常
        raise ASRException(f"MinIO文件下载失败: {e.message}", e.error_code)
    except ASRException:
        # 重新抛出自定义异常
        raise
    except Exception as e:
        # 捕获其他未预期的异常
        raise ASRException(f"ASR处理过程中发生未知错误: {str(e)}", "UNKNOWN_ERROR")
    finally:
        # 清理临时文件
        try:
            if temp_file_path and os.path.exists(temp_file_path):
                os.remove(temp_file_path)
                logger.info(f"已删除临时音频文件: {temp_file_path}")

            if temp_dir and os.path.exists(temp_dir):
                os.rmdir(temp_dir)
                logger.info(f"已删除临时目录: {temp_dir}")
        except Exception as e:
            logger.warning(f"清理临时文件时出错: {str(e)}")