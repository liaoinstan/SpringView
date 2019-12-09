package com.liaoinstan.springview.weixinheaderv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liaoinstan.springview.container.BaseSimpleFooter;
import com.liaoinstan.springview.utils.DensityUtil;
import com.liaoinstan.springview.widget.SpringView;

public class WeixinV2InnerFooter extends BaseSimpleFooter {

    private OnDragFinishListener onDragFinishListener;

    WeixinV2InnerFooter(OnDragFinishListener onDragFinishListener) {
        setMovePara(2.0f);
        setType(SpringView.Type.FOLLOW);
        this.onDragFinishListener = onDragFinishListener;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View root = new View(viewGroup.getContext());
        ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, DensityUtil.dp2px(200));
        root.setLayoutParams(layoutParams);
        return root;
    }

    @Override
    public int getDragLimitHeight(View rootView) {
        return DensityUtil.dp2px(60);
    }

    @Override
    public int getDragMaxHeight(View rootView) {
        return rootView.getMeasuredHeight();
    }

    @Override
    public int getDragSpringHeight(View rootView) {
        return getDragMaxHeight(rootView) - 1;
    }


    @Override
    public void onPreDrag(View rootView) {
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {
    }

    @Override
    public void onStartAnim() {
        if (onDragFinishListener != null) onDragFinishListener.onDragFinish();
    }

    @Override
    public void onFinishAnim() {
    }

    interface OnDragFinishListener {
        void onDragFinish();
    }
}
