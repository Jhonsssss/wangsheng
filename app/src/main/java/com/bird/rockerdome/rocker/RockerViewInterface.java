package com.bird.rockerdome.rocker;

import android.view.View;

public interface RockerViewInterface {  
	/**
	 * callback after finish rotation
	 * @param x -1 ~ 1
	 * @param y -1 ~ 1
	 * @param angle -180 ~ 180
	 */
	public void onRockerChanged(View view, float x, float y, float angle);
}