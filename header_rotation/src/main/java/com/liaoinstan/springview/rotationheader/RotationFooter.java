package com.liaoinstan.springview.rotationheader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;

import com.liaoinstan.springview.container.BaseSimpleFooter;
import com.liaoinstan.springview.widget.SpringView;


/**
 * Created by Administrator on 2016/3/21.
 */
public class RotationFooter extends BaseSimpleFooter {

    private RotateAnimation mRotateUpAnim;
    private ProgressBar footer_progress;

    //记录拖拽是否超过弹动高度
    private boolean hasOverLimitHeight;

    public RotationFooter() {
        setType(SpringView.Type.OVERLAP);
        setMovePara(2.0f);

        mRotateUpAnim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setInterpolator(new LinearInterpolator());
        mRotateUpAnim.setRepeatCount(Integer.MAX_VALUE);
        mRotateUpAnim.setDuration(600);
        mRotateUpAnim.setFillAfter(true);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.rotation_footer, viewGroup, false);
        footer_progress = view.findViewById(R.id.rotation_footer_progress);
        return view;
    }

    @Override
    public int getDragLimitHeight(View rootView) {
        return rootView.getMeasuredHeight();
    }

    @Override
    public void onPreDrag(View rootView) {
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
        int dragLimitHeight = getDragLimitHeight(rootView);
        if (dy >= dragLimitHeight) hasOverLimitHeight = true;
        if (!hasOverLimitHeight) {
            float rota = 360 * Math.abs(dy) / rootView.getMeasuredHeight();
            footer_progress.setRotation(rota);
        }
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {
    }

    @Override
    public void onStartAnim() {
        footer_progress.startAnimation(mRotateUpAnim);
    }

    @Override
    public void onFinishAnim() {
        footer_progress.clearAnimation();
        hasOverLimitHeight = false;
    }
}