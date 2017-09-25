/* 1.
 * BASICAMENTE SE ENCARGA DE VERIFICAR SI HAY QUE ENTRAR OTRA VEZ EN UN VALOR DADO.
 * ENTONCES, PARA CADA POSICION Y TIPO DE ENTRADA, SYMBOL + TYPE, PUEDE PASAR
 * 1. QUE HAYA BENEFICIO, NO ENTRAMOS MAS
 * 2. QUE HAYA PERDIDA, DAMOS OTRA OPORTUNIDAD DADO POR EL PARAMETRO.
 */

package com.tim.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Priority;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.tim.dao.MarketDAO;
import com.tim.dao.OrderDAO;
import com.tim.dao.PositionDAO;
import com.tim.dao.RealTimeDAO;
import com.tim.dao.ShareDAO;
import com.tim.service.TIMApiGITrader;
import com.tim.util.ConfigKeys;
import com.tim.util.LogTWM;
import com.tim.util.PositionStates;
import com.tim.util.Utilidades;
import com.tim.model.Position;


/* REGLAS BASICAS DE OPERATIVA
 * 1. SI ES UN FUTURO Y ESTA VENCIDO. NO DEJAMOS OPERAR SI EL FUTURO VENCE MAÑANA
 * 2. ETC.
 * 3. ETC.
 * 
 */
public class RuleValidStock extends Rule {

	public RuleValidStock(){
		super();
		this.JSP_PAGE = "/jsp/admin/rule/rulevalidstock.jsp";
		
	}
	
	public boolean Verify(Share ShareStrategy, Market oMarket) {
		// TODO Auto-generated method stub
		
		
		// POR DEFECTO VERIFICADO. SI CADUCA NO.
		boolean verified = true;
			
		
		boolean bIsFutureStock = ShareStrategy.getSecurity_type().equals(ConfigKeys.SECURITY_TYPE_FUTUROS)  && ShareStrategy.getExpiry_date()!=null;
		
		
		
		
		
		
		try
        {
			if (bIsFutureStock)
			{
				Calendar calHoy = Calendar.getInstance();
				
				
				java.util.Date dHoy = calHoy.getTime();
				
				java.util.Date calFuture = new Date(ShareStrategy.getExpiry_date().getTime());
				calFuture.setDate(calFuture.getDate()-1);
				
				verified = calFuture.after(dHoy);   // futuro y caducado..D-1 al dia de vencimiento.
				/* CADUCAN LOS STOCKS EL DIA DEL VENCIMIENTO MENOS UN DIA 
				 * POR TANTO, EL DIA ANTERIOR TAMPOCO OPERAMOS. DESACTIVAMOS EL TRADING */
				if (!verified)
				{
					/* FUTURO VENCIDO D-1 DEL VENCIMIENTO */
					ShareStrategy.setActive(new Long(0));
					ShareStrategy.setActive_trading(new Long(0));
					
					ShareDAO.updateShare(ShareStrategy);
					
				}
					
				
			/*	LogTWM.getLog(RuleValidStock.class);
				LogTWM.log(Priority.INFO, ShareStrategy.getSymbol() + ":" + verified);
				*/
			}
					
					
		
				
		}
		catch (Exception er)
        {
			LogTWM.getLog(RuleValidStock.class);
			LogTWM.log(Priority.ERROR, er.getMessage());
			er.printStackTrace();
			verified = false;
        }

		return verified;
	}
	
	@SuppressWarnings("deprecation")
	public static void main(String[] args) throws Exception {
 		// TODO Auto-generated method stub
    	
		
		
		
		/* Calendar calHoy = Calendar.getInstance();
		
		
		java.util.Date dHoy = calHoy.getTime();
		
		java.util.Date calFuture = new Date(MyShare.getExpiry_date().getTime());
		calFuture.setDate(calFuture.getDate()-1);
		
		System.out.println(calFuture.after(dHoy));
		
		System.out.println(sdf.format(dHoy) + "|" + sdf.format(calFuture));
		*/
		
 	}
	
	
	
}
