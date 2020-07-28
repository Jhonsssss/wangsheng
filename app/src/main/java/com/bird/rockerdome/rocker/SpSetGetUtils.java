package com.bird.rockerdome.rocker;

import com.bird.rockerdome.MyApplication;

/**
 * 存储和获取SP
 */
public class SpSetGetUtils {

  public SpSetGetUtils() {

  }

  //飞行操控模式 //1是摇杆 2是安全摇杆
  public static int getoperateMode() {
    return (int) SharedPreferencesUtils.getParam(MyApplication.getContext(), Constants.SP_OPERATE_MODE,
            Constants.ROCKER_MODE);
  }
  //飞行操控模式 //1是摇杆 2是安全摇杆
  public static void setoperateMode(int  operateMode) {
    if (operateMode==1||operateMode==2)
 SharedPreferencesUtils.setParam(MyApplication.getContext(), Constants.SP_OPERATE_MODE,
           operateMode);
  }



  //摇杆是什么手  1美国手   2日本手
  public static int getRockerMode() {
    return (int) SharedPreferencesUtils.getParam(MyApplication.getContext(), Constants.SP_ROCKER_MODE, 1);
  }

  public static void setRockerMode(int i) {
    if(i == 1 || i == 2){
      SharedPreferencesUtils.setParam(MyApplication.getContext(), Constants.SP_ROCKER_MODE, i);
    }
  }

}
