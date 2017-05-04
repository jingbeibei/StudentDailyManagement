package com.nuc.jingbeibei.studentdailymanagement.ui;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.app.ActivityCollector;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;
import com.nuc.jingbeibei.studentdailymanagement.utils.IntentUtils;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;

import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {
    private Button idLoginBtn;
    private EditText idAccountEdit;
    private EditText idPasswordEdit;
    private TextView idRegisterText;
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private boolean isTeacher;

    private ImageView idBackArrowImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityCollector.addActivity(this);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        editor=pref.edit();
        isTeacher = pref.getBoolean("isTeacher", false);

        initView();
        initEvent();
    }

    private void initEvent() {
        idLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String account = idAccountEdit.getText().toString();
                String password = idPasswordEdit.getText().toString();
                if (account == null || password == null) {
                    ToastUtils.toast(LoginActivity.this, "账号或密码不完整");
                    idLoginBtn.setClickable(true);
                    return;
                }
                idLoginBtn.setText("Loding..");
                idLoginBtn.setClickable(false);
                if (isTeacher) {
                    verifyUserForTeacher(account, password);
                } else {
                    verifyUserForStudent(account, password);
                }
            }
        });
        idRegisterText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.doIntent(LoginActivity.this, RegisterActivity.class);
            }
        });
        idBackArrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(LoginActivity.this);
            }
        });
    }

    private void initView() {
        idLoginBtn = (Button) findViewById(R.id.id_login_btn);
        idAccountEdit = (EditText) findViewById(R.id.id_account_edit);
        idPasswordEdit = (EditText) findViewById(R.id.id_passwordl_edit);
        idRegisterText = (TextView) findViewById(R.id.id_register_text);
        idBackArrowImage = (ImageView) findViewById(R.id.id_back_arrow_image);

    }

    private void verifyUserForStudent(String userId, String password) {//验证学生用户 坑啊改变不了泛型 md
        BmobQuery<Student> query = new BmobQuery<Student>();
        //查询数据
        query.addWhereEqualTo("userId", userId);
        query.addWhereEqualTo("password", password);
        //返回1条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(1);
        //执行查询方法
        query.findObjects(new FindListener<Student>() {
            @Override
            public void done(List<Student> object, BmobException e) {
                if (e == null) {
                    ToastUtils.toast(LoginActivity.this, "登录成功");
                    editor.putString("objectid",object.get(0).getObjectId());
                    editor.commit();
                    IntentUtils.doIntent(LoginActivity.this, MainActivity.class);
                } else {
                    ToastUtils.toast(LoginActivity.this, "用户名或密码错误");
                    idLoginBtn.setClickable(true);
                }
            }
        });

    }

    private void verifyUserForTeacher(String userId, String password) {//验证老师用户 坑啊改变不了泛型 md
        BmobQuery<Teacher> query = new BmobQuery<Teacher>();
        //查询数据
        query.addWhereEqualTo("userId", userId);
        query.addWhereEqualTo("password", password);
        //返回1条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(1);
        //执行查询方法
        query.findObjects(new FindListener<Teacher>() {
            @Override
            public void done(List<Teacher> object, BmobException e) {
                if (e == null) {
                    ToastUtils.toast(LoginActivity.this, "登录成功");
                    editor.putString("objectid",object.get(0).getObjectId());
                    editor.commit();
                    IntentUtils.doIntent(LoginActivity.this, MainActivity.class);
                } else {
                    ToastUtils.toast(LoginActivity.this, "用户名或密码错误");
                    idLoginBtn.setClickable(true);
                }
            }
        });

    }

}
