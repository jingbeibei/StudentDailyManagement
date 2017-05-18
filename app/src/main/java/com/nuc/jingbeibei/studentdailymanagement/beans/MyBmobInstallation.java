package com.nuc.jingbeibei.studentdailymanagement.beans;

import cn.bmob.v3.BmobInstallation;

/**
 * Created by jingbeibei on 2017/5/16.
 */

public class MyBmobInstallation extends BmobInstallation {
    private String className;
    private Integer floag;//0:老师，1;学生
    private String name;

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public int getFloag() {
        return floag;
    }

    public void setFloag(int floag) {
        this.floag = floag;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
