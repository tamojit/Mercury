package com.learningpod.android.beans.questions;

import java.io.Serializable;
import java.util.List;

import com.learningpod.android.parser.ExplanationConverter;
import com.learningpod.android.parser.QuestionChoiceConverter;
import com.thoughtworks.xstream.annotations.XStreamAlias;
import com.thoughtworks.xstream.annotations.XStreamAsAttribute;
import com.thoughtworks.xstream.annotations.XStreamConverter;
import com.thoughtworks.xstream.annotations.XStreamImplicit;
import com.thoughtworks.xstream.annotations.XStreamImplicitCollection;
import com.thoughtworks.xstream.annotations.XStreamOmitField;
 

public class QuestionChoiceBean implements Serializable {

	 
	private String correct;
	private String choiceId;	 
	private String choiceBody;
	public String getCorrect() {
		return correct;
	}
	public void setCorrect(String correct) {
		this.correct = correct;
	}
	public String getChoiceId() {
		return choiceId;
	}
	public void setChoiceId(String choiceId) {
		this.choiceId = choiceId;
	}
	public String getChoiceBody() {
		return choiceBody;
	}
	public void setChoiceBody(String choiceBody) {
		this.choiceBody = choiceBody;
	}
	
	
	
	 
	 
	
	
}
