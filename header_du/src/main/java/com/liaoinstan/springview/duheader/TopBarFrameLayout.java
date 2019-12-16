package com.liaoinstan.springview.duheader;

import android.animation.ValueAnimator;
import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.ViewTreeObserver;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;


/**
 * create by liaoinstan 2019/12/13
 * 配合DuHeader一起使用，实现和‘毒’App一样的header效果，也可以自行定制单独使用
 * 继承自FrameLayout
 */
public class TopBarFrameLayout extends FrameLayout {

    private TextView textTopBar;

    public TopBarFrameLayout(Context context) {
        this(context, null);
    }

    public TopBarFrameLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TopBarFrameLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        LayoutInflater.from(getContext()).inflate(R.layout.view_top_bar, this, true);
        textTopBar = findViewById(R.id.text_top_bar);
        super.onFinishInflate();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int measuredHeight = textTopBar.getMeasuredHeight();
                textTopBar.setTranslationY(-measuredHeight);
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isInEditMode()) {
            int measuredHeight = textTopBar.getMeasuredHeight();
            textTopBar.setTranslationY(-measuredHeight);
        }
    }

    //底部导航条，展开收起动画
    private void startAnim(boolean showOrHide) {
        if (textTopBar == null) return;
        if (showOrHide && textTopBar.getTranslationY() == 0) return;
        if (!showOrHide && textTopBar.getTranslationY() == textTopBar.getHeight()) return;
        ValueAnimator anim = ValueAnimator.ofFloat(showOrHide ? textTopBar.getHeight() : 0f, showOrHide ? 0f : textTopBar.getHeight());
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                textTopBar.setTranslationY(-value);
            }
        });
        anim.start();
    }

    //############################################
    //################ 对外方法 ##################
    //############################################

    /**
     * 获取topBar TextView，如果你想修改样式，直接修改即可
     */
    public TextView getTopBarView() {
        return textTopBar;
    }

    /**
     * 设置文字，最常用的调用封装
     */
    public void setTopBarText(CharSequence text) {
        textTopBar.setText(text);
    }

    /**
     * 先显示topBar，然后再延迟time后隐藏topBar
     */
    public void showAndHideDelay(int time) {
        show();
        hideDelay(time);
    }

    /**
     * 延迟time后显示topBar
     */
    public void showDelay(int time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                show();
            }
        }, time);
    }

    /**
     * 延迟time后隐藏topBar
     */
    public void hideDelay(int time) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                hide();
            }
        }, time);
    }

    /**
     * 显示topBar
     */
    public void show() {
        startAnim(true);
    }

    /**
     * 隐藏topBar
     */
    public void hide() {
        startAnim(false);
    }
}
