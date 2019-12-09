package com.liaoinstan.springview.weixinheaderv2;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.liaoinstan.springview.weixinheader.R;

/**
 * create by liaoinstan
 * 微信顶部titleBar，由于weixin header V2 许多拖拽动画需要和顶部标题栏交互，所以这里定义了一个WeixinTitleBar封装内部逻辑
 */
public class WeixinTitleBar extends FrameLayout {

    private TextView text_delete;
    private View title_bg_view;
    private View lay_title_content;
    private FrameLayout lay_content;

    public WeixinTitleBar(Context context) {
        this(context, null);
    }

    public WeixinTitleBar(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WeixinTitleBar(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onFinishInflate() {
        View root = LayoutInflater.from(getContext()).inflate(R.layout.view_weixin_title_bar, null, false);
        text_delete = root.findViewById(R.id.text_delete);
        title_bg_view = root.findViewById(R.id.title_bg_view);
        lay_title_content = root.findViewById(R.id.lay_title_content);
        lay_content = root.findViewById(R.id.lay_content);

        int childCount = getChildCount();
        for (int i = 0; i < childCount; i++) {
            View childView = getChildAt(0);
            removeView(childView);
            lay_content.addView(childView);
        }
        addView(root);

        initView();
        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (isInEditMode()) {
            int height = getMeasuredHeight();
            ViewGroup.LayoutParams layoutParams = lay_title_content.getLayoutParams();
            layoutParams.height = height;
            lay_title_content.setLayoutParams(layoutParams);
            text_delete.setTranslationY(height);
        }
    }

    private void initView() {
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                int height = getMeasuredHeight();
                ViewGroup.LayoutParams layoutParams = lay_title_content.getLayoutParams();
                layoutParams.height = height;
                lay_title_content.setLayoutParams(layoutParams);
                text_delete.setTranslationY(height);
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    public void setExtraHeight(int height) {
        ViewGroup.LayoutParams layoutParams = getLayoutParams();
        layoutParams.height = height + getTitleViewHeight();
        setLayoutParams(layoutParams);
    }

    public void setTitleBgAlpha(float alpha) {
        getTitleBgView().setAlpha(alpha);
    }

    public int getTitleViewHeight() {
        return lay_title_content.getMeasuredHeight();
    }

    public int getDeleteViewHeight() {
        return getDeleteView().getHeight();
    }

    public View getTitleBgView() {
        return title_bg_view;
    }

    public TextView getDeleteView() {
        return text_delete;
    }
}
