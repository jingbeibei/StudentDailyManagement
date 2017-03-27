package com.nuc.jingbeibei.studentdailymanagement.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;

import java.util.ArrayList;

/**
 * Created by jingbeibei on 2017/3/24.
 */

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder>{
    public ArrayList<String> datas = null;

    public void setListener(MyItemClickListener listener) {
        this.listener = listener;
    }

    private MyItemClickListener listener;

    public NoticeAdapter(ArrayList<String> datas) {
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item,parent,false);
        return new ViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(datas.get(position));
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTextView;
        private MyItemClickListener listener;
        public ViewHolder(View view,MyItemClickListener listener){
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text);
            this.listener=listener;
            view.setOnClickListener(ViewHolder.this);
        }

        @Override
        public void onClick(View v) {
            int positon=getAdapterPosition();
            listener.onItemClick(v,getAdapterPosition());
        }
    }
}
