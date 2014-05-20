package com.learningpod.android.activities;

import java.util.HashMap;

import com.learningpod.android.BackgroundAsyncTasks;
import com.learningpod.android.BackgroundTasks;
import com.learningpod.android.BaseActivity;
import com.learningpod.android.R;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;



public class SplashActivity extends BaseActivity {

	//Splash screen timer
	private static int SPLASH_TIME_OUT = 500;
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	         
	        new Handler().postDelayed(new Runnable() {
	        	 
	            /*
	             * Showing splash screen with a timer. This will be useful when you
	             * want to show case your app logo / company
	             */
	 
	            @Override
	            public void run() {
	            	float sp = pixelsToSp(40f);
	            	float sp1= pixelsToSp(36f);
	            	float f1 = convertPixelsToDp(96f);
	            	float f2 = convertPixelsToDp(76f);
	                // This method will be executed once the timer is over
	                // Start your app main activity
	            	HashMap<String, Object> params = new HashMap<String, Object>();
	        		params.put("selectedAccount", null);
	        		new BackgroundAsyncTasks(SplashActivity.this, params).execute(BackgroundTasks.SELECTED_ACCOUNT_AUTHENTICATION);
	 
	                // close this activity
	                
	            }
	        }, SPLASH_TIME_OUT);
	 }
	 
	 public  float pixelsToSp( Float px) {
		    float scaledDensity =  getResources().getDisplayMetrics().scaledDensity;
		    return px/scaledDensity;
		}
	 public  float convertDpToPixel(float dp){
		    Resources resources = getResources();
		    DisplayMetrics metrics = resources.getDisplayMetrics();
		    float px = dp * (metrics.densityDpi / 160f);
		    return px;
		}
	 
	 public  float convertPixelsToDp(float px){
		    Resources resources = getResources();
		    DisplayMetrics metrics = resources.getDisplayMetrics();
		    float dp = px / (metrics.densityDpi / 160f);
		    return dp;
		}
}
