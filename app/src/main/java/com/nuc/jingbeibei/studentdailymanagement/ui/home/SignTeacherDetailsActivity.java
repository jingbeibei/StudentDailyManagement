package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.content.Intent;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.app.ActivityCollector;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignRecord;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignType;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.beans.StudentClass;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.Bmob;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobQueryResult;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SQLQueryListener;

public class SignTeacherDetailsActivity extends AppCompatActivity {
    private TextView signTileTv, publisherTv, signStartTimeTv, signEndTimeTv, isCompleteTv, publisherAddress, visibleClass;
    private Button SuccessBtn, isFailBtn, unSignBtn;
    private SignType signType;
    private Teacher teacher;
    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private TextView BarTitle;
    private ImageView BackImage;
    private ArrayList<String> studentIdList = new ArrayList<>();
    private ArrayList<SignRecord> failList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_teacher_details);
        ActivityCollector.addActivity(this);
        signType = (SignType) getIntent().getSerializableExtra("signType");
        teacher = (Teacher) getIntent().getSerializableExtra("teacher");
        initView();
        initEvent();
        getStudentOfSign();
        getStudentOfSignAllRecord();
        getStudentOfSignSuccess();
        getStudentOfSignFail();
    }

    private void initView() {
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        signTileTv = (TextView) findViewById(R.id.id_sign_title_text);
        publisherTv = (TextView) findViewById(R.id.id_sign_publisher_text);
        signStartTimeTv = (TextView) findViewById(R.id.id_sign_start_time_text);
        signEndTimeTv = (TextView) findViewById(R.id.id_sign_end_time_text);
        isCompleteTv = (TextView) findViewById(R.id.id_sign_is_complete);
        publisherAddress = (TextView) findViewById(R.id.id_sign_publisher_adress_text);
        visibleClass = (TextView) findViewById(R.id.id_visible_class_text);
        SuccessBtn = (Button) findViewById(R.id.id_sign_success_btn);
        isFailBtn = (Button) findViewById(R.id.id_sign_fail_btn);
        unSignBtn = (Button) findViewById(R.id.id_un_sign_btn);
        BarTitle.setText("签到详情");

        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(SignTeacherDetailsActivity.this);
            }
        });
        signTileTv.setText(signType.getTitle());
        publisherTv.setText(signType.getPublisher().getRealName());
        signStartTimeTv.setText(signType.getStartTime());
        signEndTimeTv.setText(signType.getEndTime());
        publisherAddress.setText(signType.getLocation());
        try {
            if (new Date().before(sdf.parse(signType.getEndTime()))) {//当前时间在结束时间之前
                isCompleteTv.setText("未结束");
            } else {
                isCompleteTv.setText("已结束");
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    private void initEvent() {
        isFailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignTeacherDetailsActivity.this, SignFailListActivity.class);
                intent.putExtra("signType", signType);
                startActivity(intent);
            }
        });
        unSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(SignTeacherDetailsActivity.this, UnSignListActivity.class);
                intent.putExtra("signType", signType);
                startActivity(intent);
            }
        });


    }

    private void getStudentOfSign() {//得到签到对应的所有学生//班级
        BmobQuery<StudentClass> query = new BmobQuery<StudentClass>();
        query.addWhereRelatedTo("visibleClass", new BmobPointer(signType));
        query.findObjects(new FindListener<StudentClass>() {
            @Override
            public void done(List<StudentClass> object, BmobException e) {
                if (e == null) {
                    String[] classArray = new String[object.size()];
                    int i = 0;
                    String classString = "";
                    for (StudentClass studentClass : object) {
                        classArray[i] = studentClass.getObjectId();
                        classString = classString + ";" + studentClass.getClassNo();
                    }
                    visibleClass.setText(classString);
                    getStudentOfClass(classArray);
                    Log.i("bmob", "查询个数：" + object.size());
                } else {
                    Log.i("bmob", "失败：" + e.getMessage());
                }
            }

        });
    }

    private void getStudentOfClass(String[] studentClasss) {//可以得到涉及的所有学生数
        BmobQuery<Student> query = new BmobQuery<Student>();
        query.addWhereContainsAll("studentClass", Arrays.asList(studentClasss));
        query.findObjects(new FindListener<Student>() {
            @Override
            public void done(List<Student> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {

                    }
                }
            }
        });
    }

    private void getStudentOfSignSuccess() {//查询本次签到成功的所有记录
        BmobQuery<SignRecord> query = new BmobQuery<>();
        query.addWhereEqualTo("signType", new BmobPointer(signType));
        query.addWhereEqualTo("isSuccess", 1);
        query.findObjects(new FindListener<SignRecord>() {
            @Override
            public void done(List<SignRecord> list, BmobException e) {
                if (e == null) {
                    SuccessBtn.setText("签到成功（" + list.size() + ")");
                }
            }
        });
    }

    private void getStudentOfSignFail() {//查询本次签到失败的所有记录
        BmobQuery<SignRecord> query = new BmobQuery<>();
        query.include("student");
        query.addWhereEqualTo("signType", new BmobPointer(signType));
        query.addWhereEqualTo("isSuccess", 0);
        query.findObjects(new FindListener<SignRecord>() {
            @Override
            public void done(List<SignRecord> list, BmobException e) {
                if (e == null) {
                    isFailBtn.setText("签到失败（" + list.size() + ")");
                }
            }
        });
    }

    private void getStudentOfSignAllRecord() {//查询本次签到的所有记录
        BmobQuery<SignRecord> query = new BmobQuery<>();
        query.addWhereEqualTo("signType", new BmobPointer(signType));
        query.findObjects(new FindListener<SignRecord>() {
            @Override
            public void done(List<SignRecord> list, BmobException e) {
                if (e == null) {
                    for (SignRecord signRecord : list) {
                        studentIdList.add(signRecord.getUserId());
                    }
                    getStudentOfUnsign();
                }
            }
        });
    }

    private void getStudentOfUnsign() {//查询未签到学生
        BmobQuery<Student> query = new BmobQuery<Student>();
        query.addWhereNotContainedIn("userId", studentIdList);
        query.findObjects(new FindListener<Student>() {
            @Override
            public void done(List<Student> list, BmobException e) {
                if (e == null) {
                    if (list.size() != 0) {
                        unSignBtn.setText("未签到（" + list.size() + ")");
                    }
                }
            }
        });


//        String bql ="select * from Student where objectId not in (select include student.objectId from SignRecord where related signType to pointer('signType', '"+signType.getObjectId()+"') )";
        String bql1 = "select userId from SignRecord where signType=pointer('SignType', '" + signType.getObjectId() + "')";
        String bql = "select * from Student where userId in (select userId from SignRecord where signType=pointer('SignType', '" + signType.getObjectId() + "'))";
        String BQ2 = "select * from Student where userId not in ('11')";
//        new BmobQuery<Student>().doSQLQuery(bql, new SQLQueryListener<Student>() {
//
//            @Override
//            public void done(BmobQueryResult<Student> result, BmobException e) {
//                if (e == null) {
//                    List<Student> list = (List<Student>) result.getResults();
//                    if (list != null && list.size() > 0) {
//
//                    } else {
//                        Log.i("smile", "查询成功，无数据返回");
//                    }
//                } else {
//                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
//                }
//            }
//        });

//        new BmobQuery<SignRecord>().doSQLQuery(bql1, new SQLQueryListener<SignRecord>() {
//
//            @Override
//            public void done(BmobQueryResult<SignRecord> result, BmobException e) {
//                if (e == null) {
//                    List<SignRecord> list = (List<SignRecord>) result.getResults();
//                    if (list != null && list.size() > 0) {
//
//                    } else {
//                        Log.i("smile", "查询成功，无数据返回");
//                    }
//                } else {
//                    Log.i("smile", "错误码：" + e.getErrorCode() + "，错误描述：" + e.getMessage());
//                }
//            }
//        });


    }

}
