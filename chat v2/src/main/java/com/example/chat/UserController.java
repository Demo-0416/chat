package com.example.chat;

import com.example.chat.User;
import com.example.chat.UserMapper;
import jakarta.annotation.Resource;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;
import java.util.List;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin
@RestController
@RequestMapping("/user")
public class UserController {

  @Resource
  UserMapper userMapper;
  String getRegisterCode="111111";

  @GetMapping
  public List<com.example.chat.User> getUser() {
    return userMapper.findAll();
  }

  @PostMapping
  public String addUser(@RequestBody com.example.chat.User user) {
    userMapper.save(user);
    return "成功";
  }

  @PutMapping
  public String updateUser(@RequestBody com.example.chat.User user) {
    userMapper.updateById(user);
    return "成功";
  }

  @DeleteMapping("/{userId}")
  public String deleteUser(@PathVariable("userId") Long id) {
    userMapper.deleteById(id);
    return "成功";
  }
  @GetMapping("/signup/{email}")
  public String UserSignUp(HttpServletRequest request) {
    GetCodeNumber number = new GetCodeNumber();
    String email = request.getParameter("email");

    HttpSession session = request.getSession();
    String sessionCode = (String) session.getAttribute("code");
    User user = userMapper.findByEmail(email);

    if(user == null) {
      getRegisterCode = number.GetNumber(email);
      return "验证码已发送";
    } else {
      return "邮箱已注册";
    }
  }

  @PutMapping("/register")
  public String UserRegister(HttpServletRequest request) {
    String code = request.getParameter("code");
    String email = request.getParameter("email");
    String password = request.getParameter("password");
    User user = new User();
    user.setUserEmail(email);
    user.setUserPassword(password);
    if(getRegisterCode.equals(code)) {
      user.setUserName(user.getUserEmail());
      userMapper.save(user);
      return "注册成功";
    } else {
      return "验证码错误";
    }
  }

  @PostMapping ("/login")
  public Result check(@RequestBody User user){
    User user1=new User();
    System.out.println(user1.getUserName());
    if(user.getUserEmail() == null){
      return Result.failed("请输入手机号码");
    }else {
      user1=userMapper.findNumber(user.getUserEmail());
      if(user1 == null){
        return Result.failed("用户不存在");
      }else {
        user1=userMapper.findPassword(user.getUserEmail(), user.getUserPassword());
        if(user1 == null){
          return Result.failed("密码错误");
        }else {
          return Result.success("登录成功");
        }
      }
    }
  }
}
