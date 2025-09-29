# service/whisperx_services.py
# 使用whisperx进行语音转写，并支持说话人分离
from opencc import OpenCC
import torch
import whisperx
import gc
import yaml
import tempfile
import os
import logging
from utils.minio_utils import download_file_from_minio, MinIOException
from utils.config_util import get_config

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
            model_type = get_config("whisper.model_type","large-v2")
            device = get_config("whisper.device","cpu")
            compute_type = get_config("whisper.compute_type","float16")
            download_root =  get_config("whisper.download_root",None)
            whisper_model = whisperx.load_model(model_type,device=device,compute_type=compute_type,download_root=None,
                                                local_files_only=False,language="zh")  # 可根据需要选择不同大小的模型
            logger.info(f"Whisper模型加载完成,模型类型：{model_type}")
        except Exception as e:
            raise ASRException(f"加载Whisper模型失败: {str(e)}", "MODEL_LOAD_ERROR")
    return whisper_model

def transcribe_audio(file_path: str,min_speakers:int,max_speakers:int) -> str:
    """使用Whisper模型进行语音转写
    Args:
        file_path (str): 音频文件路径
        min_speakers (int): 说话人最少数量（如果已知）
        max_speakers (int): 说话人最多数量（如果已知）
    Returns:
        str: 转写结果文本
    Raises:
        ASRException: 语音转写过程中出现的各类异常
    """
    try:
        logger.info(f"开始语音转写: {file_path}")

        # 获取Whisper模型
        model = _get_whisper_model()
        audio = whisperx.load_audio(file_path)
        logger.info(f"音频文件加载完成，音频长度: {len(audio)}")
        # 1.执行转写
        batch_size = get_config("whisper.batch_size",16)
        #initial_prompt="以下是普通话的句子，这是一段会议记录。请将其转写成书面语。"
        result = model.transcribe(audio,batch_size=batch_size,language="zh",task="transcribe",print_progress=True)
        logger.info(f"语音转写完成,语言：{result['language']}，文本分段内容：{result['segments']}")

        #2.转录文本时间戳对齐
        # logger.info("开始精准对齐文本时间戳...")
        # device = get_config("whisper.device","cpu")
        # model_a, metadata = whisperx.load_align_model(language_code=result["language"], device=device)
        # result = whisperx.align(result["segments"], model_a, metadata, audio, device, return_char_alignments=False)
        # logger.info(f"精准对齐后的文本分段内容：{result['segments']}")
        # # 释放对齐模型占用的显存
        # del model_a
        # del metadata
        # gc.collect()
        # if device == "cuda":
        #     torch.cuda.empty_cache()


        #3.区分说话人并分配说话人标签
        logger.info("开始说话人分离...")
        hf_token = get_config("diarization.hf_token",None)
        device = get_config("whisper.device","cpu")
        diarize_model = whisperx.diarize.DiarizationPipeline(use_auth_token=hf_token, device=device)
        # add min/max number of speakers if known
        if min_speakers>0 and max_speakers>0 and min_speakers<=max_speakers:
            logger.info(f"使用已知的说话人数量范围进行说话人分离，最少说话人: {min_speakers}, 最多说话人: {max_speakers}")
            diarize_segments = diarize_model(audio, min_speakers=min_speakers, max_speakers=max_speakers)
        else:
            logger.info("使用未知的说话人数量进行说话人分离")
            diarize_segments = diarize_model(audio)
        logger.info(f"说话人分离完成，分离结果: {diarize_segments}")
        # 将说话人标签分配给转录文本
        result = whisperx.assign_word_speakers(diarize_segments, result)
        # 释放说话人分离模型占用的显存
        del diarize_model
        del diarize_segments
        gc.collect()
        if device == "cuda":
            torch.cuda.empty_cache()
        #5.中文繁体转换为中文简体
        logger.info("开始中文繁体转换为中文简体...")
        result = convert_segments_to_simplified(result)

        logger.info(f"最终转写结果: {result['segments']}")
        # 4.提取最终转写文本
        text = result["segments"]
        return text

    except Exception as e:
        logger.exception(f"语音转写发生错误，具体原因: {str(e)}")
        raise ASRException(f"语音转写失败: {str(e)}", "TRANSCRIBE_ERROR")


def synthesize(audio_url: str, message_data: dict, config_path="config.yaml") -> str:
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
        min_speakers = message_data.get("minSpeakers",0)
        max_speakers = message_data.get("maxSpeakers",0)
        transcribed_text = transcribe_audio(temp_file_path,min_speakers=min_speakers,max_speakers=max_speakers)

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

def convert_segments_to_simplified(result: dict) -> dict:
    """
    对result['segments']中的每个分段的text进行繁体转简体，返回转换后的result
    Args:
        result (dict): WhisperX转写结果，包含'segments'数组
    Returns:
        dict: 转换后的result
    """
    cc = OpenCC('t2s')
    segments = result.get('segments', [])
    for seg in segments:
        if 'text' in seg:
            seg['text'] = cc.convert(seg['text'])
    result['segments'] = segments
    return result