package com.learningpod.android.parser;

import java.io.InputStream;

import com.learningpod.android.beans.questions.QuestionBean;
import com.learningpod.android.beans.questions.QuestionChoiceBean;
import com.learningpod.android.utility.LearningpodException;
import com.thoughtworks.xstream.XStream;

public class QuestionParser implements GenericParser {

	private QuestionBean question;
	
	@Override
	public Object retrieveSerializedObject() {
		// TODO Auto-generated method stub
		return question;
	}

	@Override
	public void parse(InputStream iStream) throws LearningpodException {
		// TODO Auto-generated method stub
		XStream xs = new XStream();		
		xs.autodetectAnnotations(true);
		xs.ignoreUnknownElements();
		// set aliases	
		xs.alias("learningpod",QuestionBean.class);
		xs.alias("simpleChoice",QuestionChoiceBean.class);
		question = (QuestionBean)xs.fromXML(iStream);
		
	}
	
	
}
