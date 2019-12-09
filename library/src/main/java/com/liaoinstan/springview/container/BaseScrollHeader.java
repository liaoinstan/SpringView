package com.liaoinstan.springview.container;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.liaoinstan.springview.R;
import com.liaoinstan.springview.widget.SpringView;

public class BaseScrollHeader extends BaseHeader {

    private View rootView;

    @Override
    public SpringView.Type getType() {
        return SpringView.Type.SCROLL;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        rootView = inflater.inflate(R.layout.default_header, viewGroup, false);
        return rootView;
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
//        Log.e(TAG, "onDropAnim -> dy:" + dy);
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {
        Log.e(TAG, "onLimitDes -> upORdown:" + upORdown);
    }

    @Override
    public void onStartAnim() {
        Log.e(TAG, "onStartAnim");
    }

    @Override
    public void onFinishAnim() {
        Log.e(TAG, "onFinishAnim");
    }
}
