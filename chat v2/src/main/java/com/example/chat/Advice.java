package com.example.chat;


import lombok.Data;

@Data
public class Advice {
    private int adviceID;
    private String userEmail;
    private String userAdvice;
    private String managerReceive;
    private int number;
    private int code=0;
}
