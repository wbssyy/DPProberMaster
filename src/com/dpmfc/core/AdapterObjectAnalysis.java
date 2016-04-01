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

public class AdapterObjectAnalysis extends StructureAnalysis {
	
	//weight of each role of the pattern
	private int adapterW = Weight.INHERITANCE_A * Weight.ASSOCIATION_A;	//55;
	private int targetW  = Weight.INHERITANCE_B;	//7;
	private int adapteeW = Weight.ASSOCIATION_B;	//13;
	private static int number = 1;
	private static int temp = 0;
	
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
		
		for (String adapterName : adapterList) {
			RelatedClass relatedClass = allRelationMap.get(adapterName);
			List<String> targetList   = new ArrayList<String>();
			List<String> adapteeList   = new ArrayList<String>();
			
			HashMap<String, Integer> relatedMap = relatedClass.getRelatedClassMap();
			Set<Entry<String, Integer>> relatedSet = relatedMap.entrySet();
			for (Entry<String, Integer> entry : relatedSet) {
				String className = entry.getKey();
				int weight =entry.getValue();
				
				if (weight % Weight.INHERITANCE_B == 0) {
					targetList.add(className);
				}
				
				if (weight % Weight.ASSOCIATION_B == 0) {
					adapteeList.add(className);
				}
			}
			
			if (targetList.size()>0 && adapteeList.size()>0) {
//				System.out.println(temp++ + "-----------------");
//				printResult(adapterName, targetList, adapteeList);
				
				try {
					foobar(adapterName, targetList, adapteeList);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		// print result
//		printPatternInstance();
	}
	
	public void printResult(String adapter, List<String> targetList, List<String> adapteeList) {
		for (String target : targetList) {
			for (String adaptee : adapteeList) {
				if (!adapter.contains(".") && !target.equals(adaptee)) {
					number++;	
					System.out.println();
					System.out.println(number + "adapter: " + adapter);
					System.out.print("target: ");
					System.out.print(target + " ");
					System.out.println();
					System.out.print("adaptee: ");
					System.out.print(adaptee + " ");
				}
			}
		}
	}
	
	public void foobar(String adapter, List<String> targetList, List<String> adapteeList) {
		
		List<String> adapterMethods, targetMethods, adapteeMethods;
		AdapterCodeAnalysis codeAnalysis;
		String adapterPath = classAndPath.get(adapter);
//		System.out.println(adapterPath+"---------adapterPath");
		
		codeAnalysis = new AdapterCodeAnalysis(adapterPath, "adapter");
		
		HashMap methodInvoMap    = codeAnalysis.getMethodInvoMap();
		
		HashMap adapterField     = codeAnalysis.getFieldNameAndType();
		HashMap targetMethodMap  = new HashMap();
		HashMap adapteeMethodMap = new HashMap();
		
		adapterMethods = codeAnalysis.getMethodList();
		
		for (String targetName : targetList) {
			String targetPath = classAndPath.get(targetName);
//			System.out.println(superPath+"++++++++++superPath");
			
			codeAnalysis = new AdapterCodeAnalysis(targetPath);
			
			targetMethods = codeAnalysis.getMethodList();
			targetMethodMap.put(targetName, targetMethods);
		}
		
		for (String adapteeName : adapteeList) {
			String adapteePath = classAndPath.get(adapteeName);
			
			codeAnalysis = new AdapterCodeAnalysis(adapteePath);
			
			adapteeMethods = codeAnalysis.getMethodList();
			adapteeMethodMap.put(adapteeName, adapteeMethods);
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
							
							if (methodInvoList != null) {
								for (Object methodInvo : methodInvoList) {
									// whether to call a super class method
									String methodInvoStr = methodInvo.toString();
									
//									System.out.println(methodInvoStr + "-------method invocation");
//									System.out.println(adapteeMName + "========adaptee method name");
									
									if (methodInvoStr.contains(".")) {
										String fieldName = methodInvoStr.substring(0, methodInvoStr.indexOf("."));
//										System.out.println(fieldName + "--------------fieldName");
										String adapteeType = null;
										
										if (adapterField.get(fieldName) != null) {
											adapteeType = adapterField.get(fieldName).toString();
										}
//										System.out.println(adapteeMName + "---------adapteemname");
										adapteeMName = adapteeMName.substring(0, adapteeMName.indexOf("(")+1);
//										System.out.println(adapteeMName + "---------adapteemname");
										if (adapteeType != null && adapteeType.equals(adapteeName) && 
												methodInvoStr.contains(adapteeMName) &&
												!classAndPath.get(adapter).equals(classAndPath.get(targetName)) &&
												!classAndPath.get(adapter).equals(classAndPath.get(adapteeName)) && 
												!targetName.equals(adapteeName)) {
											// output final result
//											System.out.println(number++);
//											System.out.println("real adapter: " + adapter);
//											System.out.println(classAndPath.get(adapter));
//											System.out.println("adapter method: " + adapterM);
//											System.out.println("real target: " + targetName);
//											System.out.println(classAndPath.get(targetName));
//											System.out.println("real adaptee: " + adapteeName);
//											System.out.println(classAndPath.get(adapteeName));
//											System.out.println("adaptee method: " + adapteeM.toString());
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
}
