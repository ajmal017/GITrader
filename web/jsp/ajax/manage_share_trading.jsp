<%@page import="com.tim.dao.TradingMarketDAO"%>
<%@page import="com.tim.dao.MarketShareDAO"%>
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
if (request.getParameter("marketid")!=null)
	IdMarket = Long.parseLong(request.getParameter("marketid"));
Long IdShare= new Long(0);
String Action ="";
if (request.getParameter("shareid")!=null)
	IdShare = Long.parseLong(request.getParameter("shareid"));
if (request.getParameter("action")!=null)
	Action= request.getParameter("action");

/* VERIFICAR SI TIENE OPERACIONES ABIERTAS EN ESTE MOMENTO SI SE DESACTIVA */
boolean bError = false;

try 
{

	if (Action.equals("delete") && PositionDAO.ExistsPositionShareOpen(IdShare.intValue()))
			bError  = true;
	if (!bError)
	{
		if (Action.equals("delete"))
		{
			TradingMarketDAO.deleteTradingShare(IdShare.intValue(), IdMarket.intValue());
			
			
		}
		if (Action.equals("add"))
		{
					
			TradingMarketDAO.addTradingShare(IdShare.intValue(), IdMarket.intValue());
		}
		
		
		out.println("OK. Configuración guardada correctamente.");
	}
	else
		out.println("NOOK. No puede desactivarse de la operativa con posiciones abiertas.");	
}
catch (Exception e)
{
	out.println("NOOK. No puede desactivarse de la operativa. " + e.getMessage());
}

%>


 