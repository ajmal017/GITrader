<%@page import="com.tim.util.ConfigKeys"%>
<%@page import="com.tim.dao.ConfigurationDAO" %>
<%@page import="com.tim.dao.PositionDAO" %>
<%@page import="com.tim.model.Configuration" %>
<%@page import="com.tim.model.Position" %>

<%

    String _KeyValue  = "";
    String _KeyName  = "";
    String _messageOK  = "";
    boolean _bError=false;
    if (request.getParameter("m")!=null)
    {
    	 _KeyValue  =request.getParameter("m");    	
    }
    /*else 
    	_bError=true;*/
    if (request.getParameter("qK")!=null)
    {
    	_KeyName  =request.getParameter("qK");    	
    }
    /* else 
    	_bError=true; */

    /* VALIDACION CONTRA SIMULATION_MODE. NO ES POSIBLE CAMBIAR EL MODO CON POSICIONES ABIERTAS. 
    
       public static final String _CONFIG_KEY_ACCOUNT_NAME = "SIMULATED_T";
    public static final String _CONFIG_KEY_SIMULATE_NAME = "REAL_T";
    */ 	
	
	Configuration _oConf = ConfigurationDAO.getConfiguration(_KeyName);
    	
    if (_KeyName.equals(ConfigKeys._CONFIG_KEY_SIMULATE_NAME))  // VERIFICAMOS POSICIONES ABIERTAS
    {
    	
    	_bError = PositionDAO.ExistsPositionShareOpen(-1);
    	
    }
    		
	
	
	/* todo bien ? */
    if (_oConf!=null && !_bError)
    {
    		_oConf.setValue(_KeyValue);
    		ConfigurationDAO.setConfigurationKey(_oConf);
    		_messageOK ="OK|"  + _KeyValue;
    		%>
    		
    <%
    }	
    else   // error
    {
      _messageOK ="NOOK. Error.Propiedad no encontrada, datos enviados incorrectos o posiciones abiertas existentes.";
    } 
 %>
 <%=_messageOK %>	

