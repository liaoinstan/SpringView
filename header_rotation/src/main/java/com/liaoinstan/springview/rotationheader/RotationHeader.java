package com.liaoinstan.springview.rotationheader;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;

import com.liaoinstan.springview.container.BaseSimpleHeader;
import com.liaoinstan.springview.widget.SpringView;

/**
 * Created by Administrator on 2016/3/21.
 */
public class RotationHeader extends BaseSimpleHeader {

    private RotateAnimation mRotateUpAnim;
    private RotateAnimation mRotateUpAnim2;
    private RotateAnimation mRotateUpAnim3;
    private RotateAnimation mRotateUpAnim4;
    private RotateAnimation mRotateUpAnim5;

    private ProgressBar progress1;
    private ProgressBar progress2;
    private ProgressBar progress3;
    private ProgressBar progress4;
    private ProgressBar progress5;

    //记录拖拽是否超过弹动高度
    private boolean hasOverLimitHeight;

    public RotationHeader() {
        setType(SpringView.Type.OVERLAP);
        setMovePara(2.0f);

        mRotateUpAnim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setInterpolator(new LinearInterpolator());
        mRotateUpAnim.setRepeatCount(Integer.MAX_VALUE);
        mRotateUpAnim.setDuration(1200);
        mRotateUpAnim.setFillAfter(true);

        mRotateUpAnim2 = new RotateAnimation(0.0f, -360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim2.setInterpolator(new LinearInterpolator());
        mRotateUpAnim2.setRepeatCount(Integer.MAX_VALUE);
        mRotateUpAnim2.setDuration(800);
        mRotateUpAnim2.setFillAfter(true);

        mRotateUpAnim3 = new RotateAnimation(0.0f, -360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim3.setInterpolator(new LinearInterpolator());
        mRotateUpAnim3.setRepeatCount(Integer.MAX_VALUE);
        mRotateUpAnim3.setDuration(500);
        mRotateUpAnim3.setFillAfter(true);

        mRotateUpAnim4 = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim4.setInterpolator(new LinearInterpolator());
        mRotateUpAnim4.setRepeatCount(Integer.MAX_VALUE);
        mRotateUpAnim4.setDuration(400);
        mRotateUpAnim4.setFillAfter(true);

        mRotateUpAnim5 = new RotateAnimation(0.0f, -360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim5.setInterpolator(new LinearInterpolator());
        mRotateUpAnim5.setRepeatCount(Integer.MAX_VALUE);
        mRotateUpAnim5.setDuration(800);
        mRotateUpAnim5.setFillAfter(true);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.rotation_header, viewGroup, false);
        progress1 = view.findViewById(R.id.progress1);
        progress2 = view.findViewById(R.id.progress2);
        progress3 = view.findViewById(R.id.progress3);
        progress4 = view.findViewById(R.id.progress4);
        progress5 = view.findViewById(R.id.progress5);
        return view;
    }

    @Override
    public int getDragLimitHeight(View rootView) {
        return rootView.getMeasuredHeight() / 4;
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
        int dragLimitHeight = getDragLimitHeight(rootView);
        if (dy >= dragLimitHeight) hasOverLimitHeight = true;
        if (!hasOverLimitHeight) {
            float rota = 360 * dy / rootView.getMeasuredHeight();
            progress1.setRotation(rota);
            progress2.setRotation(-rota);
            progress3.setRotation(-rota);
            progress4.setRotation(rota);
            progress5.setRotation(-rota);
        }
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {
    }

    @Override
    public void onStartAnim() {
        progress1.startAnimation(mRotateUpAnim);
        progress2.startAnimation(mRotateUpAnim2);
        progress3.startAnimation(mRotateUpAnim3);
        progress4.startAnimation(mRotateUpAnim4);
        progress5.startAnimation(mRotateUpAnim5);
    }

    @Override
    public void onFinishAnim() {
        progress1.clearAnimation();
        progress2.clearAnimation();
        progress3.clearAnimation();
        progress4.clearAnimation();
        progress5.clearAnimation();
        hasOverLimitHeight = false;
    }
}