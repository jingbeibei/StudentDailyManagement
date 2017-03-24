package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;
import com.nuc.jingbeibei.studentdailymanagement.utils.IntentUtils;

import cn.bmob.v3.BmobObject;

public class NoticeActivity extends AppCompatActivity {
private Button idPublishNoticeBtn;
    private BmobObject bmobObject;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        bmobObject = (BmobObject) getIntent().getSerializableExtra("object");
        initView();
        initEvent();
    }

    private void initView() {
        idPublishNoticeBtn= (Button) findViewById(R.id.id_publish_notice_btn);
    }

    private void initEvent() {
        idPublishNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.doIntentWithObject(NoticeActivity.this,publishNoticeActivity.class,"object",(Teacher)bmobObject);
            }
        });
    }
}
