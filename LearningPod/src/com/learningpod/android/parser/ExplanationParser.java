package com.learningpod.android.parser;

import java.io.InputStream;

import com.learningpod.android.beans.explanations.ExplanationBean;
import com.learningpod.android.utility.LearningpodException;
import com.thoughtworks.xstream.XStream;

public class ExplanationParser implements GenericParser{

	private ExplanationBean explanation;
	@Override
	public Object retrieveSerializedObject() {
		// TODO Auto-generated method stub
		return explanation;
	}

	@Override
	public void parse(InputStream iStream) throws LearningpodException {
		// TODO Auto-generated method stub
		XStream xs = new XStream();		
		xs.autodetectAnnotations(true);
		xs.ignoreUnknownElements();
		// set aliases	
		xs.alias("learningpod",ExplanationBean.class);
		explanation = (ExplanationBean)xs.fromXML(iStream);
		
	}

}
