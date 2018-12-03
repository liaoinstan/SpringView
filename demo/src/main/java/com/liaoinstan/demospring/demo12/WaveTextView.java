package com.liaoinstan.demospring.demo12;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;
import android.util.Log;

import com.liaoinstan.springview.utils.DensityUtil;

public class WaveTextView extends AppCompatTextView {
    public WaveTextView(Context context) {
        this(context, null);
    }

    public WaveTextView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaveTextView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttr(attrs);
        initBase();
    }

    private void initAttr(@Nullable AttributeSet attrs) {
//        TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.SpreadView, 0, 0);
//        spreadColor = a.getColor(R.styleable.SpreadView_spread_spread_color, ContextCompat.getColor(getContext(), R.color.common_blue));
//        a.recycle();
    }

    private Paint paint;
    private float radius;
    private float centerX;//圆心x
    private float centerY;//圆心y
    private float maxRadius;
    private ValueAnimator valueAnimator;

    private void initBase() {
        paint = new Paint();
        paint.setColor(Color.RED);
        paint.setAntiAlias(true);
        if (isInEditMode()) {
            //编辑器预览
            radius = DensityUtil.dp2px(50);
        }
    }


    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        //圆心位置
        centerX = w / 2;
        centerY = h / 2;
        maxRadius = (float) Math.sqrt(Math.pow(w / 2d, 2) + Math.pow(h / 2d, 2)) + 10;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        //绘制扩散的圆
        canvas.drawCircle(centerX, centerY, radius, paint);
        super.onDraw(canvas);
    }

    public void start() {
        if (valueAnimator != null) {
            valueAnimator.cancel();
        }
        valueAnimator = ValueAnimator.ofFloat(0, maxRadius);
        valueAnimator.setDuration(200);
        valueAnimator.addUpdateListener(animation -> {
            radius = (float) animation.getAnimatedValue();
            Log.e("liao", radius + "");
            postInvalidate();
        });
        valueAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationCancel(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
            }

            @Override
            public void onAnimationStart(Animator animation) {
            }
        });
        valueAnimator.start();
    }

    public void reset() {
        radius = 0;
        postInvalidate();
    }
}
