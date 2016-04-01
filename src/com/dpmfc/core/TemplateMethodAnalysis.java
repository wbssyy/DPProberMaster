package com.dpmfc.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.Map.Entry;

import com.dpmfc.bean.Weight;
import com.dpmfc.bean.RelationBean.RelatedClass;

public class TemplateMethodAnalysis extends StructureAnalysis{

	//weight of each role of the pattern
	private int abstractW = Weight.INHERITANCE_B; //
	private int concreteW = Weight.INHERITANCE_A; // 
	private int number = 1;
	
	@Override
	public void doStructureAnalyze() {
		// TODO Auto-generated method stub
		HashSet<String> abstractSet = new HashSet();
		
		HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
		Set<Entry<String, Integer>> set = weightMap.entrySet();
		for (Entry<String, Integer> entry : set) {
			String className = entry.getKey();
			int weight = entry.getValue();
			
			if (weight % abstractW == 0) {
				abstractSet.add(className);
			}
		}
		
		for (String entry : abstractSet) {
			foobar(entry);
		}
	}
	
	private void foobar(String abstractClass) {
		String path = classAndPath.get(abstractClass);
	
		TemplateMethodCodeAnalysis codeAnalysis = new TemplateMethodCodeAnalysis(path);
		
		if (codeAnalysis.getAbstractClass() != null && 
				codeAnalysis.getAbstractClass().equals(abstractClass) && 
				codeAnalysis.getTemplateMethod().size() > 0) {
			
			// print result
//			printPattern(abstractClass,codeAnalysis.getTemplateMethod());
		}
	}

	private void printPattern(String abstractClass, ArrayList<String> templateMethod) {
		System.out.println(number++ + ". " + abstractClass);
		for (String string : templateMethod) {
			System.out.println("templateMethod: " + string);
		}
	}
}
