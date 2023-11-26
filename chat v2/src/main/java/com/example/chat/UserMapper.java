package com.example.chat;

import com.example.chat.User;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

public interface UserMapper {
  @Select("select * from user")
  List<com.example.chat.User> findAll();

  @Update("INSERT INTO `user` (`username`, `useremail`, `userpassword`) VALUES (#{userName}, #{userEmail}, #{userPassword});")
  @Transactional
  void save(com.example.chat.User user);

  @Update("update user set userName = #{userName}, userPhone = #{userPhone}, userpassword = #{userPassword} where userId = #{userId}")
  @Transactional
  void updateById(com.example.chat.User user);

  @Delete("DELETE from user where userId = #{userId}")
  void deleteById(Long id);

  @Select("SELECT * from user where useremail = #{email}")
  User findByEmail(String email);

  @Select("select * from login where number=#{number}")
  User findNumber(@Param("number") String number);

  @Select("select * from login where number = #{number} and password = #{password}")
  User findPassword(@Param("number") String number,@Param("password") String password);


  @Select("SELECT * from logged where email = #{email}")
  String findLoggedUser(String email);

  @Select("SELECT email from register where email = #{email}")
  String findRegisterByEmail(String email);

  @Select("update register set code = #{registerCode} where email = #{email}")
  @Transactional
  void updateRegister(String email, String registerCode);

  @Select("INSERT INTO `register` (`email`, `code`) VALUES (#{email}, #{registerCode});")
  @Transactional
  void addRegister(String email, String registerCode);

  @Select("SELECT code from register where email = #{email}")
  String findRegisterCodeByEmail(String email);
}
