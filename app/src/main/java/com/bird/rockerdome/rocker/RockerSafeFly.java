package com.bird.rockerdome.rocker;



/**
 * Created by Administrator on 2017/3/3.
 */

public class RockerSafeFly {
    boolean  leftPress=false;
    boolean  rightPress=false;
    private int rockerMode; //摇杆是什么手  0美国手   1日本手
    private RockerSafeView left,right;

    public RockerSafeFly( RockerSafeView left, RockerSafeView right){
        this.left = left;
        this.right = right;
        rockerMode = SpSetGetUtils.getRockerMode();
    }

    public void initRockerSafeFly(){
        left.setIsLeft(true,rockerMode);
        right.setIsLeft(false,rockerMode);
        left.setOnSensorLinstener(new RockerSafeView.RockerSafeListener() {
            @Override
            public void onRockerClick(int position,boolean isDown) {

            }

            @Override
            public void toggleLeftOrRight(boolean isShow) {
                leftPress=!isShow;
            }
        });
        right.setOnSensorLinstener(new RockerSafeView.RockerSafeListener() {
            @Override
            public void onRockerClick(int position,boolean isDown) {
            }

            @Override
            public void toggleLeftOrRight(boolean isShow) {
                rightPress=!isShow;
            }
        });
    }

    public void dismissRockerSafeFly(){
        reset();
    }

    public void reset(){
        if(left!=null)
            left.reset();
        if(right!=null)
            right.reset();
    }

}
