package com.example.chat;


import lombok.Data;

@Data
public class Advice {
    // adviceID is the ID of the advice
    private int adviceID;
    // userEmail is the email of the user who sent the advice
    private String userEmail;
    // userAdvice is the advice sent by the user
    private String userAdvice;
    // managerReceive is the email of the manager who received the advice
    private String managerReceive;
    // number is the number of the advice
    private int number;
    // code is the code of the advice
    private int code = 0;
}