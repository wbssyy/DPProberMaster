package com.dpmfc.test;

import java.io.BufferedInputStream;
import java.io.FileInputStream;

import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.CompilationUnit;

public class J2AST {
    private ASTParser astParser = ASTParser.newParser(AST.JLS3);
    
    public CompilationUnit getCompilationUnit(String fileName) throws Exception {
	BufferedInputStream bufferedInputStream = 
		new BufferedInputStream(new FileInputStream(fileName));
	byte[] inputByte = new byte[bufferedInputStream.available()];
	bufferedInputStream.read(inputByte);
	bufferedInputStream.close();
	this.astParser.setSource(new String(inputByte).toCharArray());
	CompilationUnit result = (CompilationUnit)this.astParser.createAST(null);
	
	return result;
    }
}
