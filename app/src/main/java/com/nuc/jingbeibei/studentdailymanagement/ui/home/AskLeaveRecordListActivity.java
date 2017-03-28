package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.adapter.LeaveRecordAdapter;
import com.nuc.jingbeibei.studentdailymanagement.adapter.MyItemClickListener;
import com.nuc.jingbeibei.studentdailymanagement.beans.LeaveRecord;
import com.nuc.jingbeibei.studentdailymanagement.beans.Notice;
import com.nuc.jingbeibei.studentdailymanagement.utils.IntentUtils;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static java.security.AccessController.getContext;

public class AskLeaveRecordListActivity extends AppCompatActivity implements MyItemClickListener {
    private SharedPreferences pref;
    private boolean isTeacher = false;
    private BmobObject object;
    private XRecyclerView mRecyclerView;
    private LeaveRecordAdapter mAdapter;

    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多

    private int curPage = 0;        // 当前页的编号，从0开始
    private String lastTime;
    private int count = 10;// 每页的数据是10条

    List<LeaveRecord> records = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ask_leave_record_list);
        pref = getSharedPreferences("data", MODE_PRIVATE);
        isTeacher = pref.getBoolean("isTeacher", false);
        object = (BmobObject) getIntent().getSerializableExtra("object");

        mAdapter = new LeaveRecordAdapter(records);
        mAdapter.setListener(this);
        mRecyclerView = (XRecyclerView) findViewById(R.id.leave_record_recyclerview);
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

    @Override
    protected void onStart() {
        super.onStart();
        getData(0, STATE_REFRESH);
    }

    private void getData(int page, final int actionType) {
        BmobQuery<LeaveRecord> query = new BmobQuery<>();
        query.include("student,counselorName");
        // 按时间降序查询
        query.order("-createdAt");
        if (isTeacher) {
            query.addWhereEqualTo("counselorName", new BmobPointer(object));
        } else {
            query.addWhereEqualTo("student", new BmobPointer(object));
        }
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
        query.findObjects(new FindListener<LeaveRecord>() {
            @Override
            public void done(List<LeaveRecord> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {
                        if (actionType == STATE_REFRESH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                            curPage = 0;
                            records.clear();
                            // 获取最后时间
                            lastTime = list.get(list.size() - 1).getCreatedAt();
                        }

                        records.addAll(list);
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
                        ToastUtils.toast(AskLeaveRecordListActivity.this, "没有更多数据了");
                    } else if (actionType == STATE_REFRESH) {
                        ToastUtils.toast(AskLeaveRecordListActivity.this, "没有数据");
                    }

                } else {
                    ToastUtils.toast(AskLeaveRecordListActivity.this, "查询失败");
                    mRecyclerView.refreshComplete();
                }
            }
        });

    }

    @Override
    public void onItemClick(View view, int postion) {
        IntentUtils.doIntentWithObject(AskLeaveRecordListActivity.this, AskForLeaveDetailsActivity.class, "object", records.get(postion - 1));
    }
}