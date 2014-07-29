package com.dpmfc.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Map.Entry;

import com.dpmfc.bean.ProjectInfo;
import com.dpmfc.bean.Weight;
import com.dpmfc.bean.RelationBean.RelatedClass;

public class BridgeAnalysis extends StructureAnalysis {
	//weight of each role of the pattern
	private int abstractionW         = Weight.INHERITANCE_B * Weight.ASSOCIATION_A;	//77;
	private int refinedAbstractionW  = Weight.INHERITANCE_A;
	private int implementorW         = Weight.INHERITANCE_B * Weight.ASSOCIATION_B;	//91;
	private int concreteImplementorW = Weight.INHERITANCE_A;	//5;
	private static int number = 1;
	
	@Override
	public void doStructureAnalyze() {
		
		List<String> abstractionList = new ArrayList<String>();
		HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
		Set<Entry<String, Integer>> set = weightMap.entrySet();
		
		//get all the abstraction candidates
		for (Entry<String, Integer> entry : set) {
			String className = entry.getKey();
			int weight = entry.getValue();
			
			if (weight != 0 && weight % abstractionW == 0) {
				abstractionList.add(className);
			}
		}
		
		for (String abstraction : abstractionList) {
			RelatedClass relatedClass = allRelationMap.get(abstraction);
			List<String> implementorList   = new ArrayList<String>();
			
			HashMap<String, Integer> relatedMap = relatedClass.getRelatedClassMap();
			Set<Entry<String, Integer>> relatedSet = relatedMap.entrySet();
			for (Entry<String, Integer> entry : relatedSet) {
				String className = entry.getKey();
				int weight =entry.getValue();
				
				if (weight % Weight.ASSOCIATION_B == 0) {
					implementorList.add(className);
					System.out.println(abstraction + "--" + className + "--" + weight);
				}
			}
			
			if (implementorList.size()>0) {
				printResult(abstraction, implementorList);
//				System.out.println(temp++ + "-----------------");
//				printResult(adapterName, targetList, adapteeList);
				
//				try {
//					foobar(adapterName, targetList, adapteeList);
//				} catch (Exception e) {
//					// TODO Auto-generated catch block
//					e.printStackTrace();
//				}
			}
		}

	}

	private void printResult(String abstraction, List<String> implememtorList) {
		
		for (String implementor : implememtorList) {
			if (!abstraction.equals(implementor)) {
				System.out.println(number++ + ". abstraction: " + abstraction);
				System.out.println("implementor: " + implementor);
			}
		}
		
//		System.out.print("refinedAbstraction: ");
//		for (Object refinedAbs : refinedAbsList) {
//			System.out.print(refinedAbs + " ");
//		}
//		System.out.println();
//		
//		System.out.print("concreteImplementor: ");
//		for (Object concreteImp : concreteImpList) {
//			System.out.print(concreteImp + " ");
//		}
		System.out.println();
	}
}
