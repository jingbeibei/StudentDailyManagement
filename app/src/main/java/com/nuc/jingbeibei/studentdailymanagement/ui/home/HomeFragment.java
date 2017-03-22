package com.nuc.jingbeibei.studentdailymanagement.ui.home;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;
import com.nuc.jingbeibei.studentdailymanagement.utils.IntentUtils;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;

public class HomeFragment extends Fragment {

    private SharedPreferences pref;
    private Button idClassManagementBtn;
    private String objectId = "";
    private boolean isTeacher = false;
    private BmobObject object;
    private boolean isCounselor=false;
    private Button idClassCounselorBtn;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getActivity().getSharedPreferences("data", getContext().MODE_PRIVATE);
        objectId = pref.getString("objectid", "");
        isTeacher = pref.getBoolean("isTeacher", false);
        if (isTeacher) {
            getTeacherObject();
        } else {
            getStudentObject();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        idClassManagementBtn = (Button) view.findViewById(R.id.id_class_management_btn);
        idClassCounselorBtn= (Button) view.findViewById(R.id.id_class_counselor_btn);

    }

    private void initEvent() {
        idClassManagementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.doIntent(getActivity(), ClassManageActivity.class);
                IntentUtils.doIntentWithObject(getActivity(), ClassManageActivity.class, "object", object);
            }
        });
        idClassCounselorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCounselor){
                    IntentUtils.doIntent(getActivity(), ClassManageActivity.class);
                    IntentUtils.doIntentWithObject(getActivity(), ClassManageActivity.class, "object", object);
                }else {
                    ToastUtils.toast(getActivity(),"您还不是辅导员，暂时无法使用此功能");
                }
            }
        });
    }

    private void getTeacherObject() {
        BmobQuery<Teacher> query = new BmobQuery<Teacher>();
        query.getObject(objectId, new QueryListener<Teacher>() {

            @Override
            public void done(Teacher object1, BmobException e) {
                if (e == null) {
                    object = object1;
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    private void getStudentObject() {
        BmobQuery<Student> query = new BmobQuery<Student>();
        query.getObject(objectId, new QueryListener<Student>() {

            @Override
            public void done(Student object1, BmobException e) {
                if (e == null) {
                    object = object1;
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }
}
