package com.dpmfc.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtil {
	
	public static Object getClassFromXML() throws Exception {
		//create DOM object
		DocumentBuilderFactory docBuilderFactory = 
				DocumentBuilderFactory.newInstance();
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document document = docBuilder.parse(new File("property.xml"));
		
		//get the node name and get the class name
		NodeList nodeList = document.getElementsByTagName("className");
		Node node = nodeList.item(0).getFirstChild();
		String className = node.getNodeValue();
		
		//return an object through the class name
		Class tempClass = Class.forName(className);
		Object object = tempClass.newInstance();
		return object;
	}

}
