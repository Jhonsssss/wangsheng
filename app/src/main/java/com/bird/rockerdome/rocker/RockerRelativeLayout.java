package com.bird.rockerdome.rocker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.RelativeLayout;

import com.bird.rockerdome.R;


public class RockerRelativeLayout extends RelativeLayout {
  private static final String TAG = "RockerRelativeLayout";

  private RockerView sv_rocker_one;
  private RockerView sv_rocker_two;

  private int viewW = -1;
  private int viewH = -1;

  private float oneX = -1;
  private float oneY = -1;
  private int   oneW = -1;
  private int   oneH = -1;

  private float twoX = -1;
  private float twoY = -1;
  private int   twoW = -1;
  private int   twoH = -1;

  public RockerRelativeLayout(Context context, AttributeSet attrs) {
    super(context, attrs);
    ViewTreeObserver vto = getViewTreeObserver();
    vto.addOnGlobalLayoutListener(new OnGlobalLayoutListener() {

      @SuppressLint("NewApi") public void onGlobalLayout() {
        getViewTreeObserver().removeOnGlobalLayoutListener(this);

        viewW = getWidth();
        viewH = getHeight();

        int cnt = getChildCount();
        for (int i = 0; i < cnt; i++) {
          View view = getChildAt(i);
          if (view.getId() == R.id.rv_throttle_left) {
            sv_rocker_one = (RockerView) view;
          } else if (view.getId() == R.id.rv_throttle_right) {
            sv_rocker_two = (RockerView) view;
          }
        }
      }
    });
  }

  @SuppressLint("NewApi") @Override
  public boolean onInterceptTouchEvent(MotionEvent ev) {
    int pointerCount = ev.getPointerCount();
    switch (ev.getAction()) {
      case MotionEvent.ACTION_DOWN:
        float x = ev.getX();
        float y = ev.getY();

        // For Rocker one
        if (null != sv_rocker_one) {
          if (oneX < 0 || oneY < 0) {
            oneX = sv_rocker_one.getX();
            oneY = sv_rocker_one.getY();
          }

          if (oneW < 0 || oneH < 0) {
            oneW = sv_rocker_one.getWidth();
            oneH = sv_rocker_one.getHeight();
          }

          if (oneW > 0 && oneH > 0) {
            float oneCenterL = (x - oneW / 2);
            float oneCenterT = (y - oneH / 2);

          /*  if (oneCenterL < 0) {
              oneCenterL = 0;
            } else*/

            if (x > (viewW - oneW / 2)) {
              oneCenterL = (viewW - oneW);
            }

           /* if (oneCenterT < 0) {
              oneCenterT = 0;
            } else */

            /*if (y > (viewH - oneH / 2)) {
              oneCenterT = (viewH - oneH);
            }*/

            sv_rocker_one.setX(oneCenterL);
            sv_rocker_one.setY(oneCenterT);
          }
        }

        // For Rocker two
        if (null != sv_rocker_two) {
          if (twoX < 0 || twoY < 0) {
            twoX = sv_rocker_two.getX();
            twoY = sv_rocker_two.getY();
          }

          if (twoW < 0 || twoH < 0) {
            twoW = sv_rocker_two.getWidth();
            twoH = sv_rocker_two.getHeight();
          }

          if (twoW > 0 && twoH > 0) {
            float twoCenterL = (x - twoW / 2);
            float twoCenterT = (y - twoH / 2);

            if (twoCenterL < 0) {
              twoCenterL = 0;
            }
           /* else if (x > (viewW - twoW / 2)) {
              twoCenterL = (viewW - twoW);
            }*/

           /* if (twoCenterT < 0) {
              twoCenterT = 0;
            }*/
           /* else if (y > (viewH - twoH / 2)) {
              twoCenterT = (viewH - twoH);
            }*/

            sv_rocker_two.setX(twoCenterL);
            sv_rocker_two.setY(twoCenterT);
          }
        }
        break;
      case MotionEvent.ACTION_POINTER_UP:
        break;
      case MotionEvent.ACTION_UP:
        // showController on default position
        goOriginPosition();
        break;
      case MotionEvent.ACTION_CANCEL:
        goOriginPosition();
        break;
    }

    if(pointerCount >= 3){
      return true;
    }
    return super.onInterceptTouchEvent(ev);
  }

  @Override
  public boolean onTouchEvent(MotionEvent event) {
    return super.onTouchEvent(event);
  }

  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b) {
    super.onLayout(changed, l, t, r, b);
    viewW = r - l;
    viewH = b - t;
    goOriginPosition();
  }

  public void resetlayout() {
    goOriginPosition();
    //invalidate();
  }

  private void goOriginPosition() {
    //LogUtil.Lee("RockerRelativeLayout  oneX " + oneX + " oneY " + oneY+"  twoX " + twoX + " twoY " + twoY);
    if (null != sv_rocker_one && oneX > 0 && oneY > 0) {
      sv_rocker_one.setX(oneX);
      sv_rocker_one.setY(oneY);
    }

    if (null != sv_rocker_two && twoX > 0 && twoY > 0) {
      sv_rocker_two.setX(twoX);
      sv_rocker_two.setY(twoY);
    }
  }
}
