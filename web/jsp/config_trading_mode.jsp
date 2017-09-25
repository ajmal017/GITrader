<%@page import="com.tim.dao.ConfigurationDAO" %>
<%@page import="com.tim.model.Configuration" %>


<%

    String _KeyValue  = "";
	
	Configuration _oConf = ConfigurationDAO.getConfiguration("ACCOUNT_IB_NAME");
     _KeyValue  = "";
    if (_oConf!=null)
    	_KeyValue  = _oConf.getValue();
    	

%>

<div id="wrapper_set_account">
  <span class="info">Introduzca la cuenta para operar acorde al modo elegido. Es posible dejarla en blanco con una única cuenta o especificarla en caso de poseer varias</span>
  <form>
  <fieldset>  	   
    <label for="name">Account Name</label>
    <input type="text" name="name" id="name" class="mandatory" value=<%=_KeyValue%>>    
  </fieldset>
  </form>
</div>
