<%@page import="com.tim.util.TWSMail"%>
<%@page import="com.tim.util.PositionStates"%>
<%@page import="com.tim.service.TIMApiWrapper"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.List"%> 
<%@page import="com.tim.dao.MarketDAO"%>
<%@page import="com.tim.dao.MarketShareDAO"%>
<%@page import="com.tim.dao.OrderDAO"%>
<%@page import="com.tim.dao.PositionDAO"%>
<%@page import="com.tim.dao.ShareDAO"%>
<%@page import="com.tim.dao.TradingMarketDAO"%>
<%@page import="com.tim.model.Market"%>
<%@page import="com.tim.model.Position"%>
<%@page import="com.tim.model.Share"%>
<%@page import="com.tim.model.Trading"%>
<%@page import="com.tim.model.Trading_Market"%>
<%@page import="com.tim.util.Utilidades"%>

 
  
 <div class="borde_bloque"><span class="titulo_bloque"></span>
 <div class="admin">
 <div class="lista_acciones_trading">Posición</div>
 <script>
 
 var PositionSELL = '<%=PositionStates.statusTWSFire.SELL.toString()%>';
 var PositionBUY = '<%=PositionStates.statusTWSFire.BUY.toString() %>'; 
 
 function doFunctionForYes()
 {
 
 // con jquery y dialog da pete
 // validamos valores númericos
	 
	 document.forms[0].submit();

 
 }
 function doFunctionForNo()
 {
 	window.location.reload();
 }
 
 
 
 function go(action)
 {
$("#operation").val(action);
bError = false;
sError = "";

// verificamos solo para modificar.
if (action=="modificar")
{	
	if  ($.trim($("#precio_stop_profit").val())!='' && $.trim($("#precio_stop_lost").val())!='')
	{
		
		// es una venta, no permitimos stop de profit por encima de lost
		//alert(PositionSELL + "|" +  $("#tipo").val() + "|" + $.trim($("#precio_stop_profit").val())+ $.trim($("#precio_stop_lost").val()));
		if (PositionSELL == $("#tipo").val() && $.trim($("#precio_stop_profit").val())> $.trim($("#precio_stop_lost").val()) )
		{
			sError = "Siendo una venta, el precio stop profit debe ser menor  que el stop lost";
			bError = true;
		}	
		// es una compra, no permitimos stop de profit debajo  de lost
		if (PositionBUY == $("#tipo").val() &&$.trim($("#precio_stop_lost").val())> $.trim($("#precio_stop_lost").val()))
		{
				sError = "Siendo una compra, el precio stop profit debe ser mayor que el stop lost";
				bError = true;
		}
		/* alert($.trim($("#acciones_salida").val()));
		alert($.trim($("#acciones_pendientes").val())); 
		alert($.trim($("#acciones_salida").val()) >  $.trim($("#acciones_pendientes").val())); */
		if (parseInt($("#acciones_salida").val())> parseInt($("#acciones_pendientes").val()))
		{
				sError = "No se puede continuar. El número de aciones a operar con el Stop de Profit debe ser menor o igual que las acciones pendientes";
				bError = true;
		}	
		
	}
	else
		{
		sError = "No se puede continuar.Revise alguno de los datos introducidos";
		bError = true;
		}
	
}

if (!bError)

{
	 //return false;
	 // no confirmación de dialogo 18.03.2013
	 
	 /* $("#dialogo").html("¿Está seguro que quiere modificar/cancelar la posición?. <br/>Los cambios solo se aplicarán a la posición abierta en curso..");
	 $("#dialogo").dialog({
	      modal: true, title: 'Aviso', zIndex: 10000, autoOpen: true,
	      width: 'auto', resizable: false,
	      buttons: {
	          Si: function () {
	              doFunctionForYes();
	              $(this).dialog("close");
	          },
	          No: function () {
	              doFunctionForNo();
	              $(this).dialog("close");
	          }
	      },
	      close: function (event, ui) {
	          $(this).remove();
	      }
	});*/
	doFunctionForYes();
}
else
{
	 $("#dialogo").html(sError);
	 $("#dialogo").dialog({
      modal: true,
      buttons: {
        Ok: function() {
          $( this ).dialog( "close" );
        }
      }
    });;


 }
 }
 </script>
 	
<%



String _Error = "Operación guardada/cancelada correctamente";

boolean _bError = false;
 
int PositionID;

if (request.getParameter("pos_id_action")!=null) {  // hay operacion

	PositionID = Integer.parseInt(request.getParameter("pos_id_action"));

	Double _PriceLimitE = null;
	Double _PriceLimitS = null;
	Double _PriceStopLost = null;
	Double _PriceStopProfit  = null;
	Double _PercenStopLost = null;
	Double _PercenStopProfit = null;
	
	Long _ShareToTrade=null;
	
	
	if (request.getParameter("preciole")!=null)		
		_PriceLimitE = Double.parseDouble(request.getParameter("preciole"));
	if (request.getParameter("preciols")!=null)		
		_PriceLimitS = Double.parseDouble(request.getParameter("preciols"));
	if (request.getParameter("precio_stop_lost")!=null)		
		_PriceStopLost = Double.parseDouble(request.getParameter("precio_stop_lost"));
	if (request.getParameter("precio_stop_profit")!=null)		
		_PriceStopProfit = Double.parseDouble(request.getParameter("precio_stop_profit"));
	
	if (request.getParameter("stop_lost")!=null)		
		_PercenStopLost = Double.parseDouble(request.getParameter("stop_lost")) / 100;
	if (request.getParameter("stop_profit")!=null)		
		_PercenStopProfit = Double.parseDouble(request.getParameter("stop_profit")) /100;
	
	if (request.getParameter("acciones_salida")!=null)		
		_ShareToTrade = Long.parseLong(request.getParameter("acciones_salida"));
	
	
	Position _UpdatePosition = null;
	_UpdatePosition = PositionDAO.getPosition(Integer.parseInt(request.getParameter("pos_id_action"),10));
	if (_UpdatePosition!=null) // la encuentra
	{
		if  (request.getParameter("operation")!=null && request.getParameter("operation").equals("modificar"))
		{
			// modificamos la operacion
			if (_PriceLimitE!=null)
				_UpdatePosition.setLimit_price_buy(_PriceLimitE);
			if (_PriceLimitS!=null)
			_UpdatePosition.setLimit_price_sell(_PriceLimitS);
			if (_PriceStopLost!=null)
			{	
				_UpdatePosition.setSell_percentual_stop_lost(_PercenStopLost);  // se guarda en 0.015  1.5%
				_UpdatePosition.setSell_price_stop_lost(_PriceStopLost);  // se guarda en 0.015  1.5%
			}	
			if (_ShareToTrade!=null)
			{	
				_UpdatePosition.setShare_number_to_trade(_ShareToTrade);				
			}
			if (_PriceStopProfit!=null)
			{	
				_UpdatePosition.setSell_percentual_stop_profit(_PercenStopProfit);
				_UpdatePosition.setSell_price_stop_profit(_PriceStopProfit);
			}
			
			PositionDAO.updatePositionByPositionID(_UpdatePosition);
		}
		// cancelamos la operacion
		if  (request.getParameter("operation")!=null && request.getParameter("operation").equals("cancelar"))
		{	
			// puede darse que no se pueda cancelar, controlamos las FILLED
			// entradas o salidas
			boolean bCanBeCancelled = false;
			
	
			
			
			if ((_UpdatePosition.getState_buy()!=null && 
						_UpdatePosition.getState_sell()!=null && 
							!_UpdatePosition.getState_sell().equals(PositionStates.statusTWSCallBack.Filled.toString())) 
					||					!_UpdatePosition.getState_buy().equals(PositionStates.statusTWSCallBack.Filled.toString()))
			{
				bCanBeCancelled = true;
				
			}
			
			if (bCanBeCancelled && _UpdatePosition.getPending_cancelled()!=null && _UpdatePosition.getPending_cancelled().intValue()==0)
			{
				
				// las PendingSubmit hay que eliminarlas porque no estan negociando. P.e. Prohibido operar a corto, bloqueo de consola
				if (_UpdatePosition.getState_buy().equals(PositionStates.statusTWSCallBack.PendingSubmit.toString()))
				{
					PositionDAO.deletePositionByPositionID(_UpdatePosition);
					
				}
				else
				{					
				// establecemos el flag para que el gestor de Trading la Procese.
				_UpdatePosition.setPending_cancelled(new Integer(1));
				PositionDAO.updatePositionByPositionID(_UpdatePosition);
				}
			}
			else
			{
				_Error = "Operación no se puede cancelar. Compruebe si está confirmada o en proceso de cancelarse";
				_bError = true;
				
			}
		}
		
			
	}
	else
	{	
		_Error = "No se ha encontrado la operación con código " + request.getParameter("pos_id_action");
		_bError = true;
	}
	
	if (_bError)
	{		
	%>	
    <div class="f14px margintop60px tmarket flota_izq textleft negrita"><%=_Error%></div>
	<%
	}
	// no error 
	else
	{%>
		<script>window.location.href = '<%=request.getContextPath() + "/jsp/inicio.jsp"%>'</script>	
	<%}
}
else

{
		
	

Position oPosition = null;

Share oShare = null;


if (request.getParameter("positionid")!=null)
{	
	Long PositionId = Long.parseLong(request.getParameter("positionid"));
	oPosition = PositionDAO.getPosition(PositionId.intValue());
	
	oShare = ShareDAO.getShare(oPosition.getShareID());
	
}
else
{	
	 oPosition = new Position();	
}


%>
			<div id="dialogo"></div>
		    		    	
    		<form name="f" id="f" action="position.jsp" method="post" >    		
    		<table  class="tmarket">    			
    		<tr>
    		<td>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="accion">Acción</label> <input class="position" disabled="true" type="text" id="accion" name="accion" value="<%=oShare.getSymbol()%>">
    		</div>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="estado">Estado</label> <input class="position" disabled="true" type="text" id="estado" name="estado" value="<%=oPosition.getState()%>">
    		</div>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="estado_entrada">Estado Oper. Entrada</label> <input class="position" disabled="true" type="text" id="estado_entrada" name="estado_entrada" value="<%=oPosition.getState_buy()%>">
    		</div>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="estado_salida">Estado Oper. Salida</label> <input class="position" disabled="true" type="text" id="estado_salida" name="estado_salida" value="<%=oPosition.getState_sell()%>">
    		</div>
    		<%  
    		String ChangeableEnt = "disabled=true";
    		String ClassChangeEnt = "";
    		if (oPosition.getDate_real_buy()==null)   // no hubo compra 
    		{
    			ChangeableEnt = "";
    			ClassChangeEnt = "fondo_blanco";
    		}
    		String  ChangeableSal= "disabled=true";
    		String ClassChangeSal= "";
    		if (!ChangeableEnt.equals("") &&  oPosition.getDate_real_sell()==null)   // no hubo venta 
    		{
    			ChangeableSal = "";
    			ClassChangeSal= "fondo_blanco";
    		}
    		String  ChangeableStop = "disabled=true";   // se pueden modificar los stops¿¿¿
    		String ClassChangeStop= "";
    		if (ChangeableEnt.equals("")  ||   ChangeableSal.equals(""))   // no hubo compra 
    		{
    			ChangeableStop = "";
    			ClassChangeStop = "fondo_blanco";
    		}
    		
    		%> 
    		
    		
    		<div>
    			<form name="f2"  method="post" ></form>
    			<label class="flota_izq w200px textleft negrita" for="precioe">Precio Entrada</label> <input class="position"  type="text" id="precioe" name="precioe" readonly="true" value="<%=oPosition.getPrice_real_buy()%>">
    		</div>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="preciole">Precio Limite Entrada</label> <input readonly="true" <%=ChangeableEnt %>  class="position number <%=ClassChangeEnt%>" type="text" id="preciole" name="preciole" value="<%=oPosition.getLimit_price_buy()%>">
    		</div>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="precios">Precio Salida</label> <input readonly="true" class="position number"  type="text" id="precios" name="precios" value="<%=oPosition.getPrice_real_sell()%>">
    		</div>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="preciols">Precio Limite Salida</label> <input  disabled="true" <%=ChangeableSal%> class="position" type="text" id="preciols" name="preciols" value="<%=oPosition.getLimit_price_sell()%>">
    		</div>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="acciones">Número Acciones</label> <input disabled="true" class="position" type="text" id="acciones" name="acciones" value="<%=oPosition.getShare_number()%>">
    		</div>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="tipo">Tipo</label> <input  disabled="true" class="position" type="text" id="tipo" name="tipo" value="<%=oPosition.getType()%>">
    		</div>
    		<div class="caja_redonda_marron">
    		<div>
    			<label  class="flota_izq w200px textleft negrita" for="stop_profit">% Stop Profit</label> 
    			<input <%=ChangeableStop%>  readonly="true" class="position" type="text" id="stop_profit" name="stop_profit" value="<%=Utilidades.RedondeaPrice(oPosition.getSell_percentual_stop_profit()*100) %>">    			
    		</div>
    		<%
    		// entrada tipo posicion  largo
    		Double PrecioStopProfit = new Double(0);
    		Double PrecioStopLost = new Double(0);
    		Double MaxStopProfit = new Double(0);
    		Double MaxStopLost = new Double(0);
    		
    		%>
    		<div>
    			<label class="flota_izq w200px  textleft negrita" for="precio_stop_profit">Precio Stop Profit*</label> 
    			<input class="position fondo_blanco number mandatory" maxlength="50" "text" id="precio_stop_profit" name="precio_stop_profit" value="<%=oPosition.getSell_price_stop_profit()%>">
    		</div>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="acciones_pendientes">Acciones Pendientes </label> 
    			<input class="position" maxlength="50" disabled="true"  "text" id="acciones_pendientes" name="acciones_pendientes" value="<%=(oPosition.getShare_number() - oPosition.getShare_number_traded())%>">
    		</div>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="acciones_salida">Acciones a Operar (Stop Profit)</label> 
    			<input class="position fondo_blanco entero mandatory" maxlength="50" "text" id="acciones_salida" name="acciones_salida" value="<%=(oPosition.getShare_number_to_trade())%>">
    		</div>
    		</div>
    		<div class="caja_redonda_marron">
    		 <div>
    			<label class="flota_izq w200px textleft negrita" for="stop_lost">% Stop Lost</label> <input  readonly="true"  <%=ChangeableStop%> class="position" type="text" id="stop_lost" name="stop_lost" value="<%=Utilidades.RedondeaPrice(oPosition.getSell_percentual_stop_lost() * 100)%>">
    		</div>
    			<div>
    			<label class="flota_izq w200px textleft negrita" for="precio_stop_lost">Precio Stop Lost*</label> 
    			<input class="position fondo_blanco number mandatory" maxlength="50" "text" id="precio_stop_lost" name="precio_stop_lost" value="<%=oPosition.getSell_price_stop_lost()%>">
    		</div>
    		</div>
    		<input type="hidden" value="<%=oPosition.getPositionID()%>" name="pos_id_action" id="pos_id_action"/>    		
    		<input type="hidden" value="" name="operation" id="operation"/>
    		<div class="flota_der">
    		<% 
				if (!oPosition.getState().equals(PositionStates.status.SELL_OK.toString())) {    		
    		%>
    			<input class="button" type="button" name="cancelar" id="cancelar" value="Cancelar Posición" onclick="go('cancelar')"/>	
    			<input  class="button" type="button" name="modificar" value="Modificar Posición" onclick="go('modificar')"/>
    		<% 
				}    		
    		%>
    			<input class="button" type="button" name="volver" value="Volver" onclick="window.history.back()"/>
    		</div>    	
    		    	
    		</td>
    		</tr>
    		
	</table>
</form>		
    		
    		
    		
    		
<% } %>  
</div>  			    		
 </div>
 </div>
  
  <script>
 
  </script> 
  
<script>
 _KeepFloatNumbers();
 _KeepIntegerNumbers();
//_KeepIncrementInput("stop_profit",-100 );
_KeepIncrementInput("precio_stop_profit",0);
//_KeepIncrementInput("stop_lost",-100);
_KeepIncrementInput("precio_stop_lost",0);
_KeepOnChangeValueFromStopProfit($("#tipo").val(), $("#precioe").val(),"stop_profit","precio_stop_profit");
_KeepOnChangeValueFromStopLost($("#tipo").val(), $("#precioe").val(),"stop_lost","precio_stop_lost");


</script>

  