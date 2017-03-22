package com.nuc.jingbeibei.studentdailymanagement.utils;

import android.util.Log;

import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

/**
 * Created by jingbeibei on 2017/3/22.
 */

public class GetObjectSingleton {
    private static volatile Teacher teacherInstance=null;
    private GetObjectSingleton() {}
    public static Teacher getTeacherInstance( String objectId){
        if (teacherInstance == null) {
            synchronized (GetObjectSingleton.class) {
                if (teacherInstance == null) {
//                    teacherInstance = new Singleton();
                    BmobQuery<Teacher> query = new BmobQuery<Teacher>();
                    query.getObject(objectId, new QueryListener<Teacher>() {

                        @Override
                        public void done(Teacher object, BmobException e) {
                            if(e==null){
                                teacherInstance=object;
                            }else{
                                Log.i("bmob","失败："+e.getMessage()+","+e.getErrorCode());
                            }
                        }

                    });


                }
            }
        }
        return teacherInstance;
    }
}
