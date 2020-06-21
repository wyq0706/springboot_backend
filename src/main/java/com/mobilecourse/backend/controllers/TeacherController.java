package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.TeacherDao;
import com.mobilecourse.backend.model.Project;
import com.mobilecourse.backend.model.User;
import com.mobilecourse.backend.nosql.elasticsearch.document.EsProduct;
import com.mobilecourse.backend.service.EsProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

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

    @Autowired
    private EsProductService esService;

    @RequestMapping(value = "/upload_recruit", method = { RequestMethod.POST })
    public String upload(HttpServletRequest request,@RequestParam(value = "title")String title,
                         @RequestParam(value = "research_direction",defaultValue="")String research_direction,
                         @RequestParam(value = "requirement",defaultValue="") String requirement,
                         @RequestParam(value = "description",defaultValue="") String description) {
            HttpSession session=request.getSession();
            User account=(User)session.getAttribute("sid");
            if(account==null) {//如果不为空
                return wrapperMsg("invalid","该账号未登录",null);
            }
            if(!account.isType()){
                return wrapperMsg("invalid","该账号不是老师",null);
            }
            if(title==null){
                return wrapperMsg("invalid","标题不能为空",null);
            }
            // mysql storage
            Project s = new Project();
            s.setTitle(title);
            s.setResearch_direction(research_direction);
            s.setRequirement(requirement);
            s.setDescription(description);
            s.setTeacher_id(account.getId());
            TeacherMapper.uploadProject(s);

            // elasticsearch storage
            EsProduct esp=new EsProduct();
            //防止不同类型的相同id碰撞
            esp.setId(s.getId()*4);
            esp.setDepartment(account.getDepartment());
            esp.setItem_id(s.getId());
            esp.setUser_id(s.getTeacher_id());
            esp.setType("project");
            esp.setKeywords(account.getUsername());
            esp.setReal_name(account.getReal_name());
            esp.setName(s.getTitle());
            esp.setSubTitle(s.getDescription());
            // 存储文档到es中
            esService.create(esp);

            JSONObject wrapperMsg = new JSONObject();
            wrapperMsg.put("project_id", s.getId());
            return wrapperMsg("valid","成功创建",wrapperMsg);
    }

    @RequestMapping(value = "/update_recruit", method = { RequestMethod.POST })
    public String update_recruit(HttpServletRequest request,@RequestParam(value="id")Integer id,
                                 @RequestParam(value = "title",defaultValue="")String title,
                         @RequestParam(value = "research_direction",defaultValue="")String research_direction,
                         @RequestParam(value = "requirement",defaultValue="") String requirement,
                         @RequestParam(value = "description",defaultValue="") String description) {
        HttpSession session=request.getSession();
        User account=(User)session.getAttribute("sid");
        if(account==null) {//如果不为空
            return wrapperMsg("invalid","该账号未登录",null);
        }
        if(!account.isType()){
            return wrapperMsg("invalid","该账号不是老师",null);
        }
        if(id==null){
            return wrapperMsg("invalid","项目id不能为空",null);
        }
        Project s = new Project();
        s.setId(id);
        s.setTitle(title);
        s.setResearch_direction(research_direction);
        s.setRequirement(requirement);
        s.setDescription(description);
        s.setTeacher_id(account.getId());
        TeacherMapper.updateProject(s);

        // update elasticsearch storage
        EsProduct esp=esService.get(id * 4);
        if(!title.equals("")) {
            esp.setName(title);
        }
        if(!description.equals("")) {
            esp.setSubTitle(description);
        }
        esService.create(esp);

        return wrapperMsg("valid","成功修改",null);
    }

    @RequestMapping(value = "/cancel_recruit", method = { RequestMethod.POST })
    public String cancel_recruit(HttpServletRequest request,@RequestParam(value="id")Integer id) {
        HttpSession session=request.getSession();
        User account=(User)session.getAttribute("sid");
        if(account==null) {//如果不为空
            return wrapperMsg("invalid","该账号未登录",null);
        }
        if(!account.isType()){
            return wrapperMsg("invalid","该账号不是老师",null);
        }
        if(id==null){
            return wrapperMsg("invalid","项目id不能为空",null);
        }
        TeacherMapper.cancelProject(id);

        // delete from elasticsearch storage
        esService.delete(id*4);
        return wrapperMsg("valid", "成功删除", null);
    }

    @RequestMapping(value = "/get_signin_student/{id}", method = { RequestMethod.GET })
    public String get_signin_student(HttpServletRequest request,@PathVariable(value="id")Integer id) {
        HttpSession session=request.getSession();
        User account=(User)session.getAttribute("sid");
        if(account==null) {//如果不为空
            return wrapperMsg("invalid","该账号未登录",null);
        }
        if(!account.isType()){
            return wrapperMsg("invalid","该账号不是老师",null);
        }
        if(id==null){
            return wrapperMsg("invalid","项目id不能为空",null);
        }
        TeacherMapper.getSignin(id);
        List<User> list=TeacherMapper.getSignin(id);
        JSONArray jsonArray = new JSONArray();
        for (User s : list) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("type",s.isType());
            jsonObject.put("id", s.getId());
            jsonObject.put("name", s.getUsername());
            jsonObject.put("real_name",s.getReal_name());
            jsonObject.put("school",s.getSchool());
            jsonObject.put("grade",s.getGrade());
            jsonArray.add(jsonObject);
        }
        return wrapperMsgArray("valid", "", jsonArray);
    }

    @RequestMapping(value = "/get_my_project", method = { RequestMethod.GET })
    public String get_my_project(HttpServletRequest request) {
        User account = getUserFromSession(request);
        if (account != null) {//如果不为空
            JSONArray jsonArray = new JSONArray();
            List<Project> list = TeacherMapper.getProById(account.getId());
            for (Project project : list) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", project.getId());
                jsonObject.put("title", project.getTitle());
                jsonObject.put("description", project.getDescription());
                jsonObject.put("department", account.getDepartment());
                String name = account.getReal_name();
                if (name != null && name.length() > 0) {
                    jsonObject.put("teacher", account.getReal_name());
                } else {
                    jsonObject.put("teacher", account.getUsername());
                }
                jsonArray.add(jsonObject);
            }

            return wrapperMsgArray("valid", "", jsonArray);
        } else {
            return wrapperMsg("invalid", "未登录", null);
        }
    }
}
