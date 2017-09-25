/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tim.dao;

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
import com.tim.model.Order;

/**
 *
 * @author je10034
 */
public class OrderDAO {

    public OrderDAO() {
    }

    public static Order LastOrderID() 
    {
       
       Order resultado = null;
       
       BeanHandler blh= new BeanHandler(Order.class);
    	
       try
           {
               QueryRunner qr=new QueryRunner();                             
               resultado = (Order)  qr.query("select coalesce(max(orderID),1) orderID from orders ",  blh);
               
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(Order.class);
           	LogTWM.log(Priority.FATAL, "LastOrderID:" + e.getMessage());
               
              
           }
		return resultado;
    
       }

    public static boolean addOrder(int orderID, int shareID) 
    {
       
       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();
               datos.add(orderID);
               datos.add(shareID);
               
               qr.update("insert into orders (orderID, shareID) values(?,?)", datos.toArray()); 
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(Order.class);
              	LogTWM.log(Priority.FATAL, " addOrder:" + e.getMessage());
               return false;
              
           }
		return true;
    
       }
    
    public static boolean updateStatusOrder(int OrderId, int Status) 
    {
       
       try
           {
               QueryRunner qr=new QueryRunner();
               java.util.List datos = new java.util.ArrayList();
               datos.add(Status);
               datos.add(OrderId);
               
               qr.update("update orders set checked=? where orderID=?", datos.toArray()); 
                       
           }
           catch(Exception e)
           {                
        	   LogTWM.getLog(Order.class);
             	LogTWM.log(Priority.FATAL, "updateStatusOrder:" + e.getMessage());
               return false;
              
           }
		return true;
    
       }
    
    
     public static Order getOrder(int OrderID) 
     {
       Order resultado = null;

        try
            {
                QueryRunner qr=new QueryRunner();
                BeanHandler blh= new BeanHandler(Order.class);
                
                java.util.List datos = new java.util.ArrayList();
                datos.add(OrderID);
                resultado = (Order)  qr.query("select * from orders WHERE  orderID=?  ",  datos.toArray(),blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(Order.class);
             	LogTWM.log(Priority.FATAL, "getOrder:" + e.getMessage());
               
            }
            return resultado;
        }    
     

     public static Order getOrdersPending() 
     {
       Order resultado = null;

        try
            {
                QueryRunner qr=new QueryRunner();
                BeanListHandler blh= new BeanListHandler(Order.class);
                
                resultado = (Order)  qr.query("select * from orders WHERE  checked=0 ", blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(Order.class);
             	LogTWM.log(Priority.FATAL, "getOrdersPending:" + e.getMessage());
               
            }
            return resultado;
        }    
     
     
      
        

}


