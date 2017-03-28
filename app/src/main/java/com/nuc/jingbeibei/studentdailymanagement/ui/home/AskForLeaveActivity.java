package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.LeaveRecord;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.utils.IntentUtils;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class AskForLeaveActivity extends AppCompatActivity {
    private TextView studentNameTV, startTimeTV, endTimeTv, counselorNameTV;
    private EditText leaveReasonEdit, parentNameEdit, parentTelephoneNoEdit, homeAddressEdit;
    private Button leaveCommitBtn;
    private Student student;
    private Button leaveRecordBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_for_leave);
        student = (Student) getIntent().getSerializableExtra("object");

        initView();
        initEvent();
    }

    private void initView() {
        studentNameTV = (TextView) findViewById(R.id.id_student_name_text);
        startTimeTV = (TextView) findViewById(R.id.id_leave_start_time_text);
        endTimeTv = (TextView) findViewById(R.id.id_leave_end_time_text);
        counselorNameTV = (TextView) findViewById(R.id.id_leave_counselor_name_text);
        leaveReasonEdit = (EditText) findViewById(R.id.id_leave_reason_edit);
        parentNameEdit = (EditText) findViewById(R.id.id_parent_name_edit);
        parentTelephoneNoEdit = (EditText) findViewById(R.id.id_parent_telephoneNo_edit);
        homeAddressEdit = (EditText) findViewById(R.id.id_home_address_edit);
        leaveCommitBtn = (Button) findViewById(R.id.id_ask_for_leave_commit_btn);
        leaveRecordBtn= (Button) findViewById(R.id.id_leave_record_btn);

        studentNameTV.setText(student.getRealName());
        counselorNameTV.setText(student.getStudentClass().getCounselor().getRealName());
    }

    private void initEvent() {
        leaveCommitBtn.setOnClickListener(new View.OnClickListener() {//提交事件
            @Override
            public void onClick(View v) {
             String startTime=startTimeTV.getText().toString();
                String endTime=endTimeTv.getText().toString();
                String reason=leaveReasonEdit.getText().toString();
                String pareatName=parentNameEdit.getText().toString();
                String pareatTelephoneNo=parentTelephoneNoEdit.getText().toString();
                String homeAddress=homeAddressEdit.getText().toString();
                if (startTime.equals("")||endTime.equals("")||reason.equals("")||pareatName.equals("")||pareatTelephoneNo.equals("")||homeAddress.equals("")){
                    ToastUtils.toast(AskForLeaveActivity.this,"输入信息不完整，请检查！");
                }else {
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    try {
                        if (format.parse(startTime).getTime()<format.parse(endTime).getTime()){//起始时间小于结束时间
                            commitLeave(startTime,endTime,reason,pareatName,pareatTelephoneNo,homeAddress);
                        }else {
                            ToastUtils.toast(AskForLeaveActivity.this,"日期范围不正确");
                        }
                    } catch (ParseException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        startTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//时间选择器
                TimePickerView pvTime = new TimePickerView.Builder(AskForLeaveActivity.this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {//选中事件回调
                        startTimeTV.setText(getTime(date));
                    }
                })
                        .build();
                pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
                pvTime.show();
            }
        });
        endTimeTv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//时间选择器
                TimePickerView pvTime = new TimePickerView.Builder(AskForLeaveActivity.this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date,View v) {//选中事件回调
                        endTimeTv.setText(getTime(date));
                    }
                })
                        .build();
                pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
                pvTime.show();
            }
        });

        leaveRecordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.doIntentWithObject(AskForLeaveActivity.this,AskLeaveRecordListActivity.class,"object",student);
            }
        });
    }

    private void commitLeave(String startTime,String endTime,String reason,String pareatName,String pareatTelephoneNo,String homeAddress) {//提交请假信息
        LeaveRecord leaveRecord=new LeaveRecord();
        leaveRecord.setStudent(student);
        leaveRecord.setStartTime(startTime);
        leaveRecord.setEndTime(endTime);
        leaveRecord.setReason(reason);
        leaveRecord.setParentName(pareatName);
        leaveRecord.setParentTelephoneNo(pareatTelephoneNo);
        leaveRecord.setHomeAddress(homeAddress);
        leaveRecord.setCounselorName(student.getStudentClass().getCounselor());
        leaveRecord.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e==null){
                    ToastUtils.toast(AskForLeaveActivity.this,"提交成功等待老师审核");
                }
            }
        });

    }
    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }
}
