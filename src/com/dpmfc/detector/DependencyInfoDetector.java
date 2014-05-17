/**
 * @author SYY
 * @date 04-03-2014
 */

package com.dpmfc.detector;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.ClassInstanceCreation;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.SingleVariableDeclaration;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.dpmfc.bean.RelationBean;
import com.dpmfc.bean.Weight;
import com.dpmfc.detector.AssociationInfoDetector.FieldVisitor;

public class DependencyInfoDetector extends RelationDetector{
	
	private String source;
	private String destination = "";

	public DependencyInfoDetector(String projectPath, RelationBean relationBean) throws IOException {
		super(projectPath, relationBean);
	}

	@Override
	public boolean visit(TypeDeclaration node) {
		source = node.getName().toString();
		
		MethodDeclaration[] methodDeclaration = node.getMethods();
		
		for (int i = 0; i < methodDeclaration.length; i++) {
			methodDeclaration[i].accept(new MethodFieldVisitor());
		}
		
		return true;
	}
	
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

}
