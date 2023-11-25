package com.example.chat;

import com.example.chat.User;
import com.example.chat.UserMapper;
import jakarta.annotation.Resource;
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

  @GetMapping("/{userPhone}")
  public User findUser(@PathVariable("userPhone") String phone) {
    return userMapper.findByPhone(phone);
  }

  @GetMapping("/test")
  public String test(){
    return "1111111";
  }
  @PostMapping ("/login")
  public Result check(@RequestBody User user){
    User user1=new User();
    System.out.println(user1.getUserName());
    if(user.getUserPhone() == null){
      return Result.failed("请输入手机号码");
    }else {
      user1=userMapper.findNumber(user.getUserPhone());
      if(user1 == null){
        return Result.failed("用户不存在");
      }else {
        user1=userMapper.findPassword(user.getUserPhone(), user.getUserPassword());
        if(user1 == null){
          return Result.failed("密码错误");
        }else {
          return Result.success("登录成功");
        }
      }
    }
  }
}
