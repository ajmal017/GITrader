<%@page import="com.tim.dao.RuleDAO"%>
<%@page import="com.tim.model.Rule"%>
<%@page import="com.tim.dao.StrategyDAO"%>




 <script>
 
 
 
 function doFunctionForYes()
 {
 
 // con jquery y dialog da pete
 // validamos valores númericos
	 
	 document.forms[0].submit();

 
 }
 function doFunctionForNo()
 {
 	//window.location.reload();
 }
 
 
 
 function go()
 {
bError = false;



$('.mandatory').each(function() {
    
	var currentElement = $(this);
    var value = $.trim(currentElement.val()); // if it is an input/select/textarea field
    
    if (value=='')
    {	
    	bError = true;    	    	  
    	var SPAN_Error = $(this).addClass("borderrojo");
    	
    	
    	
    	
    }
    	
    // TODO: do something with the value
});


if (!bError)

{
	 //return false;
	 doFunctionForYes();
	/*  $("#dialogo").html("¿Está seguro que quiere modificar  la regla?.");
	 $("#dialogo").dialog({
	      modal: true, title: 'Aviso', zIndex: 10000, autoOpen: true,
	      width: 'auto', resizable: false,
	      buttons: {
	          Si: function () {
	              doFunctionForYes();
	              $(this).dialog("close");
	          },
	          No: function () {
	              doFunctionForNo();
	              $(this).dialog("close");
	          }
	      },
	      close: function (event, ui) {
	          $(this).remove();
	      }
	});*/
}
else
{
	 $("#dialogo").html("No se puede continuar.Revise alguno de los datos introducidos en color rojo");
	 $("#dialogo").dialog({
      modal: true,title: 'Error',
      buttons: {
        Ok: function() {
          $( this ).dialog( "close" );
        }
      }
    });;


 }
 }
 </script>
 	
  
 <div class="borde_bloque"><span class="titulo_bloque"></span>
 <div class="admin">

 	
<%




Rule oRule = null;
Rule oRuleDATA = null; 
if (request.getAttribute("rule")!=null)
{
	oRule = (Rule) request.getAttribute("rule");
	
	oRuleDATA= RuleDAO.getRule(oRule.getRuleId().intValue());
}
else
	oRule = new Rule();


String _Error = "Operación guardada/cancelada correctamente";
boolean bError = false;

boolean _bModeUpdate= true;

if (request.getParameter("pos_id_action")!=null) {  // hay operacion

	
	if(request.getParameter("pos_id_action").equals("-1"))	
		_bModeUpdate =false;
	
    Long RuleID = Long.parseLong(request.getParameter("pos_id_action"));
	String Nombre = request.getParameter("nombre").toUpperCase();
	String Descripcion  =request.getParameter("description").toUpperCase();	
	Integer Activa = (request.getParameter("activa")!=null ? new Integer(1) : new Integer(0)) ;
	
	if (!bError)
	{	
		
		oRuleDATA.setActive(new Integer(Activa).intValue());		
		oRuleDATA.setName(Nombre);
		oRuleDATA.setDescription(Descripcion);
		
		
		if (_bModeUpdate)  // nueva
		{ 
				RuleDAO.updateRuleByRuleID(oRuleDATA);		    
		}
			%>
		<div class="f14px margintop60px tmarket flota_izq textleft negrita"><%=_Error%></div>
		<script>window.location.href = '<%=request.getContextPath() + "/jsp/rules.jsp"%>'</script>	
		
		<% 
		
	}
	else
	{
		%><div class="f14px margintop60px tmarket flota_izq textleft negrita"><%=_Error%>
		<input class="button" type="button" name="volver" value="Volver" onclick="window.history.back()"/></div>
	<% 	

	}

    
}
else

{


%>
	    	<div id="dialogo"></div>	
    		<div class="lista_acciones_trading"><img class="img_cab"  src="<%=request.getContextPath()%>/images/stock-market-financial-dice-trading-6150113.jpg">Rule</div>
    		<form name="f" id="f" action="admin_ficha_rule.jsp" method="post" >    		
    		<table  class="tmarket">    			
    		<tr>
    		<td>
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="nombre">Nombre*</label> <input class="position fondo_blanco mandatory flota_izq" type="text" id="nombre" name="nombre" value="<%=(oRuleDATA.getName()!=null ? oRuleDATA.getName() : "")%>">

    		</div>
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="symbol">Descripción* </label> <textarea rows="5" cols="50" class="position fondo_blanco mandatory flota_izq" id="symbol" name="description"><%=(oRuleDATA.getDescription() !=null ? oRuleDATA.getDescription() : "") %></textarea>
    		</div>
    		
    		<div>
    			<label class="flota_izq w200px textleft negrita" for="activa">Activa*</label> 
    			<% 
    				int _Activa = oRuleDATA.getActive();
    				String _txtActive = "";
    				if (_Activa==1)
    				{	
    					_txtActive = "checked";
    				}
    			%>
    			<input class="position fondo_blanco number flota_izq" type="checkbox"  id="activa" name="activa" <%=_txtActive%>>
    			 
    		</div>
    		    		    		
    		<input type="hidden" value="<%=oRuleDATA.getRuleId()%>" name="pos_id_action" id="pos_id_action" />
    		<input type="hidden" value="<%=request.getParameter("ruleidclass")%>" name="ruleidclass" id="ruleidclass" />
    		    		
    		<div class="flota_der">    			
    			<input  class="button" type="button" name="aceptar" value="Aceptar" onclick="go()"/>
    			<input class="button" type="button" name="volver" value="Volver" onclick="window.history.back()"/>
    		</div>
    		</td>
    		</tr>    		    		
    		</table>
    		</form>
    		    		
    			    		
 </div>
 </div>
 
 <script>
_KeepFloatNumbers();
_KeepIntegerNumbers();
</script>
<% }  %>
  
