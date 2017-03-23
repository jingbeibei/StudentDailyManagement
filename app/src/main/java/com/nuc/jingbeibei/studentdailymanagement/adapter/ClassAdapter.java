package com.nuc.jingbeibei.studentdailymanagement.adapter;


import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;

import android.view.View;
import android.view.ViewGroup;

import android.widget.TextView;

import com.nuc.jingbeibei.studentdailymanagement.R;
import com.nuc.jingbeibei.studentdailymanagement.beans.StudentClass;
import com.nuc.jingbeibei.studentdailymanagement.beans.Teacher;

import java.util.ArrayList;
import java.util.Arrays;

import java.util.List;

import cn.bmob.v3.datatype.BmobRelation;
import cn.bmob.v3.exception.BmobException;
import cn.bmob.v3.listener.UpdateListener;

/**
 * Created by jingbeibei on 2017/3/21.
 */

public class ClassAdapter extends RecyclerView.Adapter implements ItemTouchHelperAdapter {
    private List<String> mItems = null;
    private Teacher teacher;
    private int flag=0;//0代表班级，  1代表老师

    public void setTeacherHolderClass(List<StudentClass> teacherHolderClass) {
        this.teacherHolderClass = teacherHolderClass;
    }

    private List<StudentClass> teacherHolderClass;

    private static final String[] STRINGS = new String[]{
            "One", "Two", "Three", "Four", "Five", "Six", "Seven", "Eight", "Nine", "Ten"
    };

    public ClassAdapter(List mItems, Teacher teacher,int flag) {
        this.mItems = mItems;
        this.teacher = teacher;
        this.flag=flag;
//        mItems.addAll(Arrays.asList(STRINGS));

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_test_layout, parent, false);
        ItemViewHolder itemViewHolder = new ItemViewHolder(view);
        return itemViewHolder;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((ItemViewHolder) holder).textView.setText(mItems.get(position));
    }


    @Override
    public int getItemCount() {
        return mItems.size();
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        String prev = mItems.remove(fromPosition);
        mItems.add(toPosition > fromPosition ? toPosition - 1 : toPosition, prev);
        notifyItemMoved(fromPosition, toPosition);
    }

    @Override
    public void onItemDissmiss(int position) {
        mItems.remove(position);
        notifyItemRemoved(position);
        removeClassService(teacherHolderClass.get(position));
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public TextView textView;


        public ItemViewHolder(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.id_item_test_text);

        }

    }

    public void clearItemData() {
        mItems.clear();
    }

    private void removeClassService(StudentClass studentClass) {
        if (flag==1) {
            BmobRelation relation = new BmobRelation();
            relation.remove(studentClass);
            teacher.setHoldClass(relation);
            teacher.update(new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    if (e == null) {
                        Log.i("bmob", "关联关系删除成功");
                    } else {
                        Log.i("bmob", "失败：" + e.getMessage());
                    }
                }

            });
        }else {
            studentClass.remove("counselor");
            studentClass.update(new UpdateListener() {

                @Override
                public void done(BmobException e) {
                    if(e==null){
                        Log.i("bmob","删除辅导员成功");
                    }else{
                        Log.i("bmob","删除辅导员失败："+e.getMessage());
                    }
                }

            });

        }
    }
}
