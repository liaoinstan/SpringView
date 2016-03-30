package com.liaoinstan.springview.container;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liaoinstan.springview.R;
import com.liaoinstan.springview.widget.SpringView;

/**
 * Created by Administrator on 2016/3/23.
 */
public class AcFunHeader extends BaseHeader {
    private int imgSrc;
    private ImageView acfun_header_img;

    public AcFunHeader(int imgSrc){
        this.imgSrc = imgSrc;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.acfun_header, viewGroup, true);
        acfun_header_img = (ImageView) view.findViewById(R.id.acfun_header_img);
        acfun_header_img.setImageResource(imgSrc);
        return view;
    }

    @Override
    public int getDragLimitHeight(View rootView) {
        return 150;
    }

    @Override
    public int getDragMaxHeight(View rootView) {
        return rootView.getMeasuredHeight();
    }

    @Override
    public int getDragSpringHeight(View rootView) {
        return 150;
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