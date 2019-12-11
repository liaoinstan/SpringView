package com.liaoinstan.springview.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.os.Build;
import android.os.Handler;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.OverScroller;
import android.widget.ScrollView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.view.ScrollingView;
import androidx.core.widget.NestedScrollView;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.appbar.AppBarLayout;
import com.liaoinstan.springview.R;
import com.liaoinstan.springview.container.InnerFooter;
import com.liaoinstan.springview.container.InnerHeader;
import com.liaoinstan.springview.listener.AppBarStateChangeListener;

/**
 * Created by liaoinstan on 2016/3/11.
 */
@SuppressWarnings("unused ")
public class SpringView extends ViewGroup {

    //是否需要回调接口：TOP 只回调刷新、BOTTOM 只回调加载更多、BOTH 都需要、NONE 都不
    public enum Give {BOTH, TOP, BOTTOM, NONE}

    //拖拽类型
    public enum Type {OVERLAP, FOLLOW, DRAG, SCROLL}

    private Context context;
    private LayoutInflater inflater;
    private OverScroller mScroller;
    private Handler handler = new Handler();
    private Flag flag = new Flag();             //Spring使用的标志位
    private OnFreshListener listener;           //监听回调
    private int lastScrollY;                    //上一次滚动时的Y轴滚动距离
    private boolean needChangeType = false;     //是否需要改变样式
    private boolean needResetAnim = false;      //是否需要弹回的动画
    private boolean isMoveNow = false;          //当前是否正在拖动
    private boolean enableHeader = true;        //是否禁止header下拉（默认可用）
    private boolean enableFooter = true;        //是否禁止footer上拉（默认可用）
    private boolean isCallFresh = false;        //是否手动调用callFresh()进行刷新操作

    private int MOVE_TIME = 600;                //统一弹动动画时间
    //移动参数：计算手指移动量的时候会用到这个值，值越大，移动量越小，若值为1则手指移动多少就滑动多少px
    private float MOVE_PARA = 2;
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
    //储存手指拉动的总距离
    private float dsY;
    //储存当前的Y轴滚动距离
    private float scrollY;
    //储存上一次的Y轴滚动距离
    private float oldScrollY;
    //滑动事件目前是否在本控件的控制中（用于过渡滑动事件：比如正在滚动recyclerView到顶部后自动切换到SpringView处理后续事件进行下拉）
    private boolean isInControl = false;

    private Give give = Give.BOTH;
    private Type type = Type.FOLLOW;
    private Type _type;

    //头尾视图
    private View header;
    private View footer;
    //目标View，即被SpringView包裹的View
    private View contentLay;
    private View contentView;
    //头尾布局资源id
    private int headerResourceId;
    private int footerResourceId;

    //记录AppBarLayout的当前状态（展开或折叠）
    private AppBarStateChangeListener.State appbarState = AppBarStateChangeListener.State.EXPANDED;
    private boolean appBarCouldScroll = false;

    //保存上次ContentView的paddingTop和paddingBottom
    int paddingTopContent;
    int paddingBottomContent;

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
            headerResourceId = ta.getResourceId(R.styleable.SpringView_header, 0);
        }
        if (ta.hasValue(R.styleable.SpringView_footer)) {
            footerResourceId = ta.getResourceId(R.styleable.SpringView_footer, 0);
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
        if (headerResourceId != 0) {
            _setHeader(new InnerHeader(headerResourceId));
        }
        if (footerResourceId != 0) {
            _setFooter(new InnerFooter(footerResourceId));
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

        //TODO:在需要的时候才设置监听器？
        if (contentView instanceof RecyclerView) {
            //TODO:recyclerView在设置这个监听器的时候就会自动调用一次，ScrollView不会
            ((RecyclerView) contentView).addOnScrollListener(new RecyclerView.OnScrollListener() {
                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    paddingScroll();
                }
            });
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                contentView.setOnScrollChangeListener(new OnScrollChangeListener() {
                    @Override
                    public void onScrollChange(View v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                        paddingScroll();
                    }
                });
            }
        }

        //保存padding
        paddingTopContent = contentView.getPaddingTop();
        paddingBottomContent = contentView.getPaddingBottom();

        super.onFinishInflate();
    }

    private void paddingScroll() {
        if (judgeType(headerHander) != Type.SCROLL && judgeType(footerHander) != Type.SCROLL) {
            return;
        }
        //计算内容高度和滚动距离
        int contentHeight;
        int scrollY;
        if (contentView instanceof RecyclerView) {
            contentHeight = ((RecyclerView) contentView).computeVerticalScrollRange();
            scrollY = ((RecyclerView) contentView).computeVerticalScrollOffset();
        } else if (contentView instanceof NestedScrollView) {
            //不知道为何，NestedScrollView获取computeVerticalScrollRange的结果会莫名奇妙多出一个paddingTop的高度？
            contentHeight = ((ScrollingView) contentView).computeVerticalScrollRange() - contentView.getPaddingTop();
            scrollY = contentView.getScrollY();
        } else if (contentView instanceof ScrollView) {
            contentHeight = ((ViewGroup) contentView).getChildAt(0).getMeasuredHeight();
            scrollY = contentView.getScrollY();
        } else {
            //TODO:?contentView是其他的情况
            contentHeight = contentView.getMeasuredHeight();
            scrollY = contentView.getScrollY();
        }

        //可滚动最大高度
        int maxY = contentHeight - contentView.getMeasuredHeight() + contentView.getPaddingBottom() + contentView.getPaddingTop();
        if (maxY < 0) maxY = 0;
        //上下部分滚动距离
        //int offsetBottom = footer.getMeasuredHeight() - (maxY - scrollY);
        //int offsetTop = -(header.getMeasuredHeight() - scrollY);
        int offsetBottom = FOOTER_SPRING_HEIGHT - (maxY - scrollY);
        int offsetTop = HEADER_SPRING_HEIGHT - scrollY;

        if (judgeType(headerHander) == Type.SCROLL) {
            if (offsetTop > 0) {
                header.setVisibility(VISIBLE);
                header.setTranslationY(offsetTop);
                scrollCallbackHelper.onHeaderMove(headerHander, header, offsetTop);
            } else {
                header.setTranslationY(0);
                scrollCallbackHelper.onHeaderMove(headerHander, header, 0);
            }
        }
        if (judgeType(footerHander) == Type.SCROLL) {
            if (offsetBottom > 0) {
                footer.setVisibility(VISIBLE);
                footer.setTranslationY(-offsetBottom);
                scrollCallbackHelper.onFooterMove(footerHander, footer, offsetBottom);
            } else {
                footer.setTranslationY(0);
                scrollCallbackHelper.onFooterMove(footerHander, footer, 0);
            }
        }

        //Log.e("paddingScroll", "maxY:" + maxY + " scrollY:" + scrollY + " contentHeight:" + contentHeight + " offsetBottom:" + offsetBottom + " offsetTop:" + offsetTop);

        //准备回调事件
        if (scrollY == 0) {
            //滚动到了顶部
            if (judgeType(headerHander) == Type.SCROLL) {
                scrollCallbackHelper.onScrollTop(headerHander, listener);
            }
        }
        if (scrollY == maxY) {
            //滚动到了底部
            //如果maxY=0则内部高度不足一屏，此时scrollY也为0，可以认为此时既在顶部同时也在底部
            if (judgeType(footerHander) == Type.SCROLL) {
                scrollCallbackHelper.onScrollBottom(footerHander, listener);
            }
        }
        if (maxY <= FOOTER_SPRING_HEIGHT) {
            //除去footer的高度已经不足一屏了
            if (judgeType(footerHander) == Type.SCROLL) {
                scrollCallbackHelper.onScreenFull(footerHander, footer, false);
            }
        } else {
            //超过一屏
            if (judgeType(footerHander) == Type.SCROLL) {
                scrollCallbackHelper.onScreenFull(footerHander, footer, true);
            }
        }
    }

    //回调器：SCROLL
    private ScrollCallbackHelper scrollCallbackHelper = new ScrollCallbackHelper();

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
            HEADER_ENDING_HEIGHT = headerHander.getEndingAnimHeight(header);
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
            FOOTER_ENDING_HEIGHT = footerHander.getEndingAnimHeight(footer);
        } else {
            if (footer != null) FOOTER_LIMIT_HEIGHT = footer.getMeasuredHeight();
            FOOTER_SPRING_HEIGHT = FOOTER_LIMIT_HEIGHT;
        }
        boolean needHeaderScroll = headerHander != null && judgeType(headerHander) == Type.SCROLL;
        boolean needFooterScroll = footerHander != null && judgeType(footerHander) == Type.SCROLL;
        if (needHeaderScroll || needFooterScroll) {
            //设置padding
            if (contentView instanceof ViewGroup) {
                int paddingTopAdd = needHeaderScroll ? HEADER_SPRING_HEIGHT : 0;
                int paddingBottomAdd = needFooterScroll ? FOOTER_SPRING_HEIGHT : 0;
                contentView.setPadding(0, paddingTopContent + paddingTopAdd, 0, paddingBottomContent + paddingBottomAdd);
                ((ViewGroup) contentView).setClipToPadding(false);
            } else {
                //TODO:
            }
        } else {
            contentView.setPadding(0, paddingTopContent, 0, paddingBottomContent);
            ((ViewGroup) contentView).setClipToPadding(false);
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

            //设置层级关系
            if (judgeType(headerHander) == Type.OVERLAP) {
                if (judgeType(footerHander) == Type.OVERLAP) {
                    //content:3 header: x footer x
                    contentLay.bringToFront();
                } else {
                    //content:2 header: 1 footer 3
                    if (header != null) header.bringToFront();
                    contentLay.bringToFront();
                    if (footer != null) footer.bringToFront();
                }
            } else {
                if (judgeType(footerHander) == Type.OVERLAP) {
                    //content:2 header: 3 footer 1
                    if (footer != null) footer.bringToFront();
                    contentLay.bringToFront();
                    if (header != null) header.bringToFront();
                } else {
                    //content:1 header: x footer x
                    if (header != null) header.bringToFront();
                    if (footer != null) footer.bringToFront();
                }
            }
            if (judgeType(headerHander) == Type.SCROLL || judgeType(footerHander) == Type.SCROLL) {
                //scroll模式在layout好之后就要刷新一下header/footer位置，因为scroll模式的header/footer默认就是展示的
                paddingScroll();
            }
        }
    }

    //记录单次滚动x,y轴偏移量
    private float dy;
    private float dx;
    //记录当前滚动事件是否需要SpringView进行处理，如果需要则SpringView拦截事件（比如已经滚动到顶部了还继续下拉）
    private boolean isNeedMyMove;

    private int downAction;
    private boolean isContentDown;

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        dealMulTouchEvent(event);
        int action = event.getAction();
        switch (action) {
            case MotionEvent.ACTION_DOWN: {
                flag.hasCallFull = false;
                flag.hasCallRefresh = false;
                flag.hasCallEnding = false;
                //手指拉动第一次的Y坐标
                float firstY = event.getY();
                isNeedMyMove = false;
                //判断当前down事件是否发生在content区域
                if (isTop() && firstY < -getScrollY()) {
                    isContentDown = false;
                } else {
                    isContentDown = true;
                }
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
                isNeedMyMove = isNeedMyMove(event);
                //如果内部控件既在顶部又在最底部，那说明该控件内容不足一屏，还不足以滚动
                boolean isNotFull = isTop && isBottom;
                //如果down事件是不是在content区域发生的（比如说header内），则不对这个事件进行跟踪转发
                if (isNeedMyMove && !isInControl && !isNotFull && isContentDown) {
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
                break;
            case MotionEvent.ACTION_MOVE:
                if (isNeedMyMove) {
                    //按下的时候关闭回弹
                    needResetAnim = false;
                    //下拉的时候显示header并隐藏footer，上拉的时候相反
                    if (isTop()) {
                        //TODO:
                        if (judgeType(footerHander) == Type.SCROLL) {
                            showHeaderAndFooter(true, true);
                        } else {
                            showHeaderAndFooter(true, false);
                        }
                    } else if (isBottom()) {
                        //TODO:
                        if (judgeType(headerHander) == Type.SCROLL) {
                            showHeaderAndFooter(true, true);
                        } else {
                            showHeaderAndFooter(false, true);
                        }
                    }
                    //回调callOnPreDrag接口
                    callbackOnPreDrag();
                    //执行位移操作
                    doMove();
                    //回调onLimitDes接口
                    callbackOnLimitDes();
                    isInControl = true;
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
            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP:
                needResetAnim = true;      //松开的时候打开回弹
                flag.hasCallPreDrag = false;
                restSmartPosition();
                dsY = 0;
                dy = 0;
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
                downAction = action;
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
                downAction = action;
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
        float movePara;
        if (dy > 0 && headerHander != null && headerHander.getMovePara() > 0) {
            movePara = headerHander.getMovePara();
        } else if (dy < 0 && footerHander != null && footerHander.getMovePara() > 0) {
            movePara = footerHander.getMovePara();
        } else {
            movePara = MOVE_PARA;
        }
        int movedy;
        if (dy > 0) {
            movedy = (int) (((MAX_HEADER_PULL_HEIGHT + getScrollY()) / (float) MAX_HEADER_PULL_HEIGHT) * dy / movePara);
        } else {
            movedy = (int) (((MAX_FOOTER_PULL_HEIGHT - getScrollY()) / (float) MAX_FOOTER_PULL_HEIGHT) * dy / movePara);
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
            callOnScrollAndDrag();
            invalidate();
        }
        //在滚动动画完全结束后回调接口
        //滚动回调过程中mScroller.isFinished会多次返回true，导致判断条件被多次进入，设置标志位保证只调用一次
        if (!isMoveNow && mScroller.isFinished()) {
            if (flag.scrollAnimType == 0) {
                if (!flag.hasCallFull) {
                    flag.hasCallFull = true;
                    callOnAfterFullAnim();
                }
            } else if (flag.scrollAnimType == 1) {
                if (!flag.hasCallRefresh) {
                    flag.hasCallRefresh = true;
                    callOnAfterRefreshAnim();
                }
            } else if (flag.scrollAnimType == 2) {
                if (!flag.hasCallEnding) {
                    flag.hasCallEnding = true;
                    callOnAfterEndingAnim();
                }
            }
        }
    }

    //springView滚动或者拖拽的时候会执行该方法
    private void callOnScrollAndDrag() {
        //回调onDropAnim接口
        callbackOnDropAnim();
        //回调onFinishAnim接口
        callbackOnFinishAnim();
        //获取type，优先使用header/footer的返回值
        boolean top = isTop();
        boolean bottom = isBottom();
        Type typeIn;
        if (top) {
            typeIn = judgeType(headerHander);
        } else if (bottom) {
            typeIn = judgeType(footerHander);
        } else {
            return;
        }
        //只在需要变更布局方式的时候才发起requestLayout()
        if (isChildScrollToBottom() && oldScrollY <= 0 && scrollY > 0) {
            //从下拉状态回到上拉时
            requestLayout();
        } else if (isChildScrollToTop() && oldScrollY >= 0 && scrollY < 0) {
            //从上拉状态回到下拉时
            requestLayout();
        }
        //更新位置
        if (typeIn == Type.OVERLAP) {
            if (header != null) header.setTranslationY(header.getHeight() + getScrollY());
            if (footer != null) footer.setTranslationY(-footer.getHeight() + getScrollY());
            //重置状态
            if (contentLay != null) contentLay.setTranslationY(0);
        } else if (typeIn == Type.DRAG) {
            if (contentLay != null) contentLay.setTranslationY(getScrollY());
            //重置状态
            if (header != null) header.setTranslationY(0);
            if (footer != null) footer.setTranslationY(0);
        } else if (typeIn == Type.FOLLOW || typeIn == Type.SCROLL) {
            //重置状态
            if (contentLay != null) contentLay.setTranslationY(0);
            if (header != null) header.setTranslationY(0);
            if (footer != null) footer.setTranslationY(0);
        }
        //TODO：只在onScrollChanged回调会有闪烁
        paddingScroll();
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldX, int oldY) {
        super.onScrollChanged(x, y, oldX, oldY);
        scrollY = y;
        oldScrollY = oldY;
        //重置位置
        if (y == 0) {
            if (contentLay != null) contentLay.setTranslationY(0);
            if (header != null) header.setTranslationY(0);
            if (footer != null) footer.setTranslationY(0);
        }
        paddingScroll();
    }

    //在完成全部动作后会执行此方法
    private void callOnAfterFullAnim() {
        //回调onResetAnim()
        if (flag.callFreshOrLoad == 1 || flag.callFreshOrLoad == 3) {
            if (headerHander != null) {
                if (flag.callHeaderAnimFlag == 2) {
                    headerHander.onResetAnim();
                    flag.callHeaderAnimFlag = 0;
                }
            }
        } else if (flag.callFreshOrLoad == 2 || flag.callFreshOrLoad == 4) {
            if (footerHander != null) {
                if (flag.callFooterAnimFlag == 2) {
                    footerHander.onResetAnim();
                    flag.callFooterAnimFlag = 0;
                }
            }
        }
        //回调onFinishDrag()
        if (flag.callFreshOrLoad == 1) {
            if (headerHander != null) headerHander.onFinishDrag(header);
            //如果在回弹动作中没有进行回调(说明是设置为不可弹动状态)，那么在这里进行(但是callFresh例外)
            if (give == Give.BOTTOM || give == Give.NONE && !isCallFresh) {
                listener.onRefresh();
            }
            isCallFresh = false;
        } else if (flag.callFreshOrLoad == 2) {
            if (footerHander != null) footerHander.onFinishDrag(footer);
            if (give == Give.TOP || give == Give.NONE) {
                listener.onLoadmore();
            }
        } else if (flag.callFreshOrLoad == 3) {
            if (headerHander != null) headerHander.onFinishDrag(header);
        } else if (flag.callFreshOrLoad == 4) {
            if (footerHander != null) footerHander.onFinishDrag(footer);
        }
        flag.callFreshOrLoad = 0;
        //动画完成后检查是否需要切换header/footer，是则切换
        if (_headerHander != null) {
            _setHeader(_headerHander);
            _headerHander = null;
        }
        if (_footerHander != null) {
            _setFooter(_footerHander);
            _footerHander = null;
        }
        //动画完成后检查是否需要切换type，是则切换
        if (needChangeType) {
            _setType(_type);
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

    //回调自定义header/footer onFinishAnim接口
    private void callbackOnFinishAnim() {
        if (getScrollY() < 0 && getScrollY() > -10 && headerHander != null)
            if (flag.callHeaderAnimFlag == 1) {
                headerHander.onFinishAnim();
                flag.callHeaderAnimFlag = 2;
            }
        if (getScrollY() > 0 && getScrollY() < 10 && footerHander != null)
            if (flag.callFooterAnimFlag == 1) {
                footerHander.onFinishAnim();
                flag.callFooterAnimFlag = 2;
            }
    }

    //回调自定义header/footer OnPreDrag接口
    private void callbackOnPreDrag() {
        if (!flag.hasCallPreDrag) {
            if (isTop()) {
                if (headerHander != null) headerHander.onPreDrag(header);
                flag.hasCallPreDrag = true;
            } else if (isBottom()) {
                if (footerHander != null) footerHander.onPreDrag(footer);
                flag.hasCallPreDrag = true;
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
            flag.callFreshOrLoad = 1;
            if (headerHander != null) {
                if (flag.callHeaderAnimFlag == 0 || flag.callHeaderAnimFlag == 2) {
                    headerHander.onStartAnim();
                    flag.callHeaderAnimFlag = 1;
                }
            }
        } else if (isBottom()) {
            flag.callFreshOrLoad = 2;
            if (footerHander != null) {
                if (flag.callFooterAnimFlag == 0 || flag.callFooterAnimFlag == 2) {
                    footerHander.onStartAnim();
                    flag.callFooterAnimFlag = 1;
                }
            }
        }
    }

    /**
     * 判断是否需要由该控件来控制滑动事件
     */
    @SuppressWarnings("all")
    private boolean isNeedMyMove(MotionEvent event) {
        if (contentLay == null) {
            return false;
        }
        //如果手指的拖拽范围在header内则不拦截（微信header等，需要把事件传递给header内部自行处理，SpringView不干涉）
        if (downAction == MotionEvent.ACTION_DOWN) {
            if (getScrollY() < 0 && event.getY() < -getScrollY()) {
                return false;
            }
        }
        //横向拖拽距离大于竖直距离则不拦截（侧滑删除）
        if (Math.abs(dy) <= Math.abs(dx)) {
            return false;
        }
        boolean isNowTop = isChildScrollToTop();
        boolean isNowBottom = isChildScrollToBottom();
        //用户禁止了下拉操作，则不控制
        if (!enableHeader && isNowTop && dy > 0) {
            return false;
        }
        //用户禁止了上拉操作，则不控制
        if (!enableFooter && isNowBottom && dy < 0) {
            return false;
        }
        if (header != null) {
            //其中的20是一个防止触摸误差的偏移量
            if (isNowTop) {
                if (judgeType(headerHander) != Type.SCROLL) {
                    if (dy > 0 || getScrollY() < 0 - 20) {
                        return true;
                    }
                }
            }
        }
        if (footer != null) {
            if (isNowBottom) {
                if (judgeType(footerHander) != Type.SCROLL) {
                    if (dy < 0 || getScrollY() > 0 + 20) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * 重置控件位置到初始状态
     */
    private void resetPosition() {
        flag.scrollAnimType = 0;
        isInControl = false;    //重置位置的时候，滑动事件已经不在控件的控制中了
        mScroller.startScroll(0, getScrollY(), 0, -getScrollY(), MOVE_TIME);
        invalidate();
    }

    /**
     * 重置控件位置到刷新状态（或加载状态）
     */
    private void resetRefreshPosition() {
        flag.scrollAnimType = 1;
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
        flag.scrollAnimType = 2;
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
            if (isTop()) {
                //顶部被拉动
                if (isTopOverLimit()) {
                    //顶部拉动超过limit位置
                    callbackOnStartAnim();
                    if (give == Give.BOTH || give == Give.TOP)
                        resetRefreshPosition();
                    else
                        resetPosition();
                } else {
                    //顶部拉动未超过limit位置
                    flag.callFreshOrLoad = 3;
                    resetPosition();
                }
            } else if (isBottom()) {
                //底部被拉动
                if (isBottomOverLimit()) {
                    //底部拉动超过limit位置
                    callbackOnStartAnim();
                    if (give == Give.BOTH || give == Give.BOTTOM)
                        resetRefreshPosition();
                    else
                        resetPosition();
                } else {
                    //底部拉动未超过limit位置
                    flag.callFreshOrLoad = 4;
                    resetPosition();
                }
            }
        }
    }

    /**
     * {@link #callFresh()}的执行方法，不要暴露在外部
     */
    private void _callFresh() {
        isCallFresh = true;
        flag.scrollAnimType = 1;     //不是全部回弹动画（半回弹到limit位置）
        needResetAnim = true;   //允许执行回弹动画
        flag.hasCallRefresh = false;
        flag.hasCallFull = false;
        flag.callFreshOrLoad = 1;
        if (headerHander != null) {
            headerHander.onPreDrag(header);
            headerHander.onStartAnim();
        }
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

    private Type judgeType(DragHander dragHander) {
        if (dragHander == null) {
            return null;
        } else if (dragHander.getType() != null) {
            return dragHander.getType();
        } else if (type != null) {
            return type;
        } else {
            return Type.FOLLOW;
        }
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
     * 判断当前状态是否正在拉动顶部
     */
    private boolean isTop() {
        return getScrollY() < 0;
    }

    /**
     * 判断当前状态是否正在拉动底部
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
    private void _setType(Type type) {
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
        if (judgeType(headerHander) != Type.SCROLL) {
            _callFresh();
        }
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

    //过时，不再使用double类型了，请调用下面的重载方法
    @Deprecated
    public void setMovePara(double movePara) {
        setMovePara((float) movePara);
    }

    public void setMovePara(float movePara) {
        //移动参数：计算手指移动量的时候会用到这个值，值越大，移动量越小，若值为1则手指移动多少就滑动多少px (默认为2，建议在1-2之间)
        //如果 header 或者 footer 实现了 getMovePara() 接口，则会优先使用接口提供的值，建议在自定义header/footer中设置移动参数，而不是通过该方法
        //自1.6.0起，header 和 footer 的移动参数相互独立，不再是一个值同时决定两头的移动参数了
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
            _setType(type);
        }
    }

    /**
     * 获取当前样式
     */
    public Type getType() {
        return type;
    }

    public View getContentLay() {
        return contentLay;
    }

    public View getContentView() {
        return contentView;
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

    @SuppressWarnings("unchecked")
    public <TH extends DragHander> TH getHeader(Class<TH> c) {
        return (TH) headerHander;
    }

    @SuppressWarnings("unchecked")
    public <TF extends DragHander> TF getFooter(Class<TF> c) {
        return (TF) footerHander;
    }

    public void setHeader(DragHander headerHander) {
        if (this.headerHander != null && isTop()) {
            _headerHander = headerHander;
            resetPosition();
        } else {
            _setHeader(headerHander);
        }
    }

    /**
     * {@link #setHeader(DragHander)} 的执行方法，不要暴露在外部
     */
    private void _setHeader(DragHander headerHander) {
        this.headerHander = headerHander;
        if (header != null) {
            removeView(this.header);
        }
        View tempView = headerHander.getView(inflater, this);
        //建议在自定义Header中getView方法中调用inflate(R.layout.header_view, viewGroup, false);时，第三个参数'attachToRoot'传false
        //这里进行这个判断是为了兼容，无论attachToRoot用的什么参数都能够正常运行
        //_setFooter(.)同理
        if (tempView instanceof SpringView) {
            //如果tempView就是SpringView，则说明自定义Header的getView方法中inflate传入了true，此时header已经被添加到SpringView中了，无需addView
            //获取最后一个view，即是header view
            this.header = getChildAt(getChildCount() - 1);
        } else {
            //否则，则说明getView方法中inflate传入的是false，需要在这里添加
            //此时tempView即是header view
            addView(tempView);
            this.header = tempView;
        }
        requestLayout();
    }

    public void setFooter(DragHander footerHander) {
        if (this.footerHander != null && isBottom()) {
            _footerHander = footerHander;
            resetPosition();
        } else {
            _setFooter(footerHander);
        }
    }

    /**
     * {@link #setFooter(DragHander)} 的执行方法，不要暴露在外部
     */
    private void _setFooter(DragHander footerHander) {
        this.footerHander = footerHander;
        if (footer != null) {
            removeView(footer);
        }
        View tempView = footerHander.getView(inflater, this);
        //同_setHeader(.),注释详见_setHeader(.)方法
        if (tempView instanceof SpringView) {
            this.footer = getChildAt(getChildCount() - 1);
        } else {
            addView(tempView);
            this.footer = tempView;
        }
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

        float getMovePara();

        Type getType();

        /**
         * 即将开始拖拽时的回调，可进行初始化操作
         */
        void onPreDrag(View rootView);

        /**
         * 拖拽结束时回调，不管是否拖拽超过limit阈值
         */
        void onFinishDrag(View rootView);

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
         * 开始动画回调：拉动超过临界点后松开时回调
         * 和{@link #onFinishAnim()}总是成对调用
         */
        void onStartAnim();

        /**
         * 结束动画回调：头(尾)即将全部弹回时回调
         * 和{@link #onStartAnim()}总是成对调用
         */
        void onFinishAnim();

        /**
         * 头(尾)弹回已经彻底结束时回调
         * onStartAnim在有效回弹开始时回调，onFinishAnim会在有效回弹结束时回调，但此时回弹动画并未完全完成
         * 如果此项继续下拉则会继续触发onStartAnim -> onFinishAnim，而onResetAnim则会在回弹动画彻底结束后回调，可以在这个回调中进行释放动画资源等操作
         * 增加这个接口主要是为了处理用户不断上下拉动疯狂拖拽header/footer时的手势兼容。
         */
        void onResetAnim();

        //###########################################
        //########### 收场动画相关接口 ##############
        //###########################################

        /**
         * 收场动画执行时间
         */
        int getEndingAnimTime();

        /**
         * 收场动画回弹高度
         */
        int getEndingAnimHeight(View rootView);

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
