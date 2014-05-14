package com.learningpod.android.utility;

public class LearningpodException extends Exception {

	private String logMessage;
	private String message;
	
	public LearningpodException(final String message, final String logMessage,final StackTraceElement[] trace ){
		this.message = message;
		this.logMessage = logMessage;
		this.setStackTrace(trace);
	}
	
	public String getLogMessage() {
		return logMessage;
	}


	@Override
	public String getMessage() {
		return message;
	}
}
