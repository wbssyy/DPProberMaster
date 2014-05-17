package com.dpmfc.core;

import java.util.List;

import com.dpmfc.bean.ProjectInfo;
import com.dpmfc.bean.Weight;

public class SingletonAnalysis extends StructureAnalysis {
	
	private int singletonW = Weight.ASSOCIATION_A * Weight.ASSOCIATION_B; //143
	private static int number = 0;

	@Override
	public void doStructureAnalyze(ProjectInfo projectInfo) {
		// TODO Auto-generated method stub
		List<String> singletonList = projectInfo.containWeight(singletonW);
		for (String string : singletonList) {
			number++;
			System.out.println(number + "singleton: " + string);
		}
	}
	
	private List doCodeAnalyze() {
		return null;
	}

}
