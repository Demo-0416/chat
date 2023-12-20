package com.example.chat;

public class Result {
    //Declare a private String variable called code
    private String code;
    //Declare a private String variable called msg
    private String msg;
    //Declare a private Object variable called data
    private Object data;

    //Declare a public method called getMsg that returns a String
    public String getMsg() {
        //Return the value of the msg variable
        return msg;
    }

    //Declare a public method called setMsg that takes a String parameter
    public void setMsg(String msg) {
        //Set the value of the msg variable to the value of the parameter
        this.msg = msg;
    }

    //Declare a public method called getData that returns an Object
    public Object getData() {
        //Return the value of the data variable
        return data;
    }

    //Declare a public method called setData that takes an Object parameter
    public void setData(Object data) {
        //Set the value of the data variable to the value of the parameter
        this.data = data;
    }

    //Declare a public method called getCode that returns a String
    public String getCode() {
        //Return the value of the code variable
        return code;
    }

    //Declare a public method called setCode that takes a String parameter
    public void setCode(String code) {
        //Set the value of the code variable to the value of the parameter
        this.code = code;
    }

    //Declare a public static method called success that returns a Result object
    public static Result success() {
        //Create a new Result object
        Result result = new Result();
        //Set the value of the code variable to "success"
        result.setCode("success");
        //Return the new Result object
        return result;
    }

    //Declare a public static method called failed that returns a Result object
    public static Result failed() {
        //Create a new Result object
        Result result = new Result();
        //Set the value of the code variable to "failed"
        result.setCode("failed");
        //Return the new Result object
        return result;
    }

    //Declare a public static method called success that takes a String parameter and returns a Result object
    public static Result success(String msg) {
        //Create a new Result object
        Result result = new Result();
        //Set the value of the code variable to "success"
        result.setCode("success");
        //Set the value of the msg variable to the value of the parameter
        result.setMsg(msg);
        //Return the new Result object
        return result;
    }

    //Declare a public static method called failed that takes a String parameter and returns a Result object
    public static Result failed(String msg) {
        //Create a new Result object
        Result result = new Result();
        //Set the value of the code variable to "failed"
        result.setCode("failed");
        //Set the value of the msg variable to the value of the parameter
        result.setMsg(msg);
        //Return the new Result object
        return result;
    }

}