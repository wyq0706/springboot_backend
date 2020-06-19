package com.mobilecourse.backend.model;

import java.util.Date;

// 此类中的类型必须和数据库中table的列类型一一对应，否则会出现问题
public class Project {
    // getter和setter可以使用idea自带的功能生成，右键点击->Generate->Getter and Setter即可自动生成


    private int id;
    private String title;
    private String research_direction;
    private String requirement;
    private String description;
    private int teacher_id;
    private Date created_time;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getResearch_direction() {
        if(research_direction!=null) {
            return research_direction;
        }
        else{return "";}
    }

    public Date getCreated_time() {
        return created_time;
    }

    public void setResearch_direction(String research_direction) {
        this.research_direction = research_direction;
    }

    public String getRequirement() {
        if(requirement!=null) {
            return requirement;
        }
        else{return "";}
    }

    public void setRequirement(String requirement) {
        this.requirement = requirement;
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

    public int getTeacher_id() {
        return teacher_id;
    }

    public void setTeacher_id(int teacher_id) {
        this.teacher_id = teacher_id;
    }

    private User teacher;
    public User getTeacher() {
        return teacher;
    }

    public void setTeacher(User teacher) {
        this.teacher = teacher;
    }
}
