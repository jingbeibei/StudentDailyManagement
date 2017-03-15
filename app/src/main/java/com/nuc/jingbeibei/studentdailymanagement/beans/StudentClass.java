package com.nuc.jingbeibei.studentdailymanagement.beans;

import cn.bmob.v3.BmobObject;

/**
 * Created by jingbeibei on 2017/3/15.
 */

public class StudentClass extends BmobObject{
    private String classNo;//班级号

    public String getClassNo() {
        return classNo;
    }

    public void setClassNo(String classNo) {
        this.classNo = classNo;
    }
}
