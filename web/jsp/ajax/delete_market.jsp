<%@page import="com.tim.dao.TradingMarketDAO"%>
<%@page import="com.tim.dao.MarketDAO"%>
<%@page import="com.tim.dao.MarketShareDAO"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.tim.dao.PositionDAO" %>
<%@page import="com.tim.dao.ShareDAO" %>
<%@page import="com.tim.model.Position" %>
<%@page import="com.tim.model.Share" %>
<%@page import="java.util.Calendar" %>
<%@page import="com.tim.util.*" %>



<%


Long IdMarket= new Long(0);
if (request.getParameter("marketid")!=null)
	IdMarket = Long.parseLong(request.getParameter("marketid"));


/* VERIFICAR SI TIENE ACCIONES ASOCIADAS EL MERCADO */
boolean bError = false;
/* dejo desactivar con operaciones abiertas, pero hay que cerrar posición si la hubiera

if (NewStatus.equals(new Long(0)) &&  PositionDAO.ExistsPositionShareOpen(IdShare.intValue()))
{
	bError = true;
}*/

java.util.List<Share> lShares = ShareDAO.getListShareByMarket(IdMarket);

if (lShares.size()>0)	
	bError = true;

if (!bError)
{
	
	
	MarketDAO.deleteMarket(IdMarket);
	TradingMarketDAO.deleteTradingMarket(IdMarket.intValue());
	
	out.println("OK. Mercado eliminado correctamente");
}
else
	out.println("NOOK. No puede eliminar con acciones asociadas a él");	


%>
 