package com.liaoinstan.springview.container;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.liaoinstan.springview.R;
import com.liaoinstan.springview.utils.DensityUtil;

/**
 * Created by Administrator on 2016/3/23.
 */
public class AcFunFooter extends BaseFooter {
    private Context context;
    private int imgSrc;
    private ImageView acfun_footer_img;

    public AcFunFooter(Context context, int imgSrc) {
        this.context = context;
        this.imgSrc = imgSrc;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.acfun_footer, viewGroup, true);
        acfun_footer_img = (ImageView) view.findViewById(R.id.acfun_footer_img);
        acfun_footer_img.setImageResource(imgSrc);
        return view;
    }

    @Override
    public int getDragLimitHeight(View rootView) {
        return DensityUtil.dp2px(70);
    }

    @Override
    public int getDragMaxHeight(View rootView) {
        return rootView.getMeasuredHeight();
    }

    @Override
    public int getDragSpringHeight(View rootView) {
        return DensityUtil.dp2px(70);
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
}
