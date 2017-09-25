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
import com.tim.model.Market;
import com.tim.model.Share;

/**
 *
 * @author je10034
 */
public class MarketDAO {
	
    public MarketDAO() {
    	
    }


     public static java.util.List getListAllMarket() 
     {
        java.util.List resultado = null;

        try
            {
                QueryRunner qr=new QueryRunner();
                
                BeanListHandler blh= new BeanListHandler(Market.class);
                resultado =( java.util.List) qr.query("select * from market  ", blh);
                        
            }
            catch(Exception e)
            {     
            	LogTWM.getLog(MarketDAO.class);
            	LogTWM.log(Priority.FATAL, "getListAllMarket:" + e.getMessage());
               
            }
            return resultado;
        }    
     
     
     public static void deleteMarket(Long MarketID) 
     {
    	 try
         {
         	QueryRunner qr=new QueryRunner();
        		
        		String SqlPosition= "delete from market ";       		
        		SqlPosition += " where marketId=" + MarketID;
        		
        		qr.update(SqlPosition); 
                         
             }
             catch(Exception e)
             {                
             	LogTWM.getLog(MarketDAO.class);
             	LogTWM.log(Priority.FATAL, "updateMarket:" + e.getMessage());
                
             }
        }    
     
     
     public static Market getMarket(Long MarketID) 
     {
    	 Market resultado = null;

        try
            {
        		java.util.List datos = new java.util.ArrayList();               
        		datos.add(MarketID);
                QueryRunner qr=new QueryRunner();
                BeanHandler blh= new BeanHandler(Market.class);
                resultado =( Market) 
                qr.query("select * from market where marketId=?", datos.toArray(), blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(MarketDAO.class);
            	LogTWM.log(Priority.FATAL, "getMarket:" + e.getMessage());
               
            }
            return resultado;
        }    
     
     public static Market getMarketByName(String _Market) 
     {
    	 Market resultado = null;

        try
            {
        		java.util.List datos = new java.util.ArrayList();               
        		datos.add(_Market.toUpperCase().trim());
                QueryRunner qr=new QueryRunner();
                BeanHandler blh= new BeanHandler(Market.class);
                resultado =( Market) 
                qr.query("select * from market where name=?", datos.toArray(), blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(MarketDAO.class);
            	LogTWM.log(Priority.FATAL, "getMarketByName:" + e.getMessage());
               
            }
            return resultado;
        }    
     
     public static java.util.List getListActiveMarket() 
     {
        java.util.List resultado = null;

        try
            {
                QueryRunner qr=new QueryRunner();
                BeanListHandler blh= new BeanListHandler(Market.class);
                resultado =( java.util.List) qr.query("select * from market where active=1", blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(MarketDAO.class);
            	LogTWM.log(Priority.FATAL, "getListActiveMarket:" + e.getMessage());
               
            }
            return resultado;
        }
     
     
     /* FORMATO HH:MM:SS */
     
     public static java.util.List getListActiveMarketBtHours(String Hour, Integer Reading, Integer Trading) 
     {
        java.util.List resultado = null;

        try
            {	
                QueryRunner qr=new QueryRunner();
                java.util.List datos = new java.util.ArrayList();
                String Sql = "select * from market where active=?";
                datos.add(1);
                if (Reading!=null)                	
                {	
                	datos.add(Reading);
                	Sql += " and reading=?";
                }	
                if (Trading!=null)         
                {	
                	datos.add(Trading);
                	Sql += " and trading=?";
                }	
                Sql += " and start_hour<=" + Hour + " and end_hour>=" + Hour;
                BeanListHandler blh= new BeanListHandler(Market.class);                               
                resultado =( java.util.List) qr.query(Sql,datos.toArray(), blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(MarketDAO.class);
            	LogTWM.log(Priority.FATAL, "getListActiveMarketBtHours:" + e.getMessage());
               
            }
            return resultado;
        }
  
     public static void updateMarket(Market oMarket) 
     {
    	 
        try
        {
        	QueryRunner qr=new QueryRunner();
       		
       		String SqlPosition= "update market ";       		
       		SqlPosition +=  "set marketID=##marketId##," ;
       		SqlPosition += "name=##name##,";
       		SqlPosition += "identifier=##identifier##,";
       		SqlPosition += "active=##active##,";
       		SqlPosition += "trading=##trading##,";
       		SqlPosition += "reading=##reading##,";
       		/* SqlPosition += "security_type=##security_type##,";*/
       		SqlPosition += "currency=##currency##,";
       		/* SqlPosition += "exchange=##exchange##,";
       		SqlPosition += "primary_exchange=##primary_exchange##,"; */
       		SqlPosition += "start_hour=##start_hour##,";
       		SqlPosition += "end_hour=##end_hour##";
       		
       		
       		SqlPosition += " where marketId=" + oMarket.getMarketID();
       		
       		qr.updateBean(SqlPosition,oMarket); 
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(MarketDAO.class);
            	LogTWM.log(Priority.FATAL, "updateMarket:" + e.getMessage());
               
            }
         
        }
     public static void updateMarket2(Market oMarket) 
     {
    	 
        try
        {
        	QueryRunner qr=new QueryRunner();
       		
       		String SqlPosition= "update market ";       		
       		SqlPosition +=  "set marketID=##marketId##," ;
       		SqlPosition += "name=##name##,";
       		SqlPosition += "identifier=##identifier##,";
       		SqlPosition += "active=##active##,";
       		SqlPosition += "trading=##trading##,";
       		SqlPosition += "reading=##reading##,";
       		/* SqlPosition += "security_type=##security_type##,"; */
       		SqlPosition += "currency=##currency##,";
       		/* SqlPosition += "exchange=##exchange##,";
       		SqlPosition += "primary_exchange=##primary_exchange##,"; */
       		SqlPosition += "start_hour=##start_hour##,";
       		SqlPosition += "end_hour=##end_hour##";
       		
       		
       		SqlPosition += " where marketId=" + oMarket.getMarketID();
       		
       		qr.updateBean(SqlPosition,oMarket); 
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(MarketDAO.class);
            	LogTWM.log(Priority.FATAL, "updateMarket:" + e.getMessage());
               
            }
         
        }
        
     
     public static boolean insertMarket(Market oMarket) 
     {
        boolean resultadoOK = true;

        try
            {
        		QueryRunner qr=new QueryRunner();
        		String SqlPosition= "insert into market (";        	
        		SqlPosition += "name,";
        		SqlPosition += "identifier,";        	
        		SqlPosition += "active,";
        		SqlPosition += "trading,";
        		SqlPosition += "reading,";        		
        		SqlPosition += "currency,";        		
        		SqlPosition += "start_hour,";
        		SqlPosition += "end_hour";        		
        		SqlPosition += ") values (";
        		SqlPosition += "##name##,";
        		SqlPosition += "##identifier##,";
        		SqlPosition += "##active##,";
        		SqlPosition += "##trading##,";
        		SqlPosition += "##reading##,";
        		SqlPosition += "##currency##,";
        		SqlPosition += "##start_hour##,";
        		SqlPosition += "##end_hour##)";
        		
        		qr.updateBean(SqlPosition,oMarket);
        		                
                        
            }
            catch(Exception e)
            {          
            	resultadoOK = false;
            	 LogTWM.getLog(PositionDAO.class);
               	LogTWM.log(Priority.FATAL, "addPosition:" + e.getMessage());
               
            }
            return resultadoOK;
        } 
        

}


