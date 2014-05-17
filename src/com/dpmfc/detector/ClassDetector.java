package com.dpmfc.detector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.dpmfc.bean.RelationBean;
import com.dpmfc.bean.RelationBean.RelatedClass;
import com.dpmfc.test.JavaCode2AST;
import com.dpmfc.util.FileUtil;

public class ClassDetector extends ASTVisitor{
	
	private HashSet<String> allClassSet = new HashSet<String>();
	
	private HashMap<String, HashSet<String>> classAndPath = new HashMap<String, HashSet<String>>();
	private HashSet<String> classInSameFile;
	
	public ClassDetector(String projectPath) throws IOException{
		super();
		
		FileUtil fileTool = new FileUtil();
		ArrayList<String> filePath = fileTool.getAllJavaFilePath(projectPath);
		
		for (String path: filePath) {
			
			classInSameFile = new HashSet<String>();
			
			CompilationUnit compUnit = JavaCode2AST.getCompilationUnit(path);
			compUnit.accept(this);
			
			classAndPath.put(path, classInSameFile);
		}
		
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		//
		String className = node.getName().toString();
		allClassSet.add(className);
		
		classInSameFile.add(className);
		
		return super.visit(node);
	}
	
	public HashSet<String> getAllClassList() {
		return allClassSet;
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
	
	public void printAllClass() {
		int num = 1;
		for (String string : allClassSet) {
			System.out.println(num++ + "========"+string);
		}
	}
	
	public void printClassPath() {
		Iterator iterator = classAndPath.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			String pathString = entry.getKey().toString();
			System.out.println("Path: " + pathString);
			
			HashSet<String> set = (HashSet<String>)entry.getValue();
			for (String string : set) {
				System.out.println("      " + string);
			}
		}
	}
}
