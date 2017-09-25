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
 
 
 
 <div class="borde_bloque"><span class="titulo_bloque"></span>
 <div class="admin">


<script>

function _deleteMarket(MarketId)
 {
	 $.post("ajax/delete_market.jsp", { marketid :MarketId },
			  function(data) {
					// cambiamos la imagen										
				
				  /* FECHAS DE FEEDS */
				 
				  if (data.indexOf("NOOK")>=0)
				  {
					  $("#dialog").html("<p><b>" + data + "</b></p>");					  
					  $("#dialog").dialog({
						      modal: true, title: 'Aviso', zIndex: 10000, autoOpen: true,
						      width: 'auto', resizable: false,
						      buttons: {
						          Aceptar: function () {						        	
						             $(this).dialog("close");
						          }
						      }
						});
					  
					  
				  }	  
				  else
				  {
					  //$("#platform_content_error").hide(2000);					  
					  location.reload();
					  
					
				  }

					
			  });
 }

</script>
<%

java.util.List<Market> lMarkets = MarketDAO.getListAllMarket();

if (lMarkets!=null)
{	
	if (lMarkets!=null)
	{
			/* recorremos mercados */ 
	    	
			%>
	    		
    		<div class="lista_acciones_trading"><img class="img_cab"  src="<%=request.getContextPath()%>/images/stock-market-financial-dice-trading-6150113.jpg">
    		<ul  class="trigger_action"><li><a class="negrita f12px link_action" href="<%=request.getContextPath()%>/jsp/admin_ficha_market.jsp"><img src="<%=request.getContextPath()%>/images/action_icons/add.png"/><span class="span_action">Añadir</span></a></li></ul>
    		</div>
    		
    		<table class="tmarket" cellpadding="5" cellspacing="5">
    		<tr class="header">
    		<td>Mercado</td>
    		<!-- <td>Exchange</td>
    		<td>Primary Exchange</td> -->
    		<td>Apertura</td>
    		<td>Cierre</td>
    		<td>Activo</td>
    		<td></td>
    		<td></td>
    		
    		
    		</tr>
	    	<%	 
			
	    	for (int j=0;j<lMarkets.size();j++)
	    	{
	    		
	    		Market oMarket = lMarkets.get(j);
	    		

	    		
	    		String _Color = "red_15x15.jpg";
	    		if (oMarket.getActive().equals(new Long(1)))
	    		{	    			
	    			_Color = "green_15x15.jpg";
	    			
	    		}
	    		
	    	%>
	    	
				<tr class="data rowmarket">
		    		<td><a href="<%=request.getContextPath()%>/jsp/admin_shares.jsp?marketid=<%=oMarket.getMarketID()%>"><%=oMarket.getName() %></a></td>		    		
		    		<td><%=oMarket.getStart_hour() %></td>
		    		<td><%=oMarket.getEnd_hour() %></td>
		    		<td><img  src="<%=request.getContextPath()%>/images/<%=_Color%>"></img></td>
		    		<td><ul  ><li class="trigger_action"><a class="negrita f12px link_action" href="<%=request.getContextPath()%>/jsp/admin_ficha_market.jsp?marketid=<%=oMarket.getMarketID()%>"><span class="span_action">Editar</span></a></li></ul></td>
		    		<td><ul><li class="trigger_action ancho30px"><a class="negrita f12px link_action" href="javascript:_deleteMarket('<%=oMarket.getMarketID()%>')"><img class="ancho20px" src="<%=request.getContextPath()%>/images/action_icons/delete.png"/></a></li></ul></td>
		    	</tr>		   
		     </a>
					
	    		
	    <%

	   
			}// FIN DE IF
			%>
			
			   </table>	
			   <% 
	}

} // fin de trading para hoy
%>


    		
	    		
 </div>
 </div>
 
<div id="dialog" ></div> 
  
