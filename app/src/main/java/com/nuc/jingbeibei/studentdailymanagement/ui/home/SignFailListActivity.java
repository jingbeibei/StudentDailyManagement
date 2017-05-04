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
import com.nuc.jingbeibei.studentdailymanagement.app.ActivityCollector;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignRecord;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignType;
import com.nuc.jingbeibei.studentdailymanagement.utils.IntentUtils;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SignFailListActivity extends AppCompatActivity implements MyItemClickListener {
    private SignType signType;
    private XRecyclerView mRecyclerView;
    private List<SignRecord> signRecords;
    private TextView BarTitle;
    private ImageView BackImage;
    private SignFailAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_fail_list);
        ActivityCollector.addActivity(this);
        signType = (SignType) getIntent().getSerializableExtra("signType");
        signRecords = new ArrayList<>();
        mAdapter = new SignFailAdapter((ArrayList<SignRecord>) signRecords, this);
        mAdapter.setListener(this);
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        BarTitle.setText("签到失败");
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
                ActivityCollector.removeActivity(SignFailListActivity.this);
            }
        });
    }

    private void getStudentOfSignFail() {//查询本次签到失败的所有记录
        BmobQuery<SignRecord> query = new BmobQuery<>();
        query.include("student.studentClass,signType");
        query.addWhereEqualTo("signType", new BmobPointer(signType));
        query.addWhereEqualTo("isSuccess", 0);
        query.findObjects(new FindListener<SignRecord>() {
            @Override
            public void done(List<SignRecord> list, BmobException e) {
                if (e == null) {
                    if (signRecords != null)
                        signRecords.clear();
                    if (list.size() == 0) {
                        ToastUtils.toast(SignFailListActivity.this, "没有签到失败记录");
                    }
                    signRecords.addAll(list);
                    mAdapter.notifyDataSetChanged();
                    mRecyclerView.refreshComplete();

                }
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        getStudentOfSignFail();
    }

    @Override
    public void onItemClick(View view, int postion) {
        IntentUtils.doIntentWithObject(SignFailListActivity.this, SignStudentFailDetailsActivity.class, "record", signRecords.get(postion - 1));
    }
}
