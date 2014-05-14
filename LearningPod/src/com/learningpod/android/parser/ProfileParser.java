package com.learningpod.android.parser;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import com.google.gson.Gson;
import com.learningpod.android.beans.UserProfileBean;
import com.learningpod.android.utility.LearningpodException;

public class ProfileParser implements GenericParser {

	private UserProfileBean userProfile;
	
	
	@Override
	public Object retrieveSerializedObject() {
		// TODO Auto-generated method stub
		return userProfile;
	}

	@Override
	public void parse(InputStream iStream) throws LearningpodException {
		Gson gson = new Gson();
		Reader reader = new InputStreamReader(iStream);
		userProfile =  gson.fromJson(reader,UserProfileBean.class);		
	}

}
