package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.Manifest;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.bigkoo.pickerview.TimePickerView;
import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignType;
import com.nuc.jingbeibei.studentdailymanagement.beans.StudentClass;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.SaveListener;
import cn.bmob.v3.listener.UpdateListener;

public class PublishSignActivity extends AppCompatActivity {
    private TextView teacherNameTV, startTimeTV, endTimeTV, selectClassTV, myPlaceTV;
    private EditText signtitleET;
    private Button publishSignBtn;
    private Teacher teacher;
    private Double latitude;//纬度
    private Double longitude;//经度
    private String location;//位置
    private List<StudentClass> netStudentClassList = new ArrayList<StudentClass>();
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;


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
        publishSignBtn= (Button) findViewById(R.id.id_publish_sign_btn);
//初始化定位
        mLocationClient = new AMapLocationClient(getApplicationContext());

//初始化AMapLocationClientOption对象
        mLocationOption = new AMapLocationClientOption();
        //设置定位模式为AMapLocationMode.Hight_Accuracy，高精度模式。
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        //获取一次定位结果：
//该方法默认为false。
        mLocationOption.setOnceLocation(true);
//关闭缓存机制
        mLocationOption.setLocationCacheEnable(false);
//获取最近3s内精度最高的一次定位结果：
//设置setOnceLocationLatest(boolean b)接口为true，启动定位时SDK会返回最近3s内精度最高的一次定位结果。如果设置其为true，setOnceLocation(boolean b)接口也会被设置为true，反之不会，默认为false。
        mLocationOption.setOnceLocationLatest(true);
        //设置是否返回地址信息（默认返回地址信息）
        mLocationOption.setNeedAddress(true);
        //给定位客户端对象设置定位参数
        mLocationClient.setLocationOption(mLocationOption);
        if (ContextCompat.checkSelfPermission(PublishSignActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //申请WRITE_EXTERNAL_STORAGE权限
            ActivityCompat.requestPermissions(PublishSignActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);//自定义的code
        } else {
            //启动定位
            mLocationClient.startLocation();
        }
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
        myPlaceTV.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(PublishSignActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(PublishSignActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                            1);//自定义的code
                } else {
                    //启动定位
                    mLocationClient.startLocation();
                }
            }
        });
        publishSignBtn.setOnClickListener(new View.OnClickListener() {//提交签到
            @Override
            public void onClick(View v) {
                String title=signtitleET.getText().toString();
                String endTime=endTimeTV.getText().toString();
                String startTime=startTimeTV.getText().toString();
                String visibleClass=selectClassTV.getText().toString();
                if(title.equals("")||endTime.equals("")||startTime.equals("")||visibleClass.equals("")){
                    ToastUtils.toast(PublishSignActivity.this,"信息不完整，请检查");
                }{
                    SignType signType=new SignType();
                    signType.setPublisher(teacher);
                    signType.setEndTime(endTime);
                    signType.setStartTime(startTime);
                    signType.setTitle(title);
                    BmobRelation relation = new BmobRelation();
                    for (StudentClass studentClass:netStudentClassList){
                        relation.add(studentClass);
                    }
                    signType.setLatitude(latitude);
                    signType.setLongitude(longitude);
                    signType.setLocation(location);
                    signType.setVisibleClass(relation);
                    signType.save(new SaveListener<String>() {
                        @Override
                        public void done(String s, BmobException e) {
                            if(e==null){
                                ToastUtils.toast(PublishSignActivity.this,"发布成功！");
                            }else{
                                ToastUtils.toast(PublishSignActivity.this,"发布失败！"+e.getMessage());

                            }
                        }
                    });
                }

            }
        });

        //设置定位回调监听
        mLocationClient.setLocationListener(new AMapLocationListener() {
            @Override
            public void onLocationChanged(AMapLocation aMapLocation) {
                if (aMapLocation != null) {
                    if (aMapLocation.getErrorCode() == 0) {
//可在其中解析amapLocation获取相应内容。
                        latitude = aMapLocation.getLatitude();//获取纬度
                        longitude=aMapLocation.getLongitude();//获取经度
                        float s = aMapLocation.getAccuracy();//获取精度信息
                        location= aMapLocation.getAddress();//地址，如果option中设置isNeedAddress为false，则没有此结果，网络定位结果中会有地址信息，GPS定位不返回地址信息。
                        myPlaceTV.setText(location);
                    } else {
                        //定位失败时，可通过ErrCode（错误码）信息来确定失败的原因，errInfo是错误信息，详见错误码表。
                        Log.e("AmapError", "location Error, ErrCode:"
                                + aMapLocation.getErrorCode() + ", errInfo:"
                                + aMapLocation.getErrorInfo());
                    }
                }
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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //可在此继续其他操作。
        //启动定位
        if (requestCode == 1) {
            mLocationClient.startLocation();
        }
    }

}

