package com.dpmfc.detector;

import java.awt.MenuBar;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Vector;

import javax.swing.tree.ExpandVetoException;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.dpmfc.bean.RelationBean;
import com.dpmfc.test.JavaCode2AST;
import com.dpmfc.util.FileUtil;
import com.dpmfc.util.OutputUtil;

public class MarkGeneric extends ASTVisitor {
	
	private ArrayList<String> fieldNameList;
	private ArrayList<String> expressionList;
	
	private int number = 1;
	
	public MarkGeneric(String projectPath, RelationBean relationBean) throws Exception{
		super();
//	MenuBar
		FileUtil fileTool = new FileUtil();
		ArrayList<String> filePath = fileTool.getAllJavaFilePath(projectPath);
		
		for (int i = 0; i < filePath.size(); i++) {
			fieldNameList = new ArrayList<String>();
			expressionList = new ArrayList<String>();
//			System.out.println(number++);
			
			CompilationUnit compUnit = JavaCode2AST.getCompilationUnit(filePath.get(i));
			compUnit.accept(this);
			
//			for (String fieldName : fieldNameList) {
//				System.out.println(fieldName);
//			}
			for (String fieldName : fieldNameList) {

				for (String expression : expressionList) {

					String expressionName = expression.substring(0, expression.indexOf("."));
					String expressionMethod = expression.substring(expression.indexOf("."));
					
					if (fieldName.contains("=")) {
						fieldName = fieldName.substring(0, fieldName.indexOf("="));
					}
					
					if (expressionName.contains(fieldName) && expressionMethod.contains("add")) {
						System.out.println(fieldName + ": " + expression);
					}
				}
			}
		}
	}

	@Override
	public boolean visit(FieldDeclaration node) {
		Vector<String> tempStrings = new Vector<>();
		Vector te = new Vector<>();
		
		String fieldName = node.fragments().get(0).toString();
		if (fieldNameList != null) {
			fieldNameList.add(fieldName);
		}
		
//		System.out.println("fieldName: " + fieldName);
		
		return super.visit(node);
	}
	
	@Override
	public boolean visit(MethodInvocation node) {

		if (node.getExpression() != null && expressionList != null) {
			String expression = node.toString();	
			expressionList.add(expression);
//			System.out.println("expression: " + expression);
		}
		
		return super.visit(node);
	}
	
	public static void main(String[] a) throws Exception{
//		MenuBar
		MarkGeneric markGeneric = new MarkGeneric("D:\\test\\jdk1.4.2_src\\java\\awt");
	}
}
