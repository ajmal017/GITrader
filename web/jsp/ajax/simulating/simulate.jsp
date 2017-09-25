<%@page import="com.tim.dao.ConfigurationDAO" %>
<%@page import="com.tim.model.Configuration" %>


<% 
Configuration _oConf = ConfigurationDAO.getConfiguration("SIMULATION_MODE");
String _KeyValue  = "";
if (_oConf!=null)
    _KeyValue  = _oConf.getValue();
 %>
 	