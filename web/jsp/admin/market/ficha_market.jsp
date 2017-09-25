<%@page import="java.util.Locale"%>
<%@page import="com.tim.dao.ConfigurationDAO"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.text.DateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.tim.util.ConfigKeys"%>
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
 
 
 
function go()
{
bError = false;



$('.mandatory').each(function() {
    
	var currentElement = $(this);
    var value = $.trim(currentElement.val()); // if it is an input/select/textarea field
    
    if (value=='')
    {	
    	bError = true;    	    	  
    	var SPAN_Error = $(this).addClass("borderrojo");
    }
    	
    // TODO: do something with the value
});
if ($('#start').timepicker('getTime')>$('#end').timepicker('getTime'))
{
	bError = true;
}
	
$('.mandatory').each(function() {
    
	var currentElement = $(this);
    var value = $.trim(currentElement.val()); // if it is an input/select/textarea field
    
    if (value=='')
    {	
    	bError = true;    	    	  
    	var SPAN_Error = $(this).addClass("borderrojo");
    }
    	
    // TODO: do something with the value
});


if (!bError)

{	 
	 doFunctionForYes();
}
else
{
	 $("#dialogo").html("No se puede continuar. Faltan datos o la hora de apertura debe ser menos que la de cierre.Revise alguno de los datos introducidos en color rojo");
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



String _StartMarket = "";
String _EndMarket = "";


Long MarketId = null;
if (request.getParameter("marketid")!=null && !request.getParameter("marketid").equals("-1"))
{	
	MarketId = Long.parseLong(request.getParameter("marketid"));	
	oMarket = MarketDAO.getMarket(MarketId);
	
	StringBuilder _stHour= new StringBuilder();
	StringBuilder _endHour= new StringBuilder();

	_stHour.append(oMarket.getStart_hour().substring(0, 2));
	_stHour.append(":");
	_stHour.append(oMarket.getStart_hour().substring(2, 4));
	
	_endHour.append(oMarket.getEnd_hour().substring(0, 2));
	_endHour.append(":");
	_endHour.append(oMarket.getEnd_hour().substring(2, 4));
	
	
	_StartMarket = _stHour.toString();
	_EndMarket = _endHour.toString();
	
	
	
}
else
{	
	  MarketId = new Long(-1);	
	  //oMarket = MarketDAO.getMarket(Long.parseLong(request.getParameter("marketid")));
	  oMarket = new Market();	
}


String _Error = "Operación guardada/cancelada correctamente";
boolean bError = false;

boolean _bModeUpdate= true;

if (request.getParameter("pos_id_action")!=null) {  // hay operacion

	
	if(request.getParameter("pos_id_action").equals("-1"))	
		_bModeUpdate =false;
	
	Long MarketID = Long.parseLong(request.getParameter("marketid"));	
	String Nombre = request.getParameter("nombre").toUpperCase();
	String Identificador =request.getParameter("symbol").toUpperCase();	
	/* String _SEC_TYPE =request.getParameter("type").toUpperCase();
	String _CURRENCY =request.getParameter("currency").toUpperCase();
	String _EXCHANGE =request.getParameter("exchange").toUpperCase();
	String _PRIMARY_EXCHANGE =request.getParameter("primary_exchange").toUpperCase();*/
	String _STARTHOUR =request.getParameter("start").replace(":", "");
	String _ENDHOUR =request.getParameter("end").replace(":", "");
	
	
	if (!_bModeUpdate)  // nueva
	{
		Market oMarketExists = MarketDAO.getMarketByName(Identificador);
		if (oMarketExists!=null)
		{
			bError = true;
			_Error = "Ya existe un mercado con el nombre " + Nombre.toUpperCase();
		}

	}	
	if (!bError)
	{	
		oMarket.setActive(new Long(1));
		oMarket.setEnd_hour(_ENDHOUR);
		oMarket.setStart_hour(_STARTHOUR);
		oMarket.setIdentifier(Identificador);
		oMarket.setName(Nombre);		
		oMarket.setReading(new Long(1));
		oMarket.setTrading(new Long(1));
		// viene del select... marketid|securitytype
		String _MarketUpdated = request.getParameter("market");
		
		if (_bModeUpdate)  // nuevo??
		{ 
			
			MarketDAO.updateMarket(oMarket);			
		}
		else
		{
			MarketDAO.insertMarket(oMarket);			 					
		}
		%>
		<div class="f14px margintop60px tmarket flota_izq textleft negrita"><%=_Error%></div>
		<script>window.location.href = '<%=request.getContextPath() + "/jsp/admin.jsp?t=" + Math.random()%>'</script>	
		
		<% 
		
	}
	else
	{
		%><div class="f14px margintop60px tmarket flota_izq textleft negrita"><%=_Error%>
		<input class="button" type="button" name="volver" value="Volver" onclick="window.history.back()"/></div>
	<% 	

	}

    
}
else  // hay operacion

{


%>
	    	<div id="dialogo"></div>	
    		<div class="lista_acciones_trading"></div>
    		<form name="f" id="f" action="admin_ficha_market.jsp" method="post" >    		
    		<table  class="tmarket">    			
    		<tr>
    		<td>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="nombre">Nombre*</label> 
    			<input class="position fondo_blanco mandatory" type="text" id="nombre" name="nombre" value="<%=(oMarket.getName()!=null ? oMarket.getName() : "")%>">

    		</div>
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="symbol">Identificador* </label> 
    			<input class="position fondo_blanco mandatory" type="text" id="symbol" name="symbol" value="<%=(oMarket.getIdentifier() !=null ? oMarket.getIdentifier() : "") %>">
    		</div>
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="currency">Moneda*</label>
    			<select class="position fondo_blanco mandatory" id="currency" name="currency">
    			<option id="<%=ConfigKeys.CURRENCY_DOLLAR%>"><%=ConfigKeys.CURRENCY_DOLLAR%></option>
    			<option id="<%=ConfigKeys.CURRENCY_EURO%>"><%=ConfigKeys.CURRENCY_EURO%></option>    			
    			</select>    			
    		</div>
    		
    		
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="start">Apertura*</label>
    			<input class="position fondo_blanco mandatory"  id="start" name="start">      			
    		</div>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="end">Cierre*</label> 
    			<input class="position fondo_blanco mandatory" id="end" name="end">
    		</div>
    		
    		
    		</div>
    		
    		    		    	
    		
    		
    		<input type="hidden" value="<%=MarketId%>" name="pos_id_action" id="pos_id_action" />   
    		<input type="hidden" value="<%=MarketId%>" name="marketid" id="marketid" />
    		 		
    		<div class="flota_der">    			
    			<input  class="button" type="button" name="aceptar" value="Aceptar" onclick="go()"/>
    			<input class="button" type="button" name="volver" value="Volver" onclick="window.history.back()"/>
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



$(function() {
  
  $('#start').timepicker( // custom hours and minutes
		    {hours: {
		        starts: 0,                // First displayed hour
		        ends: 23                  // Last displayed hour
		    },
		    minutes: {
		        starts: 0,                // First displayed minute
		        ends: 55,                 // Last displayed minute
		        interval: 5,              // Interval of displayed minutes
		        manual: []                // Optional extra entries for minutes
		    }		  
		});
  
  $('#end').timepicker( // custom hours and minutes
		    {hours: {
		        starts: 0,                // First displayed hour
		        ends: 23                  // Last displayed hour
		    },
		    minutes: {
		        starts: 0,                // First displayed minute
		        ends: 55,                 // Last displayed minute
		        interval: 5,              // Interval of displayed minutes
		        manual: []                // Optional extra entries for minutes
		    }		  
		});
	
  
  
  
  /* Globalize.culture("en-EN");
  
  $( "#start" ).timespinner( "value", "en-EN" );
  $( "#end" ).timespinner( "value", "en-EN" ); */
  
  <% if (!_StartMarket.equals(""))
  {
	%>
	$('#start').timepicker('setTime','<%=_StartMarket%>');
	$('#end').timepicker('setTime','<%=_EndMarket%>');
  <%
  }  
  %>
  
  
  /* $('#getTime_button').val('Selected time : ' + $('#timepicker').timepicker('getTime'));
  
  
  function getHourMinute() {
	    $('#getHourMinute_button').val(
	            'Hour : ' + $('#timepicker').timepicker('getHour')
	            + ', ' +
	            'minute : ' + $('#timepicker').timepicker('getMinute')
	    );
	}*/
  
  
});



</script>


<%
}
%>