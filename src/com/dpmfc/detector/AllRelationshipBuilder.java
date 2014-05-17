package com.dpmfc.detector;

import java.util.HashMap;
import java.util.Set;
import java.util.Map.Entry;

import com.dpmfc.bean.RelationBean;
import com.dpmfc.bean.RelationBean.RelatedClass;
import com.dpmfc.util.OutputUtil;

public class AllRelationshipBuilder {
	
	private static RelationBean allRelation = new RelationBean();
	private HashMap<String, String> classAndPath = new HashMap<String, String>();
	
	public void buildAllRelationship(String projectPath) throws Exception{

		RelationDetector2 relationDetector = new RelationDetector2(projectPath, allRelation);
		allRelation = relationDetector.getAllRelation();
		
		classAndPath = relationDetector.getClassAndPath();

		relationDetector.removeJDKClass(allRelation);
	}
	
	public void printRelation() {
		HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
		Set<Entry<String, RelatedClass>> set = allRelationMap.entrySet();
		StringBuilder sb = new StringBuilder();
		
		for (Entry<String, RelatedClass> entry : set) {
			String className = entry.getKey();
			System.out.println("Class Name: " + className);
			
			sb.append("Class Name: " + className + "\n");
			
			RelatedClass relatedClass = entry.getValue();
			
			HashMap<String, Integer> relatedMap = relatedClass.getRelatedClassMap();
			Set<Entry<String, Integer>> relatedSet = relatedMap.entrySet();
			for (Entry<String, Integer> entry2 : relatedSet) {
				String relatedClassName = entry2.getKey();
				int relatedClassWeight = entry2.getValue();
				System.out.println("		related class: " + "<" + relatedClassName + ", " 
						+ relatedClassWeight + ">");
				
				sb.append("		related class: " + "<" + relatedClassName + ", " 
						+ relatedClassWeight + ">" + "\n");
				
			}
		}
		
		try {
			OutputUtil.outputToTXT(sb.toString());
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public RelationBean getAllRelation() {
		return allRelation;
	}
	
	public HashMap getClassAndPath() {
		return classAndPath;
	}
}
