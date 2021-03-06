package com.learningpod.android;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.learningpod.android.activities.MapActivity;
import com.learningpod.android.activities.MapActivityBeforeLogin;
import com.learningpod.android.activities.PodQuestionActivity;
import com.learningpod.android.activities.SplashActivity;
import com.learningpod.android.beans.UserProfileBean;
import com.learningpod.android.beans.explanations.ExplanationBean;
import com.learningpod.android.beans.pods.PodBean;
import com.learningpod.android.beans.pods.PodQuestionBean;
import com.learningpod.android.beans.pods.PodQuestionExplanation;
import com.learningpod.android.beans.questions.QuestionBean;
import com.learningpod.android.parser.GenericParser;
import com.learningpod.android.parser.ParserFactory;
import com.learningpod.android.parser.ParserType;
import com.learningpod.android.rest.HttpRestServiceHandler;
import com.learningpod.android.rest.RestServiceHandlerFactory;
import com.learningpod.android.utility.LearningpodException;
import com.learningpod.android.utility.UrlConstants;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.accounts.AccountManagerFuture;
import android.accounts.AuthenticatorException;
import android.accounts.OperationCanceledException;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.AssetManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;



public class BackgroundAsyncTasks extends AsyncTask<BackgroundTasks, Integer, Object> {

	private BaseActivity currentActivity = null;
	private BackgroundTasks task = null;
	private HashMap<String, Object> params;
	
	
	public BackgroundAsyncTasks(BaseActivity activity, HashMap<String, Object> params){
		this.currentActivity = activity;
		this.params = params;
	}

	@Override
	protected void onPreExecute() {
		// TODO Auto-generated method stub
		super.onPreExecute();
		currentActivity.getProgressDialog().setMessage("Loading...");
		if(!(currentActivity instanceof SplashActivity)){
			currentActivity.getProgressDialog().show();
		}
	}

	@Override
	protected Object doInBackground(BackgroundTasks... tasks) {
		// TODO Auto-generated method stub
		this.task = tasks[0];
		
		
		
		// REST service handler for all rest service communication
		HttpRestServiceHandler serviceHandler = RestServiceHandlerFactory
				.getServiceHandler();
		if(task==BackgroundTasks.SELECTED_ACCOUNT_AUTHENTICATION){
			// for testing when there is no Internet connection.
			 
			// get the selected account 
			Account selectedAccount = (Account)params.get("selectedAccount");
			// if this is called before login
			if(selectedAccount==null){
				// check if the user has not logged out and he has an entry in shared preference
				SharedPreferences loginPreferences =  currentActivity.getSharedPreferences("Login", Context.MODE_PRIVATE);
				String loggedInEmail = loginPreferences.getString("loggeduser", "");
				if(!loggedInEmail.equalsIgnoreCase("")){
					// we have a logged in user
					AccountManager accountManager = AccountManager
							.get(currentActivity.getApplicationContext());
					Account[] accounts = accountManager.getAccountsByType("com.google");
					for(Account acc:accounts){
						if(acc.name.equalsIgnoreCase(loggedInEmail)){
							selectedAccount =acc;
						}
					}
				}else{
					return null;
				}
			}
			// get the auth token for the selected account
			String authToken = updateToken(selectedAccount, true);
			// call google api for getting basic profile information if auth token is retrieved
			UserProfileBean userProfileBean = null;
			if(!authToken.equalsIgnoreCase("")){
				HashMap<String, String> urlParams = new HashMap<String, String>();
				urlParams.put("access_token", authToken);
				try {
				 userProfileBean = 	(UserProfileBean)serviceHandler.fireHttpGet(UrlConstants.PROFILE_DATA_FETCH, urlParams, ParserType.PROFILE_DETAILS_PARSER);				
				} catch (LearningpodException e) {					
					e.printStackTrace();
				}
			}else{
				// auth token not recieved. user may not have allowed access.
				return null;
			}
			
			return userProfileBean ;
		}
		
		else if(task==BackgroundTasks.GMAIL_ACCOUNT_AUTHENTICATION){
			UserProfileBean userProfile = (UserProfileBean)params.get("userProfile");
			this.task = BackgroundTasks.SELECTED_ACCOUNT_AUTHENTICATION;
			return userProfile;
		}
		
		else if(task==BackgroundTasks.LOAD_POD_QUESTIONS){
			PodBean selectedPod = (PodBean)params.get("selectedPod");			
			AssetManager assetMgr = currentActivity.getAssets();
			ArrayList<QuestionBean> questions = new  ArrayList<QuestionBean>();			
			ArrayList<ArrayList<ExplanationBean>> explanations = new ArrayList<ArrayList<ExplanationBean>>();
			for(PodQuestionBean ques :selectedPod.getPodElements()){
				try {
					InputStream is = assetMgr.open("pods/questions/"+ ques.getItemId() + "." + ques.getVersion()  + ".xml");
					questions.add((QuestionBean)parseUtility(is, ParserType.QUESTION_PARSER));
					
					// get explanations for a question
					ArrayList<ExplanationBean> explanationsForThisQues = new ArrayList<ExplanationBean>();
					for(PodQuestionExplanation exp :ques.getExplanations()){
						InputStream isExp = assetMgr.open("pods/explanation/"+ exp.getItemId() + ".xml");
						explanationsForThisQues.add((ExplanationBean)parseUtility(isExp, ParserType.EXPLANATION_PARSER));
					}
					explanations.add(explanationsForThisQues);
					
					
				} catch (IOException e) {
					Log.e("LearningPod","pod xmls not found " + ques.getItemId()+ "." + ques.getVersion());
				}catch(LearningpodException e){
					 Log.e("LearningPod","Error in parsing Pod xml");
				}
			}
			HashMap<String,Object> passParamsToPostExecute = new HashMap<String, Object>();
			passParamsToPostExecute.put("questions", questions);
			passParamsToPostExecute.put("selectedPod", selectedPod);
			passParamsToPostExecute.put("explanations", explanations);
			
			return passParamsToPostExecute;
		}
		
		return null;
	}
	
	@Override
	protected void onPostExecute(Object result) {
		// TODO Auto-generated method stub
		//currentActivity.getProgressDialog().hide();
		if(result==null){
			// handling null specifically for one task
			if(task!=BackgroundTasks.SELECTED_ACCOUNT_AUTHENTICATION){
				// something went wrong
				currentActivity.getProgressDialog().hide();
				return;
			}
			
		}
		if(task==BackgroundTasks.SELECTED_ACCOUNT_AUTHENTICATION){
			UserProfileBean userProfile  = (UserProfileBean) result;
			ContentCacheStore.getContentCache().setLoggedInUserProfile(userProfile);
			ArrayList<PodBean> pods = new ArrayList<PodBean>();
			AssetManager assetManager = currentActivity.getAssets();
			// fetch the list of PODS 
			 try{
				 String[] listOfPods = assetManager.list("pods");
				 for(String pod :listOfPods){
					if(assetManager.list("pods/" + pod).length>0){
						// this is a folder
						continue;
					}
					InputStream is =  assetManager.open("pods/" + pod);
					pods.add((PodBean)parseUtility(is, ParserType.POD_PARSER));
				 }
			 }catch(IOException e){
				 Log.e("LearningPod","pod xmls not found");
			 }catch(LearningpodException e){
				 Log.e("LearningPod","Error in parsing Pod xml");
			 }
			 
			Intent intent = null;
			if(userProfile==null){
				// go to map screen without user logged in
				intent = new Intent(currentActivity,MapActivityBeforeLogin.class);
			}else{
				if(params.get("selectedPlanet")==null){
					//go to map activity after login. user has selected log in from menu
					intent = new Intent(currentActivity,MapActivity.class);
				}
				else{
					
					// go to specific pod after login as user has clicked on planet to log in
					int selectedPlanetId = ((Integer)params.get("selectedPlanet"));
					PodBean selectedPodBeforeLogin = pods.get(selectedPlanetId);
					HashMap<String,Object> paramsForLoadingPod = new HashMap<String,Object>();
					paramsForLoadingPod.put("selectedPod",selectedPodBeforeLogin);			
					new BackgroundAsyncTasks(currentActivity, paramsForLoadingPod).execute(BackgroundTasks.LOAD_POD_QUESTIONS);
					return;
				}
			}
			
			// store the pods for later use in cache content
			ContentCacheStore.getContentCache().setPods(pods);			
			currentActivity.startActivity(intent);
			currentActivity.getProgressDialog().hide();
			currentActivity.finish();
			
		}else if(task==BackgroundTasks.LOAD_POD_QUESTIONS){
			
			HashMap<String, Object> paramsFromDoInBackground = (HashMap<String, Object>)result;
			//get the selected pod
			PodBean selectedPod = (PodBean)paramsFromDoInBackground.get("selectedPod");
			//get the Pod questions			
			ArrayList<QuestionBean> questions = (ArrayList<QuestionBean>)paramsFromDoInBackground.get("questions");
			// get the explanations
			ArrayList<ArrayList<ExplanationBean>> explanations = (ArrayList<ArrayList<ExplanationBean>>) paramsFromDoInBackground.get("explanations");
			// create the intent
			Intent intent = new Intent(currentActivity,PodQuestionActivity.class);
			//put objects into extra
			intent.putExtra("questions",questions);
			intent.putExtra("selectedPod", selectedPod);
			intent.putExtra("explanations",explanations );
			currentActivity.startActivity(intent);
			currentActivity.getProgressDialog().hide();
			currentActivity.finish();
		}
		
	}
	
	private Object parseUtility(InputStream is,ParserType type) throws LearningpodException{
		GenericParser parser = ParserFactory.getParser(type);
		parser.parse(is);
		return parser.retrieveSerializedObject();
	}
	
	private boolean listAssetFiles(String path) {

	    String [] list;
	    try {
	        list = currentActivity.getAssets().list(path);
	        if (list.length > 0) {
	            // This is a folder
	            for (String file : list) {
	                if (!listAssetFiles(path + "/" + file))
	                    return false;
	            }
	        } else {
	            // This is a file
	            // TODO: add file name to an array list
	        }
	    }catch (IOException e) {
	        return false;
	    }

	    return true; 
	} 
	
	private String updateToken(Account selectedAccount, boolean invalidateToken){
		String authToken = "";
		AccountManager manager = AccountManager.get(currentActivity.getApplicationContext());
		String scope = "https://www.googleapis.com/auth/userinfo.profile";
		AccountManagerFuture<Bundle> accountManagerFuture  = manager.
				getAuthToken(selectedAccount, "oauth2:" + scope, null, currentActivity, null, null);
		try {
			Bundle authTokenBundle = accountManagerFuture.getResult();
			authToken = authTokenBundle.getString(AccountManager.KEY_AUTHTOKEN).toString();
			
			if(invalidateToken){
				manager.invalidateAuthToken("com.google", authToken);
				authToken = updateToken(selectedAccount, false);
			}
		} catch (OperationCanceledException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AuthenticatorException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return authToken;
	}

}
