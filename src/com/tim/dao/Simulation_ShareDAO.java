/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tim.dao;

import java.sql.Timestamp;

import com.tim.util.LogTWM;
import com.tim.util.bbdd.QueryRunner;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Priority;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.model.Configuration;
import com.tim.model.Hist_Data_Share_Status;
import com.tim.model.Simulation_Share;

/**
 *
 * @author je10034
 */
public class Simulation_ShareDAO {

    public Simulation_ShareDAO() {
    }

    public static java.util.List<Simulation_Share> getSimulation_Trades(Long SimulationID) 
    {
    	java.util.List<Simulation_Share> resultado = null;

       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();               
               datos.add(SimulationID);               
               BeanListHandler blh= new BeanListHandler(Simulation_Share.class);
               resultado =(java.util.List<Simulation_Share>) qr.query("select * from simulation_share a where simulationID=?", datos.toArray(),blh);                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(Simulation_Share.class);
           	   LogTWM.log(Priority.FATAL, "getSimulation_Trades:[" +  SimulationID + "]" + e.getMessage());
              
           }
           return resultado;
       }
  
    public static Simulation_Share getSimulation_TradesByShare(Long shareID) 
    {
    	Simulation_Share resultado = null;

       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();               
               datos.add(shareID);               
               BeanHandler blh= new BeanHandler(Simulation_Share.class);
               resultado =(Simulation_Share) qr.query("select * from simulation_share a where shareID=?", datos.toArray(),blh);                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(Simulation_Share.class);
           	   LogTWM.log(Priority.FATAL, "getSimulation_TradesByShare:[" +  shareID + "]" + e.getMessage());
              
           }
           return resultado;
       }
    public static Simulation_Share getSimulation_TradesByShare(Long SimulationID, Long ShareId) 
    {
    	Simulation_Share resultado = null;

       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();               
               datos.add(SimulationID);               
               BeanHandler blh= new BeanHandler(Simulation_Share.class);
               resultado =(Simulation_Share) qr.query("select * from simulation_share a where simulationID=?", datos.toArray(),blh);                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(Simulation_Share.class);
           	   LogTWM.log(Priority.FATAL, "getSimulation_Trades:[" +  SimulationID + "]" + e.getMessage());
              
           }
           return resultado;
       }    
    public static int updateProccesedDateSimulation(int ShareId, Timestamp ProcessedDate, Timestamp  nextExecution) 
    	{
       int resultado;

       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();
               datos.add(ProcessedDate);                    
               datos.add(nextExecution);               
               datos.add(ShareId);
                   

               qr.update("update simulation_share set processedDate=?, nextExecution=? where shareid = ?" , datos.toArray());
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(TradingMarketDAO.class);
           	   LogTWM.log(Priority.FATAL, "updateProccesedDateSimulation:" + e.getMessage());
           	   return 0;
              
           }
           return 1;
       }
    

    /* forzar lecturas pasadas */
    public static boolean addSimulationShareInfo(int shareId) 
    {
       
       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();               
               datos.add(shareId);
               
              // MyLog.log(Priority.SEVERE, "insert into realtime_shares:" );
               String _SQLInsert = "insert into simulation_share(shareID)";               
               	_SQLInsert +=  " values (?)";
               
               qr.update(_SQLInsert, datos.toArray()); 
              
                       
           }
           catch(Exception e)
           {                
           	LogTWM.getLog(Simulation_ShareDAO.class);
           	LogTWM.log(Priority.FATAL, "addSimulationShareInfo:" + e.getMessage());
            return false;
              
           }
		return true;
    
       }
    
}