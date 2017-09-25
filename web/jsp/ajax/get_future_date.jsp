<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.tim.dao.PositionDAO" %>
<%@page import="com.tim.dao.ShareDAO" %>
<%@page import="com.tim.model.Position" %>
<%@page import="com.tim.model.Share" %>
<%@page import="java.util.Calendar" %>
<%@page import="com.tim.util.*" %>



<%


String Expire_Months= "";
String Expire_DayOfWeek= "";
String Expire_WeekOfMonth= "";

if (request.getParameter("e_m")!=null)
	Expire_Months = request.getParameter("e_m");
if (request.getParameter("e_d")!=null)
	Expire_DayOfWeek = request.getParameter("e_d");
if (request.getParameter("e_w")!=null)
	Expire_WeekOfMonth = request.getParameter("e_w");

if (!Expire_Months.equals("") &&  !Expire_Months.equals("") && !Expire_Months.equals(""))
	out.print(Utilidades.getActiveFutureDate(Expire_Months, Expire_DayOfWeek, Expire_WeekOfMonth));



%>
 