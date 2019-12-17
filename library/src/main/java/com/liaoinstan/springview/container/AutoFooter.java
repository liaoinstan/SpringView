package com.liaoinstan.springview.container;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.liaoinstan.springview.R;
import com.liaoinstan.springview.widget.SpringView;

public class AutoFooter extends BaseScrollFooter {

    protected View rootView;
    protected ProgressBar progress_auto;
    protected View lay_auto_bottom_line;
    protected TextView text_bottom;
    protected boolean isInProgress;

    private String bottomText;

    public AutoFooter() {
        this("我是有底线的");
    }

    public AutoFooter(String bottomText) {
        setType(SpringView.Type.SCROLL);
        this.bottomText = bottomText;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        rootView = inflater.inflate(R.layout.auto_footer, viewGroup, false);
        progress_auto = rootView.findViewById(R.id.progress_auto);
        lay_auto_bottom_line = rootView.findViewById(R.id.lay_auto_bottom_line);
        text_bottom = rootView.findViewById(R.id.text_bottom);
        text_bottom.setText(bottomText);
        showProgressOrBottomLine(true);
        return rootView;
    }

    @Override
    protected void onScrollBottom() {
    }

    @Override
    protected void onScrollOut() {
    }

    @Override
    protected void onFooterMove(View rootView, int dy) {
    }

    @Override
    protected void onScreenFull(View rootView, boolean screenFull) {
        rootView.setVisibility(screenFull ? View.VISIBLE : View.GONE);
    }

    private void showProgressOrBottomLine(boolean isProgress) {
        this.isInProgress = isProgress;
        progress_auto.setVisibility(isProgress ? View.VISIBLE : View.GONE);
        lay_auto_bottom_line.setVisibility(!isProgress ? View.VISIBLE : View.GONE);
    }

    public void showProgress() {
        showProgressOrBottomLine(true);
    }

    public void showBottomLine() {
        showProgressOrBottomLine(false);
    }

    public boolean isInProgress() {
        return isInProgress;
    }
}
