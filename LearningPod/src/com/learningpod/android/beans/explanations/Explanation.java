package com.learningpod.android.beans.explanations;

import java.io.Serializable;

import com.learningpod.android.parser.ExplanationConverter;
import com.thoughtworks.xstream.annotations.XStreamConverter;

public class Explanation implements Serializable{

	@XStreamConverter(ExplanationConverter.class)
	private String explanationBody;

	public String getExplanationBody() {
		return explanationBody;
	}

	public void setExplanationBody(String explanationBody) {
		this.explanationBody = explanationBody;
	}
}
