package com.liaoinstan.springview.weixinheaderv2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liaoinstan.springview.container.BaseHeader;
import com.liaoinstan.springview.utils.DensityUtil;
import com.liaoinstan.springview.weixinheader.R;

public class WeixinV2InnerHeader extends BaseHeader {

    private View root;
    private TextView text_search;

    public WeixinV2InnerHeader(OnSearchClickListener onSearchClickListener) {
        this.onSearchClickListener = onSearchClickListener;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        root = inflater.inflate(R.layout.weixin_header_v2_inner, viewGroup, false);
        text_search = root.findViewById(R.id.text_search);
        text_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (onSearchClickListener != null) onSearchClickListener.onSearchClick(v);
            }
        });
        return root;
    }

    @Override
    public int getDragLimitHeight(View rootView) {
        return getDragSpringHeight(rootView) / 2;
    }

    @Override
    public int getDragMaxHeight(View rootView) {
        return rootView.getMeasuredHeight() + DensityUtil.dp2px(100);
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
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {
    }

    @Override
    public void onStartAnim() {
    }

    @Override
    public void onFinishAnim() {
    }

    private OnSearchClickListener onSearchClickListener;

    public void setOnSearchClickListener(OnSearchClickListener onSearchClickListener) {
        this.onSearchClickListener = onSearchClickListener;
    }

    interface OnSearchClickListener {
        void onSearchClick(View view);
    }
}
