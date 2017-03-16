package com.nuc.jingbeibei.studentdailymanagement.app;

import android.app.Activity;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jingbeibei on 2017/3/16.
 */

public class ActivityCollector {
    public static List<Activity> list = new ArrayList<>();
    public static void addActivity(Activity activity){
        list.add(activity);
    }
    public static void removeActivity(Activity activity){
        list.remove(activity);
        activity.finish();
    }
    public static void finishAll(){
        for (Activity activity : list){
            if (!activity.isFinishing()){
                activity.finish();
            }
        }
    }
}
