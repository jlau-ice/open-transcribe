package main

import (
	"log"
	"os"
	"strconv"
	"strings"

	"github.com/spf13/viper"
)

type Config struct {
	RocketMQ struct {
		NameServer string
		Topic      string
		Group      string
		Retry      int
	}
	Server struct {
		Port int
		Host string
	}
	Python struct {
		Callback string
	}
	Log struct {
		Level string
	}
}

func LoadConfig() Config {
	var config Config

	// 设置配置文件
	viper.SetConfigName("application")
	viper.SetConfigType("yaml")
	viper.AddConfigPath(".")

	// 设置环境变量前缀
	viper.SetEnvPrefix("RMC")
	viper.AutomaticEnv()
	
	// 设置环境变量键名替换规则
	viper.SetEnvKeyReplacer(strings.NewReplacer(".", "_"))

	// 设置默认值
	setDefaults()

	// 读取配置文件（如果存在）
	if err := viper.ReadInConfig(); err != nil {
		if _, ok := err.(viper.ConfigFileNotFoundError); ok {
			log.Println("⚠️  Config file not found, using environment variables and defaults")
		} else {
			log.Fatalf("❌ Error reading config file: %v", err)
		}
	}

	// 手动处理环境变量覆盖
	overrideWithEnvVars(&config)

	err := viper.Unmarshal(&config)
	if err != nil {
		log.Fatalf("❌ Unable to decode config: %v", err)
	}

	// 验证配置
	validateConfig(&config)

	log.Printf("✅ Config loaded successfully")
	log.Printf("📋 Configuration summary:")
	log.Printf("   - Server: %s:%d", config.Server.Host, config.Server.Port)
	log.Printf("   - RocketMQ: %s", config.RocketMQ.NameServer)
	log.Printf("   - Python Callback: %s", config.Python.Callback)
	
	return config
}

func setDefaults() {
	viper.SetDefault("server.host", "0.0.0.0")
	viper.SetDefault("server.port", 8080)
	viper.SetDefault("rocketmq.nameserver", "127.0.0.1:9876")
	viper.SetDefault("rocketmq.topic", "TestTopic")
	viper.SetDefault("rocketmq.group", "TestGroup")
	viper.SetDefault("rocketmq.retry", 2)
	viper.SetDefault("python.callback", "http://127.0.0.1:5000/mq_callback")
	viper.SetDefault("log.level", "info")
}

func overrideWithEnvVars(config *Config) {
	// 手动处理一些特殊的环境变量
	if host := os.Getenv("RMC_SERVER_HOST"); host != "" {
		config.Server.Host = host
	}
	
	if portStr := os.Getenv("RMC_SERVER_PORT"); portStr != "" {
		if port, err := strconv.Atoi(portStr); err == nil {
			config.Server.Port = port
		}
	}
	
	if nameServer := os.Getenv("RMC_ROCKETMQ_NAMESERVER"); nameServer != "" {
		config.RocketMQ.NameServer = nameServer
	}
	
	if callback := os.Getenv("RMC_PYTHON_CALLBACK"); callback != "" {
		config.Python.Callback = callback
	}
}

func validateConfig(config *Config) {
	if config.Server.Port <= 0 || config.Server.Port > 65535 {
		log.Fatalf("❌ Invalid server port: %d", config.Server.Port)
	}
	
	if config.RocketMQ.NameServer == "" {
		log.Fatalf("❌ RocketMQ name server cannot be empty")
	}
	
	if config.Python.Callback == "" {
		log.Printf("⚠️  Python callback URL is empty")
	}
}
