/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.tim.dao;

import com.tim.util.LogTWM;
import com.tim.util.bbdd.QueryRunner;
import org.apache.commons.dbutils.handlers.BeanListHandler;
import org.apache.commons.logging.impl.Log4JLogger;
import org.apache.log4j.Priority;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.model.Market;

/**
 *
 * @author je10034
 */
public class MarketShareDAO {

    public MarketShareDAO() {
    }


     public static java.util.List getListAllSharesFromMarket() 
     {
        java.util.List resultado = null;

        try
            {
                QueryRunner qr=new QueryRunner();
                BeanListHandler blh= new BeanListHandler(Market.class);
                
                String SQL = "select b.* from market a, share b where a.marketId = shareId";
                
                resultado =( java.util.List) qr.query(SQL, blh);
                        
            }
            catch(Exception e)
            {                
            	LogTWM.getLog(MarketShareDAO.class);
            	LogTWM.log(Priority.FATAL, "getListAllSharesFromMarket:" + e.getMessage());
               
            }
            return resultado;
        }    
     
         

}


