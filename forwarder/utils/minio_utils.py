# utils/minio_utils.py
import urllib.parse
import yaml
import os
import tempfile
from minio import Minio
from minio.error import S3Error
import logging

logger = logging.getLogger(__name__)

class MinIOException(Exception):
    """MinIO操作异常"""
    def __init__(self, message: str, error_code: str = "MINIO_ERROR"):
        self.message = message
        self.error_code = error_code
        super().__init__(self.message)

def download_file_from_minio(audio_url: str, temp_dir: str, config_path="config.yaml") -> str:
    """
    使用MinIO SDK下载音频文件到临时目录
    
    Args:
        audio_url (str): MinIO文件URL，格式如 http://minio-server:9000/bucket-name/object-key
        temp_dir (str): 临时目录路径
        config_path (str): 配置文件路径
        
    Returns:
        str: 下载的临时文件路径
        
    Raises:
        MinIOException: MinIO操作异常
    """
    try:
        # 解析MinIO URL
        parsed_url = urllib.parse.urlparse(audio_url)
        if parsed_url.path.startswith('/'):
            path_parts = parsed_url.path[1:].split('/')
        else:
            path_parts = parsed_url.path.split('/')
            
        if len(path_parts) < 2:
            raise MinIOException(f"无效的MinIO URL格式: {audio_url}", "INVALID_URL_FORMAT")
            
        bucket_name = path_parts[0]
        object_name = '/'.join(path_parts[1:])
        
        # 加载MinIO配置
        with open(config_path, 'r', encoding='utf-8') as f:
            config = yaml.safe_load(f)
            minio_config = config.get('minio', {})
        
        # 创建MinIO客户端
        client = Minio(
            minio_config['endpoint'],
            access_key=minio_config['access_key'],
            secret_key=minio_config['secret_key'],
            secure=minio_config.get('secure', False)
        )
        
        # 生成临时文件路径
        file_extension = os.path.splitext(object_name)[1] or ".wav"
        temp_file_path = os.path.join(temp_dir, f"audio_file{file_extension}")
        
        # 下载文件
        logger.info(f"开始从MinIO下载文件: bucket={bucket_name}, object={object_name}")
        client.fget_object(bucket_name, object_name, temp_file_path)
        
        file_size = os.path.getsize(temp_file_path)
        logger.info(f"MinIO文件下载完成，大小: {file_size} bytes, 保存路径: {temp_file_path}")
        
        return temp_file_path
        
    except S3Error as e:
        raise MinIOException(f"MinIO下载文件失败: {e.message}", "MINIO_DOWNLOAD_ERROR")
    except FileNotFoundError:
        raise MinIOException(f"配置文件未找到: {config_path}", "CONFIG_NOT_FOUND")
    except KeyError as e:
        raise MinIOException(f"MinIO配置缺失: {str(e)}", "MISSING_MINIO_CONFIG")
    except Exception as e:
        raise MinIOException(f"保存MinIO文件时出错: {str(e)}", "MINIO_FILE_SAVE_ERROR")