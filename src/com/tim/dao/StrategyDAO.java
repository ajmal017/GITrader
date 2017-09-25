/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tim.dao;

import java.sql.Timestamp;
import java.util.Calendar;

import com.tim.util.LogTWM;
import com.tim.util.bbdd.QueryRunner;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Priority;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.model.Market;
import com.tim.model.Position;
import com.tim.model.Strategy;
import com.tim.model.Trading;
import com.tim.model.Trading_Market;


/**
 *
 * @author je10034
 */
public class StrategyDAO {

    public StrategyDAO() {
    }

    @SuppressWarnings("rawtypes")
	public static java.util.List getListStrategies(int tradingID) 
    {
       java.util.List resultado = null;

       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();               
               datos.add(tradingID);               
               BeanListHandler blh= new BeanListHandler(Strategy.class);
               resultado =(java.util.List) qr.query("select a.* from strategy a, trading_strategy b where a.strategyID  = b.strategyID and b.tradingID=? order by type asc", datos.toArray(),blh);
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(StrategyDAO.class);
           	   LogTWM.log(Priority.FATAL, "getListStrategies:" + e.getMessage());
              
           }
           return resultado;
       }    
  
    
    @SuppressWarnings("rawtypes")
	public static Strategy getStrategy(Long StrategyID) 
    {
    	Strategy resultado = null;

       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();               
               datos.add(StrategyID);               
               BeanHandler blh= new BeanHandler(Strategy.class);
               resultado =(Strategy) qr.query("select a.* from strategy a where  a.strategyID=?", datos.toArray(),blh);
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(StrategyDAO.class);
           	   LogTWM.log(Priority.FATAL, "getStrategy:" + e.getMessage());
              
           }
           return resultado;
       }
    
    @SuppressWarnings("rawtypes")
   	public static Strategy getStrategy(String StrategyClassName) 
       {
       	Strategy resultado = null;

          try
              {
                  QueryRunner qr=new QueryRunner();
                  java.util.List datos = new java.util.ArrayList();               
                  datos.add(StrategyClassName);               
                  BeanHandler blh= new BeanHandler(Strategy.class);
                  resultado =(Strategy) qr.query("select  *  from strategy where  className=?", datos.toArray(),blh);
                          
              }
              catch(Exception e)
              {                
           	   LogTWM.getLog(StrategyDAO.class);
              	   LogTWM.log(Priority.FATAL, "getStrategy:" + e.getMessage());
                 
              }
              return resultado;
          }    
    
    public static boolean updateStrategyByStrategyID(Strategy oStrategy) 
    {
       boolean resultadoOK = true;

       try
       {
       
       		QueryRunner qr=new QueryRunner();
       		
       		String Sqlstrategy= "update strategy ";       		
       		Sqlstrategy +=  "set strategyID=##strategyID##," ;
       		Sqlstrategy += "name=##name##,";
       		Sqlstrategy += "description=##description##,";
       		Sqlstrategy += "activada=##activada##,";
       		Sqlstrategy += "offsetmin_fromopen_market=##offsetmin_fromopen_market##,";
       		Sqlstrategy += "type=##type##,";
       		Sqlstrategy += "className=##className##,";
       		Sqlstrategy += "sell_all_deadline_min_toclose=##sell_all_deadline_min_toclose##,";
       		Sqlstrategy += "sell_all_deadline_type_operation=##sell_all_deadline_type_operation##,";
       		Sqlstrategy += "sell_all_deadline_deactivate_trading=##sell_all_deadline_deactivate_trading##,";
       		Sqlstrategy += "tmp_sell_all_deadline_min_toclose=##tmp_sell_all_deadline_min_toclose##,";       		
       		Sqlstrategy += "mcad_timebars=##mcad_timebars##,";
       		Sqlstrategy += "macd_periods=##macd_periods##,";
       		Sqlstrategy += "mcad_rateavg_entry=##mcad_rateavg_entry##";
       		
       		Sqlstrategy += " where strategyID=" + oStrategy.getStrategyId();
       		
       		qr.updateBean(Sqlstrategy,oStrategy);
               
                       
           }
           catch(Exception e)
           {          
        	   resultadoOK = false;
        	   LogTWM.getLog(StrategyDAO.class);
             	LogTWM.log(Priority.FATAL, "updateStrategyByStrategyID:" + e.getMessage());
              
           }
           return resultadoOK;
       }
    
    @SuppressWarnings("rawtypes")
	public static java.util.List getListStrategies() 
    {
       java.util.List resultado = null;

       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();                                            
               BeanListHandler blh= new BeanListHandler(Strategy.class);
               resultado =(java.util.List) qr.query("select a.* from strategy a",blh);
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(StrategyDAO.class);
           	   LogTWM.log(Priority.FATAL, "getListStrategies:" + e.getMessage());
              
           }
           return resultado;
       }    
  
    
    public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
   	 
    	Strategy  oStrategy = StrategyDAO.getStrategy(new Long(1));
    	
    	int j = 0;
   	 	double Lista = 0;
		}


}


