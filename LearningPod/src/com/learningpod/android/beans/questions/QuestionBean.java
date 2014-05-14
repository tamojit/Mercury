package com.learningpod.android.beans.questions;

import java.io.Serializable;
import java.util.List;

import com.learningpod.android.parser.QuestionBodyConverter;
import com.thoughtworks.xstream.annotations.XStreamConverter;

public class QuestionBean implements Serializable {

	private QuestionChoiceQuestionBean choiceQuestion;

	public QuestionChoiceQuestionBean getChoiceQuestion() {
		return choiceQuestion;
	}

	public void setChoiceQuestion(QuestionChoiceQuestionBean choiceQuestion) {
		this.choiceQuestion = choiceQuestion;
	}
	
	
}
