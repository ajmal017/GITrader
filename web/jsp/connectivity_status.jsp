<script>
function _getPlatformStatus()
 {
	 $.post("ajax/tws_conectivity_check.jsp", 
			  function(data) {
					// cambiamos la imagen										
				
				  /* FECHAS DE FEEDS */
				 
				  if (data.indexOf("NOOK")>=0)
				  {
					  aData = data.split("|");
					  
					  $("#platform_content_error").html(aData[1]);
					  //$("#platform_content_error").show(2000);
					  $("#platform_content_error").fadeIn();
					  
					  
				  }	  
				  else
				  {
					  //$("#platform_content_error").hide(2000);					  
					  $("#platform_content_error").fadeOut();
					  
					
				  }

					
			  });
 }

$(document).ready(function() {
	 
	 setInterval( "_getPlatformStatus();", 5000);   ///////// 10 seconds	 
	});

</script>

<div id="platform_content_error" class="oculto platform_message_error"></div>
 