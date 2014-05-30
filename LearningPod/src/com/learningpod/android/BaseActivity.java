package com.learningpod.android;

import com.learningpod.android.activities.AccountSelectorActivity;
import com.learningpod.android.activities.MapActivity;
import com.learningpod.android.activities.MapActivityBeforeLogin;
import com.learningpod.android.activities.PodQuestionActivity;
import com.learningpod.android.activities.WordListActivity;

import android.provider.Settings;
import android.accounts.Account;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Html;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

public class BaseActivity extends Activity implements
		DialogInterface.OnClickListener {

	private ProgressDialog progressDialog;
	private AlertDialog alertDialog;
	private PopupWindow menuitempopup;
	private Account[] accounts = null;
	private View loginWindowView;
	 
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
		DisplayMetrics metrics = BaseActivity.this.getResources().getDisplayMetrics();
		int width = metrics.widthPixels;
		int height = metrics.heightPixels;
		menuitempopup = new PopupWindow(width/2,height);
		
		
		
		if(detail=="ABOUT")
		{
			
			loginWindowView = getLayoutInflater().inflate(
					R.layout.popup_menuitemabout, null);
			menuitempopup.setContentView(loginWindowView);
			menuitempopup.showAtLocation(loginWindowView, Gravity.RIGHT, 0, 0);
			Button Close = (Button) loginWindowView.findViewById(R.id.close);
			Close.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					{
						menuitempopup.dismiss();
					}

				}
			});	
			
			
		} 
		if(detail=="HELP")
		{
			loginWindowView = getLayoutInflater().inflate(
					R.layout.popup_menuitem, null);
			menuitempopup.setContentView(loginWindowView);
			menuitempopup.showAtLocation(loginWindowView, Gravity.RIGHT, 0, 0);
			Button Close = (Button) loginWindowView.findViewById(R.id.close);
			Close.setOnClickListener(new OnClickListener() {

				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					{
						menuitempopup.dismiss();
					}

				}
			});
	//	textinpopup.setText(Html.fromHtml("&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<b>Stellar Vocabulary Grade 5<b><p><b>Log In:</b><br>Log In to the app by selecting a Google Account (email address) that is configured in your device. Once you log in and start practicing questions, your practice history will be stored against your login account.<br><br><b>Map Screen:</b><br>The Map screen displays 15 “planets,” each of which corresponds to a distinct category for fifth grade vocabulary. Each planet contains five questions. Click on a planet to get started and answer questions.<br>A little building on the planet along with a progress bar indicates that you are practicing within that planet. Once you answer all five questions, you will see a ‘flag’ atop the building that signifies completion.<br><br><b>Word List:</b><br>Click on Word List to access a list of grade-appropriate words.<br><br><b>Question/Answer Page</b><br>The question-and-answer page contains a multiple choice question and a set of answer choices for that question. Read the question carefully, select an answer choice, and click Submit to get a visual indication of whether you answered the question correctly or not. You will also see a brief explanation of the answer. Click Next to proceed to the next question. Click Back to go to the previous question you practiced.<br>The progress indicator on the top right corner helps you keep track of your activity within a planet. It will give you an overview of the number of questions you answered correctly or incorrectly thus far.<br>Click on Map to go back to the Map Screen.<br><br><b>Summary Page:</b><br>Once you complete all the questions in a specific planet, you will see a  summary page that indicates the percentage of correct answers. This summary page also allows you to send your results to the email address of your choice.<br>Click on the question numbers on the summary page to review a planet you have already practiced. Alternatively, click on the Back button on the summary page to see the last question of a particular planet; use the Back and Next buttons to review questions.<br><br><b style='margin-left:40px;'>Overflow Menu:</b><br>Log In: Log In to the app<br>Log Out: Log Out of the app<br>About: About Page<br>Help: Help Page"));
		}
		if(detail=="TERMS")
		{
			loginWindowView = getLayoutInflater().inflate(
					R.layout.popup_menuitemterms, null);
			menuitempopup.setContentView(loginWindowView);
			menuitempopup.showAtLocation(loginWindowView, Gravity.RIGHT, 0, 0);
			Button Close = (Button) loginWindowView.findViewById(R.id.close);
			Close.setOnClickListener(new OnClickListener() {

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

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is
		// presentpublic Men.
		if (!(this instanceof AccountSelectorActivity)) {
			getMenuInflater().inflate(R.menu.common_menu, menu);
		}
		
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
			menuitempopup.showAtLocation(findViewById(android.R.id.content)
					.getRootView(), Gravity.CENTER, 0, 0);
		}

		else if (item.getItemId() == R.id.about) {		
			menupopup("ABOUT");
			menuitempopup.showAtLocation(findViewById(android.R.id.content)
					.getRootView(), Gravity.CENTER, 0, 0);
		} 
		
		else if (item.getItemId() == R.id.terms) {
			
			menupopup("TERMS");
			menuitempopup.showAtLocation(findViewById(android.R.id.content)
					.getRootView(), Gravity.CENTER, 0, 0);
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

}