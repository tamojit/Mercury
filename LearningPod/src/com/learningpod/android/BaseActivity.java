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
		DisplayMetrics metrics = new DisplayMetrics();
		getWindowManager().getDefaultDisplay().getMetrics(metrics);
		int popupWidth = (int)(0.8*metrics.widthPixels);
		int popupHeight = (int)(0.8*metrics.heightPixels);
		menuitempopup = new PopupWindow(popupWidth, popupHeight);
		View loginWindowView = getLayoutInflater().inflate(
				R.layout.popup_menuitem, null);
		menuitempopup.setContentView(loginWindowView);
		//
		TextView textinpopup = (TextView) loginWindowView
				.findViewById(R.id.text);
		TextView headerpopup = (TextView) loginWindowView
				.findViewById(R.id.popupheader);
		
		//textinpopup.setText(Html.fromHtml(getString(R.string.Terms)));
		if(detail=="TERMS")
		{
			textinpopup.setText(Html.fromHtml("Stellar Vocabulary for 5th Grade (&quot;the App&quot;) is owned by Learningpod, Inc. (&quot;we&quot; or &quot;us&quot;). By using the App, you agree to be bound by these <b>Terms of Use & Privacy Policy</b>(the Agreement).<p><b>Accessing and Using the App from Educational Institutions:</b></p><p>This app uses Google Authentication and Authorization services (collectively known as â€œAuthâ€�) for access, including registration and log in. If any consent is required for you or your students (â€œUsersâ€�) to enter information for Auth or otherwise use functionality and tools (â€œServicesâ€�), such as from your students, their parents or the school, you represent that you have obtained any such consents. Additionally, access and use of the app from Educational Institutions will be subject to the respective Educational Institution's Terms of Use and Policies.<p>We do not collect or store personally identifying information (â€œPIIâ€�) about Users, such as first and last name, email or phone number. The App may create anonymous user accounts on Learningpod that do not contain PII for the purpose of storing progress and performance data (â€œPerformance Dataâ€�) that is generated within the App. The App does not use any location services.</p><p><b>How We Use Information:</b> We may use information,notably Performance Data, to: keep multiple devices in sync for Users; display User interactions with Services via a teacher dashboard; provide school-, district- or state-level reporting as requested; and enhance, develop, support and administer Services.</p><p><b>Disclosure of Information:</b>We do not share your information with third parties for their marketing purposes.  We may share your information with service providers who help us to run the App or to fulfill your request. We may disclose your information in response to legal process, to protect our rights, or as otherwise required by law. If we sell our company or part of it, or our business enters into a joint venture with another business entity, we may disclose your information to our new business partners or owners.</p><p><b>DISCLAIMERS; LIMITATIONS ON LIABILITY</b></p><p>YOUR USE OF THE APP IS AT YOUR OWN RISK. ALL INFORMATION ON OR VIA THE APP IS PROVIDED TO YOU AS IS WITHOUT WARRANTY OF ANY KIND. IN NO EVENT SHALL LEARNINGPOD BE LIABLE TO YOU FOR ANY DAMAGES (INCLUDING INDIRECT, INCIDENTAL, CONSEQUENTIAL, OR PUNITIVE DAMAGES) ARISING OUT OF YOUR USE OF, OR INABILITY TO USE, THE APP.</p><p><b>No Rights of Third Parties:</b></p>This Agreement does not create rights enforceable by third parties.<p><b>Choice of Law and Forum:</b></p> Any dispute between us arising from or related to these Terms and Conditions will be governed by New York law and the exclusive forum for disputes arising from or related to this agreement shall be the state and federal courts of New York.<p><b>Severability:</b></p>If any provision of this Agreement is ruled unenforceable, that provision will be severed from this Agreement, and the other provisions will remain effective and enforceable.<p><b>How to Contact Us:</b></p>For any questions or comments concerning to this Agreement, please email: info@learningpod.com."));
			headerpopup.setText("Terms Of Service");
		}
		if(detail=="HELP")
		{
			textinpopup.setText(Html.fromHtml("&nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;  &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp; &nbsp;<b>Stellar Vocabulary Grade 5<b><p><b>Log In:</b><br>Log In to the app by selecting a Google Account (email address) that is configured in your device. Once you log in and start practicing questions, your practice history will be stored against your login account.<br><br><b>Map Screen:</b><br>The Map screen displays 15 â€œplanets,â€� each of which corresponds to a distinct category for fifth grade vocabulary. Each planet contains five questions. Click on a planet to get started and answer questions.<br>A little building on the planet along with a progress bar indicates that you are practicing within that planet. Once you answer all five questions, you will see a â€˜flagâ€™ atop the building that signifies completion.<br><br><b>Word List:</b><br>Click on Word List to access a list of grade-appropriate words.<br><br><b>Question/Answer Page</b><br>The question-and-answer page contains a multiple choice question and a set of answer choices for that question. Read the question carefully, select an answer choice, and click Submit to get a visual indication of whether you answered the question correctly or not. You will also see a brief explanation of the answer. Click Next to proceed to the next question. Click Back to go to the previous question you practiced.<br>The progress indicator on the top right corner helps you keep track of your activity within a planet. It will give you an overview of the number of questions you answered correctly or incorrectly thus far.<br>Click on Map to go back to the Map Screen.<br><br><b>Summary Page:</b><br>Once you complete all the questions in a specific planet, you will see a Â summary page that indicates the percentage of correct answers. This summary page also allows you to send your results to the email address of your choice.<br>Click on the question numbers on the summary page to review a planet you have already practiced. Alternatively, click on the Back button on the summary page to see the last question of a particular planet; use the Back and Next buttons to review questions.<br><br><b style='margin-left:40px;'>Overflow Menu:</b><br>Log In: Log In to the app<br>Log Out: Log Out of the app<br>About: About Page<br>Help: Help Page"));
			headerpopup.setText("Help");
		}
		if(detail=="ABOUT")
		{
			
		}
		//textinpopup.setText(detail);
		// close the popup window
		
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
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
		if(progressDialog!=null)progressDialog.dismiss();
		if(alertDialog!=null) alertDialog.dismiss();
		if(menuitempopup!=null) menuitempopup.dismiss();
		super.onDestroy();
		
	}

}