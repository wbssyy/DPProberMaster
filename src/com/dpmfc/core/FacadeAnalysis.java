package com.dpmfc.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import com.dpmfc.bean.Weight;
import com.dpmfc.bean.RelationBean.RelatedClass;

public class FacadeAnalysis extends StructureAnalysis{
	
	//weight of each role of the pattern
	private int facadeW = Weight.ASSOCIATION_A * Weight.ASSOCIATION_A * Weight.DEPENDENCY_B; // 121
	private int subSystemW = Weight.ASSOCIATION_B; // 13
	private int clientW = Weight.DEPENDENCY_A; // 2
	private int number = 1;

	@Override
	public void doStructureAnalyze() {

		HashSet<String> facadeSet = new HashSet();
	
		HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
		Set<Entry<String, Integer>> set = weightMap.entrySet();
		
		for (Entry<String, Integer> entry : set) {
			String className = entry.getKey();
			int weight = entry.getValue();
			
			if (weight % facadeW == 0) {
				facadeSet.add(className);
			}
		}
		
		for (String facade : facadeSet) {
			
			HashSet<String> subSystemSet = new HashSet();
			
			RelatedClass relatedClass = allRelationMap.get(facade);
			HashMap<String, Integer> relatedClassMap = relatedClass.getRelatedClassMap();
			Set<Entry<String, Integer>> relatedClassSet = relatedClassMap.entrySet();
			
			for (Entry<String, Integer> entry : relatedClassSet) {
				String className = entry.getKey();
				int weight = entry.getValue();
				
				if (weight % subSystemW == 0 && !className.equals(facade)) {
					subSystemSet.add(className);
				}
			}
			
			if (subSystemSet.size() > 1) {
				printPattern(facade, subSystemSet);
			}
		}
		
	}

	private void printPattern(String facade, HashSet<String> subSystemSet) {
		System.out.println(number++ + ". " + facade);
		System.out.println(subSystemSet);
	}
}
