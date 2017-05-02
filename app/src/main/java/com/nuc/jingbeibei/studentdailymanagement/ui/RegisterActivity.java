package com.nuc.jingbeibei.studentdailymanagement.ui;

import android.content.SharedPreferences;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioGroup;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.app.ActivityCollector;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.beans.StudentClass;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;
import com.nuc.jingbeibei.studentdailymanagement.utils.IntentUtils;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;

public class RegisterActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private SharedPreferences.Editor editor;
    private boolean isTeacher;
    private EditText idAccountEdit;
    private EditText idPasswordEdit;
    private EditText idPhoneEdit;
    private EditText idYanzhengmaEdit;
    private Button idGetnumBtn;
    private RadioGroup idIscounselorRadioGroup;
    private Button idRegisterBtn;
    private LinearLayout idCounselorLayout;
    private String account, password, phone, yanzhengma, realName, classname;
    private EditText idRealNameEdit;
    private boolean isCounSelor = false;
    private AutoCompleteTextView idClassNameAutoText;
    private StudentClass studentClass;
    private ImageView idBackArrowImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActivityCollector.addActivity(this);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        isTeacher = pref.getBoolean("isTeacher", false);
        editor = pref.edit();
        initView();
        initEvent();
    }

    private void initView() {
        idAccountEdit = (EditText) findViewById(R.id.id_account_edit);
        idPasswordEdit = (EditText) findViewById(R.id.id_password_edit);
        idPhoneEdit = (EditText) findViewById(R.id.id_phone_edit);
        idYanzhengmaEdit = (EditText) findViewById(R.id.id_yanzhengma_edit);
        idGetnumBtn = (Button) findViewById(R.id.id_getvnum_btn);
        idIscounselorRadioGroup = (RadioGroup) findViewById(R.id.id_iscounselor_radiogroup);
        idRegisterBtn = (Button) findViewById(R.id.id_register_btn);
        idCounselorLayout = (LinearLayout) findViewById(R.id.id_counselor_layout);
        idRealNameEdit = (EditText) findViewById(R.id.id_realname_edit);
        idClassNameAutoText = (AutoCompleteTextView) findViewById(R.id.id_class_auto_text);
        idBackArrowImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        if (isTeacher) {
            idCounselorLayout.setVisibility(View.VISIBLE);
        }
        getClassName();
    }

    private void initEvent() {
        idRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                account = idAccountEdit.getText().toString();
                password = idPasswordEdit.getText().toString();
                phone = idPhoneEdit.getText().toString();
                yanzhengma = idYanzhengmaEdit.getText().toString();
                realName = idRealNameEdit.getText().toString();
                if (account.equals("") && password.equals("") && phone.equals("") && yanzhengma.equals("") && realName.equals("")) {

                    ToastUtils.toast(RegisterActivity.this, "所有信息不能为空");
                } else {
                    RegisterMethod();

                }
            }
        });

        idIscounselorRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, @IdRes int checkedId) {
                if (checkedId == R.id.id_iscounselor_true_radiobtn) {
                    isCounSelor = true;
                }
            }
        });
        idBackArrowImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(RegisterActivity.this);
            }
        });
    }

    private void RegisterMethod() {
        if (isTeacher) {
            Teacher teacher = new Teacher();
            teacher.setUserId(account);
            teacher.setPassword(password);
            teacher.setTelephoneNo(phone);
            teacher.setRealName(realName);
            teacher.setIsCounselor(isCounSelor);
            teacher.setRealName(realName);
            teacher.save(new SaveListener<String>() {

                @Override
                public void done(String objectId, BmobException e) {
                    if (e == null) {
                        ToastUtils.toast(RegisterActivity.this, "注册成功");
                        editor.putString("objectid", objectId);
                        editor.commit();
                        IntentUtils.doIntent(RegisterActivity.this, MainActivity.class);
                    } else {
                        ToastUtils.toast(RegisterActivity.this, "注册失败");
                    }
                }
            });

        } else {
            classname = idClassNameAutoText.getText().toString();
            if (!classname.equals("")) {
                getStudentClassByName(classname);//查询是否有这个学校

            } else {
                ToastUtils.toast(RegisterActivity.this, "班级不能为空");
            }
        }

    }


    public void getClassName() {//获取所有班级名
        BmobQuery<StudentClass> query = new BmobQuery<StudentClass>();
//返回100条数据，如果不加上这条语句，默认返回10条数据
        query.setLimit(100);
//执行查询方法
        query.findObjects(new FindListener<StudentClass>() {
            @Override
            public void done(List<StudentClass> object, BmobException e) {
                if (e == null) {
                    ArrayList<String> list = new ArrayList<String>();
                    for (StudentClass classname : object) {
                        list.add(classname.getClassNo());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<String>(RegisterActivity.this, R.layout.support_simple_spinner_dropdown_item);
                    adapter.addAll(list);
                    idClassNameAutoText.setAdapter(adapter);
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    public void getStudentClassByName(final String classname) {//查询班级ByName


        BmobQuery<StudentClass> query = new BmobQuery<StudentClass>();
        query.addWhereEqualTo("classNo", classname);
        //返回100条数据，如果不加上这条语句，默认返回10条数据
//        query.setLimit();
        //执行查询方法
        query.findObjects(new FindListener<StudentClass>() {
            @Override
            public void done(List<StudentClass> object, BmobException e) {
                if (e == null) {
                    if (object.size() != 0) {
                        studentClass = object.get(0);
                    }
                    if (studentClass == null) {//如果没有这个班级
                        addStudentClss(classname);//添加班级
                    } else {
                        addStudent(studentClass);//添加学生
                    }
                } else {
                    ToastUtils.toast(RegisterActivity.this, "bmob失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

    }

    public void addStudentClss(String classNo) {//创建一个班级
        final StudentClass studentClass = new StudentClass();
        studentClass.setClassNo(classNo);
        studentClass.save(new SaveListener<String>() {
            @Override
            public void done(String id, BmobException e) {
                if (e == null) {
                    studentClass.setObjectId(id);
                    addStudent(studentClass);
                } else {
                    ToastUtils.toast(RegisterActivity.this, "bmob失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });


    }

    public void addStudent(StudentClass studentclass) {
        Student student = new Student();
        student.setRealName(realName);
        student.setUserId(account);
        student.setRealName(realName);
        student.setTelephoneNo(phone);
        student.setPassword(password);
        student.setStudentClass(studentclass);
        student.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                if (e == null) {
                    editor.putString("objectid", s);
                    editor.commit();
                    ToastUtils.toast(RegisterActivity.this, "注册成功");
                    IntentUtils.doIntent(RegisterActivity.this, MainActivity.class);
                } else {
                    ToastUtils.toast(RegisterActivity.this, "注册失败");
                }
            }
        });

    }

}
