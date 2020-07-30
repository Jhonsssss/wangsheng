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
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;

import com.bird.rockerdome.R;


public class RockerView extends View {
    private static final String TAG = "RockerView";

    private Context context = null;
    private Paint paint;

    private int viewW = 0;
    private int viewH = 0;
    private int POINT_CENTER_X = 0;  // Center X based on current view
    private int POINT_CENTER_Y = 0;  // Center Y based on current view
    //固定摇杆背景圆形的X,Y坐标以及半径
    private int bgCenterX = 0;
    private int bgCenterY = 0;
    private int bgR = 0;  // 背景圆圈的半径
    private int bgHalfWidth = 0;  // 背景圆形的内接正方形边长的二分之一
    //摇杆的X,Y坐标以及摇杆的半径
    private int pointCenterX = 0;
    private int pointCenterY = 0;
    private int pointR = 0;  // 触控圆点的半径
    private float angle = 0;  // 旋转的弧度
    private boolean aroundCircle = true;//轨迹是圆形还是矩形

    private float scaling = 1;
    private int circleWidth = 0;
    private Bitmap bmpBg = null;
    private Bitmap bmpPoint = null;
    private RockerViewInterface rockerViewInterface = null;
    private int clickImgX = 0;
    private int clickImgY = 0;
    private boolean isDrawClickImg = false;
    private RectF rectF, rectFInner;
    private Path left, top, right, bottom;
    private Region regionLeft, regionTop, regionRight, regionBottom, regionInner;
    private Path inner;
    private int selectedPosition = -1;
    private Bitmap leftLight, rightLight, topLight, bottomLight;
    public static final int INNER = -1;
    public static final int LEFT = 0;
    public static final int TOP = 1;
    public static final int RIGHT = 2;
    public static final int BOTTOM = 3;


    public RockerView(Context context, AttributeSet attrs) {
        super(context, attrs);

        this.context = context;
        this.setKeepScreenOn(true);
        paint = new Paint();
        paint.setAntiAlias(true);
        setFocusable(true);
        setFocusableInTouchMode(true);
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

    /**
     * @param bgId    背景图片资源id
     * @param pointId 点的图片资源id
     */
    public void initView(int bgId, int pointId) {
        if (bgId > 0) {
            bmpBg = BitmapFactory.decodeResource(context.getResources(), bgId);
            if (null != bmpBg) {
                circleWidth = bmpBg.getWidth();
            }
        }
        if (pointId > 0) {
            bmpPoint = BitmapFactory.decodeResource(context.getResources(), pointId);
        }

        //上下左右的滑动到对应区域的光线
        leftLight = BitmapFactory.decodeResource(context.getResources(), R.mipmap.left_move_light);
        rightLight = BitmapFactory.decodeResource(context.getResources(), R.mipmap.right_move_light);
        topLight = BitmapFactory.decodeResource(context.getResources(), R.mipmap.top_move_light);
        bottomLight = BitmapFactory.decodeResource(context.getResources(), R.mipmap.bottom_move_light);
        ViewTreeObserver vto = getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

            @Override
            public void onGlobalLayout() {
                getViewTreeObserver().removeOnGlobalLayoutListener(this);
                relayoutRc();

            }
        });
        initRegion(context.getResources());
    }

    private void relayoutRc() {
        viewH = getLayoutParams().height;
        viewW = viewH;
        circleWidth = bmpBg.getWidth();
        scaling = (float) (viewW - DimensionUtil.dp2px(context, 12)) / circleWidth;
        if (viewW > 0 && circleWidth > 0) {
            scaling = (float) (viewW - DimensionUtil.dp2px(context, 12)) / circleWidth;
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
                bgR -= pointR;
                bgHalfWidth = (int) ((Math.sin((45f / 180f) * Math.PI)) * bgR);
                pointCenterX = POINT_CENTER_X = bgCenterX;
                pointCenterY = POINT_CENTER_Y = bgCenterY;
                requestLayout();
            }
        }
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
        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            if (mRockerSafeListener != null) {
                mRockerSafeListener.toggleLeftOrRight(false);
            }
        } else if (event.getAction() == MotionEvent.ACTION_MOVE) {
            x = (int) event.getX();  // x based on current view
            y = (int) event.getY();  // y based on current view
            if (aroundCircle) {
                showAroundCircle(x, y);
            } else {
                showAroundSquare(x, y);
            }
        } else if (event.getAction() == MotionEvent.ACTION_UP || event.getAction() == MotionEvent.ACTION_CANCEL) {
            resetValueWhenUp();
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
        angle = 0;
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
            // 画背景
            if (null != bmpBg) {
                canvas.drawBitmap(bmpBg, DimensionUtil.dp2px(context, 6), DimensionUtil.dp2px(context, 6), null);
            }
            //画点的位置
            if (null != bmpPoint) {
                canvas.drawBitmap(bmpPoint, (pointCenterX - pointR), (pointCenterY - pointR), null);

                //根据点的位置来画摇杆外围光圈
                matchingRegion(pointCenterX - pointR + bmpPoint.getWidth() / 2, pointCenterY - pointR + bmpPoint.getHeight() / 2);
                switch (selectedPosition) {
                    case INNER:
                        break;
                    case LEFT:
                        canvas.drawBitmap(leftLight, 0, (getHeight() - leftLight.getHeight()) / 2, paint);
                        break;
                    case TOP:
                        canvas.drawBitmap(topLight, (getWidth() - topLight.getWidth()) / 2, 0, paint);
                        break;
                    case RIGHT:
                        canvas.drawBitmap(rightLight, getWidth() - rightLight.getWidth(), (getHeight() - rightLight.getHeight()) / 2, paint);
                        break;
                    case BOTTOM:
                        canvas.drawBitmap(bottomLight, (getWidth() - bottomLight.getWidth()) / 2, getWidth() - bottomLight.getHeight(), paint);
                        break;
                    default:
                        break;
                }
            }

            if (null != rockerViewInterface) {
                // xVal, yVal: -1 ~ 1
                float xVal = 0;
                float yVal = 0;
                if (aroundCircle) {
                    xVal = (float) (pointCenterX - POINT_CENTER_X) / bgR;
                    yVal = (float) (pointCenterY - POINT_CENTER_Y) / bgR;
                } else {
                    xVal = (float) (pointCenterX - POINT_CENTER_X) / bgHalfWidth;
                    yVal = (float) (pointCenterY - POINT_CENTER_Y) / bgHalfWidth;
                    //LogUtil.Lee("RockerView fales  twoX     xVal： " + xVal + " yVal： " + yVal);
                }
                rockerViewInterface.onRockerChanged(this, xVal, yVal, angle);
            }
        } catch (Exception e) {
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
        if (topLight != null) {
            topLight.recycle();
            topLight = null;
        }
        if (rightLight != null) {
            rightLight.recycle();
            rightLight = null;
        }
        if (leftLight != null) {
            leftLight.recycle();
            leftLight = null;
        }
        if (bottomLight != null) {
            bottomLight.recycle();
            bottomLight = null;
        }

        if (leftLight != null) {
            leftLight.recycle();
            leftLight = null;
        }
        if (rightLight != null) {
            rightLight.recycle();
            rightLight = null;
        }
        if (topLight != null) {
            topLight.recycle();
            topLight = null;
        }
        if (bottomLight != null) {
            bottomLight.recycle();
            bottomLight = null;
        }
    }

    public void reset() {
        //当释放按键时摇杆要恢复摇杆的位置为初始位置
        pointCenterX = bgCenterX;
        pointCenterY = bgCenterY;
        angle = 0;
    }

    private RockerViewListener mRockerSafeListener;

    public void setOnSensorLinstener(RockerViewListener listener) {
        mRockerSafeListener = listener;
    }

    public interface RockerViewListener {
        void toggleLeftOrRight(boolean isShow);
    }


    /**
     * 初始化对应的多个区域
     * @param resources 资源
     */
    public void initRegion(Resources resources) {
        float rockerWidth = resources.getDimensionPixelSize(R.dimen.rocker_width);
        //四个扇形Path  描述四个扇形区域
        float radius = rockerWidth / 2;
        //计算45度扇形的起始点
        float cos45 = (float) (Math.cos(45) * rockerWidth);
        rectF = new RectF(0, 0,
                rockerWidth, rockerWidth);
        //四个方向按钮区域
        left = new Path();
        left.lineTo(-cos45, cos45);
        left.addArc(rectF, 135, 90f);
        left.lineTo(radius, radius);

        top = new Path();
        top.lineTo(-cos45, -cos45);
        top.addArc(rectF, 225, 90f);
        top.lineTo(radius, radius);

        right = new Path();
        right.lineTo(cos45, -cos45);
        right.addArc(rectF, 315, 90f);
        right.lineTo(radius, radius);

        bottom = new Path();
        bottom.lineTo(cos45, cos45);
        bottom.addArc(rectF, 45, 90f);
        bottom.lineTo(radius, radius);

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
        float innerRadiux = (float) (radius * 0.3);
        rectFInner = new RectF(-innerRadiux, -innerRadiux,
                innerRadiux, innerRadiux);
        inner = new Path();
        inner.addCircle(radius, radius, (float) (radius * 0.3), Path.Direction.CW);
        regionInner = new Region();
        inner.computeBounds(rectFInner, true);
        regionInner.setPath(inner, new Region((int) rectFInner.left, (int) rectFInner.top, (int) rectFInner.right, (int) rectFInner.bottom));

    }

    //判断点中的点在哪一个区域
    private void matchingRegion(int x, int y) {//判断中心点的位置在哪个区域
        if (regionInner.contains(x, y)) {
            selectedPosition = INNER;
            return;
        }
        if (regionLeft.contains(x, y)) {
            selectedPosition = LEFT;
            return;
        }
        if (regionTop.contains(x, y)) {
            selectedPosition = TOP;
            return;
        }
        if (regionRight.contains(x, y)) {
            selectedPosition = RIGHT;
            return;
        }
        if (regionBottom.contains(x, y)) {
            selectedPosition = BOTTOM;
            return;
        }

    }


}