<%@page import="java.text.DecimalFormat"%>
<%@page import="com.tim.dao.RealTimeDAO"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.sql.Timestamp"%>
<%@page import="com.tim.dao.PositionDAO" %>
<%@page import="com.tim.dao.ShareDAO" %>
<%@page import="com.tim.model.Position" %>
<%@page import="com.tim.model.Share" %>
<%@page import="com.tim.model.RealTime" %>
<%@page import="java.util.Calendar" %>
<%@page import="com.tim.util.*" %>



<%

/*  <rows>
<page>1</page>
<total>2</total>
<row id="reg1">
    <cell>reg1-cell1</cell>
    <cell>reg1-cell2</cell>
</row>
<row id="reg2">
    <cell>reg2-cell1</cell>
    <cell>reg2-cell2</cell>
</row>
</rows>

elect a.*, b.name, symbol shareSymbol, strategy_buy.name strategyBuy_name,";
Sql += " strategy_sell.name strategySell_name


*/

Long _page = new Long(0);
Long _reg_x_page = new Long(0);
if (request.getParameter("page")!=null)
	_page = Long.parseLong(request.getParameter("page")) ;	

if (request.getParameter("rp")!=null)
	_reg_x_page = Long.parseLong(request.getParameter("rp"));	


/* FILTRO DE TIPOS DE OPERACIONES,
ABIERTAS
EJECUTADAS
TODAS */
String _FilterPosition = "";
if (request.getParameter("filterps")!=null)
	_FilterPosition = request.getParameter("filterps");	




Calendar Hoy = Calendar.getInstance();

SimpleDateFormat sdf = new SimpleDateFormat();
sdf.applyPattern("dd/MM/yyyy HH:mm:ss"); 

DecimalFormat sNumero = new DecimalFormat();
sNumero.applyPattern("######.##");


SimpleDateFormat sdf2 = new SimpleDateFormat();
sdf2.applyPattern("HH:mm:ss");


java.util.List<Position> lPositions =  PositionDAO.getTradingPositions(new Timestamp(Hoy.getTimeInMillis()), _page,_reg_x_page, false, _FilterPosition); 

java.util.List<Position> lPositionsTOTAL = PositionDAO.getTradingPositions(new Timestamp(Hoy.getTimeInMillis()), _page,_reg_x_page, true, _FilterPosition);

RealTime _oDataFeed = null;
 _oDataFeed = (RealTime) RealTimeDAO.getFirstLastRealTimeTrading();



String _XMLPos = "<rows>";

Long RegIni = ((_page -1) * _reg_x_page) +1 ;

if (lPositions!=null && lPositions.size()>0)
{	
	
	_XMLPos += "<page>" + _page + "</page>";
	_XMLPos += "<total>" + lPositionsTOTAL.size() + "</total>";
	
	 
	 
	for (int j=0;j<lPositions.size();j++)
	{
		
		Position oMyPosition  = (Position) lPositions.get(j);
		
		_XMLPos += "<row id=\"reg" + RegIni + "\">";
		//_XMLPos += "<cell>" + oMyPosition.getShareName() +  "</cell>";
		if (oMyPosition.getDate_real_buy()!=null)		
	     	_XMLPos += "<cell>" +sdf2.format(oMyPosition.getDate_real_buy()) + "</cell>";
	    else
	     	_XMLPos += "<cell></cell>";
		_XMLPos += "<cell>" + oMyPosition.getShareSymbol() +  "</cell>";	
		
	    if (oMyPosition.getDate_real_sell()!=null)		
		     	_XMLPos += "<cell>" +sdf.format(oMyPosition.getDate_real_sell()) + "</cell>";
    	else
		     	_XMLPos += "<cell></cell>";
		 _XMLPos += "<cell>" +oMyPosition.getType() + "</cell>";
		 
		 /* acciones pendientes o el total si esta cerrada la posición */
		 int _SharePending;
		 if (oMyPosition.getState().equals(PositionStates.status.SELL_OK.toString())) {  // cerrada????
				 _SharePending =oMyPosition.getShare_number().intValue(); 	 
		 }
		 else
		 {	 
		 	_SharePending = oMyPosition.getShare_number_traded()!=null ? oMyPosition.getShare_number_traded().intValue() : 0;
		 	_SharePending = oMyPosition.getShare_number().intValue() - _SharePending;
		 }
		 _XMLPos += "<cell>" + _SharePending + "</cell>";    			     
	    if (!oMyPosition.getPrice_real_buy().equals(new Double(-1)))		
		     	_XMLPos += "<cell>" +Utilidades.RedondeaPrice(oMyPosition.getPrice_real_buy()) + "</cell>";
		 else
		     	_XMLPos += "<cell></cell>";
			
	    if (!oMyPosition.getPrice_real_sell().equals(new Double(-1)))		
				     	_XMLPos += "<cell>" +Utilidades.RedondeaPrice(oMyPosition.getPrice_real_sell()) + "</cell>";
		    else
				     	_XMLPos += "<cell></cell>";
		if (oMyPosition.getRealtime_value().equals(new Long(-1)))
	     {
	    		 _XMLPos += "</cell>";
	     }		 
	     else 
	     {	 
	    	 
	     _XMLPos += "<cell>" +Utilidades.RedondeaPrice(oMyPosition.getRealtime_value())+ "</cell>";
	     }
		// Balance vacio, se calcula despues
		_XMLPos += "<cell></cell>";
		// precio perdida
		if (!oMyPosition.getSell_price_stop_lost().equals(new Double(-1)))		
				     	_XMLPos += "<cell>" +oMyPosition.getSell_price_stop_lost() + "</cell>";
		    else
				     	_XMLPos += "<cell></cell>";
		 // stop perdida
		 // stop beneficio
		 // precio beneficio
		 if (!oMyPosition.getSell_price_stop_profit().equals(new Double(-1)))		
				     	_XMLPos += "<cell>" +oMyPosition.getSell_price_stop_profit() + "</cell>";
		    else
				     	_XMLPos += "<cell></cell>";
		
	     _XMLPos += "<cell>" +oMyPosition.getStrategyBuy_name()+ "</cell>";
	     _XMLPos += "<cell>" +oMyPosition.getStrategySell_name()+ "</cell>";
	     _XMLPos += "<cell>" +oMyPosition.getPositionID()+ "</cell>";
	     _XMLPos += "<cell>" +oMyPosition.getState_buy()+ "</cell>";
	     _XMLPos += "<cell>" +oMyPosition.getState_sell()+ "</cell>";
	     if (oMyPosition.getTrading_data_operations()!=null)
	     { 
	     	_XMLPos += "<cell>" +oMyPosition.getTrading_data_operations()+ "</cell>";
	     }
	     else
	     {
	    	 _XMLPos += "<cell></cell>";
	     }
	     
	     Share _Share = ShareDAO.getShare(oMyPosition.getShareID()); 
	     
	     if (_Share!=null &&  _Share.getMultiplier()!=null)
	     { 
	     	_XMLPos += "<cell>" +_Share.getMultiplier()+ "</cell>";
	     }
	     else
	     {
	    	 _XMLPos += "<cell>1</cell>";   // 1 por defecto
	     }
	    	 
	     _XMLPos += "</row>";
	    	 

	     
	     RegIni++;

	}
	

}

_XMLPos += "</rows>";

response.setContentType("application/xml");
out.print(_XMLPos);

%>
 