package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.StudentDao;
import com.mobilecourse.backend.dao.TeacherDao;
import com.mobilecourse.backend.dao.UserDao;
import com.mobilecourse.backend.model.Plan;
import com.mobilecourse.backend.model.Project;
import com.mobilecourse.backend.model.Test;
import com.mobilecourse.backend.model.User;
import com.mobilecourse.backend.service.EsProductService;
import com.mobilecourse.backend.nosql.elasticsearch.document.EsProduct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.data.domain.Page;
import org.springframework.util.ResourceUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.List;

@RestController
@EnableAutoConfiguration
//设置此参数可以给这个类的所有接口都加上前缀
@RequestMapping("/api/user")
public class UserController extends CommonController {

    @Autowired
    private UserDao UserMapper;

    @Autowired
    private EsProductService esService;

    @Autowired
    private TeacherDao TeacherMapper;

    @Autowired
    private StudentDao StudentMapper;

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

            // elasticsearch storage
            EsProduct esp=new EsProduct();
            //防止不同类型的相同id碰撞
            if(type) {
                esp.setId(s.getId() * 4 -2);
                esp.setType("teacher");
            }else{
                esp.setId(s.getId() * 4 -3);
                esp.setType("student");
            }
            esp.setDepartment("");
            esp.setItem_id(s.getId());
            esp.setUser_id(s.getId());
            esp.setKeywords(username);
            esp.setName(username);
            esp.setSubTitle("");
            // 存储文档到es中
            esService.create(esp);
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
                boolean type=s.get(0).isType();
                JSONObject object=new JSONObject();
                object.put("type",type);
                object.put("user_id",s.get(0).getId());
                return wrapperMsg("valid","成功登录",object);
            }
    }

    @RequestMapping(value = "/logout", method = { RequestMethod.GET })
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

            // update elasticsearch storage
            EsProduct esp=null;
            if(account.isType()) {
                esp = esService.get(account.getId() * 4 - 2);
            }else {
                esp = esService.get(account.getId() * 4 - 3);
            }
            esp.setSubTitle(signature);
            esService.create(esp);

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
            //removeInfoFromSession(request,"sid");
            account.setUsername(newName);
            //putInfoToSession(request, "sid", account);

            // update elasticsearch storage
            EsProduct esp=null;
            if(account.isType()) {
                esp = esService.get(account.getId() * 4 - 2);
            }else {
                esp = esService.get(account.getId() * 4 - 3);
            }
            esp.setName(newName);
            esService.create(esp);

            return wrapperMsg("valid","成功更新",null);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }

    @RequestMapping(value = "/update_password", method = { RequestMethod.POST })
    public String update_password(HttpServletRequest request, @RequestParam(value = "old_password")String old_password,@RequestParam(value = "new_password")String new_password) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            String username = account.getUsername();
            if(!account.getPassword().equals(old_password)){
                return wrapperMsg("invalid","密码错误！",null);
            }
            UserMapper.updatePassword(username,new_password);
            account.setPassword(new_password);
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
            account.setPersonal_info(personal_info);
            UserMapper.updatePersonalInfo(username,personal_info);
            return wrapperMsg("valid","成功更新",null);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }

    @RequestMapping(value = "/update_icon", method = { RequestMethod.POST })
    public String update_icon(HttpServletRequest request, @RequestParam(value = "file") MultipartFile file) throws FileNotFoundException {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            String id = String.valueOf(account.getId());
            if (!file.isEmpty()) {
                String fileName = id + ".jpg";
                String filePath = System.getProperty("user.dir")+"/icon/"+fileName;
                System.out.println(filePath);
                try {
                    File serverFile=new File(filePath);
                    if(!serverFile.exists()) {
                        //先得到文件的上级目录，并创建上级目录，在创建文件
                        serverFile.getParentFile().mkdir();
                    }
                    //创建文件
                    file.transferTo(new File(filePath));
                } catch (IOException e) {
                    e.printStackTrace();
                    return wrapperMsg("invalid","更新失败",null);
                }

            }
            return wrapperMsg("valid","成功更新",null);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }

    @RequestMapping(value = "/getIcon/{id}", method = { RequestMethod.GET })
    public void getIcon(HttpServletRequest request, @PathVariable(value = "id") String id, HttpServletResponse response) throws IOException{
        try {
            String fileName = id + ".jpg";
            String address = System.getProperty("user.dir")+"/icon/"+fileName;
            FileInputStream hFile=new FileInputStream(new File(address));
            int i=hFile.available();
            byte data[]=new byte[i];
            hFile.read(data);
            hFile.close();
            response.setContentType("image/jpeg");
            OutputStream toClient=response.getOutputStream();
            toClient.write(data);
            toClient.close();
        }catch (IOException e){
            try {
                String address = System.getProperty("user.dir")+"/icon/default.jpg";
                FileInputStream hFile=new FileInputStream(new File(address));
                int i=hFile.available();
                byte data[]=new byte[i];
                hFile.read(data);
                hFile.close();
                response.setContentType("image/jpeg");
                OutputStream toClient=response.getOutputStream();
                toClient.write(data);
                toClient.close();
            }catch (IOException a){
                PrintWriter toClient=response.getWriter();
                response.setContentType("text/html;charset=gb2312");
                toClient.write("无法打开图片");
                toClient.close();
            }
        }

    }

    @RequestMapping(value = "/go_follow", method = { RequestMethod.POST })
    public String go_follow(HttpServletRequest request, @RequestParam(value = "user_id")Integer user_id) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            if(user_id==account.getId()){return wrapperMsg("invalid","不可以追踪自己！",null);}
            UserMapper.goFollow(user_id,account.getId());
            return wrapperMsg("valid","成功追踪",null);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }

    @RequestMapping(value = "/cancel_follow", method = { RequestMethod.POST })
    public String cancel_follow(HttpServletRequest request, @RequestParam(value = "user_id")Integer user_id) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            UserMapper.cancelFollow(user_id,account.getId());
            return wrapperMsg("valid","取消追踪",null);
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
                jsonObject.put("username", s.getUsername());
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
                jsonObject.put("username", s.getUsername());
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
            jsonObject.put("researchDirection",s.getResearch_direction());
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

            // elasticsearch需要-->减少一次数据库查询，详见TeacherController.upload_recruit
            // for new item
            account.setDepartment(department);
            account.setReal_name(realname);
            UserMapper.verification(username,realname,school,department,grade);

            // update elasticsearch storage (existed items)
            // users
            EsProduct esp=null;
            if(account.isType()) {
                esp = esService.get(account.getId() * 4 - 2);
            }else{
                esp = esService.get(account.getId()* 4 - 3);
            }
            esp.setDepartment(department);
            esp.setReal_name(realname);
            esp.setKeywords(realname);
            esService.create(esp);

            if(account.isType()) {
                // projects
                List<Project> list = TeacherMapper.getProById(account.getId());
                for (Project pro : list) {
                    esp = esService.get(pro.getId() * 4);
                    esp.setDepartment(department);
                    esp.setReal_name(realname);
                    esp.setKeywords(realname);
                    esService.create(esp);
                }
            }else {
                // plans
                List<Plan> list = StudentMapper.getMyPlan(account.getId());
                for (Plan pro : list) {
                    esp = esService.get(pro.getId() * 4-1);
                    esp.setDepartment(department);
                    esp.setReal_name(realname);
                    esp.setKeywords(realname);
                    esService.create(esp);
                }
            }

            return wrapperMsg("valid","成功验证",null);
        }else {
            return wrapperMsg("invalid","未成功验证",null);
        }
    }

    @RequestMapping(value = "/search",method = {RequestMethod.POST})
    public String search(HttpServletRequest request,
                               @RequestParam(value = "key_word")String keyword,
                               @RequestParam(value = "type")String type) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            Page<EsProduct> esp_page=esService.search(keyword,0,10);
            List<EsProduct> esp_list=esp_page.getContent();
            JSONArray jsonArray = new JSONArray();
            boolean ifAll=type.equals("All");
            for (EsProduct s : esp_list) {
                if(ifAll||type.equals(s.getType())) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", s.getType());
                    jsonObject.put("id", s.getItem_id());
                    jsonObject.put("description", s.getSubTitle());
                    jsonObject.put("title", s.getName());
                    jsonObject.put("name", s.getKeywords());
                    jsonObject.put("department", s.getDepartment());
                    jsonArray.add(jsonObject);
                }
            }
            return wrapperMsgArray("valid","",jsonArray);
        }else {
            return wrapperMsg("invalid","未成功验证",null);
        }
    }

    @RequestMapping(value = "/recommend",method = {RequestMethod.POST})
    public String recommend(HttpServletRequest request) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            Page<EsProduct> esp_page=esService.search("软件",0,10);
            List<EsProduct> esp_list=esp_page.getContent();
            JSONArray jsonArray = new JSONArray();
            for (EsProduct s : esp_list) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("type", s.getType());
                jsonObject.put("id", s.getItem_id());
                jsonObject.put("description", s.getSubTitle());
                jsonObject.put("title", s.getName());
                jsonObject.put("name", s.getKeywords());
                jsonObject.put("department", s.getDepartment());
                jsonArray.add(jsonObject);
            }
            return wrapperMsgArray("valid","",jsonArray);
        }else {
            return wrapperMsg("invalid","未成功验证",null);
        }
    }

    @RequestMapping(value = "/home",method = {RequestMethod.GET})
    public String home(HttpServletRequest request) {
        User user=getUserFromSession(request);
        if(user!=null) {//如果不为空
            JSONObject jsonObject = new JSONObject();
            int id = user.getId();
            User account=UserMapper.getUserById(id).get(0);
            String username = account.getUsername();
            Boolean type = account.isType();
            String icon_url = "http://47.94.145.111:8080/api/user/getIcon/"+String.valueOf(id);
            Boolean verification = account.isVerification();
            String signature = account.getSignature();
            String person_info = account.getPersonal_info();
            String real_name = account.getReal_name();
            String school =  account.getSchool();
            String department =  account.getDepartment();
            String grade = account.getGrade();
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
            jsonObject.put("id",id);
            jsonObject.put("real_name",real_name);
            jsonObject.put("school",school);
            jsonObject.put("department",department);
            jsonObject.put("grade",grade);
            jsonObject.put("follow_num",follow_num);
            jsonObject.put("followee_num",followee_num);
            jsonObject.put("star_or_pro_num",star_or_pro_num);
            return wrapperMsg("valid","",jsonObject);
        }else {
            return wrapperMsg("invalid","未成功验证",null);
        }
    }

    @RequestMapping(value = "/project_info/{id}",method = {RequestMethod.GET})
    public String project_info(HttpServletRequest request, @PathVariable(value = "id")Integer project_id) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            Project s=UserMapper.getSingleProject(project_id);
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
            jsonObject.put("description",s.getDescription());
            jsonObject.put("requirement",s.getRequirement());
            jsonObject.put("createTime",s.getCreated_time());
            jsonObject.put("research_direction",s.getResearch_direction());

            boolean ifStarred = UserMapper.getProjectIfStarred(project_id, account.getId())>0;
            boolean ifSigned= UserMapper.getProjectIfSigned(project_id, account.getId())>0;

            jsonObject.put("isStarred",ifStarred);
            jsonObject.put("isRegistered",ifSigned);
            return wrapperMsg("valid","",jsonObject);
        }else {
            return wrapperMsg("invalid","未成功验证",null);
        }
    }

    @RequestMapping(value = "/plan_info/{id}",method = {RequestMethod.GET})
    public String plan_info(HttpServletRequest request, @PathVariable(value = "id")Integer plan_id) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            try {
                List<Plan> s=UserMapper.getSinglePlan(plan_id);
                Plan plan=s.get(0);
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("plan_title", plan.getTitle());
                jsonObject.put("student_id",plan.getStudent_id());
                jsonObject.put("type",plan.getType());
                jsonObject.put("plan_direction",plan.getPlan_direction());
                List<User> users=UserMapper.getUserById(plan.getStudent_id());
                if(users==null){
                    jsonObject.put("department","");
                }else {
                    jsonObject.put("department", users.get(0).getDepartment());
                }
                jsonObject.put("description",plan.getDescription());
                jsonObject.put("createTime",plan.getCreated_time());

                return wrapperMsg("valid","",jsonObject);
            }
            catch (Exception e){
                return wrapperMsg("invalid","Plan ID不存在！",null);
            }

        }else {
            return wrapperMsg("invalid","未成功验证",null);
        }
    }


    @RequestMapping(value = "/user_info/{id}",method = {RequestMethod.GET})
    public String userInfo(HttpServletRequest request,@PathVariable(value = "id") Integer id) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            JSONObject jsonObject = new JSONObject();
            List<User> s = UserMapper.getUserById(id);
            if(s==null){return wrapperMsg("invalid","没有该用户名的用户",null);}
            User otherUser = s.get(0);
            String username = otherUser.getUsername();
            Boolean type = otherUser.isType();
            String icon_url = "http://47.94.145.111:8080/api/user/getIcon/"+String.valueOf(id);
            Boolean verification = otherUser.isVerification();
            String signature = otherUser.getSignature();
            String person_info = otherUser.getPersonal_info();
            String real_name = otherUser.getReal_name();
            String school =  otherUser.getSchool();
            String department =  otherUser.getDepartment();
            String grade = otherUser.getGrade();
            boolean relation = (UserMapper.ifFollow(id,account.getId())>=1);
            jsonObject.put("icon_url",icon_url);
            jsonObject.put("username",username);
            jsonObject.put("type",type);
            jsonObject.put("verification",verification);
            jsonObject.put("signature",signature);
            jsonObject.put("personal_info",person_info);
            jsonObject.put("real_name",real_name);
            jsonObject.put("department",department);
            jsonObject.put("grade",grade);
            jsonObject.put("school",school);
            jsonObject.put("relation",relation);
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
                List<User> teacher = UserMapper.getUserById(s.getTeacher_id());
                User otherUser = teacher.get(0);
                jsonObject.put("id",s.getId());
                jsonObject.put("title",s.getTitle());
                jsonObject.put("description",s.getDescription());
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

    @RequestMapping(value = "/get_plan_or_pro/{id}", method = { RequestMethod.GET })
    public String getPlanOrPro(HttpServletRequest request,@PathVariable(value = "id") Integer id) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            JSONArray jsonArray = new JSONArray();
            List<User> s = UserMapper.getUserById(id);
            if(s==null){return wrapperMsg("invalid","没有该用户名的用户",null);}
            User otherUser = s.get(0);
            if(otherUser.isType()){
                List<Project> list=UserMapper.getProById(id);
                for (Project project : list) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id",project.getId());
                    jsonObject.put("title",project.getTitle());
                    jsonObject.put("description",project.getDescription());
                    jsonObject.put("department",otherUser.getDepartment());
                    String name = otherUser.getReal_name();
                    if(name!=null&& name.length()>0) {
                        jsonObject.put("name", otherUser.getReal_name());
                    }
                    else{
                        jsonObject.put("name",otherUser.getUsername());
                    }
                    jsonArray.add(jsonObject);
                }
            }
            else{
                List<Plan> list=UserMapper.getPlanById(id);
                for (Plan plan : list) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("id",plan.getId());
                    jsonObject.put("title",plan.getTitle());
                    jsonObject.put("description",plan.getDescription());
                    jsonObject.put("department",otherUser.getDepartment());
                    String name = otherUser.getReal_name();
                    if(name!=null&& name.length()>0) {
                        jsonObject.put("name", otherUser.getReal_name());
                    }
                    else{
                        jsonObject.put("name",otherUser.getUsername());
                    }
                    jsonArray.add(jsonObject);
                }

            }
            return wrapperMsgArray("valid","",jsonArray);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }



}
