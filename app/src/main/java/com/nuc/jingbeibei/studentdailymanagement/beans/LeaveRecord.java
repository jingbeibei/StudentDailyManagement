package com.nuc.jingbeibei.studentdailymanagement.beans;

import cn.bmob.v3.BmobObject;

/**
 * Created by jingbeibei on 2017/3/15.
 */

public class LeaveRecord extends BmobObject{//请假记录
    private String reason;//请假原因
    private String startTime;//开始时间
    private String endTime;//结束时间
    private String leaveTime;//销假时间
    private String parentName;//家长姓名
    private String parentTelephoneNo;//家长联系方式
    private Student student;//请假人
    private Teacher counselorName;//审批者

    public String getReason() {
        return reason;
    }

    public void setReason(String reason) {
        this.reason = reason;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public String getLeaveTime() {
        return leaveTime;
    }

    public void setLeaveTime(String leaveTime) {
        this.leaveTime = leaveTime;
    }

    public String getParentName() {
        return parentName;
    }

    public void setParentName(String parentName) {
        this.parentName = parentName;
    }

    public String getParentTelephoneNo() {
        return parentTelephoneNo;
    }

    public void setParentTelephoneNo(String parentTelephoneNo) {
        this.parentTelephoneNo = parentTelephoneNo;
    }

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public Teacher getCounselorName() {
        return counselorName;
    }

    public void setCounselorName(Teacher counselorName) {
        this.counselorName = counselorName;
    }
}
