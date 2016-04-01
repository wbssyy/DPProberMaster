package com.dpmfc.test;

import java.util.HashMap;

import com.dpmfc.bean.RelationBean;
import com.dpmfc.core.AdapterClassAnalysis;
import com.dpmfc.core.AdapterObjectAnalysis;
import com.dpmfc.core.BridgeAnalysis;
import com.dpmfc.core.CompositeAnalysis;
import com.dpmfc.core.DecoratorAnalysis;
import com.dpmfc.core.FacadeAnalysis;
import com.dpmfc.core.ObserverAnalysis;
import com.dpmfc.core.ProxyAnalysis;
import com.dpmfc.core.SingletonAnalysis;
import com.dpmfc.core.StrategyAnalysis;
import com.dpmfc.core.StructureAnalysis;
import com.dpmfc.core.TemplateMethodAnalysis;
import com.dpmfc.core.WeightCalculator;
import com.dpmfc.detector.AllRelationshipBuilder;


public class Test {

	private static RelationBean allRelation = new RelationBean();
	
	public static void main(String[] args) throws Exception {
//D:\\test\\src\\java\\util
//D:\\JOSS-1\\JOSS-1\\struts(2.3.16)\\src
		String projectPath = "D:\\test\\jhotdraw60b1\\jhotdraw60b1";
		
		/*
		 * Extract and build all relation 
		 */
		long a = System.currentTimeMillis();
		AllRelationshipBuilder relationshipBuilder = new AllRelationshipBuilder();
		relationshipBuilder.buildAllRelationship(projectPath);
//		relationshipBuilder.printRelation();
		RelationBean allRelation = relationshipBuilder.getAllRelation();
		
		
		HashMap<String, String> sourceAndParameterMap = relationshipBuilder.getSourceAndParameterMap();
		/*
		 * test 
		 */
//		HashMap<String, String> map = relationshipBuilder.getClassAndPath();
//		Iterator iterator = map.entrySet().iterator();
//		while (iterator.hasNext()) {
//			Map.Entry entry = (Map.Entry)iterator.next();
//			String key = entry.getKey().toString();
//			String path = entry.getValue().toString();
//			
//			System.out.println("class:　" + key + "; path: " + path);
//		}
		
		
		/*
		 * calculate weight
		 */
		WeightCalculator calculator = new WeightCalculator();
		calculator.calculateWeight(allRelation);
		HashMap<String, Integer> weightMap = calculator.getWeightMap();
		
//		calculator.printWeightMap();
		
		
		// calculate system relation and class weight
		System.out.println("system weight");
		System.out.println(System.currentTimeMillis()-a);
		
		/*
		 * analysis pattern
		 */
		StructureAnalysis analysis;
		HashMap<String, String> classAndPath = relationshipBuilder.getClassAndPath();
		
		long time = System.currentTimeMillis();
		
		analysis = new AdapterClassAnalysis();
		analysis.init(weightMap, allRelation);
		analysis.setClassAndPath(classAndPath);
		analysis.doStructureAnalyze();
		
		analysis = new AdapterObjectAnalysis();
		analysis.init(weightMap, allRelation);
		analysis.setClassAndPath(classAndPath);
		analysis.doStructureAnalyze();
		
//		analysis = new BridgeAnalysis();
//		analysis.init(weightMap, allRelation);
//		analysis.setClassAndPath(classAndPath);
//		analysis.doStructureAnalyze();
//		
//		analysis = new TemplateMethodAnalysis();
//		analysis.init(weightMap, allRelation);
//		analysis.setClassAndPath(classAndPath);
//		analysis.doStructureAnalyze();
//	
//		analysis = new DecoratorAnalysis();
//		analysis.init(weightMap, allRelation);
//		analysis.setClassAndPath(classAndPath);
//		analysis.doStructureAnalyze();
//		
//		analysis = new ProxyAnalysis();
//		analysis.init(weightMap, allRelation);
//		analysis.setClassAndPath(classAndPath);
//		analysis.doStructureAnalyze();
//		
//		analysis = new CompositeAnalysis();
//		analysis.init(weightMap, allRelation);
//		analysis.setClassAndPath(classAndPath);
//		analysis.setSourceAndParameterMap(sourceAndParameterMap);
//		analysis.doStructureAnalyze();
//		
//		analysis = new SingletonAnalysis();
//		analysis.init(weightMap, allRelation);
//		analysis.setClassAndPath(classAndPath);
//		analysis.doStructureAnalyze();
//		
//		analysis = new StrategyAnalysis();
//		analysis.init(weightMap, allRelation);
//		analysis.setClassAndPath(classAndPath);
//		analysis.doStructureAnalyze();
//		
//		analysis = new FacadeAnalysis();
//		analysis.init(weightMap, allRelation);
//		analysis.setClassAndPath(classAndPath);
//		analysis.doStructureAnalyze();
//		
//		allRelation.printAllRelationMap();
//		allRelation.printToFile();
		
		/*
		 * calculate time-consuming
		 */
		long b = System.currentTimeMillis() - time;
		System.out.println( "time "+b);
		a = System.currentTimeMillis();
	}
}
