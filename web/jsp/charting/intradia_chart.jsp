<%@page import="com.tim.util.ConfigKeys"%>
<%@page import="com.tim.dao.StrategyDAO"%>
<%@page import="com.tim.model.Strategy"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="java.util.Calendar"%>
<%@page import="com.tim.dao.RealTimeDAO"%>
<%@page import="java.text.DecimalFormat"%>
<%@page import="java.util.List"%> 
<%@page import="com.tim.dao.MarketDAO"%>
<%@page import="com.tim.dao.MarketShareDAO"%>
<%@page import="com.tim.dao.OrderDAO"%>
<%@page import="com.tim.dao.PositionDAO"%>
<%@page import="com.tim.dao.ShareDAO"%>
<%@page import="com.tim.dao.TradingMarketDAO"%>
<%@page import="com.tim.dao.MarketDAO"%>
<%@page import="com.tim.model.Market"%>
<%@page import="com.tim.model.Position"%>
<%@page import="com.tim.model.RealTime"%>
<%@page import="com.tim.model.Share"%> 
<%@page import="com.tim.model.Trading"%>
<%@page import="com.tim.model.Trading_Market"%>
<%@page import="com.tim.util.Utilidades"%>


<!--  FLEXIGRID NO VA CON LA 1.9.0 DE JQUERY -->
<script src="<%=request.getContextPath()%>/js/jquery-1.8.3.js"></script>
<!--  <link  rel="stylesheet" type="text/css"  href="<%=request.getContextPath()%>/css/main.css"></link> --> 
<script src="<%=request.getContextPath()%>/js/highstock.js"></script> 
<div id="container"   style="height: 500px; min-width: 500px"></div><!--  style="width:800px; height:600px;" -->

<%

String _STOCKName = request.getParameter("stockname");
String _STOCKID = request.getParameter("stockid");
String _STOCKMarketId = request.getParameter("mktid");


Share oShare= ShareDAO.getShare(Long.parseLong(_STOCKID));
Market oMarket = MarketDAO.getMarket(Long.parseLong(_STOCKMarketId));

String HoraInicioLecturaMaxMin = Utilidades.getActualHourFormatPlusMinutes(oMarket.getStart_hour(), oShare.getOffset1min_read_from_initmarket());
String HoraFinLecturaMaxMin = Utilidades.getActualHourFormatPlusMinutes(oMarket.getStart_hour(), oShare.getOffset2min_read_from_initmarket());

Calendar DateMinMaxFrom =Utilidades.getNewCalendarWithHour(HoraInicioLecturaMaxMin);
Calendar DateMinMaxTo =Utilidades.getNewCalendarWithHour(HoraFinLecturaMaxMin);


RealTime _ActualRealTime = RealTimeDAO.getMinMaxValue_DatesRealTimeBetweenDates(oShare.getShareId().intValue(), new java.sql.Timestamp(DateMinMaxFrom.getTimeInMillis()),new java.sql.Timestamp(DateMinMaxTo.getTimeInMillis()));

String _UpperRange="";
String _LowerRange="";


if (_ActualRealTime!=null )
	if (_ActualRealTime.getMin_value()!=null)
		_LowerRange = _ActualRealTime.getMin_value().toString()  ;
	if (_ActualRealTime.getMax_value()!=null)
		_UpperRange = _ActualRealTime.getMax_value().toString() ;
%>

	
	<script>
	
	

	 $(document).ready(function() {

	    var options = {
	          chart: {
	            renderTo: 'container',
	            //type: 'StockChart'
	        },
			global: {
				useUTC: false 
			}, 
	        events: {
                load: function(){
                    this.hideLoading(); 
                } 
            },			
	    	title: {
				text: '<%=_STOCKName%> stock price'
			},
			
			yAxis : {
				title : {
					text : 'Exchange / Range Price'
				},
				plotLines : [{
					value : <%=_LowerRange %>,
					color : 'orange',
					dashStyle : 'Solid',
					width : 1,
					label : {
						text : '          Lower Range (<%=_LowerRange%>)',
						align: 'right',
		                x: -10
						
					}
				}, {
					value : <%=_UpperRange%>,
					color : 'red',
					dashStyle : 'Solid',
					width : 1,
					label : {
						text : '           Upper Range (<%=_UpperRange%>)',
						align: 'right',
		                x: -10
					}
				}]
			},

			
			xAxis: { 
				gapGridLineWidth: 0,
				type : 'datetime'
			},
			
			/* rangeSelector : {
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
			},*/
	        
			series : [{
				name : '<%=_STOCKName%>',
				marker : {
					enabled : true,
					radius : 2
				},
				/*type: 'area',
				 marker : {
					enabled : true,
					radius : 3 
				},*/
				 data : [],
				 id : 'dataseries', 
				//gapSize: 5,
				tooltip: {
					valueDecimals: 2
				}
				
				
			}
			 /* ,{
			type : 'flags',
			data : [{
				x : Date.UTC(2013, 12, 25),
				title : 'A',
				text : 'Shape: "squarepin"'
			}, {
				x : Date.UTC(2013, 12, 25),
				title : 'A',
				text : 'Shape: "squarepin"'
			}],
			onSeries : 'dataseries',
			shape : 'squarepin',
			width : 16
		}*/]
	    };
 
		Highcharts.setOptions({
			global: {
				useUTC: false
			}
		});
 
	    $.getJSON('<%=request.getContextPath()%>/jsp/ajax/get_intradia_chart.jsp?stockid=<%=_STOCKID%>&stockmktid=<%=_STOCKMarketId%>', function(data) {
	        options.series[0].data = data; 
			//chartName.showLoading();
	        var chart = new Highcharts.StockChart(options);
	        //chart.hideLoading();  
	    });

	});
	
	
	
		</script>
