package com.tim.dao;

import com.tim.util.LogTWM;
import com.tim.util.bbdd.QueryRunner;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Priority;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.model.Rule;
import com.tim.model.Strategy;
import com.tim.model.Trading;
import com.tim.model.Trading_Market;

public class RuleDAO {

	
	public static boolean updateRuleByRuleID(Rule oRule) 
    {
       boolean resultadoOK = true;

       try
       {
       
       		QueryRunner qr=new QueryRunner();
       		
       		String SqlRule= "update rule ";       		
       		SqlRule +=  "set ruleID=##ruleID##," ;
       		SqlRule += "name=##name##,";
       		SqlRule += "description=##description##,";
       		SqlRule += "active=##active##,";
       		SqlRule += "buy_minto_offset_close=##buy_minto_offset_close##,";
       		SqlRule += "buy_limit_amount_day=##buy_limit_amount_day##,";
       		SqlRule += "className=##className##,";
       		SqlRule += "buy_limit_torepeat_same_share=##buy_limit_torepeat_same_share##,";
       		SqlRule += "buy_max_positionday_share=##buy_max_positionday_share##";
       		
       		SqlRule += " where ruleID=" + oRule.getRuleId();
       		
       		qr.updateBean(SqlRule,oRule);
               
                       
           }
           catch(Exception e)
           {          
        	   resultadoOK = false;
        	   LogTWM.getLog(RuleDAO.class);
             	LogTWM.log(Priority.FATAL, "updateRuleByRuleID:" + e.getMessage());
              
           }
           return resultadoOK;
       }

	
	 @SuppressWarnings("rawtypes")
	   	public static Rule getRule(String RuleClassName) 
	       {
		 Rule resultado = null;

	          try
	              {
	                  QueryRunner qr=new QueryRunner();
	                  java.util.List datos = new java.util.ArrayList();               
	                  datos.add(RuleClassName);               
	                  BeanHandler blh= new BeanHandler(Rule.class);
	                  resultado =(Rule) qr.query("select  *  from Rule where  className=?", datos.toArray(),blh);
	                          
	              }
	              catch(Exception e)
	              {                
	           	   LogTWM.getLog(RuleDAO.class);
	              	   LogTWM.log(Priority.FATAL, "getRule:" + e.getMessage());
	                 
	              }
	              return resultado;
	          }    
	
	 @SuppressWarnings("rawtypes")
		public static java.util.List<Rule> getListRules(int tradingID) 
	    {
	       java.util.List<Rule> resultado = null;

	       try
	           {
	               QueryRunner qr=new QueryRunner();
	               java.util.List datos = new java.util.ArrayList();                
	               datos.add(tradingID);               
	               BeanListHandler blh= new BeanListHandler(Rule.class);
	               resultado =(java.util.List) qr.query("select a.* from rule a, trading_rule b where a.ruleID  = b.ruleID and b.tradingID=?", datos.toArray(),blh);	                      
	           }
	           catch(Exception e)
	           {                
	        	   LogTWM.getLog(RuleDAO.class);
	           	   LogTWM.log(Priority.FATAL, "getListRules:" + e.getMessage());	              
	           }
	           return resultado;
	       }    
	 @SuppressWarnings("rawtypes")
		public static java.util.List<Rule> getListRules() 
	    {
	       java.util.List<Rule> resultado = null;

	       try
	           {
	               QueryRunner qr=new QueryRunner();
	               BeanListHandler blh= new BeanListHandler(Rule.class);
	               resultado =(java.util.List) qr.query("select a.* from rule a",blh);	                      
	           }
	           catch(Exception e)
	           {                
	        	   LogTWM.getLog(RuleDAO.class);
	           	   LogTWM.log(Priority.FATAL, "getListRules:" + e.getMessage());	              
	           }
	           return resultado;
	       }    
	 @SuppressWarnings("rawtypes")
		public static Rule getRule(int RuleID) 
	    {
	       Rule resultado = null;

	       try
	           {
	               QueryRunner qr=new QueryRunner();
	               java.util.List datos = new java.util.ArrayList();               
	               datos.add(RuleID);               
	               BeanHandler blh= new BeanHandler(Rule.class);
	               resultado = (Rule) qr.query("select a.* from rule a where a.ruleID =?", datos.toArray(),blh);	                      
	           }
	           catch(Exception e)
	           {                
	        	   LogTWM.getLog(Rule.class);
	           	   LogTWM.log(Priority.FATAL, "getRule:" + e.getMessage());	              
	           }
	           return resultado;
	       }    
	  
	        
}
