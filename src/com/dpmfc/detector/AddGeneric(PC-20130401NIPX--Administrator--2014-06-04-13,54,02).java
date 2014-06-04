package com.dpmfc.detector;

import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;
import org.eclipse.jdt.core.dom.VariableDeclarationStatement;

import com.dpmfc.bean.Weight;
import com.dpmfc.test.JavaCode2AST;

public class AddGeneric extends ASTVisitor{
	
	private String expression;
	private String parameterizedType;
	private String fieldName;
	
	public AddGeneric(String classPath, String expression) throws Exception{
		super();

		this.expression = expression;
		
//		System.out.println(classPath);
//		System.out.println(expression);
		CompilationUnit compUnit = JavaCode2AST.getCompilationUnit(classPath);
		compUnit.accept(this);
		
		if (fieldName != null) {
			compUnit = JavaCode2AST.getCompilationUnit(classPath);
			compUnit.accept(this);
		}
		
		if (parameterizedType != null) {
//			System.out.println(classPath + "##############");
			System.out.println("---" + expression + "---" + parameterizedType);
		}
	}

	@Override
	public boolean visit(MethodDeclaration node) {

//		System.out.println(node.getName() + "--------------");
		if (node.getBody() != null) {
			node.getBody().accept(new MethodVisitor());
		}
		
		if (fieldName != null) {
			List parametersList = node.parameters();
		
			for (Object object : parametersList) {
				String parameter = object.toString();
				if (parameter.contains(fieldName)) {
//					System.out.println("par: " + parameter.substring(0, parameter.indexOf(" ")));
					parameterizedType = parameter.substring(0, parameter.indexOf(" "));
				}
			}
		}
		return super.visit(node);
	}

	class MethodVisitor extends ASTVisitor {
		
//		@Override
//		public boolean visit(ClassInstanceCreation node) {
//			System.out.println("ddd"+node.toString());
//			if (node.getType() != null && fieldName != null ) {
//				getTypeName(node.getType());
//			}
//
//			return super.visit(node);
//		}
		
		@Override
		public boolean visit(VariableDeclarationStatement node) {
			
//			System.out.println("ddd"+node.toString());
//			System.out.println("fff"+((VariableDeclarationFragment)node.fragments().get(0)).getName());
			
			String tempFieldName = 
					((VariableDeclarationFragment)node.fragments().get(0))
						.getName().toString();
			if (node.getType() != null && fieldName != null && tempFieldName.equals(fieldName)) {
				getTypeName(node.getType());
				System.out.println("fff"+parameterizedType);
			}
			return super.visit(node);
		}



		@Override
		public boolean visit(MethodInvocation node) {
			
			if (node.getExpression() != null) {
				String expression = node.toString();	
				
				if (getExpression().equals(expression)) {
					
					Pattern pattern = Pattern.compile("\\( *new *([A-Z]\\w*)\\(");
					Matcher matcher = pattern.matcher(expression);
					if (matcher.find()) {
						parameterizedType = matcher.group(1);
					}
					
					pattern = Pattern.compile("\\.add\\w*\\( *(\\w*)\\)");
					matcher = pattern.matcher(expression);
					if (matcher.find()) {
						fieldName = matcher.group(1);
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
		else if (node.isSimpleType()) {
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
