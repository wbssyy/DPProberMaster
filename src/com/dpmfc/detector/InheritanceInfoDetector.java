/**
 * @author SYY
 * @date 03-03-2014
 */
package com.dpmfc.detector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.dpmfc.bean.RelationBean;
import com.dpmfc.bean.Weight;

public class InheritanceInfoDetector extends RelationDetector{	
	
	public InheritanceInfoDetector(String projectPath, RelationBean relationBean) throws IOException {
		super(projectPath, relationBean);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		
		String source = node.getName().toString();
		String destination;
		
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
}
