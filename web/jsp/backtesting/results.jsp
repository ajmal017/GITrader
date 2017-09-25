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
 

<% Long _sId = Long.parseLong(request.getParameter("sid")); %>

 <div class="borde_bloque"><span class="titulo_bloque"></span>
 <div class="admin">	
<div class="lista_acciones_trading"><img class="img_cab" src="<%=request.getContextPath()%>//images/stock-market-financial-dice-trading-6150113.jpg">
</div>
<div class="results">
	 <div>
	</div>
</div>

</div>
</div>
