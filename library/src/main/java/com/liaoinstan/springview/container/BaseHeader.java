package com.liaoinstan.springview.container;

import android.view.View;

import com.liaoinstan.springview.widget.SpringView;

/**
 * Created by liaoinstan on 2016/3/24.
 * 这个类为所有头部的虚基类，实现自定义的头部需继承自该类
 * 该类实现了SpringView.DragHander接口，为3个高度接口提供了默认的返回值
 */
public abstract class BaseHeader implements SpringView.DragHander {

    /**
     * 这个方法用于设置当前View的临界高度(limit hight)，即拉动到多少会被认定为刷新超作，而没到达该高度则不会执行刷新
     * 返回值大于0才有效，如果<=0 则设置为默认header的高度
     * 默认返回0
     */
    @Override
    public int getDragLimitHeight(View rootView) {
        return 0;
    }

    /**
     * 这个方法用于设置下拉最大高度(max height)，无论怎么拉动都不会超过这个高度
     * 返回值大于0才有效，如果<=0 则默认600px
     * 默认返回0
     */
    @Override
    public int getDragMaxHeight(View rootView) {
        return 0;
    }

    /**
     * 这个方法用于设置下拉弹动高度(spring height)，即弹动后停止状态的高度
     * 返回值大于0才有效，如果<=0 则设置为默认header的高度
     * 默认返回0
     */
    @Override
    public int getDragSpringHeight(View rootView) {
        return 0;
    }
}
