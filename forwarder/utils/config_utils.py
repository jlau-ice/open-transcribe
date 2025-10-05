# utils/config_utils.py
import yaml
import os
from typing import Any, Dict, Optional

class ConfigManager:
    """配置管理器，用于读取和管理config.yaml配置文件"""

    _instance = None
    _config = None
    _config_path = "config.yml"

    def __new__(cls, config_path: str = "config.yml"):
        if cls._instance is None:
            cls._instance = super(ConfigManager, cls).__new__(cls)
            cls._config_path = config_path
        return cls._instance

    def _load_config(self) -> Dict[str, Any]:
        """加载配置文件"""
        if self._config is None:
            if not os.path.exists(self._config_path):
                raise FileNotFoundError(f"配置文件未找到: {self._config_path}")

            with open(self._config_path, 'r', encoding='utf-8') as f:
                self._config = yaml.safe_load(f)

        return self._config

    def get_config(self, key_path: str, default: Any = None) -> Any:
        """
        根据键路径获取配置值

        Args:
            key_path (str): 配置键路径，使用点号分隔，如 "rocketmq.nameserver"
            default (Any): 默认值，当配置项不存在时返回

        Returns:
            Any: 配置值或默认值
        """
        try:
            config = self._load_config()
            keys = key_path.split('.')
            value = config

            for key in keys:
                value = value[key]

            return value
        except (KeyError, TypeError):
            return default

    def get_all_config(self) -> Dict[str, Any]:
        """
        获取所有配置

        Returns:
            Dict[str, Any]: 完整配置字典
        """
        return self._load_config()

    def reload_config(self) -> None:
        """重新加载配置文件"""
        self._config = None
        self._load_config()

# 全局配置管理器实例
_config_manager = None

def get_config_manager(config_path: str = "config.yml") -> ConfigManager:
    """
    获取全局配置管理器实例

    Args:
        config_path (str): 配置文件路径

    Returns:
        ConfigManager: 配置管理器实例
    """
    global _config_manager
    if _config_manager is None:
        _config_manager = ConfigManager(config_path)
    return _config_manager

def get_config(key_path: str, default: Any = None) -> Any:
    """
    根据键路径获取配置值（便捷函数）

    Args:
        key_path (str): 配置键路径，使用点号分隔，如 "rocketmq.nameserver"
        default (Any): 默认值，当配置项不存在时返回

    Returns:
        Any: 配置值或默认值
    """
    manager = get_config_manager()
    return manager.get_config(key_path, default)

def get_all_config() -> Dict[str, Any]:
    """
    获取所有配置（便捷函数）

    Returns:
        Dict[str, Any]: 完整配置字典
    """
    manager = get_config_manager()
    return manager.get_all_config()