package com.dpmfc.test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class JavaCode2AST {
	private static ASTParser astParser = ASTParser.newParser(AST.JLS3);
	
	public static CompilationUnit getCompilationUnit(String fileName) throws IOException{
		BufferedInputStream bufferedInputStream = 
				new BufferedInputStream(new FileInputStream(fileName));
		
		byte[] inputBytes = new byte[bufferedInputStream.available()];
		bufferedInputStream.read(inputBytes);
		bufferedInputStream.close();
		
		astParser.setSource(new String(inputBytes).toCharArray());
		CompilationUnit result = (CompilationUnit)astParser.createAST(null);
		
		return result;
	}
}
