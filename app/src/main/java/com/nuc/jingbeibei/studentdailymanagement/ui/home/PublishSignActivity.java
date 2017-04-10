package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.StudentClass;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class PublishSignActivity extends AppCompatActivity {
    private TextView teacherNameTV, startTimeTV, endTimeTV, selectClassTV, myPlaceTV;
    private EditText signtitleET;
    private Teacher teacher;
    private List<StudentClass> netStudentClassList = new ArrayList<StudentClass>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_publish_sign);
        teacher = (Teacher) getIntent().getSerializableExtra("object");
        teacherNameTV = (TextView) findViewById(R.id.id_teacher_name_text);
        startTimeTV = (TextView) findViewById(R.id.id_leave_start_time_text);
        endTimeTV = (TextView) findViewById(R.id.id_leave_end_time_text);
        selectClassTV = (TextView) findViewById(R.id.id_select_class_text);
        myPlaceTV = (TextView) findViewById(R.id.id_myplace_text);
        signtitleET = (EditText) findViewById(R.id.id_sign_title_edit);
        teacherNameTV.setText(teacher.getRealName());

        initEvent();
    }

    private void initEvent() {
        startTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//时间选择器
                TimePickerView pvTime = new TimePickerView.Builder(PublishSignActivity.this, new TimePickerView.OnTimeSelectListener() {
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
        endTimeTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//时间选择器
                TimePickerView pvTime = new TimePickerView.Builder(PublishSignActivity.this, new TimePickerView.OnTimeSelectListener() {
                    @Override
                    public void onTimeSelect(Date date, View v) {//选中事件回调
                        endTimeTV.setText(getTime(date));
                    }
                })
                        .build();
                pvTime.setDate(Calendar.getInstance());//注：根据需求来决定是否使用该方法（一般是精确到秒的情况），此项可以在弹出选择器的时候重新设置当前时间，避免在初始化之后由于时间已经设定，导致选中时间与当前时间不匹配的问题。
                pvTime.show();
            }
        });
        selectClassTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (netStudentClassList != null) {
                    netStudentClassList.clear();
                }
                queryClasstOfTeacher();
            }
        });
    }

    private String getTime(Date date) {//可根据需要自行截取数据显示
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return format.format(date);
    }


    private void queryClasstOfTeacher() {//查询出老师所教学的班级
        final ArrayList<StudentClass> holdClassList = new ArrayList<>();
        BmobQuery<StudentClass> query = new BmobQuery<StudentClass>();//管理的所有班级

        query.addWhereRelatedTo("holdClass", new BmobPointer(teacher));
        query.findObjects(new FindListener<StudentClass>() {

            @Override
            public void done(List<StudentClass> object, BmobException e) {
                if (e == null) {
                    if (object.size() != 0) {
                        holdClassList.addAll(object);
                    }
                    getTeacherClass(holdClassList);

                    Log.i("bmob", "查询个数：" + object.size());
                } else {
                    Log.i("bmob", "失败：" + e.getMessage());
                }
            }

        });
    }

    private void getTeacherClass(final ArrayList<StudentClass> allStudentClassList) {//获得老师所辅导的班级
        final List<StudentClass> studentClassList = new ArrayList<StudentClass>();
        studentClassList.addAll(allStudentClassList);
        BmobQuery<StudentClass> query = new BmobQuery<StudentClass>();
//用此方式可以构造一个BmobPointer对象
        query.addWhereEqualTo("counselor", new BmobPointer(teacher));
        query.findObjects(new FindListener<StudentClass>() {

            @Override
            public void done(List<StudentClass> object, BmobException e) {

                if (e == null) {
                    if (object.size() != 0) {
                        for (StudentClass studentclass : object) {
                            String classNo = studentclass.getClassNo();
                            for (StudentClass studentClass2 : allStudentClassList)
                                if (classNo.equals(studentClass2.getClassNo())) {
                                    studentClassList.remove(studentClass2);//去掉重复班级
                                }

                        }
                        studentClassList.addAll(object);//得到所有班号了
                        showDialog(studentClassList);
                    }

                    Log.i("bmob", "查询个数：" + object.size());
                } else {
                    Log.i("bmob", "失败：" + e.getMessage());
                }
            }
        });
    }

    public void showDialog(final List<StudentClass> studentClassList) {

        String[] sarray = new String[studentClassList.size()];
        int i = 0;

        for (StudentClass studentclass : studentClassList)
            sarray[i++] = studentclass.getClassNo();

        new AlertDialog.Builder(PublishSignActivity.this).setTitle("复选框")
                .setMultiChoiceItems(sarray, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {

                            netStudentClassList.add(studentClassList.get(which));
                        }
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String classString = "";
                        for (StudentClass studentclass : netStudentClassList)
                            if (!classString.contains(studentclass.getClassNo()))
                                classString = classString + studentclass.getClassNo() + ";";
                        selectClassTV.setText(classString);

                    }
                })
                .setNegativeButton("取消", null).show();
    }


}

