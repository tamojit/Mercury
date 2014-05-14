package com.learningpod.android.rest;

public class RestServiceHandlerFactory {

	public static HttpRestServiceHandler getServiceHandler(){
		return new ApacheHttpRestServiceHandler();
	}
}
