package com.dpmfc.core;

import java.awt.Component;
import java.awt.Container;
import java.awt.Menu;
import java.awt.MenuBar;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.dpmfc.bean.ProjectInfo;
import com.dpmfc.bean.Weight;
import com.dpmfc.bean.RelationBean.RelatedClass;

public class CompositeAnalysis extends StructureAnalysis {

	//weight of each role of the pattern
	private int componentW = Weight.INHERITANCE_B * Weight.ASSOCIATION_B; //91;
	private int compositeW = Weight.INHERITANCE_A * Weight.ASSOCIATION_A; //55;
	private int leafW      = Weight.INHERITANCE_A; //5;
	private static int number = 0;
	
	private HashSet<String> patternInstance = new HashSet<String>();
	
	@Override
	public void doStructureAnalyze() {
		
//		Set<Entry<String, String>> tempSet = sourceAndParameterMap.entrySet();
//		for (Entry<String, String> entry : tempSet) {
//			String className = entry.getKey();
//			String par = entry.getValue();
//			System.out.println(className + "<" + par + ">");
//		}
		
		List<String> componentList = new ArrayList<String>();
		HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
		Set<Entry<String, Integer>> set = weightMap.entrySet();
		
		//get all the component candidates
		for (Entry<String, Integer> entry : set) {
			String className = entry.getKey();
			int weight = entry.getValue();	
			if (weight % componentW == 0) {
				componentList.add(className);
			}
		}
		
		System.out.println(componentList.size());
		for (String component : componentList) {
			RelatedClass relatedClass = allRelationMap.get(component);
			List<String> leafList   = new ArrayList<String>();
			List<String> compositeList   = new ArrayList<String>();
			
			HashMap<String, Integer> relatedMap = relatedClass.getRelatedClassMap();
			Set<Entry<String, Integer>> relatedSet = relatedMap.entrySet();
			
			for (Entry<String, Integer> entry : relatedSet) {
				String className = entry.getKey();
				int weight = entry.getValue();
				
				if (weight % leafW == 0) {
					leafList.add(className);
				}
				if (weight % compositeW == 0) {
					compositeList.add(className);
				}
			}
			
			for (String compos : compositeList) {
				if (sourceAndParameterMap.containsKey(compos)) {
					System.out.println(number++ + ".component: " + component + "; composite: " + compos );
				}
			}
			
//			if (leafList.size()>0 && compositeList.size()>0) {
//				foobar(component, leafList, compositeList);
//			}
		}
		
//		printPatternInstance();
	}

	public void foobar(String component, List<String> leafList, List<String> compositeList) {

		CompositeCodeAnalysis codeAnalysis;
		
		for (String composite : compositeList) {
			String compositePath = classAndPath.get(composite);
			codeAnalysis = new CompositeCodeAnalysis(compositePath, "composite");
		}
		
		
	}

	public void printPatternInstance() {
		Iterator iterator = patternInstance.iterator();
		while (iterator.hasNext()) {
			String instance = iterator.next().toString();
			System.out.println(number++);
			System.out.println(instance);
		}
	}
}
