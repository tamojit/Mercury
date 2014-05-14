package com.learningpod.android.beans.pods;

import java.io.Serializable;

import com.thoughtworks.xstream.annotations.XStreamAsAttribute;

public class PodQuestionExplanation implements Serializable {

	@XStreamAsAttribute
	private String itemId;
	@XStreamAsAttribute
	private String accessId;
	@XStreamAsAttribute
	private String elementId;
	
	
	public String getItemId() {
		return itemId;
	}
	public void setItemId(String itemId) {
		this.itemId = itemId;
	}
	
	
	
}
