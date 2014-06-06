package com.learningpod.android;


import com.learningpod.android.activities.MapActivity;
import com.learningpod.android.activities.MapActivityBeforeLogin;
import com.learningpod.android.activities.PodQuestionActivity;
import com.learningpod.android.activities.WordListActivity;

import android.provider.Settings;
import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Html;
import android.text.Html.ImageGetter;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.ImageSpan;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.TextView.BufferType;

public class BaseActivity extends Activity implements
		DialogInterface.OnClickListener {

	

	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;
	private Dialog menuitempopup1;
	private PopupWindow menuitempopup;
	private Account[] accounts = null;
	private View loginWindowView;
	
	int imageNumber=1;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		// progress dialog for the application.
		progressDialog = new ProgressDialog(this);
		progressDialog.setCancelable(false);
		progressDialog.hide();

		// alert dialog for the application.
		alertDialog = new AlertDialog.Builder(this).create();
		alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "OK", this);
		this.alertDialog.setCancelable(false);
	}

	private void menupopup(String detail) {

		int width = (int) TypedValue.applyDimension(
				TypedValue.COMPLEX_UNIT_DIP, 416, getResources()
						.getDisplayMetrics());
		DisplayMetrics metrics = BaseActivity.this.getResources().getDisplayMetrics();
		//int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		menuitempopup = new PopupWindow(width,height);

		if(detail=="HELP")
		{
		
			loginWindowView = getLayoutInflater().inflate(
					R.layout.popup_menuitem, null);
			menuitempopup.setContentView(loginWindowView);
			menuitempopup.showAtLocation(loginWindowView, Gravity.RIGHT, 0, 0);
			TextView wordlist=(TextView)loginWindowView.findViewById(R.id.textdetail3);
		    wordlist.setText(Html.fromHtml("You can access the<b> Word List </b> from the map."));
		
			TextView practicingquestions=(TextView)loginWindowView.findViewById(R.id.textdetail4);
		practicingquestions.setText(Html.fromHtml("Select an answer choice and tap <b>Submit</b>.A green check"));
		TextView practicingquestions2=(TextView)loginWindowView.findViewById(R.id.textdetailzz);
		practicingquestions2.setText(Html.fromHtml(" explanation.<br /> <br /> Tap <b>Next</b> to go to a new question and <b>Back</b> to go to the question you just practiced."));
		//A green check mark [green check] means you got the answer right and a red x mark [red x mark] means 	
			TextView completequestions=(TextView)loginWindowView.findViewById(R.id.textdetail5);
			completequestions.setText(Html.fromHtml("Once you complete all the questions in a planet, you’ll see a summary screen that shows how well you did. From this screen, you can email your results to your teacher or parent.<br /><br />You can review the questions by tapping on a question number or using <b>Back</b>."));
			LinearLayout closebutton3 = (LinearLayout)loginWindowView.findViewById(R.id.closebutton3);
			
			closebutton3 .setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					{
						menuitempopup.dismiss();
					}

				}
			});
			
		}
		
		if(detail=="ABOUT")
		{
			loginWindowView = getLayoutInflater().inflate(
					R.layout.popup_menuitemabout, null);
			menuitempopup.setContentView(loginWindowView);
			menuitempopup.showAtLocation(loginWindowView, Gravity.RIGHT, 0, 0);
			
			LinearLayout closebutton2 = (LinearLayout)loginWindowView.findViewById(R.id.closebutton);		
			closebutton2.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					{
						menuitempopup.dismiss();
					}

				}
			});
	
		}
		if(detail=="TERMS")
		{
			
			
			loginWindowView = getLayoutInflater().inflate(
					R.layout.popup_menuitemterms, null);
			menuitempopup.setContentView(loginWindowView);
			menuitempopup.showAtLocation(loginWindowView, Gravity.RIGHT, 0, 0);
			
			LinearLayout closebutton1 = (LinearLayout)loginWindowView.findViewById(R.id.closebutton1);
			closebutton1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					{
						menuitempopup.dismiss();
					}

				}
			});
		}
		
		if(detail=="LESSON")
		{
			
			
			loginWindowView = getLayoutInflater().inflate(
					R.layout.popup_menuitemlesson, null);
			menuitempopup.setContentView(loginWindowView);
			menuitempopup.showAtLocation(loginWindowView, Gravity.RIGHT, 0, 0);
			
			LinearLayout closebutton1 = (LinearLayout)loginWindowView.findViewById(R.id.closebutton2);
			closebutton1.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					{
						menuitempopup.dismiss();
					}

				}
			});
		}	
		
		//textinpopup.setText(detail);
		// close the popup window

	}
		
	//dialog	
		
		private void menudialog(String detail) {
			
			DisplayMetrics metrics = BaseActivity.this.getResources().getDisplayMetrics();
			//int width = metrics.widthPixels;
			int height = metrics.heightPixels;
			menuitempopup1 = new Dialog(BaseActivity.this);
			menuitempopup1.requestWindowFeature(Window.FEATURE_NO_TITLE);
		//	menuitempopup1.getWindow().setLayout(600, 400);
			WindowManager.LayoutParams wmlp = menuitempopup1.getWindow().getAttributes();
          //  menuitempopup1.getWindow().setLayout(300,300);
			wmlp.gravity = Gravity.RIGHT;
		//	wmlp.width=500;
		    wmlp.height=height;
		//	 loginWindowView = getLayoutInflater().inflate(
		//				R.layout.popup_menuitemterms, null);
			 //menuitempopup1.setContentView(R.layout.popup_menuitemterms);
			 menuitempopup1.show();
			 /*LinearLayout closebutton1 = (LinearLayout)menuitempopup1.findViewById(R.id.closebutton1);
				closebutton1.setOnClickListener(new OnClickListener() {

					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						{
							menuitempopup1.dismiss();
						}

					}
				});*/
		}	
		
		
	

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is
		// presentpublic Men.
		getMenuInflater().inflate(R.menu.common_menu, menu);
		
		if(this instanceof MapActivityBeforeLogin){
			menu.findItem(R.id.login).setTitle("Log In");
		}
		return true;
	}

	@Override
	public boolean onMenuItemSelected(int featureId, MenuItem item) {
		// TODO Auto-generated method stub
		if (item.getItemId() == R.id.help) {			
			menupopup("HELP");
		//	menuitempopup.showAtLocation(findViewById(android.R.id.content)
		//			.getRootView(), Gravity.CENTER, 0, 0);
		}
        
		if (item.getItemId() == R.id.Lesson) {			
			menupopup("LESSON");
		//	menuitempopup.showAtLocation(findViewById(android.R.id.content)
		//			.getRootView(), Gravity.CENTER, 0, 0);
		} 
		
		else if (item.getItemId() == R.id.about) {		
			menupopup("ABOUT");
		//	menuitempopup.showAtLocation(findViewById(android.R.id.content)
		//			.getRootView(), Gravity.CENTER, 0, 0);
		} 
		
		else if (item.getItemId() == R.id.terms) {
			menudialog("TERMS");
		//	menupopup("TERMS");
		//	menuitempopup.showAtLocation(findViewById(android.R.id.content)
		//			.getRootView(), Gravity.CENTER, 0, 0);
		} 
		else if(item.getItemId()==R.id.login){
			if(this instanceof MapActivityBeforeLogin){
				// perform login
				((MapActivityBeforeLogin)this).LoginDialogPopUp();
			}
			else {
				// perform logout
				ContentCacheStore.getContentCache().setLoggedInUserProfile(null);
				// clear the shared preference
				SharedPreferences loginPreferences = getSharedPreferences("Login", Context.MODE_PRIVATE);				
				Editor editor = loginPreferences.edit();
				editor.remove("loggeduser");
				editor.commit();
				Intent intent = new Intent(this,MapActivityBeforeLogin.class);				
				startActivity(intent);
				this.finish();
			}
		}
		else if(item.getItemId()==android.R.id.home){
			if(this instanceof WordListActivity){				
				this.finish();
			}else{
				Intent intent = new Intent(this,
						MapActivity.class);				
				startActivity(intent);
				this.finish();
			}
		}
		return true;
	}

	public ProgressDialog getProgressDialog() {
		return this.progressDialog;
	}

	public void showAlertDialog(String alertTitle, String alertMessage) {
		if (progressDialog.isShowing()) {
			progressDialog.hide();
		}
		this.alertDialog.setTitle(alertTitle);
		this.alertDialog.setMessage(alertMessage);
		this.alertDialog.show();
	}

	@Override
	public void onClick(DialogInterface dialog, int which) {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void onBackPressed() {
	    // your code.
		if(this instanceof WordListActivity){
			this.finish();
			return;
		}
		AlertDialog.Builder builder = new AlertDialog.Builder(this);
	    builder.setTitle("Exit");
	    builder.setMessage("Do you wish to exit the application?");

	    builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
	            public void onClick(DialogInterface dialog, int which) {
	                    dialog.dismiss();
	                    finish();
	            }
	        });

	    builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
	        @Override
	        public void onClick(DialogInterface dialog, int which) {
	            dialog.dismiss();
	        }
	    });
	    AlertDialog alert = builder.create();
	    alert.show();
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		if(progressDialog!=null)progressDialog.dismiss();
		if(alertDialog!=null) alertDialog.dismiss();
		if(menuitempopup!=null) menuitempopup.dismiss();
		super.onDestroy();
		
	}

}