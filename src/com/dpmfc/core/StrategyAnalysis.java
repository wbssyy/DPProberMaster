package com.dpmfc.core;

import java.util.HashMap;
import java.util.List;

import com.dpmfc.bean.ProjectInfo;
import com.dpmfc.bean.Weight;

public class StrategyAnalysis extends StructureAnalysis {

	//weight of each role of the pattern
	private int strategyW         = Weight.INHERITANCE_B * Weight.ASSOCIATION_B;	//91;
	private int concreteStrategyW = Weight.INHERITANCE_A;	//5;
	private int contextW          = Weight.ASSOCIATION_A;	//11;
	private static int number = 0;
	
	@Override
	public void doStructureAnalyze(ProjectInfo projectInfo) {

		HashMap<Integer, List<String>> specialWeightMap = projectInfo.getContainWClassMap();
		List<String> strategyList = specialWeightMap.get(strategyW);
		for (String strategy : strategyList) {
			number++;
			System.out.println(number + ". strategy: " + strategy);
		}
	}
	
	private void printResult() {
		
	}

}
