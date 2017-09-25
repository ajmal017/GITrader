<%@page import="com.tim.model.Share_Strategy"%>
<%@page import="com.tim.dao.Share_StrategyDAO"%>
<%@page import="com.tim.dao.StrategyDAO"%>
<%@page import="java.util.Locale"%>
<%@page import="com.tim.dao.ConfigurationDAO"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.tim.util.ConfigKeys"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%> 
<%@page import="com.tim.dao.MarketDAO"%>
<%@page import="com.tim.dao.MarketShareDAO"%>
<%@page import="com.tim.dao.OrderDAO"%>
<%@page import="com.tim.dao.PositionDAO"%>
<%@page import="com.tim.dao.ShareDAO"%>
<%@page import="com.tim.dao.TradingMarketDAO"%>
<%@page import="com.tim.model.Market"%>
<%@page import="com.tim.model.Position"%>
<%@page import="com.tim.model.Strategy"%>
<%@page import="com.tim.model.Share"%>
<%@page import="com.tim.model.Trading"%>
<%@page import="com.tim.model.Trading_Market"%>
<%@page import="com.tim.util.Utilidades"%>
<%@page import="java.text.NumberFormat"%>
<%@page import="java.text.DecimalFormatSymbols"%>


	
 
 <script>
 
 
 
 function doFunctionForYes()
 {
 
 // con jquery y dialog da pete
 // validamos valores númericos
	 
	 document.forms[0].submit();

 
 }
 function doFunctionForNo()
 {
 	//window.location.reload();
 }
 
 
 function _getMonthsId()
 {
	 var _tmonthsId="";	 
	 $('#futures_month :selected').each(function(i, selectedElement) {
	    	_tmonthsId += $(selectedElement).val()  + ",";  ;    	 
	    	});
	 _tmonthsId=_tmonthsId.substring(0, _tmonthsId.length-1);
	 return _tmonthsId;
 }
 
function go()
{
bError = false;



$('.mandatory').each(function() {
    
	var currentElement = $(this);
    var value = $.trim(currentElement.val()); // if it is an input/select/textarea field
    
    if (value=='')
    {	
    	alert($(this).attr("id"));
    	bError = true;    	    	  
    	var SPAN_Error = $(this).addClass("borderrojo");
    	
    	
    	
    	
    }
    	
    // TODO: do something with the value
});


if (!bError)

{	
    /* BUSCAMOS LAS ESTRATEGIAS SELECCIONADA Y LAS METEMOS EN EL INPUT */
    var _stIds= "";
    
    var _monthsId = "";
    
    $( "li.ui-selected" ).each(function( index ) {
    	_stIds+= $( this ).attr("id")  + ",";    	
    	});
    
    /*$("#futures_month option").each(function()
	{
    	if  ($(this).attr('selected')==true)    	
    		_monthsId+= $( this ).val()  + ",";  
    	   
	});*/
    
	_monthsId = _getMonthsId(); 
	
   /* $('#futures_month :selected').each(function(i, selectedElement) {
    	_monthsId += $(selectedElement).val()  + ",";  ;    	 
    	});
    
    */
    
    _stIds=_stIds.substring(0, _stIds.length-1);
	$("#strategyId").val(_stIds);
	$("#monthsId").val(_monthsId);
	$("#expirationDate").val($("#expiry_date").val());
	 
	
	
	
	
    doFunctionForYes();
 
}
else
{
	 $("#dialogo").html("No se puede continuar.Revise alguno de los datos introducidos en color rojo");
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
 	
  
 <div class="borde_bloque"><span class="titulo_bloque"></span>
 <div class="admin">
 	
<%

SimpleDateFormat sdf = new SimpleDateFormat ("dd/MM/yyyy");


//DecimalFormat df = new DecimalFormat("#0.00");

DecimalFormatSymbols otherSymbols = new DecimalFormatSymbols(Locale.getDefault());
otherSymbols.setDecimalSeparator('.');
otherSymbols.setGroupingSeparator(','); 
DecimalFormat df = new DecimalFormat("#0.00", otherSymbols);


Share oShare = null;

Market oMarket = null;


List lMarkets = MarketDAO.getListActiveMarket(); 



Long ShareId = null;
if (request.getParameter("shareid")!=null && !request.getParameter("shareid").equals("-1"))
{	
	ShareId = Long.parseLong(request.getParameter("shareid"));
	oShare = ShareDAO.getShare(ShareId.longValue());	
	oMarket = ShareDAO.getMarketFromShare(oShare); 
	
}
else
{	
	  ShareId = new Long(-1);	
	  oMarket = MarketDAO.getMarket(Long.parseLong(request.getParameter("marketid")));
	  oShare = new Share();	
}


String _Error = "Operación guardada/cancelada correctamente";
boolean bError = false;

boolean _bModeUpdate= true;

if (request.getParameter("pos_id_action")!=null) {  // hay operacion

	
	if(request.getParameter("pos_id_action").equals("-1"))	
		_bModeUpdate =false;
	
	Long MarketID = Long.parseLong(request.getParameter("marketid"));
	Long ShareID = Long.parseLong(request.getParameter("pos_id_action"));
	String Nombre = request.getParameter("nombre").toUpperCase();
	String Symbol =request.getParameter("symbol").toUpperCase();	
	Long Activa = (request.getParameter("activa")!=null ? new Long(1) : new Long(0)) ;
	Long Trading_Activo = (request.getParameter("trading_activo")!=null ? new Long(1) : new Long(0)) ;
	Long Acciones = Long.parseLong(request.getParameter("acciones"));
	Double  Hueco = Double.parseDouble(request.getParameter("hueco"))/100;
	Double Precio_Limitado =  Double.parseDouble(request.getParameter("precio_limitado"))/100;
	Double Stop_Lost = null;
	Double Stop_Profit = null;
	if (request.getParameter("stop_lost")!=null && !request.getParameter("stop_lost").equals(""))
		Stop_Lost = Double.parseDouble(request.getParameter("stop_lost"))/100;
		
	if (request.getParameter("stop_profit")!=null && !request.getParameter("stop_profit").equals(""))
		Stop_Profit = Double.parseDouble(request.getParameter("stop_profit"))/100;
	
	Long OffSet1MinFromOpen =  Long.parseLong(request.getParameter("offset1_read"));
	Long OffSet2MinFromOpen =  Long.parseLong(request.getParameter("offset2_read"));
	
	String _SEC_TYPE =request.getParameter("type").toUpperCase();	
	String _EXCHANGE =request.getParameter("exchange").toUpperCase();
	String _PRIMARY_EXCHANGE =request.getParameter("primary").toUpperCase();
	
	// Values separated by ",";
	String _StrategiesId =request.getParameter("strategyId");
	
	
	
	Double TickFutures = null;
	
	Float Multiplier = null;
	
	if (request.getParameter("multiplier")!=null && request.getParameter("multiplier")!="")	
		Multiplier = Float.parseFloat(request.getParameter("multiplier"));	
	else
		Multiplier = new Float(1);
	
	if (request.getParameter("tick_futuro")!=null && request.getParameter("tick_futuro")!="")
	{
		TickFutures = Double.parseDouble(request.getParameter("tick_futuro"));
	}
	
	Long Historical = (request.getParameter("historical")!=null ? new Long(1) : new Long(0)) ;
		
	
	Double Porcentaje_Cierre =  new Double(0.01);
	
	String sExpiry_Date = null;
	java.util.Date dExpiration = null;
	
	Timestamp _Expiry_Date=null;
	String _XmlExpirationExp = "";
	
	if (_SEC_TYPE.equals(ConfigKeys.SECURITY_TYPE_FUTUROS))
	{
		

		if (request.getParameter("expirationDate")!=null && request.getParameter("expirationDate")!="")
		{
			sExpiry_Date =  request.getParameter("expirationDate");  // dd-mm-yyyy
			
			dExpiration = sdf.parse(sExpiry_Date);
			// si es futuro
		}
		 
        _XmlExpirationExp = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>";
		_XmlExpirationExp += "<expiration>";
		
		if (request.getParameter("futures_month")!=null && request.getParameter("futures_month")!="" &&
				request.getParameter("futures_week")!=null && request.getParameter("futures_week")!="" &&
					request.getParameter("futures_day")!=null && request.getParameter("futures_day")!="")
		{
			
		//	System.out.println(request.getParameter("futures_month"));
			
			_XmlExpirationExp +=  "<months>" + request.getParameter("monthsId") + "</months>";
			_XmlExpirationExp +=  "<week>" + request.getParameter("futures_week") + "</week>";
			_XmlExpirationExp +=  "<day>" + request.getParameter("futures_day") + "</day>";
			
			//dExpiration = sdf.parse(sExpiry_Date);
			// si es futuro
		}
		_XmlExpirationExp += "</expiration>";
			
		
		
		
	
	}	
		
	
	
	
	if (!_bModeUpdate)  // nueva
	{
		Share oShareExists = ShareDAO.getShare(Symbol);
		if (oShareExists!=null)
		{
			bError = true;
			_Error = "Ya existe una acción con el símbolo " + Symbol.toUpperCase();
		}

	}	
	if (!bError)
	{	
		oShare.setShareId(ShareID);
		oShare.setActive(Activa);
		oShare.setActive_trading(Trading_Activo);
		oShare.setName(Nombre);
		oShare.setSymbol(Symbol.toUpperCase().trim());
		oShare.setNumber_purchase(Acciones);
		oShare.setPercentual_limit_buy(Precio_Limitado);
		oShare.setPercentual_value_gap(Hueco);		
		oShare.setSell_percentual_stop_lost(Stop_Lost);
		oShare.setSell_percentual_stop_profit(Stop_Profit); 
		oShare.setSell_percentual_stop_profit_position(Porcentaje_Cierre);
		oShare.setOffset1min_read_from_initmarket(OffSet1MinFromOpen.intValue());
		oShare.setOffset2min_read_from_initmarket(OffSet2MinFromOpen.intValue());
		
		oShare.setExchange(_EXCHANGE);
		oShare.setPrimary_exchange(_PRIMARY_EXCHANGE);
		oShare.setSecurity_type(_SEC_TYPE);
		
		oShare.setOffset1min_read_from_initmarket(OffSet1MinFromOpen.intValue());
		oShare.setOffset2min_read_from_initmarket(OffSet2MinFromOpen.intValue());
		
		
		if (Activa.equals(new Long(1))) // reseteamos los errores
		{
			oShare.setLast_error_data_read("");
			/* forzamos a que la verificacion diaria se dispare cuando se vuelva activa.*/ 			
		}
		
		oShare.setDate_contract_verified(null);
		oShare.setLast_error_data_trade("");
		
		oShare.setTick_Futures(TickFutures);
		
		oShare.setMultiplier(Multiplier);
		
		
		
		/* String LocalSymbol=  request.getParameter("localsymbol");
		Long Multiplier=  Long.parseLong(request.getParameter("multiplier"));
		String sExpiry_Date =  request.getParameter("expiry_date");  // dd-mm-yyyy
		*/
		// viene del select... marketid|securitytype
		Long _MarketUpdated = new Long(request.getParameter("market"));
		
		
		if (sExpiry_Date!=null &&  !sExpiry_Date.equals("") && _SEC_TYPE.equals(ConfigKeys.SECURITY_TYPE_FUTUROS))
		{
				_Expiry_Date = new Timestamp(dExpiration.getTime());
		}
		
		
		oShare.setExpiry_date(_Expiry_Date);
		
		oShare.setExpiry_expression(_XmlExpirationExp);
		
		oShare.setHistorical_data(Historical);
		
		Integer MaxShareID = new Integer(-1);
		
		if (_bModeUpdate)  // nueva??
		{ 
			
			
						
			
			//System.out.println(_MarketUpdated);
			
			
			ShareDAO.updateShare(oShare);
		    ShareDAO.updateMarketShare(oShare.getShareId(), _MarketUpdated);
		    
		    /* ACTUALIZAR EL MARKETRADING */
		    TradingMarketDAO.updateTradingShare(oShare.getShareId().intValue(), _MarketUpdated.intValue());
		    
		    MaxShareID = oShare.getShareId().intValue(); 
		    
		    
		}
		else
		{
			ShareDAO.insertShare(oShare);
			// GET MAX ENTERED 
			 MaxShareID = ShareDAO.getLastShare();
			ShareDAO.insertMarketShare(MaxShareID.intValue(), oMarket.getMarketID());
			
			/* ACTUALIZAR EL MARKETRADING */
			TradingMarketDAO.addTradingShare(MaxShareID.intValue(), oMarket.getMarketID().intValue());
		}
		
		/* BORRAMOS TODO E INSERTAMOS. RECOMENDABLE ENTORNO TRANSACCIONAL  */
	    Share_StrategyDAO.deleteStrategiesFromShare(MaxShareID);
	    Share_StrategyDAO.insertStrategiesToShare(MaxShareID,_StrategiesId);
		
		
		%>
		<div class="f14px margintop60px tmarket flota_izq textleft negrita"><%=_Error%></div>
		<script>window.location.href = '<%=request.getContextPath() + "/jsp/admin_shares.jsp?marketid=" + MarketID%>'</script>	
		
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

	
	String _selSTK = (oShare.getSecurity_type()!=null && oShare.getSecurity_type().equals(ConfigKeys.SECURITY_TYPE_STOCK) ? "selected" : "");
	String _selFUT = (oShare.getSecurity_type()!=null && oShare.getSecurity_type().equals(ConfigKeys.SECURITY_TYPE_FUTUROS) ? "selected" : "");
	
%>
	    	<div id="dialogo"></div>	
    		<div class="lista_acciones_trading"></div>
    		<form name="f" id="f" action="admin_ficha_share.jsp" method="post" >    		
    		<table  class="tmarket">    			
    		<tr>
    		<td>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="nombre">Nombre*</label> <input class="position fondo_blanco mandatory" type="text" id="nombre" name="nombre" value="<%=(oShare.getName()!=null ? oShare.getName() : "")%>">

    		</div>
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="symbol">Symbol* </label> <input class="position fondo_blanco mandatory" type="text" id="symbol" name="symbol" value="<%=(oShare.getSymbol() !=null ? oShare.getSymbol() : "") %>">
    		</div>
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="activa">Activa* (Lectura S/N)</label> 
    			<input class="position fondo_blanco number" type="checkbox"  id="activa" name="activa"
    			 <%=(oShare.getActive ()!=null && oShare.getActive ().equals(new Long(1)) ? "checked" : "")%>>
    			 
    		</div>
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="trading_activo">Trading Activo* (Operativa S/N)</label>
    			<input class="position fondo_blanco number" type="checkbox"  id="trading_activo" name="trading_activo"
    			<%=(oShare.getActive_trading()!=null && oShare.getActive_trading().equals(new Long(1)) ? "checked" : "")%>>
    			 
    		</div>
    		
			<div>
    			<label class="flota_izq w200px textleft negrita" for="historical">Histórico (S/N)
    			<img src="<%=request.getContextPath()%>/images/help16_x16.png" title="If checked, system will calculate some months of historical data, please keep in mind this due to performance purposes"/></label>
    			
    			<input class="position fondo_blanco number" type="checkbox"  id="historical" name="historical"
    			<%=(oShare.getHistorical_data()!=null && oShare.getHistorical_data().equals(new Long(1)) ? "checked" : "")%>>
    			
    		</div>
    		    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="acciones">Acciones*</label> 
    			<input class="position fondo_blanco entero mandatory" type="text" id="acciones" name="acciones" value="<%=(oShare.getNumber_purchase() !=null ? oShare.getNumber_purchase() : "")%>">
    		</div>
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="hueco">% Hueco Señal Entrada*</label> 
    			<input class="position fondo_blanco number mandatory" type="text" id="hueco" name="hueco" value="<%=(oShare.getPercentual_value_gap()!=null ? df.format(oShare.getPercentual_value_gap()*100) : "")%>">
    		</div>
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="precio_limitado">% Precio Limitado*</label>
    			<input class="position fondo_blanco number mandatory" type="text" id="precio_limitado" name="precio_limitado" value="<%=(oShare.getPercentual_limit_buy()!=null ?df.format(oShare.getPercentual_limit_buy()*100) : "")%>">
    		</div>
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="offset1_read">Tramo inicial Lectura (Minutos desde apertura (0 normalmente))*</label> 
    			<input class="position fondo_blanco entero mandatory" maxlength="50" type=text id="offset1_read" name="offset1_read" value="<%=(oShare.getOffset1min_read_from_initmarket()!=null ? oShare.getOffset1min_read_from_initmarket() : "") %>">
    		</div>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="offset2_read">Tramo Final Lectura (Minutos desde apertura (30 normalmente))*</label> 
    			<input class="position fondo_blanco entero  mandatory" maxlength="50" type=text id="offset2_read" name="offset2_read" value="<%=(oShare.getOffset2min_read_from_initmarket()!=null ?oShare.getOffset2min_read_from_initmarket() : "") %>">
    		</div>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="stop_lost">% Stop Lost</label> 
    			<input class="position fondo_blanco number" maxlength="50" type="text" id="stop_lost" name="stop_lost" value="<%=(oShare.getSell_percentual_stop_lost()!=null && oShare.getSell_percentual_stop_lost().floatValue()>0  ? df.format(oShare.getSell_percentual_stop_lost()*100) : "") %>">
    		</div>
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita " for="stop_profit">% Stop Beneficio</label> 
    		   <input class="position fondo_blanco number" maxlength="200" type="text" id="stop_profit" name="stop_profit" value="<%=(oShare.getSell_percentual_stop_profit() !=null && oShare.getSell_percentual_stop_profit()>0 ? df.format(oShare.getSell_percentual_stop_profit()*100) : "")%>">
    		</div>
    		
    		
    		
    		<%  
    		// este añadiendo un futuro.
    		// modificando se deja la capa que va con js  ||  (request.getParameter("shareid")==null)  //  && oMarket.getSecurity_type().equals(ConfigKeys.SECURITY_TYPE_FUTUROS))
    		/* if (request.getParameter("shareid")==null || (request.getParameter("shareid")!=null  && oShare.getSecurity_type().equals(ConfigKeys.SECURITY_TYPE_FUTUROS))
    		{ */
    			Double _DefaultTickFut = Double.parseDouble(ConfigurationDAO.getConfiguration("TICK_FUTURE").getValue());
    		
    		    
	    		String _ExpMonths = "1";
			    java.util.List<String> _lExpMonths = Arrays.asList("1");
	
			    String _ExpDay = "1";
			    String _ExpWeek= "1";
			    
			    if (oShare.getExpiry_expression()!=null && !oShare.getExpiry_expression().equals(""))
			    {
			    
	    		    _ExpMonths = Utilidades.getExpFutMonths(oShare.getExpiry_expression());
	    		     _lExpMonths = Arrays.asList(_ExpMonths.split("\\s*,\\s*"));
	
	    		    _ExpDay = Utilidades.getExpFutDayOfWeek(oShare.getExpiry_expression());
	    		    _ExpWeek= Utilidades.getExpFutWeek(oShare.getExpiry_expression());
    		    
			    }
    			
    			%>
    			<div class="caja_redonda_marron" id="futuros">
    			
    			<div>
    				<label class="flota_izq w200px textleft negrita" for="futures_month">Meses Vencimiento [FUTURO]</label>
    			 	<select class="position fondo_blanco mandatory"   name="futures_month"  id="futures_month" multiple="multiple">
    			 	
    			 	<%
    			 	String _strSel ="";
    			 	for (int jmonth=0;jmonth<ConfigKeys._FUTURES_MONTHS.length;jmonth++)
    			 	{   
    			 		
    			 		_strSel ="";
    			 		if (_lExpMonths!=null && _lExpMonths.size()>0 && _lExpMonths.contains(String.valueOf(jmonth+1)))
    			 			_strSel = "selected";
    			 			
    			 	
    			 	%>
    			 	
    			 		<option <%=_strSel%> value="<%=jmonth+1%>"><%= ConfigKeys._FUTURES_MONTHS[jmonth]%></option>
    			 	<% }
    			 	
    			 	%>
			        </select>			    
			    <script>
			    	//$(document).ready(function(){
			    	  $("select").multiselect({
						   multiple: true,
						   header: "Months",
						   noneSelectedText: "Select Months"
						});
			    	   //});
		        	 
		    	</script>
			        
			    
    			</div>
    			
    			<div>
    				<label class="flota_izq w200px textleft negrita" for="futures_week">Semana Vencimiento [FUTURO]</label>
    			 	<select class="position fondo_blanco mandatory" name="futures_week" id="futures_week" >    			 	
			        <option <%=_ExpWeek.equals("1") ? "selected" : "" %> value="1">1</option>			    
			        <option <%=_ExpWeek.equals("2") ? "selected" : "" %> value="2">2</option>
			        <option <%=_ExpWeek.equals("3") ? "selected" : "" %> value="3">3</option>
			        <option <%=_ExpWeek.equals("4") ? "selected" : "" %> value="4">4</option>
			        <option <%=_ExpWeek.equals("5") ? "selected" : "" %> value="5">5</option>
			    </select>			    
			    
    			</div>
    			
    			<div>
    				<label class="flota_izq w200px textleft negrita" for="futures_day">Dia Vencimiento [FUTURO]</label>
    			 	<select class="position fondo_blanco mandatory"  name="futures_day"  id="futures_day" >
    		       	<%
    			 	for (int jday=1;jday<=ConfigKeys._FUTURES_DAYOFWEEK.length;jday++)
    			 	{
    			 		_strSel ="";
    			 		if (!_ExpDay.equals("") && _ExpDay.equals(String.valueOf(jday)))
    			 			_strSel = "selected";
    			 	
    			 	%>
    			 		<option  <%=_strSel %> value = "<%=jday%>"><%= ConfigKeys._FUTURES_DAYOFWEEK[jday-1]%></option>
    			 	<% }
    			 	
    			 	%>
			        
			    </select>			    
			    
    			</div>
    			
    			<div>
    				<label class="flota_izq w200px textleft negrita" for="expiry_date">Fecha Expiración [FUTURO]</label>
    			 	<input class="position mandatory"  disabled=true type="text" id="expiry_date" name="expiry_date" value="<%=(oShare.getExpiry_date()!=null ? sdf.format(oShare.getExpiry_date()) : "-") %>">
    			</div>
    			<div>
    			<label class="flota_izq w200px textleft negrita" for="tick_futuro">Tick [FUTURO]</label>
    			 <input class="position number fondo_blanco mandatory" type="text" id="tick_futuro" name="tick_futuro" value="<%=(oShare.getTick_Futures()!=null ?oShare.getTick_Futures() : _DefaultTickFut) %>">
    			</div>
    			<div>
    			<label class="flota_izq w200px textleft negrita" for="multiplier">Multiplicador [FUTURO]</label>
    			 <input class="position number fondo_blanco mandatory" type="text" id="multiplier" name="multiplier" value="<%=(oShare.getMultiplier()!=null ?oShare.getMultiplier(): 1) %>">
    			</div>
    			</div>
    		
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="exchange">TWS Exchange*</label>
    			<select class="position fondo_blanco mandatory"  id="exchange" name="exchange">
    			<%
    			
    			 for (int j=0;j<ConfigKeys._MARKET_EXCHANGES.length;j++)
    			 {
    			 	String _sel = (oShare.getExchange()!=null &&  oShare.getExchange().equals(ConfigKeys._MARKET_EXCHANGES[j]) ? "selected" : "");
    			 	
    			 
    			 	%>
    			 
    			 
    				<option <%=_sel%> value="<%=ConfigKeys._MARKET_EXCHANGES[j]%>"><%=ConfigKeys._MARKET_EXCHANGES[j]%></option>
    				
    			 <%}
    			%>
    			</select> 
    			       			
    		</div>	
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="primary">TWS Primary Exchange*</label>
    			<select class="position fondo_blanco mandatory"  id="primary" name="primary">
    			<%
    			
    			 for (int j=0;j<ConfigKeys._PRIMARY_MARKET_EXCHANGES.length;j++)
    			 {
    				 String _sel = (oShare.getPrimary_exchange()!=null && oShare.getExchange().equals(ConfigKeys._PRIMARY_MARKET_EXCHANGES[j]) ? "selected" : "");
    			 %>
    				<option <%=_sel%> value="<%=ConfigKeys._PRIMARY_MARKET_EXCHANGES[j]%>"><%=ConfigKeys._PRIMARY_MARKET_EXCHANGES[j]%></option>    				
    			 <%}
    			%>    			
    			</select>    			      		
    		</div>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="type">Stock Type*</label>
    			<select class="position fondo_blanco mandatory"  id="type" name="type" onchange="_KeepFuturesParams('<%=ConfigKeys.SECURITY_TYPE_FUTUROS%>')">
    			
    			<option <%=_selSTK %> value="<%=ConfigKeys.SECURITY_TYPE_STOCK%>"><%=ConfigKeys.SECURITY_TYPE_STOCK%></option>    				
    			<option  <%=_selFUT %> value="<%=ConfigKeys.SECURITY_TYPE_FUTUROS%>"><%=ConfigKeys.SECURITY_TYPE_FUTUROS%></option>
    			</select>      			
    		</div>
    		
    		
    		<div> 
    			<label class="flota_izq w200px textleft negrita" for="porcentaje_cierre">% Cierre Posición (Stop Beneficio)*</label>
    			 <input class="position " disabled="true" type="text" id="porcentaje_cierre" name="porcentaje_cierre" value="100">
    		</div>
    		<% if (request.getParameter("shareid")!=null) {
    			// añadiendo, sin mercado, ya viene elegido %>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="market">Mercado</label>
    			
    				
    			
    			<select name=market id="market">
    			<% 
    			for (int j=0;j <lMarkets.size();j++)
    			{
    				String _selected = "";
    				Market Mercado = (Market) lMarkets.get(j);
    				if (oMarket.getMarketID().equals(Mercado.getMarketID()))
    					_selected = "selected";
				%>
				<option <%=_selected%> value="<%=Mercado.getMarketID()%>"><%=Mercado.getName()%></option>
				<%    				
    			}
    			
    			%>
    			</select>
    			     			    			 
    		</div>
    		    <% } %>    		    	
    		<input type="hidden" value="<%=oMarket.getMarketID()%>" name="marketid" id="marketid" />
    		
    		<!--  AÑADIENDO??? -->
    		<% if (request.getParameter("shareid")==null) { %>    		
    			<input type="hidden" value="<%=oMarket.getMarketID()%>" name="market" id="market" />
    		<% } %>	
    			
    		
    		<input type="hidden" value="<%=ShareId%>" name="pos_id_action" id="pos_id_action" />   
    		<input type="hidden" value="<%=ShareId%>" name="shareid" id="shareid" />
    		 		
    		<div class="flota_der">    			
    			<input  class="button" type="button" name="aceptar" value="Aceptar" onclick="go()"/>
    			<input class="button" type="button" name="volver" value="Volver" onclick="window.history.back()"/>
    		</div>
    		</td>
    		<td class="arriba"><!--  STRATEGIAS -->
    		<div class="adm_stratg caja_redonda_marron">
    			<div class="adm_stratg2 azul_label">Estrategias seleccionadas (Ctrl+Click)</div>
    			<ul id="selectable">
    			<% 
    			   java.util.List<Strategy> _lStrategies = StrategyDAO.getListStrategies();
    			   java.util.List<Share_Strategy> _lStrategiesStock = Share_StrategyDAO.getListStrategiesIDByShare(oShare.getShareId());
    			   if (_lStrategies!=null)
    			   {
    			   for (int j=0;j<_lStrategies.size();j++)    				
   			   		{
   						Strategy _stcItem = (Strategy)_lStrategies.get(j);
   						boolean _IsSelected=false;
   						if (_stcItem.getActive())
   						{
   							if (_lStrategiesStock!=null)  // hay estrategias de la acccion¿
   							{
   								for (int h=0;h<_lStrategiesStock.size();h++)
   								{
   									Share_Strategy _stcFromShare = (Share_Strategy)_lStrategiesStock.get(h);
   									if (_stcFromShare.getStrategyId().equals(_stcItem.getStrategyId()))
   									{
   										_IsSelected=true;
   										break;
   									}
   								}
   									
   								}
   							%>
   						
   							<li id="<%=_stcItem.getStrategyId() %>" class="ui-state-default <%=_IsSelected ? " ui-selected" : ""%>"><%=_stcItem.getName() %>
   							</li>
   							<a><div   id="stratlink|<%=_stcItem.getClassName()%>" class="stratlink"></div></a> 
   							 
   					   								
   					<% 	
   							}   						// end is active 
   					}   // end for inicial
    			   } // end if 
    			   
    			   %>   
    			   
 			     </ul>    	
			     <div id="dialog"></div>
			  <script>			  				 
				  $(".stratlink").click(function(){
					
					  var _IdVal = $(this).attr('id')
					  var _class='';					  	
					  if (_IdVal.indexOf("|")>=0)
						  _class = _IdVal.substring(_IdVal.indexOf("|")+1)
					  
					 //  var iframe = $('<iframe frameborder="0" marginwidth="0" marginheight="0" allowfullscreen></iframe>');
                     var $dialog = $('<div></div>')
					 .html('<iframe width=100%  scrolling=no height=100% src="<%=request.getContextPath()%>/jsp/admin_ficha_strategy.jsp?stratidclass=' + _class + '&wp=1" frameborder="0" marginwidth="0" marginheight="0" allowfullscreen></iframe>')
	                 .dialog({
	                         autoOpen: false,
	                         modal: true,
	                         height: 400,
	                         width: 450,
	                         title: "Estrategia"
	     	                });
                     $dialog.dialog('open');
     		   });
			  
				  
			  
			  $(function() {
				    $("#selectable").selectable({ cancel: 'a' });
				});
		//	  $( "#strateglink" ).load( "/TraderInteractiveModel/jsp/admin/ficha_estrategia.jsp?stratidclass=com.tim.model.StrategySimpleMobileAverage" );
			  </script>
    		</div>
    		</td>
    		</tr>    		    		
    		</table>
    		<input type="hidden" id="strategyId" name="strategyId" value="">
    		<input type="hidden" id="monthsId" name="monthsId" value="">
    		<input type="hidden" id="expirationDate" name="expirationDate" value="">
    		</form>
    		    		
    			    		
 </div>
 </div>
 
 
 <script>
 
 var _ExpirationFut ="";
 
 <%
 
 if (oShare.getExpiry_date()!=null) 
 { %>
	 _ExpirationFut = '<%=oShare.getExpiry_date()%>';	 
<% 
 }
%>


 
 
_KeepFloatNumbers();
_KeepIntegerNumbers();
_KeepFuturesParams('<%=ConfigKeys.SECURITY_TYPE_FUTUROS%>');
/* $( "#expiry_date" ).datepicker({
    showOn: "button",
    buttonImage: "../images/calendar.gif",
    buttonImageOnly: true
  });
$( "#expiry_date" ).datepicker( "option", "showAnim", "clip")
$( "#expiry_date" ).datepicker( "option", "dateFormat", "dd/mm/yy")
$( "#expiry_date" ).datepicker("option", "minDate", 0);
*/

$( "#futures_day" ).blur(function () {_refreshExpiryDate()})
$( "#futures_week" ).blur(function () {_refreshExpiryDate()})
$( "##futures_month" ).blur(function () {_refreshExpiryDate()})
$( "#expiry_date" ).focus(function () {_refreshExpiryDate()})
	
	
	
function _refreshExpiryDate()
{
	
	$( "#expiry_date" ).val("Waiting for date..");
	
	$.post('<%=request.getContextPath()%>/jsp/ajax/get_future_date.jsp', { e_m: _getMonthsId(), e_d : $("#futures_day").val(), e_w:$("#futures_week").val()})
	.done(function( data ) {
		  $( "#expiry_date" ).val(data.trim());
	  
	});
}
	 

<% 
if (oShare.getExpiry_date()!=null)
{
	%>
	$( "#expiry_date" ).datepicker("setDate", "<%=sdf.format(oShare.getExpiry_date())%>");
<% }
%>

	
</script>
<% }  %>
  
