<%@page import="com.tim.service.*"%>
<%@page import="com.tim.dao.*"%>

<%

int CLIENT_ID = 7;	  // el dos para leer, el 3 para escribir

String  _Host = ConfigurationDAO.getConfiguration("TWS_HOST").getValue();

String  _AccountNameIB = ConfigurationDAO.getConfiguration("ACCOUNT_IB_NAME").getValue();
int  _Port = Integer.parseInt(ConfigurationDAO.getConfiguration("TWS_PORT").getValue());

TIMApiGITrader oTWS = new TIMApiGITrader(_Host,_Port, CLIENT_ID);







try
{
		
	if (!oTWS.GITraderTWSIsConnected())
	{
		oTWS.GITraderDisconnectFromTWS();
	}
			
	
	if (!oTWS.GITraderTWSIsConnected()) 	oTWS.GITraderConnetToTWS();
	

	if (!oTWS.GITraderTWSIsConnected())
	{
		out.println("NOOK|" + oTWS.GITraderError());
	}
	else
		out.println("OK");	
	
	oTWS.disconnectFromTWS();
}
catch (Exception e)
{
	if (oTWS.GITraderTWSIsConnected()) oTWS.GITraderDisconnectFromTWS();
	out.println("NOOK|" + e.getMessage());
}






%>