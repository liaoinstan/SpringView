package com.liaoinstan.demospring.demo12;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.support.annotation.Nullable;
import android.support.v7.widget.AppCompatTextView;
import android.util.AttributeSet;

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
    private int delayMilliseconds = 30;//每帧间隔时间
    private int addRadius = 5;
    private float centerX;//圆心x
    private float centerY;//圆心y
    private float maxRadius;

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
        maxRadius = w / 2;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //绘制扩散的圆
        canvas.drawCircle(centerX, centerY, radius, paint);
//        radius += addRadius;
//        postInvalidateDelayed(delayMilliseconds);
    }

    public void start() {
        postInvalidate();
    }

    public void end() {
    }
}
