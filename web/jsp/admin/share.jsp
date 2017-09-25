<%@page import="com.tim.util.ConfigKeys"%>
<%@page import="com.tim.dao.StrategyDAO"%>
<%@page import="com.tim.model.Strategy"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.tim.dao.RealTimeDAO"%>
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
<%@page import="com.tim.model.RealTime"%>
<%@page import="com.tim.model.Share"%>
<%@page import="com.tim.model.Trading"%>
<%@page import="com.tim.model.Trading_Market"%>
<%@page import="com.tim.util.Utilidades"%>

 
 <script>
 
 var _ImgPath = '<%=request.getContextPath()%>/images/';
 
 function _DoNo(){
	 
 }
 
 function _DoYes(shareid, action, marketid){
	 
	 $.post("ajax/manage_share_trading.jsp", { shareid:shareid, action : action, marketid:marketid},
			  function(data) {
					// cambiamos la imagen										
				  	
				  if (data.indexOf("NOOK")==-1)   // NO ERROR
				  {
					
					  
					window.location.reload();
					/* var _ColorClass = "rojo colornegro";
					var _Text = "No";
					if (action=="add")
					{
						var _ColorClass = "verde colornegro";
						var _Text = "Si";
					}
					var _HTML = "<a href=javascript:action_trading(" + shareid + ",\"delete\"," + marketid + ")  class='" + _ColorClass + "'>" + _Text + "</a>";
					
					$("#cartera_" + shareid).html(_HTML);
					alert($("#cartera_" + shareid).html());
					*/
					
					
					
				  }
				  else
			      {
					  $("#dialog").html("<p><b>" + data + "</b></p>");
					  $("#dialog" ).dialog();  
					  
				  }
					
					
				  
					
			  });
	 
 }
 

 function action_trading(shareId, action, marketId)
 {	 
	 $("#dialog").html("¿Está seguro que quiere agregar/eliminar la acción de  la cartera?.");
	 $("#dialog").dialog({
	      modal: true, title: 'Aviso', zIndex: 10000, autoOpen: true,
	      width: 'auto', resizable: false,
	      buttons: {
	          Si: function () {
	        	  _DoYes(shareId, action,marketId );
	              //$(this).dialog("close");
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
				 else  // error
				  {
					  $("#dialog").html("<p><b>" + data + "</b></p>");
					  $("#dialog" ).dialog();  
				  }
				
				  
					
			  });
	 
 }
 
 
 </script>
  
 
 <div class="borde_bloque"><span class="titulo_bloque"></span>
 <div class="admin">
 	
<%

//HORA DE FIN DE CALCULO DE MAX Y MINIMOS.

Long MarketId = Long.parseLong(request.getParameter("marketid"));

Market oMarket = MarketDAO.getMarket(MarketId);

List<Share> lShares = ShareDAO.getListShareByMarket(MarketId);

DecimalFormat df = new DecimalFormat("##0.##");
SimpleDateFormat sdf = new SimpleDateFormat();
sdf.applyPattern("HH:mm:ss"); 



%>
	    	<div id="dialog"></div> 
    		<!--  <div class="lista_acciones_trading">Contratos</div> -->
    		<div class="navegacion">
	    		<ul>
	    		<li class="trigger_action"><a class="negrita f12px link_action" href="<%=request.getContextPath()%>/jsp/admin_ficha_share.jsp?marketid=<%=MarketId%>">
	    		<img src="<%=request.getContextPath()%>/images/action_icons/add.png">
	    		<span class="span_action">Contrato</span>
	    		</a>
	    		</li>
	    		<li  class="trigger_action"><a class="negrita f12px link_action" onclick="window.location.reload()" href="javascript:void(0)">
	    		<img src="<%=request.getContextPath()%>/images/action_icons/refresh.png">
	    		<span class="span_action">Refrescar</span>
	    		</a>
	    		</li >
	    		<li  class="trigger_action"><a class="negrita f12px link_action" href="<%=request.getContextPath()%>/jsp/admin.jsp?marketid=<%=MarketId%>">
	    		<img src="<%=request.getContextPath()%>/images/action_icons/back.png">
	    		<span class="span_action">Volver</span>
	    		</a>
	    		</li>
	    		</ul>    			
    			
    		</div>
    		<table class="tmarket" cellpadding="5" cellspacing="5">
    		<tr class="header">
    		<td>Symbol</td><td>Acc.</td><td>Gap</td><td>StopLost</td><td>StopProfit</td><td>Active</td><td>Trading</td>
    		<td>En Cartera</td>
    		<td>RASup</td>
    		<td>RAInf</td>
    		<td>Tws Read Error</td><td>Tws Trade Error</td>
    		</tr>
	    	<%	 
			if (lShares!=null)
			
			{	
	    	for (int j=0;j<lShares.size();j++)
	    	{
	    	
				
				Share oShare = lShares.get(j);
				
				String _ColorActive = "red_15x15.jpg";
	    		String _ColorTrading = "red_15x15.jpg";
	    		RealTime _ActualRealTime = null;
	    		
	    		if (oShare.getActive().equals(new Long(1)))
	    		{
	    			
	    			_ColorActive = "green_15x15.jpg";
	    			
	    		}
	    		
	    		if (oShare.getActive_trading().equals(new Long(1)))
	    		{
	    			
	    			_ColorTrading = "green_15x15.jpg"; 
	    			
	    		 
	    			
	    		}
	    		String HoraInicioLecturaMaxMin = Utilidades.getActualHourFormatPlusMinutes(oMarket.getStart_hour(), oShare.getOffset1min_read_from_initmarket());
    			String HoraFinLecturaMaxMin = Utilidades.getActualHourFormatPlusMinutes(oMarket.getStart_hour(), oShare.getOffset2min_read_from_initmarket());

    			Calendar DateMinMaxFrom =Utilidades.getNewCalendarWithHour(HoraInicioLecturaMaxMin);
    			Calendar DateMinMaxTo =Utilidades.getNewCalendarWithHour(HoraFinLecturaMaxMin);
    			
    			
    			_ActualRealTime = RealTimeDAO.getMinMaxValue_DatesRealTimeBetweenDates(oShare.getShareId().intValue(), new java.sql.Timestamp(DateMinMaxFrom.getTimeInMillis()),new java.sql.Timestamp(DateMinMaxTo.getTimeInMillis()));
	    			    		
	    		%>
	    		<tr class="data rowmarket">	    		
	    		<td name="<%=oShare.getSymbol()%>" id="<%=oShare.getShareId()%>" class="context-menu-one"><a href="<%=request.getContextPath()%>/jsp/admin_ficha_share.jsp?shareid=<%=oShare.getShareId()%>"><%=oShare.getSymbol() %></a></td>
	    		<td><%=oShare.getNumber_purchase() %></td>
	    		<td><%=df.format(oShare.getPercentual_value_gap()*100)%></td>
	    		<td><%=df.format(oShare.getSell_percentual_stop_lost()*100)%></td>
	    		<td><%=df.format(oShare.getSell_percentual_stop_profit()*100)%></td>
	    		<td> 	    		
	    			<img  src="<%=request.getContextPath()%>/images/<%=_ColorActive%>"></img>
	    		</td>
	    		<td><a 
	    			onclick="UpdateShare(<%=oShare.getShareId()%>)" href="javascript:void(0)">
	    			<img id="img_<%=oShare.getShareId() %>" class="share_status" src="<%=request.getContextPath()%>/images/<%=_ColorTrading%>"></img></a></td>
	    		<td id="cartera_<%=oShare.getShareId()%>"><%
	    		if (oShare.getActive_trading_today().intValue()>0)
	    		{ %>
	    			<a href="javascript:action_trading('<%=oShare.getShareId()%>','delete','<%=MarketId %>')" class="colornegro"><img  src="<%=request.getContextPath()%>/images/green_15x15.jpg"></img></a>
	    		<%	    			
	    		}
	    		else
	    		{%>
	    		   <a href="javascript:action_trading('<%=oShare.getShareId()%>','add','<%=MarketId %>')"   class="colornegro"><img  src="<%=request.getContextPath()%>/images/red_15x15.jpg"></img></a>
	    		<% }  %>
	    		
	    		</td>
	    		<td class="colornaranja izq">
	    		<% if (_ActualRealTime!=null && _ActualRealTime.get_LastTradingDate()!=null) { %>
	    		<%=sdf.format(_ActualRealTime.get_LastTradingDate())%> | <span class="colornegro"><%=_ActualRealTime.getMax_value()%></span>
	    		<% } %>
	    		</td>
	    		<td class="colornaranja izq">
	    		<% if (_ActualRealTime!=null && _ActualRealTime.get_FirstTradingDate()!=null) { %>
	    		<%=sdf.format(_ActualRealTime.get_FirstTradingDate())%> | <span class="colornegro"><%=_ActualRealTime.getMin_value()%></span>
	    		<% } %>
	    		</td>
	    			
	    			<td class="colorgris izq w100px"><%=(oShare.getLast_error_data_read()!=null && !oShare.getLast_error_data_read().equals("")  ? oShare.getLast_error_data_read() : "") %></td>
	    			<td class="colorgris izq w100px"><%=(oShare.getLast_error_data_trade()!=null && !oShare.getLast_error_data_trade().equals("")  ? oShare.getLast_error_data_trade() : "") %></td>
	    		</tr>
	    		
	    			

		    	<%	    			    	
	    		}  // FIN DE MERCADOS
	    		%>
	    		</table>
	    		
	    <%
	    		
	    		
	    } // FIN DE IF

%>
<script>
$(function(){
	// iteramos sobre cada opcion de accion c
	$.each($(".context-menu-one"), function( key, value ) {
		 
	    $.contextMenu({
	        selector: '.context-menu-one', 
	        callback: function(key, options) {
	            var m = "clicked: " + key + "," + options;
	            TINY.box.show({iframe:'charting/intradia_chart.jsp?stockid=' + $(this).attr("id") + "&stockname=" + $(this).attr("name") + "&mktid=<%=MarketId%>",post:'id=' + $(this).attr("id"),width:800,height:500,opacity:20,topsplit:3});
	           // window.console && console.log(m) || alert(m); 
	           
	        },
	        items: {
	            "Intradia area": {name: "Intradia area", icon: "cut"},
	            /*"Intradia candlestick": {name: "Intradia candlestick", icon: "cut"}, */            
	            "quit": {name: "Quit", icon: "quit"}
	        }
	    });
		
		//alert( $(this).attr("id"));
		}); 
	

    
   /*  $('.context-menu-one').on('click', function(e){
    	 alert(TINY);
    	TINY.box.show({url:'http://localhost:8080/TraderInteractiveModel/jsp/inicio.jsp',post:'id=16',width:200,height:100,opacity:20,topsplit:3});
        //console.log('clicked', this);         
    })*/
});
    </script>
</script>	    		
 </div>
 </div>

