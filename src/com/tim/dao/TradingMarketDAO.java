

package com.tim.dao;

import com.tim.util.LogTWM;
import com.tim.util.bbdd.QueryRunner;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Priority;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.model.Trading;
import com.tim.model.Trading_Market;

/**
 *
 * @author je10034
 */
public class TradingMarketDAO {
	
	

    public TradingMarketDAO() {
    }
    
    public static int deleteTradingShare(int ShareId,int MarketId) 
    {
       int resultado;

       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();
               datos.add(MarketId);
               datos.add(ShareId);               

               qr.update("delete from trading_market where marketID=? and shareID=?" , datos.toArray());
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(TradingMarketDAO.class);
           	   LogTWM.log(Priority.FATAL, "deleteTradingShare:" + e.getMessage());
           	   return 0;
              
           }
           return 1;
       }
   
    
    public static int deleteTradingMarket(int MarketId) 
    {
       int resultado;

       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();
               datos.add(MarketId);
                              

               qr.update("delete from trading_market where marketID=?" , datos.toArray());
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(TradingMarketDAO.class);
           	   LogTWM.log(Priority.FATAL, "deleteTradingShare:" + e.getMessage());
           	   return 0;
              
           }
           return 1;
       }
    
    public static int updateTradingShare(int ShareId,int MarketId) 
    {
       int resultado;

       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();
               datos.add(MarketId);
               datos.add(ShareId);               

               qr.update("update trading_market set marketid=? where shareID=?" , datos.toArray());
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(TradingMarketDAO.class);
           	   LogTWM.log(Priority.FATAL, "updateTradingShare:" + e.getMessage());
           	   return 0;
              
           }
           return 1;
       }
    
    
    
    
    public static int addTradingShare(int ShareId,int MarketId) throws Exception 
    {
       int resultado;

       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();
               Long _TradingID  = new Long(1);
               
               Trading oTradingLast = getListActiveAllTradingToday();
               
               if (oTradingLast!=null)
               {
            	   _TradingID  = oTradingLast.getTradingID();
            	   
               }
               else
               {
            	   throw new Exception("Error. No se encuentra el ID para el Trading de hoy");
            	   
               }
               
               datos.add(_TradingID);
               datos.add(MarketId);
               datos.add(ShareId);               
               
               
               qr.update("insert into trading_market (tradingID, marketID, shareID) values (?,?,?)" , datos.toArray());
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(TradingMarketDAO.class);
           	   LogTWM.log(Priority.FATAL, "addTradingShare:" + e.getMessage());
           	   throw new Exception("Error. No se encuentra el ID para el Trading de hoy");
           	   
              
           }
           return 1;
       }
    
    
    @SuppressWarnings("rawtypes")
	public static java.util.List getListMarketShares(int tradingID) 
    {
       java.util.List resultado = null;

       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();               
               datos.add(tradingID);               
               BeanListHandler blh= new BeanListHandler(Trading_Market.class);
               resultado =(java.util.List) qr.query("select a.* from trading_market a,  trading b, share c where a.tradingID = B.tradingID and a.tradingID=? and c.shareid=a.shareid ", datos.toArray(),blh);
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(TradingMarketDAO.class);
           	   LogTWM.log(Priority.FATAL, "getListMarketShares:" + e.getMessage());
              
           }
           return resultado;
       }   
    
    public static java.util.List getListActiveTradingMarketShares(int tradingID) 
    {
       java.util.List resultado = null;

       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();               
               datos.add(tradingID);               
               BeanListHandler blh= new BeanListHandler(Trading_Market.class);
               resultado =(java.util.List) qr.query("select a.* from trading_market a,  trading b, share c where a.tradingID = B.tradingID and a.tradingID=? and c.shareid=a.shareid and c.active_trading=1", datos.toArray(),blh);
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(TradingMarketDAO.class);
           	   LogTWM.log(Priority.FATAL, "getListActiveTradingMarketShares:" + e.getMessage());
              
           }
           return resultado;
       }    
    /* NORMALMENTE UNO */
    public static Trading getListActiveAllTradingToday() 
    {
       Trading oTrading=null ; 

       try
           {
               QueryRunner qr=new QueryRunner();
               BeanHandler blh= new BeanHandler(Trading.class);
               oTrading =( Trading) qr.query("select * from trading where date(date_trading) = DATE(now())", blh);
               //oTrading =( Trading) qr.query("select tradingID,name,active,date_trading,description,realtime_reading,offset_time_toactive from trading where date(date_trading) = DATE(now())", blh);
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(TradingMarketDAO.class);
           	   LogTWM.log(Priority.FATAL, "getListActiveAllTradingToday:" + e.getMessage());
              
           }
           return oTrading;
       }    
    

    public static boolean IsShareIntrading(int ShareID) 
    {
       java.util.List resultado = null;

       try
           {
               QueryRunner qr=new QueryRunner();
               BeanListHandler blh= new BeanListHandler(Trading.class);
               resultado =( java.util.List) qr.query("select * from trading_market a, trading b  where a.tradingID= a.tradingID and b.active=1 and a.shareId=" +ShareID , blh);
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(TradingMarketDAO.class);
           	   LogTWM.log(Priority.FATAL, "IsShareIntrading:" + e.getMessage());
              
           }
           return (resultado!=null);
       }
    
    public static void  StartTradingSchedulerMYSQL() 
    {

       try
           {
               QueryRunner qr=new QueryRunner();               
               qr.update("SET GLOBAL event_scheduler=on;");
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(TradingMarketDAO.class);
           	   //LogTWM.log(, "StartTradingSchedulerMYSQL:" + e.getMessage());
           	 //  LogTWM.
              
           }          
       }
    
    
    
  
        

}


