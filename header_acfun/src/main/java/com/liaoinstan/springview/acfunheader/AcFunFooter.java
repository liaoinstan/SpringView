package com.liaoinstan.springview.acfunheader;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.liaoinstan.springview.container.BaseFooter;
import com.liaoinstan.springview.container.BaseSimpleFooter;
import com.liaoinstan.springview.utils.DensityUtil;
import com.liaoinstan.springview.widget.SpringView;

/**
 * Created by Administrator on 2016/3/23.
 */
public class AcFunFooter extends BaseSimpleFooter {
    private Context context;
    private int imgSrc;
    private ImageView acfun_footer_img;

    public AcFunFooter(Context context, int imgSrc) {
        setType(SpringView.Type.OVERLAP);
        setMovePara(2.0f);
        this.context = context;
        this.imgSrc = imgSrc;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.acfun_footer, viewGroup, true);
        acfun_footer_img = view.findViewById(R.id.acfun_footer_img);
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
