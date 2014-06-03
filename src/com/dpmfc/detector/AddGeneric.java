package com.dpmfc.detector;

import java.util.HashMap;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;

import com.dpmfc.test.JavaCode2AST;

public class AddGeneric extends ASTVisitor{
	
	private String expression;
	private String parameterizedType;
	
	public AddGeneric(String classPath, String expression) throws Exception{
		super();

		this.expression = expression;
		
		System.out.println(classPath);
		System.out.println(expression);
		CompilationUnit compUnit = JavaCode2AST.getCompilationUnit(classPath);
		compUnit.accept(this);
	}

	@Override
	public boolean visit(MethodDeclaration node) {

//		System.out.println(node.getName() + "--------------");
		if (node.getBody() != null) {
			node.getBody().accept(new MethodVisitor());
		}
		return super.visit(node);
	}

	class MethodVisitor extends ASTVisitor {
		
		
		@Override
		public boolean visit(MethodInvocation node) {
			
			if (node.getExpression() != null) {
				String expression = node.toString();	
				
				if (getExpression().equals(expression)) {
					System.out.println("expression: " + expression);
					
					if (expression.contains("(new ")) {
						parameterizedType = expression.substring(expression.indexOf(" "), expression.indexOf("("));
						System.out.println(parameterizedType);
					}
				}
//				System.out.println("expression: " + expression);
			}
			return super.visit(node);
		}
	}
	
	private String getExpression() {
		return expression;
	}
	
}
