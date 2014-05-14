package com.learningpod.android.beans;

public class UserProgressInfo {

	private String podId;
	private String userId;
	private String questionId;	
	private String choiceId;
	private String selectedChoiceId;
	
	private boolean isChoiceCorrect;
	
	
	public String getPodId() {
		return podId;
	}
	public void setPodId(String podId) {
		this.podId = podId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getQuestionId() {
		return questionId;
	}
	public void setQuestionId(String questionId) {
		this.questionId = questionId;
	}
	public String getChoiceId() {
		return choiceId;
	}
	public void setChoiceId(String choiceId) {
		this.choiceId = choiceId;
	}
	public boolean isChoiceCorrect() {
		return isChoiceCorrect;
	}
	public void setChoiceCorrect(boolean isChoiceCorrect) {
		this.isChoiceCorrect = isChoiceCorrect;
	}
	public String getSelectedChoiceId() {
		return selectedChoiceId;
	}
	public void setSelectedChoiceId(String selectedChoiceId) {
		this.selectedChoiceId = selectedChoiceId;
	}
	
}
