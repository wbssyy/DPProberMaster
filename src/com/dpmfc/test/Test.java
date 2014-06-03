package com.dpmfc.test;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.Expression;
import org.eclipse.jdt.core.dom.MethodInvocation;

import com.dpmfc.bean.RelationBean;
import com.dpmfc.bean.RelationBean.RelatedClass;
import com.dpmfc.core.AdapterClassAnalysis;
import com.dpmfc.core.AdapterObjectAnalysis;
import com.dpmfc.core.CompositeAnalysis;
import com.dpmfc.core.StructureAnalysis;
import com.dpmfc.core.WeightCalculator;
import com.dpmfc.detector.AllRelationshipBuilder;
import com.dpmfc.detector.AssociationInfoDetector;
import com.dpmfc.detector.ClassDetector;
import com.dpmfc.detector.DependencyInfoDetector;
import com.dpmfc.detector.InheritanceInfoDetector;
import com.dpmfc.detector.RelationDetector;
import com.dpmfc.util.FileUtil;


public class Test {

	private static RelationBean allRelation = new RelationBean();
	
	public static void main(String[] args) throws Exception {
//D:\\test\\src\\java\\util
//D:\\JOSS-1\\JOSS-1\\struts(2.3.16)\\src
		String projectPath = "D:\\test\\src\\java\\awt";
		
		/*
		 * Extract and build all relation 
		 */
		long a = System.currentTimeMillis();
		AllRelationshipBuilder relationshipBuilder = new AllRelationshipBuilder();
		relationshipBuilder.buildAllRelationship(projectPath);
//		relationshipBuilder.printRelation();
		RelationBean allRelation = relationshipBuilder.getAllRelation();
//		allRelation.printAllRelationMap();
		
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
		
		
		/*
		 * analysis pattern
		 */
		StructureAnalysis analysis;
		HashMap<String, String> classAndPath = relationshipBuilder.getClassAndPath();
		
//		analysis = new AdapterClassAnalysis();
//		analysis.init(weightMap, allRelation);
//		analysis.setClassAndPath(classAndPath);
//		analysis.doStructureAnalyze();
//		
//		analysis = new AdapterObjectAnalysis();
//		analysis.init(weightMap, allRelation);
//		analysis.setClassAndPath(classAndPath);
//		analysis.doStructureAnalyze();
		
//		analysis = new CompositeAnalysis();
//		analysis.init(weightMap, allRelation);
//		analysis.setClassAndPath(classAndPath);
//		analysis.doStructureAnalyze();
		
//		analysis = new AdapterObjectAnalysis();
//		analysis.init(weightMap, allRelation);
//		analysis.doStructureAnalyze();
	
		
		/*
		 * calculate time-consuming
		 */
		long b = System.currentTimeMillis() - a;
		System.out.println(b);
		a = System.currentTimeMillis();
	}
}
