package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.WebSocketServer;
import com.mobilecourse.backend.dao.StudentDao;
import com.mobilecourse.backend.model.Student;
import com.mobilecourse.backend.dao.TeacherDao;
import com.mobilecourse.backend.model.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
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
    @Autowired
    private TeacherDao TeacherMapper;

    @RequestMapping(value = "/register", method = { RequestMethod.POST })
    public String register(@RequestParam(value = "username")String username,
                         @RequestParam(value = "password")String password,
                         @RequestParam(value = "type") boolean type) {
        if(type==false){
            if(StudentMapper.ifUsernameDuplicate(username)>0){
                return wrapperMsg("invalid","用户名已被注册");
            }
            Student s = new Student();
            s.setUsername(username);
            s.setPassword(password);
            StudentMapper.insert(s);
        }else{
            if(TeacherMapper.ifUsernameDuplicate(username)>0){
                return wrapperMsg("invalid","用户名已被注册");
            }
            Teacher s = new Teacher();
            s.setUsername(username);
            s.setPassword(password);
            TeacherMapper.insert(s);
        }
        return wrapperMsg("valid","成功注册");
    }

    @RequestMapping(value = "/login", method = { RequestMethod.POST })
    public String login(HttpServletRequest request, @RequestParam(value = "username")String username,
                        @RequestParam(value = "password")String password,
                        @RequestParam(value = "type") boolean type) {
        if(type==false){
            if(StudentMapper.ifUserExists(username,password)==0){
                return wrapperMsg("invalid","用户名/密码错误");
            }
            HttpSession session=request.getSession();
            Object account=session.getAttribute("sid");
            if(account!=null) {//如果不为空
                return wrapperMsg("invalid","已登录");
            }else {
                //加入session
                List<Student> s = StudentMapper.getUser(username, password);
                putInfoToSession(request, "sid", s.get(0));
            }
        }else{
            if(TeacherMapper.ifUserExists(username,password)==0){
                return wrapperMsg("invalid","用户名/密码错误");
            }
            Object account=request.getAttribute("tid");
            if(account!=null) {//如果不为空
                return wrapperMsg("invalid","已登录");
            }else {
                //加入session
                List<Student> s = StudentMapper.getUser(username, password);
                putInfoToSession(request, "tid", s.get(0));
            }
        }
        return wrapperMsg("valid","成功登录");
    }

    @RequestMapping(value = "/logout", method = { RequestMethod.POST })
    public String logout(HttpServletRequest request,@RequestParam(value = "type") boolean type) {
        if(type==false){
            HttpSession session=request.getSession();
            Object account=session.getAttribute("sid");
            if(account==null) {//如果不为空
                return wrapperMsg("invalid","该账号未登录");
            }else {
                //删除session
                removeInfoFromSession(request,"sid");
            }
        }else{
            Object account=request.getAttribute("tid");
            if(account==null) {//如果不为空
                return wrapperMsg("invalid","该账号未登录");
            }else {
                //删除session
                removeInfoFromSession(request,"sid");
            }
        }
        return wrapperMsg("valid","成功登出");
    }
}
