package com.learningpod.android.activities;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.GoogleApiClient.ConnectionCallbacks;
import com.google.android.gms.common.api.GoogleApiClient.OnConnectionFailedListener;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.learningpod.android.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.IntentSender.SendIntentException;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class GooglePlusSignInActivity extends Activity
				implements OnClickListener, OnConnectionFailedListener, ConnectionCallbacks{

	 
	
	/* Track whether the sign-in button has been clicked so that we know to resolve
	 * all issues preventing sign-in without waiting.
	 */
	private boolean mSignInClicked;
	  /* Client used to interact with Google APIs. */
	  private GoogleApiClient mGoogleApiClient;
	  /* Request code used to invoke sign in user interactions. */
	  private static final int RC_SIGN_IN = 0;
	  private boolean mIntentInProgress;
	  /* Store the connection result from onConnectionFailed callbacks so that we can
	   * resolve them when the user clicks sign-in.
	   */
	  private ConnectionResult mConnectionResult;
	  
	  private void resolveSignInError() {
		  if (mConnectionResult.hasResolution()) {
		    try {
		      mIntentInProgress = true;
		     /* startIntentSenderForResult(mConnectionResult.getIntentSender(),
		          RC_SIGN_IN, null, 0, 0, 0);*/
		      mConnectionResult.startResolutionForResult(this, // your activity
                      RC_SIGN_IN);
		    } catch (SendIntentException e) {
		      // The intent was canceled before it was sent.  Return to the default
		      // state and attempt to connect to get an updated ConnectionResult.
		      mIntentInProgress = false;
		      mGoogleApiClient.connect();
		    }
		  }
		}
	  
	  
	  protected void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
			setContentView(R.layout.google_plus_signin);
			
			// build the google api client
			mGoogleApiClient = new GoogleApiClient.Builder(this)
	        .addConnectionCallbacks(this)
	        .addOnConnectionFailedListener(this)
	        .addApi(Plus.API, null)
	        .addScope(Plus.SCOPE_PLUS_LOGIN)
	        .build();
			
			findViewById(R.id.sign_in_button).setOnClickListener(this);
	  }
	  
	  protected void onStart() {
		    super.onStart();
		    mGoogleApiClient.connect();
		  }
		
		protected void onStop() {
		    super.onStop();

		    if (mGoogleApiClient.isConnected()) {
		      mGoogleApiClient.disconnect();
		    }
		  }
		
		
		protected void onActivityResult(int requestCode, int responseCode, Intent intent) {
			  if (requestCode == RC_SIGN_IN) {
			    if (responseCode != RESULT_OK) {
			      mSignInClicked = false;
			    }

			    mIntentInProgress = false;

			    if (!mGoogleApiClient.isConnecting()) {
			      mGoogleApiClient.connect();
			    }
			  }
			}
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
			if(v.getId() == R.id.sign_in_button
				    && !mGoogleApiClient.isConnecting()){
				mSignInClicked = true;
			    resolveSignInError();
			}
		}
		
		@Override
		public void onConnected(Bundle arg0) {
			
				
			
			if(!mSignInClicked){
				if (mGoogleApiClient.isConnected()) {
					Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
					mGoogleApiClient.disconnect();
					mGoogleApiClient.connect();
				}	
			}else{
				if (Plus.PeopleApi.getCurrentPerson(mGoogleApiClient) != null) {
					Person currentPerson = Plus.PeopleApi
							.getCurrentPerson(mGoogleApiClient);
					String personName = currentPerson.getDisplayName();
					Intent intent = new Intent(this,HomeScreenActivityWithSlidingMenu.class);
					intent.putExtra("username", personName);
					startActivity(intent);				
				}
				mSignInClicked = false;
			}
			
		}

		@Override
		public void onConnectionSuspended(int arg0) {
			// TODO Auto-generated method stub
			
		}

		@Override
		public void onConnectionFailed(ConnectionResult result) {
			if (!mIntentInProgress) {
			    // Store the ConnectionResult so that we can use it later when the user clicks
			    // 'sign-in'.
			    mConnectionResult = result;

			    if (mSignInClicked) {
			      // The user has already clicked 'sign-in' so we attempt to resolve all
			      // errors until the user is signed in, or they cancel.
			      resolveSignInError();
			    }
			  }
			
			
		}
		
}
