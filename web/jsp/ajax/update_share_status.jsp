<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.tim.dao.PositionDAO" %>
<%@page import="com.tim.dao.ShareDAO" %>
<%@page import="com.tim.model.Position" %>
<%@page import="com.tim.model.Share" %>
<%@page import="java.util.Calendar" %>
<%@page import="com.tim.util.*" %>



<%


Long IdShare= new Long(0);
Long NewStatus= new Long(0);
if (request.getParameter("shareid")!=null)
	IdShare = Long.parseLong(request.getParameter("shareid"));
if (request.getParameter("newstatus")!=null)
	NewStatus = Long.parseLong(request.getParameter("newstatus"));

/* VERIFICAR SI TIENE OPERACIONES ABIERTAS EN ESTE MOMENTO SI SE DESACTIVA */
boolean bError = false;
/* dejo desactivar con operaciones abiertas, pero hay que cerrar posición si la hubiera

if (NewStatus.equals(new Long(0)) &&  PositionDAO.ExistsPositionShareOpen(IdShare.intValue()))
{
	bError = true;
}*/

if (!bError)
{
	Share oShare = ShareDAO.getShare(IdShare);
	oShare.setActive_trading(NewStatus);

	ShareDAO.updateShare(oShare);
	
	out.println("OK. Configuración guardada correctamente.");
}
else
	out.println("NOOK. No puede desactivarse de la operativa con posiciones abiertas.");	


%>
 