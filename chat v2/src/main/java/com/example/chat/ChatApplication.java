package com.example.chat;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import static com.example.chat.ChatWithGPT.gptResponsesHistory;

@SpringBootApplication
public class ChatApplication {

    public static void main(String[] args) {
        gptResponsesHistory.addLast("you are a helpful assistant，");
        SpringApplication.run(ChatApplication.class, args);
    }

}
