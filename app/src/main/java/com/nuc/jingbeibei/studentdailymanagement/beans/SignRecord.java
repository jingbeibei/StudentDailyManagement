package com.nuc.jingbeibei.studentdailymanagement.beans;

import cn.bmob.v3.BmobObject;

/**
 * Created by jingbeibei on 2017/3/15.
 */

public class SignRecord extends BmobObject{//学生的签到记录表
    private Student student;//学生
    private SignType signType;//签到类型
    private String signTime;//签到时间
    private Double latitude;//纬度
    private Double longitude;//经度
    private String location;//位置
    private Integer isSuccess;//签到是否成功 1：成功   0：失败

    public Student getStudent() {
        return student;
    }

    public void setStudent(Student student) {
        this.student = student;
    }

    public SignType getSignType() {
        return signType;
    }

    public void setSignType(SignType signType) {
        this.signType = signType;
    }

    public String getSignTime() {
        return signTime;
    }

    public void setSignTime(String signTime) {
        this.signTime = signTime;
    }

    public Double getLatitude() {
        return latitude;
    }

    public void setLatitude(Double latitude) {
        this.latitude = latitude;
    }

    public Double getLongitude() {
        return longitude;
    }

    public void setLongitude(Double longitude) {
        this.longitude = longitude;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Integer getIsSuccess() {
        return isSuccess;
    }

    public void setIsSuccess(Integer isSuccess) {
        this.isSuccess = isSuccess;
    }
}
