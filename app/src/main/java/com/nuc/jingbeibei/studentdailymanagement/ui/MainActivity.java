package com.nuc.jingbeibei.studentdailymanagement.ui;

import android.content.SharedPreferences;
import android.support.design.widget.TabLayout;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Window;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.adapter.SimpleFragmentPagerAdapter;
import com.nuc.jingbeibei.studentdailymanagement.app.ActivityCollector;

import cn.bmob.v3.BmobObject;

public class MainActivity extends AppCompatActivity {
    private SimpleFragmentPagerAdapter pagerAdapter;
    private ViewPager viewPager;
    private TabLayout tabLayout;
    private BmobObject personnel;
    private SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);
        ActivityCollector.addActivity(this);


        pagerAdapter = new SimpleFragmentPagerAdapter(getSupportFragmentManager(), this, personnel);
        viewPager = (ViewPager) findViewById(R.id.viewpager);
        tabLayout = (TabLayout) findViewById(R.id.tabs);

        viewPager.setAdapter(pagerAdapter);
        tabLayout.setupWithViewPager(viewPager);
        TabLayout.Tab one = tabLayout.getTabAt(0);
        TabLayout.Tab two = tabLayout.getTabAt(1);
        TabLayout.Tab three = tabLayout.getTabAt(2);

        one.setCustomView(R.layout.item_tab_layout_custom);
        two.setCustomView(R.layout.item_tab_two_layout_custom);
        three.setCustomView(R.layout.item_tab_three_layout_custom);
        initEvent();

    }

    private void initEvent() {

    }
}
