package com.bird.rockerdome.rocker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;


public class RockerView extends View {
    private static final String TAG = "RockerView";

    private Context context = null;
    private Paint paint;

    private int     viewW          = 0;
    private int     viewH          = 0;
    private int     POINT_CENTER_X = 0;  // Center X based on current view
    private int     POINT_CENTER_Y = 0;  // Center Y based on current view
    //固定摇杆背景圆形的X,Y坐标以及半径
    private int     bgCenterX      = 0;
    private int     bgCenterY      = 0;
    private int     bgR            = 0;  // 背景圆圈的半径
    private int     bgHalfWidth    = 0;  // 背景圆形的内接正方形边长的二分之一
    //摇杆的X,Y坐标以及摇杆的半径
    private int     pointCenterX   = 0;
    private int     pointCenterY   = 0;
    private int     pointR         = 0;  // 触控圆点的半径
    private float   angle          = 0;  // 旋转的弧度
    private boolean aroundCircle   = false;//轨迹是圆形还是矩形

    private float               scaling             = 1;
    private int                 circleWidth         = 0;
    private Bitmap bmpBg               = null;
    private Bitmap bmpPoint            = null;
    private Bitmap bmpClicPoint        = null;
    private RockerViewInterface rockerViewInterface = null;
    private int                 clickImgX           = 0;
    private int                 clickImgY           = 0;
    private boolean             isDrawClickImg      = false;
    private boolean             isDrawPoint         =false;
    private boolean             needReset           = false;
    private int mPosX;
    private int mPosY;
    private int mCurrentPosX;
    private int mCurrentPosY;

//    private AnimUtils mAnimUtils;
    public RockerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.setKeepScreenOn(true);
        paint = new Paint();
        paint.setAntiAlias(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
//        mAnimUtils = new AnimUtils();
    }

    public RockerView(Context context) {
        super(context);

        this.context = context;
        this.setKeepScreenOn(true);
        paint = new Paint();
        paint.setAntiAlias(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
    }

    public void setOnRockerChanged(RockerViewInterface rockerViewInterface) {
        this.rockerViewInterface = rockerViewInterface;
    }

    public void initView(int bgId, int pointId, int clickId) {

        if (clickId > 0) {
            bmpClicPoint = BitmapFactory.decodeResource(context.getResources(), clickId);
        }
        if (bgId > 0) {
            bmpBg = BitmapFactory.decodeResource(context.getResources(), bgId);
            if (null != bmpBg) {
                circleWidth = bmpBg.getWidth();
            }
        }

        if (pointId > 0) {
            bmpPoint = BitmapFactory.decodeResource(context.getResources(), pointId);
        }

        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @SuppressLint("NewApi")
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                viewH = getLayoutParams().height;
                viewW = viewH;

                if (null != bmpClicPoint) {

                    circleWidth = bmpBg.getWidth();
                    scaling = (float) viewW / circleWidth;
                    bmpClicPoint = BitmapUtil.resizeBitmap(bmpClicPoint, scaling);
                }
                if (viewW > 0 && circleWidth > 0) {
                    scaling = (float) viewW / circleWidth;

                    if (null != bmpBg) {
                        bmpBg = BitmapUtil.resizeBitmap(bmpBg, scaling);
                    }

                    if (null != bmpPoint) {
                        bmpPoint = BitmapUtil.resizeBitmap(bmpPoint, scaling);
                    }

                    if (null != bmpBg && null != bmpPoint) {
                        // Set circle center point
                        bgCenterX = (viewW / 2);
                        bgCenterY = (viewW / 2);

                        bgR = bmpBg.getWidth() / 2;
                        pointR = bmpPoint.getWidth() / 2;

                        // Point can only move inside the area of Circle (Background)
                        bgR -= pointR;

                        bgHalfWidth = (int) ((Math.sin((45f / 180f) * Math.PI)) * bgR);

                        pointCenterX = POINT_CENTER_X = bgCenterX;
                        pointCenterY = POINT_CENTER_Y = bgCenterY;

//                        invalidate();
                        requestLayout();
                    }
                }
            }
        });
    }

    /***
     * 得到两点之间的弧度
     */
    public double getRadian(float px1, float py1, float px2, float py2) {
        //得到两点X的距离
        float x = px2 - px1;
        //得到两点Y的距离
        float y = py1 - py2;
        //算出斜边长
        float tiltedSlash = (float) Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
        //得到这个角度的余弦值（通过三角函数中的定理 ：邻边/斜边=角度余弦值）
        float cosAngle = x / tiltedSlash;
        //通过反余弦定理获取到其角度的弧度
        float rad = (float) Math.acos(cosAngle);
        //注意：当触屏的位置Y坐标<摇杆的Y坐标我们要取反值-0~-180
        if (py2 < py1) {
            rad = -rad;
        }
        return rad;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        int x, y;
        isDrawClickImg = false;
        isDrawPoint=true;
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
                x = (int) event.getX();  // x based on current view
                y = (int) event.getY();  // y based on current view

                mPosX = (int) event.getX();
                mPosY = (int) event.getY();
                isClickCollision(x, y);
//            mAnimUtils.valueAnim(this, 0.3f, 1.0f, 100);
            if (mRockerSafeListener != null) {
                mRockerSafeListener.toggleLeftOrRight(false);
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            mCurrentPosX = (int) event.getX();
            mCurrentPosY = (int) event.getY();

//            LogUtil.Lee("安全摇杆 滑动事件  y==" + Math.abs(mCurrentPosY - mPosY) + "  x===" + Math.abs(mCurrentPosX - mPosX)+" pointR= "+pointR);

                x = (int) event.getX();  // x based on current view
                y = (int) event.getY();  // y based on current view

                if (aroundCircle) {
                    showAroundCircle(x, y);
                } else {
                    showAroundSquare(x, y);
                }
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            resetValueWhenUp();
//            mAnimUtils.valueAnim(this, 1.0f, 0.3f, 100);
            if (mRockerSafeListener != null) {
                mRockerSafeListener.toggleLeftOrRight(true);
            }
        }
        invalidate();

        return true;
    }

    private void resetValueWhenUp() {
        //当释放按键时摇杆要恢复摇杆的位置为初始位置
        pointCenterX = POINT_CENTER_X;
        pointCenterY = POINT_CENTER_Y;
        isDrawClickImg = false;
        isDrawPoint=false;
        angle = 0;
    }

    //按钮碰撞检测 PC add
    private void isClickCollision(int x, int y) {
        int w = bmpClicPoint.getWidth();
        int h = bmpClicPoint.getHeight();
        int px = pointCenterX - bmpClicPoint.getWidth() / 2;
        int pwx = pointCenterX + bmpClicPoint.getWidth() / 2;
        int py = pointCenterY - bmpClicPoint.getHeight() / 2;
        int phy = pointCenterY + bmpClicPoint.getHeight() / 2;
        clickImgX = 0;
        clickImgY = 0;
        isDrawClickImg = true;
        if (x > 0 + px && x < pwx &&
                y > 0 && y < py) { //up
            clickImgX = px;
            clickImgY = -10;
            showAroundSquare(px + (w >> 1), 0 + (h >> 1));
        } else if (x < px && x > 0 && //left
                y > py && y < phy) {
            clickImgX = -Math.round(10.0f * scaling);
            clickImgY = py + Math.round(10 * scaling);
            showAroundSquare(0 + (w >> 1), py + (h >> 1));
        } else if (x > pwx && x < viewW &&
                y > py && y < phy) {
            clickImgX = viewW - bmpClicPoint.getWidth() + Math.round(10.0f * scaling);
            clickImgY = py + Math.round(10 * scaling);
            showAroundSquare(pwx + (w >> 1), phy - (h >> 1));
        } else if (x > 0 + px && x < pwx &&
                y > phy && y < viewH) {
            clickImgX = px;
            clickImgY = viewH - bmpClicPoint.getHeight();
            showAroundSquare(px + (w >> 1), phy + (h >> 1));
        } else {
            isDrawClickImg = false;
        }
    }

    private void showAroundCircle(int x, int y) {
        // 当触屏区域不在圆形活动范围内
        if (Math.sqrt(Math.pow((bgCenterX - x), 2) + Math.pow((bgCenterY - y), 2)) >= bgR) {
            //得到摇杆与触屏点所形成的角度
            double radian = getRadian(bgCenterX, bgCenterY, x, y);
            //保证内部小圆运动的长度限制
            getXY(bgCenterX, bgCenterY, bgR, radian);

            // 换算成角度值，跟X轴正半轴形成的角度, -180 ~ 180
            angle = (float) (radian * 180 / Math.PI);
            // 换算成跟Y轴正半轴之间的角度, -180 ~ 180
            angle = (angle + 90);
            if (angle > 180) {
                angle = angle - 360;
            }
        } else {//如果小球中心点小于活动区域则随着用户触屏点移动即可
            pointCenterX = x;
            pointCenterY = y;
        }
    }

    private void showAroundSquare(int x, int y) {
        int xx = Math.abs(x - bgCenterX);
        int yy = Math.abs(y - bgCenterY);

        // 当触屏区域不在方形活动范围内
        if (xx > bgHalfWidth || yy > bgHalfWidth) {
            // 限制遥控点在方形范围内显示
            if (xx > bgHalfWidth) {
                int resX = xx - bgHalfWidth;
                if (x > bgCenterX) {
                    pointCenterX = x - resX;
                } else {
                    pointCenterX = x + resX;
                }
            } else {
                pointCenterX = x;
            }

            if (yy > bgHalfWidth) {
                int resY = yy - bgHalfWidth;
                if (y > bgCenterY) {
                    pointCenterY = y - resY;
                } else {
                    pointCenterY = y + resY;
                }
            } else {
                pointCenterY = y;
            }

            //得到摇杆与触屏点所形成的角度
            double radian = getRadian(bgCenterX, bgCenterY, x, y);

            // 换算成角度值，跟X轴正半轴形成的角度, -180 ~ 180
            angle = (float) (radian * 180 / Math.PI);
            // 换算成跟Y轴正半轴之间的角度, -180 ~ 180
            angle = (angle + 90);
            if (angle > 180) {
                angle = angle - 360;
            }
        } else {//如果小球中心点小于活动区域则随着用户触屏点移动即可
            pointCenterX = x;
            pointCenterY = y;
        }
    }

    /**
     * @param R       圆周运动的旋转点
     * @param centerX 旋转点X
     * @param centerY 旋转点Y
     * @param rad     旋转的弧度
     */
    public void getXY(float centerX, float centerY, float R, double rad) {
        //获取圆周运动的X坐标
        pointCenterX = (int) ((R * Math.cos(rad)) + centerX);
        //获取圆周运动的Y坐标
        pointCenterY = (int) ((R * Math.sin(rad)) + centerY);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        // set layout dimension as the size of current view
        setMeasuredDimension(viewW, viewH);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        try {
            if (isDrawClickImg && null != bmpClicPoint) {
                canvas.drawBitmap(bmpClicPoint, clickImgX, clickImgY, null);
            }
            if (null != bmpBg) {
                canvas.drawBitmap(bmpBg, 0, 0, null);
            }

            if (null != bmpPoint&&isDrawPoint) {
                    canvas.drawBitmap(bmpPoint, (pointCenterX - pointR), (pointCenterY - pointR), null);
            }

            if (null != rockerViewInterface) {
                float xVal = 0;
                float yVal = 0;
                if (aroundCircle) {
                    xVal = (float) (pointCenterX - POINT_CENTER_X) / bgR;
                    yVal = (float) (pointCenterY - POINT_CENTER_Y) / bgR;
                } else {
                    xVal = (float) (pointCenterX - POINT_CENTER_X) / bgHalfWidth;
                    yVal = (float) (pointCenterY - POINT_CENTER_Y) / bgHalfWidth;
                }
                rockerViewInterface.onRockerChanged(this, xVal, yVal, angle);
            }
        } catch (Exception e) {
            // TODO: handle exception
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (bmpBg != null) {
            bmpBg.recycle();
            bmpBg = null;
        }
        if (bmpPoint != null) {
            bmpPoint.recycle();
            bmpPoint = null;
        }
        if (bmpClicPoint != null) {
            bmpClicPoint.recycle();
            bmpClicPoint = null;
        }
    }

    public void reset() {
        //当释放按键时摇杆要恢复摇杆的位置为初始位置
        pointCenterX = bgCenterX;
        pointCenterY = bgCenterY;
        isDrawClickImg = false;
        angle = 0;
        needReset = true;
    }

    private RockerViewListener mRockerSafeListener;
    public void setOnSensorLinstener(RockerViewListener listener) {
        mRockerSafeListener = listener;
    }
    public interface RockerViewListener {
        void toggleLeftOrRight(boolean isShow);
    }
}