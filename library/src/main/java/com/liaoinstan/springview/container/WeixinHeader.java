package com.liaoinstan.springview.container;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Service;
import android.content.pm.PackageManager;
import android.os.Vibrator;
import android.support.v4.content.PermissionChecker;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.ImageView;

import com.liaoinstan.springview.R;
import com.liaoinstan.springview.ctrl.Program;
import com.liaoinstan.springview.ctrl.RecycleAdapterWeixinHeader;
import com.liaoinstan.springview.ctrl.StartLinearSnapHelper;
import com.liaoinstan.springview.utils.DensityUtil;

import java.util.ArrayList;
import java.util.List;


/**
 * Created by liaoinstan on 2018/6/12.
 */
public class WeixinHeader extends BaseHeader {

    private View root;
    private RecyclerView recycler;
    private RecycleAdapterWeixinHeader adapter;
    private ImageView img_dot1;
    private ImageView img_dot2;
    private ImageView img_dot3;
    private ViewGroup lay_dot;

    //中小圆点最大宽度
    private int dotWidth = DensityUtil.dp2px(12);
    //两边小圆点宽度
    private int dotWidthSide = DensityUtil.dp2px(6.5f);
    //小圆点间距
    private int dotSpace = DensityUtil.dp2px(8);
    //一行最多现实多少个item
    private int maxCountLine = 4;
    //记录小圆点最大位移距离
    private float dotMaxTranY;
    //记录拖拽是否超过弹动高度
    private boolean hasOverSpringHeight;

    private List<Program> results = new ArrayList<>();
    private OnMoreClickListener onMoreClickListener;
    private OnProgramClickListener onProgramClickListener;
    private OnProgramLongClickListener onProgramLongClickListener;
    private OnWeixinHeaderLoadImgCallback imgLoadCallback;

    @Override
    public View getView(LayoutInflater inflater, ViewGroup viewGroup) {
        root = inflater.inflate(R.layout.weixin_header, viewGroup, true);
        recycler = root.findViewById(R.id.recycler);
        img_dot1 = root.findViewById(R.id.img_dot1);
        img_dot2 = root.findViewById(R.id.img_dot2);
        img_dot3 = root.findViewById(R.id.img_dot3);
        lay_dot = root.findViewById(R.id.lay_dot);

        adapter = new RecycleAdapterWeixinHeader(this);
        recycler.setLayoutManager(new LinearLayoutManager(root.getContext(), LinearLayoutManager.HORIZONTAL, false));
        recycler.setAdapter(adapter);

        StartLinearSnapHelper snapHelper = new StartLinearSnapHelper();
        snapHelper.attachToRecyclerView(recycler);

        if (results.size() == 0) {
            results.add(new Program());
        }
        adapter.notifyDataSetChanged();

        //添加布局监听，获取父容器宽度并设置item宽度，从而达到一行item平铺效果
        recycler.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                adapter.setItemWidth(recycler.getWidth() / maxCountLine);
                adapter.notifyDataSetChanged();
                recycler.getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });

        return root;
    }

    public void freshItem(List<Program> results) {
        this.results.clear();
        this.results.addAll(results);
        //最后增加一条，因为最后一条是“更多”
        this.results.add(new Program());
        if (adapter != null) {
            adapter.notifyDataSetChanged();
        }
    }

    //添加小程序（添加到末尾，'更多'前面）
    public void addItem(Program program) {
        if (results.size() > 0) {
            int position = results.size() - 1;
            results.add(position, program);
            if (adapter != null) {
                if (results.size() <= 4) {
                    adapter.notifyItemInserted(position);
                    adapter.notifyItemRangeChanged(position, results.size() - position);
                } else {
                    adapter.notifyDataSetChanged();
                    recycler.smoothScrollToPosition(results.size() - 1);
                }
            }
        }
    }

    //删除指定位置的小程序
    public void removeItem(int position) {
        if (results.size() > 0) {
            results.remove(position);
            if (adapter != null) {
                adapter.notifyItemRemoved(position);
                adapter.notifyItemRangeChanged(position, results.size() - position);
            }
        }
    }

    @Override
    public int getDragLimitHeight(View rootView) {
        return DensityUtil.dp2px(100);
    }

    @Override
    public int getDragMaxHeight(View rootView) {
        return rootView.getMeasuredHeight();
    }

    @Override
    public int getDragSpringHeight(View rootView) {
        return rootView.getMeasuredHeight() - DensityUtil.dp2px(200);
    }

    @Override
    public void onPreDrag(View rootView) {
    }

    /**
     * 根据下拉的距离不断变化，进行动画交互
     * 主要是3个小圆点的动画
     */
    @Override
    public void onDropAnim(View rootView, int dy) {
        int dragSpringHeight = getDragSpringHeight(rootView);
        if (dy >= dragSpringHeight) hasOverSpringHeight = true;
        if (dy <= 20) reset();
        if (dy > dragSpringHeight) {
            //如果拖拽超过停顿位置，则给内容设置位移系数保持居中
            int offset = dy - dragSpringHeight;
            recycler.setTranslationY(-offset / 2);
        } else {
            //如果拖拽未超过停顿位置，则执行交互动画
            if (!hasOverSpringHeight) {
                //使内容从上向下位移
                recycler.setTranslationY(dy - dragSpringHeight);
                //小圆点的一系列位移渐变缩放等动画
                if (dy < dragSpringHeight / 2) {
                    float lv = dy / ((float) dragSpringHeight / 2); //0-1
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
                    //小圆点居中
                    lay_dot.setTranslationY(-(dy / 2 - nowWidth / 2));
                    //记录下小圆点最大位移距离
                    if (Math.abs(lay_dot.getTranslationY()) > dotMaxTranY) {
                        dotMaxTranY = Math.abs(lay_dot.getTranslationY());
                    }
                } else if (dy < dragSpringHeight * 2 / 3) {
                    //小圆点逐渐分开
                    float lv = (dy - (float) dragSpringHeight / 2) / ((float) dragSpringHeight * 2 / 3 - (float) dragSpringHeight / 2); //0-1
                    int nowSpace = (int) (lv * (dotSpace + dotWidth));
                    img_dot1.setTranslationX(-nowSpace);
                    img_dot3.setTranslationX(nowSpace);
                    //中间的小圆点不断变小
                    int width2 = (int) (dotWidth - (dotWidth - dotWidthSide) * lv);
                    ViewGroup.LayoutParams layoutParam2 = img_dot2.getLayoutParams();
                    layoutParam2.height = width2;
                    layoutParam2.width = width2;
                    img_dot2.setLayoutParams(layoutParam2);
                } else if (dy < dragSpringHeight) {
                    //分开后小圆点不断向下位移并透明消失
                    float lv = (dy - (float) dragSpringHeight * 2 / 3) / ((float) dragSpringHeight - (float) dragSpringHeight * 2 / 3); //0-1
                    float alpha = 1 - lv;
                    float nowSpace = dotMaxTranY * (1 - lv);
                    lay_dot.setTranslationY(-nowSpace);
                    lay_dot.setAlpha(alpha);
                }
            }
        }
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onLimitDes(View rootView, boolean upORdown) {
        //下拉超过临界高度时如果有震动权限就震动一下
        if (!hasOverSpringHeight && !upORdown && PermissionChecker.checkSelfPermission(rootView.getContext(), Manifest.permission.VIBRATE) == PackageManager.PERMISSION_GRANTED) {
            Vibrator vib = (Vibrator) rootView.getContext().getSystemService(Service.VIBRATOR_SERVICE);
            vib.vibrate(35);
        }
    }

    @Override
    public void onStartAnim() {
    }

    @Override
    public void onFinishAnim() {
        reset();
    }

    private void reset() {
        hasOverSpringHeight = false;
        img_dot1.setTranslationX(0);
        img_dot3.setTranslationX(0);
        lay_dot.setTranslationY(0);
        lay_dot.setAlpha(1f);
    }

    //###################### 外部接口 #######################

    public interface OnWeixinHeaderLoadImgCallback {
        void loadImg(ImageView imageView, String imgUrl, int position);
    }

    public interface OnMoreClickListener {
        void onMoreClick();
    }

    public interface OnProgramClickListener {
        void onClick(Program program, RecyclerView.ViewHolder holder, int position);
    }

    public interface OnProgramLongClickListener {
        void onLongClick(Program program, RecyclerView.ViewHolder holder, int position);
    }

    //#################### get & set #####################

    public List<Program> getResults() {
        return results;
    }

    public void setOnMoreClickListener(OnMoreClickListener onMoreClickListener) {
        this.onMoreClickListener = onMoreClickListener;
    }

    public void setOnProgramClickListener(OnProgramClickListener onProgramClickListener) {
        this.onProgramClickListener = onProgramClickListener;
    }

    public void setOnProgramLongClickListener(OnProgramLongClickListener onProgramLongClickListener) {
        this.onProgramLongClickListener = onProgramLongClickListener;
    }

    public void setOnLoadImgCallback(OnWeixinHeaderLoadImgCallback imgLoadCallback) {
        this.imgLoadCallback = imgLoadCallback;
    }

    public OnMoreClickListener getOnMoreClickListener() {
        return onMoreClickListener;
    }

    public OnProgramClickListener getOnProgramClickListener() {
        return onProgramClickListener;
    }

    public OnProgramLongClickListener getOnProgramLongClickListener() {
        return onProgramLongClickListener;
    }

    public OnWeixinHeaderLoadImgCallback getImgLoadCallback() {
        return imgLoadCallback;
    }
}
