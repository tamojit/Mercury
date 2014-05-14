package com.learningpod.android.beans.explanations;

import java.io.Serializable;

public class ExplanationBean implements Serializable{

	private Explanation explanation;

	public Explanation getExplanation() {
		return explanation;
	}

	public void setExplanation(Explanation explanation) {
		this.explanation = explanation;
	}
}
