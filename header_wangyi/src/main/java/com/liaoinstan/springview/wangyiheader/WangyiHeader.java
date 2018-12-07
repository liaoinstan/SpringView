package com.liaoinstan.springview.wangyiheader;

import android.animation.ValueAnimator;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import com.liaoinstan.springview.container.BaseHeader;
import com.liaoinstan.springview.utils.DensityUtil;

/**
 * Created by Administrator on 2018/9/4.
 */
public class WangyiHeader extends BaseHeader {

    private CircleRoundView circle_round;
    private TextView text_ending_title;
    private ViewGroup lay_ending;

    //圆圈动画
    private ValueAnimator animator;
    //圆圈最大直径
    private int maxWidthDot;
    //圆圈最小直径
    private int minWidthDot;
    //圆圈动画过程中最小直径
    private int minWidthAnimDot;

    public WangyiHeader() {
        //初始化数据
        maxWidthDot = DensityUtil.dp2px(22);
        minWidthDot = DensityUtil.dp2px(5);
        minWidthAnimDot = DensityUtil.dp2px(17);

        //初始化动画
        animator = ValueAnimator.ofInt(maxWidthDot, minWidthAnimDot);
        animator.setInterpolator(new DecelerateInterpolator());
        animator.setRepeatMode(ValueAnimator.REVERSE);
        animator.setRepeatCount(ValueAnimator.INFINITE);
        animator.setDuration(300);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.wangyi_header, viewGroup, true);
        circle_round = view.findViewById(R.id.circle_round);
        text_ending_title = view.findViewById(R.id.text_ending_title);
        lay_ending = view.findViewById(R.id.lay_ending);

        //动画监听
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int nowWidth = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = circle_round.getLayoutParams();
                layoutParams.width = nowWidth;
                layoutParams.height = nowWidth;
                circle_round.setLayoutParams(layoutParams);

                if (cancelAnimNextMax && nowWidth >= maxWidthDot) {
                    animator.cancel();
                }
            }
        });
        //初始化可见性
        circle_round.setVisibility(View.VISIBLE);
        text_ending_title.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public int getDragSpringHeight(View rootView) {
        return rootView.getMeasuredHeight();
    }

    @Override
    public void onPreDrag(View rootView) {
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
        int dragSpringHeight = getDragSpringHeight(rootView);
        if (dy < dragSpringHeight) {
            float lv = dy / ((float) dragSpringHeight); //0-1
            int nowWidth = (int) ((maxWidthDot - minWidthDot) * lv + minWidthDot);
            setCircleSize(nowWidth);
        } else {
            setCircleSize(maxWidthDot);
        }
    }

    private boolean cancelAnimNextMax;

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {

    }

    @Override
    public void onStartAnim() {
        animator.start();
    }

    @Override
    public void onEndingAnimStart() {
        //这种停止标志下次最大时停止动画
        cancelAnimNextMax = true;
//        circle_round.setVisibility(View.INVISIBLE);
//        text_ending_title.setVisibility(View.VISIBLE);
    }

    @Override
    public void onEndingAnimEnd() {
    }

    @Override
    public void onFinishAnim() {
        circle_round.setVisibility(View.VISIBLE);
        text_ending_title.setVisibility(View.INVISIBLE);
        cancelAnimNextMax = false;
    }

    private void setCircleSize(int size) {
        ViewGroup.LayoutParams layoutParam1 = circle_round.getLayoutParams();
        if (layoutParam1.width == size) return;
        layoutParam1.height = size;
        layoutParam1.width = size;
        circle_round.setLayoutParams(layoutParam1);
    }

    @Override
    public int getEndingAnimTime() {
        return 3000;
    }

    @Override
    public int getEndingAnimHight(View rootView) {
        return lay_ending.getMeasuredHeight();
    }
}