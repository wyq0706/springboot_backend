package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Teacher;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

@Mapper
public interface TeacherDao {
    // 函数的名称要和对应的Mapper文件中的id完全对应

    // 插入，可以指定类为输入的参数
    void insert(Teacher s);

    //检查用户名是否重复
    int ifUsernameDuplicate(String s);

    //用户名密码是否存在且正确
    int ifUserExists(@Param("username")String s, @Param("password")String t);
}
