package com.tim.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

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
import com.tim.dao.ShareDAO;
import com.tim.dao.SimulationDAO;
import com.tim.dao.SimulationPositionDAO;
import com.tim.service.TIMApiGITrader;
import com.tim.util.ConfigKeys;
import com.tim.util.LogTWM;
import com.tim.util.MobileAvgUtil;
import com.tim.util.PositionStates;
import com.tim.util.TWSMail;
import com.tim.util.Utilidades;

public class  StrategyStopLostProfit_MobileAverage  extends Strategy  {
	
	private Double ValueToSell =0.0;
	private List<Position> lMyActualPosition =null;
	private List<SimulationPosition> lMyActualSimulationPosition =null;
	private Position MyActualPosition =null;
	private SimulationPosition MyActualSimulationPosition =null;
	
	private Long RealTimeID= new Long(0);
	
	private LogTWM MyLog; 

	private boolean verified;
	
	private Timestamp EntrySimulationDate;
	
	int _num_macdP = 8;   // Periodos
	int _num_macdT = 5;   // Tiempo de barras
	float _entryrate=0.75f;    // Umbral de superacion
	
	public StrategyStopLostProfit_MobileAverage() {
		super();
		this.JSP_PAGE = "/jsp/admin/strategies/strategy_stoplostprofit_mobileaverage.jsp";
		// TODO Auto-generated constructor stub
	}

	public boolean isActive() {
		// TODO Auto-generated method stub
		return (this.getActive().equals(new  Integer(1)));
	}

	@Override
	
	
	/* ABRIMOS DOS, UNA DE CIERRE Y OTRA PARA CONFIRMAR EL CAMBIO DE SEÑAL */
	
	
	public boolean ExecuteSimulation(Market oMarket, Share Share,
			Calendar SimulationDate) {
			
	try
        {							
			// hace falta???????? ..creo que si, para tener control sobre la operacion de compra /venta 

			SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMM");
						
			String _OperationTYPE = "";
			double ValueLimit =0.0;
			// MyActualPosition contiene la operacion abierta.
			// controlamos los limites de salida con beneficio
			
			
			// 04.02.2013...Descartados los precios limitados en los cierres --> a mercado
			
			if (MyActualSimulationPosition.getType().equals(PositionStates.statusTWSFire.BUY.toString())) // operacion de compra normal abierta
			{
				_OperationTYPE = PositionStates.statusTWSFire.SELL.toString();
				
				
			}
			else  // venta inicial
			{
				_OperationTYPE = PositionStates.statusTWSFire.BUY.toString();
				
			}						
						
			MyActualSimulationPosition.setPrice_sell(ValueToSell);
			MyActualSimulationPosition.setState_sell(PositionStates.statusTWSCallBack.Filled.toString());
			MyActualSimulationPosition.setState(PositionStates.status.SELL_OK.toString());
			
				
			MyActualSimulationPosition.setDate_sell(new Timestamp(EntrySimulationDate.getTime()));
						  
			//MyActualSimulationPosition.setDescription(StrategyStopLostProfit_MobileAverage.class.getName());
			MyActualSimulationPosition.setStrategyID_sell(ConfigKeys.STRATEGY_SELL_AVGMOBILE_8_PERIODS_5_MINBAR);
			
			
			/* ACTUALIZAMOS LA OPERACION CON LOS DATOS DE LA VENTA. ACTUALIZAMOS MAS EN EL ORDER STATUS */
			SimulationPositionDAO.updatePositionByPositionID(MyActualSimulationPosition);
			
			
		
			/* ABRIMOS SEGUNDA SI NO HAY CIERRE DE MERCADO */
			
			Calendar _tradeDate =  Calendar.getInstance();
			
			_tradeDate.setTimeInMillis(EntrySimulationDate.getTime());
			
			Calendar calFechaFinMercado = Utilidades.getNewCalendarWithHour(_tradeDate, oMarket.getEnd_hour());
			
			if (!calFechaFinMercado.before(_tradeDate))
			{
						
			
			/* Posicion en MYSQL de CONTROL. OJO...ANTES SIEMPRE PARA DESPUES CONTROLARLA EN CASO DE ERROR. */
			SimulationPosition BuyPositionSystem = new SimulationPosition();
							
			BuyPositionSystem.setPrice_buy(ValueToSell);  // ojo, es estimativo
			BuyPositionSystem.setDate_buy(new Timestamp(EntrySimulationDate.getTime()));			
			BuyPositionSystem.setShare_number(Share.getNumber_purchase());
			BuyPositionSystem.setShareID(Share.getShareId());		
			BuyPositionSystem.setState(PositionStates.status.BUY_OK.toString());					
			BuyPositionSystem.setState_buy(PositionStates.statusTWSCallBack.Filled.toString() );
			
			/* CONTRARIA AL CIERRE */
			BuyPositionSystem.setType(_OperationTYPE);						
			//BuyPositionSystem.setRealtimeID_buy_alert(RealTimeID);
			BuyPositionSystem.setStrategyID_buy(ConfigKeys.STRATEGY_BUY_AVGMOBILE_8_PERIODS_5_MINBAR);
			//BuyPositionSystem.setSell_percentual_stop_lost(Share.getSell_percentual_stop_lost());
			//BuyPositionSystem.setSell_percentual_stop_profit(Share.getSell_percentual_stop_profit());
			BuyPositionSystem.setShare_number_traded(new Long(0));
			BuyPositionSystem.setShare_number_to_trade(Share.getNumber_purchase()); 
			
			BuyPositionSystem.setSimulationID(SimulationDAO.getActiveSimulation().getSimulationID());
			//BuyPositionSystem.setTicketTWS_buy(new Long(TicketTWSToBuy));
			SimulationPositionDAO.addPosition(BuyPositionSystem);
			
		} /* ABRIMOS SEGUNDA SI NO HAY CIERRE DE MERCADO */

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
	public boolean VerifySimulation(Share ShareStrategy, Market oMarket, Calendar _oSimulationDate ) {

		boolean verified = false; 
		
		LogTWM.getLog(StrategyStopLostProfit_MobileAverage.class);									
		//LogTWM.log(Priority.INFO, ShareStrategy.getName() );		
		
		SimpleDateFormat _sdf =new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		_FULL_DAY_SCANNED = false;
		
		boolean _DEBUG = true;
		
		try
        {
				
			/*Calendar calFechaFinMercado = Utilidades.getNewCalendarWithHour(oMarket.getEnd_hour());
		 Calendar calFechaIniMercado = Utilidades.getNewCalendarWithHour(oMarket.getStart_hour());
	*/

		Calendar calFechaFinMercado = Utilidades.getNewCalendarWithHour(_oSimulationDate, oMarket.getEnd_hour());
		Calendar calFechaIniMercado = Utilidades.getNewCalendarWithHour(_oSimulationDate, oMarket.getStart_hour());
			
			
		
	//	LogTWM.info("Share:" + ShareStrategy.getSymbol() + ",calFechaIniMercado:" + _sdf.format(calFechaIniMercado.getTime()));
		
		boolean _macdParamsOK= true;
		
		try 
		{
			_num_macdP = Integer.parseInt(this.getMacd_periods().toString());
			_num_macdT = Integer.parseInt(this.getMcad_timebars().toString());
			_entryrate = Float.parseFloat(this.getMcad_rateavg_entry().toString());
			
		}
		catch (Exception e)
		{
			_macdParamsOK = false;
			
		}
		
		Position OperationOpen = null;
	//	java.util.List<Position> OperationsOpen = null;
		
		if (SimulationPositionDAO.ExistsPositionShareOpen(ShareStrategy.getShareId().intValue()))
		{
			lMyActualSimulationPosition= (List<SimulationPosition>) SimulationPositionDAO.getPositions(ShareStrategy.getShareId().intValue(), true);
			if (lMyActualSimulationPosition!=null && lMyActualSimulationPosition.size()>0)
			{
				MyActualSimulationPosition = lMyActualSimulationPosition.get(0);
			}
		}
		else
			return false;
		
		
		
		/*TODOS LOS DATOS CORRECTOS, 
		 * BUSCAMOS UNA BARRA DE CIERRE  */
		
		
		/* COMPRA SIMULADA DE ENTRADA */ 
		Calendar _oDateFrom  =  Calendar.getInstance();
		Calendar _oDateTo  =  Calendar.getInstance();
		
		
		_oDateTo.setTimeInMillis(MyActualSimulationPosition.getDate_buy().getTime());
		_oDateFrom.setTimeInMillis(MyActualSimulationPosition.getDate_buy().getTime());
		_oDateTo.set(Calendar.SECOND, 0); 
		
		int j=0;
		while (true)
		{
			
			 //int MinutesAdd = j*_num_macdT;
			_oDateFrom.add(Calendar.MINUTE, _num_macdT);
			_oDateTo.add(Calendar.MINUTE, _num_macdT);
			
			
			
	//		LogTWM.info(_sdf.format(_oDateFrom.getTime()) + "," + _sdf.format(_oDateTo.getTime()));
			
		
			
			verified = _VerifyStrategyStopLost_MobileAvgSimulation(calFechaIniMercado, _oDateFrom, _oDateTo, ShareStrategy, oMarket, MyActualSimulationPosition);
			
			/* OJO, ACABA EL CIERRE DE MERCADO, COGEMOS LA ULTIMA BARRA */
			
			
			if (verified ||  calFechaFinMercado.before(_oDateFrom))						
			{	
				
				this.EntrySimulationDate = new Timestamp(_oDateFrom.getTimeInMillis());
				verified =true;
				if (calFechaFinMercado.before(_oDateFrom))
				{	
						RealTime ExitPrice = null;	
						// ACABA, NO HAY PRECIO, BUSCAMOS EL PRECIO DEL TRAMO FINAL.
					     ExitPrice = (RealTime) RealTimeDAO.getLastRealTimeLessThanDateSimulated(ShareStrategy.getShareId().intValue(), new Timestamp(_oDateTo.getTimeInMillis()));
					     if (ExitPrice!=null)
					    	 	this.ValueToSell = ExitPrice.getValue();
					
						_FULL_DAY_SCANNED=true;
				}		
				break;
			}
			j+=1;
		
	
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

	public boolean Verify(Share ShareStrategy, Market oMarket) {
		// TODO Auto-generated method stub
		
		boolean _verified = false;
		
		boolean _DEBUG =false;
		try
        {
			
		LogTWM.getLog(StrategyStopLostProfit_MobileAverage.class);			
			
		if (PositionDAO.ExistsPositionToSell(ShareStrategy.getShareId().intValue()))			
		{
				// supuestamente una abierta.
				lMyActualPosition = (List<Position>) PositionDAO.getPositions(ShareStrategy.getShareId().intValue(), true);
				if (lMyActualPosition!=null && lMyActualPosition.size()>0)   // operacion abierta
				{
					MyActualPosition = lMyActualPosition.get(0);
					/* CONTROLAMOS EL BENEFICIO */
					
					String HoraActual = Utilidades.getActualHourFormat();
					
					/* supongamos cierre mercado a las 2200 */
					/* HoraDeadLineToClose sera la hora actual mas los minutos de la estrategia */
					Calendar calFechaActualWithDeadLine = Utilidades.getNewCalendarWithHour(HoraActual);
					
					/* CONTROLAMOS QUE NO SEA NULL */
					if (this.getSell_all_deadline_min_toclose()!=null)
					{
						calFechaActualWithDeadLine.add(Calendar.MINUTE,this.getSell_all_deadline_min_toclose());	
					}
					
					
					Calendar calFechaFinMercado = Utilidades.getNewCalendarWithHour(oMarket.getEnd_hour());
					
					Calendar calFechaIniMercado = Utilidades.getNewCalendarWithHour(oMarket.getStart_hour());
					
					
					boolean _macdParamsOK= true;
					

					int _num_macdP = 0;   // Periodos
					int _num_macdT = 0;   // Tiempo de barras
					float _entryrate=0;// Umbral de superacion
							  
					
					try 
					{
						_num_macdP = Integer.parseInt(this.getMacd_periods().toString());
						_num_macdT = Integer.parseInt(this.getMcad_timebars().toString());
						_entryrate = Float.parseFloat(this.getMcad_rateavg_entry().toString());
						
					}
					catch (Exception e)
					{
						_macdParamsOK = false;
						
					}
					
					
					/* SOLO PODEMOS ENTRAR EN EL PERIODO MARCADO DE CADA MINUTO, PARA LO CUAL OBTENEMOS EL RESTO */					
					int _ModMinuteToEntry =calFechaActualWithDeadLine.get(Calendar.MINUTE) % _num_macdT;
					if (_ModMinuteToEntry!=0)  // NO ESTOY EN EL MINUTO 5,10,15,20..etc (para las barras de 5)
						return false;
					
					/* NO ES EL SIGUIENTE BLOQUE DE BARRA , PODRIAN SOLAPARSE ENTRADA/SALIDA, IGNORAMOS SEGUNDOS DE LA TABLA DE BBDD */
					
					Calendar calFechaEntradaCompra  = Calendar.getInstance();
					calFechaEntradaCompra.setTimeInMillis(MyActualPosition.getDate_real_buy().getTime());
					calFechaEntradaCompra.set(Calendar.SECOND, 0);
					
					if (!calFechaActualWithDeadLine.after(calFechaEntradaCompra))
						return false;
					
					
					/*  CONTROLAMOS EL DEADLINE PARA COMPRAR */ 
					
					// verificamos compra previa. No compramos si hay una compra previa y estamos en el margen de tiempo de compra.
					// verificamos si ha pasado el tiempo necesario con los offset de lectura 
					
					if (!_macdParamsOK)   // hora actyual ya ha pasado, podemos entrar en la operativa
						return false;
					
					Calendar _oDateFrom= Calendar.getInstance(); 
					Calendar _oDateTo = Calendar.getInstance();
					
					_oDateFrom.set(Calendar.SECOND, 0);
					_oDateTo.set(Calendar.SECOND, 0);
					
					if (calFechaIniMercado.after(_oDateFrom))
						return false;
					
					_verified = _VerifyStrategyStopLost_MobileAvg(calFechaIniMercado, _oDateFrom, _oDateTo, ShareStrategy, oMarket, MyActualPosition);	
						
				}
				
		}
			
			
			
		
		
        }
		catch (Exception er)
        {
			LogTWM.log(Priority.ERROR, er.getMessage());
			er.printStackTrace();	
			_verified = false;
        }

		return _verified;
	}
	
	public boolean Execute(Market oMarket, Share ShareStrategy, TIMApiGITrader oTIMApiWrapper) {
		// TODO Auto-generated method stub
		try
        {							
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

			/* ESTO HACE FALTA?????? */
			OrderDAO.addOrder(oTIMApiWrapper.getLastPositionID(), ShareStrategy.getShareId().intValue());

			// colocamos operacion de venta			
			Order BuyPositionTWS = new Order();
			
			String _OperationTYPE = "";
			double ValueLimit =0.0;
			// MyActualPosition contiene la operacion abierta.
			// controlamos los limites de salida con beneficio
			
			
			// 04.02.2013...Descartados los precios limitados en los cierres --> a mercado
			
			if (MyActualPosition.getType().equals(PositionStates.statusTWSFire.BUY.toString())) // operacion de compra normal abierta
			{
				_OperationTYPE = PositionStates.statusTWSFire.SELL.toString();
				
				
			}
			else  // venta inicial
			{
				_OperationTYPE = PositionStates.statusTWSFire.BUY.toString();
				
			}						
			
			///LogTWM.log(Priority.INFO, "Stop de " + _OperationTYPE);
			
			BuyPositionTWS.account( this.getACCOUNT_NAME());
			
			BuyPositionTWS.totalQuantity(MyActualPosition.getShare_number_to_trade().intValue());
			BuyPositionTWS.orderType(PositionStates.ordertypes.MKT.toString());		
			
			BuyPositionTWS.action(_OperationTYPE);   				

			
			int LastPositionID = Utilidades.LastPositionIDTws(oTIMApiWrapper);
			
			MyActualPosition.setPositionID_tws_sell(new Long(LastPositionID));			
			MyActualPosition.setPrice_sell(ValueToSell);
			MyActualPosition.setState_sell(PositionStates.statusTWSCallBack.PendingSubmit.toString());
			/* si metemos el date sell en las parciales, no entran las siguientes */
			/* acumulo las acciones vendidas y a vender en la operativa */
			int _RemaingShares = MyActualPosition.getShare_number_to_trade().intValue() +MyActualPosition.getShare_number_traded().intValue();
			int _TotalShares = MyActualPosition.getShare_number().intValue();
			if (_RemaingShares==_TotalShares)  // ultimo paquete de acciones
			{	
				MyActualPosition.setDate_sell(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			}
			MyActualPosition.setRealtimeID_sell_alert(RealTimeID);			  
			MyActualPosition.setDescription(StrategyStopLostProfit_MobileAverage.class.getName());
			MyActualPosition.setStrategyID_sell(ConfigKeys.STRATEGY_SELL_AVGMOBILE_8_PERIODS_5_MINBAR);
			
			
			/* ACTUALIZAMOS LA OPERACION CON LOS DATOS DE LA VENTA. ACTUALIZAMOS MAS EN EL ORDER STATUS */
			PositionDAO.updatePositionByPositionID(MyActualPosition);
			
		
			
			// LANZAMOS OPERACION DE VENTA.
			oTIMApiWrapper.GITraderOpenOrder(LastPositionID, oContrat, BuyPositionTWS);
			
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

	private boolean _VerifyStrategyStopLost_MobileAvg(Calendar IniMarket, Calendar _oDateFrom, Calendar _oDateTo,
			Share ShareStrategy, Market oMarket , Position ActualPos)
	
	{
		return _getVerifyStrategyStopLost_MobileAvg(IniMarket, _oDateFrom, _oDateTo,
				 ShareStrategy, oMarket, ActualPos, false);
	}
	
	private boolean _VerifyStrategyStopLost_MobileAvgSimulation(Calendar IniMarket, Calendar _oDateFrom, Calendar _oDateTo,
			Share ShareStrategy, Market oMarket, Position ActualPos)
	
	{
		return _getVerifyStrategyStopLost_MobileAvg(IniMarket, _oDateFrom, _oDateTo,
				 ShareStrategy, oMarket, ActualPos,true);
	}
	


	private boolean _getVerifyStrategyStopLost_MobileAvg(Calendar IniMarket, Calendar _oDateFrom, Calendar _oDateTo,
			Share ShareStrategy, Market oMarket, Position ActualPosition, boolean Simulation)
	
	{

	boolean _DEBUG = true;
	
	
	
	boolean _verified = false;
	
	SimpleDateFormat _sdf =new SimpleDateFormat("dd/MM/yyyy HH:mm");
	
	/* boolean ExistsPositionToSell = false;
	if (Simulation)
		ExistsPositionToSell = SimulationPositionDAO.ExistsPositionToSell(ShareStrategy.getShareId().intValue());
	else
		
		
	if (SimulationPositionDAO.ExistsPositionToSell(ShareStrategy.getShareId().intValue()))			
	{
			// supuestamente una abierta.
			lMyActualPosition = (List<Position>) SimulationPositionDAO.getPositions(ShareStrategy.getShareId().intValue(), true);
			if (lMyActualPosition!=null && lMyActualPosition.size()>0)   // operacion abierta
			{
				MyActualPosition = lMyActualPosition.get(0);
			}
	}
		
	if (lMyActualPosition==null || MyActualPosition==null )
		return false;
	*/
	RealTime _avgMobileSimple = MobileAvgUtil.getAvgMobileMM8(_oDateFrom, _oDateTo, _num_macdP, _num_macdT, ShareStrategy.getShareId().intValue(), Simulation) ;
	

    
	/* if (_DEBUG)
	{
		LogTWM.info(_sdf.format(_oDateFrom.getTime()));
		LogTWM.info(_sdf.format(_oDateTo.getTime())); 
	}*/
		
	
	/* VERIFICAMOS QUE EL DATO DE VUELTA DE LA MEDIA ESTA OBTENIDO DEL NUMERO DE PERIODOS SOLICITADOS */ 
	if (_avgMobileSimple!=null && _avgMobileSimple.getRealtimeID().equals(new Long(_num_macdP)))
	{
		// todo fue bien, tenemos media movil y de los periodos solicitados.
		// buscamos el cierre de la barra, ultimo valor < que el MINUTE.00  (15.00, 20,00, 25.00)
		RealTime oRTimeEnTramo = null;
		
		/* Calendar DateBarTo = Calendar.getInstance();
		DateBarTo.setTimeInMillis(_oDateTo.getTimeInMillis());
		DateBarTo.add(Calendar.MINUTE, this.getMcad_timebars());*/
		
		if (Simulation)			
			 oRTimeEnTramo =  (RealTime) RealTimeDAO.getLastRealTimeLessThanDateSimulated(ShareStrategy.getShareId().intValue(), new Timestamp(_oDateTo.getTimeInMillis()));
		else
			oRTimeEnTramo = (RealTime) RealTimeDAO.getLastRealTimeLessThanDate(ShareStrategy.getShareId().intValue(), new Timestamp(_oDateTo.getTimeInMillis()));
		
		//RealTime oRTimeEnTramo = (RealTime) RealTimeDAO.getLastRealTimeLessThanDate(ShareStrategy.getShareId().intValue(), new Timestamp(_oDateTo.getTimeInMillis()));
		/* SUPERA EL TIEMPO REAL LA MEDIA MOVIL CON EL PORCENTAJE DEL UMBRAL ???  */
		
//		LogTWM.info("Periodos:" + _num_macdP + ",Accion:" + ShareStrategy.getName() + ",Inicio:" + _sdf.format(_oDateFrom.getTimeInMillis()) + ",Fin:" + _sdf.format(_oDateTo.getTimeInMillis()));
		
		/* Cuando al cierre de una barra el precio rompa por encima de la mediamovil de x períodos 
		 * y se sitúe por encima en el % del rango de la misma, 
		 * se genera una orden de compra */
		 
		if (oRTimeEnTramo!=null)  // Tiempo real ok
		{
			
			Calendar _TimeBarWidthFrom = Calendar.getInstance(); 
			_TimeBarWidthFrom.setTimeInMillis(_oDateTo.getTimeInMillis());		
					
			Calendar _TimeBarWidthTo = Calendar.getInstance();
			_TimeBarWidthTo.setTimeInMillis(_oDateTo.getTimeInMillis());							 
			_TimeBarWidthFrom.add(Calendar.MINUTE, -_num_macdT);
			_TimeBarWidthTo.add(Calendar.SECOND, -1);
			
			 /* RealTime oRTimeWidthRange = (RealTime) RealTimeDAO.getRealTimeBetweenDates(ShareStrategy.getShareId().intValue(), new Timestamp(_TimeBarWidthFrom.getTimeInMillis()), 
					new Timestamp(_TimeBarWidthTo.getTimeInMillis())); */ 	
			
			RealTime oRTimeWidthRange = MobileAvgUtil.getMinMaxBarFromShare(_oDateTo,  _num_macdT, ShareStrategy.getShareId().intValue(), Simulation) ;
			
			
			if (oRTimeWidthRange!=null && oRTimeWidthRange.getMax_value()!=null 
					&& oRTimeWidthRange.getMin_value()!=null)    // hay ancho de barra
			{
				
				/* RAGO DE LA barra */
				float _WidthRangeBar = oRTimeWidthRange.getMax_value().floatValue() - oRTimeWidthRange.getMin_value().floatValue(); 

				/* PARA SALIR, QUE LA BARRA ESTE POR DEBAJO O ENCIMA AL 100% DE LA MEDIA MOVIL */
			
				
				boolean _AvgMovil_InsideBar  = (oRTimeWidthRange.getMax_value().floatValue()> _avgMobileSimple.getValue().floatValue() &&
									oRTimeWidthRange.getMin_value().floatValue()< _avgMobileSimple.getValue().floatValue());
				
				boolean _BuySuccess = false;
				boolean _SellSuccess = false;
				
				float _WidthBarRangePercent =  _WidthRangeBar *  _entryrate;
				
				
				/* hay cambio de tendencia en caso de que no corte la  barra? 
				 * 
				 *  DNM_RAFADIESTRO  2015 06 16. COMENTAMOS LA CLAUSULA DE QUE NO LA CORTE  
				 *  */
				
				
				boolean _MarketTrendChanged = true;
				if (ActualPosition.getType().equals(PositionStates.statusTWSFire.BUY.toString()) && 
						oRTimeEnTramo.getValue().floatValue() >_avgMobileSimple.getValue().floatValue())
						{
								_MarketTrendChanged = false;
						}
				if (ActualPosition.getType().equals(PositionStates.statusTWSFire.SELL.toString()) && 
								oRTimeEnTramo.getValue().floatValue() <_avgMobileSimple.getValue().floatValue())
						{
								_MarketTrendChanged = false;
						}
				

				
				_BuySuccess = MobileAvgUtil._IsBuySignalMM8_5MINBar(_avgMobileSimple, oRTimeWidthRange, _WidthBarRangePercent, oRTimeEnTramo);
				
				_BuySuccess =  (_BuySuccess && ActualPosition.getType().equals(PositionStates.statusTWSFire.SELL.toString()));
				
				_SellSuccess = MobileAvgUtil._IsSellSignalMM8_5MINBar(_avgMobileSimple, oRTimeWidthRange, _WidthBarRangePercent, oRTimeEnTramo);

				_SellSuccess = (_SellSuccess && ActualPosition.getType().equals(PositionStates.statusTWSFire.BUY.toString()));
				

				/*  DNM_RAFADIESTRO  2015 06 16. COMENTAMOS LA CLAUSULA DE QUE NO LA CORTE */ 
				 if  ((!_AvgMovil_InsideBar  &&_MarketTrendChanged) 
					 	||  (_AvgMovil_InsideBar && (_SellSuccess || _BuySuccess)))
				  								
				//if (_AvgMovil_InsideBar && (_SellSuccess || _BuySuccess))
				{
					
					if (_DEBUG)
					{
						LogTWM.info("_AvgMovil_InsideBar:" + _AvgMovil_InsideBar + "MediaMovil:" + _avgMobileSimple.getValue() + ",Periodos:" + _num_macdP + ",Accion:" + ShareStrategy.getName() + ",Inicio:" + _sdf.format(_oDateFrom.getTimeInMillis()) + ",Fin:" + _sdf.format(_oDateTo.getTimeInMillis())
								 + "Compra:" + _BuySuccess + ",Venta:" + _SellSuccess + ",CierreBarra:" + oRTimeEnTramo.getValue().floatValue() + "," 
								 + ",MaxBarra:" + oRTimeWidthRange.getMax_value().floatValue() + ",MinBarra:" + oRTimeWidthRange.getMin_value().floatValue() + ",EntryRate:" +  _entryrate + ",InicioBarra:" + _sdf.format(_TimeBarWidthFrom.getTimeInMillis())
								 + ",FinBarra:" + _sdf.format(_TimeBarWidthTo.getTimeInMillis()));
					}
					
					
					/* SE VENDE A MERCADO, NO TIENE SENTIDO PRECIO DE SALIDA */
					ValueToSell = oRTimeEnTramo.getValue();
					RealTimeID  = oRTimeEnTramo.getRealtimeID();
					_verified = true;
					
					
				}
			}// fin hay ancho de barra
		}
		
		
		
	} 

	return _verified;


}
	
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	 //List<Position> Lista = PositionDAO.getTradingPositions(new Timestamp(Calendar.getInstance().getTimeInMillis()));

	SimpleDateFormat _sdf =new SimpleDateFormat("dd/MM/yyyy HH:mm");

	LogTWM.getLog(StrategyStopLostProfit_MobileAverage.class);		
	
	Share Test = ShareDAO.getShare("ES");
	Market TestMarket = MarketDAO.getMarketByName("EEUU");
	
	Calendar TestDate = Calendar.getInstance();
	
	TestDate.set(2014, 10, 6, 15,15,0);   // noviembre 6 15 15
	
	LogTWM.info(_sdf.format(TestDate.getTime()));
	
	StrategyStopLostProfit_MobileAverage _this = new StrategyStopLostProfit_MobileAverage();
	
	
	_this.setMacd_periods(8);
	_this.setMcad_rateavg_entry(0.75f);
	_this.setMcad_timebars(5);
	
	LogTWM.info("Una vez que esta a true el verified, llamamos a ExecutionSimulation y despues a la estrategia de salida para verificar." +
			"Hasta que no salga que no siga la simulación en los siguientes minutos/horas/dias");
	
	
	
	
	boolean verified = _this.VerifySimulation(Test, TestMarket, TestDate );
	
	if (verified)
	{
		_this.ExecuteSimulation(TestMarket, Test, TestDate );
	}
	
	TimeZone tz = TimeZone.getTimeZone("GMT+1");
	
	Calendar cal = Calendar.getInstance();
	
	Calendar cal2 = Calendar.getInstance();
	cal2.setTimeInMillis(System.currentTimeMillis());
	
	cal.setTimeZone(tz);
	cal2.setTimeZone(tz);
	
	
	
	
	LogTWM.info(_sdf.format(new Date()));
	LogTWM.info(_sdf.format(cal.getTimeInMillis()));
	LogTWM.info(_sdf.format(cal2.getTime()));
	
	
	}

	
}
	
