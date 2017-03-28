package com.nuc.jingbeibei.studentdailymanagement.ui.home;


import android.Manifest;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.adapter.AddressBookAdapter;
import com.nuc.jingbeibei.studentdailymanagement.adapter.MyItemClickListener;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.beans.StudentClass;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;
import com.nuc.jingbeibei.studentdailymanagement.utils.IntentUtils;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.datatype.BmobPointer;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.FindListener;

import static android.content.Context.MODE_PRIVATE;

/**
 * A simple {@link Fragment} subclass.
 */
public class AddressBookPageFragment extends Fragment implements MyItemClickListener {
    private static final String EXTRA_TYPE = "type";
    private static final String EXTRA_OBJECTID = "objectId";
    private String objectId;
    private BmobObject bmobObject;
    private String type = "";
    private SharedPreferences pref;
    private boolean isTeacher;

    private XRecyclerView mRecyclerView;
    private List objectList = null;
    private List<BmobObject> studentList = new ArrayList<>();
    private List<BmobObject> teacherList = new ArrayList<>();
    private AddressBookAdapter maddressBookAdapter;

    public AddressBookPageFragment() {
        // Required empty public constructor
    }

    public static AddressBookPageFragment newInstance(String content, BmobObject bmobObject) {
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_TYPE, content);
        arguments.putSerializable(EXTRA_OBJECTID, bmobObject);
        AddressBookPageFragment addressBookPageFragment = new AddressBookPageFragment();
        addressBookPageFragment.setArguments(arguments);
        return addressBookPageFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_address_book_page, container, false);
        pref = getActivity().getSharedPreferences("data", MODE_PRIVATE);
        isTeacher = pref.getBoolean("isTeacher", false);
        type = getArguments().getString(EXTRA_TYPE);
        bmobObject = (BmobObject) getArguments().getSerializable(EXTRA_OBJECTID);
        initView(view);
//        initEvent();
        // Inflate the layout for this fragment
        return view;


    }


    private void initView(View view) {

//        maddressBookAdapter = new AddressBookAdapter(studentList, isTeacher);

        if (type.equals("学生")) {//学生：进入学生选项卡
            maddressBookAdapter = new AddressBookAdapter(studentList, false);
            if (isTeacher) {//是老师
                queryStudentOfTeacher();
            } else {//不是老师
                queryStudentOfStudent();
            }
//            maddressBookAdapter=new AddressBookAdapter(studentList,isTeacher);
//            mRecyclerView.setAdapter(maddressBookAdapter);
        } else {//老师选项卡
            maddressBookAdapter = new AddressBookAdapter(teacherList, true);
            if (isTeacher) {//是老师
                queryAllTeacher();
            } else {//不是老师
                queryTeacherByStudentID();
            }
        }
        maddressBookAdapter.setListener(this);
        mRecyclerView = (XRecyclerView) view.findViewById(R.id.address_book_recyclerview);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setAdapter(maddressBookAdapter);
        mRecyclerView.setRefreshProgressStyle(ProgressStyle.BallSpinFadeLoader);
        mRecyclerView.setLoadingMoreProgressStyle(ProgressStyle.Pacman);
        mRecyclerView.setArrowImageView(R.mipmap.banner_error);
        mRecyclerView.setPullRefreshEnabled(false);
        mRecyclerView.setLoadingMoreEnabled(false);
        mRecyclerView.refresh();

    }

//    private void initEvent() {
//        mRecyclerView.setLoadingListener(new XRecyclerView.LoadingListener() {
//            @Override
//            public void onRefresh() {
//                queryData(0, STATE_REFRESH);
//            }
//
//            @Override
//            public void onLoadMore() {
//                queryData(curPage, STATE_MORE);
//            }
//        });
//
//    }

    private void queryAllTeacher() {//查询出与老师相关的所有教师
        BmobQuery<Teacher> query = new BmobQuery<Teacher>();
        // 根据score字段升序显示数据
        query.order("realName");
//返回500条数据，如果不加上这条语句，最多1000
        query.setLimit(500);
//执行查询方法
        query.findObjects(new FindListener<Teacher>() {
            @Override
            public void done(List<Teacher> object, BmobException e) {
                if (e == null) {
//                    toast("查询成功：共"+object.size()+"条数据。");
                    for (Teacher teacher : object) {
//
                    }
                    teacherList.addAll(object);
                    maddressBookAdapter.notifyDataSetChanged();
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });

    }

    private void queryTeacherByStudentID() {//查询出与学生相关的老师信息
        Student s = (Student) bmobObject;
        final Teacher counselor = s.getStudentClass().getCounselor();//辅导员

        BmobQuery<Teacher> query = new BmobQuery<Teacher>();//查询任课老师
        // 根据realName字段升序显示数据
        query.order("realName");
        query.addWhereRelatedTo("holdClass", new BmobPointer(s.getStudentClass()));
        //返回500条数据，如果不加上这条语句，最多1000
        query.setLimit(500);
        //执行查询方法
        query.findObjects(new FindListener<Teacher>() {
            @Override
            public void done(List<Teacher> object, BmobException e) {
                if (e == null) {

                    for (Teacher teacher : object) {
                        if (counselor != null) {
                            if (!teacher.getUserId().equals(counselor.getUserId())) {//去掉重复的老师 因为辅导员和上课老师可能相同
                                teacherList.add(teacher);
                            }
                        } else {
                            teacherList.add(teacher);
                        }
                    }

                    maddressBookAdapter.notifyDataSetChanged();
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    /**
     * 查询出与老师相关的所有学生
     */
    private void queryStudentOfTeacher() {//查询出与老师相关的所有学生
        final ArrayList<StudentClass> holdClassList = new ArrayList<>();
        BmobQuery<StudentClass> query = new BmobQuery<StudentClass>();//管理的所有班级

        query.addWhereRelatedTo("holdClass", new BmobPointer(bmobObject));
        query.findObjects(new FindListener<StudentClass>() {

            @Override
            public void done(List<StudentClass> object, BmobException e) {
                if (e == null) {
                    if (object.size() != 0) {
                        holdClassList.addAll(object);
                    }
                    getTeacherClass(holdClassList);
//                myAdapter.setTeacherHolderClass(teacherClassService);
//                myAdapter.notifyDataSetChanged();
                    Log.i("bmob", "查询个数：" + object.size());
                } else {
                    Log.i("bmob", "失败：" + e.getMessage());
                }
            }

        });


    }

    private void getTeacherClass(final ArrayList<StudentClass> allStudentClassList) {//获得老师所辅导的班级
        final List<StudentClass> studentClassList = new ArrayList<StudentClass>();
        studentClassList.addAll(allStudentClassList);
        BmobQuery<StudentClass> query = new BmobQuery<StudentClass>();
//用此方式可以构造一个BmobPointer对象
        query.addWhereEqualTo("counselor", new BmobPointer(bmobObject));
        query.findObjects(new FindListener<StudentClass>() {

            @Override
            public void done(List<StudentClass> object, BmobException e) {

                if (e == null) {
                    if (object.size() != 0) {
                        for (StudentClass studentclass : object) {
                            String classNo = studentclass.getClassNo();
                            for (StudentClass studentClass2 : allStudentClassList)
                                if (classNo.equals(studentClass2.getClassNo())) {
                                    studentClassList.remove(studentClass2);//去掉重复班级
                                }

                        }
                        studentClassList.addAll(object);//得到所有班号了
                        queryStudentByClass(studentClassList);
                    }

                    Log.i("bmob", "查询个数：" + object.size());
                } else {
                    Log.i("bmob", "失败：" + e.getMessage());
                }
            }
        });
    }

    private void queryStudentOfStudent() {//查询同班学生的信息
        BmobQuery<Student> query = new BmobQuery<Student>();
        Student s = (Student) bmobObject;
        query.addWhereEqualTo("studentClass", new BmobPointer(s.getStudentClass()));

        query.findObjects(new FindListener<Student>() {

            @Override
            public void done(List<Student> objects, BmobException e) {
                if (e == null) {
                    if (objects.size() != 0) {
                        studentList.addAll(objects);
                        maddressBookAdapter.notifyDataSetChanged();
                    }
                }
            }
        });

    }

    private void queryStudentByClass(List<StudentClass> list) {


        for (StudentClass studentClass : list) {//循环发起请求
            BmobQuery<Student> query = new BmobQuery<Student>();
            query.addWhereEqualTo("studentClass", new BmobPointer(studentClass));
            query.findObjects(new FindListener<Student>() {

                @Override
                public void done(List<Student> objects, BmobException e) {
                    if (e == null) {
                        if (objects.size() != 0) {
                            studentList.addAll(objects);
                            maddressBookAdapter.notifyDataSetChanged();
                        }
                    }
                }
            });
        }

    }


    @Override
    public void onItemClick(View view, int postion) {
        String phone="";
        if (type.equals("学生")) {//学生：进入学生选项卡
            Student s= (Student) studentList.get(postion-1);
           phone=s.getTelephoneNo();
        } else {//老师选项卡
           Teacher t= (Teacher) teacherList.get(postion-1);
            phone=t.getTelephoneNo();
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(
                getActivity());
        final String finalPhone = phone;
        builder.setMessage("是否拨打电话？").setPositiveButton("取消", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        }).setNegativeButton("确认", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                String phone_number = "10086";
                Intent intent2 = new Intent(Intent.ACTION_CALL, Uri.parse("tel:"
                        + finalPhone));
                if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    ToastUtils.toast(getActivity(), "请开启打电话权限");
                    return;
                }
                getActivity().startActivity(intent2);
            }
        }).show();
    }
}
