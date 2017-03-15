package com.nuc.jingbeibei.studentdailymanagement.beans;

import cn.bmob.v3.BmobUser;

/**
 * Created by jingbeibei on 2017/3/15.
 */

public class Student extends BmobUser{
    private String myPassword;//密码
    private String studentId;//学号
    private String sex;//性别
    private String picpath;//头像路径
    private String telephoneNo;//手机号
    private String realName;//真实姓名
    private StudentClass studentClass;//所属班级

    public String getMyPassword() {
        return myPassword;
    }

    public void setMyPassword(String myPassword) {
        this.myPassword = myPassword;
    }

    public String getStudentId() {
        return studentId;
    }

    public void setStudentId(String studentId) {
        this.studentId = studentId;
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getPicpath() {
        return picpath;
    }

    public void setPicpath(String picpath) {
        this.picpath = picpath;
    }

    public String getTelephoneNo() {
        return telephoneNo;
    }

    public void setTelephoneNo(String telephoneNo) {
        this.telephoneNo = telephoneNo;
    }

    public String getRealName() {
        return realName;
    }

    public void setRealName(String realName) {
        this.realName = realName;
    }

    public StudentClass getStudentClass() {
        return studentClass;
    }

    public void setStudentClass(StudentClass studentClass) {
        this.studentClass = studentClass;
    }
}
