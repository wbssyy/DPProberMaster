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

import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.ArrayType;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.ParameterizedType;
import org.eclipse.jdt.core.dom.Type;
import org.eclipse.jdt.core.dom.TypeDeclaration;

import com.dpmfc.bean.RelationBean;
import com.dpmfc.bean.Weight;
import com.dpmfc.test.JavaCode2AST;

public class AssociationInfoDetector extends RelationDetector{
	
	/**
	 * key:class name
	 * value:associated class name list
	 */
	private String source;
	private String destination = "";
	
	public AssociationInfoDetector(String projectPath, RelationBean relationBean) throws IOException{
		super(projectPath, relationBean);
	}
	
	@Override
	public boolean visit(TypeDeclaration node) {
		source = node.getName().toString();
		
		for (Object dec : node.bodyDeclarations()) {
			((ASTNode)dec).accept(new FieldVisitor());
		}

		return true;
	}
	
	/**
	 * visit every field in the class
	 *
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
