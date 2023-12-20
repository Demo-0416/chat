package com.example.chat;

import lombok.Data;
import org.springframework.stereotype.Component;

@Component
@Data
public class User {
    //Declare a private variable to store the userId
    private Integer userId;
    //Declare a private variable to store the userName
    private String userName;
    //Declare a private variable to store the userEmail
    private String userEmail;
    //Declare a private variable to store the userPassword
    private String userPassword;

    //Getter method to return the userId
    public Integer getUserId() {
        return userId;
    }

    //Setter method to set the userId
    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    //Getter method to return the userName
    public String getUserName() {
        return userName;
    }

    //Setter method to set the userName
    public void setUserName(String userName) {
        this.userName = userName;
    }

    //Getter method to return the userEmail
    public String getUserEmail() {
        return userEmail;
    }

    //Setter method to set the userEmail
    public void setUserEmail(String userEmail) {
        this.userEmail = userEmail;
    }

    //Getter method to return the userPassword
    public String getUserPassword() {
        return userPassword;
    }

    //Setter method to set the userPassword
    public void setUserPassword(String userPassword) {
        this.userPassword = userPassword;
    }

}