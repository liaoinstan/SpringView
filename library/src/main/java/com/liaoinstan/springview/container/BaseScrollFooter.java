package com.liaoinstan.springview.container;

import android.view.View;

import com.liaoinstan.springview.widget.SpringView;

public abstract class BaseScrollFooter extends BaseFooter {

    @Override
    public final SpringView.Type getType() {
        return SpringView.Type.SCROLL;
    }

    @Override
    public final void onDropAnim(View rootView, int dy) {
        onFooterMove(rootView, dy);
    }

    @Override
    public final void onLimitDes(View rootView, boolean upORdown) {
        onScreenFull(rootView, upORdown);
    }

    @Override
    public final void onStartAnim() {
        onScrollBottom();
    }

    @Override
    public final void onFinishAnim() {
        onScrollOut();
    }

    @Override
    public final void onPreDrag(View rootView) {
    }

    @Override
    public final void onFinishDrag(View rootView) {
    }

    @Override
    public final int getDragLimitHeight(View rootView) {
        return 0;
    }

    @Override
    public final int getDragMaxHeight(View rootView) {
        return 0;
    }

    @Override
    public final int getDragSpringHeight(View rootView) {
        return 0;
    }

    @Override
    public final float getMovePara() {
        return 0;
    }

    @Override
    public final int getEndingAnimTime() {
        return 0;
    }

    @Override
    public final int getEndingAnimHight(View rootView) {
        return 0;
    }

    @Override
    public final void onEndingAnimStart() {
    }

    @Override
    public final void onEndingAnimEnd() {
    }

    /**
     * 当页面滚动到最底部时回调（此时footer已经完全展示）
     */
    protected abstract void onScrollBottom();

    /**
     * 当页面滚动到footer完全不可见时回调（此时footer已经完全滚出页面）
     */
    protected abstract void onScrollOut();

    /**
     * 当正在滚动footer时回调，此时该方法会不断调用，并提供footer滚动距离：dy
     * dy 范围为 0 - footer最大高度，为0时footer刚好不可见，为footer最大高度时刚好完全可见
     */
    protected abstract void onFooterMove(View rootView, int dy);

    /**
     * 是否一屏可用完全展示内容回调
     * screenFull 为 false 时，表示此时页面内容高度不足一屏（无法滚动），为true表示大于一屏
     * 添加这个接口的主要目的是为了，在页面内容高度不足以支持滚动时不显示footer，如果有这种需求就可以在这个回调中处理，否则可用忽略
     */
    protected abstract void onScreenFull(View rootView, boolean screenFull);
}
