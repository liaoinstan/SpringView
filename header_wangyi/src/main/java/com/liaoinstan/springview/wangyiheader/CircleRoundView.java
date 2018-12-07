package com.liaoinstan.springview.wangyiheader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.View;

import com.liaoinstan.springview.utils.DensityUtil;

public class CircleRoundView extends View {

    // 画圆所在的距形区域
    private RectF mRectF;
    //扇形线宽
    private int strokeWidth;

    private Paint mPaint;

    public CircleRoundView(Context context) {
        super(context);
        init();
    }

    public CircleRoundView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public CircleRoundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        mRectF = new RectF();
        mPaint = new Paint();
        strokeWidth = DensityUtil.dp2px(5);
        // 设置画笔相关属性
        mPaint.setAntiAlias(true);
        mPaint.setColor(Color.parseColor("#aace0000"));
        mPaint.setAntiAlias(true);//取消锯齿
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        int width = this.getWidth();
        int height = this.getHeight();

        int min = Math.min(width, height);

        int space = Math.abs(width - height) / 2;

        // 位置
        mRectF.left = (int) Math.ceil((double) strokeWidth / 2); // 左上角x
        mRectF.right = width - (int) Math.ceil((double) strokeWidth / 2); // 左下角x
        mRectF.top = (int) Math.ceil((double) strokeWidth / 2); // 左上角y
        mRectF.bottom = height - (int) Math.ceil((double) strokeWidth / 2); // 右下角y
        if (width > height) {
            mRectF.left += space;
            mRectF.right -= space;
        } else {
            mRectF.top += space;
            mRectF.bottom -= space;
        }

        if (min <= strokeWidth * 2 || isFull) {
            mPaint.setStrokeWidth(0);
            mPaint.setStyle(Paint.Style.FILL);
            canvas.drawCircle(width / 2, height / 2, min / 2, mPaint);
        } else {
            mPaint.setStrokeWidth(strokeWidth);
            mPaint.setStyle(Paint.Style.STROKE);
            canvas.drawArc(mRectF, 0, 360, false, mPaint);
        }
    }

    private boolean isFull;

    public void setFull(boolean full) {
        isFull = full;
        invalidate();
    }
}
