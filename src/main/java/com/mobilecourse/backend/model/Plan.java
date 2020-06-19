package com.mobilecourse.backend.model;

import java.util.Date;

// 此类中的类型必须和数据库中table的列类型一一对应，否则会出现问题
public class Plan {
    // getter和setter可以使用idea自带的功能生成，右键点击->Generate->Getter and Setter即可自动生成


    private int id;
    private String title;
    private String plan_direction;
    private String type;
    private String description;
    private int student_id;
    private Date created_time;

    public Date getCreated_time() {
        return created_time;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getPlan_direction() {
        if(plan_direction!=null) {
            return plan_direction;
        }
        else{return "";}
    }

    public void setPlan_direction(String plan_direction) {
        this.plan_direction = plan_direction;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDescription() {
        if(description!=null) {
            return description;
        }
        else{return "";}
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getStudent_id() {
        return student_id;
    }

    public void setStudent_id(int student_id) {
        this.student_id = student_id;
    }

    private User student;

    public User getStudent() {
        return student;
    }

    public void setStudent(User student) {
        this.student = student;
    }
}
