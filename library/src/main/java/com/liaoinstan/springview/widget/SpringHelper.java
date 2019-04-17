package com.liaoinstan.springview.widget;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;
import android.webkit.WebView;
import android.widget.ListView;
import android.widget.ScrollView;

import com.google.android.material.appbar.AppBarLayout;

/**
 * Created by liaoinstan on 2017/8/15.
 * 为处理SpringView中的一些逻辑提供的帮助类
 */

class SpringHelper {

    /**
     * 在当前布局中查找AppBarLayout并返回，没有找到返回null
     * #解决和AppBarLayout自身滚动冲突的问题#
     */
    public static AppBarLayout findAppBarLayout(View v) {
        AppBarLayout appBarLayout = null;
        ViewParent p = v.getParent();
        while (p != null) {
            if (p instanceof CoordinatorLayout) {
                break;
            }
            p = p.getParent();
        }
        if (p instanceof CoordinatorLayout) {
            CoordinatorLayout coordinatorLayout = (CoordinatorLayout) p;
            final int childCount = coordinatorLayout.getChildCount();
            for (int i = childCount - 1; i >= 0; i--) {
                final View child = coordinatorLayout.getChildAt(i);
                if (child instanceof AppBarLayout) {
                    appBarLayout = (AppBarLayout) child;
                    break;
                }
            }
        }
        return appBarLayout;
    }

    //检查appBarLayout是否可以滚动
    public static boolean couldScroll(AppBarLayout appBarLayout) {
        if (appBarLayout == null) return false;
        boolean couldScroll = false;
        for (int i = 0; i < appBarLayout.getChildCount(); i++) {
            View child = appBarLayout.getChildAt(i);
            if (couldScrollChild(child)) {
                couldScroll = true;
                break;
            }
        }
        return couldScroll;
    }

    private static boolean couldScrollChild(View v) {
        if (v.getLayoutParams() instanceof AppBarLayout.LayoutParams) {
            AppBarLayout.LayoutParams layoutParams = (AppBarLayout.LayoutParams) v.getLayoutParams();
            int scrollFlags = layoutParams.getScrollFlags();
            if ((scrollFlags & AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL) == AppBarLayout.LayoutParams.SCROLL_FLAG_SCROLL) {
                return true;
            } else {
                return false;
            }
        } else {
            Log.e("SpringView", "view检查出现异常");
            return false;
        }
    }

    //检查当前View是否是可以滚动的View（限定垂直方向）
    public static boolean isViewCouldScroll(View view) {
        //如果是以下的view ,则直接返回true
        if (view instanceof ListView
                || view instanceof ScrollView
                || view instanceof NestedScrollView
                || view instanceof RecyclerView
                || view instanceof WebView) {
            return true;
        } else {
            //否则，检查view是否可以在垂直方向上滚动
            return view.canScrollVertically(-1) || view.canScrollVertically(1);
        }
    }

    //递归查找当前view和所有子view，找到可滚动的view，找不到则返回null
    public static View findViewCouldScroll(View view) {
        if (isViewCouldScroll(view)) {
            return view;
        } else if (view instanceof ViewGroup) {
            //view group
            ViewGroup viewGroup = (ViewGroup) view;
            for (int i = 0; i < viewGroup.getChildCount(); i++) {
                View findView = findViewCouldScroll(viewGroup.getChildAt(i));
                if (findView != null) {
                    return findView;
                }
            }
            return null;
        } else {
            //view
            return null;
        }
    }
}
