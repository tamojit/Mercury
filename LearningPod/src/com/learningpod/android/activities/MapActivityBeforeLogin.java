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
import com.learningpod.android.db.LearningpodDbHandler;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.DecelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.LinearLayout.LayoutParams;
import android.content.Context;

public class MapActivityBeforeLogin extends BaseActivity implements
		OnClickListener {

	private float lastX;
	private ViewFlipper mapFlipper;
	private List<PodBean> pods = null;
	private Account[] accounts = null;
	private Typeface headerFont;
	private int selectedPlatentId;
	private boolean isPlanetClicked = false;
	private Dialog loginPopup;
	private boolean isSmallerScreen;
	public static int TYPE_WIFI = 1;
	public static int TYPE_MOBILE = 2;
	public static int TYPE_NOT_CONNECTED = 0;
	public static int CURRENT_MAP_INDEX = 0;
	int[] planetBtnArray = { R.id.planet1, R.id.planet2, R.id.planet3,
			R.id.planet4, R.id.planet5, R.id.planet6, R.id.planet7,
			R.id.planet8, R.id.planet9, R.id.planet10, R.id.planet11,
			R.id.planet12, R.id.planet13, R.id.planet14, R.id.planet15 };
	int[] planetNameArray = { R.id.planet1name, R.id.planet2name, R.id.planet3name,
			R.id.planet4name, R.id.planet5name, R.id.planet6name,
			R.id.planet7name, R.id.planet8name, R.id.planet9name,
			R.id.planet10name, R.id.planet11name, R.id.planet12name,
			R.id.planet13name, R.id.planet14name, R.id.planet15name, };

	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		headerFont = Typeface.createFromAsset(getAssets(),
				"fonts/PaytoneOne.ttf");
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		isSmallerScreen = false;
		if (metrics.heightPixels <= 800) {
			isSmallerScreen = true;
		}
		// disable the app icon and title
		getActionBar().setDisplayShowHomeEnabled(false);
		//getActionBar().setDisplayShowTitleEnabled(false);
		
		// get list of pods. getting
		pods = ContentCacheStore.getContentCache().getPods();
		setContentView(R.layout.maplayout);

		// create the view flipper
		mapFlipper = (ViewFlipper) findViewById(R.id.mapFlipper1);

		// get Inflater instance
		LayoutInflater inflater = getLayoutInflater();
		// inflate and create the Map Views
		View mapView1 = inflater.inflate(R.layout.mapview1, null);
		View mapView2 = inflater.inflate(R.layout.mapview2, null);
		View mapView3 = inflater.inflate(R.layout.mapview3, null);
		
		// initialize the map components for each screen
		initializeMaps(mapView1, 0);
		initializeMaps(mapView2, 5);
		initializeMaps(mapView3, 10);
		

		// add listeners to next and previous buttons.
		// also add animations
		findViewById(R.id.btnmapnext).setOnClickListener(this);
		findViewById(R.id.btnmapprev).setOnClickListener(this);
		((Button) findViewById(R.id.btnmapnext)).setTypeface(headerFont);
		((Button) findViewById(R.id.btnmapprev)).setTypeface(headerFont);
		
		// dont show the next and prev button if this is the first or the last
		// view
		if (CURRENT_MAP_INDEX == 0) {
			findViewById(R.id.btnmapprev).setVisibility(View.INVISIBLE);
			getActionBar().setTitle("Your Journey Begins");
		}
		
		else if(CURRENT_MAP_INDEX==1){
			getActionBar().setTitle("The Midway Planets");
		}
		else if (CURRENT_MAP_INDEX == 2) {
			findViewById(R.id.btnmapnext).setVisibility(View.INVISIBLE);
			getActionBar().setTitle("Galaxy's End");
		}

		findViewById(R.id.wordlist).setOnClickListener(this);

		// add views to the flipper
		mapFlipper.addView(mapView1, 0);
		mapFlipper.addView(mapView2, 1);
		mapFlipper.addView(mapView3, 2);
		mapFlipper.setDisplayedChild(0);
		// setContentView(mapFlipper);
	}
	
	private void initializeMaps(View mapView, int startIndex){
		
		for(int idx=startIndex;idx<startIndex+5;idx++){
			ImageButton planet =(ImageButton) mapView.findViewById(planetBtnArray[idx]);
			planet.setOnClickListener(this);		
			planet.setTag(Integer.valueOf(idx).toString());
			
			final TextView planetName = (TextView) mapView.findViewById(planetNameArray[idx]);
			planetName.setOnClickListener(this);			
			planetName.setTag(Integer.valueOf(idx).toString());
			planetName.setTypeface(headerFont);
			if(isSmallerScreen){
				planetName.setTextSize(14);
			}		
			/*planetName.setOnTouchListener(new OnTouchListener() {
				
				@Override
				public boolean onTouch(View v, MotionEvent event) {
					// TODO Auto-generated method stub
					if(event.getAction()==MotionEvent.ACTION_DOWN){
						planetName.setShadowLayer(30, 20, 20, Color.parseColor("#ffffff"));
					}
					else if(event.getAction()==MotionEvent.ACTION_UP){
						planetName.setShadowLayer(0, 0, 0, 0);
					}
					return false;
				}
			});*/
			
		}
	}

	public void LoginDialogPopUp() {

		loginPopup = new Dialog(this);
		loginPopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
		loginPopup.setContentView(R.layout.loginpopup);
		LinearLayout emailContainer = (LinearLayout) loginPopup
				.findViewById(R.id.loginmailcontainer);
		// initialize Account Manager class to fetch all configured accounts on
		// device
		AccountManager accountManager = AccountManager
				.get(getApplicationContext());
		accounts = accountManager.getAccountsByType("com.google");
		int heightInPx = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 50, getResources()
						.getDisplayMetrics());
		LinearLayout.LayoutParams textViewParams = new LayoutParams(
				LayoutParams.MATCH_PARENT, heightInPx);
		textViewParams.gravity = Gravity.CENTER;
		textViewParams.setMargins((int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 20, getResources()
						.getDisplayMetrics()), (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 10, getResources()
						.getDisplayMetrics()), (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 20, getResources()
						.getDisplayMetrics()), (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 10, getResources()
						.getDisplayMetrics()));

		for (int idx = 0; idx < accounts.length; idx++) {
			Account account = accounts[idx];
			TextView emailView = new TextView(this);
			emailView.setTag("email");
			emailView.setId(idx);
			emailView.setLayoutParams(textViewParams);
			emailView.setTextSize(20);
			emailView.setTextColor(Color.parseColor("#ffffff"));
			emailView.setText(account.name);
			emailView.setBackgroundResource(R.drawable.email_name);
			emailView.setGravity(Gravity.CENTER);
			emailView.setOnClickListener(this);
			emailContainer.addView(emailView);

			loginPopup.show();
		}
	}

	// Method to handle touch event like left to right swap and right to left
	// swap
	public boolean onTouchEvent(MotionEvent touchevent) {

		switch (touchevent.getAction()) {
		// when user first touches the screen to swap
		case MotionEvent.ACTION_DOWN: {
			lastX = touchevent.getX();
			break;
		}
		case MotionEvent.ACTION_UP: {
			float currentX = touchevent.getX();

			// if left to right swipe on screen
			if (lastX < currentX) {
				// If no more View/Child to flip
				if (mapFlipper.getDisplayedChild() == 0)
					break;

				// set the required Animation type to mapFlipper
				// The Next screen will come in form Left and current Screen
				// will go OUT from Right
				mapFlipper.setInAnimation(this, R.anim.in_from_left);
				mapFlipper.setOutAnimation(this, R.anim.out_to_right);
				CURRENT_MAP_INDEX--;
				if (CURRENT_MAP_INDEX == 0) {
					//findViewById(R.id.btnmapprev).setVisibility(View.GONE);
					//findViewById(R.id.btnmapnext).setVisibility(View.VISIBLE);
					fadeOutAnimation(findViewById(R.id.btnmapprev), 500);					
					fadeInAnimation(findViewById(R.id.btnmapnext), 500);
					getActionBar().setTitle("Your Journey Begins");
				} else {
					//findViewById(R.id.btnmapnext).setVisibility(View.VISIBLE);
					//findViewById(R.id.btnmapprev).setVisibility(View.VISIBLE);
					fadeInAnimation(findViewById(R.id.btnmapnext), 500);
					fadeInAnimation(findViewById(R.id.btnmapprev), 500);
					if(CURRENT_MAP_INDEX==1){
						getActionBar().setTitle("The Midway Planets");
					}else{
						getActionBar().setTitle("Galaxy's End");
					}
				}
				// Show the previous Screen
				mapFlipper.showPrevious();
			}

			// if right to left swipe on screen
			if (lastX > currentX) {
				if (mapFlipper.getDisplayedChild() == 2)
					break;
				// set the required Animation type to mapFlipper
				// The Next screen will come in form Right and current Screen
				// will go OUT from Left
				mapFlipper.setInAnimation(this, R.anim.in_from_right);
				mapFlipper.setOutAnimation(this, R.anim.out_to_left);
				CURRENT_MAP_INDEX++;
				if (CURRENT_MAP_INDEX == 2) {
					//findViewById(R.id.btnmapnext).setVisibility(View.GONE);
					//findViewById(R.id.btnmapprev).setVisibility(View.VISIBLE);
					fadeOutAnimation(findViewById(R.id.btnmapnext), 500);
					fadeInAnimation(findViewById(R.id.btnmapprev), 500);
					getActionBar().setTitle("Galaxy's End");
				} else {
					//findViewById(R.id.btnmapnext).setVisibility(View.VISIBLE);
					//findViewById(R.id.btnmapprev).setVisibility(View.VISIBLE);
					fadeInAnimation(findViewById(R.id.btnmapnext), 500);
					fadeInAnimation(findViewById(R.id.btnmapprev), 500);
					if(CURRENT_MAP_INDEX==1){
						getActionBar().setTitle("The Midway Planets");
					}else{
						getActionBar().setTitle("Your Journey Begins");
					}
				}
				// Show The next Screen
				mapFlipper.showNext();
			}
			break;
		}
		}
		return false;
	}

	// checking for connectivity

	public static int getConnectivityStatus(Context context) {
		ConnectivityManager cm = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);

		NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
		if (null != activeNetwork) {
			if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
				return TYPE_WIFI;

			if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
				return TYPE_MOBILE;
		}
		return TYPE_NOT_CONNECTED;
	}

	// dialog to show in case of no internet connection

	public void showCustomDialogForNoInternet() {
		// TODO Auto-generated method stub
		final Dialog dialog = new Dialog(MapActivityBeforeLogin.this);
		dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
		dialog.setContentView(R.layout.network_connectivity_alert);

	//	final EditText editText = (EditText) dialog
	//			.findViewById(R.id.editText1);

		Button button = (Button) dialog.findViewById(R.id.button1);
		button.setOnClickListener(new View.OnClickListener() {

			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub

				dialog.dismiss();
			}
		});

		dialog.show();
	}

	public void onClick(View v) {
		// TODO Auto-generated method stub
		// go to login screen

		if (v.getId() == R.id.btnmapnext) {
			// set the required Animation type to mapFlipper
			// The Next screen will come in form Right and current Screen will
			// go OUT from Left
			mapFlipper.setInAnimation(this, R.anim.in_from_right);
			mapFlipper.setOutAnimation(this, R.anim.out_to_left);
			// increment the view index;
			CURRENT_MAP_INDEX++;
			if (CURRENT_MAP_INDEX == 2) {
				//findViewById(R.id.btnmapnext).setVisibility(View.GONE);				
				//findViewById(R.id.btnmapprev).setVisibility(View.VISIBLE);
				fadeOutAnimation(findViewById(R.id.btnmapnext), 500);
				fadeInAnimation(findViewById(R.id.btnmapprev), 500);
				getActionBar().setTitle("Galaxy's End");
			} else {
				//findViewById(R.id.btnmapnext).setVisibility(View.VISIBLE);
				//findViewById(R.id.btnmapprev).setVisibility(View.VISIBLE);
				fadeInAnimation(findViewById(R.id.btnmapnext), 500);
				fadeInAnimation(findViewById(R.id.btnmapprev), 500);
				if(CURRENT_MAP_INDEX==0){
					getActionBar().setTitle("Your Journey Begins");
				}else{
					getActionBar().setTitle("The Midway Planets");
				}
			}
			// Show The Previous Screen
			mapFlipper.showNext();
		} else if (v.getId() == R.id.btnmapprev) {

			// set the required Animation type to mapFlipper
			// The Next screen will come in form Left and current Screen will go
			// OUT from Right
			mapFlipper.setInAnimation(this, R.anim.in_from_left);
			mapFlipper.setOutAnimation(this, R.anim.out_to_right);
			// increment the view index;
			CURRENT_MAP_INDEX--;
			if (CURRENT_MAP_INDEX == 0) {
				//findViewById(R.id.btnmapprev).setVisibility(View.INVISIBLE);
				//findViewById(R.id.btnmapnext).setVisibility(View.VISIBLE);
				fadeInAnimation(findViewById(R.id.btnmapnext), 500);
				fadeOutAnimation(findViewById(R.id.btnmapprev), 500);
				getActionBar().setTitle("Your Journey Begins");
			} else {
				//findViewById(R.id.btnmapnext).setVisibility(View.VISIBLE);
				//findViewById(R.id.btnmapprev).setVisibility(View.VISIBLE);
				fadeInAnimation(findViewById(R.id.btnmapnext), 500);
				fadeInAnimation(findViewById(R.id.btnmapprev), 500);
				if(CURRENT_MAP_INDEX==1){
					getActionBar().setTitle("The Midway Planets");
				}else{
					getActionBar().setTitle("Galaxy's End");
				}
			}
			// Show the previous Screen
			mapFlipper.showPrevious();
		} else if (v.getId() == R.id.wordlist) {

			Intent i = new Intent(this, WordListActivity.class);
			startActivity(i);

		} else if (v instanceof ImageButton) {

			// go to login screen
			selectedPlatentId = Integer.parseInt(v.getTag().toString());
			isPlanetClicked = true;
			// showLoginWindow();
			LoginDialogPopUp();
		} else if (v instanceof TextView) {
			// if account email id text has been clicked					
			if (v.getTag().toString().equalsIgnoreCase("email")) {
				// dismiss the login dialog	
				loginPopup.dismiss();
				if (getConnectivityStatus(this) == TYPE_NOT_CONNECTED) {
					showCustomDialogForNoInternet();
					return;
				}
				// account has been selected
				Account selectedAccount = accounts[v.getId()];
				HashMap<String, Object> params = new HashMap<String, Object>();
				// store the clicked planet's id for directly going to the pod
				// screen
				if (isPlanetClicked) {
					params.put("selectedPlanet",
							Integer.valueOf(selectedPlatentId - 1));
				}
				// store the logged in user in shared preference
				SharedPreferences loginPreferences = getSharedPreferences(
						"Login", Context.MODE_PRIVATE);
				Editor editor = loginPreferences.edit();
				editor.putString("loggeduser", selectedAccount.name);
				editor.commit();
				params.put("selectedAccount", selectedAccount);
				ContentCacheStore.getContentCache().setCurrentUserEmailId(
						selectedAccount.name);
				// clear the non shown maps from the flipper to release the memory
				View currentMapView = mapFlipper.getCurrentView();
				mapFlipper.setInAnimation(null);
				mapFlipper.setOutAnimation(null);
				mapFlipper.removeAllViews();
				mapFlipper.addView(currentMapView);
				new BackgroundAsyncTasks(MapActivityBeforeLogin.this, params)
						.execute(BackgroundTasks.SELECTED_ACCOUNT_AUTHENTICATION);
			}
			// if planet text has been clicked. absence
			else {
				selectedPlatentId = Integer.parseInt(v.getTag().toString());
				isPlanetClicked = true;				
				// showLoginWindow();
				LoginDialogPopUp();
			}
		}

	}
	
	private void fadeInAnimation(final View view, long duration ){
		if(view.getVisibility()==View.VISIBLE) return;
		Animation fadeIn = new AlphaAnimation(0, 1);
	    fadeIn.setInterpolator(new DecelerateInterpolator()); // add this
	    fadeIn.setDuration(duration);
	    fadeIn.setFillAfter(false);
	    fadeIn.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				view.setVisibility(View.VISIBLE);
				view.clearAnimation();
			}
		});
	    view.startAnimation(fadeIn);
	}

	private void fadeOutAnimation(final View view, long duration ){
		if(view.getVisibility()==View.INVISIBLE) return;
		Animation fadeOut = new AlphaAnimation(1, 0);
		fadeOut.setInterpolator(new DecelerateInterpolator()); // add this
		fadeOut.setDuration(duration);
		fadeOut.setFillAfter(false);
		fadeOut.setAnimationListener(new AnimationListener() {
			
			@Override
			public void onAnimationStart(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationRepeat(Animation animation) {
				// TODO Auto-generated method stub
				
			}
			
			@Override
			public void onAnimationEnd(Animation animation) {
				// TODO Auto-generated method stub
				view.setVisibility(View.INVISIBLE);
				view.clearAnimation();
			}
		});
	    view.startAnimation(fadeOut);
	}

}
