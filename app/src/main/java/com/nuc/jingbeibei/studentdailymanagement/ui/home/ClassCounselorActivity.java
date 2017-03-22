package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.content.DialogInterface;
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

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.adapter.ClassAdapter;
import com.nuc.jingbeibei.studentdailymanagement.beans.StudentClass;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class ClassCounselorActivity extends AppCompatActivity {
    private SharedPreferences pref;
    private String objectId;
    private BmobObject bmobObject;
    private RecyclerView idClassManageRecycView;
    private LinearLayoutManager mLayoutManager;
    private Button idAddClassBtn;
    private ClassAdapter myAdapter;
    private List<StudentClass> myobject = new ArrayList<>();
    private ArrayList<String> allClassList = new ArrayList<String>();
    private List<StudentClass> teacherClass = new ArrayList<>();
    private List<String> teacherClassList = new ArrayList<String>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_counselor);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        objectId = pref.getString("objectid", "");
        bmobObject = (BmobObject) getIntent().getSerializableExtra("object");
        getTeacherClass();
        initView();
        initEvent();
    }

    private void initView() {
        idAddClassBtn = (Button) findViewById(R.id.id_add_class_btn);
        idClassManageRecycView = (RecyclerView) findViewById(R.id.id_class_manage_recycview);
        mLayoutManager = new LinearLayoutManager(this);//设置布局管理器,默认垂直
        idClassManageRecycView.setLayoutManager(mLayoutManager);
        idClassManageRecycView.setItemAnimator(new DefaultItemAnimator());//增加或删除条目动画
        idClassManageRecycView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
        myAdapter = new ClassAdapter(teacherClassList, (Teacher) bmobObject);
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
        idAddClassBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getAllClass();

            }
        });
    }

    public void getAllClass() {//获得所有没有辅导员的班级
        BmobQuery<StudentClass> query = new BmobQuery<StudentClass>();
        query.addWhereDoesNotExists("counselor");//查询counselor列没有值得数据，即没有辅导员的班级
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
                    }
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    public void showDialog() {
        String[] sarray = (String[]) allClassList.toArray(new String[allClassList.size()]);
        new AlertDialog.Builder(ClassCounselorActivity.this).setTitle("复选框")
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
}
