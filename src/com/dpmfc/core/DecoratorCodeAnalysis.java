package com.dpmfc.core;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

import com.dpmfc.core.SingletonCodeAnalysis.SingletonFieldAnalysis;

public class DecoratorCodeAnalysis extends CodeAnalysis{
	
	private List<String> superList;
	private String componentField;
	private String operation;

	public DecoratorCodeAnalysis(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}

	public DecoratorCodeAnalysis(String path, String flag){
		super(path, flag);
		this.flag = flag;
	}

	@Override
	public boolean visit(TypeDeclaration node) {

		if (!flag.equals("")) {
			
//			if (node.getSuperclassType() != null) {
////				componentField = node.getSuperclassType().toString();
////				System.out.println(componentField+" compnentfield");
//			} else if (node.superInterfaceTypes() != null) {
//				superList = node.superInterfaceTypes();
//				for (String superInterface : superList) {
//					System.out.println("superInterface " + superInterface);
//				}
//			}
			
			FieldDeclaration[] singletonFields = node.getFields();
			for (FieldDeclaration fieldDeclaration : singletonFields) {
				fieldDeclaration.accept(new DecoratorFieldAnalysis());
			}
			
			MethodDeclaration[] methodDeclarations = node.getMethods();
			for (MethodDeclaration methodDeclaration : methodDeclarations) {
				methodDeclaration.accept(new DecoratorMethodAnalysis());
			}
		}
		return super.visit(node);
	}
	
	class DecoratorFieldAnalysis extends ASTVisitor {
		@Override
		public boolean visit(FieldDeclaration node) {
			
//			System.out.println( "flag: " + flag );
//			System.out.println("componentField: " + node.getType().toString());
			if (node.getType().toString().equals(flag)) {
				componentField =  ((VariableDeclarationFragment)node.fragments().get(0)).getName().toString();
			}
		
			return super.visit(node);
		}
	}
	
	class DecoratorMethodAnalysis extends ASTVisitor {
		String tempOperationName;
		
		@Override
		public boolean visit(MethodDeclaration node) {
			tempOperationName = node.getName().toString();
			node.accept(new DecoratorMethodInvocation());
			return super.visit(node);
		}
		
		class DecoratorMethodInvocation extends ASTVisitor {
			@Override
			public boolean visit(MethodInvocation node) {
				String invocatedMethod = node.getName().toString();
				String invocationObject = "";
				
				if (Character.isLowerCase(node.toString().charAt(0)) && node.toString().contains(".")) {
//					System.out.println(node.toString().substring(0, node.toString().indexOf(".")));
					invocationObject = node.toString().substring(0, node.toString().indexOf("."));
				}
				
				if (invocationObject.equals(componentField) && tempOperationName.equals(invocatedMethod)) {
					System.out.println(invocatedMethod + " " + componentField + " fuck!!!");
					operation = invocatedMethod;
				}
				return super.visit(node);
			}
		}
	}
	
	public String getComponentField() {
		return componentField;
	}
	
	public String getOperation() {
		return operation;
	}
}
