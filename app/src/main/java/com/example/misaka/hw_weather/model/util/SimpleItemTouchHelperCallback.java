package com.example.misaka.hw_weather.model.util;

import android.graphics.Canvas;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import android.view.ViewGroup;

import static android.support.constraint.Constraints.TAG;
import static android.support.v7.widget.helper.ItemTouchHelper.ACTION_STATE_DRAG;

public class SimpleItemTouchHelperCallback extends ItemTouchHelper.Callback {
    private ItemTouchHelperAdapter mAdapter;

    public SimpleItemTouchHelperCallback(ItemTouchHelperAdapter mAdapter) {
        this.mAdapter = mAdapter;
    }

    //获得拖动方式
    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        //第一个不能拖拽滑动
        if (viewHolder.getAdapterPosition() == 0) {
            return 0;
        }
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;        //允许上下的拖动
        int swipeFlags = ItemTouchHelper.LEFT;   //同时允许从右向左侧滑
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    //允许长按
    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    //允许滑动
    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
        //目标不能为第一个
        if (target.getAdapterPosition() == 0) {
            return false;
        }
        mAdapter.onItemMove(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {

        mAdapter.onItemDissmiss(viewHolder.getAdapterPosition());
    }

    //滑动的效果设置

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        //如果是拖拽
        int height = viewHolder.itemView.getHeight();
        // 0 -
        int num = viewHolder.getLayoutPosition();
        //size 1开始
        int i = recyclerView.getAdapter().getItemCount();
        if (dY < -(num - 1) * height) {
            dY = -(num - 1) * height;
        }
        if (dY > (i - num) * height -height/2) {
            dY = (i - num) * height -height/2;
        }
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
    }
}
