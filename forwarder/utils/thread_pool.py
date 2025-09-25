# utils/thread_pool.py
import yaml
from concurrent.futures import ThreadPoolExecutor
import os

class ThreadPoolManager:
    def __init__(self, config_path="config.yaml"):
        self.executor = None
        self._load_config(config_path)
        self._create_thread_pool()
    
    def _load_config(self, config_path):
        """加载线程池配置"""
        if os.path.exists(config_path):
            with open(config_path, 'r', encoding='utf-8') as f:
                config = yaml.safe_load(f)
                self.thread_pool_config = config.get('thread_pool', {})
        else:
            self.thread_pool_config = {}
    
    def _create_thread_pool(self):
        """创建线程池"""
        max_workers = self.thread_pool_config.get('max_workers', 10)
        self.executor = ThreadPoolExecutor(max_workers=max_workers)
        print(f"线程池已创建，最大工作线程数: {max_workers}")
    
    def get_executor(self):
        """获取线程池执行器"""
        return self.executor
    
    def shutdown(self):
        """关闭线程池"""
        if self.executor:
            self.executor.shutdown()
            print("线程池已关闭")

# 全局线程池管理器实例
thread_pool_manager = None

def get_thread_pool_manager(config_path="config.yaml"):
    """获取全局线程池管理器实例"""
    global thread_pool_manager
    if thread_pool_manager is None:
        thread_pool_manager = ThreadPoolManager(config_path)
    return thread_pool_manager

def get_executor(config_path="config.yaml"):
    """获取线程池执行器"""
    manager = get_thread_pool_manager(config_path)
    return manager.get_executor()