package main

import (
	"fmt"
	"github.com/spf13/viper"
	"log"
)

type Config struct {
	RocketMQ struct {
		NameServer string
		Topic      string
		Group      string
	}
	Server struct {
		Port int
	}
	Python struct {
		Callback string
	}
}

func LoadConfig() Config {
	var config Config

	viper.SetConfigName("application")
	viper.SetConfigType("yaml")
	viper.AddConfigPath(".")

	if err := viper.ReadInConfig(); err != nil {
		log.Fatalf("Error reading config file: %v", err)
	}

	err := viper.Unmarshal(&config)
	if err != nil {
		log.Fatalf("Unable to decode config: %v", err)
	}

	fmt.Println("✅ Config loaded successfully")
	return config
}
