package com.tim.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import org.apache.log4j.Priority;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.contracts.FutContract;
import com.ib.contracts.StkContract;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.dao.ConfigurationDAO;
import com.tim.dao.MarketDAO;
import com.tim.dao.OrderDAO;
import com.tim.dao.PositionDAO;
import com.tim.dao.RealTimeDAO;
import com.tim.dao.RuleDAO;
import com.tim.dao.ShareDAO;
import com.tim.dao.SimulationPositionDAO;
import com.tim.service.TIMApiGITrader;
import com.tim.util.ConfigKeys;
import com.tim.util.LogTWM;
import com.tim.util.PositionStates;
import com.tim.util.TWSMail;
import com.tim.util.Utilidades;

public class  StrategyMinMax  extends Strategy  {

	
	/* DATOS CALCULADOS INTERNOS DE LA ESTRATEGIA. HABRA OTROS PROPIOS DE LA ESTRATEGIA GENERAL. */
	private Double ValueToBuy =0.0;
	private Double MaxValue =0.0;
	
	/* fecha de entrada de la simulacion */ 
	private Timestamp SimulationDateTrade =null;
	
	
	
	
	
	
	
	
	

	@Override
	public boolean ExecuteSimulation(Market oMarket, Share Share,
			Calendar SimulationDate) {
		// TODO Auto-generated method stub
		try
	    {
			
			LogTWM.getLog(StrategyMinMax.class);			
			LogTWM.log(Priority.INFO, "UserAccount:" +  this.getACCOUNT_NAME() + ",detectada posible entrada SIMULADA de " + Share.getName() +  "Tick:" + Share.getSymbol() + ",PrecioCompra:" + ValueToBuy + ",Min:" + this.MinValue +"Max:" + this.MaxValue + ",MaxWithGap:" + this.MaxValueWithGap + ",MinWithGap:" + this.MinValueWithGap );
			
			// hace falta???????? ..creo que si, para tener control sobre la operacion de compra /venta 
			SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMM");
			
			boolean bIsFutureStock = Share.getSecurity_type().equals(ConfigKeys.SECURITY_TYPE_FUTUROS)  && Share.getExpiry_date()!=null;
			

			String _Expiration = "";
		    if (bIsFutureStock)
				_Expiration = sdf.format(Share.getExpiry_date());
		    		    				
			
			/* FORMATEAMOS A DOS CIFRAS */		
			// precio del tick más o menos un porcentaje ...normalmente %1
			// ojo con los FUTUROS..llevan cambios porcentuales
			
			double ValueLimit =0.0;		// largo 
					
			if (bReachedMin) // corto
			{
				ValueLimit = ValueToBuy - (ValueToBuy * Share.getPercentual_limit_buy());				
			}
			
			if (bIsFutureStock)
			{
				ValueLimit = Utilidades.TickLimit_WithMultiplier(ValueToBuy,Share.getTick_Futures(), ValueLimit, bReachedMin);
			}
			
			
			/* Posicion en MYSQL de CONTROL. OJO...ANTES SIEMPRE PARA DESPUES CONTROLARLA EN CASO DE ERROR. */
			SimulationPosition BuyPositionSystem = new SimulationPosition();
							
			BuyPositionSystem.setPrice_buy(ValueToBuy);  // ojo, es estimativo
			BuyPositionSystem.setDate_buy(SimulationDateTrade);
			BuyPositionSystem.setShare_number(Share.getNumber_purchase());
			BuyPositionSystem.setShareID(Share.getShareId());		
			BuyPositionSystem.setState(PositionStates.status.PENDING_BUY.toString());
			BuyPositionSystem.setLimit_price_buy(Utilidades.RedondeaPrice(ValueLimit));
			
			BuyPositionSystem.setType(PositionStates.statusTWSFire.BUY.toString());			
			if (bReachedMin)
			{
				BuyPositionSystem.setType(PositionStates.statusTWSFire.SELL.toString());
			}
			//BuyPositionSystem.setRealtimeID_buy_alert(RealTimeID);
			BuyPositionSystem.setStrategyID_buy(ConfigKeys.STRATEGY_BUY_MIN_MAX);
			BuyPositionSystem.setSell_percentual_stop_lost(Double.valueOf(Share.getSell_percentual_stop_lost()));
			BuyPositionSystem.setSell_percentual_stop_profit(Double.valueOf(Share.getSell_percentual_stop_profit()));
			BuyPositionSystem.setShare_number_traded(new Long(0));
			BuyPositionSystem.setShare_number_to_trade(Share.getNumber_purchase()); 
			
			//BuyPositionSystem.setTicketTWS_buy(new Long(TicketTWSToBuy));
			SimulationPositionDAO.addPosition(BuyPositionSystem);
			/* Posicion en MYSQL de CONTROL */
			
			return true;
		
	    }
			catch (Exception er)
	        {
				LogTWM.log(Priority.ERROR, er.getMessage());
				er.printStackTrace();
				return false;
	        }
	}

	@Override
	public boolean VerifySimulation(Share _oShare, Market _oMarket, Calendar _oSimulationDate ) {		
	
	boolean verified = false;
	
	_FULL_DAY_SCANNED = false;
	
	try
    {
		


	// HORA DE FIN DE CALCULO DE MAX Y MINIMOS.
	String HoraInicioLecturaMaxMin = Utilidades.getActualHourFormatPlusMinutes(_oSimulationDate, _oMarket.getStart_hour(), _oShare.getOffset1min_read_from_initmarket());
	String HoraFinLecturaMaxMin = Utilidades.getActualHourFormatPlusMinutes(_oSimulationDate,_oMarket.getStart_hour(), _oShare.getOffset2min_read_from_initmarket());
	
	/* COMPROBAMOS ALGUN TIPO DE ERROR */
	if (HoraInicioLecturaMaxMin.contains("-1") || HoraFinLecturaMaxMin.contains("-1"))
	{
		LogTWM.log(Priority.ERROR, "[Estrategia:StrategyMinMax]: Errores formateando las horas de Max y Min de la acción. Hora[" + _oMarket.getStart_hour()  + "], Offset1[" + _oShare.getOffset1min_read_from_initmarket() + "]");
		return false;
	}
		
	// ya no obtenemos el maximo y mínimo, sino el correspondiente al tramo que me han dicho
	
	Calendar DateMinMaxFrom =Utilidades.getNewCalendarWithHour(_oSimulationDate, HoraInicioLecturaMaxMin);
	Calendar DateMinMaxTo =Utilidades.getNewCalendarWithHour(_oSimulationDate,HoraFinLecturaMaxMin);
	
	
	/* TIEMPOS REALES Y MAXIMOS Y MINIMOS CONSEGUIDOS */
	RealTime oRTimeEnTramo = (RealTime) RealTimeDAO.getRealTimeBetweenDates(_oShare.getShareId().intValue(), new Timestamp(DateMinMaxFrom.getTimeInMillis()), new Timestamp(DateMinMaxTo.getTimeInMillis())); 			

	//RealTime oShareLastRTime = (RealTime) RealTimeDAO.getLastRealTime(_oShare.getShareId().intValue());
	if (oRTimeEnTramo!=null) // && oShareLastRTime!=null && oShareLastRTime.getValue()!=null)
	{	
		/* double ValueToBuy =0.0; */
		/* estos son los dos limites que debe rotar la accion para comprar */
		double MaxValueWithGap = (oRTimeEnTramo.getMax_value()*_oShare.getPercentual_value_gap().doubleValue()) +  oRTimeEnTramo.getMax_value();
		double MinValueWithGap = oRTimeEnTramo.getMin_value() - (oRTimeEnTramo.getMin_value()*_oShare.getPercentual_value_gap().doubleValue()) ;
		
		double MaxValueWithGapAndLimit = MaxValueWithGap  + (MaxValueWithGap*_oShare.getPercentual_limit_buy());
		double MinValueWithGapAndLimit = MinValueWithGap - (MinValueWithGap * _oShare.getPercentual_limit_buy());
		
		
		SimulationRealTime oShareSimulationCheckMax=  RealTimeDAO.getSimulationRealTime(_oShare.getShareId().intValue(), MaxValueWithGap, MaxValueWithGapAndLimit, new java.sql.Timestamp(_oSimulationDate.getTimeInMillis()));
		SimulationRealTime oShareSimulationCheckMin=  RealTimeDAO.getSimulationRealTime(_oShare.getShareId().intValue(), MinValueWithGapAndLimit, MinValueWithGap, new java.sql.Timestamp(_oSimulationDate.getTimeInMillis()));
		
		/* FOUND?? */								
		boolean bMaxReached = (oShareSimulationCheckMax!=null);
		boolean bMinReached = (oShareSimulationCheckMin!=null);
		
		//MyLog.log(Priority.INFO, "[Verify " + this.getClass().getName() + "] para " + ShareStrategy.getSymbol() + ", Min, Max, MinGap, MaxGap, Tick, bMaxReached, bMinReached : " + oRTimeEnTramo.getMin_value().doubleValue() + "|" + oRTimeEnTramo.getMax_value().doubleValue() +  "|" + MinValueWithGap + "|" + MaxValueWithGap + "|" +  oShareLastRTime.getValue() + "|" + bMaxReached + "|" + bMinReached) ;

		
		if ((bMaxReached && oShareSimulationCheckMax!=null && oShareSimulationCheckMax.getValue()!=null)  || 
				(bMinReached && oShareSimulationCheckMin!=null && oShareSimulationCheckMin.getValue()!=null) )					
		{
			
			this.SimulationDateTrade = (bMaxReached ? oShareSimulationCheckMax.getAddedDate() : oShareSimulationCheckMin.getAddedDate());
			
			
			Calendar calFechaActualWithDeadLine = Calendar.getInstance();
			calFechaActualWithDeadLine.setTimeInMillis(this.SimulationDateTrade.getTime());						
		
			calFechaActualWithDeadLine.add(Calendar.MINUTE,this.getSell_all_deadline_min_toclose());
			
			Calendar  calFechaFinMercado = Utilidades.getNewCalendarWithHour(calFechaActualWithDeadLine, _oMarket.getEnd_hour());
			
			/* ESTA DENTRO DEL LIMITE DE COMPRA */
			if (!calFechaActualWithDeadLine.after(calFechaFinMercado))
			{
				/* MUCHOS SON IRRELEVANTES PARA LA SIMULACION */
				this.ValueToBuy = (bMaxReached ? oShareSimulationCheckMax.getValue() : oShareSimulationCheckMin.getValue());				
				this.MaxValueWithGap = MaxValueWithGap;
				this.MinValueWithGap = MinValueWithGap;
				this.MinValue = MinValueWithGap;
				this.MaxValue = MaxValueWithGap;
				this.verified = true;				
				this.bReachedMax = bMaxReached;
				this.bReachedMin = bMinReached;
				this.RealTimeID=  (bMaxReached ? oShareSimulationCheckMax.getRealtimeID() : oShareSimulationCheckMin.getRealtimeID());						
				verified = true;
				
			}/* FIN ESTA DENTRO DEL LIMITE DE COMPRA */
			

		}
						
		
		
		}
	

	} // fin de verificacion de operacion de compra previa 		
    
	catch (Exception er)
    {
		LogTWM.log(Priority.ERROR, er.getMessage());
		er.printStackTrace();
		verified = false;
    }

	return verified;
	}

	
	private Double MinValue =0.0;
	private Double MaxValueWithGap =0.0;
	private Double MinValueWithGap =0.0;
	private boolean bReachedMax;
	private boolean bReachedMin;
	private boolean verified;
	private Long RealTimeID=new Long(0); 
	
	
	private LogTWM MyLog;
	
	
	
	
	public StrategyMinMax() {
		super();
		this.JSP_PAGE = "/jsp/admin/strategies/strategyminmax.jsp";
		// TODO Auto-generated constructor stub
	}

	public boolean isActive() {
		// TODO Auto-generated method stub
		return (this.getActive().equals(new  Integer(1)));
	}

	public boolean Verify(Share ShareStrategy, Market oMarket) {
		// TODO Auto-generated method stub
		
		boolean verified = false; 
		LogTWM.getLog(StrategyMinMax.class);									
		//LogTWM.log(Priority.INFO, ShareStrategy.getName() );		
		
		try
        {
			
		String HoraActual = Utilidades.getActualHourFormat();
			
		/* supongamos cierre mercado a las 2200 */
		/* HoraDeadLineToClose sera la hora actual mas los minutos de la estrategia */
		Calendar calFechaActualWithDeadLine = Utilidades.getNewCalendarWithHour(HoraActual);
		calFechaActualWithDeadLine.add(Calendar.MINUTE,this.getSell_all_deadline_min_toclose());
		Calendar calFechaFinMercado = Utilidades.getNewCalendarWithHour(oMarket.getEnd_hour());
		
		
		
		
		
			
		/*  CONTROLAMOS EL DEADLINE PARA COMPRAR */ 
		
		// maximos y minimos...ya lo tenemos de la tabla.
		//RealTime oShareMixMaxRTime = RealTimeDAO.getMinMaxRealTime(ShareStrategy.getShareId().intValue());
		// last tick  

		// verificamos compra previa. No compramos si hay una compra previa y estamos en el margen de tiempo de compra.
		// verificamos si ha pasado el tiempo necesario con los offset de lectura 
		if (!PositionDAO.ExistsPositionShareOpen(ShareStrategy.getShareId().intValue()) && !calFechaActualWithDeadLine.after(calFechaFinMercado))		
		{	
			
			
			// supuestamente estamos leyendo...verificamos si con respecto al mercado ya tenemos los max y min
			// comparamos que la hora de lectura final haya sobrepasado el actual 
			// HHMM
			

			// HORA DE FIN DE CALCULO DE MAX Y MINIMOS.
			String HoraInicioLecturaMaxMin = Utilidades.getActualHourFormatPlusMinutes(oMarket.getStart_hour(), ShareStrategy.getOffset1min_read_from_initmarket());
			String HoraFinLecturaMaxMin = Utilidades.getActualHourFormatPlusMinutes(oMarket.getStart_hour(), ShareStrategy.getOffset2min_read_from_initmarket());
			
			/* COMPROBAMOS ALGUN TIPO DE ERROR */
			if (HoraInicioLecturaMaxMin.contains("-1") || HoraFinLecturaMaxMin.contains("-1"))
			{
				LogTWM.log(Priority.ERROR, "[Estrategia:StrategyMinMax]: Errores formateando las horas de Max y Min de la acción. Hora[" + oMarket.getStart_hour()  + "], Offset1[" + ShareStrategy.getOffset1min_read_from_initmarket() + "]");
				return false;
			}
				
			
			if (HoraActual.compareTo(HoraFinLecturaMaxMin)>0)   // hora actyual ya ha pasado, podemos entrar en la operativa
			{
				
			
			// ya no obtenemos el maximo y mínimo, sino el correspondiente al tramo que me han dicho
			
			Calendar DateMinMaxFrom =Utilidades.getNewCalendarWithHour(HoraInicioLecturaMaxMin);
			Calendar DateMinMaxTo =Utilidades.getNewCalendarWithHour(HoraFinLecturaMaxMin);
			
			
			/* TIEMPOS REALES Y MAXIMOS Y MINIMOS CONSEGUIDOS */
			RealTime oRTimeEnTramo = (RealTime) RealTimeDAO.getRealTimeBetweenDates(ShareStrategy.getShareId().intValue(), new Timestamp(DateMinMaxFrom.getTimeInMillis()), new Timestamp(DateMinMaxTo.getTimeInMillis())); 			

			RealTime oShareLastRTime = (RealTime) RealTimeDAO.getLastRealTime(ShareStrategy.getShareId().intValue());
			if (oRTimeEnTramo!=null && oShareLastRTime!=null && oShareLastRTime.getValue()!=null)
			{	
				/* double ValueToBuy =0.0; */
				/* estos son los dos limites que debe rotar la accion para comprar */
				double MaxValueWithGap = (oRTimeEnTramo.getMax_value()*ShareStrategy.getPercentual_value_gap().doubleValue()) +  oRTimeEnTramo.getMax_value();
				double MinValueWithGap = oRTimeEnTramo.getMin_value() - (oRTimeEnTramo.getMin_value()*ShareStrategy.getPercentual_value_gap().doubleValue()) ;
				
				double MaxValueWithGapAndLimit = MaxValueWithGap  + (MaxValueWithGap*ShareStrategy.getPercentual_limit_buy());
				double MinValueWithGapAndLimit = MinValueWithGap - (MinValueWithGap * ShareStrategy.getPercentual_limit_buy());
				
				
				/* SE ALCANZA EL MAXIMO O MINIMO 
				 * CAMBIO 1.4.2013
				 *  EL REALTIME O CAMBIO DE TICK DEBE ESTAR ENTRE EL MAX O MIN Y EL BORDE SUPERIOR.
				 *  HASTA AHORA, VERIFICABAMOS QUE ESTUVIESE POR ENCIMA. NOS ASEGURAMOS CON EL PRECIO LIMITADO.
				 * */
				boolean bMaxReached = ((oShareLastRTime.getValue().doubleValue() > MaxValueWithGap) &&  (oShareLastRTime.getValue().doubleValue()< MaxValueWithGapAndLimit));
				boolean bMinReached = ((oShareLastRTime.getValue().doubleValue() < MinValueWithGap)  &&  (oShareLastRTime.getValue().doubleValue()> MinValueWithGapAndLimit));
				
				/* controlamos operaciones intradia ejecutadas para verificar si se puede entrar o no */
				int NUM_OPERATION_LARGO = 0;
				int NUM_OPERATION_CORTO = 0;
				
				//MyLog.log(Priority.INFO, "[Verify " + this.getClass().getName() + "] para " + ShareStrategy.getSymbol() + ", Min, Max, MinGap, MaxGap, Tick, bMaxReached, bMinReached : " + oRTimeEnTramo.getMin_value().doubleValue() + "|" + oRTimeEnTramo.getMax_value().doubleValue() +  "|" + MinValueWithGap + "|" + MaxValueWithGap + "|" +  oShareLastRTime.getValue() + "|" + bMaxReached + "|" + bMinReached) ;
				
				if (bMaxReached || bMinReached)					
				{
					
					/* 08.02.2013.... Para realizar una operacion, se comprueba si hubo previas (La regla de repeticion me 
					 * descarta entrar en un valor si obtuve beneficio en ambas dos (buy or sell) .Tratamos el resto de los casos
					 * que van condicionados a la operacion a realizar.
					 * 11.02.2013..Consideramos las perdidas y beneficio en un sentido
					 * 12.03.2013 --> SOLO DEJAMOS UNA POSICION POR SENTIDO
					 */
					java.util.List<Position> lPosition = PositionDAO.getTradingPositions(ShareStrategy.getShareId().intValue());
					if (lPosition!=null && lPosition.size()>0)   // si 
					{
						for (Position oLastPosition: lPosition)
						{
							// buy
							if (oLastPosition.getType().equals(PositionStates.statusTWSFire.BUY.toString()))
							{
								NUM_OPERATION_LARGO +=1; 
							}
							else  // venta
							{
								NUM_OPERATION_CORTO +=1; 
							}			
							
						}
					}
					
					/* RECUPERAMOS EL PARAMETROS DEFINIDO DE LA REGLA DE REPETICION */
					Rule  oRuleRepeatShare =  RuleDAO.getRule(ConfigKeys.RULE_TRADE_REPEAT_SHARE.intValue());
					boolean bSuccessRule = false;
					if (oRuleRepeatShare!=null)
					{
						/* cambiamos, solo se puede entrar otra vez en un valor cuando
						 * no tuve beneficio en el mismo sentido de entrada y el numero de operaciones 
						 * con perdidas sumen menos que el perametro...es decir, doy otra oportunidad para entrar 
						 * 
						 *  12.03.2013 --> SOLO PERMITIMOS ENTRAR EN UN VALOR POR SENTIDO EL NUMERO DE VECES QUE NOS INDICA EL PARAMETRO
						 */
						if (bMaxReached)   // ENTRAMOS A LARGO
						{	
							/* se puede comprar cuando no haya habido beneficio previo y con perdida no haya 
							 * habido o sea menor o igual que lo marca el parametro, es decir
							 * si el parametro marca 1, dejamos que haya otra posibilidad de entrar.
							 * 
							 *   *  12.03.2013 --> SOLO PERMITIMOS ENTRAR EN UN VALOR POR SENTIDO EL NUMERO DE VECES QUE NOS INDICA EL PARAMETRO
							 */
							bSuccessRule = NUM_OPERATION_LARGO < oRuleRepeatShare.getBuy_limit_torepeat_same_share().intValue();
						
						}
						else   //  // ENTRAMOS A CORTO
						{
							bSuccessRule = NUM_OPERATION_CORTO < oRuleRepeatShare.getBuy_limit_torepeat_same_share().intValue();	
						}
						/*LogTWM.log(Priority.INFO, ShareStrategy.getSymbol() + " SuccessRuleRepeat:" + bSuccessRule + "------------>" +
								"NUM_OPERATION_LARGO|NUM_OPERATION_CORTO|getBuy_limit_torepeat_same_share:" + NUM_OPERATION_LARGO + "|" + NUM_OPERATION_CORTO +
								"|" + oRuleRepeatShare.getBuy_limit_torepeat_same_share().intValue());*/
						
					}  // fin de hay regla de repeticion
					else
					{
						bSuccessRule = true;
					}
					
					/* verificamos las operaciones previas con beneficio o perdida...las perdidas se da otra oportunidad.
					
					
					/* no volvemos a calcular los datos en el método Execute */
					if (bSuccessRule)
					{
					
					this.ValueToBuy = oShareLastRTime.getValue();
					this.MaxValueWithGap = MaxValueWithGap;
					this.MinValueWithGap = MinValueWithGap;
					this.MinValue = oRTimeEnTramo.getMin_value();
					this.MaxValue = oRTimeEnTramo.getMax_value();
					this.verified = true;				
					this.bReachedMax = bMaxReached;
					this.bReachedMin = bMinReached;										
					this.RealTimeID=  oShareLastRTime.getRealtimeID();										
					verified = true;
					}
					/* else
					//LogTWM.log(Priority.INFO, ShareStrategy.getSymbol() + " SuccessRuleRepeat:" + bSuccessRule + "------------" +
								"NUM_OPERATION_PROFIT_CORTO|NUM_OPERATION_LOST_LARGO|NUM_OPERATION_LOST_LARGO|NUM_OPERATION_LOST_LARGO " +
								"getBuy_limit_torepeat_same_share:      " + NUM_OPERATION_PROFIT_CORTO + "|" + NUM_OPERATION_LOST_LARGO
								+"|" + NUM_OPERATION_LOST_LARGO + "|" + NUM_OPERATION_LOST_LARGO  + "|" + oRuleRepeatShare.getBuy_limit_torepeat_same_share().intValue());		
					
						*/
					
				}
				
				}
			}  //fin de comprobar lectura de maximos y minimos.

		} // fin de verificacion de operacion de compra previa 	
		
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
			
			LogTWM.getLog(StrategyMinMax.class);			
			
			LogTWM.log(Priority.INFO, "UserAccount:" +  this.getACCOUNT_NAME() + ",detectada posible entrada de " + ShareStrategy.getName() +  "Tick:" + ShareStrategy.getSymbol() + ",PrecioCompra:" + ValueToBuy + ",Min:" + this.MinValue +"Max:" + this.MaxValue + ",MaxWithGap:" + this.MaxValueWithGap + ",MinWithGap:" + this.MinValueWithGap );
			
			
			
			// hace falta???????? ..creo que si, para tener control sobre la operacion de compra /venta 
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
  		    
			//oContrat = (Contract) oTIMApiWrapper.createContract(ShareStrategy.getSymbol(), ShareStrategy.getSecurity_type(),ShareStrategy.getExchange(),oMarket.getCurrency(), _Expiration, "", 0);
			
			
			OrderDAO.addOrder(oTIMApiWrapper.getLastPositionID(), ShareStrategy.getShareId().intValue());
			// colocamos operacion de compra			
			Order BuyPositionTWS = new Order();
			
			BuyPositionTWS.account( this.getACCOUNT_NAME());
			
			BuyPositionTWS.totalQuantity(ShareStrategy.getNumber_purchase().intValue());
			BuyPositionTWS.orderType(PositionStates.ordertypes.LMT.toString());		    
			
					    				
			//BuyPositionTWS.m_allOrNone = true;		    				
			
			/* FORMATEAMOS A DOS CIFRAS */
			
			// precio del tick más o menos un porcentaje ...normalmente %1
			// ojo con los FUTUROS..llevan cambios porcentuales
			
			double ValueLimit =0.0;
			// largo 
			ValueLimit = ValueToBuy + (ValueToBuy * ShareStrategy.getPercentual_limit_buy());			
			if (bReachedMin) // corto
			{
				ValueLimit = ValueToBuy - (ValueToBuy * ShareStrategy.getPercentual_limit_buy());				
			}
			
			if (bIsFutureStock)
			{
				ValueLimit = Utilidades.TickLimit_WithMultiplier(ValueToBuy,ShareStrategy.getTick_Futures(), ValueLimit, bReachedMin);
			}
			
			
			BuyPositionTWS.lmtPrice( Utilidades.RedondeaPrice(ValueLimit));
			BuyPositionTWS.auxPrice(Utilidades.RedondeaPrice(ValueLimit));
			
			/* DIARIA 
			BuyPositionTWS.m_tif = "DAY";			*/
			/* VALIDA HASTA LA FECHA DE HOY YYYYMMDD 
			BuyPositionTWS.m_goodTillDate = Utilidades.getDateNowFormat();*/ 
			
			/*  SI ES UNA COMPRA, NOS POSICIONAMOS CORTOS SI BAJA EL MINIMO */
			BuyPositionTWS.action(PositionStates.statusTWSFire.BUY.toString());			
			if (bReachedMin)
			{
				BuyPositionTWS.action(PositionStates.statusTWSFire.SELL.toString());
			}
			
			
			int LastPositionID = Utilidades.LastPositionIDTws(oTIMApiWrapper);
		
			/* Posicion en MYSQL de CONTROL. OJO...ANTES SIEMPRE PARA DESPUES CONTROLARLA EN CASO DE ERROR. */
			Position BuyPositionSystem = new Position();
			
			BuyPositionSystem.setPositionID(new Long(LastPositionID));
			BuyPositionSystem.setDescription("");
			BuyPositionSystem.setPrice_buy(ValueToBuy);  // ojo, es estimativo
			BuyPositionSystem.setDate_buy(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			BuyPositionSystem.setShare_number(ShareStrategy.getNumber_purchase());
			BuyPositionSystem.setShareID(ShareStrategy.getShareId());
			BuyPositionSystem.setState_buy(PositionStates.statusTWSCallBack.PendingSubmit.toString());
			BuyPositionSystem.setState(PositionStates.status.PENDING_BUY.toString());
			BuyPositionSystem.setLimit_price_buy(Utilidades.RedondeaPrice(ValueLimit));
			BuyPositionSystem.setType(BuyPositionTWS.getAction());
			BuyPositionSystem.setRealtimeID_buy_alert(RealTimeID);
			BuyPositionSystem.setStrategyID_buy(ConfigKeys.STRATEGY_BUY_MIN_MAX);
			
			
    		/* 5 PUNTOS o 5%  POR DEFECTO EN LOS FUTUROS */			
    		double _defaultstop_percent = 0;
    		boolean _IsFuture = (ShareStrategy.getSecurity_type()!=null && ShareStrategy.getSecurity_type().equals(ConfigKeys.SECURITY_TYPE_STOCK) ? true : false);
    		if (_IsFuture)
    		{	    			    			
    			_defaultstop_percent = (5 * 100) / ValueToBuy /100;   
    		}
    		else
    		{
    			_defaultstop_percent = 0.05;
    		}
    		if (ShareStrategy.getSell_percentual_stop_lost()==null)
    			BuyPositionSystem.setSell_percentual_stop_lost(Double.valueOf(ShareStrategy.getSell_percentual_stop_lost()));
    		else
    			BuyPositionSystem.setSell_percentual_stop_lost(_defaultstop_percent);
    		
    		if (ShareStrategy.getSell_percentual_stop_profit()==null)
    			BuyPositionSystem.setSell_percentual_stop_profit(Double.valueOf(ShareStrategy.getSell_percentual_stop_profit()));
    		else
    			BuyPositionSystem.setSell_percentual_stop_profit(_defaultstop_percent);    		
			/* BuyPositionSystem.setSell_percentual_stop_lost(ShareStrategy.getSell_percentual_stop_lost());
			BuyPositionSystem.setSell_percentual_stop_profit(ShareStrategy.getSell_percentual_stop_profit());*/
			BuyPositionSystem.setShare_number_traded(new Long(0));
			BuyPositionSystem.setShare_number_to_trade(ShareStrategy.getNumber_purchase()); 
			
			/* MODO DE SIMULACION */
			Configuration _oConf = ConfigurationDAO.getConfiguration("SIMULATION_MODE");
			/* SIMULATION MODE A CERO POR DEFECTO. SI NO, A 1 */ 
			if (_oConf!=null && _oConf.getValue().equals(ConfigKeys._SIMULATED_TRADING_MODE))
			{
				BuyPositionSystem.setSimulation_mode(new Long(1));
			}
			
			
			//BuyPositionSystem.setTicketTWS_buy(new Long(TicketTWSToBuy));
			PositionDAO.addPosition(BuyPositionSystem);
			/* Posicion en MYSQL de CONTROL */
			
			
			oTIMApiWrapper.GITraderOpenOrder(LastPositionID, oContrat, BuyPositionTWS);
			
			//oTIMApiWrapper.LastPositionID;  //position solo una vez.. EL WRAPPER YA ME LO INDICA
			//oTIMApiWrapper.lastOrderID = oTIMApiWrapper.lastOrderID + 2; 	// orders dos, para evitar con la lectura
			
			
			
			
			
			
			
			
			
			/* MANDAMOS CORREO 	      				
			String _MailText= "[SIMULADO]." + Utilidades.getActualHourFormat() + " " + BuyPositionTWS.m_action + " " + ShareStrategy.getSymbol() + " a " + ValueToBuy + ", " + ShareStrategy.getNumber_purchase() + " acciones ";
			_MailText += " Mix  Max :  " + this.MinValue + "," + this.MaxValue;
			_MailText += " Gap Porcentual :  " +  ShareStrategy.getPercentual_value_gap();
			String[] Recipients = {"rfdiestro@gmail.com","trading1@ono.com","david_nevado@yahoo.es"};
			TWSMail Mail = new TWSMail(Recipients,_MailText, _MailText);
			Mail.SendMail();*/
		    /* FIN MANDAMOS CORREO */
			
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
	
	public static void main(String[] args) throws Exception {
 		// TODO Auto-generated method stub
    	 //List<Position> Lista = PositionDAO.getTradingPositions(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		Strategy oE=  new StrategyMinMax();
		Share oShare = ShareDAO.getShare("FB");
		Market ooMarket = MarketDAO.getMarket(new Long(2));
		oE.sell_all_deadline_min_toclose = 34;
		
		oE.Verify(oShare, ooMarket);
 	}

	




}
