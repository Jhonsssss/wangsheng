package com.bird.rockerdome.rocker;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.Region;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.bird.rockerdome.R;


/**
 * Created by Administrator on 2017/3/1.
 */

public class RockerSafeView extends View {
    private Context context;
    private Paint paint;
    private float circleX = 0, circleY = 0;  //圆心
    private Bitmap rockerSafeCircle, rockerSafeSectorUnselected, rockerSafeSectorSelected, controll_left, controll_right, controll_top, controll_bottom, safe_controll_left, safe_controll_right, safe_controll_top, safe_controll_bottom;
    private int selectedPosition = -1;  //选择的扇形的位置
    private Path left, top, right, bottom, inner; //四个扇形的Path
    private RectF rectF, rectFInner;
    private Boolean isLeft = false;
    private RockerSafeListener mListener;
    private int rockerMode;
    //    private AnimUtils animUtils;
    private float alpha = 0.4f;
    private Region regionLeft, regionTop, regionRight, regionBottom, regionInner;
    private float circleYOffset;
    private int titleHeight;

    private boolean effective = false;  //是否点击了有效区域
    private Bitmap rockerSafeSectorNothing;

    public RockerSafeView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        circleX = w / 2;
        circleY = h / 2;
    }

    private void init() {
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);
        paint = new Paint();
//        titleHeight = UiUtil.Dp2Px(30);
        Resources resources = context.getResources();
        //背景白色圆环
        rockerSafeCircle = BitmapFactory.decodeResource(resources, R.mipmap.rocker_safe_circle);
        float rockerWidth = resources.getDimensionPixelSize(R.dimen.rocker_width);
        float scaling = rockerWidth / (float) rockerSafeCircle.getWidth();
        rockerSafeCircle = BitmapUtil.resizeBitmap(rockerSafeCircle, scaling);
        //未选择状态的扇形
        rockerSafeSectorUnselected = BitmapUtil.resizeBitmap(BitmapFactory.decodeResource(resources, R.mipmap.rocker_safe_sector_unselected), scaling);
        //选择状态的扇形
        rockerSafeSectorSelected = BitmapUtil.resizeBitmap(BitmapFactory.decodeResource(resources, R.mipmap.rocker_safe_sector_selected), scaling);
        rockerSafeSectorNothing = BitmapUtil.resizeBitmap(BitmapFactory.decodeResource(resources, R.mipmap.weianxia1), scaling);
        circleYOffset = resources.getDimensionPixelSize(R.dimen.rocket_offset);
        //四个扇形Path  描述四个扇形区域
        float radius = rockerSafeCircle.getWidth() / 2;
        //计算45度扇形的起始点
        float cos45 = (float) (Math.cos(45) * circleX);
        rectF = new RectF(-radius, -radius,
                radius, radius);
        //四个方向按钮区域
        left = new Path();
        left.lineTo(-cos45, cos45);
        left.addArc(rectF, 135, 90f);
        left.lineTo(0, 0);

        top = new Path();
        top.lineTo(-cos45, -cos45);
        top.addArc(rectF, 225, 90f);
        top.lineTo(0, 0);

        right = new Path();
        right.lineTo(cos45, -cos45);
        right.addArc(rectF, 315, 90f);
        right.lineTo(0, 0);

        bottom = new Path();
        bottom.lineTo(cos45, cos45);
        bottom.addArc(rectF, 45, 90f);
        bottom.lineTo(0, 0);

        //各个区域
        regionLeft = new Region();
        left.computeBounds(rectF, true);
        regionLeft.setPath(left, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        regionTop = new Region();
        top.computeBounds(rectF, true);
        regionTop.setPath(top, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        regionRight = new Region();
        right.computeBounds(rectF, true);
        regionRight.setPath(right, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        regionBottom = new Region();
        bottom.computeBounds(rectF, true);
        regionBottom.setPath(bottom, new Region((int) rectF.left, (int) rectF.top, (int) rectF.right, (int) rectF.bottom));
        //中间不能触摸圆区域
        float innerRadiux = (float) (radius * 0.4);
        rectFInner = new RectF(-innerRadiux, -innerRadiux,
                innerRadiux, innerRadiux);
        inner = new Path();
        inner.addCircle(0, 0, (float) (radius * 0.4), Path.Direction.CW);
        regionInner = new Region();
        inner.computeBounds(rectFInner, true);
        regionInner.setPath(inner, new Region((int) rectFInner.left, (int) rectFInner.top, (int) rectFInner.right, (int) rectFInner.bottom));


        //左右上下操作指示按钮
        controll_left = BitmapUtil.resizeBitmap(BitmapFactory.decodeResource(resources, R.mipmap.sensor_mode_left_controll_left), scaling);
        controll_right = BitmapUtil.resizeBitmap(BitmapFactory.decodeResource(resources, R.mipmap.sensor_mode_left_controll_right), scaling);
        controll_top = BitmapUtil.resizeBitmap(BitmapFactory.decodeResource(resources, R.mipmap.sensor_mode_left_controll_top), scaling);
        controll_bottom = BitmapUtil.resizeBitmap(BitmapFactory.decodeResource(resources, R.mipmap.sensor_mode_left_controll_bottom), scaling);

        //安全模式左右上下操作指示按钮
        safe_controll_left = BitmapUtil.resizeBitmap(BitmapFactory.decodeResource(resources, R.mipmap.safe_mode_left_controll_left), scaling);
        safe_controll_right = BitmapUtil.resizeBitmap(BitmapFactory.decodeResource(resources, R.mipmap.safe_mode_left_controll_right), scaling);
        safe_controll_top = BitmapUtil.resizeBitmap(BitmapFactory.decodeResource(resources, R.mipmap.safe_mode_left_controll_top), scaling);
        safe_controll_bottom = BitmapUtil.resizeBitmap(BitmapFactory.decodeResource(resources, R.mipmap.safe_mode_left_controll_bottom), scaling);
//        animUtils = new AnimUtils();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
//        setAlpha(alpha);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.translate(circleX, circleY + circleYOffset);  //画布圆心位置确定
        //绘制白色圆环
        canvas.drawBitmap(rockerSafeCircle, -rockerSafeCircle.getWidth() / 2, -rockerSafeCircle.getHeight() / 2, paint);
        canvas.scale(0.98f, 0.98f);    //画布缩放
        //绘制扇形
        for (int i = 0; i < 4; i++) {

            if (i == selectedPosition) {
                canvas.drawBitmap(rockerSafeSectorSelected, -rockerSafeSectorSelected.getWidth() / 2, -rockerSafeSectorSelected.getHeight() / 2, paint);
            } else {
                canvas.drawBitmap(rockerSafeSectorUnselected, -rockerSafeSectorUnselected.getWidth() / 2, -rockerSafeSectorUnselected.getHeight() / 2, paint);
            }
            canvas.rotate(90, 0, 0);
        }

        //绘制上下左右操作按钮
        float offset1 = rockerSafeCircle.getWidth() / 2 - controll_left.getWidth() / 2;
        float offset2 = controll_left.getHeight() / 2;
        if (isLeft) {
            if (rockerMode == 1) {
                canvas.drawBitmap(controll_left, -offset1, -offset2, paint);
                canvas.drawBitmap(controll_right, offset1 - controll_left.getWidth(), -offset2, paint);
                canvas.drawBitmap(controll_top, -offset2, -(offset1), paint);
                canvas.drawBitmap(controll_bottom, -offset2, offset1 - controll_left.getWidth(), paint);
            } else {
                canvas.drawBitmap(controll_left, -offset1, -offset2, paint);
                canvas.drawBitmap(controll_right, offset1 - controll_left.getWidth(), -offset2, paint);
                canvas.drawBitmap(safe_controll_top, -offset2, -(offset1), paint);
                canvas.drawBitmap(safe_controll_bottom, -offset2, offset1 - controll_left.getWidth(), paint);
            }
        } else {
            if (rockerMode == 1) {
                canvas.drawBitmap(safe_controll_left, -offset1, -offset2, paint);
                canvas.drawBitmap(safe_controll_right, offset1 - controll_left.getWidth(), -offset2, paint);
                canvas.drawBitmap(safe_controll_top, -offset2, -(offset1), paint);
                canvas.drawBitmap(safe_controll_bottom, -offset2, offset1 - controll_left.getWidth(), paint);
            } else {
                canvas.drawBitmap(safe_controll_left, -offset1, -offset2, paint);
                canvas.drawBitmap(safe_controll_right, offset1 - controll_left.getWidth(), -offset2, paint);
                canvas.drawBitmap(controll_top, -offset2, -(offset1), paint);
                canvas.drawBitmap(controll_bottom, -offset2, offset1 - controll_left.getWidth(), paint);
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                matchingRegion((int) event.getX(), (int) event.getY());
                if (effective) {
                    invalidate();
//                    animUtils.valueAnim(this, alpha, 1.0f, 100);
                    if (mListener != null) {
                        mListener.toggleLeftOrRight(false);
                        mListener.onRockerClick(selectedPosition, true);
                    }
                }
                break;
            case MotionEvent.ACTION_UP:
                if (effective) {
                    invalidate();
//                    animUtils.valueAnim(this, 1.0f, alpha, 100);
                    if (mListener != null) {
                        mListener.toggleLeftOrRight(true);
                        mListener.onRockerClick(selectedPosition, false);
                    }
                    effective = false;
                }
                selectedPosition = -1;
                break;
        }
        return true;
    }

    //判断点中的点在哪一个区域
    private void matchingRegion(int x, int y) {
        x = x - (int) circleX;
        y = y - titleHeight - (int) circleY; //坐标偏移
        if (regionInner.contains(x, y))
            return;
        if (regionLeft.contains(x, y)) {
            selectedPosition = 3;
            effective = true;
            return;
        }
        if (regionTop.contains(x, y)) {
            selectedPosition = 0;
            effective = true;
            return;
        }
        if (regionRight.contains(x, y)) {
            selectedPosition = 1;
            effective = true;
            return;
        }
        if (regionBottom.contains(x, y)) {
            selectedPosition = 2;
            effective = true;
            return;
        }

    }

    public void setIsLeft(boolean isLeft, int rockerMode) {
        this.isLeft = isLeft;
        this.rockerMode = rockerMode;
        invalidate();
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (rockerSafeCircle != null) {
            rockerSafeCircle.recycle();
            rockerSafeCircle = null;
        }
        if (rockerSafeSectorUnselected != null) {
            rockerSafeSectorUnselected.recycle();
            rockerSafeSectorUnselected = null;
        }
        if (rockerSafeSectorNothing != null) {
            rockerSafeSectorNothing.recycle();
            rockerSafeSectorNothing = null;
        }
        if (rockerSafeSectorSelected != null) {
            rockerSafeSectorSelected.recycle();
            rockerSafeSectorSelected = null;
        }
        if (controll_left != null) {
            controll_left.recycle();
            controll_left = null;
        }
        if (controll_right != null) {
            controll_right.recycle();
            controll_right = null;
        }
        if (controll_top != null) {
            controll_top.recycle();
            controll_top = null;
        }
        if (controll_bottom != null) {
            controll_bottom.recycle();
            controll_bottom = null;
        }
        if (safe_controll_left != null) {
            safe_controll_left.recycle();
            safe_controll_left = null;
        }
        if (safe_controll_right != null) {
            safe_controll_right.recycle();
            safe_controll_right = null;
        }
        if (safe_controll_top != null) {
            safe_controll_top.recycle();
            safe_controll_top = null;
        }
        if (safe_controll_bottom != null) {
            safe_controll_bottom.recycle();
            safe_controll_bottom = null;
        }
    }

    public void reset() {
        effective = false;
        selectedPosition = -1;
//        setAlpha(alpha);
        invalidate();
    }

    public void setOnSensorLinstener(RockerSafeListener listener) {
        mListener = listener;
    }

    public interface RockerSafeListener {
        void onRockerClick(int position, boolean isDown);

        void toggleLeftOrRight(boolean isShow);
    }

}
