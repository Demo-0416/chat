package com.example.chat;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionManager {
    private ConcurrentHashMap<String, DialogueSession> userSessions = new ConcurrentHashMap<>();

    public DialogueSession getSession(String dialogueId) {
        return userSessions.computeIfAbsent(dialogueId, k -> new DialogueSession());
    }
    public void removeSession(String dialogueId) {
        userSessions.remove(dialogueId);
    }

}
