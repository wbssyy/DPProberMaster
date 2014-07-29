package com.dpmfc.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.dpmfc.bean.ProjectInfo;
import com.dpmfc.bean.Weight;
import com.dpmfc.bean.RelationBean.RelatedClass;

public class SingletonAnalysis extends StructureAnalysis {
	
	private int singletonW = Weight.ASSOCIATION_A * Weight.ASSOCIATION_B; //143
	private static int number = 0;

	@Override
	public void doStructureAnalyze() {
		// TODO Auto-generated method stub

		HashSet<String> singletonList = new HashSet();
		HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
		Set<Entry<String, Integer>> set = weightMap.entrySet();
		
		//get all the abstraction candidates
		for (Entry<String, Integer> entry : set) {
			String className = entry.getKey();
			int weight = entry.getValue();
			
			if (weight != 0 && weight % singletonW == 0) {
				singletonList.add(className);
			}
		}
		
		for (String singleton : singletonList) {
			
			RelatedClass relatedClass = allRelationMap.get(singleton);
			HashMap<String, Integer> relatedMap = relatedClass.getRelatedClassMap();
			
			if (relatedMap.get(singleton) != null && relatedMap.get(singleton) % singletonW == 0) {
				foobar(singleton);
			}
		}
	}
	
	private void foobar(String singleton) {
		String singletonPath = classAndPath.get(singleton);
		System.out.println(singletonPath + " " + singleton);
		codeAnalysis = new SingletonCodeAnalysis(singletonPath);
		
		HashMap<String, HashMap<String, String>> typeMap = codeAnalysis.getTypeMap();
		HashMap<String, String> constructorMap = typeMap.get(singleton);
		
		if (constructorMap != null && !constructorMap.containsKey("public") 
				&& codeAnalysis.getMethodList().contains(singleton)) {
			System.out.println("singleton: " + singleton );
		}
	}

}
