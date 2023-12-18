package com.example.chat;


import static com.example.chat.SemanticSimilarity.semanticSimilarityDirectInDatabase;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ExecutionException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.w3c.dom.Text;


@RestController
@CrossOrigin()
@RequestMapping("/user")
public class UserController {


  @Resource
  UserMapper userMapper;
  String getRegisterCode;

  public UserController() throws ExecutionException, InterruptedException {
  }


  @GetMapping
  public List<com.example.chat.User> getUser() {
    return userMapper.findAll();
  }

  @PostMapping("/manageadduser")
  public String addUser(@RequestBody com.example.chat.User user) {
    userMapper.save(user);
    return "成功";
  }

  @DeleteMapping("/{email}")
  public String deleteUser(@PathVariable("email")String email) {
    userMapper.deleteById(email);
    return "成功";
  }

  private static final String URL = "jdbc:mysql://localhost:3306/emailmanagement";
  private static final String USER = "root";
  private static final String PASSWORD = "wj20031012";
  public static Connection SetConnection() throws SQLException {
    return  DriverManager.getConnection(URL, USER, PASSWORD);
  }
  @GetMapping("/signup")
  public String UserSignUp(HttpServletRequest request){
    GetCodeNumber number = new GetCodeNumber();
    String email = request.getParameter("email");
    User user = userMapper.findByEmail(email);

    if(user == null) {

      String registerCode = GetCodeNumber.GetNumber(email);

      String email1Register = userMapper.findRegisterByEmail(email);
      if(email1Register == null){
        userMapper.addRegister(email, registerCode);
      } else {
        userMapper.updateRegister(email, registerCode);
      }
      return registerCode;
    } else {
      return "邮箱已注册";
    }
  }

  @GetMapping("/findpassword")
  public String UserFindPassword(HttpServletRequest request){
    GetCodeNumber number = new GetCodeNumber();
    String email = request.getParameter("email");
    User user = userMapper.findByEmail(email);

    if(user != null) {

      String registerCode = number.GetNumber(email);

      String email1Register = userMapper.findRegisterByEmail(email);
      if(email1Register == null){
        userMapper.addRegister(email, registerCode);
      } else {
        userMapper.updateRegister(email, registerCode);
      }
      return registerCode;
    } else {
      return "邮箱未注册";
    }
  }

  @GetMapping ("/check")
  public String CheckCode(HttpServletRequest request) {
    String userInputCode = request.getParameter("code");
    String email = request.getHeader("email");
    String registerCode = userMapper.findRegisterCodeByEmail(email);
    System.out.println(email+registerCode);
    if (userInputCode.equals(registerCode)){
      return "验证码正确";
    } else {
      return "验证码错误";
    }
  }

  @GetMapping ("/register")
  public String UserRegister(HttpServletRequest request) {
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    User user = new User();
    user.setUserEmail(email);
    user.setUserPassword(password);
    user.setUserName(user.getUserEmail());
    userMapper.save(user);
    return "添加用户成功";

  }

  @GetMapping("/changepassword")
  public String userChangePassword(HttpServletRequest request) {
    String email = request.getParameter("email");
    String oldpassword = request.getParameter("oldpassword");
    String newpassword = request.getParameter("newpassword");
    if (oldpassword.equals(userMapper.findByEmail(email).getUserPassword())) {
      userMapper.changePassword(email, newpassword);
      return "修改成功";
    } else {
      return "密码错误";
    }
  }

  @GetMapping("/resetpassword")
  public String UserResetPassword(HttpServletRequest request) {
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    userMapper.changePassword(email, password);
    return "修改密码成功";
  }





  @GetMapping ("/login")
  public String userLogin(HttpServletRequest request){
    String email=request.getParameter("email");
    String password=request.getParameter("password");
    String cookie = request.getParameter("cookie");
    User user=new User();
    user.setUserEmail(email);
    user.setUserPassword(password);
    User user1;
    if(user.getUserEmail() == null){
      return "请输入邮箱号码";
    }else {
      user1=userMapper.findByEmail(user.getUserEmail());
      if(user1 == null){
        return "用户不存在";
      }else {
        if(user1.getUserPassword().equals(password)){
          if(userMapper.findLogInUser(cookie) == null){
            userMapper.addLogIn(email, cookie);
          } else {
            userMapper.updateLogInUser(email, cookie);
          }

          return "登录成功";
        } else {
          return "密码错误";
        }
      }
    }
  }

  @GetMapping("/deletelogged")
  public String deleteLogIn(HttpServletRequest request){
    String cookie = request.getParameter("cookie");
    userMapper.deleteLoggedByEmail(cookie);
    return "退出成功";
  }

  //对话功能
  @PostMapping("/savedialogue")
  public void saveDialogue(@RequestBody String dialogue, HttpServletRequest request){
    ChatWithGPT chat = new ChatWithGPT();
    String cookie = request.getParameter("session-id");
    int dialogueId = Integer.parseInt(request.getParameter("dialogueid"));
    LocalDateTime time = LocalDateTime.now();
    if(dialogueId == 0) {
      if(userMapper.findLogInUser(cookie) == null){}
      else {
        String email = userMapper.findLogInUser(cookie);
        userMapper.addDialogue(dialogue, email, time);
      }
    } else {
      if(userMapper.findLogInUser(cookie) == null){}
      else {
        String email = userMapper.findLogInUser(cookie);
        userMapper.updateDialogue(dialogue, email, time, dialogueId);

      }
    }


  }

  @PostMapping("/showdialogues")
  public List<String> showDialogues(HttpServletRequest request){
    String cookie = request.getHeader("session-id");
    if(userMapper.findLogInUser(cookie) == null){
      return Collections.emptyList();
    }
    else {
      String email = userMapper.findLogInUser(cookie);
      List<String> list= userMapper.getDialogueTime(email);
      for (int i = 0; i < list.size(); i++) {
        for (int j = i + 1; j < list.size(); j++) {
          if (list.get(i).equals(list.get(j))) {
            list.remove(j);
            j--;
          }
        }
      }
      System.out.println(list);
      return list;
    }
  }

  @GetMapping("/showdialogue")
  public String showDialogue(HttpServletRequest request){
    String cookie = request.getParameter("session-id");
    String time = request.getParameter("time");
    if(userMapper.findLogInUser(cookie) == null){
      return "没有找到";
    } else {
      String email = userMapper.findLogInUser(cookie);
      String list = userMapper.findDialogue(email, time);
      System.out.println(list);
      return list;
    }
  }

  @GetMapping("/deletedialogue")
  public void deleteDialogue(HttpServletRequest request){
    String cookie = request.getParameter("session-id");
    String time = request.getParameter("time");
    System.out.println(cookie+time);
    if (userMapper.findLogInUser(cookie) == null){
    } else {
      String email = userMapper.findLogInUser(cookie);
      userMapper.deleteDialogue(email, time);
    }
  }

  @GetMapping("/findlaws")
  public List<Content> findLaws(HttpServletRequest request){
    String cookie = request.getParameter("session-id");
    String keyWord = request.getParameter("keyword");
    List<Content> result = new ArrayList<>();
    if (userMapper.findLogInUser(cookie) == null){
      return Collections.emptyList();
    } else {
      result.addAll(userMapper.findLaws("%"+keyWord+"%"));
      return result;
    }
  }


  @PostMapping("/longsearch")
  public List<Content> adsadf(@RequestBody String userInput)
      throws ExecutionException, InterruptedException {
    List<Content> result = new ArrayList<>();
    List<String> list = semanticSimilarityDirectInDatabase(userInput);
    for (String str : list) {
      String[] strings= str.split(":");
      String str1 = strings[1].substring(0, strings[1].length()-2);
      result.add(userMapper.findLawByContent(str1));
    }
    return result;
  }

  @GetMapping("/getname")
  public String findUserName(HttpServletRequest request) {
    String cookie = request.getParameter("session-id");
    if(userMapper.findLogInUser(cookie) == null) {
      return null;
    } else {
      String email = userMapper.findLogInUser(cookie);
      return userMapper.findLogInUserName(email);
    }

  }

  @GetMapping("/setname")
  public void setUserName(HttpServletRequest request) {
    String cookie = request.getParameter("seesion-id");
    String newName = request.getParameter("newname");
    if(userMapper.findLogInUser(cookie) == null) {}
    else {
      userMapper.setUserName(newName, userMapper.findLogInUser(cookie));
    }
  }

  @GetMapping("/adduser")
  public String addUser(HttpServletRequest request){
    String email=request.getParameter("email");
    String password=request.getParameter("password");
    String name=request.getParameter("name");
    User user=new User();
    user.setUserEmail(email);
    user.setUserPassword(password);
    user.setUserName(name);
    if((userMapper.findByEmail(user.getUserEmail()))!=null){
      return "用户已存在";
    }else {
      userMapper.save(user);
      return "添加成功";
    }
  }

  @GetMapping("/deleteuser")
  public String deleteUser(HttpServletRequest request){
    String email=request.getParameter("email");
    User user=new User();
    user.setUserEmail(email);
    if((userMapper.findByEmail(user.getUserEmail()))==null){
      return "用户不存在";
    }else {
      userMapper.deleteById(user.getUserEmail());
      return "删除成功";
    }
  }

  @GetMapping("updateuser")
  public String updateUser(HttpServletRequest request){
    String email=request.getParameter("email");
    String password=request.getParameter("password");
    String name=request.getParameter("name");
    User user=new User();
    user.setUserEmail(email);
    user.setUserPassword(password);
    user.setUserName(name);
    if((userMapper.findByEmail(user.getUserEmail()))==null){
      return "用户不存在";
    }else {
      userMapper.updateUser(user);
      return "修改成功";
    }
  }
  @GetMapping("finduser")
  public Object findUser(HttpServletRequest request){
    String email=request.getParameter("email");
    String name=request.getParameter("name");
    User user=new User();
    user.setUserEmail(email);
    if((userMapper.findByEmail(user.getUserEmail()))==null){
      return "用户不存在";
    }else {
      User user1=new User();
      user1=userMapper.findByEmail(user.getUserEmail());
      return user1;
    }
  }

  @GetMapping("/addlaw")
  public String addLaw(HttpServletRequest request){
    String name=request.getParameter("name");
    String content= request.getParameter("content");
    String explain=request.getParameter("explain");
    Law law=new Law();
    law.setContent(content);
    law.setExplain(explain);
    law.setName(name);
    if((userMapper.findLawByContent(content))!=null){
      return "法律已存在";
    }else {
      userMapper.addLaw(law);
      return "已成功添加";
    }
  }

  @GetMapping("/deletelaw")
  public String deleteLaw(HttpServletRequest request){
    String name=request.getParameter("name");
    String content= request.getParameter("content");
    String explain=request.getParameter("explain");
    Law law=new Law();
    law.setContent(content);
    law.setExplain(explain);
    law.setName(name);
    if((userMapper.findLawByContent(content))==null){
      return "法律不存在";
    }else {
      userMapper.deleteLaw(law.getContent());
      return "已成功添加";
    }
  }

  @GetMapping("/updatelaw")
  public String updateLaw(HttpServletRequest request){
    String name=request.getParameter("name");
    String content= request.getParameter("content");
    String explain=request.getParameter("explain");
    Law law=new Law();
    law.setContent(content);
    law.setExplain(explain);
    law.setName(name);
    if(userMapper.findLawByContent(law.getContent())==null){
      return "法律不存在";
    }else {
      userMapper.updateLaw(law.getExplain());
      return "已修改";
    }
  }

  @GetMapping("/findlaw")
  public Object findLaw(HttpServletRequest request){
    String name=request.getParameter("name");
    String content= request.getParameter("content");
    String explain=request.getParameter("explain");
    Law law=new Law();
    law.setContent(content);
    law.setExplain(explain);
    law.setName(name);
    if(userMapper.findLawByContent(law.getContent())==null){
      return "法律不存在";
    }else {
      List<Content> law1=new ArrayList<>();
      law1=userMapper.findLaws(law.getExplain());
      return law1;
    }

  }







}
