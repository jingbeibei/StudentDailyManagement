package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.adapter.ClassAdapter;
import com.nuc.jingbeibei.studentdailymanagement.app.ActivityCollector;
import com.nuc.jingbeibei.studentdailymanagement.beans.StudentClass;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;
import com.nuc.jingbeibei.studentdailymanagement.utils.GetObjectSingleton;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;
import cn.bmob.v3.listener.QueryListener;
import cn.bmob.v3.listener.UpdateListener;

public class ClassManageActivity extends AppCompatActivity {
    private RecyclerView idClassManageRecycView;
    private LinearLayoutManager mLayoutManager;
    private TextView BarTitle;
    private ImageView BackImage;
    private TextView BarRight;
    private ArrayList<String> allClassList = new ArrayList<String>();
    private List<String> teacherClassList = new ArrayList<String>();
    private List<StudentClass> myobject = new ArrayList<>();
    private List<StudentClass> teacherClass = new ArrayList<>();
    private List<StudentClass> teacherClassService = null;//第一次网络查询的班级
    private SharedPreferences pref;
    private String objectId;
    private BmobObject bmobObject;
    private ClassAdapter myAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_manage);
        ActivityCollector.addActivity(this);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        objectId = pref.getString("objectid", "");
        bmobObject = (BmobObject) getIntent().getSerializableExtra("object");
        getTeacherClass();
        initView();
        initEvent();
    }

    private void initView() {
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BarRight = (TextView) findViewById(R.id.bar_right_tv);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        BarRight.setText("添加");
        BarRight.setVisibility(View.VISIBLE);
        BarTitle.setText("班级管理");

        idClassManageRecycView = (RecyclerView) findViewById(R.id.id_class_manage_recycview);
        mLayoutManager = new LinearLayoutManager(this);//设置布局管理器,默认垂直
        idClassManageRecycView.setLayoutManager(mLayoutManager);
        idClassManageRecycView.setItemAnimator(new DefaultItemAnimator());//增加或删除条目动画
        idClassManageRecycView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        myAdapter = new ClassAdapter(teacherClassList, (Teacher) bmobObject, 1);
//idClassManageRecycView.setAdapter(myAdapter);
        //先实例化Callback
        ItemTouchHelper.Callback callback = new ItemTouchHelperCallback(myAdapter);
        //用Callback构造ItemtouchHelper
        ItemTouchHelper touchHelper = new ItemTouchHelper(callback);
        //调用ItemTouchHelper的attachToRecyclerView方法建立联系
        touchHelper.attachToRecyclerView(idClassManageRecycView);
        idClassManageRecycView.setAdapter(myAdapter);
    }

    private void initEvent() {
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(ClassManageActivity.this);
            }
        });
        BarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getAllClass();
            }
        });

    }

    public void getAllClass() {//获得所有班级
        BmobQuery<StudentClass> query = new BmobQuery<StudentClass>();
        query.setLimit(50);
//执行查询方法
        query.findObjects(new FindListener<StudentClass>() {
            @Override
            public void done(List<StudentClass> object, BmobException e) {
                if (e == null) {
                    if (object.size() != 0) {
                        myobject.addAll(object);
                        for (StudentClass studentclass : object) {
                            allClassList.add(studentclass.getClassNo());
//                       allClassList.add(object.get(i).getClassNo());
                        }
                        showDialog();
                    } else {
                        ToastUtils.toast(ClassManageActivity.this, "暂时没有合适班级");
                    }
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    public void showDialog() {
        String[] sarray = (String[]) allClassList.toArray(new String[allClassList.size()]);
        new AlertDialog.Builder(ClassManageActivity.this).setTitle("复选框")
                .setMultiChoiceItems(sarray, null, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            teacherClass.add(myobject.get(which));
                        }
                    }
                })
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d("班级号", teacherClass.toString());
//                        getTeacherInstance(teacherClass, objectId);
                        addClass(teacherClass, (Teacher) bmobObject);
                        myAdapter.clearItemData();
                    }
                })
                .setNegativeButton("取消", null).show();
    }

    public void addClass(List<StudentClass> studentClassList, Teacher teacher) {//将班级与老师关联

        BmobRelation relation = new BmobRelation();
        for (StudentClass studentClass : studentClassList) {
            relation.add(studentClass);
        }
        teacher.setHoldClass(relation);
        teacher.update(new UpdateListener() {
            @Override
            public void done(BmobException e) {
                if (e == null) {
                    allClassList.clear();
                    Log.i("bmob", "多对多关联添加成功");
                    getTeacherClass();
                } else {
                    Log.i("bmob", "失败：" + e.getMessage());
                }
            }

        });
    }

//    public void getTeacherInstance(final List<StudentClass> studentClassList, String objectId) {//获得老师信息
//        BmobQuery<Teacher> query = new BmobQuery<Teacher>();
//        query.getObject(objectId, new QueryListener<Teacher>() {
//
//            @Override
//            public void done(Teacher object, BmobException e) {
//                if (e == null) {
//                    addClass(studentClassList, object);
//                } else {
//                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
//                }
//            }
//        });
//
//    }


    public void getTeacherClass() {
        if (teacherClass != null) {
            teacherClass.clear();
        }
        // 查询老师关联的所有班级，因此查询的是班级
        BmobQuery<StudentClass> query = new BmobQuery<StudentClass>();
        query.addWhereRelatedTo("holdClass", new BmobPointer(bmobObject));
        query.findObjects(new FindListener<StudentClass>() {

            @Override
            public void done(List<StudentClass> object, BmobException e) {
                if (e == null) {
                    if (object.size() != 0) {
                        teacherClassService = object;
                        for (StudentClass studentclass : object) {
                            teacherClassList.add(studentclass.getClassNo());
                        }
                    }
                    myAdapter.setTeacherHolderClass(teacherClassService);
                    myAdapter.notifyDataSetChanged();
                    Log.i("bmob", "查询个数：" + object.size());
                } else {
                    Log.i("bmob", "失败：" + e.getMessage());
                }
            }

        });
    }

}
