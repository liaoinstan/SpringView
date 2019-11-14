package com.liaoinstan.springview.weixinheaderv2;

import android.animation.ValueAnimator;
import android.graphics.Canvas;
import android.os.Handler;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.liaoinstan.springview.weixinheader.R;

import java.util.Collections;
import java.util.List;

import static com.liaoinstan.springview.weixinheaderv2.RecycleAdapterWeixinHeaderV2.TYPE_HEADER;
import static com.liaoinstan.springview.weixinheaderv2.RecycleAdapterWeixinHeaderV2.TYPE_MORE;

/**
 * Created by liaoinstan on 2019/11.
 * 给recyclerview 添加拖拽的帮助类ItemTouchHelper.Callback的实现
 */
public class DragItemTouchCallback extends ItemTouchHelper.Callback {

    private RecycleAdapterWeixinHeaderV2 adapter;
    private TextView deleteView;

    public DragItemTouchCallback(RecycleAdapterWeixinHeaderV2 adapter, TextView deleteView) {
        this.adapter = adapter;
        this.deleteView = deleteView;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return false;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder) {
        if (recyclerView.getLayoutManager() instanceof GridLayoutManager) {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN | ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        } else {
            final int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
            final int swipeFlags = 0;
            return makeMovementFlags(dragFlags, swipeFlags);
        }
    }

    @Override
    public RecyclerView.ViewHolder chooseDropTarget(@NonNull RecyclerView.ViewHolder selected, @NonNull List<RecyclerView.ViewHolder> dropTargets, int curX, int curY) {
        int right = curX + selected.itemView.getWidth();
        int bottom = curY + selected.itemView.getHeight();
        RecyclerView.ViewHolder winner = null;
        int winnerScore = -1;
        final int dx = curX - selected.itemView.getLeft();
        final int dy = curY - selected.itemView.getTop();
        final int targetsSize = dropTargets.size();
        for (int i = 0; i < targetsSize; i++) {
            final RecyclerView.ViewHolder target = dropTargets.get(i);
            int position = target.getLayoutPosition();
            boolean isLastItem = Utils.isLastItem(adapter.getResults(), position);
            boolean hasMineProgram = Utils.hasMineProgram(adapter.getResults());
            int limitValue = 0;
            if (isLastItem && !hasMineProgram && target instanceof RecycleAdapterWeixinHeaderV2.HolderHeader) {
                //如果这个item是最后一个item，并且当前集合里没有'我的小程序'，说明target此时是'拖拽区域'
                //这时设置一个和它高度一致的偏移量
                limitValue = -target.itemView.getHeight() / 2;
                areaViewHolder = (RecycleAdapterWeixinHeaderV2.HolderHeader) target;
            }
            if (dx > 0) {
                int diff = target.itemView.getRight() - right;
                if (diff < 0 && target.itemView.getRight() > selected.itemView.getRight()) {
                    final int score = Math.abs(diff);
                    if (score > winnerScore) {
                        winnerScore = score;
                        winner = target;
                    }
                }
            }
            if (dx < 0) {
                int diff = target.itemView.getLeft() - curX;
                if (diff > 0 && target.itemView.getLeft() < selected.itemView.getLeft()) {
                    final int score = Math.abs(diff);
                    if (score > winnerScore) {
                        winnerScore = score;
                        winner = target;
                    }
                }
            }
            if (dy < 0) {
                int diff = target.itemView.getTop() - curY;
                if (diff > limitValue && target.itemView.getTop() < selected.itemView.getTop()) {
                    final int score = Math.abs(diff);
                    if (score > winnerScore) {
                        winnerScore = score;
                        winner = target;
                    }
                }
            }

            if (dy > 0) {
                int diff = target.itemView.getBottom() - bottom;
                if (diff < -limitValue && target.itemView.getBottom() > selected.itemView.getBottom()) {
                    final int score = Math.abs(diff);
                    if (score > winnerScore) {
                        winnerScore = score;
                        winner = target;
                    }
                }
            }
        }
        return winner;
    }

    @Override
    public boolean onMove(@NonNull RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        int fromPosition = viewHolder.getAdapterPosition(); //得到拖动ViewHolder的position
        int toPosition = target.getAdapterPosition();       //得到目标ViewHolder的position
        return doOnMove(fromPosition, toPosition, false);
    }

    private boolean doOnMove(int fromPosition, int toPosition, boolean isAreaDrag) {
        //Log.e("onMove", "fromPosition:" + fromPosition + " toPosition:" + toPosition + " isRecentStart:" + isRecentStart+ " isAreaDrag:" + isAreaDrag);
        int toViewType = adapter.getItemViewType(toPosition);
        //header类别不可拖拽（拖到'拖拽区域'的情况例外）
        if (toViewType == TYPE_HEADER && !isAreaDrag) {
            return false;
        }
        //只有在'最近使用'内容为空时才可拖拽
        if (toViewType == TYPE_MORE && Utils.getRecentProgramCount(adapter.getResults()) > 0) {
            return false;
        }
        boolean isRecentFrom = Utils.isDragToRecentProgram(adapter.getResults(), fromPosition);
        boolean isRecentTo = Utils.isDragToRecentProgram(adapter.getResults(), toPosition);
        //不允许直接从'我的小程序'拖拽到'最近使用'（如果拖拽开始的位置就是'最近使用'的话，那么允许拖回去）
        if (!isRecentFrom && isRecentTo && !isRecentStart) {
            return false;
        }
        //开始执行拖拽逻辑
        if (fromPosition < toPosition) {
            for (int i = fromPosition; i < toPosition; i++) {
                Collections.swap(adapter.getResults(), i, i + 1);
            }
        } else {
            for (int i = fromPosition; i > toPosition; i--) {
                Collections.swap(adapter.getResults(), i, i - 1);
            }
        }
        adapter.notifyItemMoved(fromPosition, toPosition);
        return true;
    }

    @Override
    public long getAnimationDuration(@NonNull RecyclerView recyclerView, int animationType, float animateDx, float animateDy) {
        return 200;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
            //获取item在屏幕上位置
            int[] locationHolder = new int[2];
            int[] locationDelete = new int[2];
            viewHolder.itemView.getLocationOnScreen(locationHolder);
            deleteView.getLocationOnScreen(locationDelete);
            //检查是否拖拽到‘删除按钮’的区域
            if (locationHolder[1] + viewHolder.itemView.getHeight() / 2 >= locationDelete[1]) {
                if (!lastDeleteFlag) {
                    //拖入了‘删除按钮’的区域
                    deleteView.setText(R.string.weixin_v2_delete_in);
                    deleteView.setSelected(true);
                }
                lastDeleteFlag = true;
            } else {
                if (lastDeleteFlag) {
                    //拖出了‘删除按钮’的区域
                    deleteView.setText(R.string.weixin_v2_delete_out);
                    deleteView.setSelected(false);
                }
                lastDeleteFlag = false;
            }
            //检查拖拽手势是否进入'拖拽区域'
            if (areaViewHolder != null) {
                if (areaViewHolder.text_drag_area.getVisibility() == View.VISIBLE) {
                    int bottom = areaViewHolder.itemView.getBottom();
                    int top = bottom - areaViewHolder.text_drag_area.getHeight();
                    int dragCenter = viewHolder.itemView.getTop() + viewHolder.itemView.getHeight() / 2;
                    if (dY + dragCenter >= top && dY + dragCenter <= bottom) {
                        //拖拽进入'拖拽区域'，记录下位置
                        areaFromPosition = viewHolder.getAdapterPosition();
                        areaToPosition = areaViewHolder.getAdapterPosition();
                    } else {
                        //超出区域，撤销记录
                        areaFromPosition = -1;
                        areaToPosition = -1;
                    }
                } else {
                    areaFromPosition = -1;
                    areaToPosition = -1;
                }
            }
        }
    }

    private RecycleAdapterWeixinHeaderV2.HolderHeader areaViewHolder;
    private RecyclerView.ViewHolder nowViewHolder;
    private int areaFromPosition;
    private int areaToPosition;
    private boolean isRecentStart;
    private boolean lastDeleteFlag;

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder, int actionState) {
        //Log.e("onSelectedChanged", "onSelectedChanged:" + actionState);
        if (actionState == ItemTouchHelper.ACTION_STATE_DRAG) {
            //长按 按下，开始拖拽
            isRecentStart = Utils.isRecentProgram(adapter.getResults(), viewHolder.getAdapterPosition());
            nowViewHolder = viewHolder;
            if (viewHolder instanceof RecycleAdapterWeixinHeaderV2.HolderItem) {
                startItemDragAnim((RecycleAdapterWeixinHeaderV2.HolderItem) viewHolder);
            }
            if (onDragListener != null) onDragListener.onStartDrag();
        } else if (actionState == ItemTouchHelper.ACTION_STATE_IDLE) {
            if (nowViewHolder instanceof RecycleAdapterWeixinHeaderV2.HolderItem) {
                endItemDragAnim((RecycleAdapterWeixinHeaderV2.HolderItem) nowViewHolder);
            }
            if (onDragListener != null) onDragListener.onFinishDrag();
            if (deleteView.isSelected() && onDragListener != null) {
                //如果该item需要删除，则隐藏显示效果
                boolean isDrop = onDragListener.onDrop(nowViewHolder, nowViewHolder.getAdapterPosition());
                nowViewHolder.itemView.setVisibility(isDrop ? View.GONE : View.VISIBLE);
                return;
            }
            if (areaFromPosition != -1 && areaToPosition != -1) {
                nowViewHolder.itemView.setVisibility(View.GONE);
                //调用move把小程序移动到拖拽区域，这时需要隐藏item动画
                doOnMove(areaFromPosition, areaToPosition, true);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        nowViewHolder.itemView.setVisibility(View.VISIBLE);
                        ValueAnimator anim = ValueAnimator.ofFloat(0, 1); //1-1.5
                        anim.setInterpolator(new DecelerateInterpolator());
                        anim.setDuration(200);
                        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
                            @Override
                            public void onAnimationUpdate(ValueAnimator animation) {
                                Float value = (Float) animation.getAnimatedValue();
                                nowViewHolder.itemView.setAlpha(value);
                            }
                        });
                        anim.start();
                    }
                }, 300);
            }
        }
        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);
    }

    //############ item动画 ############

    private float MAX_SCALE = 1.5f;

    private void startItemDragAnim(final RecycleAdapterWeixinHeaderV2.HolderItem holder) {
        holder.text_name.setVisibility(View.GONE);
        ValueAnimator anim = ValueAnimator.ofFloat(1, MAX_SCALE); //1-1.5
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                holder.itemView.setScaleX(value);
                holder.itemView.setScaleY(value);
                float alpha = 2 - value;//1-0.5
                holder.itemView.setAlpha(alpha);
            }
        });
        anim.start();
    }

    private void endItemDragAnim(final RecycleAdapterWeixinHeaderV2.HolderItem holder) {
        holder.text_name.setVisibility(View.VISIBLE);
        ValueAnimator anim = ValueAnimator.ofFloat(MAX_SCALE, 1); //1.5-1
        anim.setInterpolator(new DecelerateInterpolator());
        anim.setDuration(300);
        anim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                Float value = (Float) animation.getAnimatedValue();
                holder.itemView.setScaleX(value);
                holder.itemView.setScaleY(value);
                float alpha = 2 - value;//0.5-1
                holder.itemView.setAlpha(alpha);
            }
        });
        anim.start();
    }


    //############ 对外接口 ############

    private OnDragListener onDragListener;

    public DragItemTouchCallback setOnDragListener(OnDragListener onDragListener) {
        this.onDragListener = onDragListener;
        return this;
    }

    public interface OnDragListener {
        void onStartDrag();

        void onFinishDrag();

        boolean onDrop(RecyclerView.ViewHolder viewHolder, int position);
    }
}
