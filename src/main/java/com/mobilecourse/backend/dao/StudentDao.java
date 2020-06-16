package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Plan;
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

    void uploadPlan(Plan s);

    void updatePlan(Plan s);

    void cancelPlan(int id);

    void goStar(@Param("project_id") Integer i, @Param("student_id") Integer j);

    void quitStar(@Param("project_id") Integer i, @Param("student_id") Integer j);

    List<Project> getMyPro(@Param("id")Integer i);

    List<Plan> getMyPlan(@Param("id")Integer i);

    List<User> getUserById(@Param("id")Integer id);

}
