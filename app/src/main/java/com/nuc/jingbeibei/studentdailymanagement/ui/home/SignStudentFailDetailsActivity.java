package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.app.ActivityCollector;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignRecord;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignType;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class SignStudentFailDetailsActivity extends AppCompatActivity {
    private TextView BarTitle;
    private ImageView BackImage;
    private TextView titleTV, startTV, endTV, publisherAddressTV, studentAddressTV, signTimeTV, isSuccessTV;
    private Button signBtn;
    private SignRecord signRecord;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_student_fail_details);
        ActivityCollector.addActivity(this);
       signRecord= (SignRecord) getIntent().getSerializableExtra("record");
        initView();
        initEvent();
    }

    private void initView() {
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        titleTV = (TextView) findViewById(R.id.id_sign_title_text);
        startTV = (TextView) findViewById(R.id.id_sign_start_time_text);
        endTV = (TextView) findViewById(R.id.id_sign_end_time_text);
        publisherAddressTV = (TextView) findViewById(R.id.id_sign_publisher_adress_text);
        studentAddressTV = (TextView) findViewById(R.id.id_sign_student_address_text);
        signTimeTV = (TextView) findViewById(R.id.id_sign_student_time_text);
        signBtn = (Button) findViewById(R.id.id_sign_btn);
        isSuccessTV = (TextView) findViewById(R.id.id_sign_is_success_text);
        BarTitle.setText("签到详情");

        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(SignStudentFailDetailsActivity.this);
            }
        });
        titleTV.setText(signRecord.getSignType().getTitle());
        startTV.setText(signRecord.getSignType().getStartTime());
        endTV.setText(signRecord.getSignType().getEndTime());
        publisherAddressTV.setText(signRecord.getSignType().getLocation());
        studentAddressTV.setText(signRecord.getLocation());
        isSuccessTV.setText(signRecord.getIsSuccess() == 0 ? "签到失败" : "签到成功");

    }

    private void initEvent() {
        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateSignRecord();
            }
        });

    }

    private void updateSignRecord() {
        signRecord.setIsSuccess(1);
        signRecord.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e==null){
                    ToastUtils.toast(SignStudentFailDetailsActivity.this,"修改成功");
                }else {
                    ToastUtils.toast(SignStudentFailDetailsActivity.this,"修改失败");
                }
            }
        });
    }
}
