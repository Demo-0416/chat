package com.example.chat;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.mail.Session;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.chat.ChatWithGPT.escapeStringForJson;

@CrossOrigin
@RestController
@RequestMapping("/chat")
public class GptApiController {
    private ConcurrentHashMap<String, UserSession> userSessions = new ConcurrentHashMap<>();

    @PostMapping("/NewUserMessage")
    public ResponseEntity<String>receiveUserMessage(HttpServletRequest request) throws Exception {
        int userId=Integer.parseInt(request.getParameter("userId"));
        System.out.println(userId);
        String userMessage=request.getParameter("userMessage");
        UserSession session = userSessions.computeIfAbsent(String.valueOf(userId), k -> new UserSession());
        System.out.println("接收到用户信息，开始计算相似度");
        if (session.isEmpty()) {
            System.out.println("这是一个新的session");
            String promptToAdd = SemanticSimilarity.semanticSimilarityDirectInRegulations(userMessage).toString();
            System.out.println(promptToAdd);
            session.addGptResponse(escapeStringForJson("你被开发来回答一些法律相关的问题，你将帮助解决用户的问题。我通过nlp处理已经找到了需要的和用户问题相关的法律，你的所有回答都将给予以下你会需要的法律知识：" + promptToAdd + "在回答用户的请求的时候你需要遵守以下准则://1.在回答用户的问题时候尽可能展现详细的法条内容 //2，回答要具体并有建设性 //3.回答只基于以上法律，不相关的法律不要出现"));
        }
        session.addUserMessage(userMessage);
        String prompt = session.generatePrompt();
        String gptResponse = GPT3API.getResponse(prompt);
        System.out.println("sending request");
        return ResponseEntity.ok(gptResponse);

    }
}
