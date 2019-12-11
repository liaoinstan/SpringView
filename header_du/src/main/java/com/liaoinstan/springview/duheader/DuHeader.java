package com.liaoinstan.springview.duheader;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liaoinstan.springview.container.BaseSimpleHeader;
import com.liaoinstan.springview.utils.DensityUtil;

public class DuHeader extends BaseSimpleHeader {

    private View rootView;
    private DuView duView;
    private boolean hasOverSpringHeight;

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        rootView = inflater.inflate(R.layout.du_header, viewGroup, false);
        duView = rootView.findViewById(R.id.du_view);
        return rootView;
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
        int dragLimitHeight = getDragLimitHeight(rootView);
        if (dy < 20) hasOverSpringHeight = false;
        if (dy >= dragLimitHeight) hasOverSpringHeight = true;
        if (!hasOverSpringHeight) {
            if (dy < DensityUtil.dp2px(40)) {
                duView.setProgress(100);
            } else {
                float lv = 1 - (float) (dy - DensityUtil.dp2px(40)) / (dragLimitHeight - DensityUtil.dp2px(40)); //0-1
                duView.setProgress(lv * 100);
            }
        } else {
            duView.setProgress(0);
        }
    }

    @Override
    public int getDragLimitHeight(View rootView) {
        return rootView.getMeasuredHeight();
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {

    }

    @Override
    public void onStartAnim() {
        Log.e("xxx","onStartAnim");
        duView.startAnim();
    }

    @Override
    public void onFinishAnim() {
        Log.e("xxx","onFinishAnim");
        duView.resetAnim();
    }
}
