package com.dpmfc.core;

import java.awt.Component;
import java.awt.Container;
import java.awt.Menu;
import java.awt.MenuBar;
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

public class CompositeAnalysis extends StructureAnalysis {

	//weight of each role of the pattern
	private int componentW = Weight.INHERITANCE_B * Weight.INHERITANCE_B * Weight.ASSOCIATION_B; //637;
	private int compositeW = Weight.INHERITANCE_A * Weight.ASSOCIATION_A; //55;
	private int leafW      = Weight.INHERITANCE_A; //5;
	private static int number = 1;
	
	@Override
	public void doStructureAnalyze() {
		
		List<String> componentList = new ArrayList<String>();
		HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
		Set<Entry<String, Integer>> set = weightMap.entrySet();
		
//		RelatedClass map = allRelationMap.get("MenuComponent.MenuBar");
//		HashMap<String, String> temp = map.getRelatedClassMap();
//		Iterator iterator1 = temp.entrySet().iterator();
//		while (iterator1.hasNext()) {
//			Map.Entry entry = (Map.Entry)iterator1.next();
//			String keyString = entry.getKey().toString();
//			String vString = entry.getValue().toString();
//			System.out.println(keyString + " " + vString);
//		}
		
		//get all the component candidates
		for (Entry<String, Integer> entry : set) {
			String className = entry.getKey();
			int weight = entry.getValue();	
			if (weight % componentW == 0) {
				componentList.add(className);
			}
		}
		
		for (String cn : componentList) {
			System.out.println(cn);
		}
		
		System.out.println(componentList.size());
		for (String component : componentList) {
					
			RelatedClass relatedClass = allRelationMap.get(component);
			List<String> leafList   = new ArrayList<String>();
			List<String> compositeList   = new ArrayList<String>();
			
			HashMap<String, Integer> relatedMap = relatedClass.getRelatedClassMap();
			Set<Entry<String, Integer>> relatedSet = relatedMap.entrySet();
			
			for (Entry<String, Integer> entry : relatedSet) {
				String className = entry.getKey();
				int weight = entry.getValue();
				
//				if (weight % leafW == 0) {
//					leafList.add(className);
//				}
				
//				if (component.equals("MenuItem")) {
//					System.out.println("ddd");
//				}
				if (weight % compositeW == 0) {
					compositeList.add(className);
//					System.out.println("component: " + component + "; composite: " + className );
				}
			}
			
			foobar(component, leafList, compositeList);
		}
		
		System.out.println(sourceAndParameterMap.size());
		Iterator iterator = sourceAndParameterMap.entrySet().iterator();
		while (iterator.hasNext()) {
			Map.Entry entry = (Map.Entry)iterator.next();
			String keyString = entry.getKey().toString();
			String classNameString = entry.getValue().toString();
			System.out.println(keyString + "---->" + classNameString);
		}
		
//		printPatternInstance();
	}

	public void foobar(String component, List<String> leafList, List<String> compositeList) {

		CompositeCodeAnalysis codeAnalysis;
		
		for (String composite : compositeList) {
			String compositePath = classAndPath.get(composite);
//			System.out.println("composite: "+composite);
			codeAnalysis = new CompositeCodeAnalysis(compositePath);
			
			HashMap<String, String> fieldMap = codeAnalysis.getFieldNameAndType();
			
			//there is a generic field in the class
			if (fieldMap.size() > 0) {
				Iterator iterator = fieldMap.entrySet().iterator();
				while (iterator.hasNext()) {
					Map.Entry entry = (Map.Entry)iterator.next();
					String fieldType = entry.getValue().toString();

					String associationClass = fieldType.substring(fieldType.indexOf("<")+1, fieldType.indexOf(">"));
					if (component.contains(associationClass)) {
						System.out.println(associationClass);
						printPatternInstance(component, composite);
					}
					
				}
			} else {
				
				// there is a generic in the class after "add generic"
				if (sourceAndParameterMap.get(composite) != null && 
						sourceAndParameterMap.get(composite).equals(component)) {
					printPatternInstance(component, composite);
				}
			}
			
		}
		
		
	}

	public void printPatternInstance(String component, String composite) {
		String str1 = "component: " + component + ", ";
		String str2 = "composite: " + composite + "\n";
		String path1 = classAndPath.get(component) + "\n";
		String path2 = classAndPath.get(composite) + "\n";
//		StringBuffer stringBuffer = new StringBuffer();
		System.out.println(number++ + ". " + str1 + str2 + path1 + path2);
	}
}
