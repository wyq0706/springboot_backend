package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.SysInfo;
import com.mobilecourse.backend.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface SysInfoDao {
    // 函数的名称要和对应的Mapper文件中的id完全对应

    void insertMessage(SysInfo c);

    List<SysInfo> getMessage(@Param("id")Integer i);

    void deleteMessage(SysInfo c);

    void updateRead(@Param("id")Integer i);

    List<User> getChatter(@Param("id") Integer i);

    SysInfo getLatestChat(@Param("id")Integer i);

    Integer getUnreadCount(@Param("id")Integer i);

}
