 function doFunctionForYes_Strategies(forMID)
 {
 
 // con jquery y dialog da pete
 // validamos valores númericos
	 
	 document.forms[forMID].submit();

 
 }
 function doFunctionForNo_Strategies()
 {
 	//window.location.reload();
 }
 
 
 
 function go_St(formsID)
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
	 $("#dialogo").html("¿Está seguro que quiere modificar  la estrategia?.");
	 $("#dialogo").dialog({
	      modal: true, title: 'Aviso', zIndex: 10000, autoOpen: true,
	      width: 'auto', resizable: false,
	      buttons: {
	          Si: function () {
	              doFunctionForYes_Strategies(formsID);
	              $(this).dialog("close");
	          },
	          No: function () {
	              doFunctionForNo_Strategies();
	              $(this).dialog("close");
	          }
	      },
	      close: function (event, ui) {
	          $(this).remove();
	      }
	});
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