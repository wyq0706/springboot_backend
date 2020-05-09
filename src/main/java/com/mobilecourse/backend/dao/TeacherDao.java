package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Project;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface TeacherDao {
    // 函数的名称要和对应的Mapper文件中的id完全对应

    // 插入，可以指定类为输入的参数
    void uploadProject(Project s);

}
