#!/usr/bin/env python3
"""
RocketMQ Gateway Python Client
==============================

A complete Python client for the RocketMQ Gateway service.
"""

import grpc
import threading
import time
import json
import logging
from typing import Optional, Callable, Dict, Any
from concurrent import futures

# Import generated gRPC files
try:
    import proto.mq_pb2 as mq_pb2
    import proto.mq_pb2_grpc as mq_pb2_grpc
except ImportError:
    print("❌ gRPC files not found. Please run 'python generate_proto.py' first.")
    sys.exit(1)

# Configure logging
logging.basicConfig(
    level=logging.INFO,
    format='%(asctime)s - %(name)s - %(levelname)s - %(message)s'
)
logger = logging.getLogger(__name__)


class RocketMQGatewayClient:
    """Complete RocketMQ Gateway client with gRPC support"""
    
    def __init__(self, host: str = "localhost", port: int = 8080, 
                 max_retries: int = 3, timeout: int = 30):
        """
        Initialize the client
        
        Args:
            host: Gateway server host
            port: Gateway server port
            max_retries: Maximum number of retries for failed operations
            timeout: Connection timeout in seconds
        """
        self.host = host
        self.port = port
        self.max_retries = max_retries
        self.timeout = timeout
        self.channel = None
        self.stub = None
        self._subscriptions = {}  # Track active subscriptions
        self._running = False
        
    def connect(self) -> bool:
        """
        Connect to the gRPC server
        
        Returns:
            True if connection successful, False otherwise
        """
        try:
            server_address = f"{self.host}:{self.port}"
            logger.info(f"🔌 Connecting to RocketMQ Gateway at {server_address}")
            
            # Create gRPC channel with options
            options = [
                ('grpc.keepalive_time_ms', 30000),
                ('grpc.keepalive_timeout_ms', 5000),
                ('grpc.keepalive_permit_without_calls', True),
                ('grpc.http2.max_pings_without_data', 0),
                ('grpc.http2.min_time_between_pings_ms', 10000),
                ('grpc.http2.min_ping_interval_without_data_ms', 300000)
            ]
            
            self.channel = grpc.insecure_channel(server_address, options=options)
            self.stub = mq_pb2_grpc.RocketMQGatewayStub(self.channel)
            
            # Test connection with health check
            try:
                health_request = mq_pb2.HealthCheckRequest(service="gateway")
                response = self.stub.HealthCheck(health_request, timeout=self.timeout)
                logger.info(f"🏥 Health check response: {response.status}")
            except grpc.RpcError as e:
                logger.warning(f"⚠️  Health check failed: {e}")
            
            logger.info("✅ Connected to RocketMQ Gateway successfully")
            return True
            
        except Exception as e:
            logger.error(f"❌ Failed to connect: {e}")
            return False
    
    def send_message(self, topic: str, body: str, retries: Optional[int] = None) -> Optional[str]:
        """
        Send a message to a RocketMQ topic
        
        Args:
            topic: The topic name
            body: The message body (string or dict)
            retries: Number of retries (overrides default)
            
        Returns:
            Result string if successful, None if failed
        """
        if not self.stub:
            logger.error("❌ Not connected to server")
            return None
        
        # Convert body to string if it's a dict
        if isinstance(body, dict):
            body = json.dumps(body)
        
        retries = retries or self.max_retries
        
        for attempt in range(retries + 1):
            try:
                logger.info(f"📤 Sending message to topic '{topic}' (attempt {attempt + 1})")
                
                request = mq_pb2.SendRequest(topic=topic, body=body)
                response = self.stub.SendMessage(request, timeout=self.timeout)
                
                logger.info(f"✅ Message sent successfully: {response.result}")
                return response.result
                
            except grpc.RpcError as e:
                logger.error(f"❌ gRPC error on attempt {attempt + 1}: {e.code()} - {e.details()}")
                if attempt < retries:
                    time.sleep(2 ** attempt)  # Exponential backoff
                else:
                    return None
            except Exception as e:
                logger.error(f"❌ Unexpected error on attempt {attempt + 1}: {e}")
                if attempt < retries:
                    time.sleep(2 ** attempt)
                else:
                    return None
        
        return None
    
    def subscribe(self, topic: str, callback: Callable[[str, str, str], None], 
                  client_id: Optional[str] = None) -> bool:
        """
        Subscribe to a RocketMQ topic
        
        Args:
            topic: The topic to subscribe to
            callback: Function to call when a message is received
                     callback(topic, body, msg_id)
            client_id: Optional client ID
            
        Returns:
            True if subscription started successfully, False otherwise
        """
        if not self.stub:
            logger.error("❌ Not connected to server")
            return False
        
        client_id = client_id or f"python_client_{int(time.time())}"
        subscription_key = f"{topic}:{client_id}"
        
        if subscription_key in self._subscriptions:
            logger.warning(f"⚠️  Already subscribed to {topic} with client ID {client_id}")
            return True
        
        try:
            logger.info(f"🔔 Subscribing to topic '{topic}' with client ID '{client_id}'")
            
            request = mq_pb2.SubscribeRequest(topic=topic, client_id=client_id)
            
            # Start subscription in a separate thread
            subscription_thread = threading.Thread(
                target=self._handle_subscription,
                args=(subscription_key, request, callback),
                daemon=True
            )
            subscription_thread.start()
            
            self._subscriptions[subscription_key] = subscription_thread
            logger.info(f"✅ Subscription started for topic '{topic}'")
            return True
            
        except Exception as e:
            logger.error(f"❌ Failed to subscribe: {e}")
            return False
    
    def _handle_subscription(self, subscription_key: str, request: mq_pb2.SubscribeRequest, 
                           callback: Callable[[str, str, str], None]):
        """Handle subscription in a separate thread"""
        try:
            self._running = True
            
            # Create streaming response
            responses = self.stub.Subscribe(request)
            
            for response in responses:
                if not self._running:
                    break
                
                try:
                    logger.debug(f"📨 Received message: {response.msg_id}")
                    callback(response.topic, response.body, response.msg_id)
                except Exception as e:
                    logger.error(f"❌ Error in message callback: {e}")
                    
        except grpc.RpcError as e:
            if e.code() != grpc.StatusCode.CANCELLED:
                logger.error(f"❌ Subscription error: {e.code()} - {e.details()}")
        except Exception as e:
            logger.error(f"❌ Unexpected subscription error: {e}")
        finally:
            # Clean up subscription
            if subscription_key in self._subscriptions:
                del self._subscriptions[subscription_key]
            logger.info(f"🔚 Subscription ended for {subscription_key}")
    
    def unsubscribe(self, topic: str, client_id: Optional[str] = None) -> bool:
        """
        Unsubscribe from a topic
        
        Args:
            topic: The topic to unsubscribe from
            client_id: Client ID (if None, unsubscribes all clients from this topic)
            
        Returns:
            True if unsubscribed successfully, False otherwise
        """
        if client_id:
            subscription_key = f"{topic}:{client_id}"
            if subscription_key in self._subscriptions:
                self._running = False
                del self._subscriptions[subscription_key]
                logger.info(f"🔚 Unsubscribed from {topic} with client ID {client_id}")
                return True
        else:
            # Unsubscribe all clients from this topic
            keys_to_remove = [key for key in self._subscriptions.keys() if key.startswith(f"{topic}:")]
            for key in keys_to_remove:
                self._running = False
                del self._subscriptions[key]
                logger.info(f"🔚 Unsubscribed from {topic}")
            return len(keys_to_remove) > 0
        
        return False
    
    def disconnect(self):
        """Disconnect from the gRPC server"""
        logger.info("🔌 Disconnecting from RocketMQ Gateway...")
        
        # Stop all subscriptions
        self._running = False
        self._subscriptions.clear()
        
        # Close channel
        if self.channel:
            self.channel.close()
            self.channel = None
            self.stub = None
        
        logger.info("✅ Disconnected successfully")


def message_handler(topic: str, body: str, msg_id: str):
    """Example message handler"""
    logger.info(f"📨 Received message:")
    logger.info(f"   Topic: {topic}")
    logger.info(f"   Message ID: {msg_id}")
    logger.info(f"   Body: {body}")
    logger.info(f"   Timestamp: {time.strftime('%Y-%m-%d %H:%M:%S')}")
    logger.info("-" * 50)


def main():
    """Main example function"""
    print("🚀 RocketMQ Gateway Python Client")
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
            ("audio_processing", {
                "task_id": "task_001",
                "audio_file": "sample.wav",
                "language": "zh-CN"
            }),
            ("audio_processing", {
                "task_id": "task_002", 
                "audio_file": "sample2.wav",
                "language": "en-US"
            }),
            ("status_updates", {
                "task_id": "task_001",
                "status": "processing",
                "progress": 50
            })
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
