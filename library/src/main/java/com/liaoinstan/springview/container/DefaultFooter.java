package com.liaoinstan.springview.container;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.core.content.ContextCompat;

import com.liaoinstan.springview.R;
import com.liaoinstan.springview.widget.SpringView;

/**
 * Created by Administrator on 2016/3/21.
 */
public class DefaultFooter extends BaseSimpleFooter {
    private Context context;
    private int rotationSrc;
    private TextView footerTitle;
    private ProgressBar footerProgressbar;

    public DefaultFooter(Context context) {
        this(context, R.drawable.progress_small);
    }

    public DefaultFooter(Context context, int rotationSrc) {
        setType(SpringView.Type.FOLLOW);
        setMovePara(2.0f);
        this.context = context;
        this.rotationSrc = rotationSrc;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        View view = inflater.inflate(R.layout.default_footer, viewGroup, false);
        footerTitle = view.findViewById(R.id.default_footer_title);
        footerProgressbar = view.findViewById(R.id.default_footer_progressbar);
        footerProgressbar.setIndeterminateDrawable(ContextCompat.getDrawable(context, rotationSrc));
        return view;
    }

    @Override
    public void onPreDrag(View rootView) {
    }

    @Override
    public void onDropAnim(View rootView, int dy) {
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {
        if (upORdown) {
            footerTitle.setText("松开载入更多");
        } else {
            footerTitle.setText("查看更多");
        }
    }

    @Override
    public void onStartAnim() {
        footerTitle.setVisibility(View.INVISIBLE);
        footerProgressbar.setVisibility(View.VISIBLE);
    }

    @Override
    public void onFinishAnim() {
        footerTitle.setText("查看更多");
        footerTitle.setVisibility(View.VISIBLE);
        footerProgressbar.setVisibility(View.INVISIBLE);
    }
}