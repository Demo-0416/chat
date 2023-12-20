package com.example.chat;

import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;

@Service
public class SessionManager {
    // Create a ConcurrentHashMap to store the user's sessions
    private ConcurrentHashMap<String, DialogueSession> userSessions = new ConcurrentHashMap<>();

    // Get the session for the user with the given dialogueId
    public DialogueSession getSession(String dialogueId) {
        // If the session does not exist, create a new one and store it in the map
        return userSessions.computeIfAbsent(dialogueId, k -> new DialogueSession());
    }

    // Remove the session for the user with the given dialogueId
    public void removeSession(String dialogueId) {
        userSessions.remove(dialogueId);
    }

}