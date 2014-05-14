package com.learningpod.android.activities;

import java.util.HashMap;

import org.brickred.socialauth.Profile;
import org.brickred.socialauth.android.DialogListener;
import org.brickred.socialauth.android.SocialAuthAdapter;
import org.brickred.socialauth.android.SocialAuthError;
import org.brickred.socialauth.android.SocialAuthAdapter.Provider;

import com.learningpod.android.BackgroundAsyncTasks;
import com.learningpod.android.BackgroundTasks;
import com.learningpod.android.BaseActivity;
import com.learningpod.android.ContentCacheStore;
import com.learningpod.android.R;
import com.learningpod.android.beans.UserProfileBean;

import android.os.Bundle;
import android.accounts.Account;
import android.accounts.AccountManager;
import android.app.ActionBar.LayoutParams;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

public class AccountSelectorActivity extends BaseActivity 
						implements OnClickListener {

	private Account[] accounts = null;
	private  SocialAuthAdapter adapter;
	
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_account_selector);
		
		// initialize Account Manager class to fetch all configured accounts on device
		AccountManager accountManager = AccountManager.get(getApplicationContext());
		//accounts = accountManager.getAccounts();
		
		// to be uncommented for devices
		accounts = accountManager.getAccountsByType("com.google");	
		
		
		LinearLayout emailButtonContainer = (LinearLayout)findViewById(R.id.emailContainer);
		Button emailButton = null;
		int buttonWidthInDp = 300;
		int buttonWidthInPx = (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, buttonWidthInDp, getResources().getDisplayMetrics());
		LinearLayout.LayoutParams buttonParams = new LinearLayout.LayoutParams(buttonWidthInPx,LayoutParams.WRAP_CONTENT);		
		buttonParams.topMargin=20;
		for(Account account : accounts){
			
			emailButton = new Button(this);			
			emailButton.setLayoutParams(buttonParams);
			emailButton.setText(account.name);
			emailButton.setOnClickListener(this);
			emailButton.setBackgroundResource(R.drawable.custom_button);
			emailButton.setTextColor(Color.parseColor("#ffffff"));
			emailButtonContainer.addView(emailButton);			
		}
		
		
		// social auth
				Button btnlogin = (Button)findViewById(R.id.btnlogin);
				adapter = new SocialAuthAdapter(new ResponseListener());
				
				btnlogin.setOnClickListener(new OnClickListener() {
				
					@Override
					public void onClick(View v) {
						// TODO Auto-generated method stub
						adapter.authorize(AccountSelectorActivity.this, Provider.GOOGLEPLUS);
						
					}
				});	
		
	}
	
	


	
	
	@Override
	public void onClick(View v) {
		Button accBtn = (Button)v;
		Account selectedAccount = null;
		for(Account acc : accounts){
			if(acc.name.equalsIgnoreCase(accBtn.getText().toString())){
				selectedAccount = acc;
				break;
			}
		}
		// save the email id clicked in content cache
		ContentCacheStore.getContentCache().setCurrentUserEmailId(selectedAccount.name);
		HashMap<String, Object> params = new HashMap<String, Object>();
		params.put("selectedAccount", selectedAccount);
		new BackgroundAsyncTasks(this, params).execute(BackgroundTasks.SELECTED_ACCOUNT_AUTHENTICATION);
	}

	

	
	private final class ResponseListener implements DialogListener {
	    @Override
	    public void onComplete(Bundle values) {
	        //successful authentication..
	    	Log.v("auth", "successfull");
	    	getProgressDialog().setMessage("Authenticating...");
	    	getProgressDialog().show();
	    	Profile profileMap = adapter.getUserProfile();
	    	// save the email id clicked in content cache
			ContentCacheStore.getContentCache().setCurrentUserEmailId(profileMap.getEmail());
	    	// convert profile object to user profile object used in the application	    	
	    	UserProfileBean userProfile = new UserProfileBean();
	    	userProfile.setName(profileMap.getFullName());
	    	userProfile.setGender(profileMap.getGender());
	    	userProfile.setId(profileMap.getValidatedId());
	    	
	    	
	    	
	    	if(profileMap!=null){
	    		HashMap<String, Object> params = new HashMap<String, Object>();
		    	params.put("userProfile", userProfile);
		    	new BackgroundAsyncTasks(AccountSelectorActivity.this, params).
		    							execute(BackgroundTasks.GMAIL_ACCOUNT_AUTHENTICATION);
				// sign out before going to the next screen
				adapter.signOut(AccountSelectorActivity.this.getApplicationContext(), Provider.GOOGLEPLUS.toString());
	    	}
	    	
	    	
	    	
	    	
	    	
	    }

	    @Override
	    public void onError(SocialAuthError error) {
	        error.printStackTrace();
	        Log.v("auth", "error");
	    }

	    @Override
	    public void onCancel() {

	    }

	    @Override
	    public void onBack() {

	    }
	}
	

}
