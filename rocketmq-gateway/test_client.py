#!/usr/bin/env python3
"""
Simple test client for RocketMQ Gateway
"""

import grpc
import json
import time

# Simple test without generated protobuf files
def test_connection():
    """Test basic connection to the gateway"""
    try:
        # Try to connect to the gRPC server
        channel = grpc.insecure_channel('localhost:8080')
        
        # Test if we can connect (this will fail without proper stub, but we can test connectivity)
        print("🔌 Testing connection to RocketMQ Gateway...")
        
        # Simple connectivity test
        try:
            grpc.channel_ready_future(channel).result(timeout=5)
            print("✅ Successfully connected to gRPC server")
            return True
        except grpc.RpcError as e:
            print(f"❌ gRPC error: {e}")
            return False
        except Exception as e:
            print(f"❌ Connection failed: {e}")
            return False
            
    except Exception as e:
        print(f"❌ Failed to create channel: {e}")
        return False

def main():
    print("🚀 RocketMQ Gateway Connection Test")
    print("=" * 40)
    
    if test_connection():
        print("\n✅ Gateway is running and accessible!")
        print("📝 You can now use the Python client to interact with the gateway.")
    else:
        print("\n❌ Gateway is not accessible.")
        print("💡 Make sure the gateway is running on localhost:8080")

if __name__ == "__main__":
    main()
