package com.tim.jobs;

import java.util.Calendar;
import java.util.List;
import java.util.UUID;
import java.util.logging.Logger;

import org.apache.log4j.Priority;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.StatefulJob;
import org.quartz.impl.StdSchedulerFactory;

import com.mchange.v2.log.MLevel;
import com.mchange.v2.log.MLog;
import com.mchange.v2.log.log4j.Log4jMLog;
import com.tim.dao.ConfigurationDAO;
import com.tim.dao.MarketDAO;
import com.tim.dao.MarketShareDAO;
import com.tim.dao.OrderDAO;
import com.tim.dao.PositionDAO;
import com.tim.dao.RuleDAO;
import com.tim.dao.ShareDAO;
import com.tim.dao.Share_StrategyDAO;
import com.tim.dao.StrategyDAO;
import com.tim.dao.TradingMarketDAO;
import com.tim.model.Market;
import com.tim.model.Position;
import com.tim.model.Rule;
import com.tim.model.Share;
import com.tim.model.Share_Strategy;
import com.tim.model.Strategy;
import com.tim.model.Trading;
import com.tim.model.Trading_Market;
import com.tim.service.TIMApiGITrader;
import com.tim.service.TIMApiGITrader;
import com.tim.util.ConfigKeys;
import com.tim.util.LogTWM;
import com.tim.util.PositionStates;
import com.tim.util.Utilidades;

@SuppressWarnings("deprecation")
public class Trading_Actions extends Thread implements StatefulJob  {

	/**
	 * @param args
	 * @throws Exception 
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
	
		public static void StartUp() throws InterruptedException
		{
			/* TENEMOS TAREAS CON EL TIEMPO REAL DE CADA ACCION 
			 * 
			 * 1. ACCEDER A LAS ACCIONES DEL MODELO DE ACCION
			 * 1.5. VERIFICAR QUE NO HAY OPERACION DE ESE MODELO PENDIENTE
			 * 2. TESTEAR SI SE CUMPLE ALGUNA. VERIFICAR MAXIMOS Y MINIMOS  
			 * 3. SI SE CUMPLE...LANZAR OPERACION ... GUARDAR OPERACION DE COMPRA VENTA Y CONTROLAMOS.
			 * 4. SEGUIMOS MIENTRAS HAYA ACCIONES CON POSIBILIDADES.
			 * 5. falta controlar cuando entra el modelo (OffSet con respecto a la hora inicil de mercado?)
			 * PENSAR EN LOS BUCLES HACERLOS DENTRO...
			 * */
				
			int CLIENT_ID = 4;	  // el dos para leer, el 3 para escribir
			
			String  _Host = ConfigurationDAO.getConfiguration("TWS_HOST").getValue();
			
			String  _AccountNameIB = ConfigurationDAO.getConfiguration("ACCOUNT_IB_NAME").getValue();
			
			
			int  _Port = Integer.parseInt(ConfigurationDAO.getConfiguration("TWS_PORT").getValue());
			
				
				

			// TODO Auto-generated method stub
			
				
			Trading  oTradingToday = null;
			oTradingToday = (Trading) TradingMarketDAO.getListActiveAllTradingToday(); 

			List<Trading_Market> oSharesMarket;
			List<Rule> olRules;
			Market oMarket;
			
			/* PARA VERIFICAR LA OPERATIVA, LANZAMOS ORDENES IMPARES TICKETSID*/
			/* PARA VERIFICAR LA LECTURA, LANZAMOS ORDENES PARES..TIEMPO REAL 
			
			 int UniqueORDERID = 0;
			 int UniquePOSITIONID;
			
			 com.tim.model.Order LastOrder = OrderDAO.LastOrderID();	 
			 
			 if (LastOrder!=null)
			 {
			    UniqueORDERID = LastOrder.getOrderID().intValue()+1 ;
			    if (Utilidades.IsPar(UniqueORDERID))	    		
			    	UniqueORDERID++;  // dejamos impares para operativa
			 }
			*/
			
			LogTWM.getLog(Trading_Actions.class);
			
			List<Market> lActiveMarkets = MarketDAO.getListActiveMarketBtHours(Utilidades.getActualHourFormat(), 1, 1);
			
			
			LogTWM.log(Priority.INFO, "Starting Trading_Actions Process..");
			
			if (lActiveMarkets.size()>0)
			{
				
			LogTWM.log(Priority.INFO, "Active Markets > 0");	
			
			if (oTradingToday!=null)
			{
				
				// creamos el wrapper con el 3
				
			 	TIMApiGITrader oTWS = new TIMApiGITrader(_Host,_Port, CLIENT_ID);
				
				
				LogTWM.log(Priority.INFO, "Trading True");
				//
				
				if (oTWS.GITraderTWSIsConnected())
				{
					oTWS.GITraderDisconnectFromTWS();
				}
				
				
				if (!oTWS.GITraderTWSIsConnected())
					oTWS.GITraderConnetToTWS();
				
				
				if (oTWS.GITraderTWSIsConnected())
				{ 
					
					LogTWM.log(Priority.INFO, "Connected True");
					
					
							    
				/* LANZAMOS LA OPERACION DE CONTINUO */ 
				    
				while (true)
				    	
				{		
					
				
				
				oSharesMarket = TradingMarketDAO.getListActiveTradingMarketShares(oTradingToday.getTradingID().intValue()); 
				//oTradingToday
				
				
				
				/* 16.01.2013. COMO LA ESTRATEGIA DE VENDER O CERRAR POSICIONES PUEDE ESTAR LANZANDOSE, 
				 * HABRIA QUE ACTUALIZAR PARA QUE SE LANCE CUANDO YA NO HAYA NADA QUE VENDER Y RESTAURAR SU ESTADO
				 */
				
				if (oSharesMarket!=null)
				{
					
				
				
			    	for (int j=0;j<oSharesMarket.size();j++)
			    	{
			    		
			    		/* COGEMOS LOS CAMBIOS DE LAS ESTRATEGIAS EN CALIENTE CADA ITERACION PARA TRATAR LOS CAMBIOS 
			    		
			    		// hay acciones???, P
			    		
			    		/* recorremos mercados y acciones  para verificar las reglas. Por reflexion, creamos la factoria
						 * de objetos que implementa cada regla */
						olRules = RuleDAO.getListRules(oTradingToday.getTradingID().intValue());
						try {
							oTradingToday.setlRules(Utilidades.LoadRules(olRules));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
							
							
						/* recorremos mercados y acciones  para verificar las estrategias. Por reflexion, creamos la factoria
						 * de objetos que implementa cada strategia */
						List oStrategy = StrategyDAO.getListStrategies(oTradingToday.getTradingID().intValue());
						try {
							oTradingToday.setlStrategies(Utilidades.LoadStrategies(oStrategy));
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						
			    		
			    		
			    		
			    		Trading_Market oShareMarket  = oSharesMarket.get(j);
			    		
			    		oMarket  = (Market) MarketDAO.getMarket(oShareMarket.getMarketId());
			    		
			    		Share oTradingShare = ShareDAO.getShare(oShareMarket.getShareId());
			    		
			    		// cambio 6.12.2013..Verificamos que la acción esté activa para el trading de hoy, 
			    		// cambio 03.03.2013...solo para comprar, no para vender una posicion, lo pasamos a la strategy
			    		
			    		
			    		java.util.List<Share_Strategy> _lStratOfShare = Share_StrategyDAO.getListStrategiesByShare(oTradingShare.getShareId(),false);
			    		
			    		// verificamos la estrategias existentes.
			    		if (oTradingToday.getlStrategies()!=null && _lStratOfShare!=null)
			    		{	
			    			
			    			
			    			boolean bCloseAllStrVerified = false;  // caso de cierre por panico
			    			
			    			
			    			
			    			for (int k=0;k<oTradingToday.getlStrategies().size();k++)	    				
			    	    	{
			    				// habra reglas que haya que cumplir...una operacion de compra previa...p.e.
			    				
			    				// verificamos si hay posibilidad de ejecución por cada operativa
			    				boolean bRULES_OK = true;
			    				Strategy MyStrategy =  oTradingToday.getlStrategies().get(k);
			    				for (int h=0;h<oTradingToday.getlRules().size();h++)	    					
				    	    	{
				    				// habra reglas que haya que cumplir...una operacion de compra previa...p.e.
			    					Rule MyRule = (Rule) oTradingToday.getlRules().get(h);
			    					// BUSCAMOS REGLAS GENERALES Y REGLAS ASOCIADAS A LAS VENTAS.
			    					
			    					/* REGLA GLOBAL, SE APLICA A TODOS  */
			    					if (MyRule.getType()==null)
			    					{	
			    						if (!MyRule.Verify(oTradingShare, oMarket)) /* NO SE CUMPLE, SALIMOS */
			    						{
			    							bRULES_OK = false;
			    							continue;	    							
			    						}
			    					}
			    					/* HAY UN REGLA DE COMPRA, VERIFICAMOS QUE LA CUMPLA SI ES UNA ESTRATEGIA IGUAL
			    					 * Y ES SOLO LA DE MAXIMOS Y MINIMOS */
			    					if (MyRule.getType()!=null && MyRule.getType().equals(MyStrategy.getType()) && 
			    							MyStrategy.getStrategyId().equals(ConfigKeys.STRATEGY_BUY_MIN_MAX))
			    					{
			    						if (!MyRule.Verify(oTradingShare, oMarket)) /* NO SE CUMPLE, SALIMOS */
			    						{
			    							bRULES_OK = false;
			    							continue;	    							
			    						}
			    					}
			    					
			    				
				    	    	}
			    				boolean bACTIVE_TRADINGOK = true;
			    				
			    				
			    				boolean _bSTRATEGY_IN_SHARE = false;
			    				/* si la estrategia esta en la accion ? */
			    				if (Utilidades.fn_IsStrategyInShareStrategies(MyStrategy.getStrategyId(),_lStratOfShare))
			    				{
			    					_bSTRATEGY_IN_SHARE = true;
			    				}
			    				
			    				// verificamos que si es una estrategia de entrada, vemos si esta activa para trading.
			    				// añadimos casos de futuros no expirados.
			    				if (MyStrategy.getType()!=null && MyStrategy.getType().equals(PositionStates.statusTWSFire.BUY.toString())
			    						&& oTradingShare.getActive_trading().equals(new Long(0)))
					    		{ 
			    					bACTIVE_TRADINGOK = false;
					    		}
			    				
			    				// para operar, el trading tiene que estar OK ademas de estar la estrategia para la accion
			    				bACTIVE_TRADINGOK = bACTIVE_TRADINGOK && _bSTRATEGY_IN_SHARE;
			    				
			    				
			    				MyStrategy.setACCOUNT_NAME(_AccountNameIB); 
			    				/* VERIFICADAS LAS REGLAS TB */
			    				if (bRULES_OK && bACTIVE_TRADINGOK  &&  MyStrategy.Verify(oTradingShare, oMarket))
			    					MyStrategy.Execute(oMarket,oTradingShare,oTWS);
			    				
			    				
			    				/* CASO ESPECIAL ESTRATEGIA DE CIERRE DE POSICIONES A UNA HORA DETERMINADA. 
			    				 * SE CIERRAN ANTES DE LA CONFIGURACION ORIGINAL.MIRAMOS EL CAMPO Y VEMOS SI ESTA
			    				 * ACTIVADO EL FLAG, ENTONCES, MIRAMOS SI NO HAY POSICIONES PARA CERRAR Y RESTAURAMOS. */
			    				
			    				 
			    				
			    				// !MyStrategy.getTmp_sell_all_deadline_min_toclose().equals(new Integer(0))
			    				if ((MyStrategy.getStrategyId().equals(ConfigKeys.STRATEGY_SELL_CLOSEALLPOSITIONS) ||
			    						MyStrategy.getStrategyId().equals(ConfigKeys.STRATEGY_BUY_MIN_MAX))
			    						&& MyStrategy.getTmp_sell_all_deadline_min_toclose()!=null &&
			    						!MyStrategy.getTmp_sell_all_deadline_min_toclose().equals(new Integer(-1)))
			    				
			    				{			    					
			    					
			    						/* MIRAMOS POSICIONES, SI NO HAY, SE ACABO EL CIERRE, 
			    						 * RESTAURAMOS EL DEADLINE ORIGINAL QUE LO TENGO EN EL TMP, LAS ESTRATEGIAS */  
			    						if (!PositionDAO.ExistsPositionToCloseByType(MyStrategy.getSell_all_deadline_type_operation()))
			    						{	
			    								
			    							    bCloseAllStrVerified = true;
			    							
			    								// LA ESTRATEGIA CONTEMPLA EL SEGUIR OPERANDO
			    								// POSTERIORMENTE..VERIFICAMOS Y ACTUALIZAMOS, LO HACEMOS TB EN EL CIERRE DE POSICIONES */
			    								// LOS PARAMETROS COMODIN DE LA ESTRATEGIA...CON LA ULTIMA, ACTUALIZAMOS EL TRADING DE LAS ACCIONES
			    								if (MyStrategy.getSell_all_deadline_deactivate_trading()!=null && MyStrategy.getSell_all_deadline_deactivate_trading().equals(new Integer(1)))
			    								{
			    									//ShareStrategy.setActive_trading(new Long(0));
			    									ShareDAO.updateMarketTradingAllShares(oShareMarket.getMarketId(), 0);
			    								}
			    								
			    						  
			    						}
			    						
			    						if (bCloseAllStrVerified)
			    						{
			    						
				    						MyStrategy.setSell_all_deadline_min_toclose(MyStrategy.getTmp_sell_all_deadline_min_toclose());
		    								MyStrategy.setTmp_sell_all_deadline_min_toclose(-1);
		    								MyStrategy.setSell_all_deadline_type_operation("ALL");
		    								MyStrategy.setSell_all_deadline_deactivate_trading(null);
		    								
		    								StrategyDAO.updateStrategyByStrategyID(MyStrategy);
		    								
			    						}
			    						
			    						
			    				
			    				}  /* FIN CASO ESPECIAL ESTRATEGIA DE CIERRE DE POSICIONES */
			    				
			    	    	}
			    		}
			    			
			    		
			    	 /* 	}	// fin cambio 6.12.2013..Verificamos que la acción esté activa para el trading de hoy */
			    		
			    		
			    		
			    	}	//fin de acciones
				}
				
				// una vez que estamos dentro..verificamos que haya conexion y este el mercado abierto.
				List<Market> lActiveMarketsCheck = MarketDAO.getListActiveMarketBtHours(Utilidades.getActualHourFormat(), 1, 1);
				boolean IsConnected = oTWS.GITraderTWSIsConnected();		   // en debug está siempre a false		
				if (lActiveMarketsCheck==null ||  !IsConnected)
				{
					if (IsConnected)
					{
						oTWS.disconnectFromTWS();
						
					}
					LogTWM.log(Priority.INFO, "Ending Trading_Actions Process..There is no open market or console closed");
					System.exit(-1);
				}
					
				
				
				}  // fin while..condicion de salida...no hay mercado o este no este conectado.
			    
			}  // fin is connected 
				else
				{
					//if (!oTWS.isConnected()) oTWS.disconnectFromTWS();
					//oTWS.destroy();
					LogTWM.log(Priority.INFO, "Ending Trading_Actions Process..disconnectFromTWS()");	
				}	
			}	//fin hay trading
			else
			{
				
				LogTWM.log(Priority.INFO, "Ending Trading_Actions Process.No existe trading para la fecha de hoy.");
			}
			}
			else
			{
				LogTWM.log(Priority.INFO, "Ending Trading_Actions Process.No market open");
			}

		}

		public static void main(String[] args) throws Exception {
	 		// TODO Auto-generated method stub
	    	 //List<Position> Lista = PositionDAO.getTradingPositions(new Timestamp(Calendar.getInstance().getTimeInMillis()));
			StartUp();
			
	 	}
		
		
}
