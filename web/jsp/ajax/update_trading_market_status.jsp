<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.tim.dao.PositionDAO" %>
<%@page import="com.tim.dao.MarketDAO" %>
<%@page import="com.tim.model.Position" %>
<%@page import="com.tim.model.Market" %>
<%@page import="java.util.Calendar" %>
<%@page import="com.tim.util.*" %>



<%


Long IdMarket= new Long(0);
Long NewStatus= new Long(0);
if (request.getParameter("marketid")!=null)
	IdMarket = Long.parseLong(request.getParameter("marketid"));
if (request.getParameter("newstatus")!=null)
	NewStatus = Long.parseLong(request.getParameter("newstatus"));


/* VERIFICAR SI TIENE OPERACIONES ABIERTAS EN ESTE MOMENTO SI SE DESACTIVA */
boolean bError = false;
if (NewStatus.equals(new Long(0)) &&  PositionDAO.ExistsPositionShareOpen(-1))
{
	bError = true;
}
if (!bError)
{	

	Market oMarket= MarketDAO.getMarket(IdMarket);
	oMarket.setTrading(NewStatus);

	MarketDAO.updateMarket2(oMarket);
	
	out.println("OK. Configuración guardada correctamente.");
}
else
	out.println("NOOK. No puede desactivarse de la operativa global con posiciones abiertas.");	


%>
 