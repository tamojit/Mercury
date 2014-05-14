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
		choiceBean.setCorrect(reader.getAttribute(0));
		choiceBean.setChoiceId(reader.getAttribute(1));
		
		String parentNodeName = reader.getNodeName();
		String choiceBody = "";
		boolean flag  = true;
		while(flag){
			String nodeName = reader.getNodeName();
			String nodeValue = reader.getValue().trim().replace("\n", "");
			if(nodeName.equals("p") && !nodeValue.equalsIgnoreCase("")){
				choiceBody = choiceBody + nodeValue;
			}
			
			if(nodeName.equals("i") && !nodeValue.equalsIgnoreCase("")){
				choiceBody = choiceBody + " <i>" + nodeValue + "</i> ";
			}
			
			
			if(reader.hasMoreChildren()){
				reader.moveDown();
			}else{
				if(reader.getNodeName().equalsIgnoreCase(parentNodeName)){
					break;
				}
				reader.moveUp();
			}
		}
		choiceBean.setChoiceBody(choiceBody);
		return choiceBean;
	}

}
