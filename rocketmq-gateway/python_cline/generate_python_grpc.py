"""
生成Python gRPC代码的脚本
修改：将代码生成到当前目录
"""

import subprocess
import sys
import os

# 定义 Protobuf 文件路径
PROTO_FILE = 'proto/mq.proto'
# 定义输出目录（当前目录）
OUTPUT_DIR = '.' # '.' 表示当前目录

def generate_grpc_code():
    """生成Python gRPC代码"""
    print("🔧 生成Python gRPC代码...")

    # 检查protoc是否安装
    try:
        result = subprocess.run(['protoc', '--version'], capture_output=True, text=True)
        print(f"✅ 找到protoc: {result.stdout.strip()}")
    except FileNotFoundError:
        print("❌ 未找到protoc，请先安装Protocol Buffers编译器")
        print("   确保 'protoc' 命令在您的系统 PATH 中。")
        return False

    # 检查 proto 文件是否存在
    if not os.path.exists(PROTO_FILE):
        print(f"❌ 未找到 Protobuf 文件: {PROTO_FILE}")
        print("   请确保您的项目结构中存在 'proto/mq.proto'。")
        return False

    # 生成Python代码的命令
    cmd = [
        'protoc',
        f'--python_out={OUTPUT_DIR}',        # 输出到当前目录
        f'--grpc_python_out={OUTPUT_DIR}',    # 输出到当前目录
        PROTO_FILE
    ]

    print(f"   执行命令: {' '.join(cmd)}")

    try:
        result = subprocess.run(cmd, capture_output=True, text=True, check=True) # 使用 check=True 来捕获非零返回码

        # 预期的生成文件名（基于 mq.proto）
        expected_files = ['mq_pb2.py', 'mq_pb2_grpc.py']

        print("✅ Python gRPC代码生成成功!")
        print("   生成的文件:")
        for file in expected_files:
            if os.path.exists(file):
                print(f"   - {file} (已生成到当前目录)")
            else:
                print(f"   - {file} (未找到，请检查)")

        return True

    except subprocess.CalledProcessError as e:
        print(f"❌ 生成失败，protoc 返回错误码 {e.returncode}:")
        print("--- stderr ---")
        print(e.stderr)
        print("--- stdout ---")
        print(e.stdout)
        return False
    except Exception as e:
        print(f"❌ 执行错误: {e}")
        return False

if __name__ == "__main__":
    if generate_grpc_code():
        print("\n🎉 现在可以直接导入并运行Python客户端测试了!")
        print("   （如果您之前的客户端代码有 `sys.path.append('python_grpc')`，请记得移除）")
    else:
        print("\n❌ 生成失败，请检查您的环境配置和 proto 文件路径。")
        sys.exit(1)