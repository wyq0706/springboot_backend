package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.UserDao;
import com.mobilecourse.backend.model.Project;
import com.mobilecourse.backend.model.Test;
import com.mobilecourse.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@EnableAutoConfiguration
//设置此参数可以给这个类的所有接口都加上前缀
@RequestMapping("/api/user")
public class UserController extends CommonController {

    @Autowired
    private UserDao UserMapper;

    @RequestMapping(value = "/register", method = { RequestMethod.POST })
    public String register(@RequestParam(value = "username")String username,
                         @RequestParam(value = "password")String password,
                         @RequestParam(value = "type") boolean type) {
            if(UserMapper.ifUsernameDuplicate(username)>0){
                return wrapperMsg("invalid","用户名已被注册",null);
            }
            User s = new User();
            s.setUsername(username);
            s.setPassword(password);
            s.setType(type);
            UserMapper.insert(s);
            return wrapperMsg("valid","成功注册",null);
    }

    @RequestMapping(value = "/login", method = { RequestMethod.POST })
    public String login(HttpServletRequest request, @RequestParam(value = "username")String username,
                        @RequestParam(value = "password")String password) {
            if(UserMapper.ifUserExists(username,password)==0){
                return wrapperMsg("invalid","用户名/密码错误",null);
            }
            HttpSession session=request.getSession();
            Object account=session.getAttribute("sid");
            if(account!=null) {//如果不为空
                return wrapperMsg("invalid","已登录",null);
            }else {
                //加入session
                List<User> s = UserMapper.getUser(username, password);
                putInfoToSession(request, "sid", s.get(0));
            }
        return wrapperMsg("valid","成功登录",null);
    }

    @RequestMapping(value = "/logout", method = { RequestMethod.POST })
    public String logout(HttpServletRequest request) {
            HttpSession session=request.getSession();
            Object account=session.getAttribute("sid");
            if(account==null) {//如果不为空
                return wrapperMsg("invalid","该账号未登录",null);
            }else {
                //删除session
                removeInfoFromSession(request,"sid");
            }
            return wrapperMsg("valid","成功登出",null);
    }

    @RequestMapping(value = "/update_signature", method = { RequestMethod.POST })
    public String update_signature(HttpServletRequest request, @RequestParam(value = "signature")String signature) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            String username = account.getUsername();
            UserMapper.updateSignature(username,signature);
            return wrapperMsg("valid","成功更新",null);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }

    @RequestMapping(value = "/update_username", method = { RequestMethod.POST })
    public String update_username(HttpServletRequest request, @RequestParam(value = "username")String newName) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            String username = account.getUsername();
            System.out.println(username);
            if(UserMapper.ifUsernameDuplicate(newName)>0){
                return wrapperMsg("invalid","该用户名已存在",null);
            }
            UserMapper.updateUsername(username,newName);
            removeInfoFromSession(request,"sid");
            account.setUsername(newName);
            putInfoToSession(request, "sid", account);
            return wrapperMsg("valid","成功更新",null);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }

    @RequestMapping(value = "/update_password", method = { RequestMethod.POST })
    public String update_password(HttpServletRequest request, @RequestParam(value = "password")String password) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            String username = account.getUsername();
            System.out.println(username);
            UserMapper.updatePassword(username,password);
            account.setPassword(password);
            removeInfoFromSession(request,"sid");
            putInfoToSession(request, "sid", account);
            return wrapperMsg("valid","成功更新",null);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }

    @RequestMapping(value = "/update_personal_info", method = { RequestMethod.POST })
    public String update_personal_info(HttpServletRequest request, @RequestParam(value = "personal_info")String personal_info) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            String username = account.getUsername();
            System.out.println(username);
            UserMapper.updatePersonalInfo(username,personal_info);
            return wrapperMsg("valid","成功更新",null);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }

    @RequestMapping(value = "/get_follower", method = { RequestMethod.GET })
    public String get_follow(HttpServletRequest request) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            List<User> list=UserMapper.getFollow(account.getId());
            JSONArray jsonArray = new JSONArray();
            for (User s : list) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type",s.isType());
                jsonObject.put("id", s.getId());
                jsonObject.put("name", s.getUsername());
                jsonArray.add(jsonObject);
            }
            return wrapperMsgArray("valid","",jsonArray);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }

    @RequestMapping(value = "/get_followed", method = { RequestMethod.GET })
    public String get_followed(HttpServletRequest request) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            List<User> list=UserMapper.getFollowed(account.getId());
            JSONArray jsonArray = new JSONArray();
            for (User s : list) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type",s.isType());
                jsonObject.put("id", s.getId());
                jsonObject.put("name", s.getUsername());
                jsonArray.add(jsonObject);
            }
            return wrapperMsgArray("valid","",jsonArray);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }

    @RequestMapping(value = "/projects", method = { RequestMethod.GET })
    public String projects() {
        List<Project> list=UserMapper.getProjects();
        JSONArray jsonArray = new JSONArray();
        for (Project s : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("project_id", s.getId());
            jsonObject.put("project_title", s.getTitle());
            jsonObject.put("teacher", s.getTeacher().getUsername());
            jsonObject.put("teacher_id",s.getTeacher().getId());
            if(s.getTeacher().getDepartment()==null){
                jsonObject.put("department","");
            }else {
                jsonObject.put("department", s.getTeacher().getDepartment());
            }
            jsonObject.put("requirement",s.getRequirement());
            jsonArray.add(jsonObject);
        }
        return wrapperMsgArray("valid","",jsonArray);
    }

    @RequestMapping(value = "/verification",method = {RequestMethod.POST})
    public String verification(HttpServletRequest request,
                               @RequestParam(value = "realname")String realname,
                               @RequestParam(value = "school")String school,
                               @RequestParam(value = "department")String department,
                               @RequestParam(value = "grade")String grade) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            String username = account.getUsername();
            UserMapper.verification(username,realname,school,department,grade);
            return wrapperMsg("valid","成功验证",null);
        }else {
            return wrapperMsg("invalid","未成功验证",null);
        }
    }

    @RequestMapping(value = "/home",method = {RequestMethod.GET})
    public String home(HttpServletRequest request) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            JSONObject jsonObject = new JSONObject();
            int id = account.getId();
            String username = account.getUsername();
            Boolean type = account.isType();
            String icon_url = "";
            Boolean verification = account.isVerification();
            String signature = account.getSignature();
            String person_info = account.getPersonal_info();
            String veri_info = account.getReal_name()+" "+account.getSchool()+account.getDepartment()+account.getGrade();
            Integer follow_num = UserMapper.getFollowNum(id);
            if(follow_num==null) follow_num = 0;
            Integer followee_num = UserMapper.getFollowedNum(id);
            if(followee_num==null) followee_num=0;
            Integer star_or_pro_num;
            if(type){
                star_or_pro_num = UserMapper.getProNum(id);
            }
            else{
                star_or_pro_num= UserMapper.getStarNum(id);
            }
            if(star_or_pro_num==null)star_or_pro_num=0;
            jsonObject.put("icon_url",icon_url);
            jsonObject.put("username",username);
            jsonObject.put("type",type);
            jsonObject.put("verification",verification);
            jsonObject.put("signature",signature);
            jsonObject.put("personal_info",person_info);
            jsonObject.put("veri_info",veri_info);
            jsonObject.put("follow_num",follow_num);
            jsonObject.put("followee_num",followee_num);
            jsonObject.put("star_or_pro_num",star_or_pro_num);
            return wrapperMsg("valid","",jsonObject);
        }else {
            return wrapperMsg("invalid","未成功验证",null);
        }
    }

    @RequestMapping(value = "/user_info",method = {RequestMethod.POST})
    public String userInfo(HttpServletRequest request,
                           @RequestParam(value = "username")String otheruser) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            JSONObject jsonObject = new JSONObject();
            List<User> s = UserMapper.getUserByName(otheruser);
            if(s==null){return wrapperMsg("invalid","没有该用户名的用户",null);}
            User otherUser = s.get(0);
            String username = otherUser.getUsername();
            Boolean type = otherUser.isType();
            String icon_url = "";
            Boolean verification = otherUser.isVerification();
            String signature = otherUser.getSignature();
            String person_info = otherUser.getPersonal_info();
            String veri_info = otherUser.getReal_name()+" "+otherUser.getSchool()+otherUser.getDepartment()+account.getGrade();
            jsonObject.put("icon_url",icon_url);
            jsonObject.put("username",username);
            jsonObject.put("type",type);
            jsonObject.put("verification",verification);
            jsonObject.put("signature",signature);
            jsonObject.put("personal_info",person_info);
            jsonObject.put("veri_info",veri_info);
            return wrapperMsg("valid","",jsonObject);
        }else {
            return wrapperMsg("invalid","未成功验证",null);
        }
    }

    @RequestMapping(value = "/get_star", method = { RequestMethod.GET })
    public String getStar(HttpServletRequest request) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            List<Project> list=UserMapper.getStar(account.getId());
            JSONArray jsonArray = new JSONArray();
            for (Project s : list) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",s.getId());
                jsonObject.put("title",s.getTitle());
                jsonObject.put("description",s.getDescription());
                jsonObject.put("requirement",s.getRequirement());
                jsonObject.put("teacher",s.getTeacher());
                jsonArray.add(jsonObject);
            }
            return wrapperMsgArray("valid","",jsonArray);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }



}
