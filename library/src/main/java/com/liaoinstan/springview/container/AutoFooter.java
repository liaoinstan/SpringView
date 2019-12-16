package com.liaoinstan.springview.container;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import com.liaoinstan.springview.R;
import com.liaoinstan.springview.widget.SpringView;

public class AutoFooter extends BaseScrollFooter {

    private View rootView;
    private ProgressBar progress_auto;
    private View lay_auto_bottom_line;
    private boolean isInProgress;

    public AutoFooter() {
        setType(SpringView.Type.SCROLL);
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        rootView = inflater.inflate(R.layout.auto_footer, viewGroup, false);
        progress_auto = rootView.findViewById(R.id.progress_auto);
        lay_auto_bottom_line = rootView.findViewById(R.id.lay_auto_bottom_line);
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
