package com.liaoinstan.demospring.demo6;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.liaoinstan.demospring.R;
import com.liaoinstan.springview.container.BaseHeader;

/**
 * Created by liaoinstan on 2016/3/23.
 * 简单定制的QQ新年刷红包效果，可以在此基础上自己增加动画特效，这里只是模拟出该效果框架进行演示，故该Header不放在library里面
 */
public class QQHeader extends BaseHeader {

    private TextView text_dot;
    private int dotcount;

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.header_qq, viewGroup, true);
        text_dot = (TextView) view.findViewById(R.id.text_dot);
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
        return rootView.getMeasuredHeight()-100;
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
        dotcount++;
        text_dot.setText("x"+dotcount);
    }

    @Override
    public void onFinishAnim() {
    }
}
