package com.tim.jobs;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import com.ib.client.Contract;
import com.ib.client.EClientSocket;
import com.ib.client.EReader;
import com.ib.client.EReaderSignal;
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
import com.tim.dao.ShareDAO;
import com.tim.dao.TradingMarketDAO;
import com.tim.model.Market;
import com.tim.model.Position;
import com.tim.model.Share;
import com.tim.service.TIMApiGITrader;
import com.tim.service.TIMApiWrapper;
import com.tim.service.TIMApiGITrader;
import com.tim.util.LogTWM;
import com.tim.util.Utilidades;

import org.apache.log4j.Priority;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;


public class Trading_Read  implements StatefulJob  {

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
    
	public static void StartUp() throws JobExecutionException, InterruptedException
	
	{
	int CLIENT_ID = 6;	  // el dos para leer, el 3 para escribir
	
	String  _Host = ConfigurationDAO.getConfiguration("TWS_HOST").getValue();
	int  _Port = Integer.parseInt(ConfigurationDAO.getConfiguration("TWS_PORT").getValue());
	
	
	SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMM");
		
//	TIMApiGITrader oTWS = new TIMApiGITrader(_Host,_Port, CLIENT_ID);

	TIMApiGITrader oTWS = new TIMApiGITrader(_Host,_Port, CLIENT_ID);

	
	/* activamos el scheduler de MYSQL por si no lo está.No me tira desde el my.ini */
	TradingMarketDAO.StartTradingSchedulerMYSQL();
	
	
	
	
	//
	LogTWM.getLog(Trading_Read.class);
	LogTWM.log(Priority.INFO, "Starting Trading_Read process..");
	
	// verificamos si hay mercado abierto.
	/* if (lActiveMarkets.size()>0)
	{
		*/
	if (oTWS.GITraderTWSIsConnected())
	{
		oTWS.GITraderDisconnectFromTWS();
	}
	
	oTWS.GITraderConnetToTWS();
	if (oTWS.GITraderTWSIsConnected())
	{
	
	
	
	Contract oContrat = null;
	
	/* VERIFICAMOS MERCADOS ACTIVOS */
    java.util.List<Market> lMarket = null;
    java.util.List<Share> lShare = null;
    
    java.util.List<com.tim.model.Order> lOrders = null;
    
    com.tim.model.Order LastOrder = null;
    
    
    int UniqueORDERID=1;
    int UniquePOSITIONID=1;
    
    
   // MyLog.log(Priority.INFO, "UniqueORDERID" + UniqueORDERID);
    
    
    /* 1. VERIFICAMOS QUE EXISTA UNA PETICION PARA EL HISTORICAL DATA QUE NO HAYA FINALIZADO SIN ERROR */
    
    
    
    
    boolean bAllRequested = false;
    
    ArrayList<String> lShareRequested = new ArrayList();
    
    /* LANZAMOS LA OPERACION DE CONTINUO */ 
    
    while (true)
    	
    {	
    	
    
    
      if (!bAllRequested)
    {
   	
    	 // empezamos a contar desde 5 o 10 minutos antes de la apertura para contar precios
    	 
    	List<Market> lActiveMarkets = MarketDAO.getListActiveMarketBtHours(Utilidades.getActualHourFormatPlusMinutes(Utilidades.getActualHourFormat(),10), 1,null);
    	//List<Market> lActiveMarkets = MarketDAO.getListActiveMarketBtHours(Utilidades.getActualHourFormat(), 1,null);
    	
	    lMarket = lActiveMarkets;
		
	    if (lMarket!=null)
	    {
	    	
	    	/* recorremos mercados */
	    	
	    	for (int j=0;j<lMarket.size();j++)
	    	{
	    		
	    		Market oMarket = lMarket.get(j);
	    		
    			
	
	    		
	    		lShare = ShareDAO.getListActiveShareByMarket(oMarket.getMarketID());
	    			    		
	    		
	    		if (lShare!=null)
	    		{
	    			
	    			//MyLog.log(Priority.INFO, "Recorremos acciones lShare!=null" + String.valueOf(lShare!=null) + "|Size:" + lShare.size());
	    		
		    		for (int z=0;z<lShare.size();z++)
		        	{

		    			boolean bToRequest=true;
		    			
		    			
		    			Share oShare = (Share)lShare.get(z);
		    			
		    			String _Expiration = "";
		      		    if (oShare.getExpiry_date()!=null)
		      		    {	
		    				_Expiration = sdf.format(oShare.getExpiry_date());
		    			//	LogTWM.log(Priority.INFO, "_Expiration:" + _Expiration);
		      		    }
		    		
		    			
		    			if  (oShare.getLast_error_data_read()!=null && !oShare.getLast_error_data_read().trim().equals(""))
		    			{
		    				
		    				lShareRequested.remove(oShare.getSymbol());
		    			}
		    			
		    			if (lShareRequested!=null && lShareRequested.contains(oShare.getSymbol() + "|" + _Expiration))
		    			{
		    				bToRequest = false; 
		    				/* if (oShare.getSymbol().contains("BB"))
			    			{
		    					LogTWM.log(Priority.INFO,"false");
			    			}*/
		    				// si por lo que sea, está activa, presentó un error, la quita de la lista para que vuelva a activarse
		    				// en caso de arreglarlo.
		    				/* if (oShare.getLast_error_data_read()!=null && !oShare.getLast_error_data_read().trim().equals(""))
		    				{
		    					
		    					bToRequest = true;
		    					lShareRequested.remove(oShare.getShareId());
		    				}*/
		    				
		    			}
		    			else
		    			{
		    				lShareRequested.add(oShare.getSymbol() + "|" + _Expiration);
		    			}
		    			//LogTWM.log(Priority.INFO,"bToRequest:" + bToRequest);
		    			
		    			
		    			/* if (lShareRequested==null || !(lShareRequested!=null && !lShareRequested.contains(oShare.getShareId())))
		    			{
		    				lShareRequested.add(oShare.getShareId());
		    			}*/
		    			
		    			//MyLog.log(Priority.INFO, oShare.getSymbol());
		    			//oTWS.		 
		    			if (bToRequest)
		    			{
		    				
			    			
		    				if (oShare.getSecurity_type().equals("FUT"))
		    				{
		    					oContrat = new FutContract( oShare.getSymbol(), _Expiration);
		    					//oContrat.multiplier(String.valueOf(oShare.getMultiplier()));		    					
		    					oContrat.exchange(oShare.getExchange());
		    					oContrat.currency(oMarket.getCurrency());
		    				}
		    				else		    					
		    					oContrat = new StkContract( oShare.getSymbol());
		    				
			    			
			    			//oContrat = (Contract) oTWS.createContract(oShare.getSymbol(), oMarket.getSecurity_type(),oMarket.getExchange(),oMarket.getCurrency());//, _Expiration, "", 0);
			    			
			    			LogTWM.log(Priority.INFO, "TradingRead de :" + oShare.getSymbol() + ":" +  oShare.getSecurity_type() + ":" + oShare.getExchange() + ":" + oMarket.getCurrency());
			    			
			    			
			    			// USAMOS EL CLIENT_ID UNICO PARA GENERAR INTERVALOS DE ORDERSID PARA 
			    			// EVITAR CONCURRENCIA Y GENERAR CODIGOS IGUALES
			    			LastOrder =  OrderDAO.LastOrderID();
			    		    
			    		    if (LastOrder!=null)
			    		    {
			    		    	UniqueORDERID =LastOrder.getOrderID().intValue()+CLIENT_ID ;			    		    	
			    		    }
			    		    else
			    		    	throw new JobExecutionException("LastOrder is null");
			    			
			    			/* insertamos control de ordenes de peticion */
			    			OrderDAO.addOrder(UniqueORDERID, oShare.getShareId().intValue());
			    			
			    			/* pedimos tiempo real */
			    			//MyLog.log(Priority.SEVERE, "Consulta de :" + UniqueORDERID + ":" +  UniqueORDERID);
			    			try {
								oTWS.GITraderGetRealTimeContract(UniqueORDERID,oContrat);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
								oTWS.GITraderConnetToTWS();
								//if (oTWS.GITraderTWSIsConnected())
								if (oTWS.GITraderTWSIsConnected())
								{
									oTWS.GITraderDisconnectFromTWS();
									
								}
							}
			        		
			    			//UniqueORDERID+=1;
		    			
		    			}
		        		
		        	}
	
	    		}
	    //		bAllRequested = true;
	    		
	    	}
	    	// fin de mercamos y acciones.
	    	
	    	
	    	
	    		
	    	
	    	/* fin recorremos mercados */
	    	
	    }
    /* } */
 		// una vez que estamos dentro..verificamos que haya conexion y este el mercado abierto.
		List<Market> lActiveMarketsCheck = MarketDAO.getListActiveMarketBtHours(Utilidades.getActualHourFormat(),1, null);
		boolean IsConnected = oTWS.GITraderTWSIsConnected();		   // en debug está siempre a false		
		if (lActiveMarketsCheck==null ||  !IsConnected)
		{
			if (IsConnected)
			{
				oTWS.disconnectFromTWS();
				
			}
			LogTWM.log(Priority.INFO, "Ending Trading_Read Process..There is no open market or console closed");
			System.exit(-1);
		}
	    	
    	// fin while
    }   // fin is connected
     
     
	  
	else
	{
		
		LogTWM.log(Priority.INFO, "Ending Trading_Read process..No conectado a la TWS");
	}	
	
	/*} // fin de mercados abiertos.
	 else
	{
		LogTWM.log(Priority.INFO, "Ending Trading_Read process..No open market");
	}	
	*/
    }
	}

}
	
	public void TestOK() throws InterruptedException
	{
		TIMApiGITrader oTWS = new TIMApiGITrader("127.0.0.1", 7497, 4);
		if (oTWS.GITraderTWSIsConnected())
		{
			oTWS.GITraderDisconnectFromTWS();
		}
		
		oTWS.GITraderConnetToTWS();
		
		Contract  _contractAPI3 =  new StkContract("AAPL");
		_contractAPI3.symbol("AAPL");
		_contractAPI3.secType("STK");
		_contractAPI3.exchange("ISLAND");
		_contractAPI3.currency("USD");
		
		oTWS.GITraderGetRealTimeContract(18007,_contractAPI3);
	}
	
	public static void main(String[] args) throws Exception {
 		// TODO Auto-generated method stub
    	 //List<Position> Lista = PositionDAO.getTradingPositions(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		
		StartUp();
		


		
		
		/* final EClientSocket m_client = oTWS.getClient();
		final EReaderSignal m_signal = oTWS.getSignal();
		
		m_client.reqMktData(18006, _contractAPI3, "", false, false, null);

		
		oTWS.getClient().eConnect("127.0.0.1", 7497, 4);
		
		final EReader reader2 = new EReader(oTWS.getClient(), oTWS.getSignal());   
		
		reader2.start();
		
		
		//An additional thread is created in this program design to empty the messaging queue
		new Thread(() -> {
		    while (oTWS.getClient().isConnected()) {
		    	oTWS.getSignal().waitForSignal();
		        try {
		            reader2.processMsgs();
		        } catch (Exception e) {
		            System.out.println("Exception: "+e.getMessage());
		        }
		    }
		}).start();
		
		
	


		

		m_client.eConnect("127.0.0.1", 7497, 3);
		//! [connect]
		//! [ereader]
		final EReader reader = new EReader(m_client, m_signal);   
		
		reader.start();
		
		
		//An additional thread is created in this program design to empty the messaging queue
		new Thread(() -> {
		    while (m_client.isConnected()) {
		        m_signal.waitForSignal();
		        try {
		            reader.processMsgs();
		        } catch (Exception e) {
		            System.out.println("Exception: "+e.getMessage());
		        }
		    }
		}).start();
				*/
		//! [ereader]
		// A pause to give the application time to establish the connection
		// In a production application, it would be best to wait for callbacks to confirm the connection is complete
		
		
		
		//! [reqHeadTimeStamp]

		//! [cancelHeadTimestamp]
		
		//! [cancelHeadTimestamp]
		
		//! [reqhistoricaldata]
		/* Calendar cal = Calendar.getInstance();
		cal.add(Calendar.MONTH, -6);
		SimpleDateFormat form = new SimpleDateFormat("yyyyMMdd HH:mm:ss");
		String formatted = form.format(cal.getTime());
		m_client.reqHistoricalData(4001, _contractAPI31, formatted, "1 M", "1 day", "MIDPOINT", 1, 1, false, null);
		m_client.reqHistoricalData(4002, _contractAPI31, formatted, "10 D", "1 min", "TRADES", 1, 1, false, null);
		Thread.sleep(2000);
		/*** Canceling historical data requests ***/
		/* m_client.cancelHistoricalData(4001);
		m_client.cancelHistoricalData(4002);*/
		//! [reqhistoricaldata]
			
		
		

		//_APIGTrader.
		

		
		//tickDataOperations(wrapper.getClient());
		//orderOperations(wrapper.getClient(), wrapper.getCurrentOrderId());
		//contractOperations(wrapper.getClient());
		//hedgeSample(wrapper.getClient(), wrapper.getCurrentOrderId());
		//testAlgoSamples(wrapper.getClient(), wrapper.getCurrentOrderId());
		//bracketSample(wrapper.getClient(), wrapper.getCurrentOrderId());
		//bulletins(wrapper.getClient());
		//reutersFundamentals(wrapper.getClient());
		//marketDataType(wrapper.getClient());
		//historicalDataRequests(wrapper.getClient());
		//accountOperations(wrapper.getClient());
		//newsOperations(wrapper.getClient());
		//marketDepthOperations(wrapper.getClient());
		//rerouteCFDOperations(wrapper.getClient());
		//marketRuleOperations(wrapper.getClient()); 
		//tickDataOperations(wrapper.getClient());
		//pnlSingle(wrapper.getClient());
		//continuousFuturesOperations(wrapper.getClient());

		Thread.sleep(100000);
		//m_client.eDisconnect();

	 	
 	}
}
