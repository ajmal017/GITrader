<%@page import="com.tim.util.*" %>
<li id="li_datagrid_extra">
<div id="datagrid_extra">
<div class="izq"> 
<form name="frmfilterps" id=frmfilterps>
<div id="console_last_update" class="colorBlanco negrita flota_izq">
</div> 
<div class="filter_positions borde_bloque"><span class="titulo_bloque"></span>

<select id="filterps" name="filterps" onchange='$("#flex1").flexReload()'>
<option value="abiertas"><%=ConfigKeys.FILTER_CONSOLA_OPEN %></option>
<option value="ejecutadas"><%=ConfigKeys.FILTER_CONSOLA_EXECUTED %></option>
<option value="todas"><%=ConfigKeys.FILTER_CONSOLA_ALL %></option>
</select> 				
</div>
</div>
<div class="der"> 
<div id="first_feed_ticket" class="colorBlanco negrita flota_izq">
</div>
<div id="last_feed_ticket" class="colorBlanco negrita flota_izq">
</div>

</form>
</div>
</div>
</li>
<script>
$("#dropdown").append($("#li_datagrid_extra"));
</script>