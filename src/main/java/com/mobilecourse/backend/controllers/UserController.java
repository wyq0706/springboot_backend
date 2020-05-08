package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.WebSocketServer;
import com.mobilecourse.backend.dao.StudentDao;
import com.mobilecourse.backend.model.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.List;

@RestController
@EnableAutoConfiguration
//设置此参数可以给这个类的所有接口都加上前缀
@RequestMapping("/api/user")
public class UserController extends CommonController {

    @Autowired
    private StudentDao StudentMapper;

    @RequestMapping(value = "/register", method = { RequestMethod.POST })
    public String insert(@RequestParam(value = "username")String username,
                         @RequestParam(value = "password")String password,
                         @RequestParam(value = "type") boolean type) {
        if(type==false){
            Student s = new Student();
            s.setUsername(username);
            s.setPassword(password);
            StudentMapper.insert(s);
        }
        return wrapperMsg(200, "success");
    }
}
