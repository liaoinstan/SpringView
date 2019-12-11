package com.liaoinstan.springview.widget;

import android.view.View;

import com.liaoinstan.springview.widget.SpringView;

/**
 * Scroll模式的callback处理类
 */
class ScrollCallbackHelper {

    private int lastHeaderDropDy = -1;
    private int lastFooterDropDy = -1;
    private Boolean lastFooterInScreen = null;
    private boolean callStartFooter = true;
    private boolean callFinishFooter = true;
    private boolean callStartHeader = true;
    private boolean callFinishHeader = true;

    /**
     * header 在移动时回调
     */
    void onHeaderMove(SpringView.DragHander headerHander, View header, int offsetTop) {
        if (lastHeaderDropDy != offsetTop) {
            headerHander.onDropAnim(header, offsetTop);
            if (offsetTop == 0 && callFinishHeader) {
                headerHander.onFinishAnim();
                callFinishHeader = false;
                callStartHeader = true;
            }
        }
        lastHeaderDropDy = offsetTop;
    }

    /**
     * footer 在移动时回调
     */
    void onFooterMove(SpringView.DragHander footerHander, View footer, int offsetBottom) {
        if (lastFooterDropDy != offsetBottom) {
            footerHander.onDropAnim(footer, offsetBottom);
            if (offsetBottom == 0 && callFinishFooter) {
                footerHander.onFinishAnim();
                callFinishFooter = false;
                callStartFooter = true;
            }
        }
        lastFooterDropDy = offsetBottom;
    }

    /**
     * 滚动到了顶部时回调
     */
    void onScrollTop(SpringView.DragHander headerHander, SpringView.OnFreshListener listener) {
        if (callStartHeader) {
            if (headerHander != null) headerHander.onStartAnim();
            if (listener != null) listener.onRefresh();
            callStartHeader = false;
            callFinishHeader = true;
        }
    }

    /**
     * 滚动到了底部时回调
     */
    void onScrollBottom(SpringView.DragHander footerHander, SpringView.OnFreshListener listener) {
        if (callStartFooter) {
            if (footerHander != null) footerHander.onStartAnim();
            if (listener != null) listener.onLoadmore();
            callStartFooter = false;
            callFinishFooter = true;
        }
    }

    /**
     * 页面内容高度是否足够显示footer的回调
     */
    void onScreenFull(SpringView.DragHander footerHander, View footer, boolean upORdown) {
        if (lastFooterInScreen == null || upORdown != lastFooterInScreen) {
            footerHander.onLimitDes(footer, upORdown);
            lastFooterInScreen = upORdown;
        }
    }


}
