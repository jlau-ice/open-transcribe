
# 🎙️ 免费音频转文本工具

一个基于 **Vue3 + Arco Design + TailwindCSS** 的音频转文本 Web 应用雏形。  
支持用户上传音频或直接录音，借助 AI 实现语音转文本。

本项目是一个基于[Monica AI Audio to Text](https://monica.im)
 页面设计理念开发的示例页面，旨在实现音频转文字的前端交互展示效果。

⚠️ 声明：本项目仅参考了 Monica 网站的页面布局和交互设计，未直接使用其源代码或音频处理功能。


---

## ✨ 功能特性
- 🎤 **实时录音**：支持网页端直接录音并上传。
- 📂 **本地文件上传**：支持选择本地音频文件，最大 50MB。
- 🔄 **多格式支持**：支持 MP3、WAV、M4A、AAC、OGG、FLAC、WEBM、OPUS 等常见音频格式。
- ⚡ **AI 转录**：调用语音识别模型，将音频内容快速转换为文本。
- 🕒 **长时音频**：单次录音最长支持 6 小时。
- 📝 **历史记录**：展示最近使用的文件，方便快速查看。

---

## 🖼️ 页面雏形
当前已完成的雏形功能：
- 左侧导航栏：主页、任务管理、模型配置。
- 最近使用：展示最近上传/转换过的音频任务。
- 主界面：上传文件/开始录音入口，文件限制提示，支持文件类型说明。
![alt text](/readme/image.png)

---

## 🚀 技术栈
- **前端框架**：Vue 3
- **UI 组件库**：Arco Design Vue
- **样式**：TailwindCSS + SCSS

---

## 📦 本地运行
```bash
# 克隆项目
git clone -b main https://github.com/jlau-ice/open-transcribe.git

# 进入目录
cd web-ui

# 安装依赖
npm install 

# 启动开发环境
npm run dev

