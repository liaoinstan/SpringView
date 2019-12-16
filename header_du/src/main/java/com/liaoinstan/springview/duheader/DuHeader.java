package com.liaoinstan.springview.duheader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liaoinstan.springview.container.BaseSimpleHeader;

public class DuHeader extends BaseSimpleHeader {

    private DuView duView;
    private boolean hasOverSpringHeight;

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View rootView = inflater.inflate(R.layout.du_header, viewGroup, false);
        duView = rootView.findViewById(R.id.du_view);
        return rootView;
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
        int dragLimitHeight = getDragLimitHeight(rootView);
        if (dy < 20) hasOverSpringHeight = false;
        if (dy >= dragLimitHeight) hasOverSpringHeight = true;
        if (!hasOverSpringHeight) {
            float lv = 1 - (float) (dy) / (dragLimitHeight); //1-0
            duView.setProgress(lv * 100);
            //向下偏移半个view高度，平滑拖拽
            float height = duView.getMeasuredHeight() * 0.5f;
            float tranY = height * lv;
            duView.setTranslationY(tranY);
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
        duView.startAnim();
    }

    @Override
    public void onFinishAnim() {
        duView.resetAnim();
    }
}
