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
}
