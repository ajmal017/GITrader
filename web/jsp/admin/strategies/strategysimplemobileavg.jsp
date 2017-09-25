<%@page import="com.tim.model.Strategy"%>
<%@page import="com.tim.dao.StrategyDAO"%>
<%@page import="java.util.Locale"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormatSymbols"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.tim.util.*"%>



  
 <div class="borde_bloque"><span class="titulo_bloque"></span>
 <div class="admin">

 	
<%
String _nameDisabled="";
if (request.getParameter("wp")!=null)  /* si viene de poup no puede tocar el nombre porque si no tengo que refrescar el origen con el nombre nuevo */
{
	 _nameDisabled="disabled";
}


Strategy oStrategy = null;
Strategy oStrategyDATA = null; 
if (request.getAttribute("strategy")!=null)
{
	oStrategy = (Strategy) request.getAttribute("strategy");
	oStrategyDATA= StrategyDAO.getStrategy(oStrategy.getStrategyId());
}
else
	oStrategy = new Strategy();

DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
otherSymbols.setDecimalSeparator('.');
otherSymbols.setGroupingSeparator(','); 
DecimalFormat df = new DecimalFormat("#0", otherSymbols);


String _Error = "Operación guardada/cancelada correctamente";
boolean bError = false;

boolean _bModeUpdate= true;

if (request.getParameter("pos_id_action")!=null) {  // hay operacion

	
	if(request.getParameter("pos_id_action").equals("-1"))	
		_bModeUpdate =false;
	
    Long StratID = Long.parseLong(request.getParameter("pos_id_action"));
	
	String Descripcion  =request.getParameter("description").toUpperCase();	
	boolean Activa = (request.getParameter("activa")!=null ? true : false) ;
	
	Integer PERIODS = Integer.parseInt(request.getParameter("periodos"));
	Integer TIMEBARS = Integer.parseInt(request.getParameter("timebars"));
	Float ENTRYRATE  = Float.parseFloat(request.getParameter("entryrate"));
	
	String _OperationTYPE  = request.getParameter("operations");
	
	
	String Nombre = (request.getParameter("nombre")!=null ? request.getParameter("nombre").toUpperCase() : "");
	
	if (!bError)
	{	
		
		oStrategyDATA.setActivada(Activa);	
		if (!Nombre.equals(""))
			oStrategyDATA.setName(Nombre);
		oStrategyDATA.setDescription(Descripcion);
		
		oStrategyDATA.setMacd_periods(PERIODS);
		oStrategyDATA.setMcad_rateavg_entry(ENTRYRATE / 100);
		oStrategyDATA.setMcad_timebars(TIMEBARS);
		oStrategyDATA.setSell_all_deadline_type_operation(_OperationTYPE);
		
		
		if (_bModeUpdate)  // nueva
		{ 
			StrategyDAO.updateStrategyByStrategyID(oStrategyDATA);		    
		}
			%>
		<div class="f14px margintop60px tmarket flota_izq textleft negrita"><%=_Error%></div>
		<script> 
		<% if (request.getParameter("wp")!=null) { %>
			$('.ui-dialog-titlebar-close', top.document).click();
		<% } 
		else %>
		   window.location.href = '<%=request.getContextPath() + "/jsp/estrategias.jsp"%>';
		</script> 
		
		<% 
		
	}
	else
	{
		%><div class="f14px margintop60px tmarket flota_izq textleft negrita"><%=_Error%>
		<input class="button" type="button" name="volver" value="Volver" onclick="window.history.back()"/></div>
	<% 	

	}

    
}
else

{


%>
	    	<div id="dialogo"></div>	
    		<div class="lista_acciones_trading"><img class="img_cab"  src="<%=request.getContextPath()%>/images/stock-market-financial-dice-trading-6150113.jpg">Estrategia</div>
    		<form  name="fS" id="fS" action="admin_ficha_strategy.jsp" method="post" >    		
    		<table  class="tmarket">    			
    		<tr>
    		<td>
    		<div> 
    			<label class="flota_izq w200px textleft negrita" for="nombre">Nombre*</label> <input <%=_nameDisabled%> class="position fondo_blanco mandatory flota_izq <%=_nameDisabled%>" type="text" id="nombre" name="nombre" value="<%=(oStrategyDATA.getName()!=null ? oStrategyDATA.getName() : "")%>">

    		</div>

    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="symbol">Descripción* </label> <textarea rows="5" cols="50" class="position fondo_blanco mandatory flota_izq" id="symbol" name="description"><%=(oStrategyDATA.getDescription() !=null ? oStrategyDATA.getDescription() : "") %></textarea>
    		</div>
    		
			<div>
    	    <label class="flota_izq w200px textleft negrita"   for="operations">Filtro Operación*</label>
			<select class="position fondo_blanco mandatory" name="operations" id="operations">
			<% String _AllSelec = "";
    	       String _BuySelec = "";
    	       String _SelSelec = "";
    	       if (oStrategyDATA.getSell_all_deadline_type_operation()!=null && !oStrategyDATA.getSell_all_deadline_type_operation().equals(""))
    	       {
    	    	   if (oStrategyDATA.getSell_all_deadline_type_operation().equals(PositionStates.statusTWSFire.BUY.toString()))
    	    		   _BuySelec ="selected";
    	    	   if (oStrategyDATA.getSell_all_deadline_type_operation().equals(PositionStates.statusTWSFire.SELL.toString()))
    	    		   _SelSelec ="selected";
    	    	   if (oStrategyDATA.getSell_all_deadline_type_operation().equals("ALL"))
    	    		   _AllSelec ="selected";
    	    	   
    	    	   
    	       }
    	       
    	       
    	    		
    	    		%>
			<option <%=_AllSelec %> value="ALL">ALL</option>
			<option <%=_BuySelec %> value="<%=PositionStates.statusTWSFire.BUY.toString()%>"><%=PositionStates.statusTWSFire.BUY.toString()%></option>
			<option <%=_SelSelec %> value="<%=PositionStates.statusTWSFire.SELL.toString()%>"><%=PositionStates.statusTWSFire.SELL.toString()%></option>
			</select>
			</div>
    		
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="periodos">Número Periodos*</label> 
    			<input class="position fondo_blanco mandatory entero flota_izq" type="text" id="periodos" name="periodos" value="<%=(oStrategyDATA.getMacd_periods()!=null ? oStrategyDATA.getMacd_periods() : 0)%>"/>

    		</div>
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="timebars">Periodo Barra (min)*</label> 
    			<input class="position fondo_blanco mandatory entero flota_izq" type="text" id="timebars" name="timebars" value="<%=(oStrategyDATA.getMcad_timebars()!=null ? oStrategyDATA.getMcad_timebars() : 0)%>"/>

    		</div>
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="entryrate">% Alto Rango Entrada (0-100)*</label> 
    			
    			<input class="position fondo_blanco mandatory entero flota_izq" type="text" id="entryrate" name="entryrate" value="<%=(oStrategyDATA.getMcad_rateavg_entry()!=null ? df.format(oStrategyDATA.getMcad_rateavg_entry()*100) : 0.0)%>"/>

    		</div>
    		
    		
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="activa">Activa*</label> 
    			<% 
    				boolean _Activa = oStrategyDATA.getActive();
    				String _txtActive = "";
    				if (_Activa)
    				{	
    					_txtActive = "checked";
    				}
    			%>
    			<input class="position fondo_blanco number flota_izq" type="checkbox"  id="activa" name="activa" <%=_txtActive%>>
    			 
    		</div>
    		
    		    		    		
    		<input type="hidden" value="<%=oStrategyDATA.getStrategyId()%>" name="pos_id_action" id="pos_id_action" />
    		<input type="hidden" value="<%=request.getParameter("stratidclass")%>" name="stratidclass" id="stratidclass" />
			<input type="hidden" value="<%=(request.getParameter("wp")!=null ? request.getParameter("wp") : "")%>" name="wp" id="wp" />
    		    		
    		<div class="flota_der">    			
    			<input  class="button" type="button" name="aceptar" value="Aceptar" onclick="go_St('fS')"/>
				<%
				// si viene de la ventana dialog 
				if (request.getParameter("wp")==null) {  %>
					<input class="button" type="button" name="volver" value="Volver" onclick="window.history.back()"/>		
				<% } %>
    		
    		</div>
    		
    		</td>
    		</tr>    		    		
    		</table>
    		</form>
    		    		
    			    		
 </div>
 </div>
 
 <script>
_KeepFloatNumbers();
_KeepIntegerNumbers();
</script>
<% }  %>
  
