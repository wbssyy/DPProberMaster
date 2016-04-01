package com.dpmfc.core;

import java.util.ArrayList;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class TemplateMethodCodeAnalysis extends CodeAnalysis {

	private ArrayList<String> templateMethod;
	private String abstractClass;
	private boolean isAbstract = false;
	
	public TemplateMethodCodeAnalysis(String path) {
		super(path);
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		
		isAbstract = false;
		for (Object object : node.modifiers()) {
			if (object.toString().equals("abstract")) {
				isAbstract = true;
				templateMethod = new ArrayList<String>();
				abstractClass = node.getName().toString();
			}
		}
		return super.visit(node);
	}
	
	@Override
	public void endVisit(TypeDeclaration node) {

		if (isAbstract) {
			
			MethodDeclaration[] methodDeclarations = node.getMethods();
			for (MethodDeclaration methodDeclaration : methodDeclarations) {
				methodDeclaration.accept(new MethodAnalysis());
			}
		}
		super.endVisit(node);
	}

	public ArrayList<String> getTemplateMethod() {
		return templateMethod;
	}

	public String getAbstractClass() {
		return abstractClass;
	}

	class MethodAnalysis extends ASTVisitor {
		private int callTime = 0;         // the number of internal methods that called in this method
		private HashSet<String> calledMethods = new HashSet<String>();
		@Override
		public boolean visit(MethodDeclaration node) {
			node.accept(new MethodInvocationAnalysis());
			
			if (calledMethods.size() > 2) {
				templateMethod.add(node.getName().toString());
			}
			return super.visit(node);
		}
		
		class MethodInvocationAnalysis extends ASTVisitor {
			@Override
			public boolean visit(MethodInvocation node) {
				String invocationMethod = node.getName().toString();
				for (Object methodName : methodList) {
					if (methodName.toString().contains(invocationMethod)) {
						calledMethods.add(invocationMethod);
					}
				}
				return super.visit(node);
			}
		}
	}
}
