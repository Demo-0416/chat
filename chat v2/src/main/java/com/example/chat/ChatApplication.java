package com.example.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import org.mybatis.spring.annotation.MapperScan;

@SpringBootApplication
@MapperScan("com.example.chat")
public class ChatApplication {

    public static void main(String[] args) {
        ModelLoader.loadModel();
        SpringApplication.run(ChatApplication.class, args);
    }

}
