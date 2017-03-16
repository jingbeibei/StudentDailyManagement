package com.nuc.jingbeibei.studentdailymanagement.utils;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by jingbeibei on 2017/3/16.
 */

public class ToastUtils {
    public static void toast(Context context, String str){
        Toast.makeText(context, str, Toast.LENGTH_SHORT).show();
    }
}
