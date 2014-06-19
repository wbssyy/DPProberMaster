package com.dpmfc.detector;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import com.dpmfc.test.JavaCode2AST;

public class AddGeneric extends ASTVisitor{
	
	private String expression;
	private String parameterizedType;
	private String genericFieldName;
	private String tempFieldName;
	String className;
	
	
	public AddGeneric(String classPath, String expression) throws Exception{
		super();

		this.expression = expression;
		
		Pattern pattern = Pattern.compile("\\( *(\\w*)\\)");
		Matcher matcher = pattern.matcher(expression);
		if (matcher.find()) {
			genericFieldName = matcher.group(1);
		} 
		
//		System.out.println(classPath);
//		System.out.println("35: "+expression);
		CompilationUnit compUnit = JavaCode2AST.getCompilationUnit(classPath);
		compUnit.accept(new GenericParaInMethod());
		
		if (tempFieldName != null && parameterizedType == null) {
			compUnit = JavaCode2AST.getCompilationUnit(classPath);
			compUnit.accept(this);
		}
		
		if (parameterizedType != null) {
//			System.out.println(classPath + "##############");
			System.out.println("---" + expression + "---  " + parameterizedType);
		}
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {

		if (tempFieldName != null && parameterizedType == null) {
			String temp = 
					((VariableDeclarationFragment)node.fragments().get(0))
						.getName().toString();
//			System.out.println("74: " + temp + "  " + genericFieldName);
			if (temp.equals(genericFieldName)) {
//				System.out.println("75: " + node.getType().toString());
				getTypeName(node.getType());
			}
		}
		
		return super.visit(node);
	}
	
	class GenericParaInMethod extends ASTVisitor {
		
		@Override
		public boolean visit(TypeDeclaration node) {
			className = node.getName().toString();
//			System.out.println("className "+className);
			return super.visit(node);
		}
		
		@Override
		public boolean visit(MethodDeclaration node) {

			if (node.getBody() != null && parameterizedType == null) {
				node.getBody().accept(new MethodVisitor());
			}
			
			if (tempFieldName != null && parameterizedType == null) {
				List parametersList = node.parameters();
			
				for (Object object : parametersList) {
					String parameter = object.toString();
					String parameterName = parameter.substring(parameter.indexOf(" ")+1);
					String parameterType = parameter.substring(0, parameter.indexOf(" "));
					
					if (parameterName.equals(genericFieldName)) {
//						System.out.println("in MethodDeclaration: " + parameterType + " " + parameterName);
						parameterizedType = parameterType;
					}
				}
			}
			return super.visit(node);
		}
	}

	class MethodVisitor extends ASTVisitor {
		
		@Override
		public boolean visit(VariableDeclarationStatement node) {
			
//			System.out.println("ddd"+node.toString());
//			System.out.println("fff"+((VariableDeclarationFragment)node.fragments().get(0)).getName());
			
			tempFieldName = 
					((VariableDeclarationFragment)node.fragments().get(0))
						.getName().toString();
			if (node.getType() != null && tempFieldName != null 
					&& tempFieldName.equals(genericFieldName) 
					&& parameterizedType == null) {
				getTypeName(node.getType());
//				System.out.println("in VariableDeclarationStatement "+parameterizedType);
			}
			return super.visit(node);
		}



		@Override
		public boolean visit(MethodInvocation node) {
			
			if (node.getExpression() != null) {
				String expression = node.toString();	
				
				if (getExpression().equals(expression) && parameterizedType == null) {
					
					Pattern pattern = Pattern.compile("\\( *new *([A-Z]\\w*)\\(");
					Matcher matcher = pattern.matcher(expression);
					if (matcher.find()) {
						parameterizedType = matcher.group(1);
						matcher = null;
					}
					
					pattern = Pattern.compile("\\.(?:add)|(?:indexOf)|(?:contains)\\w*\\( *(\\w*)\\)");
					matcher = pattern.matcher(expression);
					if (matcher.find()) {
						tempFieldName = matcher.group(1);
						matcher = null;
					}
					
					pattern = Pattern.compile("\\.add\\w*\\( *(\\w*)\\)");
					matcher = pattern.matcher(expression);
					if (matcher.find()) {
						if (matcher.group(1).toString().equals("this")) {
							parameterizedType = className;
//							System.out.println("this");
//							System.out.println(parameterizedType);
						}
						matcher = null;
					}
				}
				
				
//				System.out.println("expression: " + expression);
			}
			return super.visit(node);
		}
		
	}
	
	private void getTypeName(Type node) {

		//if it's a array , get the component type and continue judgment
		if (node.isArrayType()) {
			ArrayType type = (ArrayType) node;
			getTypeName(type.getComponentType());
		}
		
		//if it's parameterized, get the argument type and continue judgment
		else if (node.isParameterizedType()) {
			ParameterizedType type = (ParameterizedType) node;
			for (Object o: type.typeArguments()) {
				Type t = (Type)o;
				getTypeName(t);
			}
		}
		
		//if it's a simple type
		else if (node.isSimpleType() && parameterizedType == null) {
			parameterizedType = node.toString();
		}
	}
	
	private String getExpression() {
		return expression;
	}
	
	public String getParameterizedType() {
		return parameterizedType;
	}
	
}
