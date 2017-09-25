/*La idea es automatizar este modelo sobre ESZ4, actual vencimiento diciembre del futuro SP500, este campo Symbol cambiará cada trimestre igual que en el anterior desarrollo.

- Se debe capturar el tiempo real tick desde las 15:00 hora España hasta el cierre a 22:00
  en período temporal 5 minutos, donde la primera captura será el precio que refleje a las 15:00:00,  el segundo 15:05:00, tercero 15:10:00 y sucesivos.

- A estas capturas de 5 minutos le aplicamos una media móvil simple de 8 períodos.

- Cuando al cierre de una barra el precio rompa por encima de la mediamovil de 8 períodos y se sitúe por encima en el 75% del rango de la misma, se genera una orden de compra de 1 Futuro a mercado "Market". Y a al inversa cuando la barra perfore la media, venderá.

- El stop se ejecuta cuando una barra cierre en su 100% por debajo de la media movil.

- Cuando alcance 10 puntos de beneficio se ejecuta stop profit, aquí genera un campo donde el trader pueda modificar esta variable.

- Por ejemplo si se combra a 1980 y se vende a 1990, en este precio haría 2 contratos, 1 para cerrar la compra en 80 y otro abre un corto en 90.

- Es posible que debamos limitar el número de operaciones que realice en el día, tenlo en cuenta para crear un contador.

- No necesitamos base de datos anteriores.
*/

package com.tim.model;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import  com.tim.util.MobileAvgUtil;

import org.apache.log4j.Priority;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.Util;
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
import com.tim.dao.SimulationDAO;
import com.tim.dao.SimulationPositionDAO;
import com.tim.service.TIMApiGITrader;
import com.tim.util.ConfigKeys;
import com.tim.util.LogTWM;
import com.tim.util.PositionStates;
import com.tim.util.TWSMail;
import com.tim.util.Utilidades;

public class  StrategySimpleMobileAverage  extends Strategy  {

	
	/* DATOS CALCULADOS INTERNOS DE LA ESTRATEGIA. HABRA OTROS PROPIOS DE LA ESTRATEGIA GENERAL. */
	private Double ValueToBuy =0.0;
	private Double avgMobileValue =0.0;
	private boolean bSellOperation;
	private boolean bBuyOperation;
	
	private Timestamp EntrySimulationDate;
	
	
	
	int _num_macdP = 8;   // Periodos
	int _num_macdT = 5;   // Tiempo de barras
	float _entryrate=0.75f;    // Umbral de superacion
	
	
	
	@Override
	public boolean ExecuteSimulation(Market oMarket, Share Share,
			Calendar SimulationDate) {
			
				// TODO Auto-generated method stub
				try
			    {
					
					LogTWM.getLog(StrategySimpleMobileAverage.class);			
					LogTWM.log(Priority.INFO, "UserAccount:" +  this.getACCOUNT_NAME() + ",detectada posible entrada SIMULADA de " + Share.getName() +  "Tick:" + Share.getSymbol() + ",PrecioCompra:" + ValueToBuy);
					
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
					
					/* Posicion en MYSQL de CONTROL. OJO...ANTES SIEMPRE PARA DESPUES CONTROLARLA EN CASO DE ERROR. */
					SimulationPosition BuyPositionSystem = new SimulationPosition();
									
					BuyPositionSystem.setPrice_buy(ValueToBuy);  // ojo, es estimativo
					BuyPositionSystem.setDate_buy(EntrySimulationDate);					
					BuyPositionSystem.setShare_number(Share.getNumber_purchase());
					BuyPositionSystem.setShareID(Share.getShareId());		
					BuyPositionSystem.setState(PositionStates.status.BUY_OK.toString());					
					BuyPositionSystem.setState_buy(PositionStates.statusTWSCallBack.Filled.toString() );
					
					BuyPositionSystem.setType(PositionStates.statusTWSFire.BUY.toString());			
					if (bSellOperation)
					{
						BuyPositionSystem.setType(PositionStates.statusTWSFire.SELL.toString());
					}
					//BuyPositionSystem.setRealtimeID_buy_alert(RealTimeID);
					BuyPositionSystem.setStrategyID_buy(ConfigKeys.STRATEGY_BUY_AVGMOBILE_8_PERIODS_5_MINBAR);
					//BuyPositionSystem.setSell_percentual_stop_lost(Share.getSell_percentual_stop_lost());
					//BuyPositionSystem.setSell_percentual_stop_profit(Share.getSell_percentual_stop_profit());
					BuyPositionSystem.setShare_number_traded(new Long(0));
					BuyPositionSystem.setShare_number_to_trade(Share.getNumber_purchase()); 
					
					BuyPositionSystem.setSimulationID(SimulationDAO.getActiveSimulation().getSimulationID());
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
	
	

	public boolean VerifySimulation(Share ShareStrategy, Market oMarket, Calendar _oSimulationDate ) {
		boolean verified = false; 
		LogTWM.getLog(StrategySimpleMobileAverage.class);									
		//LogTWM.log(Priority.INFO, ShareStrategy.getName() );		
		
		SimpleDateFormat _sdf =new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");

		_FULL_DAY_SCANNED = false;
		boolean _DEBUG = true;
		
		try
        {
			
		
		
			
		/* Calendar calFechaFinMercado = Utilidades.getNewCalendarWithHour(oMarket.getEnd_hour());
		Calendar calFechaIniMercado = Utilidades.getNewCalendarWithHour(oMarket.getStart_hour());
		*/

		Calendar calFechaFinMercado = Utilidades.getNewCalendarWithHour(_oSimulationDate, oMarket.getEnd_hour());
		Calendar calFechaIniMercado = Utilidades.getNewCalendarWithHour(_oSimulationDate, oMarket.getStart_hour());
			
		
		
		/* LogTWM.info("calFechaIniMercado:" + _sdf.format(calFechaIniMercado.getTime()));
		LogTWM.info("calFechaFinMercado:" + _sdf.format(calFechaFinMercado.getTime())); */
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
		
		Calendar _oDateFrom=Calendar.getInstance();
		Calendar _oDateTo=Calendar.getInstance();
		
		
		_oDateFrom.setTimeInMillis(calFechaIniMercado.getTimeInMillis());
		_oDateTo.setTimeInMillis(calFechaIniMercado.getTimeInMillis());;
		
		_oDateFrom.set(Calendar.SECOND, 0);
		_oDateTo.set(Calendar.SECOND, 0);
		
		// recorremos el tramo del dia en bloques de 5.
		// 	while (true)
		
		//  no hay posicion abierta?
		
		List<Position> SimulationOperation = SimulationPositionDAO.getTradingPositionsByDate(ShareStrategy.getShareId().intValue(), new Timestamp(_oSimulationDate.getTimeInMillis()),SimulationDAO.getActiveSimulation().getSimulationID().intValue());

		//LogTWM.info("ExistsPositionShareOpen  "  + SimulationPositionDAO.ExistsPositionShareOpen(ShareStrategy.getShareId().intValue()));
		
		if (SimulationPositionDAO.ExistsPositionShareOpen(ShareStrategy.getShareId().intValue()))
			return false;
		
		if (SimulationOperation!=null && SimulationOperation.size()>0)
		{	
			
			//List<Position> SimulationOperation = SimulationPositionDAO.getTradingPositions(ShareStrategy.getShareId().intValue());
			if (SimulationOperation!=null && SimulationOperation.size()>0)
			{
				Position OperationOpen = SimulationOperation.get(0);
				
				_oDateFrom.setTimeInMillis(OperationOpen.getDate_sell().getTime());
				_oDateTo.setTimeInMillis(OperationOpen.getDate_sell().getTime());
				
				
			//	LogTWM.info("_oDateFrom POS:  "  + _sdf.format(_oDateFrom.getTime()));
				
			}
			else
				return false;
			
		}
		if (calFechaFinMercado.before(_oDateFrom))
		{	
			_FULL_DAY_SCANNED = true;
			return false;
		}
			
		
		
		/*_oDateFrom.set(Calendar.SECOND, 0);
		_oDateTo.set(Calendar.SECOND, 0);
		*/
		int j=0;
		while (true)
		{
			
			 //int MinutesAdd = j*_num_macdT;
			_oDateFrom.add(Calendar.MINUTE, _num_macdT);
			_oDateTo.add(Calendar.MINUTE, _num_macdT);
			
		//	LogTWM.info("Adding " + _num_macdT + " min:" + _sdf.format(_oDateTo.getTime()) + ",dateFrom:" +_sdf.format(_oDateFrom.getTime())  + ",calFechaFinMercado:" +_sdf.format(calFechaFinMercado.getTime()) );
			
			LogTWM.info("Estrategia Mobile Avg  Entrada : " +_sdf.format(_oDateTo.getTime()));
			
			if (calFechaFinMercado.before(_oDateFrom))
			{					
				_FULL_DAY_SCANNED = true;
				return false;
			}
			
			
			
			verified = _VerifyStrategySimpleMobileAvgSimulation(calFechaIniMercado, _oDateFrom, _oDateTo, ShareStrategy, oMarket);
			if (verified)
			{	
				this.EntrySimulationDate = new Timestamp(_oDateFrom.getTimeInMillis());
			//	LogTWM.info("Saliendo bucle de hora, entrada a " + _sdf.format(_oDateFrom.getTime()));
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

	
	
	private boolean verified;
	private Long RealTimeID=new Long(0); 
	
	
	private LogTWM MyLog;
	
	
	
	
	public StrategySimpleMobileAverage() {
		super();
		this.JSP_PAGE = "/jsp/admin/strategies/strategysimplemobileavg.jsp";
		// TODO Auto-generated constructor stub
	}

	public boolean isActive() {
		// TODO Auto-generated method stub
		return (this.getActive().equals(new  Integer(1)));

	}

	public boolean Verify(Share ShareStrategy, Market oMarket) {
		// TODO Auto-generated method stub
		
		boolean verified = false; 
		LogTWM.getLog(StrategySimpleMobileAverage.class);									
		//LogTWM.log(Priority.INFO, ShareStrategy.getName() );		
		
		boolean _DEBUG = true;
		
		try
        {
			
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
			
		/*  CONTROLAMOS EL DEADLINE PARA COMPRAR */ 
		
		// verificamos compra previa. No compramos si hay una compra previa y estamos en el margen de tiempo de compra.
		// verificamos si ha pasado el tiempo necesario con los offset de lectura 
		if (!PositionDAO.ExistsPositionShareOpen(ShareStrategy.getShareId().intValue()) && !calFechaActualWithDeadLine.after(calFechaFinMercado))		
		{	
			
			

			// HORA DE INICIO TRADING CON RESPECTO AL DESPLAZAMIENTO OffSet2 (hay offset para entrar cuando se quiera por accion)
			String HoraInicioTrading = Utilidades.getActualHourFormatPlusMinutes(oMarket.getStart_hour(), ShareStrategy.getOffset2min_read_from_initmarket());
			//String HoraFinLecturaMaxMin = Utilidades.getActualHourFormatPlusMinutes(oMarket.getStart_hour(), ShareStrategy.getOffset2min_read_from_initmarket());
			
			/* COMPROBAMOS ALGUN TIPO DE ERROR */
			if (HoraInicioTrading.contains("-1") )
			{
				LogTWM.log(Priority.ERROR, "[Estrategia:"  + StrategySimpleMobileAverage.class.getName() + "]: Errores formateando las horas de Max y Min de la acción. Hora[" + oMarket.getStart_hour()  + "], Offset1[");
				return false;
			}
			
			Calendar _oDateFrom= Calendar.getInstance(); 
			Calendar _oDateTo = Calendar.getInstance(); 	
			
			 
			if (HoraActual.compareTo(HoraInicioTrading)>0 && _macdParamsOK)   // hora actyual ya ha pasado, podemos entrar en la operativa
			{
				verified = _VerifyStrategySimpleMobileAvg(calFechaIniMercado, _oDateFrom, _oDateTo, ShareStrategy, oMarket);
				
				//return false;
			
			}
				
		      } // fin de verificacion de operacion de compra previa
			//}
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
			
			LogTWM.getLog(StrategySimpleMobileAverage.class);			
			
			LogTWM.log(Priority.INFO, "UserAccount:" +  this.getACCOUNT_NAME() + ",detectada posible entrada de " + ShareStrategy.getName() +  "Tick:" + ShareStrategy.getSymbol() + ",PrecioCompra:" + ValueToBuy + ",Min:");
			
			
			
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

			// colocamos operacion de compra			
			Order BuyPositionTWS = new Order();
			
			BuyPositionTWS.account(this.getACCOUNT_NAME());
			
			BuyPositionTWS.totalQuantity(ShareStrategy.getNumber_purchase().intValue());
			//BuyPositionTWS.m_orderType = PositionStates.ordertypes.LMT.toString();
			BuyPositionTWS.orderType(PositionStates.ordertypes.MKT.toString());	
						
			//BuyPositionTWS.m_allOrNone = true;		    				
			
			/* FORMATEAMOS A DOS CIFRAS */
			
			// precio del tick más o menos un porcentaje ...normalmente %1
			// ojo con los FUTUROS..llevan cambios porcentuales
			if (bBuyOperation) // corto			
				BuyPositionTWS.action( PositionStates.statusTWSFire.BUY.toString());			
			else
				BuyPositionTWS.action( PositionStates.statusTWSFire.SELL.toString());
			
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
			BuyPositionSystem.setType(BuyPositionTWS.getAction());
			BuyPositionSystem.setRealtimeID_buy_alert(RealTimeID);
			BuyPositionSystem.setStrategyID_buy(ConfigKeys.STRATEGY_BUY_AVGMOBILE_8_PERIODS_5_MINBAR);
			
			
    		/* 5 PUNTOS o 5%  POR DEFECTO EN LOS FUTUROS */			
    		double _defaultstop_percent = 0;
    		boolean _IsFuture = (ShareStrategy.getSecurity_type()!=null && ShareStrategy.getSecurity_type().equals(ConfigKeys.SECURITY_TYPE_FUTUROS) ? true : false);
    		if (_IsFuture)
    		{	    			    			
    			_defaultstop_percent = (5 * 100) / ValueToBuy / 100 ;   
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
	
	
private boolean getVerifiedStraSimMobileAvg(Calendar IniMarket, Calendar _oDateFrom, Calendar _oDateTo,
		Share ShareStrategy, Market oMarket, boolean Simulation)
{


	
	/* ESTABLECEMOS LA FECHA INICIO, RESTAMOS LOS MINUTOS * BARRAS 
	 *  SI SON LAS 15:45 Y NECESITO MEDIA DE 8 PERIODOS DE 5 MINUTOS CADA BARRA, LA FECHA INICIO 
	 *  SERIA LAS 15:05
	 * 15:05, 10,15,20,25,30,35,40
	 * 
	 *  PARA REDONDEAR EN EL BLOQUE MAS CERCANO ANTERIOR, COGEMOS EL RESTO Y RESTAMOS
//	 *  22:38  / MINUTOS , COGEMOS EL RESTO --> 22:35.
	 * */
						
	/* Calendar _oDateFrom= Calendar.getInstance(); 
	Calendar _oDateTo = Calendar.getInstance(); */
	
	 _oDateFrom.set(Calendar.SECOND, 0);
	_oDateTo.set(Calendar.SECOND, 0);
	
	
	/* if (IniMarket.after(_oDateFrom))
		return false;
	*/
	
	verified = false;
	
	SimpleDateFormat _sdf =new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
	
	RealTime _avgMobileSimple = MobileAvgUtil.getAvgMobileMM8(_oDateFrom, _oDateTo, _num_macdP, _num_macdT, ShareStrategy.getShareId().intValue(), Simulation) ;
	
	//RealTime _avgMobileSimple = RealTimeDAO.getAvgSimpleMobile(new Timestamp(_oDateFrom.getTimeInMillis()),new Timestamp(_oDateTo.getTimeInMillis()), ShareStrategy.getShareId().intValue(),_num_macdP , _num_macdT);
    
	/* VERIFICAMOS QUE EL DATO DE VUELTA DE LA MEDIA ESTA OBTENIDO DEL NUMERO DE PERIODOS SOLICITADOS */
	
	// if (_DEBUG)
	//	LogTWM.info("_avgMobileSimple:" + _avgMobileSimple + ",Share:" + ShareStrategy.getSymbol() + ",Fecha" + _sdf.format(_oDateTo.getTime()));
	
	if (_avgMobileSimple!=null && _avgMobileSimple.getRealtimeID().equals(new Long(_num_macdP)))
	{
		// todo fue bien, tenemos media movil y de los periodos solicitados.
		// buscamos el cierre de la barra, ultimo valor < que el MINUTE.00  (15.00, 20,00, 25.00)
		RealTime oRTimeEnTramo =  null;
		
		/* Calendar DateBarTo = Calendar.getInstance();
		DateBarTo.setTimeInMillis(_oDateTo.getTimeInMillis());
		DateBarTo.add(Calendar.MINUTE, this.getMcad_timebars());
		
		*/
		if (Simulation)			
			 oRTimeEnTramo =  (RealTime) RealTimeDAO.getLastRealTimeLessThanDateSimulated(ShareStrategy.getShareId().intValue(), new Timestamp(_oDateTo.getTimeInMillis()));
		else
			oRTimeEnTramo = (RealTime) RealTimeDAO.getLastRealTimeLessThanDate(ShareStrategy.getShareId().intValue(), new Timestamp(_oDateTo.getTimeInMillis()));
		/* SUPERA EL TIEMPO REAL LA MEDIA MOVIL CON EL PORCENTAJE DEL UMBRAL ???  */
	/*	if (_DEBUG)
			LogTWM.info("_avgMobileSimple:" + _avgMobileSimple.getValue());
			*/
		/* Cuando al cierre de una barra el precio rompa por encima de la mediamovil de x períodos 
		 * y se sitúe por encima en el % del rango de la misma, 
		 * se genera una orden de compra */
		 
		if (oRTimeEnTramo!=null)  // Tiempo real ok
		{
			
			/* FALTA, HAY QUE CALCULAR EL ANCHO (MAX,MIN, DE LA BARRA) Y APLICARLE EL entrydate */
			/* FROM : TIEMPO ACTUAL, ES DECIR, 15.00, 15.05, 15.10 -1 PERIODO */
			/* FROM : TIEMPO ACTUAL, ES DECIR, 15.00, 15.05, 15.10  */
		
			RealTime oRTimeWidthRange = MobileAvgUtil.getMinMaxBarFromShare(_oDateTo,  _num_macdT, ShareStrategy.getShareId().intValue(), Simulation) ;
			
			if (oRTimeWidthRange!=null && oRTimeWidthRange.getMax_value()!=null 
					&& oRTimeWidthRange.getMin_value()!=null)    // hay ancho de barra
			{
				
				/* RAGO DE LA barra */
				float _WidthRangeBar = oRTimeWidthRange.getMax_value().floatValue() - oRTimeWidthRange.getMin_value().floatValue(); 
				
				/* COMPRA , VENTA  ?? */
				/* RANGO MAYOR IGUAL QUE LA MEDIA MOVIL Y QUE EL CIERRE DE LA BARRA ESTE POR ENCIMA DEL
				 * 75% DEL RANGO DE LA MISMA, (MAX-MIN) * 0.75 +  MIN o - Max
				 *   oRTimeEnTramo = PRECIO CIERRE
				 *   
				 *   
				 *   
				 *    */
				
			
				/* VARIABLE PARA CONTROLAR QUE SI LA BARRA NO CORTE LA MM, NOS ASEGUREMOS QUE LA
				 * BARRA N-1 SI LA CORTE 
				 */
				
				boolean _AvgMovil_InsideBar_N_1 = false;
				
				boolean _AvgMovil_InsideBar  = (oRTimeWidthRange.getMax_value().floatValue()> _avgMobileSimple.getValue().floatValue() &&
									oRTimeWidthRange.getMin_value().floatValue()< _avgMobileSimple.getValue().floatValue());
				
				/* si no la corta 
				 * 
				 * DNM_RAFADIESTRO  2015 06 16. COMENTAMOS LA CLAUSULA DE QUE NO LA CORTE
				 *  
				if (!_AvgMovil_InsideBar)
				{
					RealTime _avgMobileSimple_N_1 = MobileAvgUtil.getAvgMobileMM8(_oDateFrom, _oDateTo, _num_macdP, _num_macdT, ShareStrategy.getShareId().intValue(),1, Simulation ) ;
					RealTime oRTimeWidthRange_N_1 = null;
																
					if (_avgMobileSimple_N_1!=null) // hay MEDIA MOVIL?
					{
						oRTimeWidthRange_N_1 = MobileAvgUtil.getMinMaxBarFromShare(_oDateTo,  _num_macdT, ShareStrategy.getShareId().intValue(),1, Simulation ) ;
																		
																		
						_AvgMovil_InsideBar_N_1  = (oRTimeWidthRange_N_1.getMax_value().floatValue()> _avgMobileSimple_N_1.getValue().floatValue() &&
								oRTimeWidthRange_N_1.getMin_value().floatValue()< _avgMobileSimple_N_1.getValue().floatValue());													
				     } 
				}
				*/
				
				boolean _BuySuccess = false;
				boolean _SellSuccess = false;
				
				// que no entre si es la primera posicion y viene de una mecanismo de continuidad de la señal anterior
				boolean _bPositionVerified = true;
				
				float _WidthBarRangePercent =  _WidthRangeBar *  _entryrate;
				
				
					//_BuySuccess = (oRTimeEnTramo.getValue().floatValue()>= _avgMobileSimple.getValue().floatValue()  &&
					//		oRTimeEnTramo.getValue().floatValue() >= (oRTimeWidthRange.getMin_value().floatValue() + (_WidthRangeBar *  _entryrate)));
					
					/* A/ La barra debe cruzar la MM8 y al cierre el 75% de su cuerpo debe ser superior al precio cierre de la MM.
					y B/ Además, el precio cierre de la barra será => que el 75% del rango.*/
					
					/* 2- La particularidad de este caso afecta a la primera sentencia, el cierre de la barra se sitúa en su 100% por encima de la MM, con lo cual debe comprar, el filtro B aquí no afecta.
					 * */
				
				String _OperationFilter = "ALL";
				if (this.getSell_all_deadline_type_operation()!=null && !this.getSell_all_deadline_type_operation().equals(""))
					_OperationFilter = this.getSell_all_deadline_type_operation();
				 
				
				_BuySuccess = MobileAvgUtil._IsBuySignalMM8_5MINBar(_avgMobileSimple, oRTimeWidthRange, _WidthBarRangePercent, oRTimeEnTramo);
				
				
				
				_SellSuccess = MobileAvgUtil._IsSellSignalMM8_5MINBar(_avgMobileSimple, oRTimeWidthRange, _WidthBarRangePercent, oRTimeEnTramo);
				
				
				
				/* DNM_RAFADIESTRO  2015 06 16. COMENTAMOS LA CLAUSULA DE QUE NO LA CORTE  
				 * 
				 * if (!_AvgMovil_InsideBar && _AvgMovil_InsideBar_N_1)
				 * 
				if (_AvgMovil_InsideBar)
				{	
						_BuySuccess = oRTimeEnTramo.getValue().floatValue() >= _avgMobileSimple.getValue().floatValue();
						_SellSuccess = oRTimeEnTramo.getValue().floatValue() <= _avgMobileSimple.getValue().floatValue();
				}*/
				
				_BuySuccess = _BuySuccess &&  
						(_OperationFilter.equals("ALL") || _OperationFilter.equals(PositionStates.statusTWSFire.BUY.toString())); 

				
				_SellSuccess = _SellSuccess  &&  
						(_OperationFilter.equals("ALL") || _OperationFilter.equals(PositionStates.statusTWSFire.SELL.toString()));
				
				//Para entrar, 
				// 1. corte la barra en el n y de señal en el n 
				// 2. q no corte la barra en el n y la corte en el n-1
				if (_AvgMovil_InsideBar  && (_BuySuccess || _SellSuccess))
						/* DNM_RAFADIESTRO  2015 06 16. COMENTAMOS LA CLAUSULA DE QUE NO LA CORTE  
						  || (!_AvgMovil_InsideBar && _AvgMovil_InsideBar_N_1)) */
					
				{
					/* 
					--> VERIFICAR LA ENTRADA DE LA PRIMERA OPERATIVA PARA QUE NO SEA UN MECANISMO DE CONTINUIDAD, 
					SOLO ENTRE SI ES CAMBIO DE TENDENCIA
					DEBE IR A LA SEÑAL JUSTAMENTE ANTERIOR Y VER QUE TIPO DE SEÑAL DA PARA QUE NO COINCIDA.
					OJO, SOLO CUANDO DE SEÑAL DE CORTE DE BARRA, SI NO LA CORTA, ENTRAMOS DIRECTOS SI LA N-1 LA CORTA
					*/
					
					/* DNM_RAFADIESTRO  2015 06 16. COMENTAMOS LA CLAUSULA DE QUE NO LA CORTE   
					
					boolean bExistsDayPos = false;
					List<Position> _lPosition = null;
					if (!Simulation)
						_lPosition =  PositionDAO.getTradingPositions(ShareStrategy.getShareId().intValue());
					else
						 _lPosition =  SimulationPositionDAO.getTradingPositions(ShareStrategy.getShareId().intValue());
					if (_lPosition!=null && _lPosition.size()>0)
						bExistsDayPos = true;
					
					 // no existe posicion previa, vamos a las anteriores si hubiera, máximo de x periodos
					  // si corta la barra, si  no, ya tenemos n-1 que corte de la barra con mm
					
					if (!bExistsDayPos && _AvgMovil_InsideBar)  
					{
					
						// NUMERO DE BARRAS HACIA ATRAS PARA VERIFICAR CAMBIO O NO DE TENDENCIA EN MEDIA MOVIL DE 8
						int NUM_BARS_MAX = 3;
						try
						{
							NUM_BARS_MAX = Integer.parseInt(ConfigurationDAO.getConfiguration("BARS_PERIODS_BACK_SEARCH_MM8").value);
						}
						catch (Exception e) {}
						
						
						for (int NUM_BARS=1;NUM_BARS<=NUM_BARS_MAX;NUM_BARS++)
						{
							
							
							
							RealTime _avgMobileSimple_PERIOD = MobileAvgUtil.getAvgMobileMM8(_oDateFrom, _oDateTo, _num_macdP, _num_macdT, ShareStrategy.getShareId().intValue(),NUM_BARS,Simulation) ;
							RealTime oRTimeWidthRange_PERIOD = null;
							
							RealTime oRTimeEnTramo_PERIOD = null;
							
							if (_avgMobileSimple_PERIOD!=null) // hay MEDIA MOVIL?
							{
									oRTimeWidthRange_PERIOD = MobileAvgUtil.getMinMaxBarFromShare(_oDateTo,  _num_macdT, ShareStrategy.getShareId().intValue(),NUM_BARS,Simulation) ;
									
									if (oRTimeWidthRange_PERIOD!=null)
									{
										Calendar _oDateTo_PERIOD = Calendar.getInstance();
										_oDateTo_PERIOD.setTimeInMillis(_oDateTo.getTimeInMillis());
										_oDateTo_PERIOD.add(Calendar.MINUTE, - (_num_macdT * NUM_BARS));
										
										//System.out.println("oRTimeEnTramo_PERIOD : Periodo -" + NUM_BARS + ", _oDateTo_PERIOD:" + _sdf.format(_oDateTo_PERIOD.getTime()));
										
										if (Simulation)			
											oRTimeEnTramo_PERIOD =  (RealTime) RealTimeDAO.getLastRealTimeLessThanDateSimulated(ShareStrategy.getShareId().intValue(), new Timestamp(_oDateTo_PERIOD.getTimeInMillis()));
										else
											oRTimeEnTramo_PERIOD = (RealTime) RealTimeDAO.getLastRealTimeLessThanDate(ShareStrategy.getShareId().intValue(), new Timestamp(_oDateTo_PERIOD.getTimeInMillis()));
										
										//oRTimeEnTramo_PERIOD = (RealTime) RealTimeDAO.getLastRealTimeLessThanDate(ShareStrategy.getShareId().intValue(), new Timestamp(_oDateTo_PERIOD.getTimeInMillis()));
										
										
										float _WidthRangeBar_PERIOD = oRTimeWidthRange_PERIOD.getMax_value().floatValue() - oRTimeWidthRange_PERIOD.getMin_value().floatValue(); 
										
											
										boolean _AvgMovil_InsideBar_PERIOD  = (oRTimeWidthRange_PERIOD.getMax_value().floatValue()> _avgMobileSimple_PERIOD.getValue().floatValue() &&
															oRTimeWidthRange_PERIOD.getMin_value().floatValue()< _avgMobileSimple_PERIOD.getValue().floatValue());
										
										boolean _BuySuccess_PERIOD = false;
										boolean _SellSuccess_PERIOD = false;
										
										float _WidthBarRangePercent_PERIOD =  _WidthRangeBar_PERIOD *  _entryrate;

										_BuySuccess_PERIOD = MobileAvgUtil._IsBuySignalMM8_5MINBar(_avgMobileSimple_PERIOD, oRTimeWidthRange_PERIOD, _WidthBarRangePercent_PERIOD, oRTimeEnTramo);
										
										_SellSuccess_PERIOD = MobileAvgUtil._IsSellSignalMM8_5MINBar(_avgMobileSimple_PERIOD, oRTimeWidthRange_PERIOD, _WidthBarRangePercent_PERIOD, oRTimeEnTramo_PERIOD);
										
										
										if (_AvgMovil_InsideBar_PERIOD  && (_BuySuccess_PERIOD || _SellSuccess_PERIOD))
										{
											// hay señal de entrada, salimos del bucle y verificamos la no continuidad
											
											//System.out.println("Señal de entrada : Periodo -" + NUM_BARS + ", _oDateTo_PERIOD:" + _sdf.format(_oDateTo_PERIOD.getTime()));
											
											if  ((_BuySuccess && _SellSuccess_PERIOD) ||
													(_SellSuccess && _BuySuccess_PERIOD))
													{
												
															this.ValueToBuy = oRTimeEnTramo.getValue();
														 	this.verified = true;																			
															this.RealTimeID=  oRTimeEnTramo.getRealtimeID();										
															verified = true;
															this.bBuyOperation = _BuySuccess;									
															this.bSellOperation = _SellSuccess;
															
															MyLog.info("_BuySuccess:" + _BuySuccess + ",bSellOperation:" + bSellOperation + ",Fecha:" + _sdf.format(_oDateFrom.getTime()) + ",_AvgMovil_InsideBar:" + _AvgMovil_InsideBar + ",_AvgMovil_InsideBar_N_1:" + _AvgMovil_InsideBar_N_1 
		+ ", Cierre:" + oRTimeEnTramo.getValue() + ",MaxBarra:" + oRTimeWidthRange.getMax_value() + ",MinBarra:" + oRTimeWidthRange.getMin_value() + ",avgMobile:" + _avgMobileSimple.getValue() + ",AnchoBarra:" + _WidthRangeBar);
															//System.out.println("Señal de entrada : Periodo -" + NUM_BARS + ", _oDateTo_PERIOD:" + _sdf.format(_oDateTo_PERIOD.getTime()));

															
															break;
													}
														
											
										}
										
									}
							}
							else   // no hay media movil, supuestamente no habrá mas atrás???
							{
								//System.out.println("No hay media movil, salimos : Periodo -" + NUM_BARS);
								break;
							}
								
								
							
							
							
							
							
						}
					}
					else  */ // 
					//{
						this.ValueToBuy = oRTimeEnTramo.getValue();
					 	this.verified = true;																			
						this.RealTimeID=  oRTimeEnTramo.getRealtimeID();										
						verified = true;
						/* OJO, PUEDE SER QUE NO DE SEÑAL DE COMPRA Y VENTA SEGUN CONDICIONES,
						 * SI NO QUE SE VERIFICA QUE N NO CORTE LA BARRA LA MM  SI
						 */
					
						this.bBuyOperation = _BuySuccess;									
						this.bSellOperation = _SellSuccess;
						
						MyLog.info("_BuySuccess:" + _BuySuccess + ",bSellOperation:" + bSellOperation + ",Fecha:" + _sdf.format(_oDateFrom.getTime()) + ",_AvgMovil_InsideBar:" + _AvgMovil_InsideBar + ",_AvgMovil_InsideBar_N_1:" + _AvgMovil_InsideBar_N_1 
								+ ", Cierre:" + oRTimeEnTramo.getValue() + ",MaxBarra:" + oRTimeWidthRange.getMax_value() + ",MinBarra:" + oRTimeWidthRange.getMin_value() + ",avgMobile:" + _avgMobileSimple.getValue() + ",AnchoBarra:" + _WidthRangeBar);
							
					
					//}
						
					
					
					

					
					
					}
					
				}
			}// fin hay ancho de barra
		}
			
	return verified;

}
private boolean _VerifyStrategySimpleMobileAvg(Calendar IniMarket, Calendar _oDateFrom, Calendar _oDateTo,
		Share ShareStrategy, Market oMarket)
{
	return this.getVerifiedStraSimMobileAvg( IniMarket, _oDateFrom, _oDateTo,
			ShareStrategy, oMarket, false);
}			
private boolean _VerifyStrategySimpleMobileAvgSimulation(Calendar IniMarket, Calendar _oDateFrom, Calendar _oDateTo,
		Share ShareStrategy, Market oMarket)
{
	return this.getVerifiedStraSimMobileAvg( IniMarket, _oDateFrom, _oDateTo,
			ShareStrategy, oMarket, true);
}

public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
	 //List<Position> Lista = PositionDAO.getTradingPositions(new Timestamp(Calendar.getInstance().getTimeInMillis()));

	SimpleDateFormat _sdf =new SimpleDateFormat("dd/MM/yyyy HH:mm");

	LogTWM.getLog(StrategySimpleMobileAverage.class);		
	
	Share Test = ShareDAO.getShare("ES");
	Market TestMarket = MarketDAO.getMarketByName("EEUU");
	
	Calendar TestDate = Calendar.getInstance();
	
	TestDate.set(2014, 10, 6, 15,0,0);   // noviembre 6 15 00
	
	LogTWM.info(_sdf.format(TestDate.getTime()));
	
	StrategySimpleMobileAverage _this = new StrategySimpleMobileAverage();
	
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
