package com.dpmfc.core;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class SingletonCodeAnalysis extends CodeAnalysis {
	
	private String singleton;
	
	/*
	 * key: modifier of the constructor
	 * value: constructor
	 */
	private HashMap<String, String> constructorMap;
	
	private boolean staticField;

	public SingletonCodeAnalysis(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		if (node.getName() != null) {
			singleton = node.getName().toString();	
			
			constructorMap = new HashMap<String, String>();	
			staticField = false;
			
//			System.out.println(singleton+ " type");
			
			FieldDeclaration[] singletonFields = node.getFields();
			for (FieldDeclaration fieldDeclaration : singletonFields) {
				fieldDeclaration.accept(new SingletonFieldAnalysis());
			}
			
			MethodDeclaration[] singletonMethods = node.getMethods();
			for (MethodDeclaration methodDeclaration : singletonMethods) {
				methodDeclaration.accept(new SingletonMethodAnalysis());
			}
		}
	
		if (staticField) {
			typeMap.put(singleton, constructorMap);
		}
		
		return super.visit(node);
	}

	class SingletonFieldAnalysis extends ASTVisitor {
		@Override
		public boolean visit(FieldDeclaration node) {

			if (node.getType().toString().equals(singleton)) {
				for (Object modifier : node.modifiers()) {
					if (modifier.toString().equals("static")) {
						staticField = true;
					}
				}
			}
			return super.visit(node);
		}
	}
	
	class SingletonMethodAnalysis extends ASTVisitor {
		@Override
		public boolean visit(MethodDeclaration node) {
		
			if (node.isConstructor() && node.getName().toString().equals(singleton) && node.modifiers().size()>0) {
				constructorMap.put(node.modifiers().get(0).toString(), node.getName().toString());
//				System.out.println(singleton+ " method");
			}
			
			if (node.getReturnType2()!=null && constructorMap != null) {
				String returnType = node.getReturnType2().toString();
				if (returnType.equals(singleton)) {
					methodList.add(singleton);
//					System.out.println(node.getName());
				}
			}
			
			return super.visit(node);
		}	
	}
}
