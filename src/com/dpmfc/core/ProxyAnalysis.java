package com.dpmfc.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;

import com.dpmfc.bean.ProjectInfo;
import com.dpmfc.bean.Weight;
import com.dpmfc.bean.RelationBean.RelatedClass;

public class ProxyAnalysis extends StructureAnalysis{
	
	//weight of each role of the pattern
	private int realSubjectW = Weight.INHERITANCE_A * Weight.ASSOCIATION_B;	//65;
	private int subjectW     = Weight.INHERITANCE_B * Weight.INHERITANCE_B;	//49;
	private int proxyW       = Weight.INHERITANCE_A * Weight.ASSOCIATION_A;	//55;
	private static int number = 1;
	@Override
	public void doStructureAnalyze() {
		
		List<String> proxyList = new ArrayList<String>();
		HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
		Set<Entry<String, Integer>> set = weightMap.entrySet();
		
		//get all the abstraction candidates
		for (Entry<String, Integer> entry : set) {
			String className = entry.getKey();
			int weight = entry.getValue();
			
			if (weight != 0 && weight % proxyW == 0) {
				proxyList.add(className);
			}
		}
		
		for (String proxy : proxyList) {
			RelatedClass relatedClass = allRelationMap.get(proxy);
			List<String> subjectList = new ArrayList<String>();
			List<String> realSubjectList = new ArrayList<String>();
			
			HashMap<String, Integer> relatedMap = relatedClass.getRelatedClassMap();
			Set<Entry<String, Integer>> relatedSet = relatedMap.entrySet();
			for (Entry<String, Integer> entry : relatedSet) {
				String className = entry.getKey();
				int weight =entry.getValue();
				
				if (weight % Weight.INHERITANCE_B == 0) {
					subjectList.add(className);
				}
				if (weight % Weight.ASSOCIATION_B == 0) {
					realSubjectList.add(className);
				}
			}
			
			if (subjectList.size() >0 && realSubjectList.size() > 0) {
				isRealSubject(proxy, subjectList, realSubjectList);
			}
		}

	}
	
	private void isRealSubject(String proxy, List<String> subjectList, List<String> realSubjectList) {
		
		for (String subject : subjectList) {
			HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
			RelatedClass relatedClass = allRelationMap.get(subject);
			
			HashMap<String, Integer> relatedMap = relatedClass.getRelatedClassMap();
			
			for (String realSubject : realSubjectList) {
				if (relatedMap.containsKey(realSubject) && 
						relatedMap.get(realSubject) % Weight.INHERITANCE_A == 0 && 
						!proxy.equals(realSubject)) {		
					printResult(proxy, subject, realSubject);
				}
			}
		}
	}

	private void printResult(String proxy, String subject, String realSubject) {
		
		if (!subject.equals(realSubject)) {
			System.out.println(number++ + ". proxy: " + proxy + "; " + classAndPath.get(proxy));
			System.out.println("subject: " + subject + "; " + classAndPath.get(subject));
			System.out.println("realSubject: " + realSubject + "; " + classAndPath.get(realSubject));
		}
		
		System.out.println();
	}
	
	
}
