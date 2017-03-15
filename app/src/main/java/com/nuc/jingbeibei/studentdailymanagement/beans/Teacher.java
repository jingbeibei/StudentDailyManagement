package com.nuc.jingbeibei.studentdailymanagement.beans;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by jingbeibei on 2017/3/15.
 */

public class Teacher extends BmobUser{
    private String myPassword;//密码
    private String teacherId;//教工号
    private String sex;//性别
    private String picpath;//头像路径
    private String telephoneNo;//手机号
    private String realName;//真实姓名
    private BmobRelation holdClass;//所教学的班级  bmob多对多关系类型
    private Integer isCounselor;// 1:是  0：不是   是不是辅导员

    public String getMyPassword() {
        return myPassword;
    }

    public void setMyPassword(String myPassword) {
        this.myPassword = myPassword;
    }

    public String getTeacherId() {
        return teacherId;
    }

    public void setTeacherId(String teacherId) {
        this.teacherId = teacherId;
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

    public BmobRelation getHoldClass() {
        return holdClass;
    }

    public void setHoldClass(BmobRelation holdClass) {
        this.holdClass = holdClass;
    }

    public Integer getIsCounselor() {
        return isCounselor;
    }

    public void setIsCounselor(Integer isCounselor) {
        this.isCounselor = isCounselor;
    }
}
