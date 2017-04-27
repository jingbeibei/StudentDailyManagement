package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.amap.api.location.AMapLocation;
import com.amap.api.location.AMapLocationClient;
import com.amap.api.location.AMapLocationClientOption;
import com.amap.api.location.AMapLocationListener;
import com.amap.api.location.CoordinateConverter;
import com.amap.api.location.DPoint;
import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignRecord;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignType;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.SaveListener;

public class SignStudentDetailsActivity extends AppCompatActivity {
    private SignType signType;
    private Student student;
    private TextView titleTV, startTV, endTV, publisherAddressTV, studentAddressTV, signTimeTV,isSuccessTV,publisherTV;
    private Button signBtn;
    private Double latitude;//纬度
    private Double longitude;//经度
    private String location;//位置
    //声明AMapLocationClient类对象
    public AMapLocationClient mLocationClient = null;
    //声明AMapLocationClientOption对象
    public AMapLocationClientOption mLocationOption = null;
    SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_student_details);
        signType = (SignType) getIntent().getSerializableExtra("signType");
        student= (Student) getIntent().getSerializableExtra("student");

        mLocationClient = new AMapLocationClient(getApplicationContext());
        mLocationOption = new AMapLocationClientOption();
        mLocationOption.setLocationMode(AMapLocationClientOption.AMapLocationMode.Hight_Accuracy);
        mLocationOption.setOnceLocation(true);
        mLocationOption.setLocationCacheEnable(false);
        mLocationOption.setOnceLocationLatest(true);
        mLocationOption.setNeedAddress(true);
        mLocationClient.setLocationOption(mLocationOption);
        initView();


        signBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (ContextCompat.checkSelfPermission(SignStudentDetailsActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION)
                        != PackageManager.PERMISSION_GRANTED) {
                    //申请WRITE_EXTERNAL_STORAGE权限
                    ActivityCompat.requestPermissions(SignStudentDetailsActivity.this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);//自定义的code
                } else {
                    //启动定位
                    mLocationClient.startLocation();
                }
            }
        });
    }

    private void initView() {
        titleTV = (TextView) findViewById(R.id.id_sign_title_text);
        startTV = (TextView) findViewById(R.id.id_sign_start_time_text);
        endTV = (TextView) findViewById(R.id.id_sign_end_time_text);
        publisherAddressTV = (TextView) findViewById(R.id.id_sign_publisher_adress_text);
        studentAddressTV = (TextView) findViewById(R.id.id_sign_student_address_text);
        signTimeTV = (TextView) findViewById(R.id.id_sign_student_time_text);
        signBtn = (Button) findViewById(R.id.id_sign_btn);
        isSuccessTV= (TextView) findViewById(R.id.id_sign_is_success_text);
        publisherTV= (TextView) findViewById(R.id.id_sign_publisher_text);

        titleTV.setText(signType.getTitle());
        startTV.setText(signType.getStartTime());
        endTV.setText(signType.getEndTime());
        publisherTV.setText(signType.getPublisher().getRealName());
        publisherAddressTV.setText(signType.getLocation());
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
                        studentAddressTV.setText(location);
                        try {
                            if(new Date().after(sdf.parse(signType.getEndTime()))){
                                CoordinateConverter coordinate=new CoordinateConverter(SignStudentDetailsActivity.this);
                             float distance=   coordinate.calculateLineDistance(new DPoint(latitude,longitude),new DPoint(signType.getLatitude(),signType.getLongitude()));
                                if(distance>(float)30.0){//距离大于30米 签到失败
                                    saveSignRecord(0,distance);
                                }else {
                                    saveSignRecord(1,distance);
                                }
                            }else {
                                ToastUtils.toast(SignStudentDetailsActivity.this,"签到已过期");
                                saveSignRecord(0, (float) 0.0);
                            }
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

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

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        //可在此继续其他操作。
        //启动定位
        if (requestCode == 1) {
            mLocationClient.startLocation();
        }
    }

    private void saveSignRecord(final int issuccess, float distance){//保存学生签到记录
        SignRecord signRecord=new SignRecord();
        signRecord.setLocation(location);
        signRecord.setSignType(signType);
        signRecord.setStudent(student);
        signRecord.setLongitude(longitude);
        signRecord.setLatitude(latitude);
        signRecord.setDistance(distance);
        signRecord.setIsSuccess(issuccess);
        signRecord.setSignTime(sdf.format(new Date()));
        signRecord.save(new SaveListener<String>() {
            @Override
            public void done(String s, BmobException e) {
                    if (e==null){
                        ToastUtils.toast(SignStudentDetailsActivity.this,"签到成功");
                        signBtn.setVisibility(View.GONE);
                        isSuccessTV.setText(issuccess==0?"签到失败":"签到成功");
                        studentAddressTV.setText(location);
                    }
            }
        });
    }


}
