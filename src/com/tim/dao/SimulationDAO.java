/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tim.dao;

import com.tim.util.LogTWM;
import com.tim.util.PositionStates;
import com.tim.util.bbdd.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Priority;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.model.Market;
import com.tim.model.Share;
import com.tim.model.Simulation;

/**
 *
 * @author je10034
 */
public class SimulationDAO {
	
    public SimulationDAO() {
    	
    }

     public static java.util.List<Simulation> getSimulation(Long SimulationID) 
     {
    	 java.util.List<Simulation> resultado = null;

        try
            {
        		java.util.List datos = new java.util.ArrayList();               
        		datos.add(SimulationID);
                QueryRunner qr=new QueryRunner();
                BeanListHandler blh= new BeanListHandler(Simulation.class);
                resultado =(java.util.List<Simulation>)  qr.query("select * from simulation where simulationId=?", datos.toArray(), blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(SimulationDAO.class);
            	LogTWM.log(Priority.FATAL, "getSimulation:" + e.getMessage());
               
            }
            return resultado;
        }
     
     public static java.util.List<Simulation> getPendingSimulations() 
     {
    	 java.util.List<Simulation> resultado = null;

        try
            {
        		java.util.List datos = new java.util.ArrayList();               
                QueryRunner qr=new QueryRunner();
                datos.add(PositionStates.statusSimulation.Pending.toString());
                datos.add(PositionStates.statusSimulation.Started.toString());
                BeanListHandler blh= new BeanListHandler(Simulation.class);
                resultado =(java.util.List<Simulation>)  qr.query("select * from simulation where (status is null or status=? or status=?)", datos.toArray(), blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(SimulationDAO.class);
            	LogTWM.log(Priority.FATAL, "getSimulation:" + e.getMessage());
               
            }
            return resultado;
        }    
     
     public static java.util.List<Simulation> getSimulations() 
     {
    	 java.util.List<Simulation> resultado = null;

        try
            {
        		java.util.List datos = new java.util.ArrayList();               
                QueryRunner qr=new QueryRunner();                
                BeanListHandler blh= new BeanListHandler(Simulation.class);
                resultado =(java.util.List<Simulation>)  qr.query("select * from simulation order by dateadded desc ", datos.toArray(), blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(SimulationDAO.class);
            	LogTWM.log(Priority.FATAL, "getSimulations:" + e.getMessage());
               
            }
            return resultado;
        }   
     
     
     public static Simulation getActiveSimulation() 
     {
    	 Simulation resultado = null;

        try
            {
        		java.util.List datos = new java.util.ArrayList();               
                QueryRunner qr=new QueryRunner();
                datos.add(PositionStates.statusSimulation.Started.toString());
                BeanHandler blh= new BeanHandler(Simulation.class);
                resultado =(Simulation)  qr.query("select * from simulation where (status is null or status=?)", datos.toArray(), blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(SimulationDAO.class);
            	LogTWM.log(Priority.FATAL, "getActiveSimulation:" + e.getMessage());
               
            }
            return resultado;
        }    
     
     
    
     public static boolean UpdateSimulation(Simulation oSimulation) 
     {
    	 boolean resultadoOK = true;

 		try {

 			QueryRunner qr = new QueryRunner();

 			String SqlSimulation = "update simulation ";
 			SqlSimulation += "set status=##status##"; 			
 			SqlSimulation += " where SimulationID=" + oSimulation.getSimulationID();

 			qr.updateBean(SqlSimulation, oSimulation);

 		} catch (Exception e) {
 			resultadoOK = false;
 			LogTWM.getLog(PositionDAO.class);
 			LogTWM.log(Priority.FATAL, "addPosition:" + e.getMessage());

 		}
 		return resultadoOK;
 	}  
     
     
        

}


