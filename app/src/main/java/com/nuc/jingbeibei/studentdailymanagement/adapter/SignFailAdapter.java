package com.nuc.jingbeibei.studentdailymanagement.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.SignRecord;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jingbeibei on 2017/5/4.
 */

public class SignFailAdapter extends RecyclerView.Adapter<SignFailAdapter.ViewHolder>{
    private MyItemClickListener listener;
    private ArrayList<SignRecord> signRecords;
    private Context mContext;
    public SignFailAdapter(ArrayList<SignRecord> signRecords,Context context) {
        this.signRecords = signRecords;
        this.mContext=context;
    }

    public void setListener(MyItemClickListener listener) {
        this.listener = listener;
    }


    @Override
    public SignFailAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_layout,parent,false);
        return new SignFailAdapter.ViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(SignFailAdapter.ViewHolder holder, int position) {
        holder.studentNoTV.setText(signRecords.get(position).getStudent().getUserId());
        holder.studentClassTV.setText(signRecords.get(position).getStudent().getStudentClass().getClassNo());
        holder.nameTV.setText(signRecords.get(position).getStudent().getRealName());
        String pic=signRecords.get(position).getStudent().getPicpath();
        if (pic!=null){
        Picasso.with(mContext)
                .load(pic)
                .placeholder(R.mipmap.ic_image_loading)
                .error(R.mipmap.ic_image_loadfail)
                .into(holder.headIV);
        }
    }

    @Override
    public int getItemCount() {
        return signRecords.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private MyItemClickListener listener;
        private ImageView headIV;
        private TextView nameTV;
        private TextView studentClassTV;
        private TextView studentNoTV;

        public ViewHolder(View itemView,MyItemClickListener myItemClickListener) {
            super(itemView);
            headIV= (ImageView) itemView.findViewById(R.id.headIV);
            nameTV= (TextView) itemView.findViewById(R.id.student_name_text);
            studentClassTV= (TextView) itemView.findViewById(R.id.student_class_text);
            studentNoTV= (TextView) itemView.findViewById(R.id.student_number_text);
            listener=myItemClickListener;
            itemView.setOnClickListener(ViewHolder.this);
        }

        @Override
        public void onClick(View v) {
            int positon=getAdapterPosition();
            listener.onItemClick(v,getAdapterPosition());
        }
    }
}
