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
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.ViewFlipper;
import android.widget.LinearLayout.LayoutParams;
import android.content.Context;
public class MapActivityBeforeLogin extends BaseActivity implements OnClickListener{

	private float lastX;
	private ViewFlipper mapFlipper;
	private List<PodBean> pods = null;
	
	private Account[] accounts = null;
	private Typeface headerFont;
	private int selectedPlatentId;
	private boolean isPlanetClicked=false;
	final Context context = this;
	private Dialog loginpopup;
	public static int TYPE_WIFI = 1;
    public static int TYPE_MOBILE = 2;
    public static int TYPE_NOT_CONNECTED = 0;
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		headerFont = Typeface.createFromAsset(getAssets(),
				"fonts/PaytoneOne.ttf");
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
		((ImageButton)mapView1.findViewById(R.id.planet1)).setTag(new Integer(1).toString());
		((ImageButton)mapView1.findViewById(R.id.planet2)).setOnClickListener(this);
		((ImageButton)mapView1.findViewById(R.id.planet2)).setTag(new Integer(2).toString());
		((ImageButton)mapView1.findViewById(R.id.planet3)).setOnClickListener(this);
		((ImageButton)mapView1.findViewById(R.id.planet3)).setTag(new Integer(3).toString());
		((ImageButton)mapView1.findViewById(R.id.planet4)).setOnClickListener(this);
		((ImageButton)mapView1.findViewById(R.id.planet4)).setTag(new Integer(4).toString());
		((ImageButton)mapView1.findViewById(R.id.planet5)).setOnClickListener(this);
		((ImageButton)mapView1.findViewById(R.id.planet5)).setTag(new Integer(5).toString());
		
		((ImageButton)mapView2.findViewById(R.id.planet6)).setOnClickListener(this);
		((ImageButton)mapView2.findViewById(R.id.planet6)).setTag(new Integer(6).toString());
		((ImageButton)mapView2.findViewById(R.id.planet7)).setOnClickListener(this);
		((ImageButton)mapView2.findViewById(R.id.planet7)).setTag(new Integer(7).toString());
		((ImageButton)mapView2.findViewById(R.id.planet8)).setOnClickListener(this);
		((ImageButton)mapView2.findViewById(R.id.planet8)).setTag(new Integer(8).toString());
		((ImageButton)mapView2.findViewById(R.id.planet9)).setOnClickListener(this);
		((ImageButton)mapView2.findViewById(R.id.planet9)).setTag(new Integer(9).toString());
		((ImageButton)mapView2.findViewById(R.id.planet10)).setOnClickListener(this);
		((ImageButton)mapView2.findViewById(R.id.planet10)).setTag(new Integer(10).toString());
		
		((ImageButton)mapView3.findViewById(R.id.planet11)).setOnClickListener(this);
		((ImageButton)mapView3.findViewById(R.id.planet11)).setTag(new Integer(11).toString());		
		((ImageButton)mapView3.findViewById(R.id.planet12)).setOnClickListener(this);
		((ImageButton)mapView3.findViewById(R.id.planet12)).setTag(new Integer(12).toString());
		((ImageButton)mapView3.findViewById(R.id.planet13)).setOnClickListener(this);
		((ImageButton)mapView3.findViewById(R.id.planet13)).setTag(new Integer(13).toString());
		((ImageButton)mapView3.findViewById(R.id.planet14)).setOnClickListener(this);
		((ImageButton)mapView3.findViewById(R.id.planet14)).setTag(new Integer(14).toString());
		((ImageButton)mapView3.findViewById(R.id.planet15)).setOnClickListener(this);
		((ImageButton)mapView3.findViewById(R.id.planet15)).setTag(new Integer(15).toString());
		
		
		((TextView)mapView1.findViewById(R.id.planet1name)).setTypeface(headerFont);
		((TextView)mapView1.findViewById(R.id.planet2name)).setTypeface(headerFont);
		((TextView)mapView1.findViewById(R.id.planet3name)).setTypeface(headerFont);
		((TextView)mapView1.findViewById(R.id.planet4name)).setTypeface(headerFont);
		((TextView)mapView1.findViewById(R.id.planet5name)).setTypeface(headerFont);
		
		((TextView)mapView2.findViewById(R.id.planet6name)).setTypeface(headerFont);
		((TextView)mapView2.findViewById(R.id.planet7name)).setTypeface(headerFont);
		((TextView)mapView2.findViewById(R.id.planet8name)).setTypeface(headerFont);
		((TextView)mapView2.findViewById(R.id.planet9name)).setTypeface(headerFont);
		((TextView)mapView2.findViewById(R.id.planet10name)).setTypeface(headerFont);
		
		((TextView)mapView3.findViewById(R.id.planet11name)).setTypeface(headerFont);
		((TextView)mapView3.findViewById(R.id.planet12name)).setTypeface(headerFont);
		((TextView)mapView3.findViewById(R.id.planet13name)).setTypeface(headerFont);
		((TextView)mapView3.findViewById(R.id.planet14name)).setTypeface(headerFont);
		((TextView)mapView3.findViewById(R.id.planet15name)).setTypeface(headerFont);
		
		// add listeners to next and previous buttons
		mapView1.findViewById(R.id.btnmap1next).setOnClickListener(this);
		mapView2.findViewById(R.id.btnmap2next).setOnClickListener(this);
		mapView2.findViewById(R.id.btnmap2prev).setOnClickListener(this);
		mapView3.findViewById(R.id.btnmap3prev).setOnClickListener(this);
		
		
		((Button)mapView1.findViewById(R.id.btnmap1next)).setTypeface(headerFont);
		((Button)mapView2.findViewById(R.id.btnmap2next)).setTypeface(headerFont);
		((Button)mapView2.findViewById(R.id.btnmap2prev)).setTypeface(headerFont);
		((Button)mapView3.findViewById(R.id.btnmap3prev)).setTypeface(headerFont);

		mapView1.findViewById(R.id.wordlist).setOnClickListener(this);
		mapView2.findViewById(R.id.wordlist2).setOnClickListener(this);
		mapView3.findViewById(R.id.wordlist3).setOnClickListener(this);		
  	
		// add views to the flipper
		mapFlipper.addView(mapView1,0);		
		mapFlipper.addView(mapView2,1);
		mapFlipper.addView(mapView3,2);
		setContentView(mapFlipper);	
	   }
	
	     public void LoginDialogPopUp() {

		 loginpopup = new Dialog(context);
		 loginpopup.requestWindowFeature(Window.FEATURE_NO_TITLE);
		 loginpopup.setContentView(R.layout.loginpopup);
	     LinearLayout emailContainer = (LinearLayout)loginpopup.findViewById(R.id.loginmailcontainer);
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
	
				loginpopup.show();
      }	
	}
	
	
	// Method to handle touch event like left to right swap and right to left swap
    public boolean onTouchEvent(MotionEvent touchevent) 
    {

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
			selectedPlatentId = Integer.parseInt(v.getTag().toString());
			isPlanetClicked = true;
			//showLoginWindow();
			
			LoginDialogPopUp();
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
		else if (v.getId()==R.id.wordlist || v.getId()==R.id.wordlist2 || v.getId()==R.id.wordlist3){
            
	           // set the required Animation type to mapFlipper
	           // The Next screen will come in form Left and current Screen will go OUT from Right 
			Intent i = new Intent(this, WordListActivity.class);
			startActivity(i); 
			
			}
		
		
		else if(v instanceof TextView){
		 
			
			if(getConnectivityStatus(MapActivityBeforeLogin.this)==1 || getConnectivityStatus(MapActivityBeforeLogin.this)==1)	
			{
			Account selectedAccount = accounts[v.getId()];
			HashMap<String, Object> params = new HashMap<String, Object>();
			if(isPlanetClicked){ params.put("selectedPlanet",Integer.valueOf(selectedPlatentId-1));}			
			params.put("selectedAccount", selectedAccount);			
			ContentCacheStore.getContentCache().setCurrentUserEmailId(selectedAccount.name);
			new BackgroundAsyncTasks(MapActivityBeforeLogin.this, params).execute(BackgroundTasks.SELECTED_ACCOUNT_AUTHENTICATION);
			}
			
	    	else{
				
			loginpopup.dismiss();
			showCustomDialog();
			
			}
		}
		
	}
	
	//checking for connectivity
	
	 public static int getConnectivityStatus(Context context) {
	        ConnectivityManager cm = (ConnectivityManager) context
	                .getSystemService(Context.CONNECTIVITY_SERVICE);
	 
	        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
	        if (null != activeNetwork) {
	            if(activeNetwork.getType() == ConnectivityManager.TYPE_WIFI)
	                return TYPE_WIFI;
	             
	            if(activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE)
	                return TYPE_MOBILE;
	        }
	        return TYPE_NOT_CONNECTED;
	    }
	 
 //dialog to show in case of no internet connection	
	
	 public void showCustomDialog() {
	        // TODO Auto-generated method stub
	        final Dialog dialog = new Dialog(MapActivityBeforeLogin.this);
	        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
	        dialog.setContentView(R.layout.network_connectivity_alert);
	        
	        final EditText editText = (EditText)dialog.findViewById(R.id.editText1);
	        
	        Button button = (Button)dialog.findViewById(R.id.button1);    
	        button.setOnClickListener(new View.OnClickListener() {
	            
	            @Override
	            public void onClick(View v) {
	                // TODO Auto-generated method stub
	               
	                dialog.dismiss();
	            }
	        });
	                
	        dialog.show();
	    }
}
