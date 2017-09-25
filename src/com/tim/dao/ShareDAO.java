/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tim.dao;

import com.tim.util.LogTWM;
import com.tim.util.bbdd.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Priority;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.model.Market;
import com.tim.model.Position;
import com.tim.model.Share;

/**
 *
 * @author je10034
 */
public class ShareDAO {

    public ShareDAO() {
    }


     public java.util.List getListAllShare() 
     {
        java.util.List resultado = null;

        try
            {
                QueryRunner qr=new QueryRunner();
                BeanListHandler blh= new BeanListHandler(Market.class);
                resultado =( java.util.List) qr.query("select * from share  ", blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(ShareDAO.class);
            	LogTWM.log(Priority.FATAL, "getListAllShare:" + e.getMessage());
               
            }
            return resultado;
        }    
     
     public java.util.List getListActiveShare() 
     {
        java.util.List resultado = null;

        try
            {
                QueryRunner qr=new QueryRunner();
                BeanListHandler blh= new BeanListHandler(Market.class);
                resultado =( java.util.List) qr.query("select * from share where active=1", blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(ShareDAO.class);
            	LogTWM.log(Priority.FATAL, "getListActiveShare:" + e.getMessage());
               
            }
            return resultado;
        }
     
     
     /* ACCIONES ACTIVAR POR MERCADO Y NO VERIFICADAS A NIVEL DE CONTRATO CONTRA LA TWS. */
     
     public static java.util.List getListActiveShareByMarketNotVerified(Long marketId) 
     {
        java.util.List resultado = null;

        try
        {
                QueryRunner qr=new QueryRunner();
                BeanListHandler blh= new BeanListHandler(Share.class);
                String SQL = "select a.*  from share a,  market_share b where a.active=1  "  +
                		" and a.shareId = b.shareId and b.marketId=" + marketId.intValue()  + " and  " +
                		" (date_contract_verified  is null or date(date_contract_verified) <> date(now())) order by a.name"; 
                resultado =( java.util.List) qr.query(SQL, blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(ShareDAO.class);
            	LogTWM.log(Priority.FATAL, "getListActiveShareByMarket:" + e.getMessage());
               
            }
            return resultado;
        }
     
     /* FORMATO HH:MM:SS */
     
     public static java.util.List getListActiveShareByMarket(Long marketId) 
     {
        java.util.List resultado = null;

        try
        {
                QueryRunner qr=new QueryRunner();
                BeanListHandler blh= new BeanListHandler(Share.class);
                String SQL = "select a.*  from share a,  market_share b where a.active=1  "  +
                		" and a.shareId = b.shareId and b.marketId=" + marketId.intValue()  + " order by a.name"; 
                resultado =( java.util.List) qr.query(SQL, blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(ShareDAO.class);
            	LogTWM.log(Priority.FATAL, "getListActiveShareByMarket:" + e.getMessage());
               
            }
            return resultado;
        }
     public static java.util.List getListShareByMarket(Long marketId) 
     {
        java.util.List resultado = null;

        try 
        {
                QueryRunner qr=new QueryRunner();
                BeanListHandler blh= new BeanListHandler(Share.class);
                String SQL = "select a.*, FN_IS_IN_TRADING_TODAY(a.shareid) as active_trading_today  from share a,  market_share b where   "  +
                		" a.shareId = b.shareId and b.marketId=" + marketId.intValue()  + " order by a.security_type desc, a.name"; /* PRIMERO STOCK, DESPUES FUT */ 
                resultado =( java.util.List) qr.query(SQL, blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(ShareDAO.class);
            	LogTWM.log(Priority.FATAL, "getListActiveShareByMarket:" + e.getMessage());
               
            }
            return resultado;
        }
     
     /* LISTADO DE ACCIONES CON HISTORICAL DATA A CALCULAR */
     public static java.util.List<Share> getListActiveHistData() 
     {
        
    	java.util.List resultado = null;

        try 
        {
                QueryRunner qr=new QueryRunner();
                BeanListHandler blh= new BeanListHandler(Share.class);
                String SQL = "select * from share where historical_data=1";  
                resultado =( java.util.List) qr.query(SQL, blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(ShareDAO.class);
            	LogTWM.log(Priority.FATAL, "getListActiveHistData:" + e.getMessage());
               
            }
            return resultado;
        }
     
     public static Market getMarketFromShare(Share oShare) 
     {
    	 Market resultado = null;

        try
        {
                QueryRunner qr=new QueryRunner();
                BeanHandler blh= new BeanHandler(Market.class);
                String SQL = "select a.*  from  market_share b, market a where   "  +
                		" a.marketId = b.marketId and b.shareId=" + oShare.getShareId(); 
                resultado =( Market) qr.query(SQL, blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(ShareDAO.class);
            	LogTWM.log(Priority.FATAL, "getMarketFromShare:" + e.getMessage());
               
            }
            return resultado;
        }
     
 /* FORMATO HH:MM:SS */
     
     public static Share getShare(Long shareId) 
     {
    	 Share resultado = null;

        try
        {
                QueryRunner qr=new QueryRunner();
                BeanHandler blh= new BeanHandler(Share.class);
                String SQL = "select *  from share a where a.shareID="  + shareId.intValue(); 
                 
                resultado =( Share) qr.query(SQL, blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(ShareDAO.class);
            	LogTWM.log(Priority.FATAL, "getShare:" + e.getMessage());
               
            }
            return resultado;
        }
     
     public static Share getShare(String Symbol) 
     {
    	 Share resultado = null;

        try
        {
                QueryRunner qr=new QueryRunner();
                BeanHandler blh= new BeanHandler(Share.class);
                String SQL = "select *  from share a where a.symbol='"  + Symbol.toUpperCase() + "'"; 
                 
                resultado =( Share) qr.query(SQL, blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(ShareDAO.class);
            	LogTWM.log(Priority.FATAL, "getShare:" + e.getMessage());
               
            }
            return resultado;
        }
     
     public static Integer getLastShare() 
     {
    	 Integer resultado = null;

        try
        {
        		ScalarHandler scl = new ScalarHandler( 1 );
         	    //resultado = new Long(0);                                      
                QueryRunner qr=new QueryRunner();                
                String SQL = "select max(shareid) shareid from share "; 
                 
                resultado =( Integer) qr.query(SQL, scl);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(ShareDAO.class);
            	LogTWM.log(Priority.FATAL, "getShare:" + e.getMessage());
               
            }
            return resultado;
        }
     
     
     
     public static boolean insertShare(Share oPosicion) 
     {
        boolean resultadoOK = true;

        try
            {
        		QueryRunner qr=new QueryRunner();
        		String SqlPosition= "insert into share (";        	
        		SqlPosition += "name,";
        		SqlPosition += "symbol,";        	
        		SqlPosition += "active,";
        		SqlPosition += "active_trading,";
        		SqlPosition += "percentual_value_gap,";
        		SqlPosition += "number_purchase,";
        		SqlPosition += "percentual_limit_buy,";
        		SqlPosition += "sell_percentual_stop_lost,";        		
        		SqlPosition += "sell_percentual_stop_profit,";
        		SqlPosition += "offset1min_read_from_initmarket,";
        		SqlPosition += "offset2min_read_from_initmarket,";        		
        		SqlPosition += "sell_percentual_stop_profit_position,";        		        		        
        		SqlPosition += "tick_futures,";
        		SqlPosition += "multiplier,";
        		SqlPosition += "expiry_date,";
        		SqlPosition += "security_type,";
        		SqlPosition += "exchange,";        		
        		SqlPosition += "expiry_expression,";
        		SqlPosition += "primary_exchange,";
        		SqlPosition += "historical_data";        		
        		SqlPosition += ") values (";
        		SqlPosition += "##name##,";
        		SqlPosition += "##symbol##,";
        		SqlPosition += "##active##,";
        		SqlPosition += "##active_trading##,";
        		SqlPosition += "##percentual_value_gap##,";
        		SqlPosition += "##number_purchase##,";
        		SqlPosition += "##percentual_limit_buy##,";
        		SqlPosition += "##sell_percentual_stop_lost##,";
        		SqlPosition += "##sell_percentual_stop_profit##,";
        		SqlPosition += "##offset1min_read_from_initmarket##,";
        		SqlPosition += "##offset2min_read_from_initmarket##,";
        		SqlPosition += "##sell_percentual_stop_profit_position##,";
        		SqlPosition += "##tick_futures##,";        		
        		SqlPosition += "##multiplier##,";
        		SqlPosition += "##expiry_date##,";
        		SqlPosition += "##security_type##,";
        		SqlPosition += "##exchange##,";
        		SqlPosition += "##expiry_expression##,";        		
        		SqlPosition += "##primary_exchange##,";
        		SqlPosition += "##historical_data##)";
        		
        		qr.updateBean(SqlPosition,oPosicion);
        		                
                        
            }
            catch(Exception e)
            {          
            	resultadoOK = false;
            	 LogTWM.getLog(PositionDAO.class);
               	LogTWM.log(Priority.FATAL, "addPosition:" + e.getMessage());
               
            }
            return resultadoOK;
        } 
     
     public static boolean updateMarketShare(Long ShareId, Long MarketId) 
     {
        boolean resultadoOK = true;

        try
            {
        		QueryRunner qr=new QueryRunner();
        		String SqlPosition= "update market_share ";       		
           		SqlPosition +=  "set marketID=" + MarketId ;
           		SqlPosition += " where shareId=" + ShareId;
           		
           		qr.update(SqlPosition);
                            
             }
        catch(Exception e)
        {                
        	LogTWM.getLog(ShareDAO.class);
        	LogTWM.log(Priority.FATAL, "updateMarketShare:" + e.getMessage());
           
        }
        return resultadoOK;
      }
      
     public static boolean updateMarketTradingAllShares(Long MarketId, Integer TradingYesNo) 
     {
        boolean resultadoOK = true;

        try
            {
        		QueryRunner qr=new QueryRunner();
        		String SqlPosition= "update share ";       		
           		SqlPosition +=  "set active_trading=" + TradingYesNo ;
           		SqlPosition += " where shareId in (select shareid from trading_market where marketID=" + MarketId  + ")";
           		
           		LogTWM.getLog(ShareDAO.class);
            	LogTWM.log(Priority.FATAL, SqlPosition); 
           		
           		qr.update(SqlPosition);
                            
             }
        catch(Exception e)
        {                
        	LogTWM.getLog(ShareDAO.class);
        	LogTWM.log(Priority.FATAL, "updateMarketTradingAllShares:" + e.getMessage());
           
        }
        return resultadoOK;
      }
     

     public static boolean insertMarketShare(Integer ShareId, Long MarketId) 
     {
        boolean resultadoOK = true;

        try
            {
        		QueryRunner qr=new QueryRunner();
        		String SqlPosition= "insert into market_share (shareid, marketid)";        	
        		SqlPosition += " values (" + ShareId.longValue() + "," + MarketId.longValue() + ")";
        		
        		
        		qr.update(SqlPosition);
        		             
        	/* 	 LogTWM.getLog(ShareDAO.class);
                	LogTWM.log(Priority.FATAL, SqlPosition); */
                        
            }
            catch(Exception e)
            {          
            	resultadoOK = false;
            	 LogTWM.getLog(ShareDAO.class);
               	LogTWM.log(Priority.FATAL, "insertMarketShare:" + e.getMessage());
               
            }
            return resultadoOK;
        }
     
     public static Share updateShare(Share oShare) 
     {
    	 Share resultado = null;

        try
        {
        	QueryRunner qr=new QueryRunner();
        	
       		
       		String SqlPosition= "update share ";       		
       		SqlPosition +=  "set shareID=##shareID##," ;
       		SqlPosition += "name=##name##,";
       		SqlPosition += "symbol=##symbol##,";       	
       		SqlPosition += "active=##active##,";
       		SqlPosition += "active_trading=##active_trading##,";
       		SqlPosition += "percentual_value_gap=##percentual_value_gap##,";
       		SqlPosition += "offset1min_read_from_initmarket=##offset1min_read_from_initmarket##,";
       		SqlPosition += "offset2min_read_from_initmarket=##offset2min_read_from_initmarket##,";
       		SqlPosition += "sell_percentual_stop_lost=##sell_percentual_stop_lost##,";
       		SqlPosition += "sell_percentual_stop_profit=##sell_percentual_stop_profit##,";
       		SqlPosition += "number_purchase=##number_purchase##,";
       		SqlPosition += "percentual_limit_buy=##percentual_limit_buy##,";       		
       		SqlPosition += "sell_percentual_stop_profit_position=##sell_percentual_stop_profit_position##,";       		
       		SqlPosition += "tick_futures=##tick_futures##,";
       		SqlPosition += "multiplier=##multiplier##,";
       		SqlPosition += "last_error_data_read=##last_error_data_read##,";		
       		SqlPosition += "last_error_data_trade=##last_error_data_trade##,";
       		SqlPosition += "multiplier=##multiplier##,";
       		SqlPosition += "security_type=##security_type##,";
       		SqlPosition += "expiry_date=##expiry_date##,";       		
       		SqlPosition += "exchange=##exchange##,";
       		SqlPosition += "date_contract_verified=##date_contract_verified##,";
       		SqlPosition += "expiry_expression=##expiry_expression##,";
       		SqlPosition += "primary_exchange=##primary_exchange##,";
       		SqlPosition += "historical_data=##historical_data##";       		
       		
       		SqlPosition += " where shareId=" + oShare.getShareId();
       		
       	/* 	LogTWM.getLog(ShareDAO.class);
       		LogTWM.log(Priority.FATAL, SqlPosition); */
       		
       		qr.updateBean(SqlPosition,oShare);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(ShareDAO.class);
            	LogTWM.log(Priority.FATAL, "updateShare:" + e.getMessage());
               
            }
            return resultado;
        }
        

}


