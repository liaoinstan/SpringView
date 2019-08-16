package com.liaoinstan.springview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Handler;
import androidx.annotation.Nullable;
import com.google.android.material.appbar.AppBarLayout;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;

import com.liaoinstan.springview.R;
import com.liaoinstan.springview.listener.AppBarStateChangeListener;

/**
 * Created by liaoinstan on 2016/3/11.
 */
@SuppressWarnings("unused ")
public class SpringView extends ViewGroup {

    private Context context;
    private LayoutInflater inflater;
    private OverScroller mScroller;
    private Handler handler = new Handler();
    private OnFreshListener listener;         //监听回调
    private int lastScrollY;                 //上一次滚动时的Y轴滚动距离
    private boolean isFirst = true;         //用于判断是否是拖动动作的第一次move
    private boolean needChangeType = false;     //是否需要改变样式
    private boolean needResetAnim = false;  //是否需要弹回的动画
    private boolean isMoveNow = false;       //当前是否正在拖动
    private boolean enableHeader = true;    //是否禁止header下拉（默认可用）
    private boolean enableFooter = true;    //是否禁止footer上拉（默认可用）
    private boolean isCallFresh = false;    //是否手动调用callFresh()进行刷新操作

    private int MOVE_TIME = 400;

    //是否需要回调接口：TOP 只回调刷新、BOTTOM 只回调加载更多、BOTH 都需要、NONE 都不
    public enum Give {
        BOTH, TOP, BOTTOM, NONE
    }

    public enum Type {OVERLAP, FOLLOW, DRAG}

    private Give give = Give.BOTH;
    private Type type = Type.FOLLOW;
    private Type _type;

    //移动参数：计算手指移动量的时候会用到这个值，值越大，移动量越小，若值为1则手指移动多少就滑动多少px
    private double MOVE_PARA = 2;
    //最大拉动距离(px)，拉动距离越靠近这个值拉动就越缓慢
    private int MAX_HEADER_PULL_HEIGHT = 600;
    private int MAX_FOOTER_PULL_HEIGHT = 600;
    //拉动多少距离被认定为刷新(加载)动作
    private int HEADER_LIMIT_HEIGHT;
    private int FOOTER_LIMIT_HEIGHT;
    private int HEADER_SPRING_HEIGHT;
    private int FOOTER_SPRING_HEIGHT;
    private int HEADER_ENDING_HEIGHT;
    private int FOOTER_ENDING_HEIGHT;
    //储存手指拉动上次的Y坐标
    private float mLastY;
    private float mLastX;
    //储存手指拉动第一次的Y坐标
    private float mfirstY;
    //储存手指拉动的总距离
    private float dsY;
    //滑动事件目前是否在本控件的控制中（用于过渡滑动事件：比如正在滚动recyclerView到顶部后自动切换到SpringView处理后续事件进行下拉）
    private boolean isInControl = false;

    //头尾视图
    private View header;
    private View footer;
    //目标View，即被SpringView包裹的View
    private View contentLay;
    private View contentView;
    //头尾布局资源id
    private int headerResoureId;
    private int footerResoureId;

    //记录AppBarLayout的当前状态（展开或折叠）
    private AppBarStateChangeListener.State appbarState = AppBarStateChangeListener.State.EXPANDED;
    private boolean appBarCouldScroll = false;

    private int callFreshORload = 0;
    private int scrollAnimType;         //回弹动画类别 0:结束动画1:回弹动画2:退场动画
    private boolean hasCallFull = false;
    private boolean hasCallRefresh = false;
    private boolean hasCallEnding = false;

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        //解决和CollapsingToolbarLayout冲突的问题
        AppBarLayout appBarLayout = SpringHelper.findAppBarLayout(this);
        appBarCouldScroll = SpringHelper.couldScroll(appBarLayout);
        if (appBarLayout != null) {
            appBarLayout.addOnOffsetChangedListener(new AppBarStateChangeListener() {
                @Override
                public void onStateChanged(AppBarLayout appBarLayout, State state) {
                    appbarState = state;
                }
            });
        }
    }

    public SpringView(Context context) {
        this(context, null);
    }

    public SpringView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SpringView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        inflater = LayoutInflater.from(context);
        mScroller = new OverScroller(context);
        initAttr(attrs);
    }

    private void initAttr(AttributeSet attrs) {
        if (attrs == null) return;
        //获取自定义属性
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.SpringView);
        if (ta.hasValue(R.styleable.SpringView_type)) {
            int type_int = ta.getInt(R.styleable.SpringView_type, 0);
            type = Type.values()[type_int];
        }
        if (ta.hasValue(R.styleable.SpringView_give)) {
            int give_int = ta.getInt(R.styleable.SpringView_give, 0);
            give = Give.values()[give_int];
        }
        if (ta.hasValue(R.styleable.SpringView_header)) {
            headerResoureId = ta.getResourceId(R.styleable.SpringView_header, 0);
        }
        if (ta.hasValue(R.styleable.SpringView_footer)) {
            footerResoureId = ta.getResourceId(R.styleable.SpringView_footer, 0);
        }
        ta.recycle();
    }

    @Override
    protected void onFinishInflate() {
        View content = getChildAt(0);
        if (content == null) {
            return;
        }
        setPadding(0, 0, 0, 0);
        //contentLay.setPadding(0, contentLay.getPaddingTop(), 0, contentLay.getPaddingBottom());
        if (headerResoureId != 0) {
            inflater.inflate(headerResoureId, this, true);
            header = getChildAt(getChildCount() - 1);
        }
        if (footerResoureId != 0) {
            inflater.inflate(footerResoureId, this, true);
            footer = getChildAt(getChildCount() - 1);
        }

        //找到当前布局下可以滚动的view

        if (SpringHelper.isViewCouldScroll(content)) {
            //如果内容是可以滚动的view，则直接设置contentLay,contentView
            contentLay = content;
            contentView = content;
        } else {
            //如果内容布局不能滚动，则遍历找到可以滚动的view
            View viewCouldScroll = SpringHelper.findViewCouldScroll(content);
            if (viewCouldScroll != null) {
                //找到了则，把它设置为contentView
                contentView = viewCouldScroll;
            } else {
                //没有查找到可滚动布局，则把原布局设置为contentView
                contentView = content;
            }
            contentLay = content;
        }

        super.onFinishInflate();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (getChildCount() > 0) {
            for (int i = 0; i < getChildCount(); i++) {
                View child = getChildAt(i);
                measureChild(child, widthMeasureSpec, heightMeasureSpec);
            }
        }
        //如果是动态设置的头部，则使用动态设置的参数
        if (headerHander != null) {
            //设置下拉最大高度，只有在>0时才生效，否则使用默认值
            int xh = headerHander.getDragMaxHeight(header);
            if (xh > 0) MAX_HEADER_PULL_HEIGHT = xh;
            //设置下拉临界高度，只有在>0时才生效，否则默认为header的高度
            int h = headerHander.getDragLimitHeight(header);
            HEADER_LIMIT_HEIGHT = h > 0 ? h : header.getMeasuredHeight();
            //设置下拉弹动高度，只有在>0时才生效，否则默认和临界高度一致
            int sh = headerHander.getDragSpringHeight(header);
            HEADER_SPRING_HEIGHT = sh > 0 ? sh : HEADER_LIMIT_HEIGHT;
            HEADER_ENDING_HEIGHT = headerHander.getEndingAnimHight(header);
        } else {
            //不是动态设置的头部，设置默认值
            if (header != null) HEADER_LIMIT_HEIGHT = header.getMeasuredHeight();
            HEADER_SPRING_HEIGHT = HEADER_LIMIT_HEIGHT;
        }
        //设置尾部参数，和上面一样
        if (footerHander != null) {
            int xh = footerHander.getDragMaxHeight(footer);
            if (xh > 0) MAX_FOOTER_PULL_HEIGHT = xh;
            int h = footerHander.getDragLimitHeight(footer);
            FOOTER_LIMIT_HEIGHT = h > 0 ? h : footer.getMeasuredHeight();
            int sh = footerHander.getDragSpringHeight(footer);
            FOOTER_SPRING_HEIGHT = sh > 0 ? sh : FOOTER_LIMIT_HEIGHT;
            FOOTER_ENDING_HEIGHT = footerHander.getEndingAnimHight(footer);
        } else {
            if (footer != null) FOOTER_LIMIT_HEIGHT = footer.getMeasuredHeight();
            FOOTER_SPRING_HEIGHT = FOOTER_LIMIT_HEIGHT;
        }
        setMeasuredDimension(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (contentLay != null) {
            if (header != null) {
                header.layout(0, -header.getMeasuredHeight(), getWidth(), 0);
            }
            if (footer != null) {
                footer.layout(0, getHeight(), getWidth(), getHeight() + footer.getMeasuredHeight());
            }
            contentLay.layout(0, 0, contentLay.getMeasuredWidth(), contentLay.getMeasuredHeight());

            if (type == Type.OVERLAP) {
                //overlap模式需要把内容放在最前端
                contentLay.bringToFront();
            } else if (type == Type.DRAG) {
                //drag模式需要把头尾部分放在最前端
                if (header != null) header.bringToFront();
                if (footer != null) footer.bringToFront();
            }
        }
    }

    //记录单次滚动x,y轴偏移量
    private float dy;
    private float dx;
    //记录当前滚动事件是否需要SpringView进行处理，如果需要则SpringView拦截事件（比如已经滚动到顶部了还继续下拉）
    private boolean isNeedMyMove;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        dealMulTouchEvent(event);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                hasCallFull = false;
                hasCallRefresh = false;
                hasCallEnding = false;
                mfirstY = event.getY();
                isNeedMyMove = false;
                break;
            }
            case MotionEvent.ACTION_MOVE:
                boolean isTop = isChildScrollToTop();
                boolean isBottom = isChildScrollToBottom();
                //如果列表内容不满一屏（既已经到最顶部同时有在最底部），这时对appbar进行特殊处理（展开状态向上滚动、折叠状态向下滚动不进行处理）
                //TODO:列表不满一屏的时候存在拖拽粘滞的情况，有待优化，主要问题是如何将springView已经得到的事件传递给Appbar？
                if (appBarCouldScroll) {
                    if (isTop && isBottom) {
                        if (appbarState == AppBarStateChangeListener.State.EXPANDED && dy < 0) {
                            break;
                        } else if (appbarState == AppBarStateChangeListener.State.COLLAPSED && dy > 0) {
                            break;
                        }
                    }
                    //appBarLayout处于展开状态 || appBarLayout处于折叠状态并且手势上向上拉，则SpirngView处理滑动事件，否则不处理
                    if (appbarState == AppBarStateChangeListener.State.EXPANDED || appbarState == AppBarStateChangeListener.State.COLLAPSED && dy < 0) {
                    } else {
                        break;
                    }
                }
                /////////////////////////////////////////////////////appBar end//////////////////////////////////
                dsY += dy;
                isMoveNow = true;
                isNeedMyMove = isNeedMyMove();
                if (isNeedMyMove && !isInControl) {
                    //把内部控件的事件转发给本控件处理
                    isInControl = true;
                    event.setAction(MotionEvent.ACTION_CANCEL);
                    MotionEvent ev2 = MotionEvent.obtain(event);
                    dispatchTouchEvent(event);
                    ev2.setAction(MotionEvent.ACTION_DOWN);
                    return dispatchTouchEvent(ev2);
                }
                break;
            case MotionEvent.ACTION_UP:
                isMoveNow = false;
                break;
            case MotionEvent.ACTION_CANCEL:
                isMoveNow = false;
                break;
        }
        return super.dispatchTouchEvent(event);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        return isNeedMyMove;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (contentLay == null) {
            return false;
        }
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN:
                isFirst = true;
                break;
            case MotionEvent.ACTION_MOVE:
                if (isNeedMyMove) {
                    //按下的时候关闭回弹
                    needResetAnim = false;
                    //执行位移操作
                    doMove();
                    //下拉的时候显示header并隐藏footer，上拉的时候相反
                    if (isTop()) {
                        showHeaderAndFooter(true, false);
                    } else if (isBottom()) {
                        showHeaderAndFooter(false, true);
                    }
                    //回调onDropAnim接口
                    callbackOnDropAnim();
                    //回调callOnPreDrag接口
                    callbackOnPreDrag();
                    //回调onLimitDes接口
                    callbackOnLimitDes();
                    isFirst = false;
                } else {
                    //手指在产生移动的时候（dy!=0）才重置位置
                    if (dy != 0 && isFlow()) {
                        resetPosition();
                        //把滚动事件交给内部控件处理
                        event.setAction(MotionEvent.ACTION_DOWN);
                        dispatchTouchEvent(event);
                        isInControl = false;
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                needResetAnim = true;      //松开的时候打开回弹
                isFirst = true;
                _firstDrag = true;
                restSmartPosition();
                dsY = 0;
                dy = 0;
                break;
            case MotionEvent.ACTION_CANCEL:
                break;
        }
        return false;
    }

    /**
     * 处理多点触控的情况，准确地计算Y坐标和移动距离dy
     * 同时兼容单点触控的情况
     */
    private int mActivePointerId = MotionEvent.INVALID_POINTER_ID;

    public void dealMulTouchEvent(MotionEvent ev) {
        final int action = ev.getActionMasked();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                final int pointerIndex = ev.getActionIndex();
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);
                mLastX = x;
                mLastY = y;
                mActivePointerId = ev.getPointerId(0);
                break;
            }
            case MotionEvent.ACTION_MOVE: {
                final int pointerIndex = ev.findPointerIndex(mActivePointerId);
                final float x = ev.getX(pointerIndex);
                final float y = ev.getY(pointerIndex);
                dx = x - mLastX;
                dy = y - mLastY;
                mLastY = y;
                mLastX = x;
                break;
            }
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                mActivePointerId = MotionEvent.INVALID_POINTER_ID;
                break;
            case MotionEvent.ACTION_POINTER_DOWN: {
                final int pointerIndex = ev.getActionIndex();
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId != mActivePointerId) {
                    mLastX = ev.getX(pointerIndex);
                    mLastY = ev.getY(pointerIndex);
                    mActivePointerId = ev.getPointerId(pointerIndex);
                }
                break;
            }
            case MotionEvent.ACTION_POINTER_UP: {
                final int pointerIndex = ev.getActionIndex();
                final int pointerId = ev.getPointerId(pointerIndex);
                if (pointerId == mActivePointerId) {
                    final int newPointerIndex = pointerIndex == 0 ? 1 : 0;
                    mLastX = ev.getX(newPointerIndex);
                    mLastY = ev.getY(newPointerIndex);
                    mActivePointerId = ev.getPointerId(newPointerIndex);
                }
                break;
            }
        }
    }

    //执行位移操作
    private void doMove() {
        if (!mScroller.isFinished()) mScroller.forceFinished(true);
        //根据下拉高度计算位移距离，（越拉越慢）
        int movedy;
        if (dy > 0) {
            movedy = (int) (((MAX_HEADER_PULL_HEIGHT + getScrollY()) / (float) MAX_HEADER_PULL_HEIGHT) * dy / MOVE_PARA);
        } else {
            movedy = (int) (((MAX_FOOTER_PULL_HEIGHT - getScrollY()) / (float) MAX_FOOTER_PULL_HEIGHT) * dy / MOVE_PARA);
        }
        scrollBy(0, -movedy);

        callOnScrollAndDrag();
    }

    //滚动回调
    @Override
    public void computeScroll() {
        if (mScroller.computeScrollOffset()) {
            scrollTo(0, mScroller.getCurrY());
            lastScrollY = getScrollY();
            callbackOnDropAnim();
            callOnScrollAndDrag();
            invalidate();
        }
        //在滚动动画完全结束后回调接口
        //滚动回调过程中mScroller.isFinished会多次返回true，导致判断条件被多次进入，设置标志位保证只调用一次
        if (!isMoveNow && mScroller.isFinished()) {
            if (scrollAnimType == 0) {
                if (!hasCallFull) {
                    hasCallFull = true;
                    callOnAfterFullAnim();
                }
            } else if (scrollAnimType == 1) {
                if (!hasCallRefresh) {
                    hasCallRefresh = true;
                    callOnAfterRefreshAnim();
                }
            } else if (scrollAnimType == 2) {
                if (!hasCallEnding) {
                    hasCallEnding = true;
                    callOnAfterEndingAnim();
                }
            }
        }
    }

    //springView滚动或者拖拽的时候会执行该方法
    private void callOnScrollAndDrag() {
        if (type == Type.OVERLAP) {
            if (header != null) header.setTranslationY(header.getHeight() + getScrollY());
            if (footer != null) footer.setTranslationY(-footer.getHeight() + getScrollY());
        } else if (type == Type.DRAG) {
            if (contentLay != null) contentLay.setTranslationY(getScrollY());
        }
    }

    //在完成全部动作后会执行此方法
    private void callOnAfterFullAnim() {
        //回调onFinishAnim()
        if (callFreshORload == 1) {
            if (headerHander != null) headerHander.onFinishAnim();
            //如果在回弹动作中没有进行回调(说明是设置为不可弹动状态)，那么在这里进行(但是callFresh例外)
            if (give == Give.BOTTOM || give == Give.NONE && !isCallFresh) {
                listener.onRefresh();
            }
            isCallFresh = false;
        } else if (callFreshORload == 2) {
            if (footerHander != null) footerHander.onFinishAnim();
            if (give == Give.TOP || give == Give.NONE) {
                listener.onLoadmore();
            }
        }
        callFreshORload = 0;
        //动画完成后检查是否需要切换header/footer，是则切换
        if (needChangeHeader) {
            needChangeHeader = false;
            setHeaderIn(_headerHander);
        }
        if (needChangeFooter) {
            needChangeFooter = false;
            setFooterIn(_footerHander);
        }
        //动画完成后检查是否需要切换type，是则切换
        if (needChangeType) {
            changeType(_type);
        }
    }

    //在回弹到刷新位置的时候会执行此方法

    /**
     * 只有设置可弹动或者手动调用callFresh才会执行到这个回调，不弹动状态直接执行{@link #callOnAfterFullAnim()}
     */
    private void callOnAfterRefreshAnim() {
        if (isTop()) {
            listener.onRefresh();
        } else if (isBottom()) {
            listener.onLoadmore();
        }
    }

    //在开始退场动画时会执行此方法
    private void callOnAfterEndingAnim() {
        final DragHander dragHander = isTop() ? headerHander : footerHander;
        if (dragHander == null) return;
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                dragHander.onEndingAnimEnd();
                resetPosition();
            }
        }, dragHander.getEndingAnimTime());
    }

    //回调自定义header/footer OnDropAnim接口
    private void callbackOnDropAnim() {
        if (getScrollY() < 0)
            if (headerHander != null) headerHander.onDropAnim(header, -getScrollY());
        if (getScrollY() > 0)
            if (footerHander != null) footerHander.onDropAnim(footer, -getScrollY());
    }

    private boolean _firstDrag = true;

    //回调自定义header/footer OnPreDrag接口
    private void callbackOnPreDrag() {
        if (_firstDrag) {
            if (isTop()) {
                if (headerHander != null) headerHander.onPreDrag(header);
                _firstDrag = false;
            } else if (isBottom()) {
                if (footerHander != null) footerHander.onPreDrag(footer);
                _firstDrag = false;
            }
        }
    }

    //回调自定义header/footer OnLimitDes接口
    private void callbackOnLimitDes() {
        int scrollY = getScrollY();
        if (scrollY < 0) {
            //下拉
            if (Math.abs(scrollY) >= HEADER_LIMIT_HEIGHT && Math.abs(lastScrollY) < HEADER_LIMIT_HEIGHT) {
                if (headerHander != null) headerHander.onLimitDes(header, false);
            } else if (Math.abs(scrollY) <= HEADER_LIMIT_HEIGHT && Math.abs(lastScrollY) > HEADER_LIMIT_HEIGHT) {
                if (headerHander != null) headerHander.onLimitDes(header, true);
            }
        } else {
            //上拉
            if (Math.abs(scrollY) >= HEADER_LIMIT_HEIGHT && Math.abs(lastScrollY) < HEADER_LIMIT_HEIGHT) {
                if (footerHander != null) footerHander.onLimitDes(header, true);
            } else if (Math.abs(scrollY) <= HEADER_LIMIT_HEIGHT && Math.abs(lastScrollY) > HEADER_LIMIT_HEIGHT) {
                if (footerHander != null) footerHander.onLimitDes(header, false);
            }
        }
        lastScrollY = scrollY;
    }

    private void callbackOnStartAnim() {
        if (isTop()) {  //下拉
            callFreshORload = 1;
            if (headerHander != null) headerHander.onStartAnim();
        } else if (isBottom()) {
            callFreshORload = 2;
            if (footerHander != null) footerHander.onStartAnim();
        }
    }

    /**
     * 判断是否需要由该控件来控制滑动事件
     */
    @SuppressWarnings("all")
    private boolean isNeedMyMove() {
        if (contentLay == null) {
            return false;
        }
        //横向拖拽距离大于竖直距离则不拦截
        if (Math.abs(dy) <= Math.abs(dx)) {
            return false;
        }
        boolean isTop = isChildScrollToTop();
        boolean isBottom = isChildScrollToBottom();
        //用户禁止了下拉操作，则不控制
        if (!enableHeader && isTop && dy > 0) {
            return false;
        }
        //用户禁止了上拉操作，则不控制
        if (!enableFooter && isBottom && dy < 0) {
            return false;
        }
        if (header != null) {
            //其中的20是一个防止触摸误差的偏移量
            if (isTop && dy > 0 || getScrollY() < 0 - 20) {
                return true;
            }
        }
        if (footer != null) {
            if (isBottom && dy < 0 || getScrollY() > 0 + 20) {
                return true;
            }
        }
        return false;
    }

    /**
     * 重置控件位置到初始状态
     */
    private void resetPosition() {
        scrollAnimType = 0;
        isInControl = false;    //重置位置的时候，滑动事件已经不在控件的控制中了
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), MOVE_TIME);
        invalidate();
    }

    /**
     * 重置控件位置到刷新状态（或加载状态）
     */
    private void resetRefreshPosition() {
        scrollAnimType = 1;
        isInControl = false;    //重置位置的时候，滑动事件已经不在控件的控制中了
        if (getScrollY() < 0) {     //下拉
            mScroller.startScroll(0, getScrollY(), 0, -getScrollY() - HEADER_SPRING_HEIGHT, MOVE_TIME);
            invalidate();
        } else {       //上拉
            mScroller.startScroll(0, getScrollY(), 0, -getScrollY() + FOOTER_SPRING_HEIGHT, MOVE_TIME);
            invalidate();
        }
    }

    /**
     * 重置到收场动画状态
     */
    private void resetEndingPosition() {
        scrollAnimType = 2;
        isInControl = false;    //重置位置的时候，滑动事件已经不在控件的控制中了
        if (getScrollY() < 0) {     //下拉
            if (headerHander != null) headerHander.onEndingAnimStart();
            mScroller.startScroll(0, getScrollY(), 0, -getScrollY() - HEADER_ENDING_HEIGHT, MOVE_TIME);
            invalidate();
        } else {       //上拉
            if (footerHander != null) footerHander.onEndingAnimStart();
            mScroller.startScroll(0, getScrollY(), 0, -getScrollY() + FOOTER_ENDING_HEIGHT, MOVE_TIME);
            invalidate();
        }
    }

    /**
     * 智能判断是重置控件位置到初始状态还是到刷新/加载状态
     */
    private void restSmartPosition() {
        if (listener == null) {
            //没有设置监听器直接复原
            resetPosition();
        } else {
            if (isTopOverLimit()) {
                //顶部拉动超过limit位置
                callbackOnStartAnim();
                if (give == Give.BOTH || give == Give.TOP)
                    resetRefreshPosition();
                else
                    resetPosition();
            } else if (isBottomOverLimit()) {
                //底部拉动超过limit位置
                callbackOnStartAnim();
                if (give == Give.BOTH || give == Give.BOTTOM)
                    resetRefreshPosition();
                else
                    resetPosition();
            } else {
                //顶部和底部拉动均为超过limit位置，直接复原
                resetPosition();
            }
        }
    }

    /**
     * {@link #callFresh()}的执行方法，不要暴露在外部
     */
    private void _callFresh() {
        isCallFresh = true;
        scrollAnimType = 1;     //不是全部回弹动画（半回弹到limit位置）
        needResetAnim = true;   //允许执行回弹动画
        hasCallRefresh = false;
        callFreshORload = 1;
        if (headerHander != null) headerHander.onStartAnim();
        showHeaderAndFooter(true, false);
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY() - HEADER_SPRING_HEIGHT, MOVE_TIME);
        invalidate();
    }

    private void showHeaderAndFooter(boolean showHeader, boolean showFooter) {
        if (header != null)
            header.setVisibility(showHeader ? View.VISIBLE : View.INVISIBLE);
        if (footer != null)
            footer.setVisibility(showFooter ? View.VISIBLE : View.INVISIBLE);
    }


    /**
     * 判断目标View是否滑动到顶部 还能否继续滑动
     */
    private boolean isChildScrollToTop() {
        return !contentView.canScrollVertically(-1);
    }

    /**
     * 是否滑动到底部
     */
    private boolean isChildScrollToBottom() {
        return !contentView.canScrollVertically(1);
    }

    /**
     * 判断顶部拉动是否超过临界值
     */
    private boolean isTopOverLimit() {
        return -getScrollY() > HEADER_LIMIT_HEIGHT;
    }

    /**
     * 判断底部拉动是否超过临界值
     */
    private boolean isBottomOverLimit() {
        return getScrollY() > FOOTER_LIMIT_HEIGHT;
    }

    /**
     * 判断当前状态是否拉动到顶部
     */
    private boolean isTop() {
        return getScrollY() < 0;
    }

    /**
     * 判断当前状态是否拉动到底部
     */
    private boolean isBottom() {
        return getScrollY() > 0;
    }

    /**
     * 判断当前滚动位置是否已经进入可折叠范围了
     */
    private boolean isFlow() {
        return getScrollY() > -30 && getScrollY() < 30;
    }

    /**
     * 切换Type的执行方法，之所以不暴露在外部，是防止用户在拖动过程中调用造成布局错乱，虽然并不会有人这样做
     * 所以在外部方法{@link #setType(Type)}中设置标志，然后在拖动完毕后判断是否需要调用，是则调用本方法执行切换
     */
    private void changeType(Type type) {
        this.type = type;
        requestLayout();
        needChangeType = false;
        if (header != null) header.setTranslationY(0);
        if (footer != null) footer.setTranslationY(0);
    }

    //#############################################
    //##            对外暴露的方法               ##
    //#############################################

    /**
     * 重置控件位置，暴露给外部的方法，用于在刷新或者加载完成后调用
     */
    public void onFinishFreshAndLoad() {
        if (!isMoveNow && needResetAnim) {
            if (isCallFresh) {
                //手动调用callFresh进行刷新
                if (isTop()) {
                    //检查是否需要执行收场动画
                    if (headerHander != null && headerHander.getEndingAnimTime() > 0) {
                        //需要则，回弹到收场高度
                        resetEndingPosition();
                    } else {
                        //不需要则，直接跳过
                        resetPosition();
                    }
                } else {
                    resetPosition();
                }
            } else {
                //拉动回弹刷新
                boolean needTop = isTop() && (give == Give.TOP || give == Give.BOTH);
                boolean needBottom = isBottom() && (give == Give.BOTTOM || give == Give.BOTH);
                if (needTop || needBottom) {
                    //检查是否需要执行收场动画
                    if (headerHander != null && headerHander.getEndingAnimTime() > 0) {
                        //需要则，回弹到收场高度
                        resetEndingPosition();
                    } else {
                        //不需要则，直接跳过
                        resetPosition();
                    }
                }
            }
        }
    }

    public void onFinishFreshAndLoadDelay(int delay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                onFinishFreshAndLoad();
            }
        }, delay);
    }

    public void onFinishFreshAndLoadDelay() {
        onFinishFreshAndLoadDelay(100);
    }

    /**
     * 手动调用该方法使SpringView进入拉动更新的状态
     */
    public void callFresh() {
        _callFresh();
    }

    public void callFreshDelay(int delay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                callFresh();
            }
        }, delay);
    }

    public void callFreshDelay() {
        callFreshDelay(100);
    }

    public void setMoveTime(int time) {
        this.MOVE_TIME = time;
    }

    public void setMovePara(double movePara) {
        this.MOVE_PARA = movePara;
    }

    /**
     * 是否禁用SpringView
     */
    public void setEnable(boolean enable) {
        this.enableHeader = enable;
        this.enableFooter = enable;
    }

    public boolean isEnable() {
        return enableHeader && enableFooter;
    }

    public boolean isEnableHeader() {
        return enableHeader;
    }

    public void setEnableHeader(boolean enableHeader) {
        this.enableHeader = enableHeader;
    }

    public boolean isEnableFooter() {
        return enableFooter;
    }

    public void setEnableFooter(boolean enableFooter) {
        this.enableFooter = enableFooter;
    }

    /**
     * 设置监听
     */
    public void setListener(OnFreshListener listener) {
        this.listener = listener;
    }

    /**
     * 动态设置弹性模式
     */
    public void setGive(Give give) {
        this.give = give;
    }

    /**
     * 改变样式的对外接口
     */
    public void setType(Type type) {
        if (isTop() || isBottom()) {
            //如果当前用户正在拖动，直接调用changeType()会造成布局错乱
            //设置needChange标志，在执行完拖动后再调用changeType()
            needChangeType = true;
            //把参数保持起来
            _type = type;
        } else {
            changeType(type);
        }
    }

    /**
     * 获取当前样式
     */
    public Type getType() {
        return type;
    }

    /**
     * 回调接口
     */
    public interface OnFreshListener {
        /**
         * 下拉刷新，回调接口
         */
        void onRefresh();

        /**
         * 上拉加载，回调接口
         */
        void onLoadmore();
    }

    public View getHeaderView() {
        return header;
    }

    public View getFooterView() {
        return footer;
    }

    private boolean needChangeHeader = false;
    private boolean needChangeFooter = false;
    private DragHander _headerHander;
    private DragHander _footerHander;
    private DragHander headerHander;
    private DragHander footerHander;

    public DragHander getHeader() {
        return headerHander;
    }

    public DragHander getFooter() {
        return footerHander;
    }

    public void setHeader(DragHander headerHander) {
        if (this.headerHander != null && isTop()) {
            needChangeHeader = true;
            _headerHander = headerHander;
            resetPosition();
        } else {
            setHeaderIn(headerHander);
        }
    }

    private void setHeaderIn(DragHander headerHander) {
        this.headerHander = headerHander;
        if (header != null) {
            removeView(this.header);
        }
        headerHander.getView(inflater, this);
        this.header = getChildAt(getChildCount() - 1);
//        contentLay.bringToFront(); //把内容放在最前端
        requestLayout();
    }

    public void setFooter(DragHander footerHander) {
        if (this.footerHander != null && isBottom()) {
            needChangeFooter = true;
            _footerHander = footerHander;
            resetPosition();
        } else {
            setFooterIn(footerHander);
        }
    }

    private void setFooterIn(DragHander footerHander) {
        this.footerHander = footerHander;
        if (footer != null) {
            removeView(footer);
        }
        footerHander.getView(inflater, this);
        this.footer = getChildAt(getChildCount() - 1);
        requestLayout();
    }

    /**
     * header/footer核心接口
     */
    public interface DragHander {
        View getView(LayoutInflater inflater, ViewGroup viewGroup);

        int getDragLimitHeight(View rootView);

        int getDragMaxHeight(View rootView);

        int getDragSpringHeight(View rootView);

        /**
         * 即将开始拖拽时的回调，可进行初始化操作
         */
        void onPreDrag(View rootView);

        /**
         * 手指拖动控件过程中的回调，用户可以根据拖动的距离添加拖动过程动画
         *
         * @param dy 拖动距离，下拉为+，上拉为-
         */
        void onDropAnim(View rootView, int dy);

        /**
         * 手指拖动控件过程中每次抵达临界点时的回调，用户可以根据手指方向设置临界动画
         *
         * @param upORdown 是上拉还是下拉 true(上)，false(下)
         */
        void onLimitDes(View rootView, boolean upORdown);

        /**
         * 拉动超过临界点后松开时回调
         */
        void onStartAnim();

        /**
         * 头(尾)已经全部弹回时回调
         */
        void onFinishAnim();

        /**
         * 收场动画执行时间
         */
        int getEndingAnimTime();

        /**
         * 收场动画回弹高度
         */
        int getEndingAnimHight(View rootView);

        /**
         * 收场动画开始执行
         */
        void onEndingAnimStart();

        /**
         * 收场动画结束执行
         */
        void onEndingAnimEnd();
    }
}
