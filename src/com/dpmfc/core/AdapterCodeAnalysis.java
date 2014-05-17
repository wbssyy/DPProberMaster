package com.dpmfc.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.dpmfc.bean.PatternInstance;
import com.dpmfc.test.JavaCode2AST;

public class AdapterCodeAnalysis extends CodeAnalysis {
	
	// save all methodInvocation in a method
	private List methodIvocationList;

	public AdapterCodeAnalysis(String path, String flag){
		super(path, flag);
		this.flag = flag;
	}
	
	public AdapterCodeAnalysis(String path) {
		super(path);
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {

		if (node.isInterface()) {
			flag = "target";
		}
		return super.visit(node);
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		
		if (flag != null && flag.equals("adapter")) {
			String name = ((VariableDeclarationFragment)node.fragments().get(0)).getName().toString();
			String type = node.getType().toString();

			fieldNameAndType.put(name, type);
		}
		return super.visit(node);
	}

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
		
//		System.out.println(flag + "methodDeclaration");
		
		// if the class is a adapter candidate, check methodIvocation for each method
		if (flag != null && flag.equals("adapter")) {
			
			methodIvocationList = new ArrayList();
			node.accept(new AdapterMethodIvocation());
//			System.out.println("method declaration");
			methodInvoMap.put(method.toString(), methodIvocationList);
		}

		return super.visit(node);
	}

	/*
	 * A inner class which check all the method invocations in a method
	 */
	class AdapterMethodIvocation extends ASTVisitor{
		
		@Override
		public boolean visit(MethodInvocation node) {
			String expression = "";
			String methodIvocation = node.toString();
			methodIvocationList.add(methodIvocation);
			
			return super.visit(node);
		}
	}
}
