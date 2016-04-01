package com.dpmfc.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.dpmfc.bean.Weight;
import com.dpmfc.bean.RelationBean.RelatedClass;

public class ObserverAnalysis extends StructureAnalysis{
	
	private int observerW = Weight.INHERITANCE_B * Weight.ASSOCIATION_B; // 91
	private int subjectW  = Weight.INHERITANCE_B * Weight.ASSOCIATION_A; // 77
	private int concreteObserverW = Weight.INHERITANCE_A * Weight.DEPENDENCY_A; //10
	private int concreteSubjectW = Weight.INHERITANCE_A * Weight.DEPENDENCY_B; //15
	int number = 1;

	@Override
	public void doStructureAnalyze() {
	
		HashSet<String> observerSet = new HashSet();
		HashSet<String> subjectSet = new HashSet();
	
		HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
		Set<Entry<String, Integer>> set = weightMap.entrySet();
		
		for (Entry<String, Integer> entry : set) {
			String className = entry.getKey();
			int weight = entry.getValue();
			
			if (weight % observerW == 0) {
				observerSet.add(className);
			}
			
			if (weight % subjectW == 0) {
				subjectSet.add(className);
			}
		}
		
		for (String observer : observerSet) {	
			HashSet<String> concreteObserverSet = findConcreteClass(observer);
			
			RelatedClass relatedClass = allRelationMap.get(observer);
			HashMap<String, Integer> relatedClassMap = relatedClass.getRelatedClassMap();
			
			for (String subject : subjectSet) {
				if (relatedClassMap.containsKey(subject) && 
						relatedClassMap.get(subject) % Weight.ASSOCIATION_A == 0) {
					HashSet<String> concreteSubjectSet = findConcreteClass(subject);
					
					if (checkConcreteRole(observer, subject, concreteObserverSet, concreteSubjectSet)) {
						System.out.println(number++ + " " + observer + " " + subject);
					}
				}
			}
			
		}
	}
	
	// find a concreteObserver or a concreteSubject.
	public HashSet<String> findConcreteClass(String superClass) {
		
		HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
		RelatedClass relatedClass = allRelationMap.get(superClass);
		HashMap<String, Integer> relatedClassMap = relatedClass.getRelatedClassMap();
		Set<Entry<String, Integer>> superRelatedSet = relatedClassMap.entrySet();
		
		HashSet<String> concreteClassSet = new HashSet<String>();
		
		for (Entry<String, Integer> entry : superRelatedSet) {
			String className = entry.getKey();
			int weight = entry.getValue();
			
			if (weight % Weight.INHERITANCE_A == 0) {
				concreteClassSet.add(className);
			}
		}
		
		return concreteClassSet;
	}
	
	public boolean checkConcreteRole(String observer, String subject, HashSet<String> conObserver, HashSet<String> conSubject) {
		for (String cObserver : conObserver) {
			for (String cSubject : conSubject) {
				HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
				RelatedClass relatedClass = allRelationMap.get(cObserver);
				HashMap<String, Integer> relatedClassMap = relatedClass.getRelatedClassMap();
				if (relatedClassMap.containsKey(cSubject) && 
						relatedClassMap.get(cSubject) % Weight.DEPENDENCY_B == 0) {
					
					return true;
				}
			}
		}
		return false;
	}
}
