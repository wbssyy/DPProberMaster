package com.dpmfc.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class ProjectInfo {

	private HashMap<String, Integer> weightMap;	
	private HashMap dependencyMap, inheritanceMap, assciationMap;
	/*
	 * key: a special weight
	 * value: the classes which contain the special weight
	 */
	private HashMap<Integer, List<String>> containWClassMap = new HashMap<Integer, List<String>>(); 
	/*
	 * key: a class
	 * value: all the classes that have relation with the key class
	 */
	private HashMap<String, HashSet<String>> relationMap = new HashMap<String, HashSet<String>>();
	
	public ProjectInfo() {
		
		weightMap = new HashMap<String, Integer>();
		
		dependencyMap = new HashMap<String, HashSet<String>>();
		inheritanceMap = new HashMap<String, HashSet<String>>();
		assciationMap = new HashMap<String, HashSet<String>>();
	}
	
	public HashMap getContainWClassMap() {
		return containWClassMap;
	}
	
	//check if there are any special weight in the relationSet 
	public List containWeight(int tempWeight, HashSet relationSet) {
		
		List<String> classList = new ArrayList<String>();
		Iterator iterator = relationSet.iterator();
		
		while (iterator.hasNext()) {
			String className = iterator.next().toString();
			Integer weight = weightMap.get(className);
			
			if (weight % tempWeight == 0) {
				classList.add(className);
			}
		}
		return classList;
	}
	
	public boolean hasRelation(String classA, String classB) {
		HashSet setA = relationMap.get(classA);	
		HashSet setB = relationMap.get(classB);
		if (!setA.contains(classB) && !setB.contains(classA)) {
			return false;
		} else {
			return true;
		}
	}
	
	public List whichPoinToIt(String className, int tempW) {
		List classList = new ArrayList<String>();
		Iterator iterator = null;
		
		switch (tempW) {
		case Weight.DEPENDENCY_A:
			iterator = dependencyMap.entrySet().iterator();
			break;
		case Weight.INHERITANCE_A:
			iterator = inheritanceMap.entrySet().iterator();
			break;
		case Weight.ASSOCIATION_A:
			iterator = assciationMap.entrySet().iterator();
			break;
		}
		
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			String sourceClass  = entry.getKey().toString();
			HashSet<String> set = (HashSet)entry.getValue();
//			if (set.contains(className)) {
//				classList.add(sourceClass);
//			}
		}
		return classList;
	}
	
	public List<String> containWeight(int tempWeight) {
		List<String> classList = new ArrayList<String>();
		Iterator iterator = weightMap.entrySet().iterator();
		while (iterator.hasNext()){
			Map.Entry entry = (Map.Entry)iterator.next();
			String className = entry.getKey().toString();
			Integer weight = (Integer)entry.getValue();
			
			if (weight % tempWeight == 0) {
				classList.add(className);
			}
		}
		return classList;
	}
	
	private void fillContainWeightMap() {
		final int inherAassocA = Weight.INHERITANCE_A * Weight.ASSOCIATION_A; // 55
		final int inherAassocB = Weight.INHERITANCE_A * Weight.ASSOCIATION_B; // 65
		final int inherBassocA = Weight.INHERITANCE_B * Weight.ASSOCIATION_A; // 77
		final int inherBassocB = Weight.INHERITANCE_B * Weight.ASSOCIATION_B; // 91
		final int inherB2assocB = Weight.INHERITANCE_B * Weight.INHERITANCE_B * Weight.ASSOCIATION_B; // 637
		
		int[] array = {inherAassocA, inherAassocB, inherBassocA, inherBassocB, inherB2assocB};
		
		for (int weight : array) {
			List list = containWeight(weight);
			containWClassMap.put(weight, list);
		}
	}
	
	private void getRelationMap() {
		Iterator iterator = weightMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			String className = entry.getKey().toString();
			
			HashSet relationSet = new HashSet();
			if (dependencyMap.get(className) != null) {
				HashSet dependencySet = (HashSet)dependencyMap.get(className);
				relationSet.addAll(dependencySet);
			}
			if (inheritanceMap.get(className) != null) {
				HashSet inheritanceSet = (HashSet)inheritanceMap.get(className);
				relationSet.addAll(inheritanceSet);
			}
			if (assciationMap.get(className) != null) {
				HashSet associationSet = (HashSet)assciationMap.get(className);
				relationSet.addAll(associationSet);
			}
			
			relationMap.put(className, relationSet);
		}
	}
	
	
	public HashMap<String, Integer> getWeightMap() {
		return weightMap;
	}

	public void setWeightMap(HashMap<String, Integer> weightMap) {
		this.weightMap = weightMap;
		getRelationMap();
		fillContainWeightMap();
	}

	public HashMap getDependencyMap() {
		return dependencyMap;
	}

	public void setDependencyMap(HashMap dependencyMap) {
		this.dependencyMap = dependencyMap;
	}

	public HashMap getInheritanceMap() {
		return inheritanceMap;
	}

	public void setInheritanceMap(HashMap inheritanceMap) {
		this.inheritanceMap = inheritanceMap;
	}

	public HashMap getAssciationMap() {
		return assciationMap;
	}

	public void setAssciationMap(HashMap assciationMap) {
		this.assciationMap = assciationMap;
	}
}
