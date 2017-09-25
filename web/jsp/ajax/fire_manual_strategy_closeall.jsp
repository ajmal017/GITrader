<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.tim.dao.StrategyDAO" %>
<%@page import="com.tim.dao.PositionDAO" %>
<%@page import="com.tim.model.Position" %>
<%@page import="com.tim.model.Share" %>
<%@page import="com.tim.model.Strategy" %>
<%@page import="java.util.Calendar" %>
<%@page import="com.tim.util.*" %>



<%

//[1,2,3] TODAS LAS STRATEGIAS AFECTADAS POR ESTE PARAMETRO..MIN MAX Y CLOSE ALL
String _IdStrategy= ConfigKeys.STRATEGY_SELL_CLOSEALLPOSITIONS  + "|" + ConfigKeys.STRATEGY_BUY_MIN_MAX;     
String _Action="";  // [stop, start] 
if (request.getParameter("strat_action")!=null)
	_Action = request.getParameter("strat_action");

/* SI ES START */
/* COPIAMOS EL VALOR ORIGINAL DE DEADLINE PARA METER UN VALOR MUY GRANDE Y LO COPIAMOS */
/* SI ES STOP */
/* RESTAURAMOS  */

/* MIRAMOS POSICIONES, SI NO HAY, NO HACEMOS NADA */
if (PositionDAO.ExistsPositionToClose(null))
{	


String[] aStrategy = _IdStrategy.split("\\|");



for (int j=0;j<aStrategy.length;j++)
{
	
	
	Strategy oStrategy  = StrategyDAO.getStrategy(new Long(aStrategy[j]));
	if (oStrategy!=null)
	{	
		if (_Action.equals("start"))
		{
			oStrategy.setTmp_sell_all_deadline_min_toclose(oStrategy.getSell_all_deadline_min_toclose());
			/* MINUTOS ANTES DEL CIERRE. PONEMOS UN NUMERO MUY GRANDE, 30 min al cierre.
			por defecto. 60min * 8 horas de mercado = 480*/
			
			oStrategy.setSell_all_deadline_min_toclose(480);
			
			StrategyDAO.updateStrategyByStrategyID(oStrategy);
			
		}
		/* restauramos la config inicial */
		if (_Action.equals("stop"))
		{
			
			oStrategy.setSell_all_deadline_min_toclose(oStrategy.getTmp_sell_all_deadline_min_toclose());
			
			oStrategy.setTmp_sell_all_deadline_min_toclose(null);
			
			StrategyDAO.updateStrategyByStrategyID(oStrategy);
			
		}
			
	}	

}

	out.println("OK. Configuración guardada correctamente.");
}   // NO HAY POSICIONES, NO HACEMOS NADA
else
{
	out.println("NOOK.No puede iniciarse el proceso. No hay posiciones abiertas.");
}

%>
 