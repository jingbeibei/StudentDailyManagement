package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.adapter.MyItemClickListener;
import com.nuc.jingbeibei.studentdailymanagement.adapter.SignRecordAdapter;
import com.nuc.jingbeibei.studentdailymanagement.beans.LeaveRecord;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignType;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.beans.StudentClass;
import com.nuc.jingbeibei.studentdailymanagement.utils.IntentUtils;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobDate;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

public class SignListStudentActivity extends AppCompatActivity implements MyItemClickListener {
    private XRecyclerView mRecyclerView;
    private Student student;
    private SignRecordAdapter mAdapter;
    private ArrayList<SignType> signTypes;
    private String studentClassId = "";


    private static final int STATE_REFRESH = 0;// 下拉刷新
    private static final int STATE_MORE = 1;// 加载更多

    private int curPage = 0;        // 当前页的编号，从0开始
    private String lastTime;
    private int count = 10;// 每页的数据是10条

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sign_list);
        student = (Student) getIntent().getSerializableExtra("object");
        studentClassId = student.getStudentClass().getObjectId();
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

    private void getData(int page, final int actionType) {
        BmobQuery<SignType> query = new BmobQuery<>();
        query.include("publisher");
        query.include("visibleClass");
        // 按时间降序查询
        query.order("-createdAt");
//        query.addWhereContainsAll("visibleClass", Arrays.asList(student.getStudentClass()));
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
                        for (final SignType signType : list) {//遍历每条记录
//                            signType.getVisibleClass().getObjects();
//                            int size=signType.getVisibleClass().getObjects().size();//得到一条记录中的班级数量
//                            for(int i=0;i<size;i++)
//                            if( signType.getVisibleClass().getObjects().get(i).getObjectId().equals(student.getStudentClass().getObjectId())){
//                                signTypes.add(signType);
//                                break;
//                            }
                            BmobQuery<StudentClass> query = new BmobQuery<StudentClass>();
                            query.addWhereRelatedTo("visibleClass", new BmobPointer(signType));
                            query.findObjects(new FindListener<StudentClass>() {

                                @Override
                                public void done(List<StudentClass> studentClasses, BmobException e) {
                                    if (e == null) {
                                        for (StudentClass studentClass : studentClasses) {
                                            if (studentClass.getObjectId().equals(studentClassId)) {
                                                signTypes.add(signType);
                                                break;
                                            }
                                        }
//                                        Log.i("bmob","查询个数："+object.size());
                                    } else {
                                        Log.i("bmob", "失败：" + e.getMessage());
                                    }
                                }

                            });
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
                        ToastUtils.toast(SignListStudentActivity.this, "没有更多数据了");
                    } else if (actionType == STATE_REFRESH) {
                        ToastUtils.toast(SignListStudentActivity.this, "没有数据");
                    }

                } else {
                    ToastUtils.toast(SignListStudentActivity.this, "查询失败");
                    mRecyclerView.refreshComplete();
                }
            }
        });

    }

    @Override
    public void onItemClick(View view, int postion) {
        Toast.makeText(this, "我是第" + postion + "项", Toast.LENGTH_SHORT).show();
//        IntentUtils.doIntentWithObject(NoticeActivity.this,NoticeDetailsActivity.class,"notice",noticeList.get(postion-1));
    }
}