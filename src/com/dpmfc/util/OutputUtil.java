package com.dpmfc.util;

import java.io.FileWriter;
import java.io.IOException;

public class OutputUtil {

    public static void outputToTXT(String result) throws Exception{
	FileWriter fwrite = null;
	try {
	    fwrite = new FileWriter("D:\\test\\result.txt",true);
	    fwrite.write(result);
	    fwrite.flush();	
	} catch (IOException e) {
	    // TODO Auto-generated catch block
	    e.printStackTrace();
	} finally {
	    try {
		if (fwrite != null)
		    fwrite.close();
	    } catch (IOException e) {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }
}
