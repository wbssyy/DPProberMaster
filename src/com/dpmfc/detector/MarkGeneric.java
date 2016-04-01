package com.dpmfc.detector;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import com.dpmfc.util.XMLUtil;

public class MarkGeneric {
	
	/*
	 * key: field name
	 * value: field type
	 */
	private HashMap<String, String> fieldAndTypeMap;
	
	/*
	 * key: field name
	 * value: the class where the field in
	 */
	private HashMap<String, String> fieldAndClassMap;
	
	private ArrayList<String> expressionList;
	private String parameterizedType;
	private String sourceClassName;
	
	static int iii = 1;
	
	public MarkGeneric() {
		fieldAndTypeMap = new HashMap<String, String>();
		fieldAndClassMap = new HashMap<String, String>();
		expressionList = new ArrayList<String>();
	}
	
	
	/*
	 * Determine whether there is a add() and a remove() call by the field
	 */
	public void hasAddAndRemoveCall(String classPath) {

		Iterator iterator = fieldAndTypeMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			String fieldName = entry.getKey().toString();
			
			boolean addFlag = false;			
			for (String expression : expressionList) {
				
				String expressionName = expression.substring(0, expression.indexOf("."));
				String expressionMethod = expression.substring(expression.indexOf("."));
				
				if (fieldName.contains("=")) {
					fieldName = fieldName.substring(0, fieldName.indexOf("="));
				}
				if (expressionName.equals(fieldName) && (expressionMethod.contains("add") ||
						expressionMethod.contains("contains"))) {
					getParameterizedType(classPath, expression);
					addFlag = true;
				}
				if (addFlag && expressionMethod.contains("indexOf")) {
					getParameterizedType(classPath, expression);
				}
				
				if (parameterizedType != null) {
					break;
				}
			}
			if (!addFlag) {
				iterator.remove();
			}
		}
	}
	
	
	/*
	 * 
	 */
	public void printGeneric(String classPath) {
		
		Iterator iterator = fieldAndTypeMap.entrySet().iterator();
		
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			String fieldName = entry.getKey().toString();
			String fieldType = entry.getValue().toString();
			
			System.out.println(iii++ + " " +fieldType + "<" + parameterizedType +
					">" + " " + fieldName + "; " + classPath);
		}
	}
	
	public void setSourceClassName(String source) {
		sourceClassName = source;
	}
	
	public String getSourceClassName() {
		return sourceClassName;
	}
	
	public String getParameterizedType() {
		return parameterizedType;
	}
	
	/*
	 * get generic parameterized type
	 */
	public void getParameterizedType(String classPath, String expression) {
		
		classPath = classPath.replace('\\', ' ');
		classPath = classPath.replaceAll(" ", "\\\\");
//		System.out.println(classPath);
		try {
			AddGeneric addGeneric = new AddGeneric(classPath, expression);
			parameterizedType = addGeneric.getParameterizedType();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	/*
	 * Determine whether the field is a generic
	 */
	public void checkGenericByField(String source, FieldDeclaration node) {
		
		if (!node.getType().isParameterizedType()) {
				String fieldName = node.fragments().get(0).toString();
				if (fieldAndTypeMap != null) {
					fieldAndTypeMap.put(fieldName, node.getType().toString());	
					
					fieldAndClassMap.put(fieldName, source);
				}
		}
	}
	
	
	/*
	 * collects all method invocation that in a class. In order to determine whether there
	 *  a add() or remove() in hasAddOrRemoveCall().
	 */
	public void collectMethodCall(MethodInvocation node) {

		if (node.getExpression() != null && expressionList != null) {
			String expression = node.toString();	
			expressionList.add(expression);
//			System.out.println("expression: " + expression);
		}
	}
	
	/*
	 * 判断字段的类型是否为系统内的泛型
	 */
	public void checkGenericInSystem(List genericClassList, List JDKGenericList) {
		
		Iterator iterator = fieldAndTypeMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			String fieldName = entry.getKey().toString();
			String fieldType = entry.getValue().toString();
			
			boolean flag = false;
			
			for (Object generic : genericClassList) {
				if (generic.toString().contains(fieldType)) {
					flag = true;
				}
			}
			
			for (Object JDKGeneric : JDKGenericList) {
				if (JDKGeneric.toString().contains(fieldType)) {
					flag = true;
				}
			}
			
			if (!flag) {
				iterator.remove();
			} else {
				sourceClassName = fieldAndClassMap.get(fieldName);
			}
		}
	}
}
