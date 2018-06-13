package com.liaoinstan.springview.ctrl;

import android.support.v7.widget.RecyclerView;

/**
 * Created by liaoinstan on 2016/6/12 0012.
 * RecyclerView 的item点击事件接口，设置在adapter中
 */
public interface OnRecycleItemClickListener {
    void onItemClick(RecyclerView.ViewHolder viewHolder, int position);
}
