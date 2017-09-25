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
 
 <script>
 
 var _ImgPath = '<%=request.getContextPath()%>/images/';
 
 function _DoNo(){
	 
 }
 
 function _DoYes(){
	 
	 $.post("ajax/delete_share_trading.jsp", { shareid:shareid},
			  function(data) {
					// cambiamos la imagen										
			
				  if (data.indexOf("NOOK")==-1)   // NO ERROR
				  {					
					var _Color = "red_15x15.jpg";
					if (status==1)
						_Color = "green_15x15.jpg";
					$("#img_" + shareid).attr("src",_ImgPath+ _Color);
				  }
				
				  $("#dialog").html("<p><b>" + data + "</b></p>");
				  $("#dialog" ).dialog();
					
			  });
	 
 }
 

 function delete_trading()
 {
	 
	 $("#dialogo").html("¿Está seguro que quiere eliminarla de la cartera?.");
	 $("#dialogo").dialog({
	      modal: true, title: 'Aviso', zIndex: 10000, autoOpen: true,
	      width: 'auto', resizable: false,
	      buttons: {
	          Si: function () {
	        	  _DoYes();
	              $(this).dialog("close");
	          },
	          No: function () {
	        	  _DoNo();
	              $(this).dialog("close");
	          }
	      },
	      close: function (event, ui) {
	          $(this).remove();
	      }
	});
	 
 }
 
 function UpdateShare(shareid)
 {
	 
	 var ImgStatus = $("#img_" + shareid).attr("src");
	 var status=1;
	 if (ImgStatus.indexOf("green")>0)
	 {
		 status =0;
	 }
	 
	 $.post("ajax/update_share_status.jsp", { shareid:shareid, newstatus : status},
			  function(data) {
					// cambiamos la imagen										
			
				  if (data.indexOf("NOOK")==-1)   // NO ERROR
				  {					
					var _Color = "red_15x15.jpg";
					if (status==1)
						_Color = "green_15x15.jpg";
					$("#img_" + shareid).attr("src",_ImgPath+ _Color);
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
    
    lMarket = lActiveTradingMarkets;
		
	if (lMarket!=null)
	{
			/* recorremos mercados */ 
	    	
			%>
	    		
    		<div class="lista_acciones_trading">Acciones Intradia</div>
    		<table cellpadding="5" cellspacing="5">
    		<tr class="header">
    		<td>Mercado</td><td>Nombre</td><td>Symbol</td><td>Trading Activo</td>
    		<td>Cartera</td>
    		
    		</tr>
	    	<%	 
			
	    	for (int j=0;j<lMarket.size();j++)
	    	{
	    		
	    		Trading_Market oTrading_Market = lMarket.get(j);
	    		
				Market oMarket = MarketDAO.getMarket(oTrading_Market.getMarketId());
				
				Share oShareTrading= ShareDAO.getShare(oTrading_Market.getShareId());
				
	    		int _newstatus = 1;
	    		String _Color = "red_15x15.jpg";
	    		if (oShareTrading.getActive_trading().equals(new Long(1)))
	    		{
	    			_newstatus = 0;
	    			_Color = "green_15x15.jpg";
	    			
	    		}
	    		
	    			    		
	    		%>
	    		<tr class="data">
	    		<td><%=oMarket.getName() %></td>
	    		<td><%=oShareTrading.getName() %></td>
	    		<td><%=oShareTrading.getSymbol() %></td>
	    		<td><a 
	    			onclick="UpdateShare(<%=oShareTrading.getShareId()%>)" href="javascript:void(0)">
	    			<img id="img_<%=oShareTrading.getShareId() %>" class="share_status" src="<%=request.getContextPath()%>/images/<%=_Color%>"></img></a></td>
	    		
	    		<td><input class="button" type="button" name="delete_trading" id="delete_trading" value="Eliminar" onclick="delete_trading()"/>
	    			
	    		</tr>
	    		
	    			

		    	<%	    			    	
	    		}  // FIN DE MERCADOS
	    		%>
	    		</table>
	    		
	    <%
	    		
	    		
	    } // FIN DE IF
	   
	}// FIN DE IF


} // fin de trading para hoy
%>
	    		
 </div>
 </div>
 <div id="dialog"></div>
 <div id="dialog_result"></div>
  
