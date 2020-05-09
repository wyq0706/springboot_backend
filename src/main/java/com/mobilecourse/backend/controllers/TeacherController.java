package com.mobilecourse.backend.controllers;

import com.mobilecourse.backend.dao.TeacherDao;
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
@RequestMapping("/api/teacher")
public class TeacherController extends CommonController {

    @Autowired
    private TeacherDao TeacherMapper;

    @RequestMapping(value = "/upload_recruit", method = { RequestMethod.POST })
    public String upload(HttpServletRequest request,@RequestParam(value = "title")String title,
                         @RequestParam(value = "research_direction",defaultValue="")String research_direction,
                         @RequestParam(value = "requirement",defaultValue="") String requirement,
                         @RequestParam(value = "description",defaultValue="") String description) {
            HttpSession session=request.getSession();
            User account=(User)session.getAttribute("sid");
            if(account==null) {//如果不为空
                return wrapperMsg("invalid","该账号未登录");
            }
            if(!account.isType()){
                return wrapperMsg("invalid","该账号不是老师");
            }
            if(title==null){
                return wrapperMsg("invalid","标题不能为空");
            }
            Project s = new Project();
            s.setTitle(title);
            s.setResearch_direction(research_direction);
            s.setRequirement(requirement);
            s.setDescription(description);
            s.setTeacher_id(account.getId());
            TeacherMapper.uploadProject(s);
            return wrapperMsg("valid","成功创建");
    }
}
