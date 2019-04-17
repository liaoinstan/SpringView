package com.liaoinstan.springview.wangyiheader;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.liaoinstan.springview.utils.DensityUtil;

import androidx.annotation.Nullable;

public class CircleRoundView extends View {

    private int color_circle;
    // 画圆所在的距形区域
    private RectF mRectF;
    //扇形线宽
    private int strokeWidth;

    private Paint mPaint;

    public CircleRoundView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs,0);
        initBase();
    }

    public CircleRoundView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
        initBase();
    }

    private void initAttr(@Nullable AttributeSet attrs) {
        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.WaveTextView, 0, 0);
        color_circle = a.getColor(R.styleable.WaveTextView_wave_color, Color.parseColor("#aace0000"));
        a.recycle();
    }

    private void initBase() {
        mRectF = new RectF();
        mPaint = new Paint();
        strokeWidth = DensityUtil.dp2px(5);
        // 设置画笔相关属性
        mPaint.setAntiAlias(true);
        mPaint.setColor(color_circle);
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
