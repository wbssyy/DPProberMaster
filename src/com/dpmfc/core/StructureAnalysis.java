package com.dpmfc.core;

import java.util.HashMap;

import com.dpmfc.bean.ProjectInfo;
import com.dpmfc.bean.RelationBean;
import com.dpmfc.detector.RelationDetector;

public abstract class StructureAnalysis {
	
	protected HashMap<String, Integer> weightMap;
	protected RelationBean allRelation;
	
	protected StructureAnalysis structureAnalysis;
	protected CodeAnalysis      codeAnalysis;
	
	protected HashMap<String, String> classAndPath;
	
	public HashMap<String, String> getClassAndPath() {
		return classAndPath;
	}

	public void setClassAndPath(HashMap<String, String> classAndPath) {
		this.classAndPath = classAndPath;
	}

	public void init(HashMap<String, Integer> weightMap, RelationBean allRelation) {
		this.weightMap   = weightMap;
		this.allRelation = allRelation;
	}
	
	public void setStructureAnalysis(StructureAnalysis temp) {
		structureAnalysis = temp;
	}
	
	public void setCodeAnalysis(CodeAnalysis codeAnalysis) {
		this.codeAnalysis = codeAnalysis;
	}
	
	public abstract void doStructureAnalyze();
}
