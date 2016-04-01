package com.dpmfc.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.dpmfc.bean.Weight;
import com.dpmfc.bean.RelationBean.RelatedClass;

public class DecoratorAnalysis extends StructureAnalysis {

	//weight of each role of the pattern
	private int componentW         = Weight.INHERITANCE_B * Weight.INHERITANCE_B * Weight.ASSOCIATION_B; //637
	private int concreteComponentW = Weight.INHERITANCE_A;  //5
	private int decoratorW         = Weight.INHERITANCE_A * Weight.ASSOCIATION_A; //55
	private int concreteDecoratorW = Weight.INHERITANCE_A;	//5
	private static int number = 1;
//	private HashSet<String> decoratorSet = new HashSet<String>();
	
	@Override
	public void doStructureAnalyze() {

		HashSet<String> decoratorSet = new HashSet();
		HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
		Set<Entry<String, Integer>> set = weightMap.entrySet();
		
		//get all the decorator candidates
		for (Entry<String, Integer> entry : set) {
			String className = entry.getKey();
			int weight = entry.getValue();
			
			if (weight != 0 && weight % decoratorW == 0) {
				decoratorSet.add(className);
			}
		}
		
		for (String decorator : decoratorSet) {
			
			RelatedClass relatedClass = allRelationMap.get(decorator);
			List<String> concreteDecoratorList = new ArrayList<String>();
			List<String> componentList = new ArrayList<String>();
			
			HashMap<String, Integer> relatedMap = relatedClass.getRelatedClassMap();
			Set<Entry<String, Integer>> relatedSet = relatedMap.entrySet();
			
			for (Entry<String, Integer> entry : relatedSet) {
				String className = entry.getKey();
				int weight = entry.getValue();
				
				if (weight % concreteDecoratorW == 0) {
					concreteDecoratorList.add(className);
				}

				if (weight % (Weight.ASSOCIATION_B * Weight.INHERITANCE_B) == 0) {
//					if (weightMap.get(className) % componentW == 0) {
						componentList.add(className);
//					}
				}
				
//				if (componentList.size()>0 && concreteDecoratorList.size()>0) {
//					decoratorSet.add(decorator);
//				}
			}
			
			if (componentList.size() > 0) {
				foobar(decorator, componentList, concreteDecoratorList);
			}
		}
		
	}
	
	public void foobar(String decorator, List<String> componentList, List<String> concreteDecoratorList) {
		
		DecoratorCodeAnalysis codeAnalysis;
		String decoratorPath = classAndPath.get(decorator);
		codeAnalysis = new DecoratorCodeAnalysis(decoratorPath, componentList.get(0));
		
		String componentField = codeAnalysis.getComponentField();
		String operation = codeAnalysis.getOperation();
		if (componentField != null && operation != null) {
			System.out.println();
			System.out.println(number++ + ". decorator: " + decorator);
			System.out.println("    componentField: " + componentField);
		}
		
		
	}
}
