package com.dpmfc.detector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.dpmfc.bean.RelationBean;
import com.dpmfc.test.JavaCode2AST;
import com.dpmfc.util.FileUtil;
import com.dpmfc.util.XMLUtil;

public class GenericDetector extends ASTVisitor {
	
	private String classPath;
	private List<String> genericClassList;
	private List<String> jdkGenericList;
	
	public GenericDetector(String projectPath) throws Exception{
		super();

		genericClassList = new ArrayList<String>();
		jdkGenericList = new ArrayList<String>();
		FileUtil fileTool = new FileUtil();
		ArrayList<String> filePath = fileTool.getAllJavaFilePath(projectPath);
		
		for (int i = 0; i < filePath.size(); i++) {
			
			classPath = filePath.get(i);				
			CompilationUnit compUnit = JavaCode2AST.getCompilationUnit(classPath);
			compUnit.accept(this);
		}
		
		readGenericFromXML();
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		
		String source = node.getName().toString();
		
		if ( node.getSuperclassType() != null) {
			String superNode = node.getSuperclassType().toString();
			source = superNode + "." + source;
//			System.out.println("super node: "+superNode + "; son node: " + source);
		}
		
		if (node.typeParameters().size() > 0) {
			String parameters = "";
			for (int i = 0; i < node.typeParameters().size(); i++) {
				parameters += node.typeParameters().get(i).toString();
				
				if (i + 1 < node.typeParameters().size()) {
					parameters += ",";
				}
			}	
			source += "<" + parameters + ">";
			genericClassList.add(source);
//			System.out.println(source + "; " + packageName);
		}
		return super.visit(node);
	}
	
	/*
	 * this method can read the common generic in JDK from XML
	 */
	public void readGenericFromXML() throws Exception{
		String genericFromXML = XMLUtil.getStringFromXML("JDKGenerics.xml", "values");
		String[] genericArray = genericFromXML.split("\n");
		
		for (String string : genericArray) {
			if (!string.equals("")) {
				jdkGenericList.add(string);
			}
		}
	}
	
//	@Override
//	public boolean visit(ParameterizedType node) {
//		System.out.println("ParameterizedType: "+node.getType().toString());
//		return super.visit(node);
//	}
	
	public List getGenericClassList() {
		return genericClassList;
	}
	
	public List getJDKGenericList() {
		return jdkGenericList;
	}
}
