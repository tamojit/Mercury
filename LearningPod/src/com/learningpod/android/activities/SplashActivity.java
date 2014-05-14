package com.learningpod.android.activities;

import java.util.HashMap;

import com.learningpod.android.BackgroundAsyncTasks;
import com.learningpod.android.BackgroundTasks;
import com.learningpod.android.BaseActivity;
import com.learningpod.android.R;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;



public class SplashActivity extends BaseActivity {

	//Splash screen timer
	private static int SPLASH_TIME_OUT = 2000;
	 @Override
	    protected void onCreate(Bundle savedInstanceState) {
	        super.onCreate(savedInstanceState);
	        LinearLayout.LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
	        ImageView splashImage = new ImageView(this);
	        splashImage.setBackgroundResource(R.drawable.app_title);
	        setContentView(splashImage);
	        new Handler().postDelayed(new Runnable() {
	        	 
	            /*
	             * Showing splash screen with a timer. This will be useful when you
	             * want to show case your app logo / company
	             */
	 
	            @Override
	            public void run() {
	                // This method will be executed once the timer is over
	                // Start your app main activity
	            	HashMap<String, Object> params = new HashMap<String, Object>();
	        		params.put("selectedAccount", null);
	        		new BackgroundAsyncTasks(SplashActivity.this, params).execute(BackgroundTasks.SELECTED_ACCOUNT_AUTHENTICATION);
	 
	                // close this activity
	                
	            }
	        }, SPLASH_TIME_OUT);
	 }
}
