/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tim.dao;

import com.tim.util.LogTWM;
import com.tim.util.bbdd.QueryRunner;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Priority;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.model.Share_Strategy;
import com.tim.model.Trading;
import com.tim.model.Trading_Market;

/**
 *
 * @author je10034
 */
public class Share_StrategyDAO {


	
    public Share_StrategyDAO() {
    }
    
    public static int deleteStrategiesFromShare(int ShareId) 
    {
       int resultado;

       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();             
               datos.add(ShareId);               

               qr.update("delete from trading_share_strategy where shareID=?" , datos.toArray());
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(Share_StrategyDAO.class);
           	   LogTWM.log(Priority.FATAL, "deleteStrategiesFromShare:" + e.getMessage());
           	   return 0;
              
           }
           return 1;
       }
   
    
   
    
    public static int insertStrategiesToShare(int ShareId,String _StrategiesId) 
    {
       int resultado;

       String[] aStrategiesId = _StrategiesId.split(","); 
       try
           {
               QueryRunner qr=new QueryRunner();
                              
               
               for (int j=0;j<aStrategiesId.length;j++)
               {
            	   java.util.List datos = new java.util.ArrayList();               
                   datos.add(ShareId);
                   datos.add(aStrategiesId[j]);
                   
            	   qr.update("insert into trading_share_strategy(shareid,strategyid) values(?,?)" , datos.toArray());
               }
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(Share_StrategyDAO.class);
           	   LogTWM.log(Priority.FATAL, "updateTradingShare:" + e.getMessage());
           	   return 0;
              
           }
           return 1;
       }
    
    
        
   
    public  static java.util.List<Share_Strategy> getListStrategiesByShare(Long ShareId, boolean FieldIdOnly)
    {
       
    java.util.List<Share_Strategy> oShStrat = null;
    try
        {
            QueryRunner qr=new QueryRunner();
            BeanListHandler blh= new BeanListHandler(Share_Strategy.class);
            java.util.List datos = new java.util.ArrayList();
            datos.add(ShareId);
            String _ListFields = "*";
            if (FieldIdOnly) 
            {
            	_ListFields = "strategyId";
            }
            
            oShStrat =(java.util.List) qr.query("select  " + _ListFields + " from trading_share_strategy  where shareId=?",  datos.toArray(),blh);              
                     
        }
        catch(Exception e)
        {                
     	   LogTWM.getLog(Share_StrategyDAO.class);
        	   LogTWM.log(Priority.FATAL, "getListStrategiesByShare:" + e.getMessage());
           
        }
        return oShStrat;
        }
    public static java.util.List<Share_Strategy> getListStrategiesIDByShare(Long ShareId) 
    {
    	return 	getListStrategiesByShare(ShareId, true);
    }    
    


    
  
        

}


