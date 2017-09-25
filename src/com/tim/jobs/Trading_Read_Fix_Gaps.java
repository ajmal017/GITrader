package com.tim.jobs;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.UUID;
import java.util.logging.Logger;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.ib.contracts.FutContract;
import com.ib.contracts.StkContract;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.dao.ConfigurationDAO;
import com.tim.dao.MarketDAO;
import com.tim.dao.MarketShareDAO;
import com.tim.dao.OrderDAO;
import com.tim.dao.PositionDAO;
import com.tim.dao.RealTimeDAO;
import com.tim.dao.ShareDAO;
import com.tim.dao.Share_StrategyDAO;
import com.tim.dao.TradingMarketDAO;
import com.tim.model.Market;
import com.tim.model.Position;
import com.tim.model.RealTime;
import com.tim.model.Share;
import com.tim.model.Share_Strategy;
import com.tim.model.Trading;
import com.tim.model.Trading_Market;
import com.tim.service.TIMApiGITrader;
import com.tim.util.ConfigKeys;
import com.tim.util.LogTWM;
import com.tim.util.PositionStates;
import com.tim.util.Utilidades;

import org.apache.log4j.Priority;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;


/* 
 * RELLENA HUECOS COMO CONSECUENCIA DE NECESITAR TRAMOS SEGÚN CIERTAS ESTRATEGIAS (MIN MAX)
 * 
 * VERIFICAMOS QUE LA HORA ACTUAL ESTA POR DETRAS DEL TRAMO PARA QUE NO 
 * SE SOLAPEN TIEMPO REAL Y FIX GAPS.
 * FALTA DETECTAR QUE SI HAY UN ERROR  (NO DEL TIPO SECURITY REQUEST)...SI NO , EL SERVICE DEL HISTORICAL DATA (A VECES LO
 * DA, DESCARTAR ESTE VALOR PARA PROSEGUIR CON LOS OTROS  O REALIZAR UN ALEATORIO.
 *  */
 
public class Trading_Read_Fix_Gaps  implements StatefulJob  {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	
	
	

	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		try {
			StartUp();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	
	/* ojo, porque pedir un tramo que no existe puede provocar error de no existencia de datos y volver a preguntar, entrando
	 * en pace violations 
	 */
    
	public static void StartUp() throws InterruptedException
	
	{
	int CLIENT_ID = 11;	  // el dos para leer, el 3 para escribir
	
	String  _Host = ConfigurationDAO.getConfiguration("TWS_HOST").getValue();
	int  _Port = Integer.parseInt(ConfigurationDAO.getConfiguration("TWS_PORT").getValue());
	
	
	SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMM");
	SimpleDateFormat sdfFull = new SimpleDateFormat ("yyyyMMdd HH:mm:ss");
	
	/* recorremos mercados */
	LogTWM.getLog(Trading_Read_Fix_Gaps.class);	
	LogTWM.log(Priority.INFO, "Starting Trading_Read_Fix_Gaps process");
		
	TIMApiGITrader oTWS = new TIMApiGITrader(_Host,_Port, CLIENT_ID);
	
	/* activamos el scheduler de MYSQL por si no lo está.No me tira desde el my.ini */
	TradingMarketDAO.StartTradingSchedulerMYSQL();
	
	//
	
	Contract oContrat = null;
	Market oMarket = null;
	/* VERIFICAMOS MERCADOS ACTIVOS */
    java.util.List<Market> lMarket = null;
    //java.util.List<Share> lShare = null;
    
    java.util.List<Trading_Market> lShare = null;
    
    java.util.List<com.tim.model.Order> lOrders = null;
    
    com.tim.model.Order LastOrder = null;
    
    
    int UniqueORDERID=1;
    int UniquePOSITIONID=1;
    
    Share oShare = null;
    
    boolean bToRequest=false;
    
    
    if (oTWS.GITraderTWSIsConnected())
	{
		oTWS.GITraderDisconnectFromTWS();
	}
	
	oTWS.GITraderConnetToTWS();
	if (oTWS.GITraderTWSIsConnected())
	{
    
   
   // MyLog.log(Priority.INFO, "UniqueORDERID" + UniqueORDERID);
    
    
        
    Calendar DateMinMaxFrom =null;
	Calendar DateMinMaxTo =null;
    
	Trading  oTradingToday = null;
	oTradingToday = (Trading) TradingMarketDAO.getListActiveAllTradingToday();
	
    
	String _Expiration = "";
    
    
   /*   if (!bAllRequested)
    {
    */	
    	 // empezamos a contar desde 5 o 10 minutos antes de la apertura para contar precios
    	 
    	List<Market> lActiveMarkets = MarketDAO.getListActiveMarketBtHours(Utilidades.getActualHourFormatPlusMinutes(Utilidades.getActualHourFormat(),10), 1,null);
    	//List<Market> lActiveMarkets = MarketDAO.getListActiveMarketBtHours(Utilidades.getActualHourFormat(), 1,null);
    	
	    lMarket = lActiveMarkets;
		
	    if (lMarket!=null && oTradingToday!=null)
	    {
	    	
	    	/* recorremos mercados */
	    	LogTWM.getLog(Trading_Read_Fix_Gaps.class);
	    	
	    	//LogTWM.log(Priority.ERROR, "oMarket:");
	    	
	    	for (int j=0;j<lMarket.size();j++)
	    	{
	    		
	    		if (!bToRequest)
    			{
	    		
	    		 oMarket = lMarket.get(j);
	    		
    			
	
	    		
	    		//lShare = ShareDAO.getListActiveShareByMarket(oMarket.getMarketID());
	    		 lShare = TradingMarketDAO.getListActiveTradingMarketShares(oTradingToday.getTradingID().intValue());	    		
	    		
	    		 
	    		 
	    		if (lShare!=null)
	    		{
	    			
	    			//MyLog.log(Priority.INFO, "Recorremos acciones lShare!=null" + String.valueOf(lShare!=null) + "|Size:" + lShare.size());
	    		
		    		for (int z=0;z<lShare.size();z++)
		        	{

		    			
		    			if (!bToRequest)
		    			{
		    			
		    			Trading_Market MyTS = (Trading_Market)lShare.get(z);	
		    				
		    			oShare = ShareDAO.getShare(MyTS.getShareId()) ;
		    			
		    			java.util.List<Share_Strategy> _lStratOfShare = Share_StrategyDAO.getListStrategiesByShare(oShare.getShareId(),false);
		    			/* COGEMOS SOLO LA PRIMERA Y QUE FINALICE EL JOBS PORQUE EL SINCRONISMO MACHACA 
		    			 * LAS VARIABLES STATICAS INTERMEDIAS DEL HISTORICAL DATA ENTRE LOS DISTINTOS VALORES 
		    			 */

		    			// cogemos acciones operables y que tengan las estrategias acordes. 
		    			
		    			 if (!TradingMarketDAO.IsShareIntrading(oShare.getShareId().intValue()) || 
		    					 !Utilidades.fn_IsStrategyInShareStrategies(ConfigKeys.STRATEGY_BUY_MIN_MAX,_lStratOfShare))
		    			 {
		    				 	continue;
		    			 }	 
		    				 
		    				 
		    			 
		    			
		    			 _Expiration = "";
		      		    if (oShare.getExpiry_date()!=null)
		    				_Expiration = sdf.format(oShare.getExpiry_date());
		    		
		      		    String HoraActual = Utilidades.getActualHourFormat();
		    			
		    			/* VERIFICAMOS QUE HAYA UN HUECO DE RANGO NO RELLENADO POR CUALQUIER RAZON */
 		      		    // 	HORA DE FIN DE CALCULO DE MAX Y MINIMOS.
		    			String HoraInicioLecturaMaxMin = Utilidades.getActualHourFormatPlusMinutes(oMarket.getStart_hour(), oShare.getOffset1min_read_from_initmarket());
		    			String HoraFinLecturaMaxMin = Utilidades.getActualHourFormatPlusMinutes(oMarket.getStart_hour(), oShare.getOffset2min_read_from_initmarket());
		    			
		    			/* COMPROBAMOS ALGUN TIPO DE ERROR */
		    			if (HoraInicioLecturaMaxMin.contains("-1") || HoraFinLecturaMaxMin.contains("-1"))
		    			{
		    				LogTWM.log(Priority.ERROR, "[Trading_Read_Fill_Gaps]: Errores formateando las horas de Max y Min de la acción. Hora[" + oMarket.getStart_hour()  + "], Offset1[" + oShare.getOffset1min_read_from_initmarket() + "]");
		    				return;
		    			}
		    				
//		    			LogTWM.log(Priority.ERROR, oShare.getSymbol());
		    			if (HoraActual.compareTo(HoraFinLecturaMaxMin)>0)   // hora actyual ya ha pasado, podemos entrar en la verificacion de huecos
		    			{
		    				// ya no obtenemos el maximo y mínimo, sino el correspondiente al tramo que me han dicho
		    			
		    				
		    				DateMinMaxFrom =Utilidades.getNewCalendarWithHour(HoraInicioLecturaMaxMin);
		    				DateMinMaxTo =Utilidades.getNewCalendarWithHour(HoraFinLecturaMaxMin);
		      		    
		    				/* TIEMPOS REALES Y MAXIMOS Y MINIMOS CONSEGUIDOS */
		    				RealTime oRTimeEnTramo = (RealTime) RealTimeDAO.getRealTimeBetweenDates(oShare.getShareId().intValue(), new Timestamp(DateMinMaxFrom.getTimeInMillis()), new Timestamp(DateMinMaxTo.getTimeInMillis()));
		    				
		    				if (oRTimeEnTramo==null)  // no hay real time, habiendo pasado la hora actual...Pedimos tiempo historico
		    				{		    				

		    					LogTWM.getLog(Trading_Read_Fix_Gaps.class);
		    					LogTWM.log(Priority.INFO, "Filling Gap  :" + oShare.getSymbol() + ":"  + sdfFull.format(DateMinMaxFrom.getTime()) + " until " + sdfFull.format(DateMinMaxTo.getTime()));
		    					bToRequest = true;
		    				}
		    			}
				    		
		        	}
	
	    		}
	    		}
	    		
	    	}
	    	// fin de mercamos y acciones.
	    	}
	    	
	    	
	    		
	    	
	    	/* fin recorremos mercados */
	    	
     
	/*  LANZAMOS LA OPERACION DE CONTINUO. SOLO ES PARA GESTIONAR  HUECOS EN LAS LECTURAS */   
    if  (bToRequest)
    {	
    		  
	    	/* insertamos control de ordenes de peticion */
			/* CALCULAMOS LA DIFERENCIA EN SEGUNDOS PARA OBTENER EL TRAMO A CONSULTAR 
	    	
	    	if (!oTWS.isConnected())
				oTWS.connectToTWS();
			
			
			if (oTWS.isConnected())
			{ */
				
	    	if (oShare.getSecurity_type().equals("FUT"))		    		
	    	{	
				oContrat = new FutContract( oShare.getSymbol(), _Expiration);
				//oContrat.multiplier(String.valueOf(oShare.getMultiplier()));		    					
				oContrat.exchange(oShare.getExchange());
				oContrat.currency(oMarket.getCurrency());
	    	}
			else		    					
				oContrat = new StkContract( oShare.getSymbol());
		    	
			//oContrat = (Contract) oTWS.createContract(oShare.getSymbol(), oShare.getSecurity_type(),oShare.getExchange(),oMarket.getCurrency(), _Expiration, "", 0);
			
			int SecondsGaps = Utilidades.minutesDiff(DateMinMaxFrom.getTime(), DateMinMaxTo.getTime());
			SecondsGaps  = SecondsGaps * 60;		
			
			LastOrder =  OrderDAO.LastOrderID();
			    
			    
		    if (LastOrder!=null)
		    {
		    	UniqueORDERID =LastOrder.getOrderID().intValue()+CLIENT_ID ;
		    }
	
			/* insertamos control de ordenes de peticion */
			OrderDAO.addOrder(UniqueORDERID, oShare.getShareId().intValue());
			
			/*  id=1 date = 20131023  19:15:00 open=5.04 high=5.05 low=4.88 close=4.94 volume=33 count=31 WAP=4.973 hasGaps=false
				id=1 date = 20131023  19:20:00 open=4.99 high=5.26 low=4.99 close=5.19 volume=44 count=40 WAP=5.163 hasGaps=false
				id=1 date = 20131023  19:25:00 open=5.2 high=5.21 low=5.07 close=5.14 volume=27 count=24 WAP=5.148 hasGaps=false
				id=1 date = 20131023  19:30:00 open=5.14 high=5.14 low=5.14 close=5.14 volume=0 count=0 WAP=5.14 hasGaps=false
				id=1 date = finished-20131023  19:15:01-20131023  19:30:01 open=-1.0 high=-1.0 low=-1.0 close=-1.0 volume=-1 count=-1 WAP=-1.0 hasGaps=false
			*/
			
			/* SUMO UN SEGUNDO PARA QUE ME SAQUE EL TRAMO 19.30.00 */
			DateMinMaxTo.set(Calendar.SECOND, DateMinMaxTo.get(Calendar.SECOND) + 1);
			
			oTWS.GITraderHistoricalData(UniqueORDERID, oContrat, 
					sdfFull.format(new Timestamp(DateMinMaxTo.getTimeInMillis())) + " CET", 
					SecondsGaps +"", "5 mins", "TRADES", 1, 1);
			
			//UniqueORDERID+=1;
			
			LogTWM.log(Priority.INFO, "Esperando el OK...");
			
			Calendar DateIni = Calendar.getInstance();
			
			/* HYA QUE PONER UN TIMEOUT DE 3 SEGUNDOS MAXIMO */
			int MAX_SECONDS_WAIT = 4;
			int execution_time=0;
			
			while (!oTWS.get_DATE_HISTORICAL_REQUEST().equals("OK") &&  execution_time <MAX_SECONDS_WAIT )				   
			{
				 	Calendar DateEnd = Calendar.getInstance();
				 	execution_time = Utilidades.secondsDiff(DateIni.getTime(),DateEnd.getTime());
//				 	LogTWM.log(Priority.INFO, "oContrat.m_symbol:" + ":" + oContrat.m_symbol + "," + "_HISTORICAL_DATA_REQUEST:" + oTWS._HISTORICAL_DATA_REQUEST + ",Execution Time:"  + execution_time);
			
			}
		//	oTWS.disconnectFromTWS();
			
			if (!oTWS.get_HISTORICAL_DATA_REQUEST().equals("OK"))
			{
				LogTWM.log(Priority.INFO, "TimeOut alcanzado...");
			}
			else
			{
				LogTWM.log(Priority.INFO, "_HISTORICAL_DATA_REQUEST:" + oTWS.get_HISTORICAL_DATA_REQUEST());
				LogTWM.log(Priority.INFO, "Recibido OK.");
			}
			
			//System.exit(-1);
			
	         
        } // fin if  (bToRequest)
	} 
	    
		
	    if (oTWS.GITraderTWSIsConnected())
		{
			oTWS.GITraderDisconnectFromTWS();
		}    
	    
	}  // fin if (oTWS.isConnected())
    else
	{
		
		LogTWM.log(Priority.INFO, "Ending Trading_Read process..No conectado a la TWS");
		if (oTWS.GITraderTWSIsConnected())
		{
			oTWS.disconnectFromTWS();
		}
	}	
	
	
}
	public static void main(String[] args) throws Exception {
 		// TODO Auto-generated method stub
    	 //List<Position> Lista = PositionDAO.getTradingPositions(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		StartUp();
 	}
}
