package com.nuc.jingbeibei.studentdailymanagement.adapter;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignType;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by jingbeibei on 2017/4/22.
 */

public class SignRecordAdapter extends RecyclerView.Adapter<SignRecordAdapter.ViewHolder>{
    public ArrayList<SignType> datas = null;
    SimpleDateFormat sdf =   new SimpleDateFormat( "yyyy-MM-dd HH:mm:ss" );

    public void setListener(MyItemClickListener listener) {
        this.listener = listener;
    }

    private MyItemClickListener listener;

    public SignRecordAdapter(ArrayList<SignType> datas) {
        this.datas = datas;
    }

    @Override
    public SignRecordAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.sign_record_item,parent,false);
        return new SignRecordAdapter.ViewHolder(view,listener);

    }

    @Override
    public void onBindViewHolder(SignRecordAdapter.ViewHolder holder, int position) {
        holder.signTileTV.setText(datas.get(position).getTitle());
        System.out.println("---------------标题--------"+datas.get(position).getTitle());
        try {
         if(new Date().before(sdf.parse(datas.get(position).getEndTime()))){//当前时间在结束时间之前
             holder.stateTV.setText("未完成");
         }else {
                holder.stateTV.setText("已完成");
            }

        } catch (ParseException e) {
            e.printStackTrace();
        }

    }

    @Override
    public int getItemCount() {
        return datas.size();
    }
    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView signTileTV,stateTV;
        private MyItemClickListener listener;
        public ViewHolder(View view,MyItemClickListener listener){
            super(view);
            signTileTV = (TextView) view.findViewById(R.id.sign_title);
            stateTV= (TextView) view.findViewById(R.id.sign_state);
            this.listener=listener;
            view.setOnClickListener(SignRecordAdapter.ViewHolder.this);
        }

        @Override
        public void onClick(View v) {
            int positon=getAdapterPosition();
            listener.onItemClick(v,getAdapterPosition());
        }
    }
}
