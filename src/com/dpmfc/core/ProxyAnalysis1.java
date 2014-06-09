package com.dpmfc.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dpmfc.bean.ProjectInfo;

public class ProxyAnalysis1 extends StructureAnalysis{
	
	//weight of each role of the pattern
	private int realSubjectW = 65;
	private int subjectW     = 49;
	private int proxyW       = 55;
	private static int number = 0;
	@Override
	public void doStructureAnalyze(ProjectInfo projectInfo) {
		
		HashMap weightMap      = projectInfo.getWeightMap();
		HashMap inheritanceMap = projectInfo.getInheritanceMap();
		HashMap associationMap = projectInfo.getAssciationMap();
		
//		HashMap<Integer, List<String>> specialWeightMap = projectInfo.getContainWClassMap();
//		List<String> proxyList = specialWeightMap.get(proxyW);
//		
//		for (String proxyName : proxyList) {
//			
//		}
		
		Iterator iterator = weightMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			String proxyName = entry.getKey().toString();
			Integer weight = (Integer)entry.getValue();
			
			if (weight % proxyW == 0) {
//				System.out.println("there are some proxy candidate!");
				
				//this class is a proxy example of candidate
				//get subject candidate list through inheritanceMap
				HashSet inheritanceSet = (HashSet)inheritanceMap.get(proxyName);
				List subjectCanList = projectInfo.containWeight(subjectW, inheritanceSet);
				
				if (subjectCanList.size() > 0) {
					
//					System.out.println("there are some subject candidate!");
					
					//get realSubject candidate list through associationMap
					HashSet associationSet = (HashSet)associationMap.get(proxyName);
					List realSubjectCanList = projectInfo.containWeight(realSubjectW, associationSet);
					
					if (realSubjectCanList.size() > 0) {
						
//						System.out.println("there are some realsubject candidate!");
						
						//check the relationship between subject candidate and realSubject candidate
						for (Object realSubject : realSubjectCanList) {
							HashSet tempInherSet = (HashSet)inheritanceMap.get(realSubject);
							
							for (Object subject : subjectCanList) {
								if (tempInherSet.contains(subject)) {
									number++;
									System.out.println(number+". proxy: " + proxyName);
									System.out.println("subject: " + subject);
									System.out.println("realSubject: " + realSubject);
								}
							}
						}
					}
				}
			}
		}
		
	}
	
}
