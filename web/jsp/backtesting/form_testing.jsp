<%@page import="com.tim.model.Share_Strategy"%>
<%@page import="com.tim.dao.Share_StrategyDAO"%>
<%@page import="com.tim.dao.StrategyDAO"%>
<%@page import="com.tim.dao.MarketDAO"%>
<%@page import="com.tim.dao.MarketShareDAO"%>
<%@page import="com.tim.dao.ShareDAO"%>
<%@page import="com.tim.dao.TradingMarketDAO"%>
<%@page import="com.tim.model.Market"%>
<%@page import="java.util.*"%>
<%@page import="com.tim.model.Share"%>
<%@page import="com.tim.model.Trading"%>
<%@page import="com.tim.model.Trading_Market"%>


 <div class="borde_bloque"><span class="titulo_bloque"></span>
 <div class="admin">	
<div class="lista_acciones_trading"><img class="img_cab" src="<%=request.getContextPath()%>//images/stock-market-financial-dice-trading-6150113.jpg">

</div>
<form name="f" id="f" action="admin_ficha_share.jsp" method="post" >    		
<table  class="tmarket">    			
<tr>
<td>  			

<div>
	<label class="flota_izq w200px textleft negrita" for="finicio">Fecha Inicio</label> 
	<input class="position fondo_blanco" type="text" id="finicio" name="finicio">
</div>
<div>
	<label class="flota_izq w200px textleft negrita" for="ffin">Fecha Fin</label> 
	<input class="position fondo_blanco" type="text" id="ffin" name="ffin">
</div>

<div>	
	<label class="flota_izq w200px textleft negrita" for="acciones">Acciones</label>
	<select name="acciones" id="acciones">
	<%
		Trading  oTradingToday = null;
		oTradingToday = (Trading) TradingMarketDAO.getListActiveAllTradingToday();

	   	List<Trading_Market> oSharesMarket;
	   	Market  oMarket = null;
	   	
	   	if (oTradingToday!=null)
	   	{
	   		
	   	oSharesMarket = TradingMarketDAO.getListMarketShares(oTradingToday.getTradingID().intValue()); 
		
	   	if 	(oSharesMarket!=null)
	   	{
	   	
		for (int j=0;j<oSharesMarket.size();j++)
	    	{
	    		
	    		Trading_Market oShareMarket  = oSharesMarket.get(j);
	    		
	    		oMarket  = (Market) MarketDAO.getMarket(oShareMarket.getMarketId());
	    		
	    		
	    		
	    		Share oTradingShare = ShareDAO.getShare(oShareMarket.getShareId());
	    		
	    		%>
	    		<option value="<%= oTradingShare.getShareId() %>"><%=oTradingShare.getSymbol()%> | <%= oMarket.getName()%></option>
	    	<%	
	    	}
	   	}
	   	}

	%>
	
	
	</select>    
</div>
</form>
</td>
</tr>
</table>
</div>
<script>
$("#acciones").multiselect({
	   multiple: true,
	   header: "Stocks",
	   noneSelectedText: "Select Stocks"
	});

$( "#finicio" ).datepicker({
showOn: "button",
buttonImage: "../images/calendar.gif",
buttonImageOnly: true
});
$( "#finicio" ).datepicker( "option", "showAnim", "clip")
$( "#finicio" ).datepicker( "option", "dateFormat", "dd/mm/yy")


$( "#ffin" ).datepicker({
	showOn: "button",
	buttonImage: "../images/calendar.gif",
	buttonImageOnly: true
	});
	$( "#ffin" ).datepicker( "option", "showAnim", "clip")
	$( "#ffin" ).datepicker( "option", "dateFormat", "dd/mm/yyyy")
	


</script>
