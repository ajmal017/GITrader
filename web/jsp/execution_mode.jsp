<%@page import="com.tim.util.*" %>
<%@page import="com.tim.dao.ConfigurationDAO" %>
<%@page import="com.tim.model.Configuration" %>


<script>

function _changeMode(eve)
{

	eve.preventDefault();	
	
	$("#dialog_mode_change").dialog({
	    autoOpen: true,
	    modal: true,
	    closeOnEscape: true,
	    buttons: {
	        OK: function() {
	        	var $this = $(this);
	            $.ajax({
	                type: 'POST',
	                //url: 'ajax/simulating/simulate.jsp?m=' + $("#mode_select").val(),
	                url: 'ajax/simulating/change_config_key.jsp?m=' + $("#mode_select").val() + '&qK=SIMULATION_MODE',
	                //data: alert(data),//$(this).dialog('close'),
	                success: function(data, textStatus, jqXHR)
    				{
        				if (data.indexOf("NOOK")>=0)  // error
        					alert(data.trim());
        				else
        				{
        					$("#mode_select").removeClass();
        					$("#mode_select").addClass($("#mode_select").val());
        					$this.dialog('close');
        			    }
        					
    				},
	                error: function(xml, status, error) {
	                    alert(error);
	                }
	            });
	        },
	        Cancel: function() {
	        	
	        	if ($("#mode_select").val()=='<%=ConfigKeys._REAL_TRADING_MODE%>')
	        	{
	        		$("#mode_select").val('<%=ConfigKeys._SIMULATED_TRADING_MODE%>');
	        	
	        	}
	        	else
	        	{
	        			$("#mode_select").val('<%=ConfigKeys._REAL_TRADING_MODE%>');
	        	
	        	}
	            $(this).dialog('close');
	        }
	    }
	});	
		
}

function _openSet()
{
	
	
	
	$("#dialog_account_mode").dialog({
	    autoOpen: true,
	    modal: true,
	    closeOnEscape: true,
	    buttons: {
	        OK: function() {
	        	var $this = $(this);
	            $.ajax({
	                type: 'POST',
	                url: 'ajax/simulating/change_config_key.jsp?m=' + $("#dialog_account_mode #name").val() + '&qK=ACCOUNT_IB_NAME',
	                //data: alert(data),//$(this).dialog('close'),
	                success: function(data, textStatus, jqXHR)
    				{
        				if (data.indexOf("NOOK")>=0)  // error
        					alert(data.trim());
        				else
        				{
        					// OK|ACCOUNT
        					$("#account_val").html(data.split("|")[1].trim());        					
        					$this.dialog('close');
        				}
        					
    				},
	                error: function(xml, status, error) {
	                    alert(error);
	                }
	            });
	        },
	        Cancel: function() {
	            $(this).dialog('close');
	        }
	    },
	    open: function() {
	        $(this).html('').load('config_trading_mode.jsp');
	    }
	});	
}

</script>

<%

Configuration _oConf = ConfigurationDAO.getConfiguration("SIMULATION_MODE");
Configuration _oConf2 = ConfigurationDAO.getConfiguration("ACCOUNT_IB_NAME");

%>

 
<div class="execution_mode" title="Permite establecer el modo de simulación. Se activará el modo simulado a través  
de la cuenta paper o similar. Deberá estar configurada para tal efecto">
<span class="titulo">Mode</span>
	<select id="mode_select" class="<%=ConfigKeys._REAL_TRADING_MODE  %>" onChange="_changeMode()">
		<option value="<%=ConfigKeys._REAL_TRADING_MODE  %>"><%=ConfigKeys._REAL_TRADING_MODE  %></option>
		<option value="<%=ConfigKeys._SIMULATED_TRADING_MODE  %>"><%=ConfigKeys._SIMULATED_TRADING_MODE  %></option>
	</select> 				
	<div class="config"><a href="javascript:_openSet()">Acc (<span id="account_val"><%=_oConf2.getValue() %></span>)</a></div>	
</div>

<% if (_oConf!=null) 
{
%>


<script>

$( document ).ready(function() {
	
	$("#mode_select").val('<%=_oConf.getValue()%>');
	$("#mode_select").removeClass();
	$("#mode_select").addClass('<%=_oConf.getValue()%>');
});
</script>


<% }  %>
 
<div id="dialog_account_mode" title="IB TWS Account Setting" style="display:none">
  <p id="info"></p>
</div>
<div id="dialog_mode_change" title="Cambio Modo" style="display:none">
  <p>¿Está seguro de que quiere cambiar el modo de la operativa?. Verifique la cuenta establecida <span class="negrita">Account</span> y que las posiciones estén todas cerradas</p>
</div>

<script>
$('#mode_select').change(function(ev) {

	_changeMode(ev);

});
</script>