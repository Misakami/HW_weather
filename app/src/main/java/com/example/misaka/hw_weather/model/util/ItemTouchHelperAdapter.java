package com.example.misaka.hw_weather.model.util;
/**
 * @author misaka
 * @date 2018/12/8
 */
public interface ItemTouchHelperAdapter {
    /**
     *
     * 数据交换
     * @param fromPosition int 移动前的位置
     * @param toPosition int 移动后的位置
     *
     */
    void onItemMove(int fromPosition,int toPosition);

    /**
     *
     * 数据删除
     * @param  position 删除的位置
     */
    void onItemDissmiss(int position);
}
