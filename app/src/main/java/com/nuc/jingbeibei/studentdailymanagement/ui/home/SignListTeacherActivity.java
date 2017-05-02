package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.adapter.MyItemClickListener;
import com.nuc.jingbeibei.studentdailymanagement.adapter.SignRecordAdapter;
import com.nuc.jingbeibei.studentdailymanagement.app.ActivityCollector;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignRecord;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignType;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SignListTeacherActivity extends AppCompatActivity implements MyItemClickListener {
    private XRecyclerView mRecyclerView;
    private SignRecordAdapter mAdapter;
    private ArrayList<SignType> signTypes;
    private Teacher teacher;
    private TextView BarTitle;
    private ImageView BackImage;

    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多
    private int curPage = 0;        // 当前页的编号，从0开始
    private String lastTime;
    private int count = 10;// 每页的数据是10条
    @Override
    protected void onStart() {
        super.onStart();
        getData(0,STATE_REFRESH);
    }
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_list_teacher);
        ActivityCollector.addActivity(this);
        teacher= (Teacher) getIntent().getSerializableExtra("object");
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        BarTitle.setText("签到");

        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(SignListTeacherActivity.this);
            }
        });

        signTypes = new ArrayList<>();
        mAdapter = new SignRecordAdapter(signTypes);
        mAdapter.setListener(this);

        mRecyclerView = (XRecyclerView) findViewById(R.id.sing_list_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        mRecyclerView.setArrowImageView(R.mipmap.banner_error);

        mRecyclerView.refresh();
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                getData(0, STATE_REFRESH);
            }

            @Override
            public void onLoadMore() {
                getData(curPage, STATE_MORE);
            }
        });

    }

    private void getData(int page, final int actionType) {//查询出与此学生相关的签到信息
        BmobQuery<SignType> query = new BmobQuery<>();
        query.include("visibleClass");
        // 按时间降序查询
        query.order("-createdAt");
        query.addWhereEqualTo("publisher",new BmobPointer(teacher));
        // 如果是加载更多
        if (actionType == STATE_MORE) {
            // 处理时间查询
            Date date = null;
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            try {
                date = sdf.parse(lastTime);
            } catch (ParseException e) {
                e.printStackTrace();
            }
            // 只查询小于等于最后一个item发表时间的数据
            query.addWhereLessThanOrEqualTo("createdAt", new BmobDate(date));
            // 跳过之前页数并去掉重复数据
//            query.setSkip(page * count + 1);
        } else {
            page = 0;
            query.setSkip(page);
        }
        // 设置每页数据个数
        query.setLimit(count);
        // 查找数据
        query.findObjects(new FindListener<SignType>() {
            @Override
            public void done(List<SignType> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        if (actionType == STATE_REFRESH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                            curPage = 0;
                            signTypes.clear();
                            // 获取最后时间
                            lastTime = list.get(list.size() - 1).getCreatedAt();
                        }


                        signTypes.addAll(list);
                        // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
                        curPage++;
                        if (actionType == STATE_REFRESH) {
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.refreshComplete();
                        } else {
                            lastTime = list.get(list.size() - 1).getCreatedAt();
                            mRecyclerView.loadMoreComplete();
                            mAdapter.notifyDataSetChanged();
                        }

                    } else if (actionType == STATE_MORE) {
                        mRecyclerView.setNoMore(true);
                        mAdapter.notifyDataSetChanged();
                        ToastUtils.toast(SignListTeacherActivity.this, "没有更多数据了");
                    } else if (actionType == STATE_REFRESH) {
                        ToastUtils.toast(SignListTeacherActivity.this, "没有数据");
                    }

                } else {
                    ToastUtils.toast(SignListTeacherActivity.this, "查询失败");
                    mRecyclerView.refreshComplete();
                }
            }
        });

    }


    @Override
    public void onItemClick(View view, int postion) {
        Toast.makeText(this, "我是第" + postion + "项", Toast.LENGTH_SHORT).show();
        Intent intent=new Intent(SignListTeacherActivity.this,SignTeacherDetailsActivity.class);
        intent.putExtra("signType",signTypes.get(postion-1));
        intent.putExtra("teacher",teacher);
        startActivity(intent);
    }
}
