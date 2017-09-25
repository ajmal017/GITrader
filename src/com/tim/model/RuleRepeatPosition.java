/* 1.
 * BASICAMENTE SE ENCARGA DE VERIFICAR SI HAY QUE ENTRAR OTRA VEZ EN UN VALOR DADO.
 * ENTONCES, PARA CADA POSICION Y TIPO DE ENTRADA, SYMBOL + TYPE, PUEDE PASAR
 * 1. QUE HAYA BENEFICIO, NO ENTRAMOS MAS
 * 2. QUE HAYA PERDIDA, DAMOS OTRA OPORTUNIDAD DADO POR EL PARAMETRO.
 */

package com.tim.model;

import java.sql.Timestamp;
import java.util.Calendar;

import org.apache.commons.dbutils.handlers.ScalarHandler;
import org.apache.log4j.Priority;

import com.ib.client.Contract;
import com.ib.client.Order;
import com.tim.dao.MarketDAO;
import com.tim.dao.OrderDAO;
import com.tim.dao.PositionDAO;
import com.tim.dao.RealTimeDAO;
import com.tim.service.TIMApiGITrader;
import com.tim.util.LogTWM;
import com.tim.util.PositionStates;
import com.tim.util.Utilidades;
import com.tim.model.Position;


/* 1.
 * BASICAMENTE SE ENCARGA DE VERIFICAR SI HAY QUE ENTRAR OTRA VEZ EN UN VALOR DADO.
 * ENTONCES, PARA CADA POSICION Y TIPO DE ENTRADA, SYMBOL + TYPE, PUEDE PASAR
 * 1. QUE HAYA BENEFICIO, NO ENTRAMOS MAS
 * 2. QUE HAYA PERDIDA, DAMOS OTRA OPORTUNIDAD DADO POR EL PARAMETRO DE LA ESTRATEGIA
 * 
 * 
 * NOTA : SOLO PUEDO VERIFICAR EL CASO MAS EXTREMO. ES DECIR, SI HUBO COMPRA Y VENTA 
 * CON BENEFICIO AMBAS...ENTONCES, NO SE PUEDE COMPRAR MAS
 * EL RESTO DE PARAMETROS, SOLO ES VERIFICABLE CON LA SEÑAL Y TIPO DE ENTRADA...BUY O SELL.
 */
public class RuleRepeatPosition extends Rule {

	
	public RuleRepeatPosition(){
		super();
		this.JSP_PAGE = "/jsp/admin/rule/rulerepeatposition.jsp";
		
	}
	public boolean Verify(Share ShareStrategy, Market oMarket) {
		// TODO Auto-generated method stub
		
		boolean verified = false;
		LogTWM.getLog(RuleRepeatPosition.class);									
		
		
		int _TotalPositionDay =0;
		
		/* BENEFICIO EN AMBAS, SE CIERRA */ 
		boolean bProfitBUY = false;
		boolean bProfitSELL = false;
		
		try
        {
			
			// verificamos posición
			// no debe haber posición abierta 			
			//Position oLastPosition = null;
			if (!PositionDAO.ExistsPositionShareOpen(ShareStrategy.getShareId().intValue()))  // no posicion abierta		
			{
			
					java.util.List<Position> lPosition = PositionDAO.getTradingPositions(ShareStrategy.getShareId().intValue());
					if (lPosition!=null && lPosition.size()>0)   // si posicion previa.Verificamos que si hay beneficio en las dos, se cierra
					{
						for (Position oLastPosition: lPosition)
						{
							// buy
							if (oLastPosition.getType().equals(PositionStates.statusTWSFire.BUY))
								bProfitBUY = oLastPosition.getPrice_real_buy() <oLastPosition.getPrice_real_sell();
							else  // venta 
								bProfitSELL = oLastPosition.getPrice_real_buy() > oLastPosition.getPrice_real_sell();						
						
								_TotalPositionDay +=1;	
						}
						
						// buscamos parametro de Operativa maxima en el dia por valor.
						// no aplica el sentido o tipo, sino maximo operativa en el dia por valor. 0 no aplica						
						if (this.getBuy_max_positionday_share()!=null && !this.getBuy_max_positionday_share().equals(new Long(0)))
						{
							verified = this.getBuy_max_positionday_share().intValue()>_TotalPositionDay;
							/* LogTWM.log(Priority.INFO, ShareStrategy.getSymbol() + this.getBuy_max_positionday_share().intValue() 
									+  ">" + _TotalPositionDay); */
						}
						
						// si cumple con los datos de maximos en el dia
						if (verified)
						{
							// 	hay ambos dos beneficios???	
							verified =	!(bProfitBUY && bProfitSELL);
						}
						/* if (!verified)
							LogTWM.log(Priority.INFO, ShareStrategy.getSymbol() + " ambos dos operaciones  beneficios o máxima operativa por valor :" + !verified);
						
						*/
						
						
					}
					else  // no posicion previa...se cumple la regla
						verified = true;
								
			}
			else  // posicion abierta
				verified = false;
			
			
				
			
        }
		
		catch (Exception er)
        {
			LogTWM.log(Priority.ERROR, er.getMessage());
			er.printStackTrace();
			verified = false;
        }

		return verified;
	}
	
	public static void main(String[] args) throws Exception {
 		// TODO Auto-generated method stub
    	 //List<Position> Lista = PositionDAO.getTradingPositions(new Timestamp(Calendar.getInstance().getTimeInMillis()));
		java.sql.Timestamp Hoy = new Timestamp(Calendar.getInstance().getTimeInMillis());
		
		Market oMarket = MarketDAO.getMarket(new Long(2));

		Double TotalAmountTrading = PositionDAO.getTotalAmountPosicion(Hoy, Hoy,oMarket, null);
		
		
 	}
	
	
	
}
