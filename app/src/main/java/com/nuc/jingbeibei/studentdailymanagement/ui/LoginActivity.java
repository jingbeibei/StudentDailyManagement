package com.nuc.jingbeibei.studentdailymanagement.ui;

import android.content.SharedPreferences;
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
import com.nuc.jingbeibei.studentdailymanagement.utils.IntentUtils;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class LoginActivity extends AppCompatActivity {
    private Button idLoginBtn;
    private EditText idAccountEdit;
    private EditText idPasswordEdit;
    private TextView idRegisterText;
    private SharedPreferences pref;
    private boolean isTeacher;
    private BmobUser bmobUser;
    private ImageView idBackArrowImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ActivityCollector.addActivity(this);
        pref=getSharedPreferences("data",MODE_PRIVATE);
        isTeacher = pref.getBoolean("isTeacher", false);
        if (isTeacher) {
            bmobUser = new Teacher();
        } else {
            bmobUser = new Student();
        }
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
                bmobUser.setUsername(account);
                bmobUser.setPassword(password);
                bmobUser.login(new SaveListener<BmobUser>() {

                    @Override
                    public void done(BmobUser bmobUser, BmobException e) {
                        if (e == null) {
                            ToastUtils.toast(LoginActivity.this, "登录成功:");
                            IntentUtils.doIntent(LoginActivity.this, MainActivity.class);
                            //通过BmobUser user = BmobUser.getCurrentUser()获取登录成功后的本地用户信息
                            //如果是自定义用户对象MyUser，可通过MyUser user = BmobUser.getCurrentUser(MyUser.class)获取自定义用户信息
                        } else {
                            ToastUtils.toast(LoginActivity.this, "登录失败:" + e.toString());
                            idLoginBtn.setClickable(true);
                        }
                    }
                });

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
}
