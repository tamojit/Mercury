package com.learningpod.android.parser;



import android.util.Log;

import com.learningpod.android.beans.questions.QuestionBodyBean;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
 

public class QuestionBodyConverter  implements Converter {

	
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
			String nodeValue = reader.getValue().trim().replace("\n", "");
			
			/*Parsing region for getting the question content */			
			if(nodeName.equals("p") && !nodeValue.equalsIgnoreCase("")){
				questionBody = questionBody + nodeValue;
			}
			if(nodeName.equals("i") && !nodeValue.equalsIgnoreCase("")){
				if(previousNodeName.equals("p")){ 
					questionBody = questionBody + " <i>" + nodeValue + "</i> ";
				}
				if(previousNodeName.equals("b")){
					questionBodyHighlighted = questionBodyHighlighted + " <i>" + nodeValue + "</i> ";
				}
				if(previousNodeName.equals("u")){
					questionBodyHighlighted = questionBodyHighlighted + " <u><b><i>" + nodeValue + "</i></b></u> ";
				}
				
			}
			
			/*Parsing region for getting the question highlighted content*/
			if(nodeName.equals("b") && !nodeValue.equalsIgnoreCase("")){
				questionBodyHighlighted = questionBodyHighlighted + nodeValue;
			}
			if(nodeName.equals("u") && !nodeValue.equalsIgnoreCase("")){
				if(previousNodeName.equals("p")){ 
					questionBody = questionBody + " <u><b>" + nodeValue + "</b></u> ";
				}
				if(previousNodeName.equals("b")){
					questionBodyHighlighted = questionBodyHighlighted + " <u><b>" + nodeValue + "</b></u> ";
				}	
				if(previousNodeName.equals("i")){
					questionBodyHighlighted = questionBodyHighlighted + " <i><u><b>" + nodeValue + "</b></u></i> ";
				}
			}
			
			
			if(nodeName.equalsIgnoreCase("img")){
				body.setQuestionImage(reader.getAttribute(0));
			}
			if(reader.hasMoreChildren()){
				previousNodeName = reader.getNodeName();
				reader.moveDown();
			}else{
				if(reader.getNodeName().equalsIgnoreCase(parentNodeName)){
					break;
				}
				reader.moveUp();
			}
			
		}
		body.setQuestionBodyStr(questionBody);
		body.setQuestionBodyHighlighted(questionBodyHighlighted);
		Log.v("question","exiting question converter");
		return body;	
	}
	
	

	

}
