package com.example.chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.Scanner;

import static com.example.chat.GPT3API.getResponse;


public class ChatWithGPT {

    //Maximum history size of the conversation
    private static final int MAX_HISTORY_SIZE = 15;
    //ArrayDeque to store user messages history
    static ArrayDeque<String> userMessagesHistory = new ArrayDeque<>();
    //ArrayDeque to store GPT3 responses history
    static ArrayDeque<String> gptResponsesHistory = new ArrayDeque<>();

    //User mapper to map user id to user object
    static UserMapper userMapper;

    //Generate prompt for the conversation
    public static String generatePrompt() {
        StringBuilder prompt = new StringBuilder();

        //Convert ArrayDeque to array to store user messages and GPT3 responses
        String[] userMessagesArray = userMessagesHistory.toArray(new String[0]);
        String[] gptResponsesArray = gptResponsesHistory.toArray(new String[0]);

        //Loop through user messages and GPT3 responses array
        for (int i = 0; i < userMessagesArray.length; i++) {
            //Append user messages and GPT3 responses to the prompt
            prompt.append("    {\"role\": \"system\", \"content\": \"").append(gptResponsesArray[i]).append("\"},");
            prompt.append("    {\"role\": \"user\", \"content\": \"").append(userMessagesArray[i]).append("\"},");
        }
        //Remove the last comma from the prompt
        if (!prompt.isEmpty()) {
            prompt.setLength(prompt.length() - 1);
        }
        //Return the prompt
        return prompt.toString();
    }

    //Chat with GPT3
    public static void chat() throws Exception {
        //Prompt user to start the conversation
        System.out.println("你：");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String prompt;
        //Check if the user messages history is greater than the maximum history size
        if (userMessagesHistory.size() >= MAX_HISTORY_SIZE) {
            //Remove the first element of the user messages history
            userMessagesHistory.pollFirst();
        }
        //Add the user message to the user messages history
        userMessagesHistory.addLast(input);
        //Generate the prompt
        prompt = generatePrompt();
        //Print the prompt
        System.out.println(prompt);
        //Get the response from GPT3
        String response = getResponse(prompt);
        //Check if the GPT3 responses history is greater than the maximum history size
        if (gptResponsesHistory.size() >= MAX_HISTORY_SIZE) {
            //Remove the first element of the GPT3 responses history
            gptResponsesHistory.pollFirst();
        }
        //Add the GPT3 response to the GPT3 responses history
        gptResponsesHistory.addLast(escapeStringForJson(response));
        //Print the GPT3 response
        System.out.println("GPT:\n" + response + "\n");
    }

    //Escape string for JSON
    public static String escapeStringForJson(String input) {
        return input
                .replace("\\", "\\\\")  // 替换反斜杠
                .replace("\"", "\\\"")  // 替换双引号
                .replace("\b", "\\b")   // 替换退格
                .replace("\f", "\\f")   // 替换换页
                .replace("\n", "\\n")   // 替换换行
                .replace("\r", "\\r")   // 替换回车
                .replace("\t", "\\t");  // 替换制表符
    }

    //Main method to start the conversation
    public static void main(String[] args) throws Exception {
        //Add the first message to the user messages history
        gptResponsesHistory.addLast("you are a helpful assistant，");
        //Loop to start the conversation
        while (true) {
            chat();
        }
    }

    //Method to save the user messages history and GPT3 responses history to the database
    public static void saveToDatabase(int id) {
        //Call the user mapper to update the user object with the serialized user messages history and GPT3 responses history
        userMapper.updateUserGpt(id, serializeDeque(userMessagesHistory), serializeDeque(gptResponsesHistory));
    }

    //Method to return the user messages history
    public String returnUserMessage() {
        //Return the serialized user messages history
        return serializeDeque(userMessagesHistory);
    }

    //Method to serialize the ArrayDeque
    public static String serializeDeque(ArrayDeque<String> deque) {
        //Return the JSON array of the ArrayDeque
        return new JSONArray(deque).toString();
    }

    //Method to deserialize the ArrayDeque
    public static ArrayDeque<String> deserializeDeque(String serializedData) throws JSONException {
        //Create a new ArrayDeque
        ArrayDeque<String> deque = new ArrayDeque<>();
        //Create a new JSONArray from the serialized data
        JSONArray jsonArray = new JSONArray(serializedData);
        //Loop through the JSONArray
        for (int i = 0; i < jsonArray.length(); i++) {
            //Add the element of the JSONArray to the ArrayDeque
            deque.add(jsonArray.getString(i));
        }
        //Return the ArrayDeque
        return deque;
    }

}