package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.app.ActivityCollector;
import com.nuc.jingbeibei.studentdailymanagement.beans.LeaveRecord;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class AskForLeaveDetailsActivity extends AppCompatActivity {
    private TextView studentNameTV, startTimeTV, endTimeTv, counselorNameTV;
    private TextView leaveReasonTV, parentNameTV, parentTelephoneNoTV, homeAddressTV,stateTV;
    private EditText replayContentEdit;
    private Button commitBtn;
    private RadioGroup redioGroup;
    private TextView BarTitle;
    private ImageView BackImage;

    private LeaveRecord leaveRecord;
    private SharedPreferences pref;
    private boolean isTeacher = false;
    private String isPass = "同意";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for_leave_details);
        ActivityCollector.addActivity(this);
        leaveRecord = (LeaveRecord) getIntent().getSerializableExtra("object");
        pref = getSharedPreferences("data", MODE_PRIVATE);
        isTeacher = pref.getBoolean("isTeacher", false);
        initView();
        initEvent();
    }


    private void initView() {
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        studentNameTV = (TextView) findViewById(R.id.id_student_name_text);
        startTimeTV = (TextView) findViewById(R.id.id_leave_start_time_text);
        endTimeTv = (TextView) findViewById(R.id.id_leave_end_time_text);
        counselorNameTV = (TextView) findViewById(R.id.id_leave_counselor_name_text);
        leaveReasonTV = (TextView) findViewById(R.id.id_leave_reason_edit);
        parentNameTV = (TextView) findViewById(R.id.id_parent_name_edit);
        parentTelephoneNoTV = (TextView) findViewById(R.id.id_parent_telephoneNo_edit);
        homeAddressTV = (TextView) findViewById(R.id.id_home_address_edit);
        replayContentEdit = (EditText) findViewById(R.id.id_leave_reply_content_edit);
        commitBtn = (Button) findViewById(R.id.id_leave_commit_button);
        redioGroup = (RadioGroup) findViewById(R.id.radioGroupID);
        stateTV= (TextView) findViewById(R.id.id_leave_state_text);

        studentNameTV.setText(leaveRecord.getStudent().getRealName());
        homeAddressTV.setText(leaveRecord.getHomeAddress());
        counselorNameTV.setText(leaveRecord.getCounselorName().getRealName());
        startTimeTV.setText(leaveRecord.getStartTime());
        endTimeTv.setText(leaveRecord.getEndTime());
        parentNameTV.setText(leaveRecord.getParentName());
        parentTelephoneNoTV.setText(leaveRecord.getParentTelephoneNo());
        leaveReasonTV.setText(leaveRecord.getReason());
        stateTV.setText(leaveRecord.getState());
        BarTitle.setText("请假详情");

        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(AskForLeaveDetailsActivity.this);
            }
        });

        if (isTeacher&&leaveRecord.getState().equals("审批中")){
            replayContentEdit.setEnabled(true);
            redioGroup.setVisibility(View.VISIBLE);
            commitBtn.setVisibility(View.VISIBLE);
        }

    }

    private void initEvent() {
        redioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.passGroupID) {
                    isPass = "同意";
                } else {
                    isPass = "拒绝";
                }
            }
        });
        commitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                leaveRecord.setReplyContent(replayContentEdit.getText().toString());
                leaveRecord.setState(isPass);
                leaveRecord.update(new UpdateListener() {
                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            ToastUtils.toast(AskForLeaveDetailsActivity.this, "审批完成");
                            finish();
                        }
                    }
                });
            }
        });
    }


}
