package com.dpmfc.bean;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import java.util.Vector;

public class RelationBean {

	private HashMap<String, RelatedClass> allRelationMap;
	
	public RelationBean() {
		allRelationMap = new HashMap<String, RelatedClass>();
	}
	
	public HashMap getAllRelationMap() {
		return allRelationMap;
	}
	
	public void setAllRelationMap(HashMap<String, RelatedClass> relationMap) {
		this.allRelationMap = relationMap;
	}
	
	public List<String> getAllClassName() {
		List<String> allClassName = new ArrayList<String>();
		Iterator iterator = allRelationMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			String className = entry.getKey().toString();
			allClassName.add(className);
		}
		return allClassName;
		
	}
	
	/*
	 * weight: it's destination's weight in this relation, it shows the direction of the relation.
	 */
	public void putRelation(String source, String destination, String relation) {
		
		RelatedClass relatedClass;
		Integer weight;
		
		//for source class
		if (allRelationMap.containsKey(source)) {
			relatedClass = allRelationMap.get(source);
		} else {
			relatedClass = new RelatedClass();
		}
		weight = reverseWeight(relation);
		relatedClass.addRelatedClass(destination, weight);
		allRelationMap.put(source, relatedClass);	
		
		//for destination class
		if (allRelationMap.containsKey(destination)) {
			relatedClass = allRelationMap.get(destination);
		} else {
			relatedClass = new RelatedClass();
		}
		weight = forwordWeight(relation);
		relatedClass.addRelatedClass(source, weight);
		allRelationMap.put(destination, relatedClass);
	}
	
	private Integer forwordWeight(String relation) {
		switch (relation) {
		case Weight.DEPENDENCY:
			return Weight.DEPENDENCY_A;
			
		case Weight.INHERITANCE:
			return Weight.INHERITANCE_A;
			
		case Weight.ASSOCIATION:
			return Weight.ASSOCIATION_A;

		default:
			return null;
		}
	}
	
	private Integer reverseWeight(String relation) {
		switch (relation) {
		case Weight.DEPENDENCY:
			return Weight.DEPENDENCY_B;
			
		case Weight.INHERITANCE:
			return Weight.INHERITANCE_B;
			
		case Weight.ASSOCIATION:
			return Weight.ASSOCIATION_B;

		default:
			return null;
		}
	}
	
	public void printAllRelationMap() {
		Iterator iterator = allRelationMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			String className = entry.getKey().toString();
			RelatedClass relatedClass = (RelatedClass)entry.getValue();
			System.out.println(className + ": ");
			relatedClass.printRelatedClassMap();
		}
	}
	
	public class RelatedClass {
		private HashMap<String, Integer> relatedClassMap;
		
		public RelatedClass() {
			relatedClassMap = new HashMap<String, Integer>();
		}
		
		public HashMap getRelatedClassMap() {
			return relatedClassMap;
		}
		
		public void addRelatedClass(String className, int weight) {
			if (relatedClassMap.containsKey(className)) {
				Integer old = relatedClassMap.get(className);
				if (old % weight != 0 ) {
					relatedClassMap.put(className, old * weight);
				}
			} else {
				relatedClassMap.put(className, weight);
			}
		}
		
		public void printRelatedClassMap() {
			Iterator iterator = relatedClassMap.entrySet().iterator();
			while (iterator.hasNext()) {
				Map.Entry entry = (Map.Entry)iterator.next();
				String className = entry.getKey().toString();
				int weight = (Integer)entry.getValue();
				System.out.println("         <" + className + ", " + weight + ">");
			}
		}
	}
	
}
