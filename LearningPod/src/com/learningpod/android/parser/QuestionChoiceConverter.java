package com.learningpod.android.parser;

import java.util.ArrayList;
import java.util.List;

import android.util.Log;

import com.learningpod.android.beans.questions.QuestionChoiceBean;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class QuestionChoiceConverter implements Converter {

	@Override
	public boolean canConvert(Class arg0) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void marshal(Object arg0, HierarchicalStreamWriter arg1,
			MarshallingContext arg2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Object unmarshal(HierarchicalStreamReader reader,
			UnmarshallingContext arg1) {
		// TODO Auto-generated method stub
		Log.v("Choice","coming to choice converter");
		List<QuestionChoiceBean> choiceBeanList = new ArrayList<QuestionChoiceBean>();
		String parentNodeName = reader.getNodeName();
		boolean flag  = true;
		while(flag){
			if(reader.hasMoreChildren()){
				reader.moveDown();
				choiceBeanList.add(parserEachChoice(reader));
				reader.moveUp();
			}else{
				break;
			}
		}
		Log.v("Choice","returning from choice converter");
		return choiceBeanList;
	}
	
	private QuestionChoiceBean parserEachChoice(HierarchicalStreamReader reader){
		QuestionChoiceBean choiceBean = new QuestionChoiceBean();
		choiceBean.setCorrect(reader.getAttribute("correct"));
		choiceBean.setChoiceId(reader.getAttribute("choiceId"));
		
		String parentNodeName = reader.getNodeName();		
		String choiceBody = "";
		if(!reader.getValue().equalsIgnoreCase("")){
			choiceBody = reader.getValue();
		}
		boolean flag  = true;
		// handle the case when there is only text in the choice to avoid going through the loop
		if(!reader.hasMoreChildren()){
			flag=false;
		}
		while(flag){
			String nodeName = reader.getNodeName();
			String nodeValue = reader.getValue().replace("\n", "");
			if(nodeName.equals("p") && !nodeValue.equalsIgnoreCase("")){
				choiceBody = choiceBody + nodeValue;
			}
			
			if(nodeName.equals("i") && !nodeValue.equalsIgnoreCase("")){
				choiceBody = choiceBody + "<i>" + nodeValue + "</i>";
			}
			
			if(nodeName.equals("u") && !nodeValue.equalsIgnoreCase("")){
				choiceBody = choiceBody + "<u>" + nodeValue + "</u>";
			}
			
			if(nodeName.equals("b") && !nodeValue.equalsIgnoreCase("")){
				choiceBody = choiceBody + "<b>" + nodeValue + "</b>";
			}
			
			if(reader.hasMoreChildren()){
				reader.moveDown();
			}else{
				if(reader.getNodeName().equalsIgnoreCase(parentNodeName)){
					choiceBody= choiceBody + nodeValue;
					break;
				}
				reader.moveUp();
			}
		}
		
		/*choiceBody=choiceBody.replace(" .", ".");
		choiceBody=choiceBody.replace(" ,", ",");
		choiceBody=choiceBody.replace(" ?", "?");*/
		choiceBean.setChoiceBody(choiceBody);
		return choiceBean;
	}

}
