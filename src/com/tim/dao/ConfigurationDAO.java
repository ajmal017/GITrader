
package com.tim.dao;

import com.tim.util.LogTWM;
import com.tim.util.bbdd.QueryRunner;

import org.apache.commons.dbutils.handlers.BeanHandler;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Priority;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.model.Configuration;

/**
 *
 * @author je10034
 */
public class ConfigurationDAO {

    public ConfigurationDAO() {
    }

    public static Configuration getConfiguration(String Key) 
    {
    	Configuration resultado = null;

       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();               
               datos.add(Key);               
               BeanHandler blh= new BeanHandler(Configuration.class);
               resultado =(Configuration) qr.query("select * from config a where a.config_key=?", datos.toArray(),blh);                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(ConfigurationDAO.class);
           	   LogTWM.log(Priority.FATAL, "getConfiguration:[" +  Key + "]" + e.getMessage());
              
           }
           return resultado;
       }    
  }


