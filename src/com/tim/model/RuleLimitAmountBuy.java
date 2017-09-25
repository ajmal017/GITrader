package com.tim.model;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Priority;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.tim.dao.MarketDAO;
import com.tim.dao.OrderDAO;
import com.tim.dao.PositionDAO;
import com.tim.dao.RealTimeDAO;
import com.tim.service.TIMApiGITrader;
import com.tim.util.LogTWM;
import com.tim.util.PositionStates;
import com.tim.util.Utilidades;


/* REGLA QUE DETERMINA LA CANTIDAD TOTAL INVERTIDA EN EL DIA Y SOBRE POSICIONES ABIERTAS
 */
public class RuleLimitAmountBuy extends Rule {

	
	public RuleLimitAmountBuy(){
		super();
		this.JSP_PAGE = "/jsp/admin/rule/rulelimitamountday.jsp";
		
	}
	
	public boolean Verify(Share ShareStrategy, Market oMarket) {
		// TODO Auto-generated method stub
		
		boolean verified = false;
		LogTWM.getLog(RuleLimitAmountBuy.class);									
		
		
		try
        {
			
			
			java.sql.Timestamp Hoy = new Timestamp(Calendar.getInstance().getTimeInMillis());
			

			Double TotalAmountTrading = PositionDAO.getTotalAmountPosicion(Hoy, Hoy,oMarket, null);
			
			//  si no hay nada, o si hay y no lo sobrepasa 
			if (TotalAmountTrading==null || (TotalAmountTrading!=null && TotalAmountTrading.intValue()<this.getBuy_limit_amount_day().intValue()))
			{	
				verified =true;
			}
			else
			{
				LogTWM.log(Priority.INFO, "Regla cantidad invertida no OK:" + TotalAmountTrading  +  "|" + this.getBuy_limit_amount_day().intValue());
			}
	
						
        }
		catch (Exception er)
        {
			LogTWM.log(Priority.ERROR, er.getMessage());
			er.printStackTrace();
			verified = false;
        }

		return verified;
	}
	
	public static void main(String[] args) throws Exception {
 		// TODO Auto-generated method stub
    	 //List<Position> Lista = PositionDAO.getTradingPositions(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		java.sql.Timestamp Hoy = new Timestamp(Calendar.getInstance().getTimeInMillis());
		
		Market oMarket = MarketDAO.getMarket(new Long(2));

		Double TotalAmountTrading = PositionDAO.getTotalAmountPosicion(Hoy, Hoy,oMarket, null);
		
		
 	}
	
	
	
}
