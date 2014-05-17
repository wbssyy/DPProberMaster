package com.dpmfc.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import com.dpmfc.bean.ProjectInfo;
import com.dpmfc.bean.RelationBean;
import com.dpmfc.bean.RelationBean.RelatedClass;
import com.dpmfc.bean.Weight;

public class AdapterClassAnalysis extends StructureAnalysis {
	
	//weight of each role of the pattern
	private int adapterW = Weight.INHERITANCE_A * Weight.INHERITANCE_A;	//25;
	private int targetW  = Weight.INHERITANCE_B;	//7;
	private int adapteeW = Weight.INHERITANCE_B;	//7;
	private static int number = 1;
	
	private HashSet<String> patternInstance = new HashSet<String>();
	
	@Override
	public void doStructureAnalyze() {

		List<String> adapterList = new ArrayList<String>();
		HashMap<String, RelatedClass> allRelationMap = allRelation.getAllRelationMap();
		Set<Entry<String, Integer>> set = weightMap.entrySet();
		
		//get all the adapter candidates
		for (Entry<String, Integer> entry : set) {
			String className = entry.getKey();
			int weight = entry.getValue();	
			if (weight % adapterW == 0) {
				adapterList.add(className);
			}
		}
		
		System.out.println( "----------"+adapterList.size() );
		
		for (String adapterName : adapterList) {
			RelatedClass relatedClass = allRelationMap.get(adapterName);
			List<String> superList   = new ArrayList<String>();
			
			HashMap<String, Integer> relatedMap = relatedClass.getRelatedClassMap();
			Set<Entry<String, Integer>> relatedSet = relatedMap.entrySet();
			
			for (Entry<String, Integer> entry : relatedSet) {
				String superName = entry.getKey();
				int weight =entry.getValue();
				if (weight % Weight.INHERITANCE_B == 0) {
					superList.add(superName);
				}
			}
			
			// do code analysis
			foobar(adapterName, superList);
		}
		
		printPatternInstance();
	}
	
	public void foobar(String adapter, List<String> superList) {
		
		List<String> adapterMethods, targetMethods, adapteeMethods;
		AdapterCodeAnalysis codeAnalysis;
		String adapterPath = classAndPath.get(adapter);
//		System.out.println(adapterPath+"---------adapterPath");
		
		codeAnalysis = new AdapterCodeAnalysis(adapterPath, "adapter");
		
		HashMap methodInvoMap = codeAnalysis.getMethodInvoMap();
		HashMap targetMethodMap = new HashMap();
		HashMap adapteeMethodMap = new HashMap();
		
		adapterMethods = codeAnalysis.getMethodList();
		
		for (String superName : superList) {
			String superPath = classAndPath.get(superName);
//			System.out.println(superPath+"++++++++++superPath");
			
			codeAnalysis = new AdapterCodeAnalysis(superPath);
			if (codeAnalysis.getFlag().equals("target")) {
				targetMethods = codeAnalysis.getMethodList();
				targetMethodMap.put(superName, targetMethods);
			} else {
				adapteeMethods = codeAnalysis.getMethodList();
				adapteeMethodMap.put(superName, adapteeMethods);
			}
		}
		
		for (String adapterM : adapterMethods) {
			
			List methodInvoList = (List) methodInvoMap.get(adapterM);
			
			Iterator tarIterator = targetMethodMap.entrySet().iterator();
			while (tarIterator.hasNext()) {
				Map.Entry entry = (Map.Entry)tarIterator.next();
				String targetName = entry.getKey().toString();
				List targetMs = (List)entry.getValue();
				
				if (targetMs.contains(adapterM)) {                 // this is a target
					
//					System.out.println("targetName: "+targetName + "; targetMethod: " + adapterM);
					
					Iterator adaIterator = adapteeMethodMap.entrySet().iterator();
					while (adaIterator.hasNext()) {
						Map.Entry adaEntry = (Map.Entry)adaIterator.next();
						String adapteeName = adaEntry.getKey().toString();
						List adapteeMs = (List)adaEntry.getValue();
						
						// is there a methodInvocation about the adaptee in the adapterM  
						for (Object adapteeM : adapteeMs) {
							String adapteeMName = adapteeM.toString();
							
							for (Object methodInvo : methodInvoList) {
								// whether to call a super class method
								String methodInvoStr = methodInvo.toString();
								if (!methodInvoStr.contains(".") && methodInvoStr.contains(adapteeMName.substring(0, adapteeMName.indexOf("(") ))) {
									// output final result
//									System.out.println(number++);
//									System.out.println("real adapter: " + adapter);
//									System.out.println(classAndPath.get(adapter));
//									System.out.println("adapter method: " + adapterM);
//									System.out.println("real target: " + targetName);
//									System.out.println(classAndPath.get(targetName));
//									System.out.println("real adaptee: " + adapteeName);
//									System.out.println(classAndPath.get(adapteeName));
//									System.out.println("adaptee method: " + adapteeM);
									
									String str1 = "real adapter: " + adapter + ", ";
									String str2 = "real target: " + targetName + ", ";
									String str3 = "real adaptee: " + adapteeName + "\n";
									String path1 = classAndPath.get(adapter) + "\n";
									String path2 = classAndPath.get(targetName) + "\n";
									String path3 = classAndPath.get(adapteeName) + "\n";
									patternInstance.add(str1 + str2 + str3 + path1 + path2 + path3);
								}
							}
						}
					}
				}	
			}
		}
	}

	private void printAllResult(String adapter, List<String> superList) {
		if (superList.size() > 0) {
			number++;
			System.out.println();
			System.out.println(number + "adapter: " + adapter);
			System.out.print("adaptee/target: ");
			for (String string : superList) {
				System.out.print(string + " ");
			}
			System.out.println();
		}
	}
	
	public void printPatternInstance() {
		Iterator iterator = patternInstance.iterator();
		while (iterator.hasNext()) {
			String instance = iterator.next().toString();
			System.out.println(number++);
			System.out.println(instance);
		}
	}
	
	private void printPartResult(String adapter, List<String> superList) {
	
		number++;
		System.out.println();
		System.out.println(number + "adapter: " + adapter);
		System.out.print("adaptee/target: ");
		for (String string : superList) {
			System.out.print(string + " ");
		}
		System.out.println();

	}
}
