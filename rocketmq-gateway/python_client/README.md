# RocketMQ Gateway Python Client

A Python client for the RocketMQ Gateway service that allows Python applications to interact with RocketMQ through gRPC.

## Features

- **Send Messages**: Send messages to RocketMQ topics
- **Subscribe to Topics**: Receive messages from RocketMQ topics via streaming
- **Health Check**: Check the health status of the gateway
- **Automatic Reconnection**: Built-in retry logic and connection management
- **Thread-Safe**: Support for concurrent operations

## Installation

1. Install dependencies:
```bash
pip install -r requirements.txt
```

2. Generate gRPC files:
```bash
python generate_proto.py
```

## Usage

### Basic Usage

```python
from client import RocketMQGatewayClient

# Create client
client = RocketMQGatewayClient("localhost", 8080)

# Connect
if client.connect():
    # Send a message
    result = client.send_message("my_topic", "Hello, RocketMQ!")
    print(f"Message sent: {result}")
    
    # Subscribe to messages
    def on_message(topic, body, msg_id):
        print(f"Received: {body} from {topic}")
    
    client.subscribe("my_topic", on_message)
    
    # Keep running
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        client.disconnect()
```

### Advanced Usage

```python
import json
from client import RocketMQGatewayClient

def message_handler(topic: str, body: str, msg_id: str):
    """Handle incoming messages"""
    try:
        data = json.loads(body)
        print(f"Received JSON message: {data}")
    except json.JSONDecodeError:
        print(f"Received text message: {body}")

# Create client with custom settings
client = RocketMQGatewayClient(
    host="localhost",
    port=8080,
    max_retries=5,
    timeout=30
)

if client.connect():
    # Send structured data
    message_data = {
        "task_id": "task_001",
        "audio_file": "sample.wav",
        "language": "zh-CN"
    }
    
    client.send_message("audio_processing", message_data)
    
    # Subscribe with custom client ID
    client.subscribe(
        topic="audio_processing_results",
        callback=message_handler,
        client_id="my_python_app"
    )
```

## API Reference

### RocketMQGatewayClient

#### Constructor
```python
RocketMQGatewayClient(host="localhost", port=8080, max_retries=3, timeout=30)
```

#### Methods

##### `connect() -> bool`
Connect to the gRPC server.

##### `send_message(topic: str, body: str, retries: Optional[int] = None) -> Optional[str]`
Send a message to a RocketMQ topic.
- `topic`: The topic name
- `body`: The message body (string or dict)
- `retries`: Number of retries (overrides default)
- Returns: Result string if successful, None if failed

##### `subscribe(topic: str, callback: Callable, client_id: Optional[str] = None) -> bool`
Subscribe to a RocketMQ topic.
- `topic`: The topic to subscribe to
- `callback`: Function to call when a message is received
- `client_id`: Optional client ID
- Returns: True if subscription started successfully

##### `unsubscribe(topic: str, client_id: Optional[str] = None) -> bool`
Unsubscribe from a topic.

##### `disconnect()`
Disconnect from the gRPC server.

## Examples

### Audio Processing Example

```python
import json
import time
from client import RocketMQGatewayClient

def process_audio_result(topic: str, body: str, msg_id: str):
    """Handle audio processing results"""
    try:
        result = json.loads(body)
        print(f"Audio processing completed:")
        print(f"  Task ID: {result.get('task_id')}")
        print(f"  Status: {result.get('status')}")
        print(f"  Transcription: {result.get('transcription', 'N/A')}")
    except Exception as e:
        print(f"Error processing result: {e}")

# Create client
client = RocketMQGatewayClient()

if client.connect():
    # Send audio processing request
    audio_request = {
        "task_id": "audio_001",
        "audio_file": "meeting.wav",
        "language": "zh-CN",
        "format": "json"
    }
    
    client.send_message("audio_processing", audio_request)
    
    # Subscribe to results
    client.subscribe("audio_processing_results", process_audio_result)
    
    # Keep running
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        client.disconnect()
```

### Batch Processing Example

```python
import json
from client import RocketMQGatewayClient

def batch_message_handler(topic: str, body: str, msg_id: str):
    """Handle batch processing messages"""
    data = json.loads(body)
    print(f"Batch item {data.get('item_id')}: {data.get('status')}")

client = RocketMQGatewayClient()

if client.connect():
    # Send multiple messages
    for i in range(10):
        message = {
            "item_id": f"item_{i:03d}",
            "data": f"Processing item {i}",
            "priority": "normal"
        }
        client.send_message("batch_processing", message)
        time.sleep(0.1)
    
    # Subscribe to batch results
    client.subscribe("batch_results", batch_message_handler)
    
    try:
        while True:
            time.sleep(1)
    except KeyboardInterrupt:
        client.disconnect()
```

## Error Handling

The client includes built-in error handling and retry logic:

```python
# Send message with custom retry count
result = client.send_message("my_topic", "Hello", retries=5)

if result is None:
    print("Failed to send message after all retries")
else:
    print(f"Message sent successfully: {result}")
```

## Logging

The client uses Python's logging module. Configure logging level:

```python
import logging

# Set logging level
logging.getLogger().setLevel(logging.DEBUG)

# Create client
client = RocketMQGatewayClient()
```

## Environment Variables

You can configure the client using environment variables:

```bash
export RMC_SERVER_HOST=localhost
export RMC_SERVER_PORT=8080
```

## Troubleshooting

### Connection Issues
- Ensure the RocketMQ Gateway server is running
- Check network connectivity
- Verify the host and port settings

### gRPC Errors
- Make sure protobuf files are generated correctly
- Check gRPC version compatibility
- Verify the server is accepting connections

### Message Delivery
- Check RocketMQ server status
- Verify topic names and permissions
- Monitor server logs for errors
