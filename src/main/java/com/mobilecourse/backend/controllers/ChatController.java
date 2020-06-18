package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.ChatDao;
import com.mobilecourse.backend.dao.StudentDao;
import com.mobilecourse.backend.model.Chat;
import com.mobilecourse.backend.model.Plan;
import com.mobilecourse.backend.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

@RestController
@EnableAutoConfiguration
//设置此参数可以给这个类的所有接口都加上前缀
@RequestMapping("/api/chat")
public class ChatController extends CommonController {

    @Autowired
    private ChatDao ChatMapper;

    @RequestMapping(value = "/get_chat_content/{id}", method = { RequestMethod.GET })
    public String get_chat_content(HttpServletRequest request, @PathVariable(value = "id")Integer id) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            List<Chat> list= ChatMapper.getMessage(id,account.getId());
            JSONArray jsonArray = new JSONArray();
            for (Chat s : list) {
                JSONObject jsonObject = new JSONObject();
                if(s.getFrom_id()==id){
                    jsonObject.put("type","sender");
                }else{
                    jsonObject.put("type","rcver");
                }
                jsonObject.put("content", s.getMessage());
                jsonObject.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(s.getCreated_time()));
                jsonArray.add(jsonObject);
            }
            return wrapperMsgArray("valid","",jsonArray);
        }else {
            return wrapperMsg("invalid","未登录",null);
        }
    }

    @RequestMapping(value = "/get_chatter", method = { RequestMethod.GET })
    public String get_chatter(HttpServletRequest request) {
        User account = getUserFromSession(request);
        if (account != null) {//如果不为空
            List<User> list = ChatMapper.getChatter(account.getId());
            JSONArray jsonArray = new JSONArray();
            for (User s : list) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", s.getUsername());
                jsonObject.put("from_id", s.getId());
                Chat c=ChatMapper.getLatestChat(s.getId(),account.getId());
                jsonObject.put("latest_content", c.getMessage());
                jsonObject.put("time",  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getCreated_time()));
                jsonArray.add(jsonObject);
            }
            return wrapperMsgArray("valid", "", jsonArray);
        } else {
            return wrapperMsg("invalid", "未登录", null);
        }
    }

}
