package com.nuc.jingbeibei.studentdailymanagement.ui;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.app.ActivityCollector;
import com.nuc.jingbeibei.studentdailymanagement.utils.IntentUtils;

import cn.bmob.v3.Bmob;

public class LoginAndRegisterActivity extends AppCompatActivity {
    private TextView idStudentText;
    private TextView idTeacherText;
    private SharedPreferences.Editor editor;
    private SharedPreferences pref;
    private String objectId = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_register);
        idStudentText = (TextView) findViewById(R.id.id_student_text);
        idTeacherText = (TextView) findViewById(R.id.id_teacher_text);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        objectId = pref.getString("objectid", "");
        ActivityCollector.addActivity(this);
        editor = pref.edit();

        // 初始化 Bmob SDK
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(this, "498a74ae317129a631c92070b28079d4");
        if (!objectId.equals("")) {
            IntentUtils.doIntent(LoginAndRegisterActivity.this, MainActivity.class);
        }

        initEvent();

    }

    private void initEvent() {

        idStudentText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("isTeacher", false);
                editor.commit();

                IntentUtils.doIntent(LoginAndRegisterActivity.this, LoginActivity.class);

            }
        });
        idTeacherText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editor.putBoolean("isTeacher", true);
                editor.commit();
                IntentUtils.doIntent(LoginAndRegisterActivity.this, LoginActivity.class);
            }
        });

    }

}
