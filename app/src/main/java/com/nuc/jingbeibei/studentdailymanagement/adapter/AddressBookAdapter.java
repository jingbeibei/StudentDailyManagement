package com.nuc.jingbeibei.studentdailymanagement.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.Student;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

import cn.bmob.v3.BmobObject;

/**
 * Created by jingbeibei on 2017/3/27.
 */

public class AddressBookAdapter extends RecyclerView.Adapter<AddressBookAdapter.ViewHolder> {
    private List<BmobObject> datas = null;
    private MyItemClickListener listener;
    private boolean isTeacher;
    private Context mContext;

    public void setListener(MyItemClickListener listener) {
        this.listener = listener;
    }

    public AddressBookAdapter(List<BmobObject> datas, boolean isTeacher,Context context) {
        this.datas = datas;
        this.isTeacher = isTeacher;
        this.mContext=context;
    }

    @Override
    public AddressBookAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_address_book_layout, parent, false);
        return new AddressBookAdapter.ViewHolder(view, listener);
    }


    @Override
    public void onBindViewHolder(AddressBookAdapter.ViewHolder holder, int position) {
        if (isTeacher) {
            Teacher s = (Teacher) datas.get(position);
            holder.studentNoTV.setText(s.getUserId());
            holder.studentClassTV.setText(s.getTelephoneNo());
            holder.nameTV.setText(s.getRealName());
            String pic=s.getPicpath();
            if (pic!=null){
                Picasso.with(mContext)
                        .load(pic)
                        .placeholder(R.mipmap.ic_image_loading)
                        .error(R.mipmap.ic_image_loadfail)
                        .into(holder.headIV);
            }
        } else {
            Student s = (Student) datas.get(position);
            holder.studentNoTV.setText(s.getUserId());
            holder.studentClassTV.setText(s.getTelephoneNo());
            holder.nameTV.setText(s.getRealName());
            String pic=s.getPicpath();
            if (pic!=null){
                Picasso.with(mContext)
                        .load(pic)
                        .placeholder(R.mipmap.ic_image_loading)
                        .error(R.mipmap.ic_image_loadfail)
                        .into(holder.headIV);
            }
        }
    }

    @Override
    public int getItemCount() {
        return datas.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        private ImageView headIV;
        private TextView nameTV;
        private TextView studentClassTV;
        private TextView studentNoTV;
        private MyItemClickListener listener;

        public ViewHolder(View view, MyItemClickListener listener) {
            super(view);
            headIV= (ImageView) itemView.findViewById(R.id.headIV);
            nameTV= (TextView) itemView.findViewById(R.id.student_name_text);
            studentClassTV= (TextView) itemView.findViewById(R.id.student_class_text);
            studentNoTV= (TextView) itemView.findViewById(R.id.student_number_text);
            this.listener = listener;
            view.setOnClickListener(AddressBookAdapter.ViewHolder.this);
        }

        @Override
        public void onClick(View v) {
            listener.onItemClick(v, getAdapterPosition());
        }
    }
}

