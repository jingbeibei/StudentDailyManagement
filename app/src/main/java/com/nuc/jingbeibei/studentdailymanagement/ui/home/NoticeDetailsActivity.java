package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.Notice;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class NoticeDetailsActivity extends AppCompatActivity {
    private Notice notice;
    private TextView noticeTitleTV;
    private TextView noticeContentTV;
    private TextView noticeReleaseTimeTV;
    private TextView noticePublisherTV;
    private Button deleteNoticeBtn;
    private SharedPreferences pref;
    private boolean isTeacher;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice_details);
       notice = (Notice) getIntent().getSerializableExtra("notice");
        pref = getSharedPreferences("data", MODE_PRIVATE);
        isTeacher = pref.getBoolean("isTeacher", false);
        initView();
        initEvent();
    }

    private void initEvent() {
        deleteNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                notice.delete(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e==null){
                            ToastUtils.toast(NoticeDetailsActivity.this,"删除公告成功");
                            finish();
                        }else {
                            ToastUtils.toast(NoticeDetailsActivity.this,"删除公告失败");
                        }
                    }
                });
            }
        });
    }

    private void initView() {
        noticeTitleTV= (TextView) findViewById(R.id.id_notice_title_text);
        noticeContentTV= (TextView) findViewById(R.id.id_notice_content_text);
        noticeReleaseTimeTV= (TextView) findViewById(R.id.id_notice_releaseTime_text);
        noticePublisherTV= (TextView) findViewById(R.id.id_notice_publisher_text);
        deleteNoticeBtn= (Button) findViewById(R.id.id_delect_notice_btn);
        if (isTeacher)
            deleteNoticeBtn.setVisibility(View.VISIBLE);
        String title=notice.getTitle();
        noticeTitleTV.setText(title);
        noticeContentTV.setText(notice.getContent());
        noticeReleaseTimeTV.setText(notice.getReleaseTime());
        noticePublisherTV.setText(notice.getPublisher().getRealName());
    }
}
