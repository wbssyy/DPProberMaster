package com.dpmfc.detector;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.Block;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ExpressionStatement;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.Modifier;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.StringLiteral;
import org.eclipse.jdt.core.dom.TypeDeclaration;

public class CodeModifier {
	public CodeModifier() {
		build();
	}
	
	private void build() {
		ASTParser parser = ASTParser.newParser(AST.JLS3);
		parser.setSource("D:\\Su yuyi\\baidu yunpan\\Work\\WorkSpace 2013-12\\ASTDemo\\src\\com\\ast\\builder\\HelloWorldBuilderForTest.java".toCharArray());
		
		CompilationUnit comp = (CompilationUnit) parser.createAST(null); 
		comp.recordModifications();
		
		AST ast = comp.getAST();
		
		//Class================================
		
		TypeDeclaration classDec = ast.newTypeDeclaration();
		classDec.setInterface(false);
		
		SimpleName className = ast.newSimpleName("HelloWorld");
		classDec.setName(className);
		
		Modifier classModifier = ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		classDec.modifiers().add(classModifier);
		
		comp.types().add(classDec);
		
		//Method===============================
		
		MethodDeclaration methodDec = ast.newMethodDeclaration();
		methodDec.setConstructor(true);
		
		SimpleName methodName = ast.newSimpleName("HelloWorld");
		methodDec.setName(methodName);
		
		Modifier methodModifier = ast.newModifier(Modifier.ModifierKeyword.PUBLIC_KEYWORD);
		methodDec.modifiers().add(methodModifier);
		
		Block methodBody = ast.newBlock();
		methodDec.setBody(methodBody);
		
		classDec.bodyDeclarations().add(methodDec);
		
		//Statement============================
		
		MethodInvocation methodInv = ast.newMethodInvocation();
		
		SimpleName nameSystem = ast.newSimpleName("System");
		SimpleName nameOut = ast.newSimpleName("out");
		QualifiedName nameSystemOut = ast.newQualifiedName(nameSystem, nameOut);
		
		SimpleName namePrintln = ast.newSimpleName("println");
		
		methodInv.setExpression(nameSystemOut);
		methodInv.setName(namePrintln);
		
		StringLiteral sHelloworld = ast.newStringLiteral();
		sHelloworld.setEscapedValue("\"Hello World!\"");
		
		methodInv.arguments().add(sHelloworld);
		
		ExpressionStatement es = ast.newExpressionStatement(methodInv);
		
		methodBody.statements().add(es);
		
		//End==================================
		
		System.out.println(comp.toString());
	}
}
