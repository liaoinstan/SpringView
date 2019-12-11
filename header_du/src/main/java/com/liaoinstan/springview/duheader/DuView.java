package com.liaoinstan.springview.duheader;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;

import java.util.ArrayList;
import java.util.List;

/**
 * 仿"毒"App 下拉动画的自定义view，通过不同的拉动系数设置其绘制效果
 * {@link #setProgress(float)} 参数范围 0 - 100
 * 当参数为0时，显示完整的'毒'字，为100时笔画完全散落，可以超出100
 */
public class DuView extends View {

    private static final float widthDu = 124;

    private Paint paint = new Paint();
    private List<Line> lines = new ArrayList<>();

    public DuView(Context context) {
        this(context, null);
    }

    public DuView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DuView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
    }

    private void init() {
        paint.setStrokeWidth(3);
        paint.setAntiAlias(true);

        //一横
        lines.add(new Line(20, 24, 61, 24, 4.5f, 1.7f, 57f));
        lines.add(new Line(61, 24, 101, 24, 4.3f, 1, 53.5f));
        //二横
        lines.add(new Line(20, 38, 61, 38, 4.2f, 1.9f, 49f));
        lines.add(new Line(61, 38, 102, 38, 4, 1.5f, 45.5f));
        //竖
        lines.add(new Line(61, 20, 61, 53, 3.7f, 1.7f, 42f));
        //母：一横
        lines.add(new Line(19, 52, 61, 52, 3.5f, 1.7f, 38.5f));
        lines.add(new Line(61, 52, 104, 52, 3.3f, 1.7f, 35f));
        //母：左竖
        lines.add(new Line(20, 51, 20, 88, 3.1f, 2, 31.5f));
        //母：右竖
        lines.add(new Line(103, 51, 103, 88, 3, 2, 28f));
        //母：二横
        lines.add(new Line(16, 69, 62, 69, 3, 2, 24.5f));
        lines.add(new Line(62, 69, 107, 69, 3, 2, 21f));
        //母：三横
        lines.add(new Line(19, 87, 62, 87, 3, 2, 17.5f));
        lines.add(new Line(62, 87, 104, 87, 3, 3, 14f));
        //母：中竖
        lines.add(new Line(65, 52, 60, 69, 3, 2, 10.5f));
        lines.add(new Line(65, 69, 60, 87, 3, 2.5f, 7f));
        //勾
        lines.add(new Line(102, 87, 97, 102, 3, 3, 3.5f));
        lines.add(new Line(52, 102, 98, 102, 3, 3.5f, 0));
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        int mWidth = (int) widthDu + getPaddingLeft() + getPaddingRight();
        int mHeight = (int) widthDu + getPaddingTop() + getPaddingBottom();
        if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT && getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, mHeight);
        } else if (getLayoutParams().width == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(mWidth, heightSize);
        } else if (getLayoutParams().height == ViewGroup.LayoutParams.WRAP_CONTENT) {
            setMeasuredDimension(widthSize, mHeight);
        }
        for (Line line : lines) {
            line.setOffsetX(getMeasuredWidth() / 2 - widthDu / 2);
            line.setOffsetY(getMeasuredHeight() / 2 - widthDu / 2);
        }
    }

    /**
     * 0-100,可以超出
     */
    public void setProgress(float progress) {
        for (Line line : lines) {
            line.setProgress(progress);
        }
        postInvalidate();
    }

    //##################################
    //#########     动画     ###########
    //##################################

    int value;
    Runnable runnable = new Runnable() {
        @Override
        public void run() {
            value++;
            if (value >= lines.size() + 3) {
                value = 0;
            }
            for (Line line : lines) {
                line.heightLight = false;
            }
            if (value - 1 >= 0 && value - 1 < lines.size()) {
                lines.get(value - 1).heightLight = true;
            }
            if (value - 2 >= 0 && value - 2 < lines.size()) {
                lines.get(value - 2).heightLight = true;
            }
            if (value - 3 >= 0 && value - 3 < lines.size()) {
                lines.get(value - 3).heightLight = true;
            }
            postInvalidate();

            postDelayed(runnable, 100);
        }
    };

    public void startAnim() {
        post(runnable);
    }

    public void stopAnim() {
        removeCallbacks(runnable);
    }

    public void resetAnim() {
        removeCallbacks(runnable);
        value = 0;
        for (Line line : lines) {
            line.heightLight = false;
        }
        postInvalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (Line line : lines) {
            line.drawLine(canvas);
        }
    }

    private class Line {
        float x1, y1, x2, y2;
        float offsetX, offsetY;
        float degree;
        float translateX;
        float zoom = 1;
        float zoomSize = 1;

        float degK;
        float transK;
        float delay;
        static final float WIDTH = 3;
        boolean heightLight;

        Line(float x1, float y1, float x2, float y2) {
            this(x1, y1, x2, y2, 0, 0, 0);
        }

        Line(float x1, float y1, float x2, float y2, float degK, float transK, float delay) {
            this.x1 = x1;
            this.y1 = y1;
            this.x2 = x2;
            this.y2 = y2;
            this.degK = degK;
            this.transK = transK;
            this.delay = delay;
        }

        void setOffsetX(float offsetX) {
            this.offsetX = offsetX;
        }

        void setOffsetY(float offsetY) {
            this.offsetY = offsetY;
        }

        void setProgress(float progress) {
            float dProgress = progress - delay > 0 ? progress - delay : 0;
            degree = dProgress * degK;
            translateX = dProgress * transK;
            zoom = 1 - dProgress / 100;
            zoomSize = 1 - dProgress / (100 - delay);
        }

        float[] center() {
            float cx = (getX1() + getX2()) / 2;
            float cy = (getY1() + getY2()) / 2;
            return new float[]{cx, cy};
        }

        Line rotate(float degree) {
            float[] center = center();
            float cx = center[0];
            float cy = center[1];
            float dx = getX2() - cx;
            float dy = getY2() - cy;
            float k = (float) Math.toRadians((double) degree);
            float dtx = (float) (Math.cos(k) * dx + Math.sin(k) * dy);
            float dty = (float) (Math.cos(k) * dy - Math.sin(k) * dx);
            float x1 = cx + dtx;
            float y1 = cy + dty;
            float x2 = 2 * cx - x1;
            float y2 = 2 * cy - y1;
            return new Line(x1, y1, x2, y2);
        }

        Line zoom(float zoom) {
            float[] center = center();
            float cx = center[0];
            float cy = center[1];
            float x1 = (1 - zoom) * cx + zoom * getX1();
            float y1 = (1 - zoom) * cy + zoom * getY1();
            float x2 = 2 * cx - x1;
            float y2 = 2 * cy - y1;
            return new Line(x1, y1, x2, y2);
        }

        Line translateX(float tansX) {
            return new Line(getX1() + tansX, getY1(), getX2() + tansX, getY2());
        }

        void drawLine(Canvas canvas) {
            Line drawLine = this.rotate(degree).translateX(translateX).zoom(zoom);
            if (zoomSize == 0) {
                paint.setAlpha(0);
                paint.setStrokeWidth(0.01f);
            } else {
                paint.setAlpha(1);
                paint.setStrokeWidth(WIDTH * zoomSize);
            }
            paint.setColor(Color.parseColor(heightLight ? "#aa000000" : "#55000000"));
            canvas.drawLine(drawLine.getX1(), drawLine.getY1(), drawLine.getX2(), drawLine.getY2(), paint);
        }

        //get & set

        float getX1() {
            return x1 + offsetX;
        }

        float getY1() {
            return y1 + offsetY;
        }

        float getX2() {
            return x2 + offsetX;
        }

        float getY2() {
            return y2 + offsetY;
        }
    }
}
