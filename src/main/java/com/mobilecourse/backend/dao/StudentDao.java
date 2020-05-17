package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Project;
import com.mobilecourse.backend.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface StudentDao {
    // 函数的名称要和对应的Mapper文件中的id完全对应

    void goSignin(@Param("project_id") Integer i, @Param("student_id") Integer j);

    void quitSignin(@Param("project_id") Integer i, @Param("student_id") Integer j);

}
