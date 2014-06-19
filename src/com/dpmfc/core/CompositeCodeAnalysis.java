package com.dpmfc.core;

import org.eclipse.jdt.core.dom.FieldDeclaration;
import org.eclipse.jdt.core.dom.VariableDeclarationFragment;

public class CompositeCodeAnalysis extends CodeAnalysis {

	public CompositeCodeAnalysis(String path) {
		super(path);
		// TODO Auto-generated constructor stub
	}
	
	public CompositeCodeAnalysis(String path, String flag) {
		super(path);
		this.flag = flag;
	}
	
	@Override
	public boolean visit(FieldDeclaration node) {
		
		if (node.getType().isParameterizedType()) {
			String name = ((VariableDeclarationFragment)node.fragments().get(0)).getName().toString();
			String type = node.getType().toString();
			
			if (!type.contains(",")) {
				fieldNameAndType.put(name, type);
			}
		}
		
		return super.visit(node);
	}
}
