package com.learningpod.android;

import java.util.List;

import com.learningpod.android.beans.UserProfileBean;
import com.learningpod.android.beans.pods.PodBean;

public class ContentCacheStore {
	
	private static ContentCacheStore contentCacheInstance ;
	private List<PodBean> pods;
	private UserProfileBean loggedInUserProfile;
	private String currentUserEmailId;
	
	
	

	private ContentCacheStore(){}
	
	public static ContentCacheStore getContentCache(){
		if(contentCacheInstance==null){
			contentCacheInstance = new ContentCacheStore();
		}
		return contentCacheInstance;
	}
	
	public List<PodBean> getPods() {
		return pods;
	}

	public void setPods(List<PodBean> pods) {
		this.pods = pods;
	}

	public UserProfileBean getLoggedInUserProfile() {
		return loggedInUserProfile;
	}

	public void setLoggedInUserProfile(UserProfileBean loggedInUserProfile) {
		this.loggedInUserProfile = loggedInUserProfile;
	}

	public String getCurrentUserEmailId() {
		return currentUserEmailId;
	}

	public void setCurrentUserEmailId(String currentUserEmailId) {
		this.currentUserEmailId = currentUserEmailId;
	}

}
