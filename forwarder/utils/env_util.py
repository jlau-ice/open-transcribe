import os

def init_ffmpeg():
    ffmpeg_path = r"D:\environment\ffmpeg-8.0-essentials_build\bin"
    if ffmpeg_path not in os.environ["PATH"]:
        os.environ["PATH"] = ffmpeg_path + os.pathsep + os.environ["PATH"]
