package com.example.chat;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import static com.example.chat.ChatWithGPT.escapeStringForJson;
import static com.example.chat.ChatWithGPT.gptResponsesHistory;

@CrossOrigin
@RestController
@RequestMapping("/chat")
public class GptApiController {
    @PostMapping("/NewUserMessage")
    public ResponseEntity<String>receiveUserMessage(@RequestBody String userMessage) throws Exception {
        System.out.println("接收到用户信息，开始计算相似度");
        if(count==0)
        {
            String promptToAdd = SemanticSimilarity.semanticSimilarityDirectInRegulations(userMessage).toString();
            System.out.println(promptToAdd);
            gptResponsesHistory.addLast(escapeStringForJson("你被开发来回答一些法律相关的问题，你将帮助解决用户的问题。我通过nlp处理已经找到了需要的和用户问题相关的法律，你的所有回答都将给予以下你会需要的法律知识：" + promptToAdd+"在回答用户的请求的时候你需要遵守以下准则://1.在回答用户的问题时候尽可能展现详细的法条内容 //2，回答要具体并有建设性 //3.回答只基于以上法律，不相关的法律不要出现"));
            count++;
        }
        System.out.println("sending request");
        ChatWithGPT.userMessagesHistory.addLast(escapeStringForJson(userMessage));
        String prompt=ChatWithGPT.generatePrompt();
        System.out.println(prompt);
        String gptResponse=GPT3API.getResponse(prompt);
        ChatWithGPT.gptResponsesHistory.addLast(escapeStringForJson(gptResponse));
        return ResponseEntity.ok(gptResponse);
    }

    int count = 0;
}



