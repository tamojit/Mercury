package com.learningpod.android.beans.pods;

import java.io.Serializable;
import java.util.List;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class PodQuestionBean implements Serializable {

	@XStreamAsAttribute
	private String itemId;
	@XStreamAsAttribute
	private String accessId;
	@XStreamAsAttribute
	private String elementId;
	@XStreamAsAttribute
	private String version;
	@XStreamAsAttribute
	private String score;
	@XStreamAsAttribute
	private String questionNumber;	
	private List<PodQuestionExplanation> explanations = null;
	
	
	
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	public List<PodQuestionExplanation> getExplanations() {
		return explanations;
	}
	public void setExplanations(List<PodQuestionExplanation> explanations) {
		this.explanations = explanations;
	}
	
	
}
