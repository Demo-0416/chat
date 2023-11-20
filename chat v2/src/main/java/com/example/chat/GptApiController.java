package com.example.chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/chat")
public class GptApiController {
    @PostMapping("/userMessage")
    public ResponseEntity<String>receiveUserMessage(@RequestBody String userMessage) throws Exception {
        ChatWithGPT.userMessagesHistory.addLast(userMessage);
        String prompt=ChatWithGPT.generatePrompt();
        String gptResponse=GPT3API.getResponse(prompt);
        ChatWithGPT.gptResponsesHistory.addLast(gptResponse);
        return ResponseEntity.ok(gptResponse);
    }
}
