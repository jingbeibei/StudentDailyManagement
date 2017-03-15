package com.nuc.jingbeibei.studentdailymanagement.beans;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by jingbeibei on 2017/3/15.
 */

public class SignType extends BmobObject{//签到类型
    private String title;//签到标题
    private String startTime;//开始时间
    private String endTime;//结束时间
    private Double latitude;//纬度
    private Double longitude;//经度
    private String location;//位置
    private Teacher publisher;//发布者
    private BmobRelation visibleClass;//可见班级

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public Teacher getPublisher() {
        return publisher;
    }

    public void setPublisher(Teacher publisher) {
        this.publisher = publisher;
    }

    public BmobRelation getVisibleClass() {
        return visibleClass;
    }

    public void setVisibleClass(BmobRelation visibleClass) {
        this.visibleClass = visibleClass;
    }
}
