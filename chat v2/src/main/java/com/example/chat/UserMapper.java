package com.example.chat;

import com.example.chat.User;
import java.util.List;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;
import org.springframework.transaction.annotation.Transactional;

public interface UserMapper {
  @Select("select * from user")
  List<com.example.chat.User> findAll();

  @Update("INSERT INTO `user` (`userId`, `userName`, `userPhone`, `userPassword`) VALUES (#{userId}, #{userName}, #{userPhone}, #{userPassword});")
  @Transactional
  void save(com.example.chat.User user);

  @Update("update user set userName = #{userName}, userPhone = #{userPhone}, userpassword = #{userPassword} where userId = #{userId}")
  @Transactional
  void updateById(com.example.chat.User user);

  @Delete("DELETE from user where userId = #{userId}")
  void deleteById(Long id);

  @Select("SELECT * from user where userPhone = #{userPhone}")
  User findByPhone(String phone);

}
