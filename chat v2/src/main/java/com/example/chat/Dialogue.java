package com.example.chat;

import lombok.Data;

@Data
public class Dialogue {
    String userMessage;
    String gptMessage;
    int dialogueId;
}
