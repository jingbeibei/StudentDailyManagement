package com.nuc.jingbeibei.studentdailymanagement.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.LeaveRecord;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by jingbeibei on 2017/3/28.
 */

public class LeaveRecordAdapter extends RecyclerView.Adapter< LeaveRecordAdapter.ViewHolder>{
    private List<LeaveRecord> datas =null;
    private Context mContext;
    public void setListener(MyItemClickListener listener) {
        this.listener = listener;
    }

    private MyItemClickListener listener;
    public LeaveRecordAdapter(List<LeaveRecord> datas) {
        this.datas = datas;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_leave_layout, parent, false);
        return new LeaveRecordAdapter.ViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(LeaveRecordAdapter.ViewHolder holder, int position) {
        holder.reasonTV.setText(datas.get(position).getReason());
        holder.classTV.setText(datas.get(position).getStudent().getStudentClass().getClassNo());
        holder.nameTV.setText(datas.get(position).getStudent().getRealName());
        String pic=datas.get(position).getStudent().getPicpath();
        if (pic!=null){
            Picasso.with(mContext)
                    .load(pic)
                    .placeholder(R.mipmap.ic_image_loading)
                    .error(R.mipmap.ic_image_loadfail)
                    .into(holder.headIV);
        }
        String state=datas.get(position).getState();
        if (state.equals("同意")){
            holder.layout.setBackgroundResource(R.mipmap.ic_success);
        }
        if (state.equals("拒绝")){
            holder.layout.setBackgroundResource(R.mipmap.ic_refuse);
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }


    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView headIV;
        private TextView nameTV;
        private TextView reasonTV;
        private TextView classTV;
        private LinearLayout layout;

        private MyItemClickListener listener;
        public ViewHolder(View view,MyItemClickListener listener){
            super(view);
            this.listener=listener;
            view.setOnClickListener(LeaveRecordAdapter.ViewHolder.this);
            headIV= (ImageView) itemView.findViewById(R.id.headIV);
            nameTV= (TextView) itemView.findViewById(R.id.student_name_text);
           reasonTV= (TextView) itemView.findViewById(R.id.reason_text);
            classTV= (TextView) itemView.findViewById(R.id.student_class_text);
            layout= (LinearLayout) itemView.findViewById(R.id.leave_layout);
        }

        @Override
        public void onClick(View v) {
            int positon=getAdapterPosition();
            listener.onItemClick(v,getAdapterPosition());
        }
    }
}
