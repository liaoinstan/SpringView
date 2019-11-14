package com.liaoinstan.springview.container;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.LayoutRes;

/**
 * 直接在布局设置footer布局，会使用该类生成默认Footer
 */
public class InnerFooter extends BaseHeader {

    private final String TAG = getClass().getSimpleName();
    private int layoutId;

    public InnerFooter(@LayoutRes int layoutId) {
        this.layoutId = layoutId;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        return inflater.inflate(layoutId, viewGroup, false);
    }

    @Override
    public void onPreDrag(View rootView) {
        Log.d(TAG, "onPreDrag");
    }

    @Override
    public void onFinishDrag(View rootView) {
        Log.d(TAG, "onFinishDrag");
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
        Log.v(TAG, "onDropAnim:" + dy);
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {
        Log.d(TAG, "onLimitDes:" + upORdown);
    }

    @Override
    public void onStartAnim() {
        Log.d(TAG, "onStartAnim");
    }

    @Override
    public void onFinishAnim() {
        Log.d(TAG, "onFinishAnim");
    }
}
