package com.mobilecourse.backend.controllers;

import com.mobilecourse.backend.dao.UserDao;
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
                return wrapperMsg("invalid","用户名已被注册");
            }
            User s = new User();
            s.setUsername(username);
            s.setPassword(password);
            s.setType(type);
            UserMapper.insert(s);
            return wrapperMsg("valid","成功注册");
    }

    @RequestMapping(value = "/login", method = { RequestMethod.POST })
    public String login(HttpServletRequest request, @RequestParam(value = "username")String username,
                        @RequestParam(value = "password")String password) {
            if(UserMapper.ifUserExists(username,password)==0){
                return wrapperMsg("invalid","用户名/密码错误");
            }
            HttpSession session=request.getSession();
            Object account=session.getAttribute("sid");
            if(account!=null) {//如果不为空
                return wrapperMsg("invalid","已登录");
            }else {
                //加入session
                List<User> s = UserMapper.getUser(username, password);
                putInfoToSession(request, "sid", s.get(0));
            }
        return wrapperMsg("valid","成功登录");
    }

    @RequestMapping(value = "/logout", method = { RequestMethod.POST })
    public String logout(HttpServletRequest request) {
            HttpSession session=request.getSession();
            Object account=session.getAttribute("sid");
            if(account==null) {//如果不为空
                return wrapperMsg("invalid","该账号未登录");
            }else {
                //删除session
                removeInfoFromSession(request,"sid");
            }
            return wrapperMsg("valid","成功登出");
    }

    @RequestMapping(value = "/update_signature", method = { RequestMethod.POST })
    public String update_signature(HttpServletRequest request, @RequestParam(value = "signature")String signature) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            String username = account.getUsername();
            UserMapper.updateSignature(username,signature);
            return wrapperMsg("valid","成功更新");
        }else {
            return wrapperMsg("invalid","未登录");
        }
    }

    @RequestMapping(value = "/update_username", method = { RequestMethod.POST })
    public String update_username(HttpServletRequest request, @RequestParam(value = "username")String newName) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            String username = account.getUsername();
            System.out.println(username);
            if(UserMapper.ifUsernameDuplicate(newName)>0){
                return wrapperMsg("invalid","该用户名已存在");
            }
            UserMapper.updateUsername(username,newName);
            removeInfoFromSession(request,"sid");
            account.setUsername(newName);
            putInfoToSession(request, "sid", account);
            return wrapperMsg("valid","成功更新");
        }else {
            return wrapperMsg("invalid","未登录");
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
            return wrapperMsg("valid","成功更新");
        }else {
            return wrapperMsg("invalid","未登录");
        }
    }

    @RequestMapping(value = "/update_personal_info", method = { RequestMethod.POST })
    public String update_personal_info(HttpServletRequest request, @RequestParam(value = "personal_info")String personal_info) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            String username = account.getUsername();
            System.out.println(username);
            UserMapper.updatePersonalInfo(username,personal_info);
            return wrapperMsg("valid","成功更新");
        }else {
            return wrapperMsg("invalid","未登录");
        }
    }


}
