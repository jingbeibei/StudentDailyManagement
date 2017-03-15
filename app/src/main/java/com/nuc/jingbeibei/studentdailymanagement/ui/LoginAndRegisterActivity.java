package com.nuc.jingbeibei.studentdailymanagement.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.utils.IntentUtils;

import cn.bmob.v3.Bmob;

public class LoginAndRegisterActivity extends AppCompatActivity {
    private TextView idLoginText;
    private TextView idRegisterText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_and_register);
        idLoginText= (TextView) findViewById(R.id.id_login_text);
        idRegisterText= (TextView) findViewById(R.id.id_register_text);

        // 初始化 Bmob SDK
        // 使用时请将第二个参数Application ID替换成你在Bmob服务器端创建的Application ID
        Bmob.initialize(this, "498a74ae317129a631c92070b28079d4");

        initEvent();

    }

    private void initEvent() {

        idLoginText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.doIntent(LoginAndRegisterActivity.this,LoginActivity.class);
            }
        });
        idRegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.doIntent(LoginAndRegisterActivity.this,RegisterActivity.class);
            }
        });

    }

}
