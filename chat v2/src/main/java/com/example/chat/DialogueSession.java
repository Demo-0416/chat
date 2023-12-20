package com.example.chat;


import lombok.Data;
import lombok.Getter;
import org.json.JSONArray;
import org.json.JSONException;

import java.util.ArrayDeque;

public class DialogueSession {
    // Getter method to return the count
    @Getter
    private int getCount;
    // Getter method to return the user messages history
    @Getter
    private ArrayDeque<String> userMessagesHistory = new ArrayDeque<>();
    // Getter method to return the GPT responses history
    @Getter
    private ArrayDeque<String> gptResponsesHistory = new ArrayDeque<>();

    // Method to check if the GPT responses history is empty
    public boolean isEmpty() {
        return gptResponsesHistory.isEmpty();
    }

    // Method to add a user message to the user messages history
    public void addUserMessage(String message) {
        userMessagesHistory.addLast(message);
    }

    // Method to add a GPT response to the GPT responses history
    public void addGptResponse(String response) {
        gptResponsesHistory.addLast(response);
    }

    // Method to set the user messages history
    public void setUserMessagesHistory(ArrayDeque<String> userMessagesHistory) {
        this.userMessagesHistory = userMessagesHistory;
    }

    // Method to set the GPT responses history
    public void setGptResponsesHistory(ArrayDeque<String> gptResponsesHistory) {
        this.gptResponsesHistory = gptResponsesHistory;
    }

    // Method to generate a prompt
    public String generatePrompt() {
        StringBuilder prompt = new StringBuilder();

        String[] userMessagesArray = userMessagesHistory.toArray(new String[0]);
        String[] gptResponsesArray = gptResponsesHistory.toArray(new String[0]);

        for (int i = 0; i < userMessagesArray.length; i++) {
            prompt.append("    {\"role\": \"system\", \"content\": \"").append(gptResponsesArray[i]).append("\"},");
            prompt.append("    {\"role\": \"user\", \"content\": \"").append(userMessagesArray[i]).append("\"},");
        }
        if (gptResponsesArray.length > userMessagesArray.length) {
            prompt.append("    {\"role\": \"system\", \"content\": \"").append(gptResponsesArray[userMessagesArray.length + 1]).append("\"},");
        }
        if (!prompt.isEmpty()) {
            prompt.setLength(prompt.length() - 1);
        }
        System.out.println(prompt);
        return prompt.toString();
    }

    // Method to return the user messages history
    public String returnUserMessage() {
        return serializeDeque(userMessagesHistory);
    }

    // Method to return the GPT responses history
    public String returnGptResponse() {
        return serializeDeque(gptResponsesHistory);
    }

    // Method to serialize the deque
    public static String serializeDeque(ArrayDeque<String> deque) {
        return new JSONArray(deque).toString();
    }

    // Method to deserialize the deque
    public static ArrayDeque<String> deserializeDeque(String serializedData) throws JSONException {
        ArrayDeque<String> deque = new ArrayDeque<>();
        JSONArray jsonArray = new JSONArray(serializedData);
        for (int i = 0; i < jsonArray.length(); i++) {
            deque.add(jsonArray.getString(i));
        }
        return deque;
    }

    // Method to add the count
    public void addGetCount() {
    }
}