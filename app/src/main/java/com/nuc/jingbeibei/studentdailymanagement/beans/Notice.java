package com.nuc.jingbeibei.studentdailymanagement.beans;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.datatype.BmobRelation;

/**
 * Created by jingbeibei on 2017/3/15.
 */

public class Notice extends BmobObject{//公告
    private String title;//标题
    private String content;//内容
    private String releaseTime;//发布时间
    private Teacher publisher;//发布者
    private BmobRelation visibleClass;//可见班级

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getReleaseTime() {
        return releaseTime;
    }

    public void setReleaseTime(String releaseTime) {
        this.releaseTime = releaseTime;
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
