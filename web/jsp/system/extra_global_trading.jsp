<%@page import="com.tim.dao.StrategyDAO"%>
<%@page import="java.util.List"%> 
<%@page import="java.util.ArrayList"%>
<%@page import="com.tim.dao.MarketDAO"%>
<%@page import="com.tim.dao.MarketShareDAO"%>
<%@page import="com.tim.dao.OrderDAO"%>
<%@page import="com.tim.dao.PositionDAO"%>
<%@page import="com.tim.dao.ShareDAO"%>
<%@page import="com.tim.dao.TradingMarketDAO"%>
<%@page import="com.tim.model.Market"%>
<%@page import="com.tim.model.Strategy"%>
<%@page import="com.tim.model.Share"%>
<%@page import="com.tim.model.Trading"%>
<%@page import="com.tim.model.Trading_Market"%>
<%@page import="com.tim.dao.StrategyDAO" %>
<%@page import="com.tim.util.ConfigKeys" %>

<%
// puede haber varias estrategias afectadas, pero actualizamos todas y cogemos una para mostrar datos.
Strategy oStrategy = null;
oStrategy  = StrategyDAO.getStrategy(new Long(ConfigKeys.STRATEGY_SELL_CLOSEALLPOSITIONS));
%>


<script>

 var _TextoMensaje = "";
 function MarketSell()
 {
	 
	 strat_action = "stop";
	 if (_TextoMensaje =="Start")
		 strat_action = "start";
		 
	 
	 
	 $.post("ajax/fire_manual_strategy_closeall.jsp", { strat_action : strat_action},
			  function(data) {
					// cambiamos la imagen
				
				  if (data.indexOf("NOOK")==-1)   // NO ERROR
				  {
				  	if (_TextoMensaje=='Start')
				  		    _TextoMensaje= "In Progress..";				  			
				  	else
				  			_TextoMensaje= "Start";
					
				  	$("#marketAction").val(_TextoMensaje);
				  }
				  $("#dialog").html("<p><b>" + data + "</b></p>");
				  $("#dialog" ).dialog();

					
			  });
	 
 }
 </script>
 



<div id="dialog" title="Aviso">
  <p></p>
</div>
<div class="borde_bloque"><span class="titulo_bloque"></span>
 <div id="syst_t">
	    		
	<div class="lista_acciones_trading">Operaciones Intradia </div>
	<table cellpadding="5" cellspacing="5">
	<tr class="header">
	<td>Nombre</td><td>Descripción</td><td></td>
	</tr>
	
	
	<% 
	if (oStrategy!=null)
	{
		String _TextoAccion = "Start";
		
		String _class= "green";
		// si el tmp esta distinto de null, quiere decir que hay algo arrancado previo
		
		if (oStrategy.getTmp_sell_all_deadline_min_toclose()!=null && !oStrategy.getTmp_sell_all_deadline_min_toclose().equals(new Integer(0)))
		{	
			_TextoAccion = "In Progress..";
			
		
		}
		
	%>
	<script>
		_TextoMensaje = '<%=_TextoAccion%>';	 	
	</script>
	<tr class="data">
			<td>Venta Global a Mercado</td> 				
			<td>Opción que permite lanzar a mercado de todas las posiciones abiertas si hubiera.
			</td>
			<td>			
			<%/* MIRAMOS POSICIONES, SI NO HAY, NO HACEMOS NADA */
			
			if (PositionDAO.ExistsPositionToClose(null))
			{ %>
				<input id="marketAction" onclick="MarketSell()" type="button" class="button large <%= _class%>" value="<%=_TextoAccion%>"/>
			<%
			}
			else { %>
				<input id="marketAction"  type="button" class="button medium red" value="No hay Posiciones Abiertas"/>
			<% }
			 %>
				
			</td>
			</tr>
	<% 
	}
	%>
</table>	
 </div> </div>
