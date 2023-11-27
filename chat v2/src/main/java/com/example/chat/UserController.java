package com.example.chat;

import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import javax.mail.Session;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@CrossOrigin
@RequestMapping("/user")
public class UserController {


  @Resource
  UserMapper userMapper;
  String getRegisterCode;


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

      String registerCode = number.GetNumber(email);

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
    System.out.println(email);
    String password = request.getParameter("password");
    User user = new User();
    user.setUserEmail(email);
    user.setUserPassword(password);
    user.setUserName(user.getUserEmail());
    System.out.println(user.getUserEmail());
    userMapper.save(user);
    return "添加用户成功";

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

}
