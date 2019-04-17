package com.liaoinstan.springview.weixinheader;

import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.OrientationHelper;
import androidx.recyclerview.widget.RecyclerView;

public class StartLinearSnapHelper extends LinearSnapHelper {

    private OrientationHelper mHorizontalHelper;

    @Nullable
    @Override
    public int[] calculateDistanceToFinalSnap(RecyclerView.LayoutManager layoutManager, View targetView) {
        int[] out = new int[2];
        if (layoutManager.canScrollHorizontally()) {
            out[0] = distanceToStart(targetView, getHorizontalHelper(layoutManager));
        } else {
            out[0] = 0;
        }

        return out;
    }

    @Nullable
    @Override
    public View findSnapView(RecyclerView.LayoutManager layoutManager) {
        return findStartView(layoutManager, getHorizontalHelper(layoutManager));
    }

    private int distanceToStart(View targetView, OrientationHelper helper) {
        return helper.getDecoratedStart(targetView) - helper.getStartAfterPadding();
    }

    private View findStartView(RecyclerView.LayoutManager layoutManager, OrientationHelper helper) {

        /**
         * 0,1,2,3  4,5,6,7  8,9,10,11
         */

        if (layoutManager instanceof LinearLayoutManager) {
            //找出第一个可见的ItemView的位置
            int firstChildPosition = ((LinearLayoutManager) layoutManager).findFirstVisibleItemPosition();
            if (firstChildPosition == RecyclerView.NO_POSITION) {
                return null;
            }

            //找到最后一个完全显示的ItemView，如果该ItemView是列表中的最后一个
            //就说明列表已经滑动最后了，这时候就不应该根据第一个ItemView来对齐了
            //要不然由于需要跟第一个ItemView对齐最后一个ItemView可能就一直无法完全显示，
            //所以这时候直接返回null表示不需要对齐
            if (((LinearLayoutManager) layoutManager).findLastCompletelyVisibleItemPosition() == layoutManager.getItemCount() - 1) {
                return null;
            }

            View firstChildView = layoutManager.findViewByPosition(firstChildPosition);
            //如果第一个ItemView被遮住的长度没有超过一半，就取该ItemView作为snapView
            //超过一半，就把下一个ItemView作为snapView
            if (helper.getDecoratedEnd(firstChildView) >= helper.getDecoratedMeasurement(firstChildView) / 2 && helper.getDecoratedEnd(firstChildView) > 0) {
                return firstChildView;
            } else {
                return layoutManager.findViewByPosition(firstChildPosition + 1);
            }
        } else {
            return null;
        }
    }


    private OrientationHelper getHorizontalHelper(@NonNull RecyclerView.LayoutManager layoutManager) {
        if (mHorizontalHelper == null) {
            mHorizontalHelper = OrientationHelper.createHorizontalHelper(layoutManager);
        }
        return mHorizontalHelper;
    }
}