package com.tim.util;

import org.apache.log4j.Logger;
import org.apache.log4j.Priority;


public class LogTWM  {


    // Creation & retrieval methods:
	
	private static Logger oLogTWS = null;
	
	
	
    public static void getLog(Class  class1) 
    {
    	oLogTWS = Logger.getLogger(class1);		
    	
    }
    
    // printing methods:
    public static void trace(Object message) {
    	oLogTWS.trace(message);
    }
    
    public static  void debug(Object message){
    	oLogTWS.debug(message);
    }
    public static void info(Object message){
    	oLogTWS.info(message);
    }
    public static  void warn(Object message)
    
    {
    	oLogTWS.warn(message);
    }
    public static void error(Object message){
    	oLogTWS.error(message);
    }
    public static void fatal(Object message)
    
    {
    	oLogTWS.fatal(message);
    }
   
    // generic printing method:
    public static void log(Priority oPrior, Object message) {
    	oLogTWS.log(oPrior, message);
    }

	  

	
}
