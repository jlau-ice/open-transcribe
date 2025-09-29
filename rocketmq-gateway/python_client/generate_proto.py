#!/usr/bin/env python3
"""
Generate Python gRPC files from proto definition
"""

import subprocess
import sys
import os

def generate_grpc_files():
    """Generate Python gRPC files from proto definition"""
    
    # Create proto directory if it doesn't exist
    os.makedirs("proto", exist_ok=True)
    
    # Copy proto file from parent directory
    proto_content = '''
syntax = "proto3";

package mq;

service RocketMQGateway {
  rpc SendMessage (SendRequest) returns (SendResponse);
  rpc Subscribe(SubscribeRequest) returns (stream MessageResponse);
  rpc HealthCheck (HealthCheckRequest) returns (HealthCheckResponse);
}

message SendRequest {
  string topic = 1;
  string body = 2;
}

message SendResponse {
  string result = 1;
}

message SubscribeRequest {
  string topic = 1;
  string client_id = 2;
}

message MessageResponse {
  string topic = 1;
  string body = 2;
  string msg_id = 3;
}

message HealthCheckRequest {
  string service = 1;
}

message HealthCheckResponse {
  enum ServingStatus {
    UNKNOWN = 0;
    SERVING = 1;
    NOT_SERVING = 2;
    SERVICE_UNKNOWN = 3;
  }
  ServingStatus status = 1;
  string message = 2;
}
'''
    
    with open("proto/mq.proto", "w") as f:
        f.write(proto_content)
    
    try:
        # Generate Python files
        subprocess.run([
            sys.executable, "-m", "grpc_tools.protoc",
            "--python_out=.",
            "--grpc_python_out=.",
            "--proto_path=proto",
            "proto/mq.proto"
        ], check=True)
        
        print("✅ Generated Python gRPC files successfully")
        return True
        
    except subprocess.CalledProcessError as e:
        print(f"❌ Failed to generate gRPC files: {e}")
        return False

if __name__ == "__main__":
    generate_grpc_files()
