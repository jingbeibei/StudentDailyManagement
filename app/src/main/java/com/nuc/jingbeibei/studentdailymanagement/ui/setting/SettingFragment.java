package com.nuc.jingbeibei.studentdailymanagement.ui.setting;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.app.ActivityCollector;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.ArrayList;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobFile;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;
import cn.bmob.v3.listener.UploadFileListener;
import me.nereo.multi_image_selector.MultiImageSelector;

public class SettingFragment extends Fragment implements View.OnClickListener {
    private ImageView headImage;
    private TextView nicknameTV;
    private RelativeLayout modifyData;
    private RelativeLayout modifyPassword;
    private Button exitBtn;
    private boolean isTeacher = false;
    private BmobObject object;
    private SharedPreferences pref;
    private String objectId = "";
    private TextView BarTitle;
    private ImageView BackImage;
    private SharedPreferences.Editor editor;

    private static final int REQUEST_IMAGE = 2;
    private ArrayList<String> mSelectPath;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getActivity().getSharedPreferences("data", getContext().MODE_PRIVATE);
        editor = pref.edit();
        objectId = pref.getString("objectid", "");
        isTeacher = pref.getBoolean("isTeacher", false);

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setting, container, false);
        headImage = (ImageView) view.findViewById(R.id.setting_head_image);
        nicknameTV = (TextView) view.findViewById(R.id.setting_nickname);
        modifyData = (RelativeLayout) view.findViewById(R.id.modify_data_layout);
        modifyPassword = (RelativeLayout) view.findViewById(R.id.modify_password_layout);
        exitBtn = (Button) view.findViewById(R.id.id_exit_btn);
        BarTitle = (TextView) view.findViewById(R.id.id_bar_title);
        BackImage = (ImageView) view.findViewById(R.id.id_back_arrow_image);
        BackImage.setVisibility(View.INVISIBLE);
        BarTitle.setText("设置");

        initEvent();
        if (isTeacher) {
            getTeacherObject(view);
        } else {
            getStudentObject(view);
        }
        return view;
    }


    private void initEvent() {
        headImage.setOnClickListener(this);
        modifyData.setOnClickListener(this);
        modifyPassword.setOnClickListener(this);
        exitBtn.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.setting_head_image:
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                    ToastUtils.toast(getActivity(), "请开启读取存储信息权限");
                    return;
                } else {
                    MultiImageSelector.create(getActivity())
                            .showCamera(true) // 是否显示相机. 默认为显示
                            .count(1) // 最大选择图片数量, 默认为9. 只有在选择模式为多选时有效
                            .single() // 单选模式
                            // .multi() // 多选模式, 默认模式;
                            .origin(mSelectPath) // 默认已选择图片. 只有在选择模式为多选时有效
                            .start(SettingFragment.this, REQUEST_IMAGE);
                }
                break;

            case R.id.modify_data_layout:
                Intent dataIntent = new Intent(getActivity(), ModifyDataActivity.class);
                dataIntent.putExtra("object", object);
                startActivity(dataIntent);
                break;
            case R.id.modify_password_layout:
                Intent passwordIntent = new Intent(getActivity(), ModifyPasswordActivity.class);
                passwordIntent.putExtra("object", object);
                startActivity(passwordIntent);
                break;
            case R.id.id_exit_btn:
                editor.clear();
                editor.commit();
                ActivityCollector.finishAll();
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE) {
            if (resultCode == getActivity().RESULT_OK) {
                mSelectPath = data.getStringArrayListExtra(MultiImageSelector.EXTRA_RESULT);
                final StringBuilder sb = new StringBuilder();
                for (String p : mSelectPath) {
                    sb.append(p);
                }

                final BmobFile bmobFile = new BmobFile(new File(sb.toString()));
                bmobFile.uploadblock(new UploadFileListener() {

                    @Override
                    public void done(BmobException e) {
                        if (e == null) {
                            //bmobFile.getFileUrl()--返回的上传文件的完整地址
                            updateUser(bmobFile.getFileUrl());
                            Bitmap bitmap = BitmapFactory.decodeFile(sb.toString());
                            headImage.setImageBitmap(bitmap);

                        } else {
                            ToastUtils.toast(getActivity(), "修改头像成功" + e.getMessage());
                        }

                    }

                    @Override
                    public void onProgress(Integer value) {
                        // 返回的上传进度（百分比）
                    }
                });

            }
        }
    }

    private void getTeacherObject(View view) {
        BmobQuery<Teacher> query = new BmobQuery<Teacher>();
        query.getObject(objectId, new QueryListener<Teacher>() {

            @Override
            public void done(Teacher object1, BmobException e) {
                if (e == null) {
                    object = object1;
                    Teacher teacher = (Teacher) object;
                    Picasso.with(getContext()).load(teacher.getPicpath()).placeholder(R.mipmap.protrait).error(R.mipmap.protrait).into(headImage);
                    nicknameTV.setText(teacher.getRealName());
//                    isCounselor=object1.getIsCounselor();
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    private void getStudentObject(View view) {
        BmobQuery<Student> query = new BmobQuery<Student>();
        query.include("studentClass.counselor");
        query.getObject(objectId, new QueryListener<Student>() {

            @Override
            public void done(Student object1, BmobException e) {
                if (e == null) {
                    object = object1;
                    Student student = (Student) object;
                    Picasso.with(getContext()).load(student.getPicpath()).placeholder(R.mipmap.protrait).error(R.mipmap.protrait).into(headImage);
                    nicknameTV.setText(student.getRealName());
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    private void updateUser(String path) {//更新头像
        if (isTeacher) {
            Teacher teacher = (Teacher) object;
            teacher.setPicpath(path);
            teacher.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        ToastUtils.toast(getActivity(), "修改头像成功");
                    }
                }
            });
        } else {
            Student student = (Student) object;
            student.setPicpath(path);
            student.update(new UpdateListener() {
                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        ToastUtils.toast(getActivity(), "修改头像成功");
                    }
                }
            });
        }
    }
}
