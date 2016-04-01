package com.dpmfc.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.dpmfc.bean.ProjectInfo;
import com.dpmfc.bean.Weight;
import com.dpmfc.bean.RelationBean.RelatedClass;

public class StrategyAnalysis extends StructureAnalysis {

	//weight of each role of the pattern
	private int strategyW         = Weight.INHERITANCE_B * Weight.INHERITANCE_B * Weight.ASSOCIATION_B;	//637;
	private int concreteStrategyW = Weight.INHERITANCE_A;	//5;
	private int contextW          = Weight.ASSOCIATION_A;	//11;
	private static int number = 1;
	 
	@Override
	public void doStructureAnalyze() {

		HashSet<String> strategyList = new HashSet();
		HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
		Set<Entry<String, Integer>> set = weightMap.entrySet();
		
		//get all the abstraction candidates
		for (Entry<String, Integer> entry : set) {
			String className = entry.getKey();
			int weight = entry.getValue();
			
			if (weight != 0 && weight % strategyW == 0) {
				strategyList.add(className);
			}
		}
		
		for (String strategy : strategyList) {
			System.out.println(number++ + "strategy: " + strategy);
		}
	}
	
	private void printResult() {
		
	}

}
