package com.tim.jobs;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Timer;
import java.util.UUID;
import java.util.logging.Logger;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPathExpressionException;

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
import com.tim.dao.TradingMarketDAO;
import com.tim.model.Market;
import com.tim.model.Position;
import com.tim.model.RealTime;
import com.tim.model.Share;
import com.tim.service.TIMApiGITrader;
import com.tim.service.TIMApiGITrader;
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
import org.xml.sax.SAXException;


/* VERIFICA CONTINUAMENTE EL CONTRACT DETAILS DE CADA VALOR ACTIVO,
 * ASI DESACTIVAMOS EN EL ERROR DEL WRAPPER PARA EVITAR PERDIDAS DE CONECTIVIDAD
 * Y POR CONSIGUIENTE DESACTIVAR EL ACTIVO
 *  */

public class Trading_Verify_ContractDetails  implements StatefulJob  {

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
	int CLIENT_ID = 12;	  // el dos para leer, el 3 para escribir
	
	String  _Host = ConfigurationDAO.getConfiguration("TWS_HOST").getValue();
	int  _Port = Integer.parseInt(ConfigurationDAO.getConfiguration("TWS_PORT").getValue());
	
	
	SimpleDateFormat sdf = new SimpleDateFormat ("yyyyMM");
	SimpleDateFormat sdfFull = new SimpleDateFormat ("yyyyMMdd HH:mm:ss");
	
	SimpleDateFormat sdfMedium = new SimpleDateFormat ("yyyyMMdd");	
	
	SimpleDateFormat sdfFutDates = new SimpleDateFormat ("dd/MM/yyyy");
	
	
	
	
	
	/* recorremos mercados */
	LogTWM.getLog(Trading_Verify_ContractDetails.class);	
	LogTWM.log(Priority.INFO, "Starting Trading_Verify_ContractDetails process");
		
	TIMApiGITrader oTWS = new TIMApiGITrader(_Host,_Port, CLIENT_ID);	
	/* activamos el scheduler de MYSQL por si no lo está.No me tira desde el my.ini */
	TradingMarketDAO.StartTradingSchedulerMYSQL();
	
	//
	
	Contract oContrat = null;
	Market oMarket = null;
	/* VERIFICAMOS MERCADOS ACTIVOS */
    java.util.List<Market> lMarket = null;
    java.util.List<Share> lShare = null;
    
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
    
   /*  LastOrder =  OrderDAO.LastOrderID();
    
    
    if (LastOrder!=null)
    {
    	UniqueORDERID =LastOrder.getOrderID().intValue()+1 ;
    }*/
   // MyLog.log(Priority.INFO, "UniqueORDERID" + UniqueORDERID);
    
    
        
    Calendar DateMinMaxFrom =null;
	Calendar DateMinMaxTo =null;
    
    
	String _Expiration = "";
    
    
   /*   if (!bAllRequested)
    {
    */	
    	 // empezamos a contar desde 120 MINUTOS de la apertura de cada mercado por ser verificación 
    	 
    	List<Market> lActiveMarkets = MarketDAO.getListActiveMarket();
    			
    			
    			//MarketDAO.getListActiveMarketBtHours(Utilidades.getActualHourFormatPlusMinutes(Utilidades.getActualHourFormat(),120), 1,null);
    	//List<Market> lActiveMarkets = MarketDAO.getListActiveMarketBtHours(Utilidades.getActualHourFormat(), 1,null);
    	
	    lMarket = lActiveMarkets;
		
	    if (lMarket!=null)
	    {
	    	
	    	/* recorremos mercados */
	    	LogTWM.getLog(Trading_Verify_ContractDetails.class);
	    	
	    	//LogTWM.log(Priority.ERROR, "oMarket:");
	    	
	    	for (int j=0;j<lMarket.size();j++)
	    	{
	    		
	    		if (!bToRequest)
    			{
	    		
	    		 oMarket = lMarket.get(j);
	    		
	    		 /* ACCIONES SIN VERIFICAR */
    			
	    		lShare = ShareDAO.getListActiveShareByMarketNotVerified(oMarket.getMarketID());
	    		
	    			    		
	    		
	    		if (lShare!=null)
	    		{
	    			
	    			//MyLog.log(Priority.INFO, "Recorremos acciones lShare!=null" + String.valueOf(lShare!=null) + "|Size:" + lShare.size());
	    		
		    		for (int z=0;z<lShare.size();z++)
		        	{

		    			
		    			if (!bToRequest)
		    			{
		    			
		    			
		    			/* COGEMOS LA PRIMERA ACCION QUE NO HAYA SIDO VERIFICADA  EL CONTRATO.
		    			 * LA FECHA DE VERIFICACION DEBE SER DE HOY*/
		    			 oShare = (Share)lShare.get(z);
		    			 
		    			 
		    			 
		    			 _Expiration = "";
		      		    if (oShare.getExpiry_date()!=null)
		      		    {
		      		    	
		      		    
		    				_Expiration = sdf.format(oShare.getExpiry_date());
		    				
		    				/* yyyyMMdd */
		    				String _ExpiredDate = sdfMedium.format(oShare.getExpiry_date());
		    				String _NowDate = Utilidades.getDateNowFormat();
		    				
		    				String _sNewFutExpDate = "";
		    				
		    				if (_NowDate.compareTo(_ExpiredDate)>0)    /* EXPIRED??? */
		    				{
				      		    try {
				      		    	
				      		    	if (oShare.getExpiry_expression()!=null)
				      		    	{
				      		    		
				      		    	
										_sNewFutExpDate = Utilidades.getActiveFutureDate(Utilidades.getExpFutMonths(oShare.getExpiry_expression()), 
												Utilidades.getExpFutDayOfWeek(oShare.getExpiry_expression()),
													Utilidades.getExpFutWeek(oShare.getExpiry_expression()));
										
										java.util.Date _dNewFutExpDate = sdfFutDates.parse(_sNewFutExpDate);
										
										_Expiration = sdf.format(_dNewFutExpDate);
										
										oShare.setExpiry_date(new Timestamp(_dNewFutExpDate.getTime()));
										
				      		    	}
									/* ACTUALIZAMOS CON LA NUEVA FECHA DE EXPIRACION */
									ShareDAO.updateShare(oShare);
									
									LogTWM.log(Priority.INFO, "Expiration Date :" + oShare.getExpiry_date());
									
									
								} catch (XPathExpressionException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (ParserConfigurationException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (SAXException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (IOException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								} catch (ParseException e) {
									// TODO Auto-generated catch block
									e.printStackTrace();
								}
				      		    
				      		    
				      		    
		    				}
		      		    
		      		  
		      		    }
		      		    

		    		
		      		    
    					LogTWM.getLog(Trading_Verify_ContractDetails.class);
    					LogTWM.log(Priority.INFO, "Verifying Contract :" + oShare.getSymbol() );
    					bToRequest = true;
		    			
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
    	  // LogTWM.log(Priority.INFO, "Expiration Date2 :" + _Expiration);
    		
    	   
    	   if (oShare.getSecurity_type().equals("FUT"))		
    	   {
				oContrat = new FutContract( oShare.getSymbol(), _Expiration);
				
				//oContrat.multiplier(String.valueOf(oShare.getMultiplier()));		    					
				oContrat.exchange(oShare.getExchange());
				oContrat.currency(oMarket.getCurrency());
    	   }
			else		    					
				oContrat = new StkContract( oShare.getSymbol());
    	   
		//	oContrat = (Contract) oTWS.createContract(oShare.getSymbol(), oShare.getSecurity_type(),oShare.getExchange(),oMarket.getCurrency(), _Expiration, "", 0);
			
			LastOrder =  OrderDAO.LastOrderID();
		    
		    if (LastOrder!=null)
		    {
		    	UniqueORDERID =LastOrder.getOrderID().intValue()+CLIENT_ID ;
		    }
			
			/* insertamos control de ordenes de peticion */
			OrderDAO.addOrder(UniqueORDERID, oShare.getShareId().intValue());
			
			
			oTWS.set_CONTRACT_DATA_REQUEST("Requested");
			oTWS.GITradergetContractDetails(UniqueORDERID, oContrat);
			
			//UniqueORDERID+=1;
			
			LogTWM.log(Priority.INFO, "Waiting OK ContractDetails...");
			
			Calendar DateIni = Calendar.getInstance();
			
			/* HYA QUE PONER UN TIMEOUT DE 3 SEGUNDOS MAXIMO */
			int MAX_SECONDS_WAIT = 4;
			int execution_time=0;
			
			 while (!oTWS.get_CONTRACT_DATA_REQUEST().equals("Ended") &&  execution_time <MAX_SECONDS_WAIT )				   
			{
				 	Calendar DateEnd = Calendar.getInstance();
				 	execution_time = Utilidades.secondsDiff(DateIni.getTime(),DateEnd.getTime());
//				 	LogTWM.log(Priority.INFO, "oContrat.m_symbol:" + ":" + oContrat.m_symbol + "," + "_HISTORICAL_DATA_REQUEST:" + oTWS._HISTORICAL_DATA_REQUEST + ",Execution Time:"  + execution_time);
			
			}
		//	oTWS.disconnectFromTWS();
			
			if (!oTWS.get_CONTRACT_DATA_REQUEST().equals("Ended"))
			{
				LogTWM.log(Priority.INFO, "TimeOut reached...");
				oTWS.set_CONTRACT_DATA_REQUEST("Stopped");   // estado inicial
			}
			else
			{				
				LogTWM.log(Priority.INFO, "getContractDetails OK.");
			}
			
			
			//System.exit(-1);
			
	         
        } // fin if  (bToRequest)
	 
	    
	    if (oTWS.GITraderTWSIsConnected())
		{
			oTWS.GITraderDisconnectFromTWS();
		}    
	    
	}  // fin if (oTWS.isConnected())
    else
	{
		
		LogTWM.log(Priority.INFO, "Ending Trading_Verify_ContractDetails..No conectado a la TWS");
		if (oTWS.GITraderTWSIsConnected())
		{
			oTWS.GITraderDisconnectFromTWS();
		}
	}	
	
	
}
	public static void main(String[] args) throws Exception {
 		// TODO Auto-generated method stub
    	 //List<Position> Lista = PositionDAO.getTradingPositions(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		StartUp();
 	}
}
