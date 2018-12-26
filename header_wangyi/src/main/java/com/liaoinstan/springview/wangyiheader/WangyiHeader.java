package com.liaoinstan.springview.wangyiheader;

import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;

import com.liaoinstan.springview.container.BaseHeader;
import com.liaoinstan.springview.utils.DensityUtil;

/**
 * Created by liaoinstan on 2018/9/4.
 */
public class WangyiHeader extends BaseHeader {

    private CircleRoundView circle_round;
    private WaveTextView text_ending_title;
    private FrameLayout lay_circle;

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
        minWidthDot = DensityUtil.dp2px(7);
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
        lay_circle = view.findViewById(R.id.lay_circle);

        //动画监听
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator valueAnimator) {
                int nowWidth = (Integer) valueAnimator.getAnimatedValue();
                ViewGroup.LayoutParams layoutParams = circle_round.getLayoutParams();
                layoutParams.width = nowWidth;
                layoutParams.height = nowWidth;
                circle_round.setLayoutParams(layoutParams);
            }
        });
        //初始化可见性
        circle_round.setVisibility(View.VISIBLE);
        text_ending_title.setVisibility(View.INVISIBLE);
        return view;
    }

    @Override
    public int getDragMaxHeight(View rootView) {
        return rootView.getMeasuredHeight();
    }

    @Override
    public int getDragLimitHeight(View rootView) {
        return DensityUtil.dp2px(80);
    }

    @Override
    public int getDragSpringHeight(View rootView) {
        return DensityUtil.dp2px(80);
    }

    @Override
    public void onPreDrag(View rootView) {
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
        int dragLimitHeight = getDragLimitHeight(rootView);
        int layHeight = lay_circle.getMeasuredHeight();
        if (dy <= 5) reset();
        if (dy < layHeight) {
            //如果此时动画还没停止，则停止动画
            if (animator.isRunning()) animator.cancel();
            //设置圆圈从 0 - 1 的透明度渐变
//            float lv = dy / (float) layHeight; //0-1
//            circle_round.setAlpha(lv);
            setCircleSize(minWidthDot);
            lay_circle.setTranslationY(0);
        } else if (dy >= layHeight && dy < dragLimitHeight) {
            float lv = (dy - layHeight) / (float) (dragLimitHeight - layHeight); //0-1
            int nowWidth = (int) ((maxWidthDot - minWidthDot) * lv + minWidthDot);
            setCircleSize(nowWidth);
            lay_circle.setTranslationY(-(dy / 2 - lay_circle.getWidth() / 2));
        } else {
            //保持圆圈居中
            setCircleSize(maxWidthDot);
            lay_circle.setTranslationY(-(dy / 2 - lay_circle.getWidth() / 2));
        }
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {

    }

    @Override
    public void onStartAnim() {
        animator.start();
    }

    @Override
    public void onFinishAnim() {
        reset();
    }

    private void reset() {
        circle_round.setVisibility(View.VISIBLE);
        text_ending_title.setVisibility(View.INVISIBLE);
    }

    private void setCircleSize(int size) {
        ViewGroup.LayoutParams layoutParam1 = circle_round.getLayoutParams();
        if (layoutParam1.width == size) return;
        layoutParam1.height = size;
        layoutParam1.width = size;
        circle_round.setLayoutParams(layoutParam1);
    }

    @Override
    public void onEndingAnimStart() {
        animator.cancel();
        circle_round.setVisibility(View.INVISIBLE);
        text_ending_title.setVisibility(View.VISIBLE);
        text_ending_title.start();
    }

    @Override
    public void onEndingAnimEnd() {
    }

    @Override
    public int getEndingAnimTime() {
        return 2000;
    }

    @Override
    public int getEndingAnimHight(View rootView) {
        return text_ending_title.getMeasuredHeight();
    }
}