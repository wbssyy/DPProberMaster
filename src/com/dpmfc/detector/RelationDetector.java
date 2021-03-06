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

public class RelationDetector extends ASTVisitor {
	
	private HashSet<String> allClassSet = new HashSet<String>();
	private HashMap<String, HashSet<String>> classAndPath = new HashMap<String, HashSet<String>>();
	private HashSet<String> classInSameFile;
	
	protected RelationBean allRelation;
	private String source;
	private String destination;
	
	public RelationDetector(String projectPath, RelationBean relationBean) throws IOException{
		super();
		
		this.allRelation = relationBean;
		FileUtil fileTool = new FileUtil();
		ArrayList<String> filePath = fileTool.getAllJavaFilePath(projectPath);
		
		for (String path: filePath) {
			
			classInSameFile = new HashSet<String>();
			
			CompilationUnit compUnit = JavaCode2AST.getCompilationUnit(path);
			compUnit.accept(this);
			
			classAndPath.put(path, classInSameFile);
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
	
	public void setAllRelation(RelationBean relationBean) {
		this.allRelation = relationBean;
	}
	
	
	// dependency detector
	@Override
	public boolean visit(TypeDeclaration node) {
		
		source = node.getName().toString();
		
		//for all classes 
		allClassSet.add(source);
		
		classInSameFile.add(source);
		
		//for association detect
		for (Object dec : node.bodyDeclarations()) {
			((ASTNode)dec).accept(new FieldVisitor());
		}
		
		//for inheritance detect
		//if it has super class
		if (node.getSuperclassType() != null) {
			
			destination = getSimpleName(node.getSuperclassType());
			allRelation.putRelation(source, destination, Weight.INHERITANCE);
		}
		
		//if it has interface
		for (Object i: node.superInterfaceTypes()) {
			
			destination = getSimpleName(((Type)i));
			allRelation.putRelation(source, destination, Weight.INHERITANCE);
		}
		
		//for dependency detect
		MethodDeclaration[] methodDeclaration = node.getMethods();
		
		for (int i = 0; i < methodDeclaration.length; i++) {
			methodDeclaration[i].accept(new MethodFieldVisitor());
		}
		
		return true;
	}
	
	private String getSimpleName(Type node) {
		String superClass = node.toString();
		
		//Type<?>
		if (node.isParameterizedType()) {
			ParameterizedType type = (ParameterizedType) node;
			superClass = getSimpleName(type.getType());
		}
		
		return superClass;
	}
	
	/*
	 * for dependency detect
	 */
	class MethodFieldVisitor extends ASTVisitor{

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
	}
	
	/*
	 * for association detect
	 */
	class FieldVisitor extends ASTVisitor {
		
		//pass internal class
		@Override
		public boolean visit(TypeDeclaration node) {
			return false;
		}

		@Override
		public boolean visit(FieldDeclaration node) {
			//if it associated with other
					
			getTypeName(node.getType());
			
			if (destination != null && !destination.equals("")) {
				allRelation.putRelation(source, destination, Weight.ASSOCIATION);
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
	}
}
