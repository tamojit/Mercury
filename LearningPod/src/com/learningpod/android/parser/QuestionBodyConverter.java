package com.learningpod.android.parser;



import android.util.Log;

import com.learningpod.android.beans.questions.QuestionBodyBean;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
 

public class QuestionBodyConverter  implements Converter {

	private boolean isForHighlightedpart = false;
	@Override
	public boolean canConvert(Class cl) {
		return true;
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter arg1,
			MarshallingContext arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext context) {
		// TODO Auto-generated method stub
		Log.v("question","coming to question converter");
		QuestionBodyBean body = new QuestionBodyBean();
		String questionBody = "";
		String questionBodyHighlighted = "";
		String parentNodeName = reader.getNodeName();
		String previousNodeName = parentNodeName;
		boolean flag  = true;
		while(flag){
			String nodeName = reader.getNodeName();
			String nodeValue = reader.getValue().replace("\n", "");;
			/*Parsing region for getting the question highlighted content*/
			if(nodeName.equals("b") ){
				if(!nodeValue.equalsIgnoreCase("")){
					questionBodyHighlighted = questionBodyHighlighted + nodeValue;
				}
				isForHighlightedpart = true;
			}
			/*Parsing region for getting the question content */			
			if(nodeName.equals("p") && !nodeValue.equalsIgnoreCase("")){
				if(isForHighlightedpart){
					questionBodyHighlighted = questionBodyHighlighted + nodeValue;
				}
				else{
					questionBody = questionBody + nodeValue;
				}
			}
			if(nodeName.equals("i") && !nodeValue.equalsIgnoreCase("")){
				 
				if(isForHighlightedpart){
					if(previousNodeName.equals("u")){
						questionBodyHighlighted = questionBodyHighlighted + "<u><i>" + nodeValue + "</i></u>";
					}else{
						questionBodyHighlighted = questionBodyHighlighted + "<i>" + nodeValue + "</i>";
					}
				}
				else{
					if(previousNodeName.equals("u")){
						questionBody = questionBody + "<u><i>" + nodeValue + "</i></u>";
					}
					else {
						questionBody = questionBody + "<i>" + nodeValue + "</i>";
					}
				}
				
				
			}
			
			
			if(nodeName.equals("u") && !nodeValue.equalsIgnoreCase("")){
				
				if(isForHighlightedpart){
					if(previousNodeName.equals("i")){
						 questionBodyHighlighted = questionBodyHighlighted + "<i><u>" + nodeValue + "</u></i>";
					}
					else{
						questionBodyHighlighted = questionBodyHighlighted + "<u>" + nodeValue + "</u>";
					}
				}	
				else{
					if(previousNodeName.equals("i")){
						questionBody = questionBody + "<i><u>" + nodeValue + "</u></i>";
					}
					else{
						questionBody = questionBody + "<u>" + nodeValue + "</u>";
					}
				}
				
			}
			
			if(nodeName.equals("br")){
				if(isForHighlightedpart){
					questionBodyHighlighted = questionBodyHighlighted +"<br/>";
				}
				else{
					//questionBody = questionBody + System.getProperty("line.separator");
							
				}
			}
			
			if(reader.hasMoreChildren()){
				previousNodeName = reader.getNodeName();
				reader.moveDown();
			}else{
				if(reader.getNodeName().equalsIgnoreCase(parentNodeName)){
					break;
				}
				if(reader.getNodeName().equalsIgnoreCase("b")){
					isForHighlightedpart = false;
				}
				reader.moveUp();
			}
			
		}
		// check for spaces before punctuation
		/*questionBody=questionBody.replace(" .", ".");
		questionBody=questionBody.replace(" ,", ",");
		questionBody=questionBody.replace(" ?", "?");
		questionBodyHighlighted=questionBodyHighlighted.replace(" .", ".");
		questionBodyHighlighted=questionBodyHighlighted.replace(" ,", ",");
		questionBodyHighlighted=questionBodyHighlighted.replace(" ?", "?");*/
		
		
		body.setQuestionBodyStr(questionBody);
		body.setQuestionBodyHighlighted(questionBodyHighlighted);
		Log.v("question","exiting question converter");
		return body;	
	}
	
	

	

}
