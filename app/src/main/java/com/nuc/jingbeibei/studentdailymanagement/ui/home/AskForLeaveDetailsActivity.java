package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.LeaveRecord;

public class AskForLeaveDetailsActivity extends AppCompatActivity {
    private TextView studentNameTV, startTimeTV, endTimeTv, counselorNameTV;
    private TextView leaveReasonTV, parentNameTV, parentTelephoneNoTV, homeAddressTV;

    private LeaveRecord leaveRecord;
    private SharedPreferences pref;
    private boolean isTeacher = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for_leave_details);
        leaveRecord = (LeaveRecord) getIntent().getSerializableExtra("object");
        pref = getSharedPreferences("data", MODE_PRIVATE);
        isTeacher = pref.getBoolean("isTeacher", false);
        initView();
    }

    private void initView() {
        studentNameTV = (TextView) findViewById(R.id.id_student_name_text);
        startTimeTV = (TextView) findViewById(R.id.id_leave_start_time_text);
        endTimeTv = (TextView) findViewById(R.id.id_leave_end_time_text);
        counselorNameTV = (TextView) findViewById(R.id.id_leave_counselor_name_text);
        leaveReasonTV = (TextView) findViewById(R.id.id_leave_reason_edit);
        parentNameTV = (TextView) findViewById(R.id.id_parent_name_edit);
        parentTelephoneNoTV = (TextView) findViewById(R.id.id_parent_telephoneNo_edit);
        homeAddressTV = (TextView) findViewById(R.id.id_home_address_edit);
        studentNameTV.setText(leaveRecord.getStudent().getRealName());
        homeAddressTV.setText(leaveRecord.getHomeAddress());
        counselorNameTV.setText(leaveRecord.getCounselorName().getRealName());
    }
}
