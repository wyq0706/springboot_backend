package com.mobilecourse.backend.model;

import java.sql.Timestamp;

// 此类中的类型必须和数据库中table的列类型一一对应，否则会出现问题
public class SysInfo {
    // getter和setter可以使用idea自带的功能生成，右键点击->Generate->Getter and Setter即可自动生成


    private int id;
    private String message;
    private int from_id;
    private int to_id;
    private Timestamp created_time;
    private boolean ifRead;

    public boolean isIfRead() {
        return ifRead;
    }

    public void setIfRead(boolean ifRead) {
        this.ifRead = ifRead;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getFrom_id() {
        return from_id;
    }

    public void setFrom_id(int from_id) {
        this.from_id = from_id;
    }

    public int getTo_id() {
        return to_id;
    }

    public void setTo_id(int to_id) {
        this.to_id = to_id;
    }

    public Timestamp getCreated_time() {
        return created_time;
    }

    public void setCreated_time(Timestamp created_time) {
        this.created_time = created_time;
    }

    private User from;

    public User getFrom() {
        return from;
    }

    public void setFrom(User from) {
        this.from = from;
    }
}
