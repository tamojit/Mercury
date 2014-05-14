package com.learningpod.android.rest;

import java.util.HashMap;

import com.learningpod.android.parser.ParserType;
import com.learningpod.android.utility.LearningpodException;




public interface HttpRestServiceHandler {

	public Object fireHttpGet(String baseUrl,HashMap<String,String> params,ParserType type) throws LearningpodException;
	public Object fireHttspGet(String baseUrl,HashMap<String,String> params,ParserType type) throws LearningpodException;
	public Object fireHttpPost(String baseUrl,HashMap<String,String> params,ParserType type,String postContent,String contentType) throws LearningpodException;
	public Object fireHttspPost(String baseUrl,HashMap<String,String> params,ParserType type,String postContent,String contentType) throws LearningpodException;
    public Object fireHttpPut(String baseUrl,HashMap<String,String> params,ParserType type,String postContent, String contentType) throws LearningpodException;
}