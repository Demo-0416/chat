package com.example.chat;

import com.example.chat.User;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

public interface UserMapper {

  //用户表
  @Select("select * from user")
  List<com.example.chat.User> findAll();

  @Update("INSERT INTO `user` (`username`, `useremail`, `userpassword`) VALUES (#{userName}, #{userEmail}, #{userPassword});")
  @Transactional
  void save(com.example.chat.User user);

  @Update("update user set userpassword = #{password} where useremail = #{email}")
  @Transactional
  void changePassword(String email, String password);

  @Delete("DELETE from user where useremail = #{email}")
  void deleteById(String email);

  @Select("SELECT * from user where useremail = #{email}")
  User findByEmail(String email);

  @Select("select * from login where number=#{number}")
  User findNumber(@Param("number") String number);

  @Select("select * from login where number = #{number} and password = #{password}")
  User findPassword(@Param("number") String number,@Param("password") String password);

  //验证码表
  @Select("SELECT email from register where email = #{email}")
  String findRegisterByEmail(String email);

  @Update("update register set code = #{registerCode} where email = #{email}")
  @Transactional
  void updateRegister(String email, String registerCode);

  @Update("INSERT INTO `register` (`email`, `code`) VALUES (#{email}, #{registerCode});")
  @Transactional
  void addRegister(String email, String registerCode);

  @Select("SELECT code from register where email = #{email}")
  String findRegisterCodeByEmail(String email);

  //登录状态表
  @Update("INSERT INTO `logged` (`email`, `cookie`) VALUES (#{email}, #{cookie});")
  @Transactional
  void addLogIn(String email, String cookie);

  @Select("SELECT email from logged where cookie = #{cookie}")
  String findLogInUser(String cookie);

  @Delete("DELETE from logged where cookie = #{cookie}")
  void deleteLoggedByEmail(String cookie);

  @Update("update logged set email = #{email} where cookie = #{cookie};")
  @Transactional
  void updateLogInUser(String email, String cookie);

  //对话内容表
  @Update("INSERT INTO `dialogue` (`dialogue`, `email`, `time`) VALUES (#{dialogue}, #{email}, #{time});")
  @Transactional
  void addDialogue(String dialogue, String email, LocalDateTime time);

  @Select("SELECT `time` from dialogue where email = #{email}")
  List<String> getDialogueTime(String email);

  @Select("SELECT `dialogue` from dialogue where email = #{email} and `time` = #{time}")
  String findDialogue(String email, String time);

  @Delete("DELETE from dialogue where email = #{email} and `time` = #{time}")
  void deleteDialogue(String email, String time);

  //法律表
  @Select("SELECT `lawName`, `lno`, `lawContent` from content where lawExplain like #{str}")
  List<Content> findLaws(String str);

  @Select("SELECT `lawName`, `lno`, `lawContent` from content where lawContent = #{str}")
  Content findLawByContent(String str);

  @Update("UPDATE login SET `username`=#{userName} , `useremail=#{userEmail} , `userpassword=#{userPassword} WHERE `useremail=#{userEmail}")
  void updateUser(User user);

  @Update("INSERT INTO `content` (`name`, `content`, `explain`) VALUES (#{name}, #{content}, #{explain});")
  @Transactional
  void addLaw(com.example.chat.Law law);

  @Delete("DELETE from content where content = #{content}")
  void deleteLaw(String content);


}
