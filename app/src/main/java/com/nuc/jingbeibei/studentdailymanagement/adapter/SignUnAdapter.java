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
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jingbeibei on 2017/5/4.
 */

public class SignUnAdapter extends RecyclerView.Adapter<SignUnAdapter.ViewHolder>{
    private MyItemClickListener listener;
    private ArrayList<Student> students;
    private Context mContext;

    public SignUnAdapter(ArrayList<Student> students, Context mContext) {
        this.students= students;
        this.mContext = mContext;
    }
    public void setListener(MyItemClickListener listener) {
        this.listener = listener;
    }

    @Override
    public SignUnAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_student_layout,parent,false);
        return new SignUnAdapter.ViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(SignUnAdapter.ViewHolder holder, int position) {
        holder.studentNoTV.setText(students.get(position).getUserId());
        holder.studentClassTV.setText(students.get(position).getStudentClass().getClassNo());
        holder.nameTV.setText(students.get(position).getRealName());
        String pic=students.get(position).getPicpath();
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
        return students.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private MyItemClickListener listener;
        private ImageView headIV;
        private TextView nameTV;
        private TextView studentClassTV;
        private TextView studentNoTV;

        public ViewHolder(View itemView, MyItemClickListener listener) {
            super(itemView);
            headIV= (ImageView) itemView.findViewById(R.id.headIV);
            nameTV= (TextView) itemView.findViewById(R.id.student_name_text);
            studentClassTV= (TextView) itemView.findViewById(R.id.student_class_text);
            studentNoTV= (TextView) itemView.findViewById(R.id.student_number_text);
            this.listener=listener;
            itemView.setOnClickListener( ViewHolder.this);
        }

        @Override
        public void onClick(View v) {
            int positon=getAdapterPosition();
            listener.onItemClick(v,getAdapterPosition());
        }
    }
}
