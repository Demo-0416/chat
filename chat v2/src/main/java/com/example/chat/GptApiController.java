package com.example.chat;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.ConcurrentHashMap;

import static com.example.chat.ChatWithGPT.*;

@CrossOrigin
@RestController
@RequestMapping("/chat")
public class GptApiController {
    private final SessionManager sessionManager;
    private ConcurrentHashMap<String, DialogueSession> userSessions = new ConcurrentHashMap<>();

    public GptApiController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    @PostMapping("/startDialogue")
    public ResponseEntity<String> startDialogue() {
        String dialogueId = generateUniqueDialogueId();
        return ResponseEntity.ok(dialogueId);
    }

    private String generateUniqueDialogueId() {
        String dialogueId = "";
        return dialogueId;
    }

    @PostMapping("/NewUserMessage")
    public ResponseEntity<String>receiveUserMessage(HttpServletRequest request) throws Exception {
        int dialogueId=Integer.parseInt(request.getParameter("dialogueId"));
        System.out.println(dialogueId);
        String userMessage=request.getParameter("userMessage");
        String userAdditionalRequest=request.getParameter("userAdditionalRequest");
        DialogueSession session = userSessions.computeIfAbsent(String.valueOf(dialogueId), k -> new DialogueSession());
        System.out.println("接收到用户信息，开始计算相似度");
        if (session.isEmpty()) {
            System.out.println("这是一个新的session");
            System.out.println(userMessage);
            String promptToAdd = SemanticSimilarity.semanticSimilarityDirectInRegulations(userMessage).toString();
            System.out.println(promptToAdd);
            session.addGptResponse(escapeStringForJson("你被开发来回答一些法律相关的问题，你将帮助解决用户的问题。我通过nlp处理已经找到了需要的和用户问题相关的法律，你的所有回答都将给予以下你会需要的法律知识：" + promptToAdd + "在回答用户的请求的时候你需要遵守以下准则://1.在回答用户的问题时候尽可能展现详细的法条内容 //2，回答要具体并有建设性 //3.回答只基于以上法律，不相关的法律不要出现//4.以下是用户的一些额外要求（如果没有则忽略）")+userAdditionalRequest);
        }
        session.addUserMessage(escapeStringForJson(userMessage));
        String prompt = session.generatePrompt();
        System.out.println("sending request");
        String gptResponse = GPT3API.getResponse(prompt);
        session.addGptResponse(escapeStringForJson(gptResponse));
        return ResponseEntity.ok(gptResponse);
    }

    @PostMapping("/historyUserMessage")
    public ResponseEntity<String>historyUserMessage(HttpServletRequest request) throws Exception {
        int dialogueId=Integer.parseInt(request.getParameter("dialogueId"));
        System.out.println(dialogueId);
        String userMessage=request.getParameter("userMessage");
        String userAdditionalRequest=request.getParameter("userAdditionalRequest");
        DialogueSession session = userSessions.computeIfAbsent(String.valueOf(dialogueId), k -> new DialogueSession());
        session.setGptResponsesHistory(deserializeDeque(userMapper.findGptMessage(dialogueId)));
        session.setUserMessagesHistory(deserializeDeque(userMapper.findUserMessage(dialogueId)));
        session.addUserMessage(escapeStringForJson(userMessage));
        String prompt = session.generatePrompt();
        System.out.println("sending request");
        String gptResponse = GPT3API.getResponse(prompt);
        session.addGptResponse(escapeStringForJson(gptResponse));
        return ResponseEntity.ok(gptResponse);
    }
}
