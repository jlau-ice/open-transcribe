FROM python:3.12-slim
WORKDIR /app
COPY . .
RUN pip install -r requirements.txt
CMD ["python", "forwarder.py"]
EXPOSE 8080
ENTRYPOINT ["/bin/bash", "-c"]
