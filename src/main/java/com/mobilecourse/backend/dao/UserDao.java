package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Project;
import com.mobilecourse.backend.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserDao {
    // 函数的名称要和对应的Mapper文件中的id完全对应

    // 插入，可以指定类为输入的参数
    void insert(User s);

    //检查用户名是否重复
    int ifUsernameDuplicate(String s);

    //用户名密码是否存在且正确
    int ifUserExists(@Param("username")String s, @Param("password")String t);

    //获取用户
    List<User> getUser(@Param("username")String s, @Param("password")String t);

    void updateUsername(@Param("username")String s, String newName);

    void updatePassword(@Param("username")String s, String newPassword);

    void updateSignature(@Param("username")String s, String newSignature);

    void updatePersonalInfo(@Param("username")String s, String newPersonalInfo);

    List<Project> getProjects();

    void goFollow(@Param("followed_id")Integer i,@Param("follower_id")Integer j);

    List<User> getFollow(@Param("id")Integer i);

    Integer getFollowNum(@Param("id")Integer i);

    List<User> getFollowed(@Param("id")Integer i);

    Integer getFollowedNum(@Param("id")Integer i);

    Integer getProNum(@Param("id")Integer i);

    Integer getStarNum(@Param("id")Integer i);

    void verification(@Param("username")String s, String realname,String school,String department,String grade);
}
