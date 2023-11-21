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
}
