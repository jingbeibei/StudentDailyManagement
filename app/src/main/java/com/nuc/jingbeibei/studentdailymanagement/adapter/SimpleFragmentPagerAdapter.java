package com.nuc.jingbeibei.studentdailymanagement.adapter;

import android.content.Context;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.nuc.jingbeibei.studentdailymanagement.ui.home.PageFragment;

import cn.bmob.v3.BmobObject;
import cn.bmob.v3.BmobUser;

/**
 * Created by jingbeibei on 2017/3/17.
 */

public class SimpleFragmentPagerAdapter extends FragmentPagerAdapter {

    private Context context;
    private static final String[] mTitles = {"ZHUYE", "tab2", "tab3"};
    private BmobObject personnel;
    public SimpleFragmentPagerAdapter(FragmentManager fm, Context context, BmobObject personnel) {
        super(fm);
        this.context = context;
        this.personnel=personnel;
    }

    @Override
    public android.support.v4.app.Fragment getItem(int position) {

        return PageFragment.newInstance(position + 1,personnel);
    }

    @Override
    public int getCount() {
        return mTitles.length;
    }

    @Override
    public CharSequence getPageTitle(int position) {
        return mTitles[position];
    }
}

