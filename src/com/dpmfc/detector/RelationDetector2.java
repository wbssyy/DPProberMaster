/**
 * @author SYY
 * @date 03-03-2014
 */
package com.dpmfc.detector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.dpmfc.bean.RelationBean;
import com.dpmfc.bean.Weight;
import com.dpmfc.bean.RelationBean.RelatedClass;
import com.dpmfc.detector.AssociationInfoDetector.FieldVisitor;
import com.dpmfc.detector.DependencyInfoDetector.MethodFieldVisitor;
import com.dpmfc.test.JavaCode2AST;
import com.dpmfc.util.FileUtil;

public class RelationDetector2 extends ASTVisitor {
	
	private HashSet<String> allClassSet = new HashSet<String>();
	
	/*
	 * key: a path
	 * value: a set of class
	 */
	private HashMap<String, HashSet<String>> pathAndClass = new HashMap<String, HashSet<String>>();
	
	// all the classes that in the same file
	private HashSet<String> classInSameFile;
	
	/*
	 * key: a class
	 * value: the path of the class
	 */
	private HashMap<String, String> classAndPath = new HashMap<String, String>();
	
	private String classPath;
	
	protected RelationBean allRelation;
	private String source;
	private String destination;
	
	public RelationDetector2(String projectPath, RelationBean relationBean) throws IOException{
		super();
		
		this.allRelation = relationBean;
		FileUtil fileTool = new FileUtil();
		ArrayList<String> filePath = fileTool.getAllJavaFilePath(projectPath);
		
		for (int i = 0; i < filePath.size(); i++) {
			classPath = filePath.get(i);
			
			classInSameFile = new HashSet<String>();
			
			CompilationUnit compUnit = JavaCode2AST.getCompilationUnit(classPath);
			compUnit.accept(this);
			
			pathAndClass.put(classPath, classInSameFile);
		}
	}
	
	public RelationBean removeJDKClass(RelationBean allRelation) {
		
		HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
		
		Iterator allIterator = allRelationMap.entrySet().iterator();
		
		while (allIterator.hasNext()) {
			
			Map.Entry allEntry = (Map.Entry)allIterator.next();
			String className = allEntry.getKey().toString();
			
			if (allClassSet.contains(className)) {
				RelatedClass relatedClass = (RelatedClass)allEntry.getValue();
				HashMap<String, Integer> relatedClassMap = relatedClass.getRelatedClassMap();
				
				Iterator iterator = relatedClassMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry entry = (Map.Entry)iterator.next();
					String tempClass = entry.getKey().toString();
					if (!allClassSet.contains(tempClass)) {
						iterator.remove();
					}
				}
				
			} else {
				allIterator.remove();
			}
		}
		allRelation.setAllRelationMap(allRelationMap);
		
		return allRelation;
	}

	public RelationBean getAllRelation() {
		return allRelation;
	}
	
	// dependency detector
	@Override
	public boolean visit(TypeDeclaration node) {
		
		source = node.getName().toString();
		if ( node.getSuperclassType() != null) {
			String superNode = node.getSuperclassType().toString();
			source = superNode + "." + source;
//			System.out.println("super node: "+superNode + "; son node: " + source);
		}
		
		
		//for all classes 
		allClassSet.add(source);
		classInSameFile.add(source);
		classAndPath.put(source, classPath);
		
		//for inheritance detect
		//if it has super class
		if (node.getSuperclassType() != null) {	
			getTypeName(node.getSuperclassType());
			allRelation.putRelation(source, destination, Weight.INHERITANCE);
		}
		
		//if it has interface
		for (Object i: node.superInterfaceTypes()) {
			getTypeName(((Type)i));
			allRelation.putRelation(source, destination, Weight.INHERITANCE);
		}
		
		return super.visit(node);
	}
	
	/*
	 * for association detect
	 */
	@Override
	public boolean visit(FieldDeclaration node) {
		
		//if it associated with other		
		getTypeName(node.getType());	
		if (destination != null && !destination.equals("")) {
			allRelation.putRelation(source, destination, Weight.ASSOCIATION);
		}
		
		return super.visit(node);
	}
	
	
	/*
	 * for dependency detect
	 */
	@Override
	public boolean visit(MethodDeclaration node) {
		
		//find each class of parameters of a method
		List parametersList = node.parameters();
		
		for (Object object : parametersList) {
			getTypeName(((SingleVariableDeclaration)object).getType());
			if (source == null || destination == null || allRelation == null) {
				System.out.println();
			}
			else {
				allRelation.putRelation(source, destination, Weight.DEPENDENCY);
			}
		}
		
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ClassInstanceCreation node) {

		//find otherclass's object which is created as local variables in method
		if (node.getType() != null) {
			getTypeName(node.getType());
			allRelation.putRelation(source, destination, Weight.DEPENDENCY);
		}

		return super.visit(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {

		//find a "static" method invocation of other class
		if (node.getExpression() != null) {
			destination = node.getExpression().toString();
			
			if (Character.isUpperCase(destination.charAt(0))) {
				allRelation.putRelation(source, destination, Weight.DEPENDENCY);
			}
			
		}
		
		return super.visit(node);
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
			destination = node.toString();
		}
	}
	
	public HashMap<String, String> getClassAndPath() {
		return classAndPath;
	}

	public void setClassAndPath(HashMap<String, String> classAndPath) {
		this.classAndPath = classAndPath;
	}

}
