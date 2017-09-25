function _KeepFloatNumbers()
{


	$(".number").keydown(function(event) {
		// Allow only backspace and delete
		
		if ( event.keyCode == 46 || event.keyCode == 8 ) {
			// let it happen, don't do anything
		}
		else {
			// Ensure that it is a number and stop the keypress
			 if ( !(event.keyCode == 8 || event.keyCode == 9 ||  event.keyCode == 190 ||  event.keyCode == 110                               // backspace  
				        || event.keyCode == 46                              // delete
				        || (event.keyCode >= 35 && event.keyCode <= 40)     // arrow keys/home/end
				        || (event.keyCode >= 48 && event.keyCode <= 57)     // numbers on keyboard
				        || (event.keyCode >= 96 && event.keyCode <= 105))   // number on keypad
				        ) {
				            event.preventDefault();     // Prevent character input
				    }
		}
	});
}




function _KeepIntegerNumbers()
{
	
	$(".entero").keydown(function(event) {
		// Allow only backspace and delete
		if ( event.keyCode == 46 || event.keyCode == 8 ) {
			// let it happen, don't do anything
		}
		else {
			// Ensure that it is a number and stop the keypress
			 if ( !(event.keyCode == 8 || event.keyCode == 9                                // backspace 
				        || event.keyCode == 46                              // delete
				        || (event.keyCode >= 35 && event.keyCode <= 40)     // arrow keys/home/end
				        || (event.keyCode >= 48 && event.keyCode <= 57)     // numbers on keyboard
				        || (event.keyCode >= 96 && event.keyCode <= 105))   // number on keypad
				        ) {
				            event.preventDefault();     // Prevent character input
				    }
		}
	});
}

function _KeepIncrementInput(Id, MinValue)
{
	
		$( "#" + Id).spinner({
      step: 0.01,
      numberFormat: "C",
    	  min : MinValue
		});      
}
function _KeepOnChangeValueFromStopProfit(Operation, PrecioEntrada, ProfitPercentId, ProfitValueId)
{
	/* $( "#" + ProfitPercentId).spinner({
		stop: function( event, ui ) {_OnChangeProfitPercent(Operation,PrecioEntrada,ProfitPercentId, ProfitValueId)}
		});		
	$( "#" + ProfitPercentId).change(function() {		
		_OnChangeProfitPercent(Operation,PrecioEntrada,ProfitPercentId, ProfitValueId);
	});*/
	$( "#" + ProfitValueId).spinner({
		stop: function( event, ui ) {_OnChangeProfitValue(Operation,PrecioEntrada,ProfitPercentId, ProfitValueId)}
		});	
	$( "#" + ProfitValueId).change(function() {
		_OnChangeProfitValue(Operation,PrecioEntrada,ProfitPercentId, ProfitValueId);
	});
}
function _KeepOnChangeValueFromStopLost(Operation, PrecioEntrada, LostPercentId, LostValueId)
{
/* 	$( "#" + LostPercentId).spinner({
		stop: function( event, ui ) {_OnChangeStopLostPercent(Operation,PrecioEntrada,LostPercentId, LostValueId)}
		});		
	$( "#" + LostPercentId).change(function() {		
		_OnChangeStopLostPercent(Operation,PrecioEntrada,LostPercentId, LostValueId);
	}); */
	$( "#" + LostValueId).spinner({
		stop: function( event, ui ) {_OnChangeStopLostValue(Operation,PrecioEntrada,LostPercentId, LostValueId)}
		});	
	$( "#" + LostValueId).change(function() {
		_OnChangeStopLostValue(Operation,PrecioEntrada,LostPercentId, LostValueId);
	});
}

function _OnChangeProfitPercent(Operation, Price, ProfitPercentId, ProfitValueId)
{ 	
	
	if (Operation=="BUY")
	{	
		
		Value = parseFloat(Price) +   parseFloat(Price * $( "#" + ProfitPercentId).val() / 100);
		
		$( "#" + ProfitValueId).val(Value.toFixed(2));
		//$( "#" + ProfitPercentId).val( ($( "#" + ProfitValueId).val() -  Price) * 100 / Price );
	}	
	else
	{	
		Value = parseFloat(Price) -   parseFloat(Price * $( "#" + ProfitPercentId).val() / 100);
		$( "#" + ProfitValueId).val(Value.toFixed(2));		
	}
}
function _OnChangeProfitValue(Operation, Price, StopLostPercentId, StopLostValueId)
{ 	
	
	if (Operation=="BUY")
	{	
		
		Value = ($( "#" + StopLostValueId).val() -  parseFloat(Price)) / parseFloat(Price) *100;		
		$( "#" + StopLostPercentId).val(Value.toFixed(2));
		
	}	
	else
	{	
		//alert(Price + "|" + parseFloat(Price * $( "#" + StopLostPercentId).val() / 100));
		Value = (parseFloat(Price) - $( "#" + StopLostValueId).val()) / parseFloat(Price) * 100;		
		$( "#" + StopLostPercentId).val(Value.toFixed(2));		
	}
}

function _OnChangeStopLostPercent(Operation, Price, LostPercentId, LostValueId)
{ 	
	  
	if (Operation=="BUY")
	{	
		Value = parseFloat(Price) -   parseFloat(Price * $( "#" + LostPercentId).val() / 100);		
		$( "#" + LostValueId).val(Value.toFixed(2));
		//$( "#" + ProfitPercentId).val( ($( "#" + ProfitValueId).val() -  Price) * 100 / Price );
	}	
	else
	{	
		//alert(Price + "|" + parseFloat(Price * $( "#" + StopLostPercentId).val() / 100));
		Value = parseFloat(Price) +   parseFloat(Price * $( "#" + LostPercentId).val() / 100);
		$( "#" + LostValueId).val(Value.toFixed(2));		
	}
}
function _OnChangeStopLostValue(Operation, Price, StopLostPercentId, StopLostValueId)
{ 	
	 
	if (Operation=="BUY")
	{	
		Value = (parseFloat(Price) - $( "#" + StopLostValueId).val()) / parseFloat(Price) *100;		
		$( "#" + StopLostPercentId).val(Value.toFixed(2));
		
	}	
	else
	{	
		//alert(Price + "|" + parseFloat(Price * $( "#" + StopLostPercentId).val() / 100));
		Value = ($( "#" + StopLostValueId).val()-parseFloat(Price)) / parseFloat(Price) * 100;
		//alert(Value);
		$( "#" + StopLostPercentId).val(Value.toFixed(2));		
	}
}

function _KeepFuturesParams(TypeSecurityFutures)
{
	var _SecSelected = $("#type option:selected");
	
	if (_SecSelected!='' && _SecSelected.length>0)
	{
		
		
		if (_SecSelected.val()!=TypeSecurityFutures)
		{
			//$("#futuros").css("display", "none");
			$("#futuros").fadeOut(500);
			/* ponemos obligatorios o no */			
			$("#futuros #expiry_date").removeClass("mandatory");
			$("#futuros #multiplier").removeClass("mandatory");
			$("#futuros #multiplier").val("");
			
		}	
		else
		{
			//$("#futuros").css("display", "block");
			$("#futuros").fadeIn(500);			
			$("#futuros #expiry_date").addClass("mandatory");	
			$("#futuros #multiplier").addClass("mandatory");
		}
			
		
	}	
		
}



