package com.mobilecourse.backend.controllers;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.TestDao;
import com.mobilecourse.backend.model.Test;
import com.mobilecourse.backend.WebSocketServer;
import org.apache.tomcat.jni.Time;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Hashtable;
import java.util.List;

@RestController
@EnableAutoConfiguration
//设置此参数可以给这个类的所有接口都加上前缀
//@RequestMapping("/test")
public class TestController extends CommonController {

    @Autowired
    private TestDao testMapper;

    // 普通请求，不指定method意味着接受所有类型的请求
    @RequestMapping(value = "/hello")
    public String hello() {
        return wrapperMsg("当前数据库中共有：" + testMapper.testCnt() + "条数据！","",null);
    }

    // 带路径参数的GET请求，示例/delete/1，指定method意味着只接受对应类型的请求
    @RequestMapping(value = "/delete/{id}", method = { RequestMethod.GET })
    public String delete(@PathVariable int id) {
        testMapper.delete(id);
        return wrapperMsg("成功删除id为：" + id + "的数据！","",null);
    }

    // 带路径参数和查询参数的GET请求，示例/update/1?content=213
    @RequestMapping(value = "/update/{id}", method = { RequestMethod.GET })
    public String update(@PathVariable int id,
                            @RequestParam(value = "content", defaultValue = "test")String content) {
        testMapper.update(id, content);
        return wrapperMsg("success","",null);
    }

    // 插入方式，设置为只能使用PUT方式访问
    @RequestMapping(value = "/insert", method = { RequestMethod.PUT })
    public String insert(@RequestParam(value = "content", defaultValue = "test")String content) {
        Test t = new Test();
        t.setContent(content);
        t.setCreateTime(Timestamp.valueOf(LocalDateTime.now()));
        testMapper.insert(t);
        return wrapperMsg("success","",null);
    }

    // 使用POST请求上传文件
    @RequestMapping(value = "/upload", method = { RequestMethod.POST })
    public String upload(@RequestParam(value = "file")MultipartFile file) {
        return wrapperMsg(file.getOriginalFilename(),"",null);
    }

    // 查询所有数据，最好给内部加上异常捕获，方便正确处理异常
    @RequestMapping("/selectAll")
    public String selectAll() {
        try {
            JSONArray jsonArray = new JSONArray();
            List<Test> list = testMapper.selectAll();
            if (list.size() == 0)
                return wrapperMsg("没有找到对应的数据！","",null);
            for (Test s : list) {
                JSONObject jsonObject = new JSONObject();
                jsonObject.put("id", s.getId());
                jsonObject.put("content", s.getContent());
                jsonObject.put("createTime", s.getCreateTime());
                jsonArray.add(jsonObject);
            }
            return wrapperMsg(jsonArray.toJSONString(),"",null);
        } catch (Exception e) {
            return wrapperMsg(e.toString(),"",null);
        }
    }

    // WebSocket群发消息
    @RequestMapping("/sendSocketMsg/{msg}")
    public String sendSocketMsg(@PathVariable String msg) {
        try {
            Hashtable<String, WebSocketServer> webSockets = WebSocketServer.getWebSocketTable();
            for (WebSocketServer socketServer : webSockets.values()) {
                socketServer.sendMessage(msg);
            }
            return wrapperMsg("sucess","",null);
        } catch (Exception e) {
            return wrapperMsg(e.toString(),"",null);
        }
    }
}
