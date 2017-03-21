package com.nuc.jingbeibei.studentdailymanagement.ui.home;


import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;

import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.utils.IntentUtils;

import static android.content.Context.MODE_PRIVATE;


public class HomeFragment extends Fragment {

    private SharedPreferences pref;
    private Button idClassManagementBtn;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        pref = getActivity().getSharedPreferences("data", getContext().MODE_PRIVATE);
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
        idClassManagementBtn= (Button) view.findViewById(R.id.id_class_management_btn);
    }

    private void initEvent() {
        idClassManagementBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                IntentUtils.doIntent(getActivity(),ClassManageActivity.class);
            }
        });
    }

}
