<div>
<ul id="dropdown" class="menu_es" >
<li><a href="<%=request.getContextPath()%>/jsp/inicio.jsp"><b>Inicio</b></a></li>
<li><a href=""><b>Informes</b></a></li>
<li><a href=""><b>Opciones</b></a>
	<ul class="menu2">
		<li><a href="<%=request.getContextPath()%>/jsp/admin.jsp"><b>Mercados</b></a></li>
		<li><a href="<%=request.getContextPath()%>/jsp/close_positions.jsp"><b>Cierres</b></a></li>
		<li><a href="<%=request.getContextPath()%>/jsp/estrategias.jsp"><b>Estrategias</b></a></li>
		<li><a href="<%=request.getContextPath()%>/jsp/rules.jsp"><b>Reglas</b></a></li>
	</ul>
</li>
<!--   <li><a href="<%=request.getContextPath()%>/jsp/sistema.jsp"><b>Sistema</b></a></li> -->
</ul>


<script>


$(document).ready(function() {
    
	// Setup the nav drop-downs
	
	
	$(function(){
			$("#dropdown").juizDropDownMenu({
				'showEffect' : 'fade',
				'hideEffect' : 'slide'
			});
		});
	

    //#("'#menu_es li").find("ul").css("display","block");
	

	
	
});

 </script>
	
 
 
</div>