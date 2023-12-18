package com.example.chat;


import lombok.Getter;

import java.util.ArrayDeque;

public class UserSession {
    @Getter
    private ArrayDeque<String> userMessagesHistory = new ArrayDeque<>();
    @Getter
    private ArrayDeque<String> gptResponsesHistory = new ArrayDeque<>();
    public boolean isEmpty(){
        return gptResponsesHistory.isEmpty();
    }

    public void addUserMessage(String message) {
        userMessagesHistory.addLast(message);
    }

    public void addGptResponse(String response) {
        gptResponsesHistory.addLast(response);
    }

    public String generatePrompt() {
        StringBuilder prompt = new StringBuilder();

        String[] userMessagesArray = userMessagesHistory.toArray(new String[0]);
        String[] gptResponsesArray = gptResponsesHistory.toArray(new String[0]);

        for (int i = 0; i < userMessagesArray.length; i++) {
            prompt.append("    {\"role\": \"system\", \"content\": \"").append(gptResponsesArray[i]).append("\"},");
            prompt.append("    {\"role\": \"user\", \"content\": \"").append(userMessagesArray[i]).append("\"},");
        }
        if(gptResponsesArray.length>userMessagesArray.length){
            prompt.append("    {\"role\": \"system\", \"content\": \"").append(gptResponsesArray[userMessagesArray.length+1]).append("\"},");
        }
        if (!prompt.isEmpty()) {
            prompt.setLength(prompt.length() - 1);
        }
        return prompt.toString();
    }

}
