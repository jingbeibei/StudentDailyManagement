package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.content.DialogInterface;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.Notice;
import com.nuc.jingbeibei.studentdailymanagement.beans.StudentClass;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class publishNoticeActivity extends AppCompatActivity {
    private EditText noticeTitleEdit;
    private EditText noticeContentEdit;
    private Button selectClassBtn;
    private Button publishNoticeBtn;
    private TextView visibleClassText;
    private Teacher teacher;
    private List<StudentClass> studentClassList = new ArrayList<>();
    private List<StudentClass> noticeClassList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        teacher = (Teacher) getIntent().getSerializableExtra("object");
        setContentView(R.layout.activity_publish_notice);
        initView();
        initEvent();
    }

    private void initView() {
        noticeTitleEdit = (EditText) findViewById(R.id.id_notice_title_edit);
        noticeContentEdit = (EditText) findViewById(R.id.id_notice_content_edit);
        selectClassBtn = (Button) findViewById(R.id.id_select_class_btn);
        publishNoticeBtn = (Button) findViewById(R.id.id_publish_notice_btn);
        visibleClassText = (TextView) findViewById(R.id.id_visible_class_text);
    }

    private void initEvent() {
        selectClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getStudentClass();

            }
        });
        publishNoticeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = noticeTitleEdit.getText().toString();
                String content = noticeContentEdit.getText().toString();
                if (title.equals("") || content.equals("")) {
                    ToastUtils.toast(publishNoticeActivity.this, "标题内容不能为空");
                } else {
                    long time = System.currentTimeMillis();//获取系统时间
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    Date date = new Date(time);

                    Notice notice = new Notice();
                    notice.setTitle(title);
                    notice.setContent(content);
                    notice.setReleaseTime(format.format(date));
                    notice.setPublisher(teacher);
                    BmobRelation relation = new BmobRelation();
                    //将班级添加到多对多关联中
                    for (StudentClass studentclass : noticeClassList)
                        relation.add(studentclass);
                    notice.setVisibleClass(relation);
                    notice.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if (e == null) {
                                finish();
                                Log.i("bmob", "多对多关联添加成功");
                            } else {
                                Log.i("bmob", "失败：" + e.getMessage());
                            }
                        }
                    });
                }
            }
        });

    }

    private void getStudentClass() {//获得老师上课的班级
        // 查询喜欢这个帖子的所有用户，因此查询的是用户表
        BmobQuery<StudentClass> query = new BmobQuery<StudentClass>();
//likes是Post表中的字段，用来存储所有喜欢该帖子的用户
        query.addWhereRelatedTo("holdClass", new BmobPointer(teacher));
        query.findObjects(new FindListener<StudentClass>() {

            @Override
            public void done(List<StudentClass> object, BmobException e) {

                if (e == null) {
                    if (object.size() != 0) {
                        studentClassList.addAll(object);
                    }
                    getCounselorClass();
                    Log.i("bmob", "查询个数：" + object.size());
                } else {
                    Log.i("bmob", "失败：" + e.getMessage());
                    getCounselorClass();
                }
            }

        });
    }

    private void getCounselorClass() {//获得辅导的班级
        BmobQuery<StudentClass> query = new BmobQuery<StudentClass>();
//用此方式可以构造一个BmobPointer对象
        query.addWhereEqualTo("counselor", new BmobPointer(teacher));
        query.findObjects(new FindListener<StudentClass>() {

            @Override
            public void done(List<StudentClass> object, BmobException e) {
                if (e == null) {
                    if (object.size() != 0) {
                        studentClassList.addAll(object);
//                        for (StudentClass studentclass : object) {
//                            teacherClassList.add(studentclass.getClassNo());
//                        }
                        showDialog();
                    }

                    Log.i("bmob", "查询个数：" + object.size());
                } else {
                    Log.i("bmob", "失败：" + e.getMessage());
                }
            }
        });
    }

    public void showDialog() {

        String[] sarray = new String[studentClassList.size()];
        int i = 0;

        for (StudentClass studentclass : studentClassList)
            sarray[i++] = studentclass.getClassNo();

        new AlertDialog.Builder(publishNoticeActivity.this).setTitle("复选框")
                .setMultiChoiceItems(sarray, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {

                            noticeClassList.add(studentClassList.get(which));
                        }
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String classString = "";
                        for (StudentClass studentclass : noticeClassList)
                            if (!classString.contains(studentclass.getClassNo()))
                                classString = classString + studentclass.getClassNo() + ";";
                        visibleClassText.setText(classString);
                        if (studentClassList != null)
                            studentClassList.clear();
                    }
                })
                .setNegativeButton("取消", null).show();
    }
}