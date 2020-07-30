package com.bird.rockerdome;

import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.View;

import com.bird.rockerdome.rocker.Constants;
import com.bird.rockerdome.rocker.RockerFly;
import com.bird.rockerdome.rocker.RockerRelativeLayout;
import com.bird.rockerdome.rocker.RockerSafeFly;
import com.bird.rockerdome.rocker.RockerSafeView;
import com.bird.rockerdome.rocker.RockerView;
import com.bird.rockerdome.rocker.SpSetGetUtils;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.Group;

public class RockerActivity extends AppCompatActivity {

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.layout_rocker);
        int op = SpSetGetUtils.getoperateMode();
        group_rocker = findViewById(R.id.group_rocker);
        group_safe_rocker = findViewById(R.id.group_safe_rocker);
        if (op == Constants.SAFETY_ROCKER_MODE) {
            intiSafeRocker();
        } else {
            initRocker();
        }

    }

    Group group_rocker, group_safe_rocker;

    private void intiSafeRocker() {

        RockerSafeView rockerSafeView1 = findViewById(R.id.rockerSafeView1);
        RockerSafeView rockerSafeView2 = findViewById(R.id.rockerSafeView2);
        rockerSafeFly = new RockerSafeFly(rockerSafeView1, rockerSafeView2);
        rockerSafeFly.initRockerSafeFly();
        group_rocker.setVisibility(View.GONE);
        group_safe_rocker.setVisibility(View.VISIBLE);
    }

    RockerSafeFly rockerSafeFly;
    RockerFly mRockerFly;

    private void initRocker() {
        RockerRelativeLayout rrlThrottleLeft = findViewById(R.id.rrl_throttle_left);
        RockerRelativeLayout rrlThrottleRight = findViewById(R.id.rrl_throttle_right);
        RockerView rvThrottleLeft = findViewById(R.id.rv_throttle_left);
        RockerView rvThrottleRight = findViewById(R.id.rv_throttle_right);
        //摇杆飞行
        mRockerFly = new RockerFly(rrlThrottleLeft, rrlThrottleRight,
                rvThrottleLeft, rvThrottleRight);
        mRockerFly.initRockerFly();
        group_rocker.setVisibility(View.VISIBLE);
        group_safe_rocker.setVisibility(View.GONE);
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mRockerFly != null) {
            mRockerFly.dismissRockerFly();
        }
        if (rockerSafeFly != null) {
            rockerSafeFly.dismissRockerSafeFly();
        }

    }
}
