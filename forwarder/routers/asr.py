from fastapi import APIRouter
from service.whisperx_services import synthesize

router = APIRouter(prefix="/asr", tags=["ASR"])

@router.post("/")
def tts_endpoint(audio_url: str):
    text = synthesize(audio_url)
    return {"text": text}
