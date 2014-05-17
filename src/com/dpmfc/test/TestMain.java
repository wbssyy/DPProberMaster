package com.dpmfc.test;

import java.awt.Choice;
import java.io.File;
import java.util.ArrayList;

import com.dpmfc.bean.ProjectInfo;
import com.dpmfc.core.*;
import com.dpmfc.detector.AllRelationshipBuilder;
import com.dpmfc.util.FileUtil;

public class TestMain {
	
	private static ArrayList<String> allClassList = new ArrayList<String>();

	private static long time;
	private static long total;
	private static long average;
	
	public static void main(String[] args) throws Exception {
		
//		for (int i = 0; i < 10; i++) {
//			   Generic();//泛型
//		}
		
		for (int i = 0; i < 10; i++) {
			   NonGeneric();//原始类型
		}
		
		System.out.println("10次总时间:   " + total + "   ms");//10次消耗时间
		System.out.println("10次平均值:   " + average + "   ms");
	}
	
	 public static void NonGeneric() {
		 
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
	 
	 public static void Generic() {
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

}
