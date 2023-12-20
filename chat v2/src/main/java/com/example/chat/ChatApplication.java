package com.example.chat;

//import statements for the necessary packages

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

//import statement for the package containing the mapper scan
import org.mybatis.spring.annotation.MapperScan;

//annotation for the SpringBootApplication
@SpringBootApplication
//annotation for the mapper scan
@MapperScan("com.example.chat")
public class ChatApplication {

    //main method to run the application
    public static void main(String[] args) {
        //call the ModelLoader class to load the model
        ModelLoader.loadModel();
        //call the SpringApplication class to run the application
        SpringApplication.run(ChatApplication.class, args);
    }

}