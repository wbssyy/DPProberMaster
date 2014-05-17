package com.dpmfc.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.dpmfc.bean.ProjectInfo;
import com.dpmfc.bean.Weight;

public class ProxyAnalysis2 extends StructureAnalysis{
	
	//weight of each role of the pattern
	private int realSubjectW = Weight.INHERITANCE_A * Weight.ASSOCIATION_B;	//65;
	private int subjectW     = Weight.INHERITANCE_B * Weight.INHERITANCE_B;	//49;
	private int proxyW       = Weight.INHERITANCE_A * Weight.ASSOCIATION_A;	//55;
	private static int number = 0;
	@Override
	public void doStructureAnalyze(ProjectInfo projectInfo) {
		
		HashMap inheritanceMap = projectInfo.getInheritanceMap();
		HashMap associationMap = projectInfo.getAssciationMap();
		
		HashMap<Integer, List<String>> specialWeightMap = projectInfo.getContainWClassMap();
		List<String> proxyList = specialWeightMap.get(proxyW);
		
		for (String proxyName : proxyList) {
			
			HashSet inheritanceSet = (HashSet)inheritanceMap.get(proxyName);
			
			if (inheritanceSet != null) {	
		
				List subjectCanList = projectInfo.containWeight(subjectW, inheritanceSet);
				
				if (subjectCanList.size() > 0) {
					
					//get realSubject candidate list through associationMap
					HashSet associationSet = (HashSet)associationMap.get(proxyName);
					List realSubjectCanList = projectInfo.containWeight(realSubjectW, associationSet);
					
					if (realSubjectCanList.size() > 0) {
	
						//check the relationship between subject candidate and realSubject candidate
						for (Object realSubject : realSubjectCanList) {
							HashSet tempInherSet = (HashSet)inheritanceMap.get(realSubject);
							
							for (Object subject : subjectCanList) {
								if (tempInherSet.size()>0 && tempInherSet.contains(subject) && !proxyName.equals(realSubject)) {
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
