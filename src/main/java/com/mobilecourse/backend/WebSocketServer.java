package com.mobilecourse.backend;

import com.alibaba.fastjson.JSONObject;
import com.mobilecourse.backend.dao.ChatDao;
import com.mobilecourse.backend.model.Chat;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.websocket.*;
import javax.websocket.server.PathParam;
import javax.websocket.server.ServerEndpoint;
import java.io.IOException;
import java.util.Hashtable;

@ServerEndpoint("/websocket/{sid}")
@Component
public class WebSocketServer {

    // 这里使用静态，让 service 属于类
    private static ChatDao ChatMapper;
    // 注入的时候，给类的 service 注入
    @Autowired
    public void setChatDao(ChatDao chatService) {
        WebSocketServer.ChatMapper = chatService;
    }

    public static Hashtable<String, WebSocketServer> getWebSocketTable() {
        return webSocketTable;
    }

    private static Hashtable<String, WebSocketServer> webSocketTable = new Hashtable<>();

    //与某个客户端的连接会话，需要通过它来给客户端发送数据
    private Session session;

    //用于标识客户端的sid
    private String sid = "";

    //推荐在连接的时候进行检查，防止有人冒名连接
    @OnOpen
    public void onOpen(Session session, @PathParam("sid")String sid) {
        this.session = session;
        this.sid = sid;
        webSocketTable.put(sid, this);
        try {
            System.out.println(sid + "成功连接websocket");
            sendMessage("连接成功！");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // 在关闭连接时移除对应连接
    @OnClose
    public void onClose() {
        System.out.println(sid + "成功断开websocket");
        webSocketTable.remove(this.sid);
    }

    // 收到消息时候的处理
    @OnMessage
    public void onMessage(String message, Session session) throws IOException {
        JSONObject obj = JSONObject.parseObject(message);
        String mes = (String) obj.get("message");
        String to= obj.get("to")+"";
        Chat c=new Chat();
        c.setFrom_id(Integer.parseInt(sid));
        c.setTo_id(Integer.parseInt(to));
        c.setMessage(mes);
        if (webSocketTable.get(to)!=null )
            c.setIfRead(true);
        else
            c.setIfRead(false);
        ChatMapper.insertMessage(c);
        sendMessageTo(mes,to);
    }


    private void sendMessageTo(String mes, String to) {
        if (webSocketTable.get(to)!=null )
            webSocketTable.get(to).session.getAsyncRemote().sendText(mes);
    }

    @OnError
    public void onError(Session session, Throwable error) {
        error.printStackTrace();
        webSocketTable.remove(this.sid);
    }

    public void sendMessage(String message) throws IOException {
        this.session.getBasicRemote().sendText(message);
    }
}
