from fastapi import FastAPI
from routers import asr

app = FastAPI(title="ASR Service")

# 注册路由
app.include_router(asr.router)

@app.get("/")
def root():
    return {"message": "Hello FastAPI!"}
