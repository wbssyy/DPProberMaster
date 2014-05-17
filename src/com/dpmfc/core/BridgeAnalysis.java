package com.dpmfc.core;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

import com.dpmfc.bean.ProjectInfo;
import com.dpmfc.bean.Weight;

public class BridgeAnalysis extends StructureAnalysis {
	//weight of each role of the pattern
	private int abstractionW         = Weight.INHERITANCE_B * Weight.ASSOCIATION_A;	//77;
	private int refinedAbstractionW  = Weight.INHERITANCE_A;
	private int implememtorW         = Weight.INHERITANCE_B * Weight.ASSOCIATION_B;	//91;
	private int concreteImplementorW = Weight.INHERITANCE_A;	//5;
	private static int number = 0;
	@Override
	public void doStructureAnalyze(ProjectInfo projectInfo) {
		
		HashMap associationMap = projectInfo.getAssciationMap();
		
		HashMap<Integer, List<String>> specialWeightMap = projectInfo.getContainWClassMap();
		List<String> implementorList = specialWeightMap.get(implememtorW);
		List<String> abstractionList = specialWeightMap.get(abstractionW);
		
		if (abstractionList != null && implementorList != null) {
			for (String abstraction : abstractionList) {
				for (String implementor : implementorList) {
					
					HashSet implementorSet = (HashSet)associationMap.get(abstraction);
					// abstraction and implementor can be match
					if (implementorSet != null && !abstraction.equals(implementor) && implementorSet.contains(implementor)) {
						List refinedAbsList  = projectInfo.whichPoinToIt(abstraction, Weight.INHERITANCE_A);
						List concreteImpList = projectInfo.whichPoinToIt(implementor, Weight.INHERITANCE_A);
						
						printResult(abstraction, implementor, refinedAbsList, concreteImpList);
					}
					
				}
			}
		}
	}

	private void printResult(String abstraction, String implementor, List refinedAbsList, List concreteImpList) {
		number++;
		System.out.println(number + ". abstraction: " + abstraction);
		System.out.println("implementor: " + implementor);
		
		System.out.print("refinedAbstraction: ");
		for (Object refinedAbs : refinedAbsList) {
			System.out.print(refinedAbs + " ");
		}
		System.out.println();
		
		System.out.print("concreteImplementor: ");
		for (Object concreteImp : concreteImpList) {
			System.out.print(concreteImp + " ");
		}
		System.out.println();
	}
}
