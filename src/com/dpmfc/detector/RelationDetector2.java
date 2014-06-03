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
import org.eclipse.jdt.core.dom.PackageDeclaration;
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

	private String packageName;
	private MarkGeneric markGeneric;
	private List<String> genericClassList;
	private List<String> JDKGenericList;
	
	public RelationDetector2(String projectPath, RelationBean relationBean) throws Exception{
		super();
		
		this.allRelation = relationBean;
		FileUtil fileTool = new FileUtil();
		ArrayList<String> filePath = fileTool.getAllJavaFilePath(projectPath);
		
		// for mark generic
		GenericDetector genericDetector = new GenericDetector(projectPath);
		genericClassList = genericDetector.getGenericClassList();
		JDKGenericList = genericDetector.getJDKGenericList();
		
		for (int i = 0; i < filePath.size(); i++) {
			
			classPath = filePath.get(i);		
			classInSameFile = new HashSet<String>();
			
			markGeneric = new MarkGeneric();
			
			CompilationUnit compUnit = JavaCode2AST.getCompilationUnit(classPath);
			compUnit.accept(this);
			
			pathAndClass.put(classPath, classInSameFile);
			
			// for mark generic
			markGeneric.checkGenericInSystem(genericClassList, JDKGenericList);
			markGeneric.hasAddAndRemoveCall(classPath);
			markGeneric.printGeneric(classPath);
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
	
	@Override
	public boolean visit(PackageDeclaration node) {
		packageName = node.getName().toString();
		return super.visit(node);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		
		source = node.getName().toString();
		
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
		
		//for all classes 
		allClassSet.add(source);
		classInSameFile.add(source);
		classAndPath.put(source, classPath);

		inheritanceDetect(node);
		
		return super.visit(node);
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		
		associationDetect(node);
		
		// for mark generic
		markGeneric.checkGenericByField(destination, node);
		
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodDeclaration node) {

		dependencyDetect(node);
		return super.visit(node);
	}
	
	@Override
	public boolean visit(ClassInstanceCreation node) {

		dependencyDetect(node);
		return super.visit(node);
	}

	@Override
	public boolean visit(MethodInvocation node) {
		
		dependencyDetect(node);
		
		// for mark generic
		markGeneric.collectMethodCall(node);
		
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
	
	
	/*
	 * for inheritance detecting, include Inheritance and realization
	 */
	private void inheritanceDetect(TypeDeclaration node) {
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
	}
	
	
	/*
	 * for association detecting
	 */
	private void associationDetect(FieldDeclaration node) {
				
		getTypeName(node.getType());
		if (destination != null && !destination.equals("")) {
			allRelation.putRelation(source, destination, Weight.ASSOCIATION);
		}
	}
	
	
	/*
	 * do dependency detecting, through parameters of method 
	 */
	private void dependencyDetect(MethodDeclaration node) {
		//get the type of parameters of each method
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
	}
	
	
	/*
	 * do dependency detecting, through Class Instance Creation
	 */
	private void dependencyDetect(ClassInstanceCreation node) {
		//find otherclass's object which is created as local variables in method
		if (node.getType() != null) {
			getTypeName(node.getType());
			allRelation.putRelation(source, destination, Weight.DEPENDENCY);
		}
	}
	
	
	/*
	 * do dependency detecting, through Method Invocation
	 */
	private void dependencyDetect(MethodInvocation node) {
		//find a "static" method invocation of other class
		if (node.getExpression() != null) {
			destination = node.getExpression().toString();
			
			if (Character.isUpperCase(destination.charAt(0))) {
				allRelation.putRelation(source, destination, Weight.DEPENDENCY);
			}
		}
	}
}