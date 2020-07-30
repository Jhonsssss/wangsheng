package com.bird.rockerdome.rocker;



/**
 */

public class RockerSafeFly {
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
            }
        });
        right.setOnSensorLinstener(new RockerSafeView.RockerSafeListener() {
            @Override
            public void onRockerClick(int position,boolean isDown) {
            }

            @Override
            public void toggleLeftOrRight(boolean isShow) {
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
