package com.example.chat;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.URL;
import java.net.InetSocketAddress;

public class GPT3API {
    private static final String API_URL = "https://api.openai.com/v1/chat/completions";
    private static final String API_KEY = "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";

    public static String getResponse(String prompt) {
        try {
            URL url = new URL(API_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Authorization", "Bearer " + API_KEY);
            connection.setRequestProperty("Content-Type", "application/json");

            // Create and send the API request
            String jsonRequestBody = "{\"model\": \"gpt-4\", \"messages\": [" + prompt + "]}";
            connection.setDoOutput(true);
            OutputStreamWriter writer = new OutputStreamWriter(connection.getOutputStream());
            writer.write(jsonRequestBody);
            writer.flush();
            writer.close();

            int responseCode = connection.getResponseCode();
            if (responseCode != HttpURLConnection.HTTP_OK) {
                return "Error: HTTP " + responseCode;
            }

            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = br.readLine()) != null) {
                response.append(line);
            }
            br.close();

            System.out.println(response.toString());
            return readFromJson(response.toString());

        } catch (IOException e) {
            e.printStackTrace();
            return "Error: Communication error";
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: An unexpected error occurred";
        }
    }


    private static String readFromJson(String jsonResponse) throws JSONException {
        JSONObject jsonObject = new JSONObject(jsonResponse);
        JSONArray choices = jsonObject.getJSONArray("choices");
        JSONObject firstChoice = choices.getJSONObject(0);
        JSONObject message = firstChoice.getJSONObject("message");
        return message.getString("content");
    }

}