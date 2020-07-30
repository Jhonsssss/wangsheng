package com.bird.rockerdome.rocker;

import android.view.View;

import com.bird.rockerdome.R;


/**
 * Created by chengbin on 2016/7/15.
 * 虚拟遥控飞行
 * 安全摇杆
 */
public class RockerFly {
  boolean  leftPress=false;
  boolean  rightPress=false;
  private int                  rockerMode; //摇杆是什么手  0美国手   1日本手
  private boolean              isFlyOritentionFan; //摄像头朝向
  private RockerRelativeLayout rrlThrottleLeft;
  private RockerRelativeLayout rrlThrottleRight;
  private RockerView rvThrottleLeft;
  private RockerView           rvThrottleRight;
  public static float mi = 2f;

  /**
   * @param rrlThrottleLeft 左侧遥控范围
   * @param rrlThrottleRight 右侧遥控范围
   * @param rvThrottleLeft 左侧摇杆
   * @param rvThrottleRight 右侧摇杆
     */
  public RockerFly(
                   RockerRelativeLayout rrlThrottleLeft, RockerRelativeLayout rrlThrottleRight,
                   RockerView rvThrottleLeft, RockerView rvThrottleRight) {
    rockerMode = SpSetGetUtils.getRockerMode();
//    isFlyOritentionFan = SpSetGetUtils.getIsFlyOritentionFan();
    this.rrlThrottleLeft = rrlThrottleLeft;
    this.rrlThrottleRight = rrlThrottleRight;
    this.rvThrottleLeft = rvThrottleLeft;
    this.rvThrottleRight = rvThrottleRight;
    initListener();
  }

  private void initListener() {
    /**左侧摇杆显示/隐藏监听*/
    rvThrottleLeft.setOnSensorLinstener(new RockerView.RockerViewListener(){
      @Override
      public void toggleLeftOrRight(boolean isShow) {
        leftPress=!isShow;
//        if(isShow){
//          mCameraActivity.showCameraLeftControll();
//        }else{
//          mCameraActivity.hideCameraLeftControll();
//        }
      }
    });
    /**右侧摇杆显示/隐藏监听*/
    rvThrottleRight.setOnSensorLinstener(new RockerView.RockerViewListener(){
      @Override
      public void toggleLeftOrRight(boolean isShow) {
        rightPress=!isShow;
//        if(isShow){
//          mCameraActivity.showCameraRightControll();
//        }else{
//          mCameraActivity.hideCameraRightControll();
//        }
      }
    });
  }

  public void initRockerFly(boolean  reLayout) {
    initRocker(rvThrottleLeft, rvThrottleRight,reLayout);
  }

  //油门
  private void initRocker(RockerView mSvRocker1, RockerView mSvRocker2,boolean  reLayout ) {
    if (rockerMode == Constants.ROCKER_LEFT_THROTTLE) {//左手油门（美国手）
      mSvRocker1.initView(R.mipmap.rocker_left_throttle_left,
              R.mipmap.rocker_point,
              R.mipmap.rocker_point,reLayout);
      mSvRocker1.setOnRockerChanged(new RockerViewInterface() {
        @Override
        public void onRockerChanged(View view, float x, float y, float angle) {
          //xuanZhuan = x; // 左右方向  shangXia = y;// 上下升降
          x = getX(x, angle);
          y = -getY(y, angle);

        }
      });

      mSvRocker2.initView(R.mipmap.rocker_left_throttle_right,
              R.mipmap.rocker_point,
              R.mipmap.rocker_point,reLayout);
      mSvRocker2.setOnRockerChanged(new RockerViewInterface() {
        @Override
        public void onRockerChanged(View view, float x, float y, float angle) {
          //zuoYou = x;// 左右副翼   qianHou = y;// 上下油门
          x = getXOrY(x);
          y = -getXOrY(y);
        }
      });
    } else if (rockerMode ==  Constants.ROCKER_RIGHT_THROTTLE) {//右手油门（日本手）

      mSvRocker1.initView(R.mipmap.rocker_right_throttle_left,
              R.mipmap.rocker_point,
              R.mipmap.rocker_point,reLayout);
      mSvRocker1.setOnRockerChanged(new RockerViewInterface() {
        @Override
        public void onRockerChanged(View view, float x, float y, float angle) {
          // xuanZhuan = x; // 左右方向  qianHou = y;// 上下升降

          x = getXOrY(x);
          y = -getXOrY(y);
        }
      });

      mSvRocker2.initView(R.mipmap.rocker_right_throttle_right,
              R.mipmap.rocker_point,
              R.mipmap.rocker_point,reLayout);
      mSvRocker2.setOnRockerChanged(new RockerViewInterface() {
        @Override
        public void onRockerChanged(View view, float x, float y, float angle) {
          //zuoYou = x;// 左右副翼  shangXia = y;// 上下油门

          x = getX(x, angle);
          y = -getY(y, angle);
        }
      });
    }
  }

  public void dismissRockerFly() {
//    Constants.RC_CMD_MODE = 0;
    resetPoint();
    if (null != rrlThrottleLeft) {
      rrlThrottleLeft.resetlayout();
    }
    if (null != rrlThrottleRight) {
      rrlThrottleRight.resetlayout();
    }
//    myApplication.uavParaGetAndSendManager.resetRocker(1500, 1500, 1500, 1500);
  }

  public void resetPoint() {
    if (null != rvThrottleLeft) {
      rvThrottleLeft.reset();
    }
    if (null != rvThrottleRight) {
      rvThrottleRight.reset();
    }
//    myApplication.uavParaGetAndSendManager.resetRocker(1500, 1500, 1500, 1500);
  }

  private int toIntCmd(float f) {
    return (int) (f * 1000);
  }

  private float getX(float x, float angle) {
    if ((Math.abs(angle) > 0 && Math.abs(angle) < 10) || ((180 - Math.abs(angle)) > 0
        && (180 - Math.abs(angle)) < 10)) {
      x = 0;
      return x;
    }
    return getXOrY(x);
  }

  private float getY(float y, float angle) {
    if ((Math.abs(angle) > 80 && Math.abs(angle) < 100)) {
      y = 0;
      return y;
    }
    return getXOrY(y);
  }

  private float getXOrY(float z) {
    if (Math.abs(z) <= 0.05) {
      z = 0f;
      return z;
    }

    if (z > 0) {
      z = (float) Math.pow(z, mi);
    } else if (z < 0) {
      z = -(float) Math.pow(-z, mi);
    }
    return z;
  }

  public void reset(){
    if(rvThrottleLeft!=null) {
      rvThrottleLeft.reset();
      rvThrottleLeft.setAlpha(0.3f);
    }

    if(rvThrottleRight!=null) {
      rvThrottleRight.reset();
      rvThrottleRight.setAlpha(0.3f);
    }
  }

}
