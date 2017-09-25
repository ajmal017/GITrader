 <%@page import="com.tim.util.PositionStates" %>
 <%@page import="com.tim.dao.ConfigurationDAO" %>
 
 <%@page import="com.tim.util.ConfigKeys" %>
 
 
 <% 
 
 
 

 //boolean OpenOrdersOnly = 
 
 Long _SecsRefresh =  Long.parseLong((ConfigurationDAO.getConfiguration("GRID_SECONDS_REFRESH")).getValue());
 
 _SecsRefresh = _SecsRefresh /1000;

 
 
 %>
 
 
 <script>
 
 /* PRECIO * ACCION, NO CUENTA EL  MULTIPLICADOR  */
 
 function fn_SumaSalidasParciales(Operations)
 {
	 var aOperations;
	 var _Result=0;
	 
	 if (Operations!='')  // hay operaciones parciales 
	 {
		 if (Operations.indexOf(";")>0)	 { // hay operaciones parciales 
		 	
			 aOperations = Operations.split(";");
			 //(7|18:25|50|1);(7|18:25|50|1)) 
		 }
		 else
		 {
			 aOperations = new Array();
		     aOperations[0] = Operations;
		 } 
			 for (j=0;j<aOperations.length;j++)
		 	 {
			 // quitamos parentesis
			 
			 
			 	aData = aOperations[0].replace("(","").split("|");
				 //alert(aData[0] + "|" + aData[2]);
				 
				 _Result = _Result + (aData[0] * aData[2]);
			}
		 
				 
		  // 1 operacion
		 
	 }
	 else
		
		 _Result = 0; 
	 
	 
	 //alert(_Result);
	 return parseFloat(_Result);
	 
 }
 
 
 /* CAMBIAMOS EL COLOR ROJO O VERDE  y METEMOS LOS RESULTADOS GLOBALES*/
 
 var PositionSELL = '<%=PositionStates.statusTWSFire.SELL.toString()%>';
 var PositionBUY = '<%=PositionStates.statusTWSFire.BUY.toString() %>';
 
 
 var _STRFilled =  '<%=PositionStates.statusTWSCallBack.Filled %>';
 
 
 /* a partir de una cadena de operaciones, devolvemos el sumatorio de los precios x las acciones,
 supuestamente solo para las cerradas 
 Precio|Fecha|Acciones|..
 
 */

 
 
 function GridColor()
 {
	 var PositiveBalance =0;
     var NegativeBalance=0;
     var TotalMoneyBuy = 0;
     
     var _FirstFeed ="";
     var _LastFeed ="";
     
     var debug = (window.location.href.indexOf("debug")>0);
 
 

     $('th[abbr="balance"]').addClass("color_resultado");
     
     
     // filtro de abiertas 
     var OpenPositionsOnly  = false;
     var _StringOpenFilter = '<%=ConfigKeys.FILTER_CONSOLA_OPEN.toLowerCase() %>';
     
     
     // ocultamos cabecera de salida y data..ojo, la cabecera es fija siempre
     if ($("#filterps").val().toLowerCase() == '<%=ConfigKeys.FILTER_CONSOLA_OPEN.toLowerCase() %>')
     {	 
    	 OpenPositionsOnly = true;
    	 $('th[abbr="precios"]').removeClass("visible");
    	 $('th[abbr="precios"]').addClass("oculto");
     }
     else
     {	 
    	 $('th[abbr="precios"]').addClass("visible");
    	 $('th[abbr="precios"]').removeClass("oculto");
 	 }
     
     
     
     
     
	 $('#flex1 tr').each( function()
	 { 
		 	
		
		 	var cellLastTick = $('td[abbr="ulttick"] >div', this); 
	        var cellPrecioCompra = $('td[abbr="precioe"] >div', this);
	        var cellPrecioV = $('td[abbr="precios"] >div', this);
	        var cellDateV =$('td[abbr="fechas"] >div', this);
	        var cellBalance = $('td[abbr="balance"] >div', this);
	        var cellAcciones = $('td[abbr="acciones"] >div', this);
	        var cellTIPOPOS = $('td[abbr="tipoc"] >div', this);  // compra o corto
	        var cellEstadoE = $('td[abbr="estadoe"] >div', this);  // compra o corto
	        var cellEstadoS= $('td[abbr="estados"] >div', this);  // compra o corto
	        
	        var cellStopP= $('td[abbr="stopp"] >div', this);  // compra o corto
	        var cellStopB= $('td[abbr="stopb"] >div', this);  // compra o corto
	        
	        
	        	        
	        var cellLastFeed= $('td[abbr="lf"] >div', this);  
	        var cellFirstFeed= $('td[abbr="ff"] >div', this);
	        
	        
	        var cellOperacionesVentaParciales= $('td[abbr="parcialOperations"] >div', this);
	        /* las operaciones de venta parciales pueden suponer que la posicion este abierta (se computa el
	        balance sobre el tiempo real de las pendientes) o cerrada (ya sobre los precios de cierre
	        		(7|18:25|50|1);(7|18:25|50|1))
	        		(Precio|Fecha|Acciones|..)
	        
	        */
	        var cellMultiplier = $('td[abbr="multiplier"] >div', this);
	        
	        // venta pendiente
	        //class="CC0000";   // 33D724 verde 
	        classOp="";
	        var balance =0;
	        
	        var _PriceLost = 0;
	        var _PriceProfit = 0;
	        
	        _FirstFeed = cellFirstFeed.text();
	        
	        _LastFeed =  cellLastFeed.text();
	        
	        
	        // ocultamos salida
	        if (OpenPositionsOnly)
	        {	
	        	cellPrecioV.addClass("oculto");
	        }	
	        
	        //alert("Compra: " + cellPrecioCompra.text() + " Acciones:" + cellAcciones.text()); 
	        
	        // quitamos las pending submit con el precio mayor de 0
	        if (parseFloat(cellPrecioCompra.text())>0)
	        {	
	        	/* if (cellTIPOPOS.text()==PositionBUY)  // largo
	        	{
	        			_PriceLost = parseFloat(cellPrecioCompra.text()) - (parseFloat(cellPrecioCompra.text()) * parseFloat(cellStopP.text()) / 100);
		        		_PriceProfit = parseFloat(cellPrecioCompra.text()) + (parseFloat(cellPrecioCompra.text()) * parseFloat(cellStopB.text())/ 100);	
	        	}
	        	else  // corto
	        	{
	        		_PriceLost = parseFloat(cellPrecioCompra.text()) + (parseFloat(cellPrecioCompra.text()) * parseFloat(cellStopP.text()) / 100);
		        	_PriceProfit = parseFloat(cellPrecioCompra.text()) - (parseFloat(cellPrecioCompra.text()) * parseFloat(cellStopB.text())/ 100);
	        		
	        	}*/
	        		
	        	
	        	TotalMoneyBuy+= (cellPrecioCompra.text() *cellAcciones.text()* cellMultiplier.text());	        
	        }
	        
	        /* calculamos los precios en base a los stop lost 
	        cellPPROFIT.text(_PriceProfit.toFixed(2));
	        cellPLOST.text(_PriceLost.toFixed(2));
	        */
 			if (($.trim(cellEstadoE)!='' && cellEstadoE.text()!=_STRFilled) || ($.trim(cellEstadoS.text())!='' && cellEstadoS.text()!=_STRFilled))   // PENDIENTE
 			{
 				classOp  = "pending";
 				
 			}
 			else if ($.trim(cellLastTick.text())!="")
	        {		 
 				if (cellTIPOPOS.text()==PositionBUY)  // compras
 					classOp="verde";  // verde
 				else
 					classOp="rojo";  // rojo
 				
	        	
	        	if ($.trim(cellDateV.text())=="")   // está. la compra solo
	        	{	
	        		
	        		// metalico
	        		
	        		
	        		balance = Math.abs(parseFloat(cellLastTick.text()) - parseFloat(cellPrecioCompra.text())) * parseFloat(cellAcciones.text()) * parseFloat(cellMultiplier.text());
	        		if (debug)
	        		{
	        			alert("balance|LastTick|Compra|Pos:" + balance + "|" +parseFloat(cellLastTick.text()) + "|" + cellPrecioCompra.text() + "|" + cellTIPOPOS.text()  + "|" + balance);
	        		}	
	        		if ($.trim(cellPrecioCompra.text())!="" && 
	        				(parseFloat(cellLastTick.text())>parseFloat(cellPrecioCompra.text()) && cellTIPOPOS.text()==PositionBUY)
	        				|| 
	        				(parseFloat(cellLastTick.text())<parseFloat(cellPrecioCompra.text()) && cellTIPOPOS.text()==PositionSELL)	        					        		
	        				)
	        			
	        		{		        			
	        			//classOp="verde";  // verde
	        			PositiveBalance+=balance;
	        			//alert("Positive Balance : " + PositiveBalance);
	        			
	        		}	
	        		if ($.trim(cellPrecioCompra.text())!="" && 
	        				(parseFloat(cellLastTick.text())<parseFloat(cellPrecioCompra.text()) && cellTIPOPOS.text()==PositionBUY)
	        				|| 
	        				(parseFloat(cellLastTick.text())>parseFloat(cellPrecioCompra.text()) && cellTIPOPOS.text()==PositionSELL)	        					        		
	        				)
	        		{	
	        			//classOp="rojo";   // rojo
	        			balance = -balance;
	        			NegativeBalance+=balance;
	        		}
	        		
	        	}  // compra y venta
	        	else
	        	{
	        		
	        		// metalico
	        		var _CantidadEntrada;
	        		var _CantidadSalida;
	        		
	        		_CantidadEntrada = parseFloat(cellPrecioCompra.text());
	        		_CantidadSalida = parseFloat(cellPrecioV.text());
	        		
	        		
	        		
	        		if ($.trim(cellOperacionesVentaParciales.text())!='') // hay parciales en una cerrada
	        		{
	        		
	        			
	        			//_CantidadEntrada = parseFloat(cellPrecioCompra.text()) * parseFloat(cellAcciones.text()) * parseFloat(cellMultiplier.text());
	        			_CantidadEntrada = parseFloat(cellPrecioCompra.text()) * parseFloat(cellAcciones.text());
		        		_CantidadSalida = fn_SumaSalidasParciales(cellOperacionesVentaParciales.text());
		        		//alert(_CantidadEntrada + "|" + _CantidadSalida);
		        		balance = Math.abs(_CantidadEntrada -_CantidadSalida) * parseFloat(cellMultiplier.text());
		        		
		        		
	        		}	        		
	        		else
	        		{	
	        			balance = Math.abs(_CantidadEntrada -_CantidadSalida) * parseFloat(cellAcciones.text()) * parseFloat(cellMultiplier.text());
	        		 
	        		}
	        		//alert(_CantidadEntrada  | "," | _CantidadSalida |"," |  balance );
	        		if (debug)
	        		{
	        			alert(_CantidadEntrada + "," + _CantidadSalida + "," +Math.abs(_CantidadEntrada -_CantidadSalida));
	        			alert("jhhhhhhhhhhh,PEntrada:" +  parseFloat(cellPrecioCompra.text()) + ", PSalida:" + parseFloat(cellPrecioV.text()) + ",Acciones" + cellAcciones.text() + ",Multi:" + cellMultiplier.text() + ",balance:" + balance);
	        		}	
	        		
	        		if ((_CantidadEntrada>_CantidadSalida  && cellTIPOPOS.text()==PositionBUY)
	        			||  (_CantidadEntrada<_CantidadSalida  && cellTIPOPOS.text()==PositionSELL))
	        		{		        		
	        			//classOp="rojo";
	        			balance = -balance;
	        			NegativeBalance+=balance;
	        		}
	        		else	        
	        		{	
	        			//classOp="verde";
	        		    PositiveBalance+=balance;
	        		
	        		}	
	        	}		        	
	        	
	        
	        
	        var _ResultadoOperacion = parseInt(balance,10);
	        
	        cellBalance.text(_ResultadoOperacion);  // redoddeamos a dos    
	      //  alert(cellLastTick.text() + "|" + cellPrecioCompra.text() + "|" + cellPrecioV.text() );
 			}
 			 $(this).addClass (classOp);
 			 /* COLUMNA RESULTADO POR COLOR, FILA POR TIPO DE OPERACION CORTO LARGO */
 			 if (_ResultadoOperacion!=0)
 			 { 
 			 if (_ResultadoOperacion>0)
 				cellBalance.addClass("verde");
 			 else
 				cellBalance.addClass("rojo");
 			 } 			
	      
	    }); 
	 	  
 	
	
	
	var BalanceTotal;
	if (PositiveBalance > Math.abs (NegativeBalance))			
		BalanceTotal = PositiveBalance - Math.abs (NegativeBalance);
	else
		BalanceTotal = PositiveBalance + NegativeBalance;
	
	if (debug)
		alert(PositiveBalance + "|" + NegativeBalance + "|" + BalanceTotal);
	
	$('#res_positivas').text(PositiveBalance.toFixed(0));
 	$('#res_negativas').text(Math.abs(NegativeBalance.toFixed(0)));
 	
 	var _classBalance = "negrita colorrojo"; 	
 	$('#res_neto').text(Math.abs(BalanceTotal).toFixed(0)); 	
 	if (BalanceTotal>0)   // POSITIVO?
 	{		
 		_classBalance = "negrita colorverde"; 		 		
 	}
 	$('#res_invertido').text(TotalMoneyBuy.toFixed(0));
 	
 	
 	$('#res_neto').removeClass();
 	$('#neto').removeClass();
 	
 	$('#res_neto').addClass(_classBalance);
	$('#neto').addClass(_classBalance);
	
	
	
 	 
 }


 
 
 $(document).ready(function() {
	 PositionCall2();
	 
	 setInterval( "refreshAjax();", <%=(ConfigurationDAO.getConfiguration("GRID_SECONDS_REFRESH")).getValue()%>);   ///////// 10 seconds
	 $('#flex1 tr').live('dblclick', function(){
		 	var cellPosition = $('td[abbr="posid"] >div', $(this)).text(); 
	        window.location.href="position.jsp?positionid=" + cellPosition;
	        
	});
	 _getFeeds();
	 
	 
	});
  
 $(function() { 
 refreshAjax = function(){
	 	$("#flex1").flexReload(); 	 
  }
 }); 
 
 function addFormData()
 {	 	  
	  var dt = $('#frmfilterps').serializeArray();
      $("#flex1").flexOptions({params: dt});
 	
 	$("#console_last_update").html("<span class=colornegro>Console Update:&nbsp;</span>" + _TimeinHHMMSS());
 	_getFeeds();
      return true;
	 
 }
 
 function _getFeeds()
 {
	 $.post("ajax/get_feeds.jsp", 
			  function(data) {
					// cambiamos la imagen										
				
				  /* FECHAS DE FEEDS */
				    aData = data.split("|");
					$("#first_feed_ticket").html("<span class=colornegro>First Feed:&nbsp;</span> " + aData[0] );
					$("#last_feed_ticket").html("<span class=colornegro>Last Feed:&nbsp;</span> " +  aData[1]);
					
			  });
 }
 
 function leadingZero(val)
 {
     var str = val.toString();
     if(str.length == 1)
     {
         str = '0' + str;
     }

     return str;
 }
 
 function _TimeinHHMMSS()
 {
	 var today = new Date();
     return this.leadingZero(today.getHours()) +':'+ this.leadingZero(today.getMinutes()) +':'+ this.leadingZero(today.getSeconds());
 }
 
 function PositionCall2()
 {
	 
     $("#console_last_update").text("Last Data: " + _TimeinHHMMSS());
     
	
 	$("#flex1").flexigrid({
	 	url: "ajax/get_positions.jsp",
		dataType: 'xml',
		colModel : [
			{display: 'Entrada', name : 'fechae', width : 45, sortable : true, align: 'center'},
			{display: 'Ticker', name : 'ticker', width : 60, sortable : true, align: 'center'},			
			{display: 'Salida ', name : 'fechas', width : 90, sortable : true, align: 'center',hide:true},
			{display: 'Tipo', name : 'tipoc', width : 45, sortable : true, align: 'center'},
			{display: 'Acciones', name : 'acciones', width : 70, sortable : true, align: 'center'},
			{display: 'P. Entrada', name : 'precioe', width : 55, sortable : true, align: 'center'},			
			{display: 'P. Salida', name : 'precios', width : 45, sortable : true, align: 'center'},
			{display: 'Last', name : 'ulttick', width : 40, sortable : true, align: 'center'},
			{display: 'Resultado', name : 'balance', width : 60, sortable : true, align: 'center'},
			{display: 'Stop Lost', name : 'pstopp', width : 65, sortable : true, align: 'center'},						
			{display: 'Stop Profit', name : 'pstopb', width : 70, sortable : true, align: 'center'},					
			{display: 'Str E', name : 'stratge', width : 0, sortable : true, align: 'center', hide:true},
			{display: 'Str S', name : 'stratgs', width : 0, sortable : true, align: 'center',hide:true},
			{display: 'PositionID', name : 'posid', width : 0, sortable : true, align: 'center', hide:true},
			{display: 'EstadoE', name : 'estadoe', width : 0, sortable : true, align: 'center', hide:true},
			{display: 'EstadoS', name : 'estados', width : 0, sortable : true, align: 'center', hide:true},
		    {display: 'OperacionesParciales', name : 'parcialOperations', width : 0, sortable : true, align: 'center', hide:true},
			{display: 'Multiplicador', name : 'multiplier', width : 0, sortable : true, align: 'center', hide:true}
		    
			
			/*{display: 'FirstFeed', name : 'ff', width : 0, sortable : true, align: 'center', hide:true},
			{display: 'LastFeed', name : 'lf', width : 0, sortable : true, align: 'center', hide:true} */
			],
		/* buttons : [
			{name: 'Add', bclass: 'add', onpress : test},
			{name: 'Delete', bclass: 'delete', onpress : test},
			{separator: true}
			],
		searchitems : [
			{display: 'Ticker', name : 'ticker'},
			{display: 'Fecha Compra', name : 'fechae'},
			{display: 'Fecha Venta', name : 'fechas'}			
			],*/
		/* sortname: "fechae",
		sortorder: "desc", */
		 usepager: true,
		//title: 'Posiciones [Compras o Ventas diarias pendiente o ejecutadas. Refresco de <%=_SecsRefresh %> seg ]',
		useRp: true,
		rp: 15,
		showTableToggleBtn: 	false,	
		onSuccess: GridColor,
		height: 350,
        onSubmit: addFormData,

	});   
 
 }

 </script>
 <div class="borde_bloque"><span class="titulo_bloque"></span>
 <div id="flex1"></div>
 <!--  <ul id="contextMenu" class="context-menu">
  <li class="custom-class1"><a href="#">Context Menu Item 1</a></li>
  <li class="custom-class2"><a href="#">Second menu item</a></li>
</ul> -->
 </div>
  
