package com.liaoinstan.springview.meituanheader;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.core.content.ContextCompat;

import com.liaoinstan.springview.container.BaseSimpleFooter;
import com.liaoinstan.springview.widget.SpringView;

/**
 * Created by Administrator on 2016/3/21.
 */
public class MeituanFooter extends BaseSimpleFooter {

    private AnimationDrawable animationLoading;

    private Context context;
    private ImageView footer_img;
    private int[] loadingAnimSrcs = new int[]{R.drawable.mt_loading01, R.drawable.mt_loading02};

    public MeituanFooter(Context context) {
        this(context, null);
    }

    public MeituanFooter(Context context, int[] loadingAnimSrcs) {
        setType(SpringView.Type.FOLLOW);
        setMovePara(2.0f);
        this.context = context;
        if (loadingAnimSrcs != null) this.loadingAnimSrcs = loadingAnimSrcs;
        animationLoading = new AnimationDrawable();
        for (int src : this.loadingAnimSrcs) {
            animationLoading.addFrame(ContextCompat.getDrawable(context, src), 150);
            animationLoading.setOneShot(false);
        }
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.meituan_footer, viewGroup, false);
        footer_img = view.findViewById(R.id.meituan_footer_img);
        if (animationLoading != null) {
            footer_img.setImageDrawable(animationLoading);
        }
        return view;
    }

    @Override
    public void onPreDrag(View rootView) {
        animationLoading.stop();
        if (animationLoading != null && animationLoading.getNumberOfFrames() > 0) {
            footer_img.setImageDrawable(animationLoading.getFrame(0));
        }
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {
    }

    @Override
    public void onStartAnim() {
        if (animationLoading != null) {
            footer_img.setImageDrawable(animationLoading);
            animationLoading.start();
        }
    }

    @Override
    public void onFinishAnim() {
        animationLoading.stop();
        if (animationLoading != null && animationLoading.getNumberOfFrames() > 0) {
            footer_img.setImageDrawable(animationLoading.getFrame(0));
        }
    }
}