package com.learningpod.android.parser;

import java.io.InputStream;

import com.learningpod.android.utility.LearningpodException;

/**
 * This is the parent interface for all types of parsers. abstracts out the 
 * type of content to be parsed and provides a unified interface 
 * for business layer
 * @author Tamo
 *
 */
public interface GenericParser {

	public Object retrieveSerializedObject();
	public void parse(InputStream iStream) throws LearningpodException;
	
}
