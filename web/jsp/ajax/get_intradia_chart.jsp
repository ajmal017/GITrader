<%@page import="com.tim.dao.MarketShareDAO"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="com.tim.dao.RealTimeDAO"%>
<%@page import="com.tim.model.RealTime"%>
<%@page import="com.tim.model.Market"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.tim.dao.PositionDAO" %>
<%@page import="com.tim.dao.ShareDAO" %>
<%@page import="com.tim.dao.MarketDAO" %>
<%@page import="com.tim.model.Position" %>
<%@page import="com.tim.model.Share" %>
<%@page import="com.tim.model.RealTime" %>
<%@page import="java.util.Calendar" %>
<%@page import="com.tim.util.*" %>


<% 

Long _StockID = Long.parseLong(request.getParameter("stockid"));
Long _StockMarketId = Long.parseLong(request.getParameter("stockmktid"));


Share StockShare = ShareDAO.getShare(_StockID);

java.util.List<RealTime> _lRealtime =  null;


Market _oMarket = MarketDAO.getMarket(_StockMarketId);

Calendar DateMinMaxFrom =Utilidades.getNewCalendarWithHour(_oMarket.getStart_hour());
Calendar DateMinMaxTo =Utilidades.getNewCalendarWithHour(_oMarket.getEnd_hour());

_lRealtime = (java.util.List<RealTime>) RealTimeDAO.getAllRealTime(_StockID.intValue(),new Timestamp(DateMinMaxFrom.getTimeInMillis()), new Timestamp(DateMinMaxTo.getTimeInMillis()));

String _JSON = "[";
 

for (int j=0;j<_lRealtime.size();j++)
	
{
	RealTime _oRTIME =_lRealtime.get(j);  
	_JSON += "[" +  _oRTIME.getAddedDate().getTime()  + "," +_oRTIME.getValue() + "]";
	
	if (j!=_lRealtime.size()-1)
		_JSON += ",";
	
	
}

_JSON += "]";



/* 
[
[1317888000000,372.5101,375,372.2,372.52],
[1317888060000,372.4,373,372.01,372.16],
[1317888120000,372.16,372.4,371.39,371.62]
]
		*/

%>





<%
 response.addHeader("Content-Type", "application/json");
/*String JSON = "jj([[1317888000000,372.5101,375,372.2,372.52],[1317888060000,372.4,373,372.01,372.16],[1317888120000,372.16,372.4,371.39,371.62],[1317888180000,371.62,372.16,371.55,371.75],[1317888240000,371.75,372.4,371.57,372],[1317888300000,372,372.3,371.8,372.24],[1317888360000,372.22,372.45,372.22,372.3],[1317888420000,372.3,373.25,372.3,373.15],[1318607940000,421.94,422,421.8241,422]])";*/
out.print(_JSON);

%>


 