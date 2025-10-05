# main.py
from contextlib import asynccontextmanager
from fastapi import FastAPI
from routers import asr
# linux MacOS 上可以正常使用rocketmq sdk  用 rocketmq_service.py
from service.rocketmq_service import RocketMQService
# Windows 上 使用
# from service.rocketmq_gateway import RocketMQService
from utils.env_util import init_ffmpeg
from utils.thread_pool import get_thread_pool_manager
import os

@asynccontextmanager
async def lifespan(app: FastAPI):
    """
    应用生命周期管理器
    """
    # 启动时执行的代码 (替代 @app.on_event("startup"))
    print("应用启动中...")
    
    # 初始化全局变量
    app.state.rocketmq_service = None
    app.state.thread_pool_manager = None
    
    try:
        # 初始化线程池管理器
        app.state.thread_pool_manager = get_thread_pool_manager()
        # 以下二选一
        # linux MacOS 上可以正常使用rocketmq sdk
        # 初始化RocketMQ服务
        app.state.rocketmq_service = RocketMQService.get_rocketmq_service()
        app.state.rocketmq_service.start()
        # windows 上使用 grpc调用中间服务
        # config_path = os.path.join(os.path.dirname(__file__), "config.yml")
        # app.state.rocketmq_service = RocketMQService.get_instance(config_path)
        
        print("应用启动完成")
        yield
        
    except Exception as e:
        print(f"应用启动失败: {e}")
        raise
    
    # 关闭时执行的代码 (替代 @app.on_event("shutdown"))
    print("应用关闭中...")
    
    # 关闭RocketMQ服务
    if hasattr(app.state, 'rocketmq_service') and app.state.rocketmq_service:
        try:
            app.state.rocketmq_service.shutdown()
            print("RocketMQ服务已关闭")
        except Exception as e:
            print(f"关闭RocketMQ服务时出错: {e}")
    
    # 关闭线程池
    if hasattr(app.state, 'thread_pool_manager') and app.state.thread_pool_manager:
        try:
            app.state.thread_pool_manager.shutdown()
            print("线程池已关闭")
        except Exception as e:
            print(f"关闭线程池时出错: {e}")
    
    print("应用已关闭")

# 使用 lifespan 替代废弃的 startup/shutdown 事件
app = FastAPI(
    title="ASR Service",
    lifespan=lifespan
)

# 注册路由
app.include_router(asr.router)

@app.get("/")
def root():
    return {"message": "Hello FastAPI!"}


import uvicorn

if __name__ == "__main__":
    # init_ffmpeg()
    uvicorn.run("main:app", host="0.0.0.0", port=8500, reload=True)