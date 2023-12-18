package com.example.chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayDeque;
import java.util.Scanner;

import static com.example.chat.GPT3API.getResponse;


public class ChatWithGPT {

    private static final int MAX_HISTORY_SIZE = 15;
    static ArrayDeque<String> userMessagesHistory = new ArrayDeque<>();
    static ArrayDeque<String> gptResponsesHistory = new ArrayDeque<>();

    static UserMapper userMapper;

    public static String generatePrompt() {
        StringBuilder prompt = new StringBuilder();

        String[] userMessagesArray = userMessagesHistory.toArray(new String[0]);
        String[] gptResponsesArray = gptResponsesHistory.toArray(new String[0]);

        for (int i = 0; i < userMessagesArray.length; i++) {
            prompt.append("    {\"role\": \"system\", \"content\": \"").append(gptResponsesArray[i]).append("\"},");
            prompt.append("    {\"role\": \"user\", \"content\": \"").append(userMessagesArray[i]).append("\"},");
        }
        if (!prompt.isEmpty()) {
            prompt.setLength(prompt.length() - 1);
        }
        return prompt.toString();
    }

    public static void chat() throws Exception {
        System.out.println("你：");
        Scanner scanner = new Scanner(System.in);
        String input = scanner.nextLine();
        String prompt;
        if (userMessagesHistory.size() >=MAX_HISTORY_SIZE) {
            userMessagesHistory.pollFirst();
        }
        userMessagesHistory.addLast(input);
        prompt = generatePrompt();
        System.out.println(prompt);
        String response = getResponse(prompt);
        if (gptResponsesHistory.size() >=MAX_HISTORY_SIZE) {
            gptResponsesHistory.pollFirst();
        }
        gptResponsesHistory.addLast(escapeStringForJson(response));
        System.out.println("GPT:\n" + response+"\n");
    }

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

    public static void main(String[] args) throws Exception {
        gptResponsesHistory.addLast("you are a helpful assistant，");
        while(true){
            chat();
        }
    }

    public static void saveToDatabase(int id){
        userMapper.updateUserGpt(id, serializeDeque(userMessagesHistory), serializeDeque(gptResponsesHistory));
    }

    public String returnUserMessage(){
        return serializeDeque(userMessagesHistory);
    }

    public static String serializeDeque(ArrayDeque<String> deque) {
        return new JSONArray(deque).toString();
    }

    public static ArrayDeque<String> deserializeDeque(String serializedData) throws JSONException {
        ArrayDeque<String> deque = new ArrayDeque<>();
        JSONArray jsonArray = new JSONArray(serializedData);
        for (int i = 0; i < jsonArray.length(); i++) {
            deque.add(jsonArray.getString(i));
        }
        return deque;
    }

}
