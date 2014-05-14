package com.learningpod.android.rest;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Map.Entry;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;

import com.learningpod.android.parser.GenericParser;
import com.learningpod.android.parser.ParserFactory;
import com.learningpod.android.parser.ParserType;
import com.learningpod.android.utility.LearningpodException;



public class ApacheHttpRestServiceHandler implements HttpRestServiceHandler {

	// When a HTTP Get Method is used

	public Object fireHttpGet(String baseUrl, HashMap<String, String> params,
			ParserType type) throws LearningpodException {
		InputStream iStream = null;
		String completeUrl = createUrl(baseUrl, params, type);
		System.out.println(completeUrl);
		try {
			HttpGet httpGet = new HttpGet(completeUrl);
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpGet);
		//	int statusCode = response.getStatusLine().getStatusCode();
			iStream = response.getEntity().getContent();
			GenericParser parser = ParserFactory.getParser(type);
			String responseStr = convertStreamToString(iStream); 
			System.out.println(responseStr);
			
			iStream = new ByteArrayInputStream(responseStr.getBytes());
			parser.parse(iStream);
			return parser.retrieveSerializedObject();

		} catch (ClientProtocolException e) {
			throw new LearningpodException(e.getMessage(),
					"Error in firing Web service Request - HTTP GET - "
							+ " Exception Message- " + e.getMessage(),
					e.getStackTrace());
		} catch (IOException e) {
			throw new LearningpodException(e.getMessage(),
					"Error in firing Web service Request  - HTTP GET - "
							+ " Exception Message- " + e.getMessage(),
					e.getStackTrace());
		}

	}

	public Object fireHttspGet(String baseUrl, HashMap<String, String> params,
			ParserType type) throws LearningpodException {

		InputStream iStream = null;
		String completeUrl = createUrl(baseUrl, params, type);
		System.out.println(completeUrl);
		try {
			HttpGet httpGet = new HttpGet(completeUrl);
			HttpClient httpclient = new DefaultHttpClient();
			HttpResponse response = httpclient.execute(httpGet);
		//	int statusCode = response.getStatusLine().getStatusCode();
			iStream = response.getEntity().getContent();
			GenericParser parser = ParserFactory.getParser(type);
			String responseStr = convertStreamToString(iStream); 
			System.out.println(responseStr);
			
			iStream = new ByteArrayInputStream(responseStr.getBytes());
			parser.parse(iStream);
			return parser.retrieveSerializedObject();

		} catch (ClientProtocolException e) {
			throw new LearningpodException(e.getMessage(),
					"Error in firing Web service Request - HTTP GET - "
							+ " Exception Message- " + e.getMessage(),
					e.getStackTrace());
		} catch (IOException e) {
			throw new LearningpodException(e.getMessage(),
					"Error in firing Web service Request  - HTTP GET - "
							+ " Exception Message- " + e.getMessage(),
					e.getStackTrace());
		}

	}

	// When a HTTP Post Method is used
	public Object fireHttpPost(String baseUrl, HashMap<String, String> params,
			ParserType type, String postContent, String contentType)
			throws LearningpodException {
		HttpPost httppost = null;
		InputStream iStream = null;

		StringEntity se = null;
		HttpClient httpclient = new DefaultHttpClient();

		String completeUrl = createUrl(baseUrl, params, type);
		System.out.println(completeUrl);
		httppost = new HttpPost(completeUrl);
		System.out.println("Post content - " + postContent);

		try {
			se = new StringEntity(postContent);
			httppost.setHeader("content-type", contentType);
			httppost.setEntity(se);
			HttpResponse httpresponse = httpclient.execute(httppost);
			HttpEntity resEntity = httpresponse.getEntity();
			iStream = resEntity.getContent();
			GenericParser parser = ParserFactory.getParser(type);
			parser.parse(iStream);

			return parser.retrieveSerializedObject();

		} catch (ClientProtocolException e) {
			throw new LearningpodException(e.getMessage(),
					"Error in firing Web service Request - HTTPS GET - "
							+ baseUrl + " --Post Content" + postContent
							+ " Exception Message- " + e.getMessage(),
					e.getStackTrace());
		} catch (IOException e) {
			throw new LearningpodException(e.getMessage(),
					"Error in firing Web service Request - HTTPS GET - "
							+ baseUrl + " --Post Content" + postContent
							+ " Exception Message- " + e.getMessage(),
					e.getStackTrace());
		}

	}

	public Object fireHttspPost(String baseUrl, HashMap<String, String> params,
			ParserType type, String postContent) throws LearningpodException {

		return null;
	}

	// Creates the URL
	private String createUrl(String baseUrl, HashMap<String, String> params,
			ParserType type) {
		StringBuffer url = new StringBuffer(baseUrl);
		for (Entry<String, String> entry : params.entrySet()) {
			url.append(entry.getKey());
			url.append("=");
			String value = URLEncoder.encode(entry.getValue());
			url.append(value);
			url.append("&");
		}
		url.deleteCharAt(url.length() - 1);
		return url.toString();

	}

	public Object fireHttpPut(String baseUrl, HashMap<String, String> params,
			ParserType type, String postContent, String contentType)
			throws LearningpodException {

		try {
			HttpClient http = new DefaultHttpClient();
			String completeUrl = createUrl(baseUrl, params, type);
			System.out.println(completeUrl);
			HttpPut putmethod = new HttpPut(completeUrl);
			putmethod.setEntity(new StringEntity(postContent));
			putmethod.setHeader("content-type", contentType);
			HttpResponse httpresponse = null;
			httpresponse = http.execute(putmethod);
			HttpEntity resEntity = httpresponse.getEntity();
			InputStream iStream = null;
			iStream = resEntity.getContent();
			GenericParser parser = ParserFactory.getParser(type);
			parser.parse(iStream);
			return parser.retrieveSerializedObject();

		} catch (ClientProtocolException e) {
			throw new LearningpodException(e.getMessage(),
					"Error in firing Web service Request - HTTPS GET - "
							+ baseUrl + " --Post Content" + postContent
							+ " Exception Message- " + e.getMessage(),
					e.getStackTrace());
		} catch (IOException e) {
			throw new LearningpodException(e.getMessage(),
					"Error in firing Web service Request - HTTPS GET - "
							+ baseUrl + " --Post Content" + postContent
							+ " Exception Message- " + e.getMessage(),
					e.getStackTrace());
		}

	}

	public Object fireHttspPost(String baseUrl, HashMap<String, String> params,
			ParserType type, String postContent, String contentType)
			throws LearningpodException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private  String convertStreamToString(InputStream is) {
	    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
	    StringBuilder sb = new StringBuilder();
	    String line = null;
	    try{
	    while ((line = reader.readLine()) != null) {
	        sb.append(line);
	    }

	    is.close();
	    }catch(Exception e){
	    	
	    }

	    return sb.toString();
	}

}
