package com.liaoinstan.springview.container;

import android.view.View;

import com.liaoinstan.springview.widget.SpringView;

/**
 * Created by liaoinstan on 2016/3/24.
 * 这个类为所有头部的虚基类，实现自定义的头部需继承自该类
 * 该类实现了SpringView.DragHander接口，为3个高度接口提供了默认的返回值
 */
public abstract class BaseHeader implements SpringView.DragHander {

    public String TAG = getClass().getSimpleName();

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

    /**
     * 设置移动参数
     * 如果你的header实现了该接口，那么SpringView会优先使用该接口返回的结果（忽略掉:SpringView.setMovePara()）
     * 添加这个接口的目的是为了让不同的header能够自行定义自己的摩擦阻力，而不再是由SpringView进行设置
     * 返回值大于0才有效，默认返回0
     */
    @Override
    public float getMovePara() {
        return 0;
    }

    /**
     * 返回header的拖动方式:type
     * 返回值非null才有效，默认返回null
     */
    @Override
    public SpringView.Type getType() {
        return null;
    }

    /**
     * 即将开始拖拽时的回调，可进行初始化操作
     */
    @Override
    public void onPreDrag(View rootView) {
    }

    /**
     * 拖拽结束时回调，不管是否拖拽超过limit阈值
     */
    @Override
    public void onFinishDrag(View rootView) {
    }

    //###################################################
    //############ 2018/9/13 新增收场动画接口 ###########
    //###################################################

    /**
     * 收场动画执行时间
     * 返回值大于0才有效
     * 默认返回0
     */
    @Override
    public int getEndingAnimTime() {
        return 0;
    }

    /**
     * 收场动画回弹高度
     * 返回值大于0才有效
     * 默认返回0
     */
    @Override
    public int getEndingAnimHight(View rootView) {
        return 0;
    }

    /**
     * 收场动画开始执行，默认空实现，如需要收场动画重写该方法
     */
    @Override
    public void onEndingAnimStart() {
    }

    /**
     * 收场动画结束执行，默认空实现，如需要收场动画重写该方法
     */
    @Override
    public void onEndingAnimEnd() {
    }
}
