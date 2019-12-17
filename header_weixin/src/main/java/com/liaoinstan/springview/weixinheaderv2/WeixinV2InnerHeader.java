package com.liaoinstan.springview.weixinheaderv2;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.core.graphics.drawable.DrawableCompat;

import com.liaoinstan.springview.container.BaseSimpleHeader;
import com.liaoinstan.springview.utils.DensityUtil;
import com.liaoinstan.springview.weixinheader.R;
import com.liaoinstan.springview.widget.SpringView;

public class WeixinV2InnerHeader extends BaseSimpleHeader {

    private OnSearchClickListener onSearchClickListener;

    WeixinV2InnerHeader(OnSearchClickListener onSearchClickListener) {
        setMovePara(2.0f);
        setType(SpringView.Type.FOLLOW);
        this.onSearchClickListener = onSearchClickListener;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View root = inflater.inflate(R.layout.weixin_header_v2_inner, viewGroup, false);
        TextView text_search = root.findViewById(R.id.text_search);
        //搜索图标着色，用DrawableCompat着色可以向下兼容，而drawableTint属性不向下兼容
        Drawable originalSearchDrawable = viewGroup.getContext().getResources().getDrawable(R.drawable.weixin_ic_search_light).mutate();
        Drawable searchDrawable = DrawableCompat.wrap(originalSearchDrawable);
        DrawableCompat.setTintList(searchDrawable, text_search.getTextColors());
        text_search.setCompoundDrawablesWithIntrinsicBounds(searchDrawable, null, null, null);
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

    interface OnSearchClickListener {
        void onSearchClick(View view);
    }
}
