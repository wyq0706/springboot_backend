package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.ChatDao;
import com.mobilecourse.backend.dao.StudentDao;
import com.mobilecourse.backend.dao.SysInfoDao;
import com.mobilecourse.backend.model.Chat;
import com.mobilecourse.backend.model.Plan;
import com.mobilecourse.backend.model.SysInfo;
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

    @Autowired
    private SysInfoDao SysInfoMapper;

    @RequestMapping(value = "/get_chat_content/{id}", method = { RequestMethod.GET })
    public String get_chat_content(HttpServletRequest request, @PathVariable(value = "id")Integer id) {
        User account=getUserFromSession(request);
        if(account!=null) {//如果不为空
            JSONArray jsonArray = new JSONArray();
            if(id==0){
                List<SysInfo> list=SysInfoMapper.getMessage(account.getId());
                for (SysInfo s: list) {
                    JSONObject jsonObject = new JSONObject();
                    jsonObject.put("type", "sender");
                    jsonObject.put("content", s.getMessage());
                    jsonObject.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(s.getCreated_time()));
                    jsonArray.add(jsonObject);
                }

                // 刷新已读
                SysInfoMapper.updateRead(account.getId());
            }else {
                List<Chat> list = ChatMapper.getMessage(id, account.getId());
                for (Chat s : list) {
                    JSONObject jsonObject = new JSONObject();
                    if (s.getFrom_id() == id) {
                        jsonObject.put("type", "sender");
                    } else {
                        jsonObject.put("type", "rcver");
                    }
                    jsonObject.put("content", s.getMessage());
                    jsonObject.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(s.getCreated_time()));
                    jsonArray.add(jsonObject);
                }

                // 刷新已读
                ChatMapper.updateRead(id, account.getId());
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
            // system info
            JSONArray jsonArray = new JSONArray();
            SysInfo s=SysInfoMapper.getLatestSysInfo(account.getId());
            if(s!=null) {
                JSONObject jsonObj = new JSONObject();
                jsonObj.put("name", "系统提醒");
                jsonObj.put("from_id", 0);
                jsonObj.put("latest_content", s.getMessage());
                jsonObj.put("time", new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(s.getCreated_time()));

                // 判断是否未读
                if (s.isIfRead())
                    jsonObj.put("real_all", true);
                else
                    jsonObj.put("real_all", false);
                jsonArray.add(jsonObj);
            }

            // chat info
            List<User> list = ChatMapper.getChatter(account.getId());
            for (User usr : list) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("name", usr.getUsername());
                jsonObject.put("from_id", usr.getId());
                Chat c=ChatMapper.getLatestChat(usr.getId(),account.getId());
                jsonObject.put("latest_content", c.getMessage());
                jsonObject.put("time",  new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(c.getCreated_time()));

                // 判断是否未读
                if(c.isIfRead()||c.getFrom_id()==account.getId())
                    jsonObject.put("real_all",true);
                else
                    jsonObject.put("real_all",false);

                jsonArray.add(jsonObject);
            }
            return wrapperMsgArray("valid", "", jsonArray);
        } else {
            return wrapperMsg("invalid", "未登录", null);
        }
    }

}
