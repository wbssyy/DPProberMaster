package com.dpmfc.core;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.dpmfc.test.JavaCode2AST;

/*
 * CodeAnalysis is the super class of all code analysis of all patterns. 
 */
public abstract class CodeAnalysis extends ASTVisitor {
	
	/*
	 * key: a method 
	 * value: methodInvocation in the method of the class
	 */
	protected HashMap<String, List> methodInvoMap = new HashMap<String, List>();
	
	// save all methods of a class
	protected List methodList;
	
	// save all class that in the same file
	protected HashMap<String, HashMap<String, String>> typeMap = new HashMap<String, HashMap<String,String>>();

	/*
	 * key: field name
	 * value: field type 
	 */
	protected HashMap fieldNameAndType = new HashMap<String, String>();
	
	/*
	 * flag represent the role of the candidate class. For different role, 
	 * there is a different operation.
	 */
	protected String flag = "";

	public CodeAnalysis(String path, String flag){
		super();
		this.flag = flag;
		init(path);
	}
	
	public CodeAnalysis(String path) {
		super();
		init(path);
	}
	
	private void init(String path) {
		
		methodList = new ArrayList();
		CompilationUnit compUnit;
		try {
			compUnit = JavaCode2AST.getCompilationUnit(path);
			compUnit.accept(this);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	@Override
//	public boolean visit(FieldDeclaration node) {
//		
//		String name = ((VariableDeclarationFragment)node.fragments().get(0)).getName().toString();
//		String type = node.getType().toString();
//		fieldNameAndType.put(name, type);
//
//		return super.visit(node);
//	}
	
	@Override
	public boolean visit(MethodDeclaration node) {

		String methodName = node.getName().toString();
		List<SingleVariableDeclaration> parameter = node.parameters();
		
		StringBuilder method = new StringBuilder();
		method.append(methodName + "(");
		
		for (SingleVariableDeclaration string : parameter) {
			method.append(string.getType().toString() + ",");
		}
		
		// get all method of the class 
		methodList.add(method.toString());
		
		return super.visit(node);
	}

	public HashMap<String, HashMap<String, String>> getTypeMap() {
		return typeMap;
	}
	
	public HashMap<String, List> getMethodInvoMap() {
		return methodInvoMap;
	}

	public void setMethodInvoMap(HashMap<String, List> methodInvoMap) {
		this.methodInvoMap = methodInvoMap;
	}

	public List getMethodList() {
		return methodList;
	}

	public void setMethodList(List methodList) {
		this.methodList = methodList;
	}

	public HashMap getFieldNameAndType() {
		return fieldNameAndType;
	}

	public void setFieldNameAndType(HashMap fieldNameAndType) {
		this.fieldNameAndType = fieldNameAndType;
	}

	public String getFlag() {
		return flag;
	}

	public void setFlag(String flag) {
		this.flag = flag;
	}
	
//	public abstract void doAnalysis(PatternInstance pInstance);
}
