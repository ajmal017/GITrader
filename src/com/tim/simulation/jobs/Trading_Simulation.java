package com.tim.simulation.jobs;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.logging.Logger;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.ib.client.OrderState;
import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.dao.ConfigurationDAO;
import com.tim.dao.MarketDAO;
import com.tim.dao.MarketShareDAO;
import com.tim.dao.OrderDAO;
import com.tim.dao.PositionDAO;
import com.tim.dao.RealTimeDAO;
import com.tim.dao.RuleDAO;
import com.tim.dao.ShareDAO;
import com.tim.dao.Share_StrategyDAO;
import com.tim.dao.SimulationDAO;
import com.tim.dao.SimulationPositionDAO;
import com.tim.dao.Hist_Data_Share_StatusDAO;
import com.tim.dao.Simulation_ShareDAO;
import com.tim.dao.StrategyDAO;
import com.tim.dao.TradingMarketDAO;
import com.tim.model.Market;
import com.tim.model.Position;
import com.tim.model.Rule;
import com.tim.model.Share;
import com.tim.model.Share_Strategy;
import com.tim.model.Simulation;
import com.tim.model.Hist_Data_Share_Status;
import com.tim.model.Simulation_Share;
import com.tim.model.Strategy;
import com.tim.model.Trading;
import com.tim.service.TIMApiWExt;
import com.tim.service.TIMApiGITrader;
import com.tim.util.ConfigKeys;
import com.tim.util.LogTWM;
import com.tim.util.PositionStates;
import com.tim.util.Utilidades;

import org.apache.log4j.Priority;
import org.omg.stub.java.rmi._Remote_Stub;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;


/* HISTORICAL DATA : POR LA EXPERIENCIA, SOLO LA SEMANA ANTERIOR DEJA PEDIR INFO EN LA CUENTA DEMO
 * 
 *  	
 */


public class Trading_Simulation  implements StatefulJob  {

	/**
	 * @param args
	 * @throws InterruptedException 
	 */
	
	
/*	static int CLIENT_ID = 19;	  // el dos para leer, el 3 para escribir
	static  String  _Host = ConfigurationDAO.getConfiguration("TWS_HOST").getValue();
	static  int  _Port = Integer.parseInt(ConfigurationDAO.getConfiguration("TWS_PORT").getValue());
	static String  _AccountNameIB = ConfigurationDAO.getConfiguration("ACCOUNT_IB_NAME").getValue();
*/
	static List<Rule> olRules = null;
	static List<Strategy> oStrategy = null;

	static Trading  oTradingToday = null;
	
	static SimpleDateFormat sdfD = null;
	static SimpleDateFormat sdf = null;
	static SimpleDateFormat sdfFull = null;
	
	static SimpleDateFormat sdfTimes = null;
	
	public void execute(JobExecutionContext arg0) throws JobExecutionException {
		// TODO Auto-generated method stub
		StartUp();
		
		
	}
    
	public static void StartUp() throws JobExecutionException
	
	{	
	
		
	
	 sdf = new SimpleDateFormat ("yyyyMM");
	
	sdfD = new SimpleDateFormat ("yyyyMMdd"); 
	
	sdfFull = new SimpleDateFormat ("yyyyMMdd HH:mm:ss");
	
	sdfTimes = new SimpleDateFormat ("HHmm");
	
		
	TIMApiWExt oTWS = new TIMApiWExt();
	
	/* activamos el scheduler de MYSQL por si no lo está.No me tira desde el my.ini */
	TradingMarketDAO.StartTradingSchedulerMYSQL();
	
	//
	LogTWM.getLog(Trading_Simulation.class);
	LogTWM.log(Priority.INFO, "Starting Trading_Simulation (Ojo, Solo probado para una semana con la demo) process...Falta ajustarse a " +
			"https://www.interactivebrokers.com/en/software/api/apiguide/tables/historical_data_limitations.htm");
	
	// verificamos si hay mercado abierto.
	/* if (lActiveMarkets.size()>0)
	{
		
	Utilidades.closeTWSConnection(oTWS);
	
	try 
	{
	
	oTWS.connectToTWS();
	if (oTWS.isConnected()	)
	{
	
	*/
	
		
	
		
	
	Contract oContrat = null;
	
	/* VERIFICAMOS MERCADOS ACTIVOS */

    

    int UniquePOSITIONID=1;
    
    
   // MyLog.log(Priority.INFO, "UniqueORDERID" + UniqueORDERID);
    
    
    /* comprobamos si hay solicitado el tiempo real de todas las acciones */
    boolean bAllRequested = false;
    
    ArrayList<String> lShareRequested = new ArrayList();
    
    /* OBTENEMOS RESULTADO DE SIMULACIONES NO ACABADAS */
    List<Simulation> lActivesSimulation = (List<Simulation>) SimulationDAO.getPendingSimulations(); 
	//List<Simulation> lActivesSimulation = (List<Simulation>) SimulationDAO.getSimulation(new Long(1));
	//List<Market> lActiveMarkets = MarketDAO.getListActiveMarketBtHours(Utilidades.getActualHourFormat(), 1,null);
	
    
	
	oTradingToday = (Trading) TradingMarketDAO.getListActiveAllTradingToday(); 
	
    if (lActivesSimulation!=null && lActivesSimulation.size()>0)
    {
    	
    	
    	
    	Simulation _oSimulation  = lActivesSimulation.get(0);
    	/* ARRANCAMOS PROCESOS */
    	_oSimulation.setStatus(PositionStates.statusSimulation.Started.toString());
    	SimulationDAO.UpdateSimulation(_oSimulation);
    	
    		
    	Calendar DateFromSimulation = Calendar.getInstance();
    	Calendar DateToSimulation =Calendar.getInstance();
    	
    	
    	
    	/* recorremos mercados y acciones  */  
    	List<Simulation_Share> _lSharesSimulation =  (List<Simulation_Share>) Simulation_ShareDAO.getSimulation_Trades(_oSimulation.getSimulationID());
    	
    	
    	
    	for (int j=0;j<_lSharesSimulation.size();j++)
    	{
    		
    		Simulation_Share _sSimulation_Share = _lSharesSimulation.get(j);
    		Share oShare = ShareDAO.getShare(_sSimulation_Share.getShareId());			
			Market oMarket = ShareDAO.getMarketFromShare(oShare);
		
			// saber si es completo o desde una fecha dada por el pacing violations
			DateToSimulation.setTimeInMillis(_oSimulation.getEndDate().getTime());
			DateFromSimulation.setTimeInMillis(_oSimulation.getStartDate().getTime());
			int NUM_DAYS_ = Utilidades.daysDiff(DateFromSimulation.getTime(), DateToSimulation.getTime());	
			
			    			
			/* INICIO Y FIN DE LA APERTURA DEL MERCADO */    
			/* PONGO MISMO DIA CON LA HORA Y FIN DE MERCADO. HAY QUE ITERAR POR CADA DIA..ESTO ES PARA PROBAR */
			//Calendar Hoy = Calendar.getInstance();
		    
		    int hourI = Integer.parseInt(oMarket.getStart_hour().substring(0, 2));
		    int minuteI = Integer.parseInt(oMarket.getStart_hour().substring(2,4));
		    
		    int hourF = Integer.parseInt(oMarket.getEnd_hour().substring(0, 2));
		    int minuteF = Integer.parseInt(oMarket.getEnd_hour().substring(2,4));
    		
		   
  			if (oMarket==null)  // esto no debiera pasar. 
			{
  					Utilidades.closeTWSConnection(oTWS);    			
					throw new JobExecutionException("No Market Exists for Share" + oShare.getSymbol());
			}	
			
  			Calendar DateDayIni = Calendar.getInstance();
			Calendar DateDayFin = Calendar.getInstance();
  			
    		for (int jDAY=0;jDAY<=NUM_DAYS_;jDAY++) // recorremos dias, ojo a los limites, por eso dia a dia.
    		{
    			  
    			 	
    			DateDayIni.setTimeInMillis(DateFromSimulation.getTimeInMillis());
    			DateDayFin.setTimeInMillis(DateFromSimulation.getTimeInMillis());    			
    			
    		    DateDayIni.add(Calendar.DATE, jDAY);
    		    DateDayFin.add(Calendar.DATE, jDAY);
    		    
    		    //oContrat = (Contract) oTWS.createHistoricalContract(oShare.getSymbol(), oShare.getSecurity_type(),oShare.getExchange(),oMarket.getCurrency(), _Expiration, "", 0, DateDayIni);
    		    
    		   // DateToSimulation.add(Calendar.DATE, jDAY);
    		    
    			// LogTWM.log(Priority.INFO, "Fecha a pedir " +sdfFull.format(DateDayIni.getTime()));
    		     
    		    
    		    boolean bAfterEndMercado = false;
    		    if (_sSimulation_Share.getLastProcessed()!=null)
    		    	bAfterEndMercado = sdfTimes.format(_sSimulation_Share.getLastProcessed()).compareTo(oMarket.getEnd_hour())>=0;
    		    if (_sSimulation_Share.getLastProcessed()==null || 
    		    			(_sSimulation_Share.getLastProcessed()!=null && bAfterEndMercado))
    		    {
    		    
	    		    DateDayIni.set(Calendar.HOUR_OF_DAY, hourI);
	    		    DateDayIni.set(Calendar.MINUTE, minuteI);
	    		    DateDayIni.set(Calendar.SECOND, 0);
	    		    DateDayIni.set(Calendar.MILLISECOND, 0);
    		    }
    		    
    		    DateDayFin.set(Calendar.HOUR_OF_DAY, hourF);
    		    DateDayFin.set(Calendar.MINUTE, minuteF);
    		    DateDayFin.set(Calendar.SECOND, 0);
    		    DateDayFin.set(Calendar.MILLISECOND, 0);
    			
    			
    			/* int SecondsGaps = Utilidades.minutesDiff(DateDayIni.getTime(), DateDayFin.getTime());
    			SecondsGaps  = SecondsGaps * 60;	// pasamos a segundos	
    			*/
    			 /* CAMBIO,  18.02.2015, REDUCIMOS LA PETICIÓN POR HORA PARA TENER MAS EXACTITUD, 15 secs*/
    			int _NUM_HOURS_TO_REQUEST = Utilidades.minutesDiff(DateDayIni.getTime(), DateDayFin.getTime()) / 60;
    			_NUM_HOURS_TO_REQUEST +=1; // pedimos una mas para el resto de la ultima hora en caso de minutos.
    			
    			/* SUMO UN SEGUNDO PARA QUE ME SAQUE EL TRAMO 19.30.00 
    			DateToSimulation.set(Calendar.SECOND, DateToSimulation.get(Calendar.SECOND) + 1);*/
    			
    			 if (DateDayIni.after(DateDayFin))   //salimos con las horas de inicio de mercado del dia siguiente
     		    {
     		    
    				 DateFromSimulation.set(Calendar.HOUR_OF_DAY, hourI);
    				 DateFromSimulation.set(Calendar.MINUTE, minuteI);
    				 DateFromSimulation.set(Calendar.SECOND, 0);
    				 DateFromSimulation.set(Calendar.MILLISECOND, 0);
     		    }
    			
    			// horas.
    			 _loadConditionsDate();    			 
    			 _verifySimulatingShare(oShare, oMarket, oTWS, DateDayIni);    				    			
    			/* POR CADA ACCION Y DIA. INVOCAMOS A LA ENTRADA. */
    			
    			 
    			 
    			 
    		    		
    	      } /* fin recorremos mercados y acciones  */
    		
    		
    	}
    	
    	/* FINALIZAMOS PROCESOS */
    	_oSimulation.setStatus(PositionStates.statusSimulation.Processed.toString());
    	SimulationDAO.UpdateSimulation(_oSimulation);
    	
    	
    }

      }  /* fin de si hay datos a rellenar. Procedemos con  las invocaciones para testear las estrategias.  */
    
    
    
    // EMPEZAMOS CON LAS VERIFICACIONES
    /* recorremos mercados y acciones  para verificar las reglas. Por reflexion, creamos la factoria
	 * de objetos que implementa cada regla */
    		
		/* CASO ESPECIAL ESTRATEGIA DE CIERRE DE POSICIONES A UNA HORA DETERMINADA. 
		 * SE CIERRAN ANTES DE LA CONFIGURACION ORIGINAL.MIRAMOS EL CAMPO Y VEMOS SI ESTA
		 * ACTIVADO EL FLAG, ENTONCES, MIRAMOS SI NO HAY POSICIONES PARA CERRAR Y RESTAURAMOS. */
		
		 
		

		
	
	
	
    
    
    		
    	
    	// fin de mercamos y acciones.
    	
    	
	//Utilidades.closeTWSConnection(oTWS);
    		
    	
    	/* fin recorremos mercados */
    	
   /*  }
	else
	{
		
		LogTWM.log(Priority.INFO, "Ending Trading_Read process..No conectado a la TWS");
	}/	
   
	}
	catch (Exception e)
	{
		LogTWM.log(Priority.ERROR, e.getMessage());
	//	Utilidades.closeTWSConnection(oTWS);
	}*

}
	
	/*  id=1 date = 20131023  19:15:00 open=5.04 high=5.05 low=4.88 close=4.94 volume=33 count=31 WAP=4.973 hasGaps=false
	id=1 date = 20131023  19:20:00 open=4.99 high=5.26 low=4.99 close=5.19 volume=44 count=40 WAP=5.163 hasGaps=false
	id=1 date = 20131023  19:25:00 open=5.2 high=5.21 low=5.07 close=5.14 volume=27 count=24 WAP=5.148 hasGaps=false
	id=1 date = 20131023  19:30:00 open=5.14 high=5.14 low=5.14 close=5.14 volume=0 count=0 WAP=5.14 hasGaps=false
	id=1 date = finished-20131023  19:15:01-20131023  19:30:01 open=-1.0 high=-1.0 low=-1.0 close=-1.0 volume=-1 count=-1 WAP=-1.0 hasGaps=false
	 */
	
	
	private static boolean _loadConditionsDate()
	{
		
		olRules = RuleDAO.getListRules(oTradingToday.getTradingID().intValue());
		oStrategy = StrategyDAO.getListStrategies(oTradingToday.getTradingID().intValue());

		try {
			oTradingToday.setlRules(Utilidades.LoadRules(olRules));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
			
		/* recorremos mercados y acciones  para verificar las estrategias. Por reflexion, creamos la factoria
		 * de objetos que implementa cada strategia */
		
		try {
			oTradingToday.setlStrategies(Utilidades.LoadStrategies(oStrategy));
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return true;
		
	}
	
	
	/* ES IMPORTANTE QUE PARA LA SIMULACION, SE ORDENEN LAS ESTRATEGIAS PARA SU EJECUCION,
	 * 1. BUY (ENTRADA)
	 * 2. SELL  (SALIDA)
	 * 
	 * PARA LA EJECUCION Y OPERATIVA NORMAL, NO HAY PROBLEMA YA QUE SE ESTAN EJECUTANDO CONTINUAMENTE.
	 */
	
	
	private static void _verifySimulatingShare( Share _oShare, Market _oMarket, TIMApiGITrader _oTWS, Calendar TradeDate)
	{
	// verificamos la estrategias existentes.
	if (oTradingToday.getlStrategies()!=null)
	{	
		
		
		  /* 1. no  HAYA SEÑALES DE ENTRADA EN NINGUN ESTRATEGIA, todo verificado
		   * 2. no haya operaciones pendientes de vender. 
		   */
		
		
		
		  Hashtable<Long,Boolean> lVerifiedEntryStrategies = new Hashtable<Long,Boolean>();
		  
		// INTRODUCIMOS LAS ESTRATEGIAS DEL VALOR
		java.util.List<Share_Strategy> _lStratOfShare =  Share_StrategyDAO.getListStrategiesByShare(_oShare.getShareId(),false);  
		  
		for (int k=0;k<oTradingToday.getlStrategies().size();k++)	    				
	    {  
			
			Strategy MyStrategy =  oTradingToday.getlStrategies().get(k);
			
			
            boolean _bSTRATEGY_IN_SHARE = false;
			//si la estrategia esta en la accion ? 
			if (Utilidades.fn_IsStrategyInShareStrategies(MyStrategy.getStrategyId(),_lStratOfShare))
			{					
				lVerifiedEntryStrategies.put(MyStrategy.getStrategyId(), new Boolean(false));
			}
			
	    }	  
		  
		  boolean _DAY_FULL_SCANNED = false;  
		
		  /* ITERAMOS POR CADA ESTRATEGIA HASTA QUE NO HAYA POSIBILIDAD DE ENTRAR */
		
		  while (!_DAY_FULL_SCANNED)
		  {	  
			  
			 
			 /* java.util.List<Share_Strategy> _lStratOfShare = Share_StrategyDAO.getListStrategiesByShare(_oShare.getShareId(),false); 
			 if (_lStratOfShare==null)
			 {
				 _DAY_FULL_SCANNED =true;
				  return;
			 }*/
			 
			  
			for (int k=0;k<oTradingToday.getlStrategies().size();k++)	    			
	    	{
				// habra reglas que haya que cumplir...una operacion de compra previa...p.e.
				
			
				Strategy MyStrategy =  oTradingToday.getlStrategies().get(k);
				
				// verificamos si hay posibilidad de ejecución por cada operativa
				boolean bRULES_OK = true;
				//Share_Strategy _oSE = (Share_Strategy) _lStratOfShare.get(k);
				
				//Strategy MyStrategy = StrategyDAO.getStrategy(_oSE.getStrategyId());
				
				/* INICIALIZAMOS A FALSE 
				if (!lVerifiedEntryStrategies.containsKey(MyStrategy.getStrategyId()))
				{	
					lVerifiedEntryStrategies.put(MyStrategy.getStrategyId(), new Boolean(false));
				}*/
				
				
				for (int h=0;h<oTradingToday.getlRules().size();h++)	    					
    	    	{
					
					
					// habra reglas que haya que cumplir...una operacion de compra previa...p.e.
					// BUSCAMOS REGLAS GENERALES Y REGLAS ASOCIADAS A LAS VENTAS.
					
					/* REGLA GLOBAL, SE APLICA A TODOS  */
					/* HAY UN REGLA DE COMPRA, VERIFICAMOS QUE LA CUMPLA SI ES UNA ESTRATEGIA IGUAL */
					/* Rule MyRule = (Rule) oTradingToday.getlRules().get(h);
					if (MyRule.getType()==null)
					{	
						if (!MyRule.Verify(_oShare, _oMarket)) 
						{
							bRULES_OK = false;
							continue;	    							
						}
					}
					
					if (MyRule.getType()!=null && MyRule.getType().equals(MyStrategy.getType()))
					{
						if (!MyRule.Verify(_oShare, _oMarket)) 
						{
							bRULES_OK = false;
							continue;	    							
						}
					}
					*/
				
    	    	}
				
	    		
				


				
				boolean bACTIVE_TRADINGOK = true;
				
				
				// verificamos que si es una estrategia de entrada, vemos si esta activa para trading.
				// añadimos casos de futuros no expirados.
				/* if (MyStrategy.getType()!=null && MyStrategy.getType().equals(PositionStates.statusTWSFire.BUY.toString())
						&& _oShare.getActive_trading().equals(new Long(0)))
	    		{ 
					bACTIVE_TRADINGOK = false;
	    		}*/
				
				bACTIVE_TRADINGOK = bACTIVE_TRADINGOK && Utilidades.fn_IsStrategyInShareStrategies(MyStrategy.getStrategyId(),_lStratOfShare);				
				
				//MyStrategy.setACCOUNT_NAME(_AccountNameIB); 
				SimpleDateFormat sdfFull = new SimpleDateFormat ("yyyyMMdd HH:mm:ss");
				
				/* VERIFICADAS LAS REGLAS TB */
				
				
				boolean bVerifiedStr = false;
				bVerifiedStr = MyStrategy.VerifySimulation(_oShare, _oMarket, TradeDate);
				
				//System.out.println("(bVerifiedStr:" + bVerifiedStr);
				
				if (bRULES_OK && bACTIVE_TRADINGOK && bVerifiedStr)
					{
				//if (bRULES_OK && bACTIVE_TRADINGOK  &&  MyStrategy.VerifySimulation(_oShare, _oMarket, TradeDate))
					//System.out.println("(MyStrategy:" + MyStrategy.getName() + "," + sdfFull.format(TradeDate.getTime()));
					MyStrategy.ExecuteSimulation(_oMarket, _oShare, TradeDate);
					}
				
				if (lVerifiedEntryStrategies.containsKey(MyStrategy.getStrategyId()))
				{	
					lVerifiedEntryStrategies.put(MyStrategy.getStrategyId(), MyStrategy.is_FULL_SIMULATION_DAY_SCANNED());
				}
				
				/* ESTAN TODAS VERIFICADAS PARA EL DIA EN CUESTION?, PASAMOS AL SIGUIENTE 
				 * ASEGURAMOS QUE HAYAN EJECUTADO TODAS LAS ESTRATEGIAS lVerifiedEntryStrategies = _lStratOfShare y que tengam
				 * el flag a SCAN_FULLED
				 * 
				 * CAMBIO, HAY QUE METER QUE TODAS LAS ESTRATEGIAS DE ENTRADA ESTEN FULLSCANNED Y NO HAYA NINGUNA POSICION ABIERTA.
				 * */
				
				if (_AllStratVerifiedInDay(lVerifiedEntryStrategies, _oShare.getShareId()))
					_DAY_FULL_SCANNED = true;
				
					
				
					    				
	    	}
		  } // en full scanned
	}			
	    	    	
	}
	
	private static boolean _AllStratVerifiedInDay(java.util.Hashtable<Long,Boolean> lStrategies, Long ShareId)
	{
		boolean bAllVerified = true;
		Set<Long> setStrat = lStrategies.keySet();

	    Iterator<Long> itr = setStrat.iterator();
	    
	    boolean ExistsPositionOpen =  (SimulationPositionDAO.ExistsPositionShareOpen(ShareId.intValue()));
	    while (itr.hasNext()) 
	    {
	      Long Index = itr.next();
	      
	      // ESTRATEGY ID
	      Strategy VerifiedStrategy =  StrategyDAO.getStrategy(Index);
	      
	      //itr.
	      //System.out.println(lStrategies.get(Index) + "|" + Index + "," + lStrategies.get(Index).equals(new Boolean(false)) + "," + ExistsPositionOpen);
	      
	      //System.out.println(
	      
	      /* PARA SABER SI ESTA TODO FULLSCANNED, DEBEMOS VER QUE NO HAY POSICION ABIERTA Y QUE TODAS LAS ESTRATEGIAS DE ENTRADA ESTA FULL SCANNED */
	      
	      if (ExistsPositionOpen || (VerifiedStrategy.getType().equals(PositionStates.statusTWSFire.BUY.toString())  && lStrategies.get(Index).equals(new Boolean(false))))
	      {
	    	 // System.out.println("bAllVerified = false");
	    	  bAllVerified = false;
	    	  break;
	      }	    	  
	      
	    }

	return  bAllVerified;
	
	}
	
	
	
	public static void main(String[] args) throws Exception {
 		// TODO Auto-generated method stub
    	 //List<Position> Lista = PositionDAO.getTradingPositions(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		StartUp();
		
 	}
}
