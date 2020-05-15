package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.model.User;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.Enumeration;

@Controller
public class CommonController {

    // session半个小时无交互就会过期
    private static int MAXTIME = 18000;

    // 添加一个code，方便客户端根据code来判断服务器处理状态并解析对应的msg
    String wrapperMsg(String msg,String detail,@Nullable JSONObject data) {
        JSONObject wrapperMsg = new JSONObject();
        wrapperMsg.put("response", msg);
        wrapperMsg.put("detail",detail);
        wrapperMsg.put("data",data);
        return wrapperMsg.toJSONString();
    }


    String wrapperMsgArray(String msg,String detail,@Nullable JSONArray data) {
        JSONObject wrapperMsg = new JSONObject();
        wrapperMsg.put("response", msg);
        wrapperMsg.put("detail",detail);
        wrapperMsg.put("data",data);
        return wrapperMsg.toJSONString();
    }

    // 添加信息到session之中，此部分用途很广泛，比如可以通过session获取到对应的用户名或者用户ID，避免繁冗操作
    public void putInfoToSession(HttpServletRequest request, String keyName, Object info)
    {
        HttpSession session = request.getSession();
        //设置session过期时间，单位为秒(s)
        session.setMaxInactiveInterval(MAXTIME);
        //将信息存入session
        session.setAttribute(keyName, info);
    }

    public User getUserFromSession(HttpServletRequest request)
    {
        HttpSession session=request.getSession();
        User account=(User)session.getAttribute("sid");
        return account;
    }

    // 添加信息到session之中，此部分用途很广泛，比如可以通过session获取到对应的用户名或者用户ID，避免繁冗操作
    public void removeInfoFromSession(HttpServletRequest request, String keyName)
    {
        HttpSession session = request.getSession();
        // 删除session里面存储的信息，一般在登出的时候使用
        session.removeAttribute(keyName);
    }
}
