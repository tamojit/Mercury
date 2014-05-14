package com.learningpod.android.beans.questions;

import java.io.Serializable;
import java.util.List;

import com.learningpod.android.parser.QuestionBodyConverter;
import com.learningpod.android.parser.QuestionChoiceConverter;
import com.thoughtworks.xstream.annotations.XStreamConverter;

public class QuestionChoiceQuestionBean implements Serializable {

	@XStreamConverter(QuestionBodyConverter.class)
	private QuestionBodyBean questionBody;
	@XStreamConverter(QuestionChoiceConverter.class)
	private List<QuestionChoiceBean> choiceInteraction = null;
	
	
	public QuestionBodyBean getQuestionBody() {
		return questionBody;
	}
	public void setQuestionBody(QuestionBodyBean questionBody) {
		this.questionBody = questionBody;
	}
	
	public List<QuestionChoiceBean> getChoiceInteraction() {
		return choiceInteraction;
	}
	public void setChoiceInteraction(List<QuestionChoiceBean> choiceInteraction) {
		this.choiceInteraction = choiceInteraction;
	}
}
