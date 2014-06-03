package com.dpmfc.util;

import java.io.File;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class XMLUtil {
	
	public static Object getClassFromXML(String fileName, String tagName) throws Exception {
		
		DocumentBuilderFactory docBuilderFactory = 
				DocumentBuilderFactory.newInstance();
		Document document = createDomObject(docBuilderFactory, fileName);

		String nodeValue = getNodeValue(document, tagName);
		
		return getObjectByNodeValue(nodeValue);
	}
	
	public static String getStringFromXML(String fileName, String tagName) throws Exception {
		
		DocumentBuilderFactory docBuilderFactory = 
				DocumentBuilderFactory.newInstance();
		Document document = createDomObject(docBuilderFactory, fileName);

		String nodeValue = getNodeValue(document, tagName);
		
		return nodeValue;
	}
	
	/*
	 * create DOM object
	 */
	private static Document createDomObject(DocumentBuilderFactory docBuilderFactory, String fileName) throws Exception{
		
		DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		Document document = docBuilder.parse(new File(fileName));	
		return document;
	}
	
	/*
	 * get the node name and get the class name
	 */
	private static String getNodeValue(Document document, String tagName) {
		
		NodeList nodeList = document.getElementsByTagName(tagName);
		Node node = nodeList.item(0).getFirstChild();
		String nodeValue = node.getNodeValue();
		return nodeValue;
	}
	
	/*
	 * return an object through the node value
	 */
	private static Object getObjectByNodeValue(String nodeValue) throws Exception{
		
		Class tempClass = Class.forName(nodeValue);
		Object object = tempClass.newInstance();
		return object;
	}

}
