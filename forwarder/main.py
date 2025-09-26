# main.py
from fastapi import FastAPI
#from routers import asr
#from routers import asr
from service.rocketmq_service import RocketMQService
from utils.thread_pool import get_thread_pool_manager

app = FastAPI(title="ASR Service")

# 注册路由
app.include_router(asr.router)

# 全局变量
rocketmq_service = None
thread_pool_manager = None

@app.on_event("startup")
async def startup_event():
    """
    应用启动时启动RocketMQ消费者
    """
    global rocketmq_service, thread_pool_manager
    # 初始化线程池管理器
    thread_pool_manager = get_thread_pool_manager()
    # 初始化RocketMQ服务
    rocketmq_service = RocketMQService.get_rocketmq_service()
    rocketmq_service.start()

@app.on_event("shutdown")
async def shutdown_event():
    """
    应用关闭时停止RocketMQ消费者和线程池
    """
    global rocketmq_service, thread_pool_manager
    if rocketmq_service:
        rocketmq_service.shutdown()
    if thread_pool_manager:
        thread_pool_manager.shutdown()

@app.get("/")
def root():
    return {"message": "Hello FastAPI!"}