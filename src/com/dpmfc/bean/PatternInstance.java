package com.dpmfc.bean;

import java.util.HashMap;

/*
 * A class which represents a set of pattern instance
 */
public class PatternInstance {
	
	// save pattern name
	private String patternName;

	/*
	 * key: a role in a pattern
	 * value: the class who plays the role
	 */
	private HashMap roleAndClass;
	
	/*
	 * key: a class name
	 * value: the path of the file that contains the class
	 */
	private HashMap classAndPath;
	
	public HashMap getClassAndPath() {
		return classAndPath;
	}

	public void setClassAndPath(HashMap classAndPath) {
		this.classAndPath = classAndPath;
	}

	public String getPatternName() {
		return patternName;
	}

	public void setPatternName(String patternName) {
		this.patternName = patternName;
	}

	public HashMap getRoleAndClass() {
		return roleAndClass;
	}

	public void setRoleAndClass(HashMap roleAndClass) {
		this.roleAndClass = roleAndClass;
	}

}
