package com.nuc.jingbeibei.studentdailymanagement.adapter;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.Notice;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by jingbeibei on 2017/3/24.
 */

public class NoticeAdapter extends RecyclerView.Adapter<NoticeAdapter.ViewHolder>{
    public ArrayList<String> datas = null;
    private ArrayList<Notice> notices;
    private Context mContext;

    public void setListener(MyItemClickListener listener) {
        this.listener = listener;
    }

    private MyItemClickListener listener;

    public NoticeAdapter(Context context, ArrayList<Notice> notices) {
        this.datas = datas;
        this.notices=notices;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_notice_layout,parent,false);
        return new ViewHolder(view,listener);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        holder.mTextView.setText(notices.get(position).getTitle());
        holder.dateTV.setText(notices.get(position).getReleaseTime());
        String pic=notices.get(position).getPublisher().getPicpath();
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
        return datas.size();
    }

    //自定义的ViewHolder，持有每个Item的的所有界面元素
    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        public TextView mTextView;
        private ImageView headIV;
        private TextView dateTV;
        private MyItemClickListener listener;
        public ViewHolder(View view,MyItemClickListener listener){
            super(view);
            mTextView = (TextView) view.findViewById(R.id.text);
            headIV= (ImageView) view.findViewById(R.id.headIV);
            dateTV= (TextView) view.findViewById(R.id.date_text);
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
