package com.liaoinstan.springview.weixinheaderv2;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewTreeObserver;

import androidx.annotation.Nullable;

import com.liaoinstan.springview.weixinheader.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * create by liaoinstan
 * 仿微信小程序header背景动画
 * 微信的动画实在看不出规律，只能模仿一个类似的烟雾效果
 */
public class SmokeView extends View {

    private Paint paint;
    private Bitmap bitmap;
    private int heightParent;
    private int widthParent;
    private boolean isStart = false;

    private List<DrawInfo> drawInfos = new ArrayList<>();

    public SmokeView(Context context) {
        this(context, null);
    }

    public SmokeView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SmokeView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @Override
    protected void onFinishInflate() {
        setBackgroundResource(R.drawable.shape_rect_gradient_weixinheader_bg);
        super.onFinishInflate();
    }

    private void init() {
        paint = new Paint();
        getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                paint.setAlpha(90);
                bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.smoke);
                for (int i = 0; i < 50; i++) {
                    drawInfos.add(new DrawInfo(bitmap));
                }
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        heightParent = getHeight();
        widthParent = getWidth();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        for (DrawInfo drawInfo : drawInfos) {
            drawInfo.drawBitmap(canvas);
        }
        if (isStart) {
            postInvalidateDelayed(17);
        }
    }

    public void startAnim() {
        if (!isStart) {
            isStart = true;
            postInvalidate();
        }
    }

    public void stopAnim() {
        isStart = false;
    }

    private class DrawInfo {
        Bitmap bitmap;
        Matrix matrix = new Matrix();
        float dx;
        float dy;

        //xy轴速度
        float moveX;
        float moveY;
        //高宽
        float width;
        float height;
        float scale;
        float rotate;

        DrawInfo(Bitmap bitmap) {
            this.bitmap = bitmap;
            Random random = new Random();
            scale = 3;
            rotate = random.nextInt(360);
            width = bitmap.getWidth() * scale;
            height = bitmap.getHeight() * scale;
            //初始化一个随机速度
            moveX = random.nextInt(6) + random.nextFloat() - 3;
            moveY = random.nextInt(6) + random.nextFloat() - 3;
            //随机一个初始位置
            if (widthParent != 0 && heightParent != 0) {
                dx = random.nextInt(widthParent) + random.nextFloat();
                dy = random.nextInt(heightParent) + random.nextFloat();
            }

            if (moveX == 0) moveX = 1;
            if (moveY == 0) moveY = 1;

        }

        void drawBitmap(Canvas canvas) {
            //x,y轴速度递加
            dx += moveX;
            dy += moveY;

            //draw
            matrix.setScale(scale, scale);
            matrix.postRotate(rotate, width / 2, height / 2);
            matrix.postTranslate(dx, dy);
            canvas.drawBitmap(bitmap, matrix, paint);

            //碰到边界反向
            if (dx + width > widthParent + 500 || dx < 0 - 200) {
                moveX = -moveX;
            }
            if (dy + height > heightParent + 500 || dy < 0) {
                moveY = -moveY;
            }
        }
    }
}
