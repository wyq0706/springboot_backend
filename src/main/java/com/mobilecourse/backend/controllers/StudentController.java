package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.StudentDao;
import com.mobilecourse.backend.dao.UserDao;
import com.mobilecourse.backend.model.Project;
import com.mobilecourse.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.List;

@RestController
@EnableAutoConfiguration
//设置此参数可以给这个类的所有接口都加上前缀
@RequestMapping("/api/student")
public class StudentController extends CommonController {

    @Autowired
    private StudentDao StudentMapper;

    @RequestMapping(value = "/sign_in", method = { RequestMethod.POST })
    public String go_singin(HttpServletRequest request, @RequestParam(value = "project_id")Integer project_id) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            StudentMapper.goSignin(project_id,account.getId());
            return wrapperMsg("valid","报名成功",null);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }

    @RequestMapping(value = "/sign_out", method = { RequestMethod.POST })
    public String go_signout(HttpServletRequest request, @RequestParam(value = "project_id")Integer project_id) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            StudentMapper.quitSignin(project_id,account.getId());
            return wrapperMsg("valid","成功取消报名",null);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }
}
