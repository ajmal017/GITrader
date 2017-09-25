package com.tim.model;

import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Priority;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.dao.OrderDAO;
import com.tim.dao.PositionDAO;
import com.tim.dao.RealTimeDAO;
import com.tim.jobs.Trading_Actions;
import com.tim.service.TIMApiGITrader;
import com.tim.service.TIMApiGITrader;
import com.tim.util.ConfigKeys;
import com.tim.util.LogTWM;
import com.tim.util.PositionStates;
import com.tim.util.TWSMail;
import com.tim.util.Utilidades;

public class  StrategyCancelPosition  extends Strategy  {

	
	private LogTWM MyLog; 
	
	
	public StrategyCancelPosition() {
		super();
		// TODO Auto-generated constructor stub
	}

	public boolean isActive() {
		// TODO Auto-generated method stub
		return (this.getActive().equals(new  Integer(1)));
	}

	public boolean Verify(Share ShareStrategy, Market oMarket) {
		// TODO Auto-generated method stub
		
		boolean verified = false;
		LogTWM.getLog(StrategyCancelPosition.class);									
		
		try
        {
		
		
		/* CALCULAMOS SI HEMOS SOBREPASADO EL DEADLINE HASTA EL CIERRE DEL MERCADO PARA VENDER */
			
		//String HoraActual = Utilidades.getActualHourFormat();
		if (PositionDAO.ExistsPositionToCancel(ShareStrategy.getShareId()))		
		{	
			
			verified =true;
			// 
		} /* pasado el tiempo limite, y hay posiciones abiertas, verified true */
        }
		catch (Exception er)
        {
												
			LogTWM.log(Priority.ERROR, er.getMessage());
			er.printStackTrace();
			verified = false;
        }

		return verified;
	}
	
	public boolean Execute(Market oMarket, Share ShareStrategy, TIMApiGITrader oTIMApiWrapper) {
		// TODO Auto-generated method stub
		try
        {
			LogTWM.getLog(StrategyCancelPosition.class);									
			
			
			Position lSharePosition =  PositionDAO.getPositionToCancel(ShareStrategy.getShareId());
			/* tenemos posiciones????? */
			if (lSharePosition!=null)
			{	
				// cancelamos y actualizamos operacion en el TWS
				oTIMApiWrapper.GITradercancelOrder(lSharePosition.getPositionID().intValue());
				lSharePosition.setDescription("Cancel Operation Manual");
				lSharePosition.setPending_cancelled(0);
				/* ACTUALIZAMOS LA OPERACION CON LOS DATOS DE LA VENTA. ACTUALIZAMOS MAS EN EL ORDER STATUS */
				PositionDAO.updatePositionByPositionID(lSharePosition);                
				return true;
			}
			else				
				return false;
			
		
        }
			catch (Exception er)
	        {
				LogTWM.log(Priority.ERROR, er.getMessage());				
				er.printStackTrace();
				return false;
	        }
			
		}
			
	

	public boolean Strategy() {
		// TODO Auto-generated method stub
		return false;
	}

	




}
