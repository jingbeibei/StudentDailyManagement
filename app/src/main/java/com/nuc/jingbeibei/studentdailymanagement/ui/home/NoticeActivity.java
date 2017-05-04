package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.adapter.MyItemClickListener;
import com.nuc.jingbeibei.studentdailymanagement.adapter.NoticeAdapter;
import com.nuc.jingbeibei.studentdailymanagement.app.ActivityCollector;
import com.nuc.jingbeibei.studentdailymanagement.beans.Notice;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;
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

public class NoticeActivity extends AppCompatActivity implements MyItemClickListener{
    private TextView BarTitle;
    private ImageView BackImage;
    private TextView BarRight;

    private BmobObject bmobObject;
    private XRecyclerView mRecyclerView;
    private NoticeAdapter mAdapter;

    private ArrayList<String> listData;
    private List<Notice> noticeList=new ArrayList<>();


    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多

    private int limit = 10;        // 每页的数据是10条
    private int curPage = 0;        // 当前页的编号，从0开始
    private String lastTime;
    private int count=10;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notice);
        ActivityCollector.addActivity(this);
        bmobObject = (BmobObject) getIntent().getSerializableExtra("object");
        initView();
        initEvent();
    }

    @Override
    protected void onStart() {
        super.onStart();
        queryData(0,STATE_REFRESH);
    }

    private void initView() {
        BarTitle = (TextView) findViewById(R.id.id_bar_title);
        BarRight= (TextView) findViewById(R.id.bar_right_tv);
        BackImage = (ImageView) findViewById(R.id.id_back_arrow_image);
        BarRight.setText("发布");
        BarRight.setVisibility(View.VISIBLE);
        BarTitle.setText("通知");

        listData = new ArrayList<String>();
        mAdapter = new NoticeAdapter(this, (ArrayList<Notice>) noticeList);
        mAdapter.setListener(this);
        mRecyclerView = (XRecyclerView) findViewById(R.id.notice_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        mRecyclerView.setArrowImageView(R.mipmap.banner_error);

        mRecyclerView.refresh();

        mRecyclerView.setPullRefreshEnabled(true);
    }

    private void initEvent() {
        BarRight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                IntentUtils.doIntentWithObject(NoticeActivity.this, publishNoticeActivity.class, "object", (Teacher) bmobObject);
            }
        });
        BackImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ActivityCollector.removeActivity(NoticeActivity.this);
            }
        });
        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
            @Override
            public void onRefresh() {
                queryData(0,STATE_REFRESH);
            }

            @Override
            public void onLoadMore() {
                queryData(curPage,STATE_MORE);
            }
        });

    }


    /**
     * 分页获取数据
     *
     * @param page       页码
     * @param actionType ListView的操作类型（下拉刷新、上拉加载更多）
     */
    private void queryData( int page, final int actionType) {
        Log.i("bmob", "pageN:" + page + " limit:" + limit + " actionType:" + actionType);

        BmobQuery<Notice> query = new BmobQuery<>();
        // 按时间降序查询
        query.order("-createdAt");
        query.addWhereEqualTo("publisher",new BmobPointer(bmobObject));
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
        query.findObjects(new FindListener<Notice>() {
            @Override
            public void done(List<Notice> list, BmobException e) {
                if (e == null) {
                    if (list.size() > 0) {

                        if (actionType == STATE_REFRESH) {
                            // 当是下拉刷新操作时，将当前页的编号重置为0，并把bankCards清空，重新添加
                            curPage = 0;
                            listData.clear();
                            noticeList.clear();
                            // 获取最后时间
                            lastTime = list.get(list.size() - 1).getCreatedAt();
                        }

                        // 将本次查询的数据添加到bankCards中
                        for (Notice notice : list) {
                            listData.add(notice.getTitle());
                        }
                        noticeList.addAll(list);
                        // 这里在每次加载完数据后，将当前页码+1，这样在上拉刷新的onPullUpToRefresh方法中就不需要操作curPage了
                        curPage++;
//                        showToast("第" + (page + 1) + "页数据加载完成");
//                        mAdapter.notifyDataSetChanged();
//                        mRecyclerView.refreshComplete();
                        if (actionType==STATE_REFRESH) {
                            mAdapter.notifyDataSetChanged();
                            mRecyclerView.refreshComplete();
                        }else {
                            lastTime = list.get(list.size() - 1).getCreatedAt();
                            mRecyclerView.loadMoreComplete();
                            mAdapter.notifyDataSetChanged();
                        }

                    } else if (actionType == STATE_MORE) {
                        mRecyclerView.setNoMore(true);
                        mAdapter.notifyDataSetChanged();
                        ToastUtils.toast(NoticeActivity.this,"没有更多数据了");
                    } else if (actionType == STATE_REFRESH) {
                        ToastUtils.toast(NoticeActivity.this,"没有数据");
                    }

                } else {
                    ToastUtils.toast(NoticeActivity.this,"查询失败");
                    mRecyclerView.refreshComplete();
                }
            }

        });
    }


    @Override
    public void onItemClick(View view, int postion) {
        Toast.makeText(this, "我是第" + postion + "项", Toast.LENGTH_SHORT).show();
        IntentUtils.doIntentWithObject(NoticeActivity.this,NoticeDetailsActivity.class,"notice",noticeList.get(postion-1));
    }
}