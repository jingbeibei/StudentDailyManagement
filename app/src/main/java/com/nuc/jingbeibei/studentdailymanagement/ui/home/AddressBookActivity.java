package com.nuc.jingbeibei.studentdailymanagement.ui.home;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.ContextCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.jcodecraeer.xrecyclerview.ProgressStyle;
import com.jcodecraeer.xrecyclerview.XRecyclerView;
import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.adapter.NoticeAdapter;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

public class AddressBookActivity extends AppCompatActivity {
    private TabLayout mTabTl;
    private ViewPager mContentVp;
    private BmobObject bmobObject;

//    private TextView BarTitle;
//    private ImageView BackImage;
//    private TextView BarRight;

    private List<String> tabIndicators;
    private List<Fragment> tabFragments;
    private ContentPagerAdapter contentAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_address_book);

        bmobObject = (BmobObject) getIntent().getSerializableExtra("object");
        initView();
        initEvent();
        initTab();
        initContent();
    }

    private void initView() {
        mTabTl = (TabLayout) findViewById(R.id.tl_tab);
        mContentVp = (ViewPager) findViewById(R.id.vp_content);

    }

    private void initEvent() {

    }

    private void initTab() {
        mTabTl.setTabMode(TabLayout.MODE_FIXED);
        mTabTl.setTabTextColors(ContextCompat.getColor(this, R.color.gray), ContextCompat.getColor(this, R.color.colorWhite));
        mTabTl.setSelectedTabIndicatorColor(ContextCompat.getColor(this, R.color.colorWhite));
        ViewCompat.setElevation(mTabTl, 10);//提供兼容性
        mTabTl.setupWithViewPager(mContentVp);
    }

    private void initContent() {
        tabIndicators = new ArrayList<>();

        tabIndicators.add("老师");
        tabIndicators.add("学生");


        tabFragments = new ArrayList<>();
        for (String s : tabIndicators) {
            tabFragments.add(AddressBookPageFragment.newInstance(s, bmobObject));
        }
        contentAdapter = new ContentPagerAdapter(getSupportFragmentManager());
        mContentVp.setAdapter(contentAdapter);
    }

    class ContentPagerAdapter extends FragmentPagerAdapter {

        public ContentPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return tabFragments.get(position);
        }

        @Override
        public int getCount() {
            return tabIndicators.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return tabIndicators.get(position);
        }

    }

}
