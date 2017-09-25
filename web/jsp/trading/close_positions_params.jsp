<%@page import="com.tim.util.PositionStates"%>
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

function _DoNo()
{
	
}

function _DoYes()
{
	var _Checked = "";
	if ($("#desactivar_trading").attr('checked')=="checked") 
			_Checked = "checked";
			
	$.post("ajax/fire_manual_strategy_closeall_advanced.jsp", { strat_action : "start",typeoperation : $("#operations").val(), disabletrd : _Checked },
			  function(data) {
					// cambiamos la imagen
				
				  if (data.indexOf("NOOK")==-1)   // NO ERROR
				  {
				  	
				  	window.location.href="inicio.jsp";
									  	
				  	
				  }
				  else
				  {
					  $("#dialog").html("<p><b>Error con la solicitud</b></p>");					  
					  $("#dialog").dialog({
						      modal: true, title: 'Aviso', zIndex: 10000, autoOpen: true,
						      width: 'auto', resizable: false,
						      buttons: {
						          Aceptar: function () {
						        	  _DoNo();
						            //  $(this).dialog("close");
						          }
						      },
						      close: function (event, ui) {
						          $(this).remove();
						      }
						});
				  }

					
			  });	
	
}

 var _TextoMensaje = "";
 
 
 function MarketSell()
 {
	 
	 	 
	 	 $("#dialog").html("¿Está seguro de proceder con el cierre de posiciones?.<br>Durante el proceso, es recomendable no tocar otra configuración hasta que haya finalizado");
		 $("#dialog").dialog({
		      modal: true, title: 'Aviso', zIndex: 10000, autoOpen: true,
		      width: '300px', resizable: false,
		      buttons: {
		          Si: function () {
		        	  _DoYes();
		              //$(this).dialog("close");
		          },
		          No: function () {
		        	  _DoNo();
		            //  $(this).dialog("close");
		          }
		      },
		      close: function (event, ui) {
		          $(this).remove();
		      }
		});
		 
	 }
	 
		 

 </script>
 


<div id="dialog" title="Aviso">
  <p></p>
</div>
<div class="borde_bloque"><span class="titulo_bloque "></span>
 <div id="admin" class="admin">
	    		
	    		
	<div class="lista_acciones_trading"> <img class="img_cab"  src="<%=request.getContextPath()%>/images/stock-market-financial-dice-trading-6150113.jpg"> Cierre total de posiciones abiertas. 
	Por favor, introduce los parámetros siguientes
	<br/>
	1. Tipo Operación (Todas, Sell, Buy)<br>
	2. Desactivar trading (No seguir operando)
	</div>
	<% 
	if (!PositionDAO.ExistsPositionToClose(null))
			{ %>
				<div class="no_operation_to_close">No hay posibilidad de cerrar las posiciones</div>
			<%
			}
			else { %>
			<table  class="tmarket " cellpadding="5" cellspacing="5">
			<tr class="header">
			<td colspan="2">Parámetros</td>
			</tr>
			<%
			String _TextoAccion = "";
			String _Type = "";
			String _DisabledTrading = "";
			if (PositionDAO.ExistsPositionToClose(null))
			{ 
				 
				if (oStrategy!=null)
				{
										
					// si el tmp esta distinto de null, quiere decir que hay algo arrancado previo
					
					if (oStrategy.getTmp_sell_all_deadline_min_toclose()!=null && !oStrategy.getTmp_sell_all_deadline_min_toclose().equals(new Integer(-1)))
					{	
						_TextoAccion = "Procesando";
						_DisabledTrading = "checked";
						_Type = oStrategy.getSell_all_deadline_type_operation(); 
						
						
					
					}
					
			%>
			
			<tr>
			<td>
			<div class="altomin">
			<label class="w200px textleft flota_izq negrita" for="operations">Parcial/Total</label><select class="floatizq" name="operations" id="operations">
			<option value="ALL">All</option>
			<option value="<%=PositionStates.statusTWSFire.BUY.toString()%>"><%=PositionStates.statusTWSFire.BUY.toString()%></option>
			<option value="<%=PositionStates.statusTWSFire.SELL.toString()%>"><%=PositionStates.statusTWSFire.SELL.toString()%></option>
			</select>
			</div>
			
			<div class="altomin">
			<label class="w200px textleft flota_izq negrita" for="operations">Desactivar Trading</label>
			<input class="floatizq" type="checkbox" <%=_DisabledTrading%> name="desactivar_trading" id="desactivar_trading"/>
			</div>
			
			<div class="altomin">
			<label class="w200px textleft flota_izq negrita" for="estado" >Estado  (En proceso o no)</label>
			<input class="floatizq" type="text"  disabled="true" name="estado" id="estado" value="<%=_TextoAccion%>"/>
			</div>
			<div class="textleft negrita">
			
			<div class="flota_izq ancho70per izq40per relativo">
			<input  class="button aire_global" type="button" name="aceptar" value="Aceptar" onclick="MarketSell()"/>
			</div>
			<%
			}
			%>
			<div class="flota_izq">
			<input class="button aire_global" type="button" name="volver" value="Volver" onclick="window.history.back()"/>
			</div>
		</div>
			
			</td>
			</tr>
			<table>
			<% }
			 %>
	
	<script>
		$("#operations").val('<%=_Type%>'); 	
	</script>
	<% 
	}
	%>

 </div> </div>
