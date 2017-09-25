package com.tim.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.log4j.Priority;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.contracts.FutContract;
import com.ib.contracts.StkContract;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.dao.OrderDAO;
import com.tim.dao.PositionDAO;
import com.tim.dao.RealTimeDAO;
import com.tim.dao.ShareDAO;
import com.tim.jobs.Trading_Actions;
import com.tim.service.TIMApiGITrader;
import com.tim.util.ConfigKeys;
import com.tim.util.LogTWM;
import com.tim.util.PositionStates;
import com.tim.util.TWSMail;
import com.tim.util.Utilidades;

public class  StrategyCloseAllPositions  extends Strategy  {

	
	private LogTWM MyLog; 
	
	
	public StrategyCloseAllPositions() {
		super();
		// TODO Auto-generated constructor stub
		this.JSP_PAGE = "/jsp/admin/strategies/strategy_closeallpositions.jsp";
	}

	public boolean isActive() {
		// TODO Auto-generated method stub
		return (this.getActive().equals(new  Integer(1)));
	}

	public boolean Verify(Share ShareStrategy, Market oMarket) {
		// TODO Auto-generated method stub
		
		boolean verified = false;
		LogTWM.getLog(StrategyCloseAllPositions.class);									
		
		try
        {
		
		
		/* CALCULAMOS SI HEMOS SOBREPASADO EL DEADLINE HASTA EL CIERRE DEL MERCADO PARA VENDER */
			
		//String HoraActual = Utilidades.getActualHourFormat();
		
		String HoraActual = Utilidades.getActualHourFormat(); 
		/* supongamos cierre mercado a las 2200 */
		/* HoraDeadLineToClose sera la hora actual mas los minutos de la estrategia */
		Calendar calFechaActualWithDeadLine = Utilidades.getNewCalendarWithHour(HoraActual);
		calFechaActualWithDeadLine.add(Calendar.MINUTE,this.getSell_all_deadline_min_toclose());
		Calendar calFechaFinMercado = Utilidades.getNewCalendarWithHour(oMarket.getEnd_hour());
		
		/* Date FechaActualWithDeadLine = new Date(calFechaActualWithDeadLine.getTimeInMillis());
		FechaActualWithDeadLine.setMinutes(FechaActualWithDeadLine.getMinutes() + this.getSell_all_deadline_min_toclose());
		Date FechaFinMercado = new Date(calFechaFinMercado.getTimeInMillis());*/
		
		
			
		/*  1. Se cumple que con el deadline la hora actual pueda aumentar el dia. Controlarlo  
		 *  2 pasado el tiempo limite o, y hay posiciones abiertas, verified true 
		 *  3. */ 
		if (calFechaActualWithDeadLine.after(calFechaFinMercado)  && PositionDAO.ExistsPositionToClose(ShareStrategy.getShareId()))		
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
			LogTWM.getLog(StrategyCloseAllPositions.class);									
			
			/* BUSCAMOS TODAS LAS OPERACIONES QUE HAYA PENDIENTES....
			 * 1. COMPRA SE CANCELAN.
			 * 2. LAS DE VENTA SE VENDEN
			 * 3. PARAMETRO NUEVO, SOLO CERRAMOS LAS QUE ESPECIFIQUE EL PARAMETRO TYPE DE LA ESTRATEGIA, ALL, SELL,BUY*/
			java.util.List<Position> lSharePositions =  PositionDAO.getPositionToClose(ShareStrategy.getShareId());
			
			String _TypeOperationToClose = "ALL";  // en la estrategia en el modo automatico, debe restaurarse el valor a ALL
			
			if (this.getSell_all_deadline_type_operation()!=null)
			{
				_TypeOperationToClose = this.getSell_all_deadline_type_operation();
			}
			
			/* tenemos posiciones????? */
			if (lSharePositions!=null && lSharePositions.size()>0)
			{	
				// por cada accion 
				for (int i=0;i<lSharePositions.size();i++)
				{
					
					boolean bPositionToClose=false;
					
					
					Position oSharePosition = lSharePositions.get(i);					
					
					if (_TypeOperationToClose.equals("ALL") || _TypeOperationToClose.equalsIgnoreCase(oSharePosition.getType()))
					{	
						bPositionToClose=true;
					}
					
						
					if (bPositionToClose)  // posicion a cerrar?
					{
						
					
					
					SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMM");

					
					boolean bIsFutureStock = ShareStrategy.getSecurity_type().equals(ConfigKeys.SECURITY_TYPE_FUTUROS)  && ShareStrategy.getExpiry_date()!=null;

					String _Expiration = "";
		  		    if (bIsFutureStock)
						_Expiration = sdf.format(ShareStrategy.getExpiry_date());
		  		    
		  		    Contract oContrat = null;
		  		    
		  		  if (ShareStrategy.getSecurity_type().equals("FUT"))
		  		  {
		  			oContrat = new FutContract( ShareStrategy.getSymbol(), _Expiration);
					//oContrat.multiplier(String.valueOf(oShare.getMultiplier()));		    					
					oContrat.exchange(ShareStrategy.getExchange());
					oContrat.currency(oMarket.getCurrency());
		  		  }
						
					else		    					
						oContrat = new StkContract( ShareStrategy.getSymbol());
		  		    
				//	oContrat = (Contract) oTIMApiWrapper.createContract(ShareStrategy.getSymbol(), ShareStrategy.getSecurity_type(),ShareStrategy.getExchange(),oMarket.getCurrency(), _Expiration, "", 0);

					
					// enviamos operacion de venta.... estamos comprados y no está ya enviada
					if (oSharePosition.getDate_real_buy()!=null && 							
							oSharePosition.getState_sell()==null)  // hay operacion real de compra, 
																  // procedemos a vender si no esta enviada ya (state_sell nos lo dice).
					{
						
						OrderDAO.addOrder(oTIMApiWrapper.getLastPositionID(), ShareStrategy.getShareId().intValue());
						// colocamos operacion de venta			
						Order BuyPositionTWS = new Order();		
						//BuyPositionTWS.m_account = "U823568";
						BuyPositionTWS.account( this.getACCOUNT_NAME());
						
						BuyPositionTWS.totalQuantity(oSharePosition.getShare_number_to_trade().intValue());
						BuyPositionTWS.orderType(PositionStates.ordertypes.MKT.toString());		    				
						//BuyPositionTWS.m_allOrNone = true;		    				
						
						/* SIN PRECIO LIMITE 
						BuyPositionTWS.m_lmtPrice = Utilidades.RedondeaPrice(ValueLimit);
						BuyPositionTWS.m_auxPrice = Utilidades.RedondeaPrice(ValueLimit);
						*/						
						/* DIARIA 
						BuyPositionTWS.m_tif = "DAY";*/			
						/* VALIDA HASTA LA FECHA DE HOY YYYYMMDD 
						BuyPositionTWS.m_goodTillDate = Utilidades.getDateNowFormat();*/
						
						/*  SALIMOS DE LA POSICION  */
						String _OperationTYPE = "";
						if (oSharePosition.getType().equals(PositionStates.statusTWSFire.BUY.toString())) // operacion de compra normal..??
						{
							_OperationTYPE = PositionStates.statusTWSFire.SELL.toString();
						}
						else  // venta inicial
						{
							_OperationTYPE = PositionStates.statusTWSFire.BUY.toString();				
						}			
						
						BuyPositionTWS.action(_OperationTYPE);								
						int LastPositionID = Utilidades.LastPositionIDTws(oTIMApiWrapper);
						oTIMApiWrapper.GITraderOpenOrder(LastPositionID, oContrat, BuyPositionTWS);
						
						oSharePosition.setPositionID_tws_sell(new Long(LastPositionID));
						//oSharePosition.setLimit_price_sell(new Double(ValueLimit));
						//MyActualPosition.setPrice_sell(new Double(ValueLimit));
						oSharePosition.setState_sell(PositionStates.statusTWSCallBack.PendingSubmit.toString());
						/* si metemos el date sell en las parciales, no entran las siguientes */
						/* acumulo las acciones vendidas y a vender en la operativa */
						int _RemaingShares = oSharePosition.getShare_number_to_trade().intValue() +oSharePosition.getShare_number_traded().intValue();
						int _TotalShares = oSharePosition.getShare_number().intValue();
						if (_RemaingShares==_TotalShares)  // ultimo paquete de acciones
						{	
							oSharePosition.setDate_sell(new Timestamp(Calendar.getInstance().getTimeInMillis()));
						}						
						oSharePosition.setDescription("Venta.[Close All Positions].");						
						oSharePosition.setStrategyID_sell(ConfigKeys.STRATEGY_SELL_CLOSEALLPOSITIONS);
						
						
					}	
					// pendientes de operacion  de compra...cancelamos
					if (oSharePosition.getDate_real_buy()==null && !oSharePosition.getState_buy().contains(PositionStates.status.CANCEL_BUY.toString()))  // hay operacion real de compra, 
																  // procedemos a vender si no esta enviada ya (state_sell nos lo dice).
					{
						// cancelamos y actualizamos operacion en el TWS
						oTIMApiWrapper.GITradercancelOrder(oSharePosition.getPositionID().intValue());
						oSharePosition.setDescription("Cancel Buy [Close All Positions].");
					}
					/* ACTUALIZAMOS LA OPERACION CON LOS DATOS DE LA VENTA. ACTUALIZAMOS MAS EN EL ORDER STATUS */
					PositionDAO.updatePositionByPositionID(oSharePosition);
					
					
					// LA ESTRATEGIA CONTEMPLA EL SEGUIR OPERANDO POSTERIORMENTE..VERIFICAMOS Y ACTUALIZAMOS
					if (this.getSell_all_deadline_deactivate_trading()!=null && this.getSell_all_deadline_deactivate_trading().equals(new Integer(1)))
					{
						ShareStrategy.setActive_trading(new Long(0));
						ShareDAO.updateShare(ShareStrategy);
					}
					
					}  // fin posicion a cerrar 
					
				}  // fin // por cada accion 
					
				
					
				}  // end if operacion de compra???
				
			
				
				
			
			return true;
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
