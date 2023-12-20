package com.example.chat;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

import static com.example.chat.ChatWithGPT.*;

@CrossOrigin
@RestController
@RequestMapping("/chat")
public class GptApiController {
    // Get the user mapper from the context
    @Resource
    UserMapper userMapper;
    // Create a session manager
    private final SessionManager sessionManager;
    // Create a ConcurrentHashMap to store the user sessions
    private ConcurrentHashMap<String, DialogueSession> userSessions = new ConcurrentHashMap<>();

    public GptApiController(SessionManager sessionManager) {
        this.sessionManager = sessionManager;
    }

    // Start a new dialogue session
    @PostMapping("/startDialogue")
    public void startDialogue(HttpServletRequest request) {
        // Get the session id from the request
        String sessionId = request.getParameter("cookie");
        // Remove the session from the session manager
        sessionManager.removeSession(sessionId);
    }

    // Receive a user message
    @GetMapping("/NewUserMessage")
    public ResponseEntity<String> receiveUserMessage(HttpServletRequest request) throws Exception {
        // Get the session id from the request
        String sessionId = request.getParameter("cookie");
        System.out.println(sessionId);
        // Get the user message from the request
        String userMessage = request.getParameter("userMessage");
        System.out.println(request.getParameter("dialogueid"));
        // Get the dialogue id from the request
        int dialogueId = -1;
        if (!Objects.equals(request.getParameter("dialogueid"), "")) {
            dialogueId = Integer.parseInt(request.getParameter("dialogueid"));
        }
        // Get the user additional request from the request
        String userAdditionalRequest = request.getParameter("userAdditionalRequest");
        // Get the session from the session manager
        DialogueSession session = sessionManager.getSession(sessionId);
        System.out.println(session);
        System.out.println("接收到用户信息，开始计算相似度");
        // If the session is empty and the dialogue id is empty
        if (session.isEmpty() && Objects.equals(request.getParameter("dialogueid"), "")) {
            System.out.println("这是一个新的session");
            System.out.println(userMessage);
            // Get the prompt to add from the semantic similarity
            String promptToAdd = SemanticSimilarity.semanticSimilarityDirectInRegulations(userMessage).toString();
            System.out.println(promptToAdd);
            session.addGptResponse(escapeStringForJson("你被开发来回答一些法律相关的问题，你将帮助解决用户的问题。我通过nlp处理已经找到了需要的和用户问题相关的法律，你的所有回答都将给予以下你会需要的法律知识：" + promptToAdd + "在回答用户的请求的时候你需要遵守以下准则://1.在回答用户的问题时候尽可能展现详细的法条内容 //2，回答要具体并有建设性 //3.回答只基于以上法律，不相关的法律不要出现//4.以下是用户的一些额外要求（如果没有则忽略）") + userAdditionalRequest);
            // Add the prompt to the session
            session.addGptResponse(escapeStringForJson("你被开发来回答一些法律相关的问题，你将帮助解决用户的问题。我通过nlp处理已经找到了需要的和用户问题相关的法律，你的所有回答都将给予以下你会需要的法律知识：" + promptToAdd + "在回答用户的请求的时候你需要遵守以下准则://1.在回答用户的问题时候尽可能展现详细的法条内容 //2，回答要具体且有建设性 //3.回答只基于以上法律，不相关的法律不要出现//4.以下是用户的一些额外要求（如果没有则忽略）") + userAdditionalRequest);
        }
        // If the dialogue id is not empty and the session is empty
        else if (!Objects.equals(request.getParameter("dialogueid"), "") && session.getGetCount() == 0) {
            System.out.println(dialogueId);
            // Set the history of the GPT responses and user messages from the database
            session.setGptResponsesHistory(deserializeDeque(userMapper.findGptMessage(dialogueId)));
            session.setUserMessagesHistory(deserializeDeque(userMapper.findUserMessage(dialogueId)));
            System.out.println(session.returnUserMessage() + session.returnGptResponse());
            // Add a get count
            session.addGetCount();
        }
        // Add the user message to the session
        session.addUserMessage(escapeStringForJson(userMessage));
        // Generate the prompt
        String prompt = session.generatePrompt();
        System.out.println("sending request");
        // Get the GPT response from the GPT3API
        String gptResponse = GPT3API.getResponse(prompt);
        // Add the GPT response to the session
        session.addGptResponse(escapeStringForJson(gptResponse));
        return ResponseEntity.ok("1231423542315");
    }

    // Receive the history of the user messages
    @PostMapping("/historyUserMessage")
    public ResponseEntity<String> historyUserMessage(HttpServletRequest request) throws Exception {
        // Get the dialogue id from the request
        int dialogueId = Integer.parseInt(request.getParameter("dialogueId"));
        System.out.println(dialogueId);
        // Get the user message from the request
        String userMessage = request.getParameter("userMessage");
        // Get the user additional request from the request
        String userAdditionalRequest = request.getParameter("userAdditionalRequest");
        // Get the session from the user sessions
        DialogueSession session = userSessions.computeIfAbsent(String.valueOf(dialogueId), k -> new DialogueSession());
        // Set the history of the GPT responses and user messages from the database
        session.setGptResponsesHistory(deserializeDeque(userMapper.findGptMessage(dialogueId)));
        session.setUserMessagesHistory(deserializeDeque(userMapper.findUserMessage(dialogueId)));
        // Add the user message to the session
        session.addUserMessage(escapeStringForJson(userMessage));
        // Generate the prompt
        String prompt = session.generatePrompt();
        System.out.println("sending request");
        // Get the GPT response from the GPT3API
        String gptResponse = GPT3API.getResponse(prompt);
        // Add the GPT response to the session
        session.addGptResponse(escapeStringForJson(gptResponse));
        return ResponseEntity.ok(gptResponse);
    }
}