/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tim.dao;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import com.tim.util.LogTWM;
import com.tim.util.bbdd.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Priority;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.model.Market;
import com.tim.model.RealTime;
import com.tim.model.Share;
import com.tim.model.SimulationRealTime;

/**
 *
 * @author je10034
 */
public class RealTimeDAO {

    public RealTimeDAO() {
    }
    
    
    public static boolean updateRealTimeByField(int realtimeId, String Field, Double Value) 
    {
       
       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();               
               datos.add(Value);
               datos.add(realtimeId);
               
               qr.update("update realtime_shares set " +  Field + "=? where realtimeId=?", datos.toArray());
               //MyLog.log(Priority.SEVERE, "updateRealTimeByField:" + realtimeId + "." + Field );
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(RealTimeDAO.class);
           	   LogTWM.log(Priority.FATAL, "updateRealTimeByField:" + e.getMessage());
               return false;
              
           }
		return true;
    
       }
    
    public static boolean DeleteSimulationRealTime(Timestamp From, Timestamp To, Long ShareId) 
    {
       
       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();               
               datos.add(From);
               datos.add(To);
               datos.add(ShareId);
               
               qr.update("delete historical_realtime_shares  where date(dateadded)>=date(?) and" +
               		" date(dateadded)<=date(?)  and shareid=?", datos.toArray());
               //MyLog.log(Priority.SEVERE, "updateRealTimeByField:" + realtimeId + "." + Field );
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(RealTimeDAO.class);
           	   LogTWM.log(Priority.FATAL, "updateRealTimeByField:" + e.getMessage());
               return false;
              
           }
		return true;
    
       }
    
    
          
     public boolean addRealTime(int shareId, double value) 
     {
        
    	  addRealTime(shareId,value,null);
    	  return true;
    	 
     }
    
     /* forzar lecturas pasadas */
     public boolean addRealTime(int shareId, double value, Timestamp DateRealTime) 
     {
        
        try
            {
                QueryRunner qr=new QueryRunner();
                java.util.List datos = new java.util.ArrayList();               
                datos.add(shareId);
                datos.add(value);
                if (DateRealTime!=null)
                {
                	datos.add(DateRealTime);
                }
                
               // MyLog.log(Priority.SEVERE, "insert into realtime_shares:" );
                String _SQLInsert = "insert into realtime_shares(shareID,value";
                if (DateRealTime!=null)
                {
                	_SQLInsert += ",dateAdded) values (?,?,?)" ;
                }
                else
                	_SQLInsert += ") values (?,?)";
                
                
                
                qr.update(_SQLInsert, datos.toArray()); 
               
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(RealTimeDAO.class);
            	LogTWM.log(Priority.FATAL, "addRealTime:" + e.getMessage());
                return false;
               
            }
		return true;
     
        }
     
     
     /* forzar lecturas pasadas */
     public boolean addSimulationRealTime(int shareId, double value, Timestamp DateRealTime) 
     {
        
        try
            {
                QueryRunner qr=new QueryRunner();
                java.util.List datos = new java.util.ArrayList();               
                datos.add(shareId);
                datos.add(value);
                if (DateRealTime!=null)
                {
                	datos.add(DateRealTime);
                }
                
               // MyLog.log(Priority.SEVERE, "insert into realtime_shares:" );
                String _SQLInsert = "insert into historical_realtime_shares(shareID,value";
                if (DateRealTime!=null)
                {
                	_SQLInsert += ",dateAdded) values (?,?,?)" ;
                }
                else
                	_SQLInsert += ") values (?,?)";
                
                
                
                qr.update(_SQLInsert, datos.toArray()); 
               
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(RealTimeDAO.class);
            	LogTWM.log(Priority.FATAL, "addRealTime:" + e.getMessage());
                return false;
               
            }
		return true;
     
        }
     
     /* FORMATO HH:MM:SS */
     public static java.util.List getListActiveShareByMarket(Long marketId) 
     {
        java.util.List resultado = null;

        try
        {
                QueryRunner qr=new QueryRunner();
                BeanListHandler blh= new BeanListHandler(Share.class);
                String SQL = "select a.shareId,a.name, a.symbol, a.active,a.addedDate  from share a,  market_share b where a.active=1  "  +
                		" and a.shareId = b.shareId "; 
                resultado =( java.util.List) qr.query(SQL, blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(RealTimeDAO.class);
            	LogTWM.log(Priority.FATAL, "getListActiveShareByMarket:" + e.getMessage());
               
            }
            return resultado;
        }
     
     
     /* ESTO ES PARA ASEGURARNOS QUE PARA EL TRADING QUE TENEMOS, TENEMOS POR LO MENOS EL HISTORICO
      * DE AL MENOS LO QUE MARQUE EL PARAMETRO DE LA CONFIGURACION. 
      * AHORA, QUE PASA SI TENEMOS DE UN DIA Y DE OTRO NO???
      * LA UNICA MANERA DE QUE TIRE Y POR LO QUE SEA NO SE PRODUZCAN HUECOS.
      * 
      *   NO ME GUSTA ELIMINARLO Y CREARLO.MUCHAS FILAS.
      *   NO ME RESUELVE LOS POSIBLES HUECOS QUE HAYA PODIDO HABER POR COMUNICACIONES..ETC.
      */
     public static List<Share> getListActiveShareByMarket(Long shareId, Timestamp From, Timestamp To ) 
     {
        java.util.List<Share> resultado = null;

        try
        {
                QueryRunner qr=new QueryRunner();
                BeanListHandler blh= new BeanListHandler(Share.class);
                String SQL = "select a.shareId,a.name, a.symbol, a.active,a.addedDate  from share a,  market_share b where a.active=1  "  +
                		" and a.shareId = b.shareId "; 
                resultado =( java.util.List) qr.query(SQL, blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(RealTimeDAO.class);
            	LogTWM.log(Priority.FATAL,  "getListActiveShareByMarket:" + e.getMessage());
               
            }
            return resultado;
        }
     
     
     
     public static SimulationRealTime getSimulationRealTime(int ShareId, Double _FromValue, Double ToValue,
    		 Timestamp _oDateSimulation) 
     {
    	 SimulationRealTime resultado = null;

        try
        {
                QueryRunner qr=new QueryRunner();
                BeanHandler blh= new BeanHandler(SimulationRealTime.class);
                java.util.List datos = new java.util.ArrayList();
                datos.add(ShareId);
                datos.add(_FromValue);
                datos.add(ToValue);
                datos.add(_oDateSimulation);                
                String SQL = "select *  from historical_realtime_shares where shareId=? and value >? and value <? and " +
                		" date(dateadded) = date(?) group by shareId";
                
                resultado =( SimulationRealTime) qr.query(SQL,  datos.toArray(),blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(RealTimeDAO.class);
            	LogTWM.log(Priority.FATAL,  "getSimulationRealTime:" + e.getMessage());
               
            }
            return resultado;
        }
     
     
     
     public static RealTime getMinMaxRealTime(int ShareId) 
     {
        RealTime resultado = null;

        try
        {
                QueryRunner qr=new QueryRunner();
                BeanHandler blh= new BeanHandler(RealTime.class);
                java.util.List datos = new java.util.ArrayList();
                datos.add(ShareId);
                String SQL = "select min(value) min_value, max(value) max_value, shareId from realtime_shares where shareId=? group by shareId"; 
                resultado =( RealTime) qr.query(SQL,  datos.toArray(),blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(RealTimeDAO.class);
            	LogTWM.log(Priority.FATAL,  "getMinMaxRealTime:" + e.getMessage());
               
            }
            return resultado;
        }
     
     /* OBTIENE LOS DATOS DE LA MEDIA MOVIL SIMPLE PARA UNA ACCION, PERIODOS DE CALCULO Y BARRAS DE
      * TIEMPO  EN MINUTOS PARA EL PRECIO DE CIERRE DE LA VELA.
      *  LA FECHA FIN LA CALCULO SUMANDO LOS DATOS DE LA FECHA INICIO
      *  
      *  
      *  IMPORTANTE : ES NECESARIO QUE HAYA REALTIME_VALUE EN CADA MINUTO DE BARRA DE CADA PERIODO (SI NO, NO DEVUELVE NADA )
      * */
     
     private static RealTime AvgSimpleMobile(Timestamp _oDateFrom,  Timestamp _oDateTo, int ShareId, int Periods, int TimeBars, boolean SimulationMode)
     {
    	 RealTime _resultadoOK = null;
         RealTime _RETresultado= null;

         try
         {
                 QueryRunner qr=new QueryRunner();
                 BeanHandler blh= new BeanHandler(RealTime.class);
                 java.util.List datos = new java.util.ArrayList();
                
                 
                 datos.add(ShareId);
                 datos.add(TimeBars);
                 datos.add(_oDateFrom);
                 datos.add(_oDateTo);
                 datos.add(ShareId);
                 datos.add(TimeBars);
                
                 /* SACAMOS EL PRECIO DE CIERRE DE CADA BARRA Y EL NUMERO DE BARRAS OBTENIDAS
                  * PARA VERIFICAR.
                  *  avg(value) --> MEDIA EN LOS PERIODOS.
                  *  max(counter) --> NUMERO DE DISTINTOS PERIODOS ENCONTRADOS 
                  */
                 
                 String SQL ="select ";
        		 if (!SimulationMode)
                	 SQL += " avg(FN_GETLAST_REALTIMESHARE_FROMDATES ";
                 else
                	 SQL += " avg(FN_GETLAST_REALTIMESHARE_FROMDATES_SIMULATION ";		 
                 SQL +="(?,FECHA1,FECHA2)) value, max(counter) as realtimeID from ( ";
                 SQL +=" select  date_sub(T.FECHAVALOR, INTERVAL SECOND(T.FECHAVALOR) SECOND) FECHA1,";
                 SQL +=" date_sub(date_add(T.FECHAVALOR, INTERVAL ? MINUTE),INTERVAL SECOND(T.FECHAVALOR) SECOND) FECHA2, T.shareid , @rn:=@rn+1 AS counter "; 
                 SQL +=" from (";
                 SQL += " select minute(dateadded) MINUTOS, dateadded FECHAVALOR, shareid from ";
                 if (!SimulationMode)
                	 SQL += " realtime_shares ";
                 else
                	 SQL += " historical_realtime_shares ";
                 SQL += "where dateadded >=? and dateadded <?";
                 SQL += " and  shareid = ? group by 	minute(dateadded) ORDER BY dateadded) T, (SELECT @rn:=0) RANK2"; 
                 SQL += " where mod(T.MINUTOS, ?)=0 ) TFINAL ";
                 /* , realtime_shares T2 ";
                 SQL += " where T2.dateadded between TFINAL.FECHA1 AND TFINAL.FECHA2 and T2.shareid = TFINAL.shareid ";
                 SQL += " and T2.dateadded = (select max(F.dateadded) from realtime_shares F where F.dateadded>= TFINAL.FECHA1  and";
                 SQL += " F.dateadded< TFINAL.FECHA2 and F.shareid = TFINAL.shareid) ";*/ 
 		
                 
                /* System.out.print(SQL);
                 System.out.print(datos.toString());
               */
                 
                 SimpleDateFormat _sdf =new SimpleDateFormat("dd/MM/yyyy HH:mm");
                 
                 //System.out.println("Periodos:" + TimeBars + ",Accion:" + ShareId+ ",Inicio:" + _sdf.format(_oDateFrom) + "|" + _sdf.format(_oDateTo));
                 
                 _resultadoOK =( RealTime) qr.query(SQL,  datos.toArray(),blh);
 	            
 	            /* VERIFICAMOS QUE HAY BARRAS SUFICIENTES SEGUN EL PARAMETRO ENVIADO , RETORNAMOS LOS DATOS */
 	            if (_resultadoOK!=null && _resultadoOK.getRealtimeID().intValue()==Periods)
 	            	_RETresultado = _resultadoOK;
 	            
 	          
 	            	
                         
             }
             catch(Exception e)
             {                
             	LogTWM.getLog(RealTimeDAO.class);
             	LogTWM.log(Priority.FATAL,  "getAvgSimpleMobile:" + e.getMessage());
                
             }
         return _RETresultado;
     }
     
     public static RealTime getAvgSimpleMobileSimulated(Timestamp _oDateFrom,  Timestamp _oDateTo, int ShareId, int Periods, int TimeBars) 
     {
 		return AvgSimpleMobile( _oDateFrom,  _oDateTo, ShareId, Periods, TimeBars, true);

     }
     
     public static RealTime getAvgSimpleMobile(Timestamp _oDateFrom,  Timestamp _oDateTo, int ShareId, int Periods, int TimeBars, boolean Simulation) 
     {
       
            //return resultado;
		return AvgSimpleMobile(_oDateFrom,  _oDateTo, ShareId, Periods, TimeBars, Simulation);
      }
     
     //, Timestamp From, Timestamp To
     public static List<RealTime> getAllRealTime(int ShareId, Timestamp From, Timestamp To) 
     {
    	 java.util.List<RealTime> resultado = null;

        try
        {
                QueryRunner qr=new QueryRunner();
                BeanListHandler blh= new BeanListHandler(RealTime.class);
                java.util.List datos = new java.util.ArrayList();
                datos.add(ShareId);
                datos.add(From);
                datos.add(To); 
                String SQL = "select * from realtime_shares where shareId=? ";             		                
                SQL +=" and dateAdded>=? and dateAdded<=?";  
                
                /* DNM : 17-11-2012 .. NO ES NECESARIO PK METO EL GROUP BY*/ 
                //order by dateAdded desc limit 1 "; 
                resultado =(java.util.List) qr.query(SQL,  datos.toArray(),blh);
              //  LogTWM.log(Priority.INFO, "getRealTimeBetweenDates:" +  datos);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(RealTimeDAO.class);
            	LogTWM.log(Priority.FATAL, "getRealTimeBetweenDates:" + e.getMessage());
               
            }
            return resultado;
        }
  
     
     public static RealTime getFirstLastRealTimeTrading() 
     {
        RealTime resultado = null;

        try
        {
                QueryRunner qr=new QueryRunner();
                BeanHandler blh= new BeanHandler(RealTime.class);
                String SQL = "select min(dateadded) _FirstTradingDate , max(dateadded) _LastTradingDate  from " +
                		" realtime_shares where date(dateadded) = date(now())"; 
                resultado =( RealTime) qr.query(SQL, blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(RealTimeDAO.class);
            	LogTWM.log(Priority.FATAL,  "getFirstLastRealTimeTrading:" + e.getMessage());
               
            }
            return resultado;
        }
     public static RealTime getLastRealTime(int ShareId) 
     {
        RealTime resultado = null;

        try
        {
                QueryRunner qr=new QueryRunner();
                BeanHandler blh= new BeanHandler(RealTime.class);
                java.util.List datos = new java.util.ArrayList();
                datos.add(ShareId);
                String SQL = "select * from realtime_shares where shareId=? order by dateAdded desc limit 1 "; 
                resultado =( RealTime) qr.query(SQL,  datos.toArray(),blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(RealTimeDAO.class);
            	LogTWM.log(Priority.FATAL,  "getLastRealTime:" + e.getMessage());
               
            }
            return resultado;
        }
     
     public static RealTime getLastRealTimeLessThanDateSimulated(int ShareId, Timestamp DateTo) 
     {
    	  return LastRealTimeLessThanDate(ShareId, DateTo, true);
     }  
     
     public static RealTime getLastRealTimeLessThanDate(int ShareId, Timestamp DateTo) 
     {
    	  return LastRealTimeLessThanDate(ShareId, DateTo, false);
     }  
     
     private  static RealTime LastRealTimeLessThanDate(int ShareId, Timestamp DateTo, boolean Simulation) 
     {
        RealTime resultado = null;

        try
        {
                QueryRunner qr=new QueryRunner();
                BeanHandler blh= new BeanHandler(RealTime.class);
                java.util.List datos = new java.util.ArrayList();
                datos.add(ShareId);                
                datos.add(ShareId);
                datos.add(DateTo);
                String SQL = "select * from ";
                if 	(!Simulation)                 	
                	SQL +=" realtime_shares ";
                else
                	SQL +=" historical_realtime_shares ";
                
                SQL +=" where shareId=?";
                SQL +=" and dateadded = (select max(dateadded) from ";
        		if 	(!Simulation)                 	
                	SQL +=" realtime_shares treal";
                else
                	SQL +=" historical_realtime_shares treal";
        		
        		SQL +=" where treal.shareId=?  and treal.dateadded<?) ";
                		
                //	and dateadded<? order by dateAdded desc limit 1 "; 
                resultado =( RealTime) qr.query(SQL,  datos.toArray(),blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(RealTimeDAO.class);
            	LogTWM.log(Priority.FATAL,  "getLastRealTime:" + e.getMessage());
               
            }
            return resultado;
        }
  
  
     public  static RealTime getRealTimeBetweenDates(int ShareId, Timestamp From, Timestamp To)
     {
    	 return RealTimeBetweenDates( ShareId, From, To, false);
     }
     
     public static RealTime getRealTimeBetweenDatesSimulated(int ShareId, Timestamp From, Timestamp To)
     {
    	 return RealTimeBetweenDates(ShareId, From, To, true);
     }
  
     private static RealTime RealTimeBetweenDates(int ShareId, Timestamp From, Timestamp To, boolean Simulation) 
     {
        RealTime resultado = null;

        try
        {
                QueryRunner qr=new QueryRunner();
                BeanHandler blh= new BeanHandler(RealTime.class);
                java.util.List datos = new java.util.ArrayList();
                datos.add(ShareId);
                datos.add(From);
                datos.add(To);
                String SQL = "select min(value) min_value, max(value) max_value, shareId  from ";
                if (!Simulation)                
                	SQL += "realtime_shares";                
                else
                	SQL += "historical_realtime_shares";
                SQL += " where shareId=? and dateAdded>=? and dateAdded<=?  group by shareId ";
                
                /* DNM : 17-11-2012 .. NO ES NECESARIO PK METO EL GROUP BY*/ 
                //order by dateAdded desc limit 1 "; 
                resultado =( RealTime) qr.query(SQL,  datos.toArray(),blh);
             //   LogTWM.log(Priority.INFO, "getRealTimeBetweenDates:" +  SQL);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(RealTimeDAO.class);
            	LogTWM.log(Priority.FATAL, "getRealTimeBetweenDates:" + e.getMessage());
               
            }
            return resultado;
        }
  
     
     public static Share getMinMaxRealTimeBetweenHours(int ShareId, java.sql.Timestamp From, java.sql.Timestamp To) 
     {
        Share resultado = null;

        try
        {
                QueryRunner qr=new QueryRunner();
                BeanHandler blh= new BeanHandler(Share.class);
                java.util.List datos = new java.util.ArrayList();
                datos.add(ShareId);
                String SQL = "select min(value) min_value, max(value) max_value, shareId from realtime_shares where shareId=?";
                SQL += " and dateAdded<=? and dateAdded>=?";
                resultado =( Share) qr.query(SQL,  datos.toArray(),blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(RealTimeDAO.class);
            	LogTWM.log(Priority.FATAL, "getMinMaxRealTimeBetweenHours:" + e.getMessage());
               
            }
            return resultado;
        }
     
     public static RealTime getMinMaxValue_DatesRealTimeBetweenDates(int ShareId, java.sql.Timestamp From, java.sql.Timestamp To) 
     {
    	 RealTime resultado = null;

        try
        {
                QueryRunner qr=new QueryRunner();
                BeanHandler blh= new BeanHandler(RealTime.class);
                java.util.List datos = new java.util.ArrayList();
                
                datos.add(ShareId);
                datos.add(From);
                datos.add(To);
                datos.add(ShareId);
                datos.add(From);
                datos.add(To);
                datos.add(ShareId);
                datos.add(From);
                datos.add(To);
                String SQL = "select min_value, FN_GETDATE_REALTIMESHARE_FROM_VALUE(?,?,min_value,?)  _FirstTradingDate,";
                SQL += " max_value, FN_GETDATE_REALTIMESHARE_FROM_VALUE(?,?,max_value,?)  _LastTradingDate ";
                SQL += "  FROM (" ;
                SQL += " select min(value) min_value ,max(value) max_value " ; 
                SQL += " from realtime_shares " ;
                SQL += " where shareid=? ";
                SQL += " and dateadded>=? and dateadded<=?) AS FFF";
                
                
               /* String SQL2 = "select min(value) min_value, max(value) max_value, shareId from realtime_shares where shareId=?";
                SQL += " and dateAdded<=? and dateAdded>=?"; 
                LogTWM.getLog(RealTimeDAO.class);
            	LogTWM.log(Priority.FATAL, SQL);
            	LogTWM.log(Priority.FATAL, datos);*/ 
                resultado =( RealTime) qr.query(SQL,  datos.toArray(),blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(RealTimeDAO.class);
            	LogTWM.log(Priority.FATAL, "getMinMaxValue_DatesRealTimeBetweenDates:" + e.getMessage());
               
            }
            return resultado;
        }
     
        

}


