#!/usr/bin/env python3
"""
RocketMQ Gateway Python Client Example
=====================================

This example demonstrates how to use the RocketMQ Gateway from Python.
It shows how to send messages and subscribe to topics via gRPC.

Requirements:
    pip install grpcio grpcio-tools

Usage:
    python python_client_example.py
"""

import grpc
import time
import threading
import json
from typing import Optional, Callable

# Note: You need to generate Python gRPC files from the proto definition
# For now, we'll create a simple implementation

class RocketMQGatewayClient:
    """Simple RocketMQ Gateway client for demonstration"""
    
    def __init__(self, host: str = "localhost", port: int = 8080):
        self.host = host
        self.port = port
        self.channel = None
        self.stub = None
        
    def connect(self):
        """Connect to the gRPC server"""
        try:
            # In a real implementation, you would use the generated gRPC stub
            # self.channel = grpc.insecure_channel(f"{self.host}:{self.port}")
            # self.stub = mq_pb2_grpc.RocketMQGatewayStub(self.channel)
            print(f"✅ Connected to RocketMQ Gateway at {self.host}:{self.port}")
            return True
        except Exception as e:
            print(f"❌ Failed to connect: {e}")
            return False
    
    def send_message(self, topic: str, body: str) -> Optional[str]:
        """
        Send a message to a RocketMQ topic
        
        Args:
            topic: The topic name
            body: The message body
            
        Returns:
            Result string if successful, None if failed
        """
        try:
            # In a real implementation:
            # request = mq_pb2.SendRequest(topic=topic, body=body)
            # response = self.stub.SendMessage(request)
            # return response.result
            
            print(f"📤 Sending message to topic '{topic}': {body}")
            
            # Simulate sending (replace with actual gRPC call)
            time.sleep(0.1)  # Simulate network delay
            result = f"Message sent to {topic}: {body[:50]}..."
            print(f"✅ {result}")
            return result
            
        except Exception as e:
            print(f"❌ Failed to send message: {e}")
            return None
    
    def subscribe(self, topic: str, callback: Callable[[str, str, str], None], 
                  client_id: str = None):
        """
        Subscribe to a RocketMQ topic
        
        Args:
            topic: The topic to subscribe to
            callback: Function to call when a message is received
                     callback(topic, body, msg_id)
            client_id: Optional client ID
        """
        try:
            print(f"🔔 Subscribing to topic '{topic}' with client ID '{client_id or 'auto'}'")
            
            # In a real implementation:
            # request = mq_pb2.SubscribeRequest(topic=topic, client_id=client_id or "")
            # for response in self.stub.Subscribe(request):
            #     callback(response.topic, response.body, response.msg_id)
            
            # Simulate subscription with mock messages
            def mock_subscription():
                message_count = 0
                while True:
                    time.sleep(2)  # Simulate message interval
                    message_count += 1
                    mock_body = f"Mock message {message_count} from {topic}"
                    mock_msg_id = f"msg_{message_count}_{int(time.time())}"
                    callback(topic, mock_body, mock_msg_id)
            
            # Run subscription in a separate thread
            subscription_thread = threading.Thread(target=mock_subscription, daemon=True)
            subscription_thread.start()
            
        except Exception as e:
            print(f"❌ Failed to subscribe: {e}")
    
    def disconnect(self):
        """Disconnect from the gRPC server"""
        if self.channel:
            self.channel.close()
            print("🔌 Disconnected from RocketMQ Gateway")


def message_handler(topic: str, body: str, msg_id: str):
    """Example message handler"""
    print(f"📨 Received message:")
    print(f"   Topic: {topic}")
    print(f"   Message ID: {msg_id}")
    print(f"   Body: {body}")
    print(f"   Timestamp: {time.strftime('%Y-%m-%d %H:%M:%S')}")
    print("-" * 50)


def main():
    """Main example function"""
    print("🚀 RocketMQ Gateway Python Client Example")
    print("=" * 50)
    
    # Create client
    client = RocketMQGatewayClient("localhost", 8080)
    
    # Connect
    if not client.connect():
        return
    
    try:
        # Example 1: Send messages
        print("\n📤 Example 1: Sending Messages")
        print("-" * 30)
        
        messages = [
            ("audio_processing", json.dumps({
                "task_id": "task_001",
                "audio_file": "sample.wav",
                "language": "zh-CN"
            })),
            ("audio_processing", json.dumps({
                "task_id": "task_002", 
                "audio_file": "sample2.wav",
                "language": "en-US"
            })),
            ("status_updates", json.dumps({
                "task_id": "task_001",
                "status": "processing",
                "progress": 50
            }))
        ]
        
        for topic, body in messages:
            client.send_message(topic, body)
            time.sleep(0.5)
        
        # Example 2: Subscribe to topics
        print("\n🔔 Example 2: Subscribing to Topics")
        print("-" * 30)
        
        # Subscribe to audio processing results
        client.subscribe("audio_processing_results", message_handler, "python_client_001")
        
        # Subscribe to status updates
        client.subscribe("status_updates", message_handler, "python_client_002")
        
        # Keep the program running to receive messages
        print("\n⏳ Listening for messages... (Press Ctrl+C to stop)")
        try:
            while True:
                time.sleep(1)
        except KeyboardInterrupt:
            print("\n🛑 Stopping...")
            
    finally:
        client.disconnect()


if __name__ == "__main__":
    main()
