package config

import (
	"fmt"
	"log"
	"os"

	"gopkg.in/yaml.v3"
)

type Config struct {
	Server struct {
		ListenPort int `yaml:"listen_port"`
	} `yaml:"server"`

	RocketMQ struct {
		Nameservers []string `yaml:"nameservers"`
		ProxyAddr   string   `yaml:"proxy_address,omitempty"`
		Producer    struct {
			GroupName      string `yaml:"group_name"`
			SendRetryTimes int    `yaml:"send_retry_times"`
		} `yaml:"producer"`
	} `yaml:"rocketmq"`
}

var Cfg *Config

// LoadConfig 加载配置文件
func LoadConfig(path string) *Config {
	data, err := os.ReadFile(path)
	if err != nil {
		log.Fatalf("读取配置文件失败: %v", err)
	}

	var cfg Config
	if err := yaml.Unmarshal(data, &cfg); err != nil {
		log.Fatalf("解析配置文件失败: %v", err)
	}

	fmt.Println("✅ 配置文件加载成功")
	Cfg = &cfg
	return &cfg
}
