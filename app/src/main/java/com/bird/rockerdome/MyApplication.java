package com.bird.rockerdome;

import android.app.Application;
import android.content.Context;

public class MyApplication extends Application {


    public static Context getContext() {
        return context;
    }

  static   Context context;


    @Override
    public void onCreate() {
        super.onCreate();

     context = this;
    }
}
