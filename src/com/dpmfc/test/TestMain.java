package com.dpmfc.test;

import java.awt.Choice;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.dpmfc.bean.ProjectInfo;
import com.dpmfc.core.*;
import com.dpmfc.detector.AllRelationshipBuilder;
import com.dpmfc.detector.CodeModifier;
import com.dpmfc.util.FileUtil;

public class TestMain {
	
	private static ArrayList<String> allClassList = new ArrayList<String>();

	private static long time;
	private static long total;
	private static long average;
	
	public static void main(String[] args) throws Exception {
//		CodeModifier modifier = new CodeModifier();
		String expression = "parameters.indexOfElement( this)";
		Pattern pattern = Pattern.compile("\\.add\\w*\\( *(\\w*)\\)");
		Matcher matcher = pattern.matcher(expression);
		if (matcher.find()) {
			System.out.println(matcher.group(1));
		} else {
			System.out.println("no");
		}
//		Vector vector = new Vector();
//		TestVector testVector = new TestVector();
//		vector.add("str");
//		vector.add(123);
//		vector.add(testVector);
//		
//		for (Object object : vector) {
//			System.out.println(object);
//		}
	}
	
	private static void NonGeneric() {
		 
		  long start = System.currentTimeMillis();//开始时间
		  ArrayList no_list=new ArrayList();
		  for (int i = 0; i < 1000000; i++) {
		   no_list.add(new Integer(i)); //添加
		  }
		  for (int i = 0; i < 1000000; i++) {
		   int j = ((Integer) no_list.get(i)).intValue();//取值
		  }
		  long end = System.currentTimeMillis();//结束时间
		  time =(end - start);
		  total +=time;
		  average = (long) (total/10f);
		  System.out.println("NonGeneric:   " + time + "   ms");//每次输出消耗时间
	 }
	 
	 private static void Generic() {
		  long start = System.currentTimeMillis();//开始时间
		  ArrayList<Integer> list = new ArrayList<Integer>();
		  for (int k = 0; k < 1000000; k++) {
		   list.add(new Integer(k));//添加
		  }
		  for (int n = 0; n < 1000000; n++) {
		   int m = list.get(n).intValue();//取值
		  }
		  long end = System.currentTimeMillis();//结束时间.
		  time =(end - start);
		  total +=time;
		  average = (long) (total/10f);
		  System.out.println("Generic:   " + time + "   ms");//每次输出消耗时间
	 }
	 
	 private static void testGeneric() {
		for (int i = 0; i < 10; i++) {
			Generic();//泛型
		}
		
		for (int i = 0; i < 10; i++) {
			NonGeneric();//原始类型
		}
		
		System.out.println("10次总时间:   " + total + "   ms");//10次消耗时间
		System.out.println("10次平均值:   " + average + "   ms");
	}

}
