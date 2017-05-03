package com.nuc.jingbeibei.studentdailymanagement.ui.setting;

import android.content.SharedPreferences;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.app.ActivityCollector;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

public class ModifyPasswordActivity extends AppCompatActivity {
    private EditText oldPasswordET;
    private EditText newPasswordET;
    private EditText confirmPasswordET;
    private TextView barTitle;
    private ImageView backIV;
    private Button modifyPasswordBtn;

    private SharedPreferences pref;
    private boolean isTeacher = false;
    private Student student;
    private Teacher teacher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify_password);
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
        oldPasswordET = (EditText) findViewById(R.id.password_old_et);
        newPasswordET = (EditText) findViewById(R.id.password_new_et);
        confirmPasswordET = (EditText) findViewById(R.id.password_confirm_et);
        modifyPasswordBtn = (Button) findViewById(R.id.modify_password_btn);
        barTitle = (TextView) findViewById(R.id.id_bar_title);
        backIV = (ImageView) findViewById(R.id.id_back_arrow_image);
        barTitle.setText("修改密码");
    }

    private void initEvent() {
        modifyPasswordBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String oldPassworld = oldPasswordET.getText().toString();
                String newPassword = newPasswordET.getText().toString();
                String confirmPassword = confirmPasswordET.getText().toString();
                if (oldPassworld.equals("") || newPassword.equals("") || confirmPassword.equals("")) {
                    Snackbar.make(confirmPasswordET, "亲，密码不能为空哦！", Snackbar.LENGTH_LONG);
                    return;
                }
                if (!newPassword.equals(confirmPassword)) {
                    Snackbar.make(confirmPasswordET, "亲，密码不一致哦！", Snackbar.LENGTH_LONG);
                    return;
                }
                modifyPasswordBtn.setText("修改中...");
                modifyPasswordBtn.setClickable(false);
                if (isTeacher) {
                    updateTeacherPwd(oldPassworld,newPassword);
                } else {
                    updateStudentPwd(oldPassworld,newPassword);
                }


            }
        });
    }

    private void updateStudentPwd(String oldPassworld, String newPassword) {
        if (oldPassworld.equals(student.getPassword())){
            student.setPassword(newPassword);
            student.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e==null){
                        ToastUtils.toast(ModifyPasswordActivity.this,"修改成功");
                        modifyPasswordBtn.setClickable(true);
                        modifyPasswordBtn.setText("修改");
                    }else {
                        ToastUtils.toast(ModifyPasswordActivity.this,"修改失败");
                        modifyPasswordBtn.setClickable(true);
                        modifyPasswordBtn.setText("修改");
                    }
                }
            });
        }else {
            ToastUtils.toast(ModifyPasswordActivity.this,"旧密码错误");
        }
    }

    private void updateTeacherPwd(String oldPassworld, String newPassword) {
        if (oldPassworld.equals(teacher.getPassword())){
            teacher.setPassword(newPassword);
            teacher.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e==null){
                        ToastUtils.toast(ModifyPasswordActivity.this,"修改成功");
                        modifyPasswordBtn.setClickable(true);
                        modifyPasswordBtn.setText("修改");
                    }else {
                        ToastUtils.toast(ModifyPasswordActivity.this,"修改失败");
                        modifyPasswordBtn.setClickable(true);
                        modifyPasswordBtn.setText("修改");
                    }
                }
            });
        }else {
            ToastUtils.toast(ModifyPasswordActivity.this,"旧密码错误");
        }
    }

}
