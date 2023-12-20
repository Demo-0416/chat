package com.example.chat;

import lombok.Data;

@Data
public class Dialogue {
    // User message
    String userMessage;
    // Generated message by GPT
    String gptMessage;
    // Time of the conversation
    String time;
    // Unique identifier for the conversation
    int dialogueId;
}