
<!--  FLEXIGRID NO VA CON LA 1.9.0 DE JQUERY -->
<script src="<%=request.getContextPath()%>/js/jquery-1.8.3.js"></script>
<!--  <link  rel="stylesheet" type="text/css"  href="<%=request.getContextPath()%>/css/main.css"></link> --> 
<script src="<%=request.getContextPath()%>/js/highstock.js"></script> 
<div id="container"   style="height: 500px; min-width: 500px"></div><!--  style="width:800px; height:600px;" -->

<%

String _STOCKName = request.getParameter("stockname");
String _STOCKID = request.getParameter("stockid");
String _STOCKMarketId = request.getParameter("mktid");


%>

	
	<script>
	
	$(function() {
		$.getJSON('<%=request.getContextPath()%>/jsp/ajax/get_candle_chart.jsp?stockid=<%=_STOCKID%>&stockmktid=<%=_STOCKMarketId%>', function(data) {

			// create the chart
			$('#container').highcharts('StockChart', {
				

				title: {
					text: 'AAPL stock price by minute'
				},
				
				rangeSelector : {
					buttons : [{
						type : 'hour',
						count : 1,
						text : '1h'
					}, {
						type : 'day',
						count : 1,
						text : '1D'
					}, {
						type : 'all',
						count : 1,
						text : 'All'
					}],
					selected : 1,
					inputEnabled : false
				},
				
				series : [{
					name : 'AAPL',
					type: 'candlestick',
					data : data,
					tooltip: {
						valueDecimals: 2
					}
				}]
			});
		});
	});
	

	 	
	
	
		</script>
