package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.adapter.MyItemClickListener;
import com.nuc.jingbeibei.studentdailymanagement.adapter.SignFailAdapter;
import com.nuc.jingbeibei.studentdailymanagement.adapter.SignUnAdapter;
import com.nuc.jingbeibei.studentdailymanagement.app.ActivityCollector;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignRecord;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignType;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class UnSignListActivity extends AppCompatActivity implements MyItemClickListener {
    private SignType signType;
    private XRecyclerView mRecyclerView;
    private List<Student> students;
    private TextView BarTitle;
    private ImageView BackImage;
    private SignUnAdapter mAdapter;
    private ArrayList<String> studentIdList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_un_sign_list);

        ActivityCollector.addActivity(this);
        signType = (SignType) getIntent().getSerializableExtra("signType");
        students = new ArrayList<>();
        mAdapter = new SignUnAdapter((ArrayList<Student>) students, this);
        mAdapter.setListener(this);
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        BarTitle.setText("未签到学生");
        mRecyclerView = (XRecyclerView) findViewById(R.id.sing_list_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        mRecyclerView.setArrowImageView(R.mipmap.banner_error);

        mRecyclerView.refresh();
        mRecyclerView.setLoadingMoreEnabled(false);
        mRecyclerView.setPullRefreshEnabled(false);

        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(UnSignListActivity.this);
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
        query.include("studentClass");
        query.addWhereNotContainedIn("userId", studentIdList);
        query.findObjects(new FindListener<Student>() {
            @Override
            public void done(List<Student> list, BmobException e) {
                if (e == null) {
                    if (students != null)
                        students.clear();
                    if (list.size() == 0) {
                        ToastUtils.toast(UnSignListActivity.this, "没有未签到学生");
                    }
                    students.addAll(list);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.refreshComplete();
                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getStudentOfSignAllRecord();
    }

    @Override
    public void onItemClick(View view, int postion) {

    }
}
