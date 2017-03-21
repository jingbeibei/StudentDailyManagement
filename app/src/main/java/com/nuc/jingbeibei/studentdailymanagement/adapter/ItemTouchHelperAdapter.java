package com.nuc.jingbeibei.studentdailymanagement.adapter;

/**
 * Created by jingbeibei on 2017/3/21.
 */

public interface ItemTouchHelperAdapter {
    //数据交换
    void onItemMove(int fromPosition,int toPosition);
    //数据删除
    void onItemDissmiss(int position);
}
