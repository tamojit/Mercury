package com.learningpod.android.parser;

import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;

public class ExplanationConverter implements Converter {

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
		String explanationBody = "";
		String parentNodeName = reader.getNodeName();
		boolean flag  = true;
		while(flag){
			String nodeName = reader.getNodeName();
			String nodeValue = reader.getValue().trim().replace("\n", "");
			if(nodeName.equals("p") && !nodeValue.equalsIgnoreCase("")){
				explanationBody = explanationBody + nodeValue;
			}
			
			if(nodeName.equals("i") && !nodeValue.equalsIgnoreCase("")){
				explanationBody = explanationBody + " <i>" + nodeValue + "</i> ";
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
		return explanationBody;
	}

}
