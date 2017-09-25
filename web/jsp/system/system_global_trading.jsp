<%@page import="java.util.List"%> 
<%@page import="java.util.ArrayList"%>
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
 
 <script>
 
 var _ImgPath = '<%=request.getContextPath()%>/images/';
 
 function UpdateMarket(marketid)
 {
	 
	 var ImgStatus = $("#market_img_" + marketid).attr("src");
	 var status=1;
	 if (ImgStatus.indexOf("green")>0)
	 {
		 status =0;
	 }
	 
	 $.post("ajax/update_trading_market_status.jsp", { marketid:marketid, newstatus : status},
			  function(data) {
					// cambiamos la imagen	
					 if (data.indexOf("NOOK")==-1)   // NO ERROR
					 {
						var _Color = "red_15x15.jpg";
						if (status==1)
							_Color = "green_15x15.jpg";
						$("#market_img_" + marketid).attr("src",_ImgPath+ _Color);
	  				 }
		
	  					$("#dialog").html("<p><b>" + data + "</b></p>");
	  					$("#dialog" ).dialog();
					  });
	 
 }
 
 </script>
 
 <div class="borde_bloque"><span class="titulo_bloque"></span>
 <div id="syst_t">
 	
<%

Trading oTrading = TradingMarketDAO.getListActiveAllTradingToday();

if (oTrading!=null)
{	

List<Trading_Market> lActiveTradingMarkets = TradingMarketDAO.getListActiveTradingMarketShares(oTrading.getTradingID().intValue());


	
if (lActiveTradingMarkets.size()>0)
{
		
	/* VERIFICAMOS MERCADOS ACTIVOS */
    java.util.List<Trading_Market> lMarket = null;
    java.util.List<Share> lShare = null;
    
    java.util.List<Long> lDuplicate = new ArrayList();
    
    lMarket = lActiveTradingMarkets;
    
    
		
	if (lMarket!=null)
	{
			/* recorremos mercados */ 
	    	
			%>
	    		
    		<div class="lista_acciones_trading">Intradia Global</div>
    		<table cellpadding="5" cellspacing="5">
    		<tr class="header">
    		<td>Mercado</td><td>Exchange</td><td>Primary Exchange</td><td>Trading Activo</td>
    		</tr>
	    	<%	 
			
	    	for (int j=0;j<lMarket.size();j++)
	    	{
	    		
	    		Trading_Market oTrading_Market = lMarket.get(j);
	    		
				Market oMarket = MarketDAO.getMarket(oTrading_Market.getMarketId());
				
				Share oShareTrading= ShareDAO.getShare(oTrading_Market.getShareId());
				
				// control de duplicados
				if (!lDuplicate.contains(oMarket.getMarketID()))
				{
					
					int _newstatus = 1;
		    		String _Color = "red_15x15.jpg";
		    		if (oMarket.getTrading().equals(new Long(1)))
		    		{
		    			_newstatus = 0;
		    			_Color = "green_15x15.jpg";
		    			
		    		}
		    		
		    			    		
		    		%>
		    		<tr class="data">
		    		<td><%=oMarket.getName() %></td>
		    		<td><%=oMarket.getExchange()  %></td>
		    		<td><%=oMarket.getPrimary_exchange() %></td>
		    		<td><a 
		    			onclick="UpdateMarket(<%=oMarket.getMarketID() %>)" href="javascript:void(0)">
		    			<img id="market_img_<%=oMarket.getMarketID() %>" class="share_status" src="<%=request.getContextPath()%>/images/<%=_Color%>"></img></a></td>
		    		</tr>
		    		
		    			

			    	<%	  
			    	
		    		}  // control de duplicados
		    		lDuplicate.add(oMarket.getMarketID());
		    		%>
		    		</table>	
					
	    		
	    <%
			}// FIN DE MERCADOS
	    		
	    } // FIN DE IF
	   
	}// FIN DE IF


} // fin de trading para hoy
%>


    		
	    		
 </div>
 </div>
  
