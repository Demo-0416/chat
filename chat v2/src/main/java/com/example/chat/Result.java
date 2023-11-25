package com.example.chat;

public class Result {
  private String code;
  private String msg;
  private Object data;
  public String getMsg() {
    return msg;
  }

  public void setMsg(String msg) {
    this.msg = msg;
  }

  public Object getData() {
    return data;
  }

  public void setData(Object data) {
    this.data = data;
  }

  public String getCode() {
    return code;
  }

  public void setCode(String code) {
    this.code = code;
  }

  public static Result success(){
    Result result=new Result();
    result.setCode("success");
    return result;
  }
  public static Result failed(){
    Result result=new Result();
    result.setCode("failed");
    return result;
  }
  public static Result success(String msg){
    Result result=new Result();
    result.setCode("success");
    result.setMsg(msg);
    return result;
  }
  public static Result failed(String msg){
    Result result=new Result();
    result.setCode("failed");
    result.setMsg(msg);
    return result;
  }

}
