package com.dpmfc.util;

import java.io.File;
import java.util.ArrayList;

public class FileUtil {
	
	private ArrayList<String> filePath = new ArrayList<String>();
	
	public ArrayList<String> getAllJavaFilePath(String dirName) {
		getJavaFile(dirName);
		return filePath;
	}
	
	public File[] getMutiPath(String rootPath) {
		File file = new File(rootPath);
		File[] fileList = null;
		if (file.isDirectory()) {
			fileList = file.listFiles(); 
			for (File tempFile : fileList) {
				System.out.println(tempFile.getName());
			}
		}
		
		return fileList;
	}
	
	/**
     * get all Java file 
     * @param dirName
     * @return
     */
	private void getJavaFile(String dirName){ 
    	File dir = new File(dirName);
    	File[]  fileList = null;
    	
    	if (dir.isDirectory()) {
    		 fileList = dir.listFiles();
    		 
    		 for (File file: fileList) {
	    		String path = file.getAbsolutePath();
	    		if (path.endsWith(".java")) {
	    			filePath.add(file.getAbsolutePath());
	            }
	    		else if (file.isDirectory()){
	    			getJavaFile(path);
	    		}
	    	}
		}
    }
    
}
