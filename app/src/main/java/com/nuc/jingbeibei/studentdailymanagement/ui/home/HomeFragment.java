package com.nuc.jingbeibei.studentdailymanagement.ui.home;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.BannerPic;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;
import com.nuc.jingbeibei.studentdailymanagement.ui.home.ImageSlideshow.ImageSlideshow;
import com.nuc.jingbeibei.studentdailymanagement.utils.IntentUtils;
import com.nuc.jingbeibei.studentdailymanagement.utils.ToastUtils;
import com.zhy.http.okhttp.OkHttpUtils;
import com.zhy.http.okhttp.callback.StringCallback;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobQuery;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.QueryListener;
import okhttp3.Call;

public class HomeFragment extends Fragment {
    //终于成功了...
    private SharedPreferences pref;
    private Button idClassManagementBtn;
    private String objectId = "";
    private boolean isTeacher = false;
    private BmobObject object;
    private boolean isCounselor = false;
    private Button idClassCounselorBtn;
    private Button idNoticeBtn;
    private Button idAddressBookBtn;
    private Button idAskForLeaveBtn;
    private Button idSignBtn;
    private Button idstudentSignBtn;
    private ImageSlideshow imageSlideshow;
    private List<String> imageUrlList;
    private List<String> titleList;
    private List<BannerPic> bannerPicsList = null;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getActivity().getSharedPreferences("data", getContext().MODE_PRIVATE);
        objectId = pref.getString("objectid", "");
        isTeacher = pref.getBoolean("isTeacher", false);
        if (isTeacher) {
            getTeacherObject();
        } else {
            getStudentObject();
        }

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        initView(view);
        initEvent();
        return view;
    }

    private void initView(View view) {
        idClassManagementBtn = (Button) view.findViewById(R.id.id_class_management_btn);
        idClassCounselorBtn = (Button) view.findViewById(R.id.id_class_counselor_btn);
        idNoticeBtn = (Button) view.findViewById(R.id.id_notice_btn);
        idAddressBookBtn = (Button) view.findViewById(R.id.id_address_book_btn);
        idAskForLeaveBtn = (Button) view.findViewById(R.id.id_ask_for_leave_btn);
        idSignBtn= (Button) view.findViewById(R.id.id_sign_btn);
        idstudentSignBtn= (Button) view.findViewById(R.id.id_student_sign_btn);
        imageSlideshow = (ImageSlideshow) view.findViewById(R.id.is_gallery);
        imageUrlList = new ArrayList<>();
        titleList = new ArrayList<>();
        OkHttpUtils.get().url("http://59.48.248.41:1020/iNUC/api/interface/GetPictureNews?").addParams("pageSize","5").build()
                .execute(new StringCallback() {
                    @Override
                    public void onError(Call call, Exception e, int id) {

                    }

                    @Override
                    public void onResponse(String response, int id) {
                        if (response.indexOf("TitlePicture") > 0) {
                            bannerPicsList = new Gson().fromJson(response, new TypeToken<List<BannerPic>>() {}.getType());
                            // 初始化数据
                            initData();
                        }
                    }
                });

    }

    private void initEvent() {
        idClassManagementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                IntentUtils.doIntent(getActivity(), ClassManageActivity.class);
                IntentUtils.doIntentWithObject(getActivity(), ClassManageActivity.class, "object", object);
            }
        });
        idClassCounselorBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isCounselor) {
//                    IntentUtils.doIntent(getActivity(), ClassManageActivity.class);
                    IntentUtils.doIntentWithObject(getActivity(), ClassCounselorActivity.class, "object", object);
                } else {
                    ToastUtils.toast(getActivity(), "您还不是辅导员，暂时无法使用此功能");
                }
            }
        });
        idNoticeBtn.setOnClickListener(new View.OnClickListener() {//公告
            @Override
            public void onClick(View v) {
                if (isTeacher) {
                    IntentUtils.doIntentWithObject(getActivity(), NoticeActivity.class, "object", object);
                } else {
                    IntentUtils.doIntentWithObject(getActivity(), NoticeStudentListActivity.class, "object", object);
                }

            }
        });
        idAddressBookBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.doIntentWithObject(getActivity(), AddressBookActivity.class, "object", object);
            }
        });
        idAskForLeaveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isTeacher) {
                    IntentUtils.doIntentWithObject(getActivity(), AskLeaveRecordListActivity.class, "object", object);
                } else {
                    IntentUtils.doIntentWithObject(getActivity(), AskForLeaveActivity.class, "object", object);
                }
            }
        });
        idSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.doIntentWithObject(getActivity(),PublishSignActivity.class,"object",object);
            }
        });
        idstudentSignBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.doIntentWithObject(getActivity(),SignListStudentActivity.class,"object",object);
            }
        });
    }

    private void getTeacherObject() {
        BmobQuery<Teacher> query = new BmobQuery<Teacher>();
        query.getObject(objectId, new QueryListener<Teacher>() {

            @Override
            public void done(Teacher object1, BmobException e) {
                if (e == null) {
                    object = object1;
                    isCounselor = object1.getIsCounselor();
                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }

    private void getStudentObject() {
        BmobQuery<Student> query = new BmobQuery<Student>();
        query.include("studentClass.counselor");
        query.getObject(objectId, new QueryListener<Student>() {

            @Override
            public void done(Student object1, BmobException e) {
                if (e == null) {
                    object = object1;

                } else {
                    Log.i("bmob", "失败：" + e.getMessage() + "," + e.getErrorCode());
                }
            }
        });
    }
    /**
     * 初始化数据
     */
    private void initData() {

        String[] imageUrls = {"http://pic3.zhimg.com/b5c5fc8e9141cb785ca3b0a1d037a9a2.jpg",
                "http://pic2.zhimg.com/551fac8833ec0f9e0a142aa2031b9b09.jpg",
                "http://pic2.zhimg.com/be6f444c9c8bc03baa8d79cecae40961.jpg",
                "http://pic1.zhimg.com/b6f59c017b43937bb85a81f9269b1ae8.jpg",
                "http://pic2.zhimg.com/a62f9985cae17fe535a99901db18eba9.jpg"};
        String[] titles = {"读读日报 24 小时热门 TOP 5 · 余文乐和「香港贾玲」乌龙绯闻",
                "写给产品 / 市场 / 运营的数据抓取黑科技教程",
                "学做这些冰冰凉凉的下酒宵夜，简单又方便",
                "知乎好问题 · 有什么冷门、小众的爱好？",
                "欧洲都这么发达了，怎么人均收入还比美国低"};
        if (bannerPicsList != null) {
            for (int i = 0; i < bannerPicsList.size(); i++) {
//            imageSlideshow.addImageTitle(imageUrls[i], titles[i]);
                imageSlideshow.addImageTitle(bannerPicsList.get(i).getTitlePicture(), bannerPicsList.get(i).getTitle());
            }
        } else {
            for (int i = 0; i < imageUrls.length; i++) {//以防获取不到数据
                imageSlideshow.addImageTitle(imageUrls[i], titles[i]);
//                imageSlideshow.addImageTitle(bannerPicsList.get(i).getTitlePicture(), bannerPicsList.get(i).getTitle());
            }
        }

        // 为ImageSlideshow设置数据        imageSlideshow.setDotSpace(12);
        imageSlideshow.setDotSize(12);
        imageSlideshow.setDelay(3000);
        imageSlideshow.setOnItemClickListener(new ImageSlideshow.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                switch (position) {
                    case 0:
                        Toast.makeText(getActivity(), "0", Toast.LENGTH_LONG).show();
//                        startActivity(new Intent(MainActivity.this,Activity_1.class));
                        break;
                    case 1:
                        Toast.makeText(getContext(), "1", Toast.LENGTH_LONG).show();
                        break;
                    case 2:
                        Toast.makeText(getContext(), "2", Toast.LENGTH_LONG).show();
                        break;
                    case 3:
                        Toast.makeText(getContext(), "3", Toast.LENGTH_LONG).show();
                        break;
                    case 4:
                        Toast.makeText(getContext(), "4", Toast.LENGTH_LONG).show();
                        break;
                }
            }
        });
        imageSlideshow.commit();
    }
}
