package com.liaoinstan.springview.weixinheaderv2;

import android.animation.ValueAnimator;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SimpleItemAnimator;

import com.liaoinstan.springview.container.BaseHeader;
import com.liaoinstan.springview.utils.DensityUtil;
import com.liaoinstan.springview.weixinheader.Program;
import com.liaoinstan.springview.weixinheader.R;
import com.liaoinstan.springview.widget.SpringView;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liaoinstan on 2019/11/06.
 * 仿微信小程序header V2 ，微信更新后小程序交互界面和之前老版本区别很大，所以这里再次模仿了一个2.0版本
 */
public class WeixinHeaderV2 extends BaseHeader implements DragItemTouchCallback.OnDragListener {

    //根布局
    private View root;
    //外部控件
    private View bottomView;
    private WeixinTitleBar weixinTitleBar;
    private View contentView;
    private View contentLay;
    private SpringView springViewOut;
    //内部控件
    private SmokeView smokeView;
    private SpringView springViewInner;
    private RecyclerView recycler;
    private RecycleAdapterWeixinHeaderV2 adapter;
    private ImageView img_dot1;
    private ImageView img_dot2;
    private ImageView img_dot3;
    private ViewGroup lay_dot;
    private ViewGroup lay_bg_content;
    private TextView text_title;
    private View bgView;

    //中小圆点最大宽度
    private int dotWidth = DensityUtil.dp2px(12);
    //两边小圆点宽度
    private int dotWidthSide = DensityUtil.dp2px(6.5f);
    //小圆点间距
    private int dotSpace = DensityUtil.dp2px(8);
    //记录拖拽是否超过弹动高度
    private boolean hasOverSpringHeight;
    //初始化数据
    private List<Program> results = new ArrayList<Program>() {{
        add(new Program("最近使用", RecycleAdapterWeixinHeaderV2.TYPE_HEADER));
        add(new Program(RecycleAdapterWeixinHeaderV2.TYPE_MORE));   //更多(...)按钮
        add(new Program("我的小程序", RecycleAdapterWeixinHeaderV2.TYPE_HEADER));
    }};
    private OnMoreClickListener onMoreClickListener;
    private OnSearchClickListener onSearchClickListener;
    private OnProgramClickListener onProgramClickListener;
    private OnProgramDropListener onProgramDropListener;
    private OnWeixinHeaderLoadImgCallback imgLoadCallback;

    public WeixinHeaderV2(View bottomView, WeixinTitleBar weixinTitleBar) {
        this.bottomView = bottomView;
        this.weixinTitleBar = weixinTitleBar;
    }

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        root = inflater.inflate(R.layout.weixin_header_v2, viewGroup, false);
        springViewInner = root.findViewById(R.id.spring_inner);
        recycler = root.findViewById(R.id.recycler);
        img_dot1 = root.findViewById(R.id.img_dot1);
        img_dot2 = root.findViewById(R.id.img_dot2);
        img_dot3 = root.findViewById(R.id.img_dot3);
        lay_dot = root.findViewById(R.id.lay_dot);
        lay_bg_content = root.findViewById(R.id.lay_bg_content);
        bgView = root.findViewById(R.id.bg_view);
        text_title = root.findViewById(R.id.text_title);
        smokeView = root.findViewById(R.id.smoke_view);

        //微信header V2 内部的弹动效果也是使用SpringView实现的，springViewInner就是header内部的SpringView
        //SpringView支持复杂嵌套，手势事件不会产生冲突，其本身只是一个轻量ViewGroup，和普通Layout布局一样简单使用
        //初始化内部springView
        springViewInner.setGive(SpringView.Give.TOP);
        springViewInner.setHeader(new WeixinV2InnerHeader(new WeixinV2InnerHeader.OnSearchClickListener() {
            @Override
            public void onSearchClick(View view) {
                if (onSearchClickListener != null) onSearchClickListener.onSearchClick();
            }
        }));
        springViewInner.setFooter(new WeixinV2InnerFooter(new WeixinV2InnerFooter.OnDragFinishListener() {
            @Override
            public void onDragFinish() {
                SpringView springView = (SpringView) root.getParent();
                springView.onFinishFreshAndLoad();
            }
        }));
        springViewInner.setListener(new SpringView.OnFreshListener() {
            @Override
            public void onRefresh() {
            }

            @Override
            public void onLoadmore() {
            }
        });

        //初始化recyclerView
        adapter = new RecycleAdapterWeixinHeaderV2(this);
        recycler.setLayoutManager(new GridLayoutManager(root.getContext(), 4, GridLayoutManager.VERTICAL, false));
        recycler.setAdapter(adapter);
        RecyclerView.ItemAnimator itemAnimator = recycler.getItemAnimator();

        //禁用默认刷新动画（否则调用"notifyItemChanged"等方法会有渐变闪烁效果，这里不希望有这种效果）
        if (itemAnimator instanceof SimpleItemAnimator)
            ((SimpleItemAnimator) itemAnimator).setSupportsChangeAnimations(false);

        //拖拽移动，使用ItemTouchHelper实现，拖拽逻辑在"DragItemTouchCallback"中
        final ItemTouchHelper itemTouchHelper = new ItemTouchHelper(new DragItemTouchCallback(adapter, weixinTitleBar != null ? weixinTitleBar.getDeleteView() : null).setOnDragListener(this));
        itemTouchHelper.attachToRecyclerView(recycler);
        adapter.setOnRecyclerLongClickListener(new RecycleAdapterWeixinHeaderV2.OnRecyclerLongClickListener() {
            @Override
            public void onLongClick(RecyclerView.ViewHolder viewHolder) {
                if (viewHolder instanceof RecycleAdapterWeixinHeaderV2.HolderItem) {
                    itemTouchHelper.startDrag(viewHolder);
                    Utils.vibrate(recycler.getContext(), 10);   //震动10ms
                }
            }
        });
        adapter.notifyDataSetChanged();

        //点击标题栏收起header
        if (weixinTitleBar != null) {
            weixinTitleBar.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (springViewOut != null) springViewOut.onFinishFreshAndLoad();
                }
            });
        }
        return root;
    }

    @Override
    public float getMovePara() {
        //header比较长，移动参数设小一点比较合适，这里用1.4感觉和微信手感差不多
        return 1.25f;
    }

    @Override
    public int getDragLimitHeight(View rootView) {
        //下拉阈值设置为固定200dp，和微信基本差不多
        return DensityUtil.dp2px(190);
    }

    @Override
    public int getDragMaxHeight(View rootView) {
        //同时设置header的高度为弹动高度
        springViewOut = (SpringView) rootView.getParent();
        //保存内容布局的引用
        contentLay = springViewOut.getContentLay();
        contentView = springViewOut.getContentView();
        ViewGroup.LayoutParams layoutParams = rootView.getLayoutParams();
        layoutParams.height = springViewOut.getMeasuredHeight();
        rootView.setLayoutParams(layoutParams);
        //最大高度和弹动高度一致
        return getDragSpringHeight(rootView);
    }

    @Override
    public int getDragSpringHeight(View rootView) {
        springViewOut = (SpringView) rootView.getParent();
        if (weixinTitleBar != null) {
            return springViewOut.getMeasuredHeight() - weixinTitleBar.getTitleViewHeight();
        } else {
            return springViewOut.getMeasuredHeight();
        }
    }

    /**
     * 根据下拉的距离不断变化，进行动画交互
     * 包括3个小圆点的动画，背景渐变，内容缩放位移，透明度变化等
     */
    @Override
    public void onDropAnim(View rootView, int dy) {
        int dragSpringHeight = getDragSpringHeight(rootView);
        int dragLimiteHeight = getDragLimitHeight(rootView);
        if (dy >= dragSpringHeight) hasOverSpringHeight = true;
        //计算小圆点居中Y轴偏移量
        int dotDY = rootView.getMeasuredHeight() - dy / 2 - lay_dot.getHeight() / 2;
        //小圆点的一系列位移渐变缩放等动画
        if (dy < DensityUtil.dp2px(50)) {
            //前50dp作为偏移量，不做任何处理
        } else if (dy < dragLimiteHeight / 2) {
            float lv = (dy - (float) DensityUtil.dp2px(50)) / ((float) dragLimiteHeight / 2 - DensityUtil.dp2px(50)); //0-1
            int nowWidth = (int) (lv * dotWidth);
            int nowWidthSide = (int) (lv * dotWidthSide);
            //小圆点不断变大
            ViewGroup.LayoutParams layoutParam1 = img_dot1.getLayoutParams();
            layoutParam1.height = nowWidthSide;
            layoutParam1.width = nowWidthSide;
            img_dot1.setLayoutParams(layoutParam1);
            ViewGroup.LayoutParams layoutParam2 = img_dot2.getLayoutParams();
            layoutParam2.height = nowWidth;
            layoutParam2.width = nowWidth;
            img_dot2.setLayoutParams(layoutParam2);
            ViewGroup.LayoutParams layoutParam3 = img_dot3.getLayoutParams();
            layoutParam3.height = nowWidthSide;
            layoutParam3.width = nowWidthSide;
            img_dot3.setLayoutParams(layoutParam3);
            lay_dot.setTranslationY(-dotDY + DensityUtil.dp2px(25));
            img_dot1.setTranslationX(0);
            img_dot3.setTranslationX(0);
        } else if (dy < dragLimiteHeight) {
            //小圆点逐渐分开
            float lv = (dy - (float) dragLimiteHeight / 2) / ((float) dragLimiteHeight / 2); //0-1
            int nowSpace = (int) (lv * (dotSpace + dotWidth));
            img_dot1.setTranslationX(-nowSpace);
            img_dot3.setTranslationX(nowSpace);
            //中间的小圆点不断变小
            int width2 = (int) (dotWidth - (dotWidth - dotWidthSide) * lv);
            ViewGroup.LayoutParams layoutParam2 = img_dot2.getLayoutParams();
            layoutParam2.height = width2;
            layoutParam2.width = width2;
            img_dot2.setLayoutParams(layoutParam2);
            lay_dot.setTranslationY(-dotDY + DensityUtil.dp2px(25));
        } else if (dy < dragLimiteHeight + DensityUtil.dp2px(100)) {
            //分开后小圆点不断透明消失
            float lv = (dy - (float) dragLimiteHeight) / (float) DensityUtil.dp2px(100); //0-1
            float alpha = 1 - lv;
            lay_dot.setAlpha(alpha);
            lay_dot.setTranslationY(-dotDY + DensityUtil.dp2px(25));
        } else if (dy >= dragLimiteHeight + DensityUtil.dp2px(100)) {
            lay_dot.setAlpha(0f);
            lay_dot.setTranslationY(-dotDY + DensityUtil.dp2px(25));
        }
        //背景渐变，内容缩放和平移
        float MIN_SCALE = 0.8f;
        //计算内容Y轴位移距离（保持顶部）
        float springMoveHeight = springViewInner.getHeight() * (1 - MIN_SCALE) / 2;
        if (dy < dragLimiteHeight) {
            lay_bg_content.setAlpha(0);
            lay_bg_content.setTranslationY(0);
            if (weixinTitleBar != null) weixinTitleBar.setTitleBgAlpha(1);
            if (contentView != null) contentView.setAlpha(1);
            if (!hasOverSpringHeight) {
                springViewInner.setScaleX(MIN_SCALE);
                springViewInner.setScaleY(MIN_SCALE);
                text_title.setScaleX(MIN_SCALE);
                text_title.setScaleY(MIN_SCALE);
                springViewInner.setTranslationY(-springMoveHeight);
            }
        } else if (dy < dragSpringHeight) {
            //计算并背景透明度
            float lv = (dy - (float) dragLimiteHeight) / ((float) dragSpringHeight - dragLimiteHeight); //0-1
            lay_bg_content.setAlpha(lv);
            if (contentView != null) contentView.setAlpha(1 - 0.5f * lv); //1-0.5
            if (weixinTitleBar != null) weixinTitleBar.setTitleBgAlpha(0.5f * lv);  //0-0.5
            //计算内容缩放比例
            float scale = MIN_SCALE + (1 - MIN_SCALE) * lv; //0.7-1
            if (!hasOverSpringHeight) {
                text_title.setScaleX(scale);
                text_title.setScaleY(scale);
                springViewInner.setScaleX(scale);
                springViewInner.setScaleY(scale);
                springViewInner.setTranslationY(-springMoveHeight * (1 - lv));
            } else {
                lay_bg_content.setTranslationY(dy - dragSpringHeight);
            }
        } else {
            lay_bg_content.setAlpha(1);
            if (weixinTitleBar != null) weixinTitleBar.setTitleBgAlpha(0.5f);
            if (contentView != null) contentView.setAlpha(0f);
            text_title.setScaleX(1);
            text_title.setScaleY(1);
            springViewInner.setScaleX(1);
            springViewInner.setScaleY(1);
            springViewInner.setTranslationY(0);
            lay_bg_content.setTranslationY(0);
        }
    }

    @Override
    public void onLimitDes(View rootView, boolean upORdown) {
        //下拉超过临界高度时震动一下
        if (!hasOverSpringHeight && !upORdown) {
            Utils.vibrate(rootView.getContext(), 10);
        }
    }

    @Override
    public void onPreDrag(View rootView) {
        //拖拽开始前，开始绘制烟雾效果
        smokeView.startAnim();
    }

    @Override
    public void onFinishDrag(View rootView) {
        //header关闭后，停止烟雾效果的绘制，节省性能开支
        smokeView.stopAnim();
    }

    @Override
    public void onStartAnim() {
        //动画：收起隐藏底部导航条
        startNavAnim(false);
    }

    @Override
    public void onFinishAnim() {
        //动画：展开显示底部导航条
        startNavAnim(true);
        reset();
    }

    //重置状态
    private void reset() {
        hasOverSpringHeight = false;
        img_dot1.setTranslationX(0);
        img_dot3.setTranslationX(0);
        lay_dot.setTranslationY(0);
        lay_dot.setAlpha(1f);
    }

    //开始拖拽item的回调
    @Override
    public void onStartDrag() {
        startHeaderDelAnim(true);
    }

    //结束拖拽item的回调
    @Override
    public void onFinishDrag() {
        startHeaderDelAnim(false);
        adapter.notifyItemChanged(Utils.getMineTitlePosition(results));
    }

    //删除回调（拖拽到了'删除按钮'时）
    @Override
    public boolean onDrop(RecyclerView.ViewHolder viewHolder, int position) {
        if (onProgramDropListener != null) {
            //如果设置了外部回调接口，先回调外部接口，返回true则执行删除，否则不执行
            boolean needDrop = onProgramDropListener.onDrop(results.get(position), viewHolder, position);
            if (needDrop) removeItem(position);
            return needDrop;
        } else {
            //如果没设置外部回调接口，直接执行删除
            removeItem(position);
            return true;
        }
    }

    //###################### 对外公开的方法（添加、移除小程序） #######################

    //添加小程序到'最近使用'
    public void addItemRecent(List<Program> programs) {
        int position = Utils.getMoreItemIndex(results);
        if (position != -1) {
            results.addAll(position, programs);
            if (adapter != null) adapter.notifyDataSetChanged();
        }
    }

    //添加小程序到'最近使用'
    public void addItemRecent(Program program) {
        int position = Utils.getMoreItemIndex(results);
        if (position != -1) {
            results.add(position, program);
            if (adapter != null) {
                adapter.notifyItemInserted(position);
            }
        }
    }

    //添加小程序到'我的小程序'
    public void addItemMine(Program program) {
        results.add(program);
        if (adapter != null) {
            adapter.notifyItemInserted(results.size());
        }
    }

    //添加小程序到'我的小程序'
    public void addItemMine(List<Program> programs) {
        results.addAll(programs);
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    //删除指定位置的小程序
    public void removeItem(int position) {
        if (results.size() > 0) {
            results.remove(position);
            if (adapter != null) {
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, results.size() - position);
                if (!Utils.hasMineProgram(results)) {
                    adapter.notifyItemChanged(Utils.getMineTitlePosition(results));
                }
            }
        }
    }

    //###################### 动画 #######################

    //底部导航条，展开收起动画
    private void startNavAnim(boolean showOrHide) {
        if (bottomView == null) return;
        if (showOrHide && bottomView.getTranslationY() == 0) return;
        if (!showOrHide && bottomView.getTranslationY() == bottomView.getHeight()) return;
        ValueAnimator anim = ValueAnimator.ofFloat(showOrHide ? bottomView.getHeight() : 0f, showOrHide ? 0f : bottomView.getHeight());
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(200);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                bottomView.setTranslationY(value);
            }
        });
        anim.start();
    }

    //"拖到这里删除"按钮，展开收起动画
    private void startHeaderDelAnim(boolean showOrHide) {
        if (contentLay == null) return;
        if (weixinTitleBar == null) return;
        ValueAnimator anim = ValueAnimator.ofInt(showOrHide ? 0 : weixinTitleBar.getDeleteViewHeight(), showOrHide ? weixinTitleBar.getDeleteViewHeight() : 0);   //show:0-height,hide:height-0
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Integer value = (Integer) animation.getAnimatedValue();
                contentLay.setTranslationY(-value);
                //逐渐展开删除按钮
                weixinTitleBar.setExtraHeight(value);
                bgView.setTranslationY(weixinTitleBar.getDeleteViewHeight() - value);
            }
        });
        anim.start();
    }

    //###################### 外部接口 #######################

    public interface OnWeixinHeaderLoadImgCallback {
        void loadImg(ImageView imageView, String imgUrl, int position);
    }

    public interface OnMoreClickListener {
        void onMoreClick();
    }

    public interface OnSearchClickListener {
        void onSearchClick();
    }

    public interface OnProgramClickListener {
        void onClick(Program program, RecyclerView.ViewHolder holder, int position);
    }

    public interface OnProgramDropListener {
        //拖拽删除，回调接口，如果返回true则执行删除，false不会删除
        boolean onDrop(Program program, RecyclerView.ViewHolder holder, int position);
    }

    //#################### get & set #####################

    public List<Program> getResults() {
        return results;
    }

    public OnSearchClickListener getOnSearchClickListener() {
        return onSearchClickListener;
    }

    public OnMoreClickListener getOnMoreClickListener() {
        return onMoreClickListener;
    }

    public OnProgramClickListener getOnProgramClickListener() {
        return onProgramClickListener;
    }

    public OnProgramDropListener getOnProgramDropListener() {
        return onProgramDropListener;
    }

    public OnWeixinHeaderLoadImgCallback getImgLoadCallback() {
        return imgLoadCallback;
    }

    public void setOnSearchClickListener(OnSearchClickListener onSearchClickListener) {
        this.onSearchClickListener = onSearchClickListener;
    }

    public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener) {
        this.onMoreClickListener = onMoreClickListener;
    }

    public void setOnProgramClickListener(OnProgramClickListener onProgramClickListener) {
        this.onProgramClickListener = onProgramClickListener;
    }

    public void setOnProgramDropListener(OnProgramDropListener onProgramDropListener) {
        this.onProgramDropListener = onProgramDropListener;
    }

    public void setOnLoadImgCallback(OnWeixinHeaderLoadImgCallback imgLoadCallback) {
        this.imgLoadCallback = imgLoadCallback;
    }
}
