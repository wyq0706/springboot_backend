package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.ChatDao;
import com.mobilecourse.backend.dao.StudentDao;
import com.mobilecourse.backend.dao.SysInfoDao;
import com.mobilecourse.backend.dao.UserDao;
import com.mobilecourse.backend.model.*;
import com.mobilecourse.backend.nosql.elasticsearch.document.EsProduct;
import com.mobilecourse.backend.service.EsProductService;
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

    @Autowired
    private SysInfoDao SysInfoMapper;

    @Autowired
    private EsProductService esService;

    @RequestMapping(value = "/sign_in", method = { RequestMethod.POST })
    public String go_singin(HttpServletRequest request, @RequestParam(value = "project_id")Integer project_id) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            StudentMapper.goSignin(project_id,account.getId());

            // 添加提醒信息到系统信息记录中
            SysInfo c=new SysInfo();
            c.setFrom_id(account.getId());
            Project pro=StudentMapper.getProByProId(project_id).get(0);
            c.setTo_id(pro.getTeacher_id());
            c.setMessage(account.getUsername()+"报名了你的【"+pro.getTitle()+"】项目。");
            SysInfoMapper.insertMessage(c);
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

    @RequestMapping(value = "/upload_plan", method = { RequestMethod.POST })
    public String upload(HttpServletRequest request,@RequestParam(value = "title")String title,
                         @RequestParam(value = "plan_direction",defaultValue="")String plan_direction,
                         @RequestParam(value = "type",defaultValue="") String type,
                         @RequestParam(value = "description",defaultValue="") String description) {
        HttpSession session=request.getSession();
        User account=(User)session.getAttribute("sid");
        if(account==null) {//如果不为空
            return wrapperMsg("invalid","该账号未登录",null);
        }
        if(account.isType()){
            return wrapperMsg("invalid","该账号不是学生",null);
        }
        if(title==null){
            return wrapperMsg("invalid","标题不能为空",null);
        }
        Plan s = new Plan();
        s.setTitle(title);
        s.setPlan_direction(plan_direction);
        s.setType(type);
        s.setDescription(description);
        s.setStudent_id(account.getId());
        StudentMapper.uploadPlan(s);

        // elasticsearch storage
        EsProduct esp=new EsProduct();
        //防止不同类型的相同id碰撞
        esp.setId(s.getId()*4-1);
        esp.setDepartment(account.getDepartment());
        esp.setItem_id(s.getId());
        esp.setUser_id(s.getStudent_id());
        esp.setType("plan");
        esp.setKeywords(account.getUsername());
        esp.setName(s.getTitle());
        esp.setReal_name(account.getReal_name());
        esp.setSubTitle(s.getDescription());
        // 存储文档到es中
        esService.create(esp);

        JSONObject wrapperMsg = new JSONObject();
        wrapperMsg.put("plan_id", s.getId());
        return wrapperMsg("valid","成功创建",wrapperMsg);
    }

    @RequestMapping(value = "/update_plan", method = { RequestMethod.POST })
    public String update_plan(HttpServletRequest request,@RequestParam(value="id")Integer id,
                                 @RequestParam(value = "title",defaultValue="")String title,
                                 @RequestParam(value = "plan_direction",defaultValue="")String plan_direction,
                                 @RequestParam(value = "type",defaultValue="") String type,
                                 @RequestParam(value = "description",defaultValue="") String description) {
        HttpSession session=request.getSession();
        User account=(User)session.getAttribute("sid");
        if(account==null) {//如果不为空
            return wrapperMsg("invalid","该账号未登录",null);
        }
        if(account.isType()){
            return wrapperMsg("invalid","该账号不是学生",null);
        }
        if(id==null){
            return wrapperMsg("invalid","项目id不能为空",null);
        }
        Plan s = new Plan();
        s.setTitle(title);
        s.setPlan_direction(plan_direction);
        s.setType(type);
        s.setDescription(description);
        s.setId(id);
        StudentMapper.updatePlan(s);

        // update elasticsearch storage
        EsProduct esp=esService.get(id * 4-1);
        if(!title.equals("")) {
            esp.setName(title);
        }
        if(!description.equals("")) {
            esp.setSubTitle(description);
        }
        esService.create(esp);

        return wrapperMsg("valid","成功修改",null);
    }

    @RequestMapping(value = "/cancel_plan", method = { RequestMethod.POST })
    public String cancel_plan(HttpServletRequest request,@RequestParam(value="id")Integer id) {
        HttpSession session=request.getSession();
        User account=(User)session.getAttribute("sid");
        if(account==null) {//如果不为空
            return wrapperMsg("invalid","该账号未登录",null);
        }
        if(account.isType()){
            return wrapperMsg("invalid","该账号不是学生",null);
        }
        if(id==null){
            return wrapperMsg("invalid","项目id不能为空",null);
        }
        StudentMapper.cancelPlan(id);

        // delete from elasticsearch storage
        esService.delete(id*4-1);

        return wrapperMsg("valid", "成功删除", null);
    }

    @RequestMapping(value = "/upload_star", method = { RequestMethod.POST })
    public String upload_star(HttpServletRequest request,@RequestParam(value="id")Integer project_id) {
        HttpSession session=request.getSession();
        User account=(User)session.getAttribute("sid");
        if(account==null) {//如果不为空
            return wrapperMsg("invalid","该账号未登录",null);
        }
        if(account.isType()){
            return wrapperMsg("invalid","该账号不是学生",null);
        }
        if(project_id==null){
            return wrapperMsg("invalid","项目id不能为空",null);
        }
        StudentMapper.goStar(project_id,account.getId());
        return wrapperMsg("valid", "成功关注", null);
    }

    @RequestMapping(value = "/cancel_star", method = { RequestMethod.POST })
    public String cancel_star(HttpServletRequest request,@RequestParam(value="id")Integer project_id) {
        HttpSession session=request.getSession();
        User account=(User)session.getAttribute("sid");
        if(account==null) {//如果不为空
            return wrapperMsg("invalid","该账号未登录",null);
        }
        if(account.isType()){
            return wrapperMsg("invalid","该账号不是学生",null);
        }
        if(project_id==null){
            return wrapperMsg("invalid","项目id不能为空",null);
        }
        StudentMapper.quitStar(project_id,account.getId());
        return wrapperMsg("valid", "成功取消关注", null);
    }

    @RequestMapping(value = "/get_my_project", method = { RequestMethod.GET })
    public String get_my_project(HttpServletRequest request) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            JSONArray jsonArray = new JSONArray();
            List<Project> list = StudentMapper.getMyPro(account.getId());
            for (Project project : list) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id",project.getId());
                List<User> teacher = StudentMapper.getUserById(project.getTeacher_id());
                User otherUser = teacher.get(0);

                jsonObject.put("title",project.getTitle());
                jsonObject.put("description",project.getDescription());
                jsonObject.put("department",otherUser.getDepartment());
                String name = otherUser.getReal_name();
                if(name!=null&& name.length()>0) {
                    jsonObject.put("teacher", otherUser.getReal_name());
                }
                else{
                    jsonObject.put("teacher",otherUser.getUsername());
                }
                jsonArray.add(jsonObject);
            }

            return wrapperMsgArray("valid","",jsonArray);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }

    @RequestMapping(value = "/get_my_plan", method = { RequestMethod.GET })
    public String get_my_plan(HttpServletRequest request) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            JSONArray jsonArray = new JSONArray();
            List<Plan> list = StudentMapper.getMyPlan(account.getId());
            for (Plan plan : list) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", plan.getId());
                jsonObject.put("title", plan.getTitle());
                jsonObject.put("description", plan.getDescription());
                jsonObject.put("department", account.getDepartment());
                String name = account.getReal_name();
                if (name != null && name.length() > 0) {
                    jsonObject.put("student", account.getReal_name());
                } else {
                    jsonObject.put("student", account.getUsername());
                }
                jsonArray.add(jsonObject);
            }

            return wrapperMsgArray("valid","",jsonArray);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }
}
