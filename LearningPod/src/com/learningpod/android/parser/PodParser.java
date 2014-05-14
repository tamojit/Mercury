package com.learningpod.android.parser;

import java.io.InputStream;

import com.learningpod.android.beans.pods.PodBean;
import com.learningpod.android.beans.pods.PodQuestionBean;
import com.learningpod.android.beans.pods.PodQuestionExplanation;
import com.learningpod.android.utility.LearningpodException;
import com.thoughtworks.xstream.XStream;

public class PodParser implements GenericParser {

	private PodBean pod = null;
	@Override
	public Object retrieveSerializedObject() {
		// TODO Auto-generated method stub
		return pod;
	}

	@Override
	public void parse(InputStream iStream) throws LearningpodException {
		// TODO Auto-generated method stub
		XStream xs = new XStream();
		
		xs.autodetectAnnotations(true);
		// set aliases
		xs.alias("pod",PodBean.class);
		xs.alias("question", PodQuestionBean.class);
		xs.alias("explanation", PodQuestionExplanation.class);
		// deserialize
		pod = (PodBean)xs.fromXML(iStream);		
	}

}
