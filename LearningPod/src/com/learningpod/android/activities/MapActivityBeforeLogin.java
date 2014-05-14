package com.learningpod.android.activities;

import java.util.HashMap;
import java.util.List;

import com.learningpod.android.BackgroundAsyncTasks;
import com.learningpod.android.BackgroundTasks;
import com.learningpod.android.BaseActivity;
import com.learningpod.android.ContentCacheStore;
import com.learningpod.android.R;
import com.learningpod.android.beans.UserProfileBean;
import com.learningpod.android.beans.pods.PodBean;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.Activity;
import android.graphics.Color;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.LinearLayout.LayoutParams;

public class MapActivityBeforeLogin extends BaseActivity implements OnClickListener{

	private float lastX;
	private ViewFlipper mapFlipper;
	private List<PodBean> pods = null;
	private PopupWindow loginWindow;
	private Account[] accounts = null;
	
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// get user profile and pods from content cache
		UserProfileBean userProfileBean = ContentCacheStore.getContentCache().getLoggedInUserProfile();		
		//get list of pods. getting 
		pods  = ContentCacheStore.getContentCache().getPods();		
		setContentView(R.layout.home_screen);
		// create the view flipper
		mapFlipper = new ViewFlipper(this);
		LayoutParams mapParams = new LayoutParams(LayoutParams.MATCH_PARENT,LayoutParams.MATCH_PARENT);
		mapFlipper.setLayoutParams(mapParams);
		
		// get Inflater instance
		LayoutInflater inflater = getLayoutInflater();
		// inflate and create the Map Views
		View mapView1 = inflater.inflate(R.layout.mapview1, null);		
		View mapView2 = inflater.inflate(R.layout.mapview2, null);
		View mapView3 = inflater.inflate(R.layout.mapview3, null);
		
		((ImageButton)mapView1.findViewById(R.id.planet1)).setOnClickListener(this);
		((ImageButton)mapView1.findViewById(R.id.planet2)).setOnClickListener(this);
		((ImageButton)mapView1.findViewById(R.id.planet3)).setOnClickListener(this);
		((ImageButton)mapView1.findViewById(R.id.planet4)).setOnClickListener(this);
		((ImageButton)mapView1.findViewById(R.id.planet5)).setOnClickListener(this);
		
		((ImageButton)mapView2.findViewById(R.id.planet6)).setOnClickListener(this);
		((ImageButton)mapView2.findViewById(R.id.planet7)).setOnClickListener(this);
		((ImageButton)mapView2.findViewById(R.id.planet8)).setOnClickListener(this);
		((ImageButton)mapView2.findViewById(R.id.planet9)).setOnClickListener(this);
		((ImageButton)mapView2.findViewById(R.id.planet10)).setOnClickListener(this);
		
		((ImageButton)mapView3.findViewById(R.id.planet11)).setOnClickListener(this);
		((ImageButton)mapView3.findViewById(R.id.planet12)).setOnClickListener(this);
		((ImageButton)mapView3.findViewById(R.id.planet13)).setOnClickListener(this);
		((ImageButton)mapView3.findViewById(R.id.planet14)).setOnClickListener(this);
		((ImageButton)mapView3.findViewById(R.id.planet15)).setOnClickListener(this);
		
		// add listeners to next and previous buttons
		mapView1.findViewById(R.id.btnmap1next).setOnClickListener(this);
		mapView2.findViewById(R.id.btnmap2next).setOnClickListener(this);
		mapView2.findViewById(R.id.btnmap2prev).setOnClickListener(this);
		mapView3.findViewById(R.id.btnmap3prev).setOnClickListener(this);
				
		
		// add views to the flipper
		mapFlipper.addView(mapView1,0);		
		mapFlipper.addView(mapView2,1);
		mapFlipper.addView(mapView3,2);
		setContentView(mapFlipper);
		initPopup();
		
	}
	
	private void initPopup(){
		loginWindow = new PopupWindow( LayoutParams.WRAP_CONTENT,
				LayoutParams.WRAP_CONTENT);
		View loginWindowView = getLayoutInflater().inflate(R.layout.loginpopup, null);
		// get email container
		LinearLayout emailContainer = (LinearLayout)loginWindowView.findViewById(R.id.loginmailcontainer);
		// initialize Account Manager class to fetch all configured accounts on device
		AccountManager accountManager = AccountManager.get(getApplicationContext());
		accounts = accountManager.getAccountsByType("com.google");
		int heightInPx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50, getResources().getDisplayMetrics());
		LinearLayout.LayoutParams textViewParams = new LayoutParams(LayoutParams.MATCH_PARENT,heightInPx);
		textViewParams.gravity = Gravity.CENTER;
		textViewParams.setMargins(
				(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()),
				(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()),
				(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 20, getResources().getDisplayMetrics()),
				(int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
		
		for(int idx=0;idx<accounts.length;idx++){
			Account account  = accounts[idx];
			TextView emailView = new TextView(this);
			emailView.setId(idx);
			emailView.setLayoutParams(textViewParams);
			emailView.setTextSize(20);
			emailView.setTextColor(Color.parseColor("#ffffff"));
			emailView.setText(account.name);
			emailView.setBackgroundResource(R.drawable.email_name);
			emailView.setGravity(Gravity.CENTER);
			emailView.setOnClickListener(this);
			emailContainer.addView(emailView);
		}
		loginWindow.setContentView(loginWindowView);
	}
	
	// Method to handle touch event like left to right swap and right to left swap
    public boolean onTouchEvent(MotionEvent touchevent) 
    {
		if(loginWindow.isShowing()){
			loginWindow.dismiss();
			return true;
		}
         switch (touchevent.getAction())
         {
                // when user first touches the screen to swap
                 case MotionEvent.ACTION_DOWN: 
                 {
                     lastX = touchevent.getX();
                     break;
                }
                 case MotionEvent.ACTION_UP: 
                 {
                     float currentX = touchevent.getX();
                     
                     // if left to right swipe on screen
                     if (lastX < currentX) 
                     {
                          // If no more View/Child to flip
                         if (mapFlipper.getDisplayedChild() == 0)
                             break;
                         
                         // set the required Animation type to mapFlipper
                         // The Next screen will come in form Left and current Screen will go OUT from Right 
                         mapFlipper.setInAnimation(this, R.anim.in_from_left);
                         mapFlipper.setOutAnimation(this, R.anim.out_to_right);
                         // Show the previous Screen
                         mapFlipper.showPrevious();
                     }
                     
                     // if right to left swipe on screen
                     if (lastX > currentX)
                     {
                         if (mapFlipper.getDisplayedChild() == 2)
                             break;
                         // set the required Animation type to mapFlipper
                         // The Next screen will come in form Right and current Screen will go OUT from Left 
                         mapFlipper.setInAnimation(this, R.anim.in_from_right);
                         mapFlipper.setOutAnimation(this, R.anim.out_to_left);
                         // Show The next Screen
                         mapFlipper.showNext();
                     }
                     break;
                 }
         }
                 return false;
    }

	@Override
	public void onClick(View v) {
		// TODO Auto-generated method stub
		// go to login screen
		if(v instanceof ImageButton){
			loginWindow.showAtLocation(mapFlipper, Gravity.CENTER, 0, 0);
		}
		else if (v.getId()==R.id.btnmap1next || v.getId()==R.id.btnmap2next){
			 // set the required Animation type to mapFlipper
           // The Next screen will come in form Right and current Screen will go OUT from Left 
           mapFlipper.setInAnimation(this, R.anim.in_from_right);
           mapFlipper.setOutAnimation(this, R.anim.out_to_left);
           // Show The Previous Screen
           mapFlipper.showNext();
		}
		else if (v.getId()==R.id.btnmap2prev || v.getId()==R.id.btnmap3prev){
			            
           // set the required Animation type to mapFlipper
           // The Next screen will come in form Left and current Screen will go OUT from Right 
           mapFlipper.setInAnimation(this, R.anim.in_from_left);
           mapFlipper.setOutAnimation(this, R.anim.out_to_right);
           // Show the previous Screen
           mapFlipper.showPrevious();
		}
		else if(v instanceof TextView){
		 
			Account selectedAccount = accounts[v.getId()];
			HashMap<String, Object> params = new HashMap<String, Object>();
			params.put("selectedAccount", selectedAccount);
			new BackgroundAsyncTasks(MapActivityBeforeLogin.this, params).execute(BackgroundTasks.SELECTED_ACCOUNT_AUTHENTICATION);
			
		}
	}
}
//bbbbb