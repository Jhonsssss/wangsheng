package com.bird.rockerdome;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.bird.rockerdome.rocker.Constants;
import com.bird.rockerdome.rocker.SpSetGetUtils;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }
    private void initView() {
    findViewById(R.id.button).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SpSetGetUtils.setoperateMode(Constants.ROCKER_MODE);
         startActivity(new Intent(MainActivity.this,RockerActivity.class));
         overridePendingTransition(0,0);
        }
    });
    findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            SpSetGetUtils.setoperateMode(Constants.SAFETY_ROCKER_MODE);
         startActivity(new Intent(MainActivity.this,RockerActivity.class));
         overridePendingTransition(0,0);
        }
    });



       int mode =     SpSetGetUtils.getRockerMode();
        RadioGroup radio_group =findViewById(R.id.radio_group);
        radio_group.setOnCheckedChangeListener(new OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int checkedId) {

                if (checkedId==R.id.radioButton3){
                    SpSetGetUtils.setRockerMode(Constants.ROCKER_USA );

                }else if (checkedId==R.id.radioButton4){
                    SpSetGetUtils.setRockerMode(Constants.ROCKER_JAPAN );
                }

            }
        });
        radio_group.check(mode== Constants.ROCKER_USA ?R.id.radioButton3:R.id.radioButton4);

    }
}