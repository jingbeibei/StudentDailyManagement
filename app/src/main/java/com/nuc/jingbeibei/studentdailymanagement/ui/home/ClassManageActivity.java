package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.adapter.ClassAdapter;

public class ClassManageActivity extends AppCompatActivity {
private RecyclerView idClassManageRecycView;
    private LinearLayoutManager mLayoutManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_class_manage);
        initView();
        initEvent();
    }

    private void initView() {
        idClassManageRecycView= (RecyclerView) findViewById(R.id.id_class_manage_recycview);
        mLayoutManager = new LinearLayoutManager(this);//设置布局管理器,默认垂直
        idClassManageRecycView.setLayoutManager(mLayoutManager);
        idClassManageRecycView.setItemAnimator(new DefaultItemAnimator());//增加或删除条目动画
        idClassManageRecycView.addItemDecoration(new DividerItemDecoration(getApplicationContext(), DividerItemDecoration.VERTICAL));
                ClassAdapter myAdapter=new ClassAdapter();
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

    }
}
