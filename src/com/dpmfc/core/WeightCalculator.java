package com.dpmfc.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.dpmfc.bean.ProjectInfo;
import com.dpmfc.bean.RelationBean;
import com.dpmfc.bean.Weight;
import com.dpmfc.bean.RelationBean.RelatedClass;
import com.dpmfc.detector.RelationDetector;
import com.dpmfc.util.OutputUtil;

public class WeightCalculator {

	private HashMap<String, Integer> weightMap = new HashMap<String, Integer>();
	
	public void calculateWeight(RelationBean allRelation) {
		HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
		Set<Entry<String, RelatedClass>> set = allRelationMap.entrySet();
		
		for (Entry<String, RelatedClass> entry : set) {
			RelatedClass relatedClass = entry.getValue();	
			HashMap<String, Integer> relatedMap = relatedClass.getRelatedClassMap();
			Set<Entry<String, Integer>> relatedSet = relatedMap.entrySet();
			
			for (Entry<String, Integer> entry2 : relatedSet) {
				String className = entry2.getKey();
				int classWeight = entry2.getValue();
				
				if (weightMap.containsKey(className)) {
					
					classWeight = removeRepeatCount(classWeight, weightMap.get(className));
					Integer newValue = weightMap.get(className) * classWeight;
					weightMap.put(className, newValue);	
					
				} else {
					weightMap.put(className, classWeight);
				}
			}
		}
	}
	
	private int removeRepeatCount(int newWeight, int oldWeight) {
		
		int DAsquare = Weight.DEPENDENCY_A * Weight.DEPENDENCY_A;
		int DBsquare = Weight.DEPENDENCY_B * Weight.DEPENDENCY_B;
		int IAsquare = Weight.INHERITANCE_A * Weight.INHERITANCE_A;
		int IBsquare = Weight.INHERITANCE_B * Weight.INHERITANCE_B;
		int AAsquare = Weight.ASSOCIATION_A * Weight.ASSOCIATION_A;
		int ABsquare = Weight.ASSOCIATION_B * Weight.ASSOCIATION_B;
		
		int[] squareArray = {DAsquare, DBsquare, IAsquare, IBsquare, AAsquare, ABsquare};
		int[] weightArray = {Weight.DEPENDENCY_A, Weight.DEPENDENCY_B, Weight.INHERITANCE_A,
				Weight.INHERITANCE_B, Weight.ASSOCIATION_A, Weight.ASSOCIATION_B};
		
		for (int i = 0; i < weightArray.length; i++) {
			
			if (oldWeight % squareArray[i] == 0) {
				while (newWeight % weightArray[i] == 0) {
					newWeight = newWeight / weightArray[i];
				}
			}
			
		}
		
		return newWeight;
	}
	
	public void printWeightMap(){
		
		Iterator iterator = weightMap.entrySet().iterator();
		int num = 1;
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			String classNameString = entry.getKey().toString();
			String weightString = entry.getValue().toString();
			
			System.out.println(num++ + classNameString + ": " + weightString);
		}
		
	}
	
	public HashMap getWeightMap() {
		return weightMap;
	}
	
}
