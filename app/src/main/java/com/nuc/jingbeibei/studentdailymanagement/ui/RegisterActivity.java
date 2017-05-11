package com.nuc.jingbeibei.studentdailymanagement.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.IdRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextUtils;
import android.text.format.Time;
import android.text.style.ForegroundColorSpan;
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
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.BmobSMS;
import cn.bmob.v3.BmobUser;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

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

    private TimeCount mTiemTimeCount;
    //短信验证码内容 验证码是6位数字的格式
    private String strContent;
    private String patternCoder = "(?<!\\d)\\d{6}(?!\\d)";

    //填写服务器号码
    private static final String SERVICECHECKNUM = "";

    //更新界面
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            if (idYanzhengmaEdit != null) {
                idYanzhengmaEdit.setText(strContent);
            }
        }

    };
    //监听短信广播
    private BroadcastReceiver smsReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Object[] objs = (Object[]) intent.getExtras().get("pdus");
            for (Object obj : objs) {
                byte[] pdu = (byte[]) obj;
                SmsMessage sms = SmsMessage.createFromPdu(pdu);
                // 短信的内容
                String message = sms.getMessageBody();
                Log.d("TAG", "message     " + message);
                String from = sms.getOriginatingAddress();
                Log.d("TAG", "from     " + from);
                if (SERVICECHECKNUM.equals(from.toString().trim()) || TextUtils.isEmpty(SERVICECHECKNUM)) {
                    Time time = new Time();
                    time.set(sms.getTimestampMillis());
                    String time2 = time.format3339(true);
                    Log.d("TAG", from + "   " + message + "  " + time2);
                    strContent = from + "   " + message;
                    //mHandler.sendEmptyMessage(1);
                    if (!TextUtils.isEmpty(from)) {
                        String code = patternCode(message);
                        if (!TextUtils.isEmpty(code)) {
                            strContent = code;
                            mHandler.sendEmptyMessage(1);
                        }
                    }
                } else {
                    return;
                }
            }

        }
    };
    /**
     * 匹配短信中间的6个数字（验证码等）
     *
     * @param patternContent
     * @return
     */
    private String patternCode(String patternContent) {
        if (TextUtils.isEmpty(patternContent)) {
            return null;
        }
        Pattern p = Pattern.compile(patternCoder);
        Matcher matcher = p.matcher(patternContent);
        if (matcher.find()) {
            return matcher.group();
        }
        return null;
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ActivityCollector.addActivity(this);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        isTeacher = pref.getBoolean("isTeacher", false);
        editor = pref.edit();
        mTiemTimeCount = new TimeCount(60000, 1000);
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
            idClassNameAutoText.setVisibility(View.GONE);
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
                    BmobSMS.verifySmsCode(phone, yanzhengma, new UpdateListener() {
                        @Override
                        public void done(BmobException e) {
                            if (e==null){
                                RegisterMethod();
                            }
                        }
                    });

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
        idGetnumBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentFilter filter = new IntentFilter();
                filter.addAction("android.provider.Telephony.SMS_RECEIVED");
                filter.setPriority(Integer.MAX_VALUE);
                registerReceiver(smsReceiver, filter);
//                mTiemTimeCount.start();
                BmobSMS.requestSMSCode( idPhoneEdit.getText().toString(),"短信验证码", new QueryListener<Integer>() {
                    @Override
                    public void done(Integer integer, BmobException e) {
                            if (e==null){
                                mTiemTimeCount.start();
                            }
                    }
                });
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
                       ActivityCollector.removeActivity(RegisterActivity.this);
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
                    ActivityCollector.removeActivity(RegisterActivity.this);
                } else {
                    ToastUtils.toast(RegisterActivity.this, "注册失败");
                }
            }
        });

    }
    //计时重发
    private class TimeCount extends CountDownTimer {

        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);
        }

        @Override
        public void onTick(long millisUntilFinished) {
            idGetnumBtn.setClickable(false);
            idGetnumBtn.setText(millisUntilFinished / 1000 + "秒后重新发送");
            Log.i("------log-----",millisUntilFinished / 1000 + "秒后重新发送");

            Spannable span = new SpannableString(idGetnumBtn.getText().toString());//获取按钮的文字
            span.setSpan(new ForegroundColorSpan(Color.YELLOW), 0, 2, Spannable.SPAN_INCLUSIVE_EXCLUSIVE);//讲倒计时时间显示为红色
            idGetnumBtn.setText(span);
        }

        @Override
        public void onFinish() {
            idGetnumBtn.setText("获取验证码");
            idGetnumBtn.setClickable(true);
        }
    }
    @Override
    protected void onDestroy() {
        try {
            unregisterReceiver(smsReceiver);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        super.onDestroy();
    }
}
