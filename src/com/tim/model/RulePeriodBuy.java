package com.tim.model;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.log4j.Priority;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.tim.dao.OrderDAO;
import com.tim.dao.PositionDAO;
import com.tim.dao.RealTimeDAO;
import com.tim.service.TIMApiGITrader;
import com.tim.util.LogTWM;
import com.tim.util.PositionStates;
import com.tim.util.Utilidades;


/* REGLA QUE DETERMINA EL PERIODO HASTA EL CUAL SE PUEDE COMPRAR. EL INICIO LO DETERMINA EL MAX Y MINIMOS QUE DETERMINA
 * CUANDO ESTA LA OSCILACION DEL VALOR
 */
public class RulePeriodBuy extends Rule {

	
	
	public RulePeriodBuy(){
		super();
		this.JSP_PAGE = "/jsp/admin/rule/ruleperiodbuy.jsp";
		
	}
	
	public boolean Verify(Share ShareStrategy, Market oMarket) {
		// TODO Auto-generated method stub
		
		boolean verified = false;
		LogTWM.getLog(RulePeriodBuy.class);									
		
		
		try
        {
			
			// supuestamente estamos leyendo...verificamos si con respecto al mercado ya tenemos 
			// no superamos la barrera de operar ...son los minutos hasta el cierre
			String HoraActual = Utilidades.getActualHourFormat(); 			
			Calendar calFechaActual= Utilidades.getNewCalendarWithHour(HoraActual);
			
			Calendar calFechaFinMercado = Utilidades.getNewCalendarWithHour(oMarket.getEnd_hour());
			calFechaFinMercado.add(Calendar.MINUTE,-this.getBuy_minto_offset_close().intValue());
			
			/* SI SE CUMPLE QUE LA HORA ACTUAL ES MENOR QUE LOS X MINUTOS ANTES DEL CIERRE  */			
			if (calFechaActual.before(calFechaFinMercado))		
			{					
				verified =true;
				// 
			} /* pasado el tiempo limite, y hay posiciones abiertas, verified true */
			else
			{
				//LogTWM.log(Priority.INFO, "Regla Periodo de Compra no OK:");
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
	
	
	
}
