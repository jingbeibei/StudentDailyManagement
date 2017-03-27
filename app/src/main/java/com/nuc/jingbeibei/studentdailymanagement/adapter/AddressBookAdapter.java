package com.nuc.jingbeibei.studentdailymanagement.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by jingbeibei on 2017/3/27.
 */

public class AddressBookAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder> {
    public List<BmobObject> datas = null;
    private MyItemClickListener listener;
    private boolean isTeacher;

    public void setListener(MyItemClickListener listener) {
        this.listener = listener;
    }

    public AddressBookAdapter(List<BmobObject> datas, boolean isTeacher) {
        this.datas = datas;
        this.isTeacher = isTeacher;
    }

    @Override
    public NoticeAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item, parent, false);
        return new NoticeAdapter.ViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(NoticeAdapter.ViewHolder holder, int position) {
        if (isTeacher) {
            Teacher s = (Teacher) datas.get(position);
            holder.mTextView.setText(s.getRealName());
        } else {
            Student s = (Student) datas.get(position);
            holder.mTextView.setText(s.getRealName());
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public TextView mTextView;
        private MyItemClickListener listener;

        public ViewHolder(View view, MyItemClickListener listener) {
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text);
            this.listener = listener;
            view.setOnClickListener(AddressBookAdapter.ViewHolder.this);
        }

        @Override
        public void onClick(View v) {
            int positon = getAdapterPosition();
            listener.onItemClick(v, getAdapterPosition());
        }
    }
}

