package com.nuc.jingbeibei.studentdailymanagement.broadcast;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.ui.LoginAndRegisterActivity;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by jingbeibei on 2017/5/16.
 */

public class MyPushMessageReceiver extends BroadcastReceiver {
    private String msg="";
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("bmob", "客户端收到推送内容："+intent.getStringExtra("msg"));
//        ToastUtils.toast(context,"客户端收到推送内容："+intent.getStringExtra("msg"));
        String s=intent.getStringExtra("msg");
        try {
            JSONObject jsonObject=new JSONObject(s);
          msg=  jsonObject.getString("alert");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        NotificationManager manager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context);
        Intent myintent = new Intent(context,LoginAndRegisterActivity.class);
        myintent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        PendingIntent pIntent = PendingIntent.getActivity(context,0,myintent,PendingIntent.FLAG_UPDATE_CURRENT);

        Notification notification = builder
                .setContentTitle("您有一条新的通知")
                .setContentText(msg+"")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(R.mipmap.ic_launcher)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher))
                .setColor(Color.parseColor("#EAA935"))
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pIntent)
                .build();
        manager.notify(1, notification);


    }
}
