package com.liaoinstan.springview.container;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ProgressBar;

import com.liaoinstan.springview.R;

/**
 * Created by Administrator on 2016/3/21.
 */
public class RotationFooter extends BaseFooter {
    private Context context;
    private int rotationSrc;

    private RotateAnimation mRotateUpAnim;

    private ProgressBar footer_progress;

    public RotationFooter(Context context) {
        this(context, R.drawable.progress_gear);
    }

    public RotationFooter(Context context, int rotationSrc) {
        this.context = context;
        this.rotationSrc = rotationSrc;

        mRotateUpAnim = new RotateAnimation(0.0f, 360.0f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        mRotateUpAnim.setInterpolator(new LinearInterpolator());
        mRotateUpAnim.setRepeatCount(Integer.MAX_VALUE);
        mRotateUpAnim.setDuration(600);
        mRotateUpAnim.setFillAfter(true);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.rotation_footer, viewGroup, true);
        footer_progress = (ProgressBar) view.findViewById(R.id.rotation_footer_progress);
        footer_progress.setIndeterminateDrawable(ContextCompat.getDrawable(context, rotationSrc));
        return view;
    }

    @Override
    public void onPreDrag(View rootView) {
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
        float rota = 360 * Math.abs(dy) / rootView.getMeasuredHeight();
        footer_progress.setRotation(rota);
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
    }
}