<%@page import="com.tim.dao.RuleDAO"%>
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
<%@page import="com.tim.model.Rule"%>
<%@page import="com.tim.model.Trading"%>
<%@page import="com.tim.model.Trading_Market"%>



<% 

String ClassRule = "";

Rule oRule = null;

if (request.getParameter("ruleidclass")!=null)
{
	ClassRule = request.getParameter("ruleidclass") ; 
}

oRule = RuleDAO.getRule(ClassRule);

//cargo la estrategia del classname y ya tengo los datos
Class oFactoryStrat =  Class.forName(oRule.getClassName());
//Strategy oStrategyFactory = new Strategy(); 

Rule oRuleView = (Rule) oFactoryStrat.newInstance();


request.setAttribute("rule", oRule);  


%>

<jsp:include page="<%=oRuleView.getJSP_PAGE()%>"/>




 
