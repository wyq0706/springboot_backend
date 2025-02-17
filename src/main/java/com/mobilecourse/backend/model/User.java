package com.mobilecourse.backend.model;

// 此类中的类型必须和数据库中table的列类型一一对应，否则会出现问题
public class User {
    // getter和setter可以使用idea自带的功能生成，右键点击->Generate->Getter and Setter即可自动生成


    private int id;
    private String username;
    private byte[] icon;
    private String signature;
    private String password;
    private String personal_info;
    private boolean verification;
    //学生/老师
    private boolean type;
    private String school;
    private String department;
    private String real_name;
    private String grade;

    public String getSchool() {
        if(school!=null){return school;}
        else{return "";}

    }

    public void setSchool(String school) {
        this.school = school;
    }

    public String getDepartment() {
        if(department!=null) {
            return department;
        }
        else{return "";}
    }

    public void setDepartment(String department) {
        this.department = department;
    }

    public String getReal_name() {
        if(real_name!=null) {
            return real_name;
        }
        else{return "";}
    }

    public void setReal_name(String real_name) {
        this.real_name = real_name;
    }

    public String getGrade() {
        if(grade!=null) {
            return grade;
        }
        else{return "";}
    }

    public void setGrade(String grade) {
        this.grade = grade;
    }

    public boolean isType() {
        return type;
    }

    public void setType(boolean type) {
        this.type = type;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public byte[] getIcon() {
        return icon;
    }

    public void setIcon(byte[] icon) {
        this.icon = icon;
    }

    public String getSignature() {
        if(signature!=null) {
            return signature;
        }
        else{return "";}
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPersonal_info() {
        if(personal_info!=null) {
            return personal_info;
        }
        else{return "";}
    }

    public void setPersonal_info(String personal_info) {
        this.personal_info = personal_info;
    }

    public boolean isVerification() {
        return verification;
    }

    public void setVerification(boolean verification) {
        this.verification = verification;
    }
}
