package com.mobilecourse.backend.dao;

import com.mobilecourse.backend.model.Chat;
import com.mobilecourse.backend.model.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChatDao {
    // 函数的名称要和对应的Mapper文件中的id完全对应

    void insertMessage(Chat c);

    List<Chat> getMessage(@Param("from_id") Integer i, @Param("to_id") Integer j);

    void deleteMessage(Chat c);

    List<User> getChatter(@Param("id") Integer i);

    Chat getLatestChat(@Param("from_id") Integer i, @Param("to_id") Integer j);

}
