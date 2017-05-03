package com.nuc.jingbeibei.studentdailymanagement.ui.setting;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.app.ActivityCollector;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

import static java.security.AccessController.getContext;

public class ModifyDataActivity extends AppCompatActivity {
    private EditText sexET;
    private EditText phoneET;
    private Button modifyBtn;
    private TextView barTitle;
    private ImageView backIV;

    private SharedPreferences pref;
    private boolean isTeacher = false;
    private Student student;
    private Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_data);
        ActivityCollector.addActivity(this);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        isTeacher = pref.getBoolean("isTeacher", false);
        if (isTeacher) {
            teacher = (Teacher) getIntent().getSerializableExtra("object");
        } else {
            student = (Student) getIntent().getSerializableExtra("object");
        }

        initView();
        initEvent();
    }

    private void initView() {
        sexET = (EditText) findViewById(R.id.data_sex_et);
        phoneET = (EditText) findViewById(R.id.data_phone_et);
        modifyBtn = (Button) findViewById(R.id.modify_data_btn);
        barTitle = (TextView) findViewById(R.id.id_bar_title);
        backIV = (ImageView) findViewById(R.id.id_back_arrow_image);
        barTitle.setText("修改资料");
        if (isTeacher) {
            sexET.setText(teacher.getSex() + "");
            phoneET.setText(teacher.getTelephoneNo() + "");
        } else {
            sexET.setText(student.getSex() + "");
            phoneET.setText(student.getTelephoneNo() + "");
        }
    }


    private void initEvent() {
        modifyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sex = sexET.getText().toString();
                String phone = phoneET.getText().toString();
                if (sex.equals("") || phone.equals("")) {
                    ToastUtils.toast(ModifyDataActivity.this, "输入不能为空");
                } else {
                    modifyBtn.setText("修改中...");
                    modifyBtn.setClickable(false);
                    if (isTeacher) {
                        updateTeacherInf(sex, phone);
                    } else {
                        updateStudentInf(sex, phone);
                    }
                }
            }
        });
        backIV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(ModifyDataActivity.this);
            }
        });
    }

    private void updateStudentInf(String sex, String phone) {
        student.setSex(sex);
        student.setTelephoneNo(phone);
        student.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e==null){
                    ToastUtils.toast(ModifyDataActivity.this,"修改成功");
                    modifyBtn.setClickable(true);
                    modifyBtn.setText("修改");
                }else {
                    ToastUtils.toast(ModifyDataActivity.this,"修改失败");
                    modifyBtn.setClickable(true);
                    modifyBtn.setText("修改");
                }
            }
        });
    }

    private void updateTeacherInf(String sex, String phone) {
        teacher.setSex(sex);
        teacher.setTelephoneNo(phone);
        teacher.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if(e==null){
                    ToastUtils.toast(ModifyDataActivity.this,"修改成功");
                    modifyBtn.setClickable(true);
                    modifyBtn.setText("修改");
                }else {
                    ToastUtils.toast(ModifyDataActivity.this,"修改失败");
                    modifyBtn.setClickable(true);
                    modifyBtn.setText("修改");
                }
            }
        });
    }

}
