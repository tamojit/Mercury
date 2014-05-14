package com.learningpod.android.beans.questions;

import java.io.Serializable;

public class QuestionBodyBean implements Serializable{

	private String questionBodyHighlighted;	
	private String questionBodyStr;
	private String questionImage;
	
	
	public String getQuestionBodyStr() {
		return questionBodyStr;
	}
	public void setQuestionBodyStr(String questionBodyStr) {
		this.questionBodyStr = questionBodyStr;
	}
	public String getQuestionImage() {
		return questionImage;
	}
	public void setQuestionImage(String questionImage) {
		this.questionImage = questionImage;
	}
	public String getQuestionBodyHighlighted() {
		return questionBodyHighlighted;
	}
	public void setQuestionBodyHighlighted(String questionBodyHighlighted) {
		this.questionBodyHighlighted = questionBodyHighlighted;
	}
	
}
