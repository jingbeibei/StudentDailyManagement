package com.nuc.jingbeibei.studentdailymanagement.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

/**
 * Created by jingbeibei on 2017/3/15.
 */

public class IntentUtils {

    public static void doIntent(Context context, Class<?> activity){
        Intent intent = new Intent(context, activity);
        context.startActivity(intent);
    }

    public static void doIntentForResult(Activity context, Class<?> activity, int requestCode){
        Intent intent = new Intent(context, activity);
        context.startActivityForResult(intent, requestCode);
    }
    public static void doIntentWithBundle(Context context, Class<?> activity, Bundle b){
        Intent intent = new Intent(context, activity);
        intent.putExtras( b);
        context.startActivity(intent);
    }
    public static void doIntentWithString(Context context, Class<?> activity, String key, String values){
        Intent intent = new Intent(context, activity);
        intent.putExtra(key, values);
        context.startActivity(intent);
    }
    public static void doIntentWithStrForResult(Activity context, Class<?> activity, String key, String values, int requestCode){
        Intent intent = new Intent(context, activity);
        intent.putExtra(key, values);
        context.startActivityForResult(intent, requestCode);
    }
    public static void doIntentWithStrsForResult(Activity context, Class<?> activity, String key, String values, String key1, String values1,int requestCode){
        Intent intent = new Intent(context, activity);
        intent.putExtra(key, values);
        intent.putExtra(key1, values1);
        context.startActivityForResult(intent, requestCode);
    }
}
