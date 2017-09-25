<%@page import="com.tim.dao.StrategyDAO"%>
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
<%@page import="com.tim.model.Strategy"%>
<%@page import="com.tim.model.Trading"%>
<%@page import="com.tim.model.Trading_Market"%>



<% 

String ClassStrag = "";

Strategy oStrategy = null;

if (request.getParameter("stratidclass")!=null)
{
	ClassStrag = request.getParameter("stratidclass") ; 
}

oStrategy = StrategyDAO.getStrategy(ClassStrag);

//cargo la estrategia del classname y ya tengo los datos
Class oFactoryStrat =  Class.forName(oStrategy.getClassName());
//Strategy oStrategyFactory = new Strategy(); 

Strategy oStrategyView = (Strategy) oFactoryStrat.newInstance();


request.setAttribute("strategy", oStrategy);  


%>

<jsp:include page="<%=oStrategyView.getJSP_PAGE()%>"/>




 
