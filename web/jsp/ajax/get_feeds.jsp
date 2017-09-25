<%@page import="com.tim.dao.RealTimeDAO"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.tim.dao.PositionDAO" %>
<%@page import="com.tim.model.Position" %>
<%@page import="com.tim.model.RealTime" %>
<%@page import="java.util.Calendar" %>
<%@page import="com.tim.util.*" %>



<%

RealTime _oDataFeed = null;
 _oDataFeed = (RealTime) RealTimeDAO.getFirstLastRealTimeTrading();
 
 SimpleDateFormat sdf2 = new SimpleDateFormat();
 sdf2.applyPattern("HH:mm:ss");
 
 String _XMLPos  ="";
 
 if (_oDataFeed.get_FirstTradingDate()!=null)
 {
	 _XMLPos += sdf2.format(_oDataFeed.get_FirstTradingDate()) + "|";
	 
 }
 else
 {
	 _XMLPos += "|";
 }
 if (_oDataFeed.get_LastTradingDate()!=null)
 {
	 _XMLPos += sdf2.format(_oDataFeed.get_LastTradingDate());
 
 }
 out.print(_XMLPos);
 
 %>