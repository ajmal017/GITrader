<%@page import="com.tim.dao.SimulationDAO"%>
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
<%@page import="com.tim.model.Simulation"%>
<%@page import="java.text.SimpleDateFormat"%>
 

<% List<Simulation> _lSimulation = SimulationDAO.getSimulations(); %>

 <div class="borde_bloque"><span class="titulo_bloque"></span>
 <div class="admin">	
<div class="lista_acciones_trading"><img class="img_cab" src="<%=request.getContextPath()%>//images/stock-market-financial-dice-trading-6150113.jpg">
<ul class="trigger_action"><li><a class="negrita f12px link_action" href="<%=request.getContextPath()%>/jsp/back_testing_new.jsp"><img src="/TraderInteractiveModel/images/action_icons/add.png"><span class="span_action">Nueva Simulación</span></a></li></ul>
</div>
<table class="tmarket" cellpadding="5" cellspacing="5">
    		<tr class="header">
    		<td>Descripción</td>    		
    		<td>Fecha Inicio</td>
    		<td>Fecha Fin</td>
    		<td>Estado</td>    		    	
    		</tr>
<%

SimpleDateFormat sdfFull = new SimpleDateFormat ("dd/MM/yyyy");

if (_lSimulation!=null && _lSimulation.size()>0)
{
for (int j=0;j<_lSimulation.size();j++)
{
	Simulation _oSimulation = _lSimulation.get(j);%>
	<tr class="data rowmarket">
    		<td><a href=""><%=_oSimulation.getDescription() %></a></td>    		
    		<td><%=sdfFull.format(_oSimulation.getStartDate().getTime()) %></td>
    		<td><%=sdfFull.format(_oSimulation.getEndDate().getTime()) %></td>
    		<td><%=_oSimulation.getStatus() %></td>    		    	
    </tr>
<%    	
}
}
%>
    		
    		
</table>
</div>
