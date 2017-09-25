<%@ page language="java" contentType="text/html; charset=ISO-8859-1"
    pageEncoding="ISO-8859-1"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=ISO-8859-1">
<title>Sistema TraderInteractiveModel</title>
<script src="<%=request.getContextPath()%>/js/jquery-1.8.3.js"></script>
<script src="<%=request.getContextPath()%>/js/juizDropDownMenu-1.5.min.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery-ui-1.10.0.custom.js"></script>
<script src="<%=request.getContextPath()%>/js/jquery-ui-multiselect/jquery.multiselect.js"></script>
<link  rel="stylesheet" type="text/css"  href="<%=request.getContextPath()%>/css/juizDropDownMenu.css"></link>
<link  rel="stylesheet" type="text/css"  href="<%=request.getContextPath()%>/css/start/jquery-ui-1.10.0.custom.min.css"></link>
<link  rel="stylesheet" type="text/css"  href="<%=request.getContextPath()%>/css/main.css"></link>
<link  rel="stylesheet" type="text/css"  href="<%=request.getContextPath()%>/js/jquery-ui-multiselect/jquery.multiselect.css"></link>

</head>
<body>
<div id="wrapper">
 <div class="top"><jsp:include page="common/top.jsp"/></div>
 <div id="content">
 	<div class="navigation"><jsp:include page="common/navigation.jsp"/></div>
 	<div class="column11"><jsp:include page="common/left.jsp"/></div>
	<div class="column12">		
	<jsp:include page="backtesting/simulation.jsp"/>
	</div>
	
	<div class="column13"><jsp:include page="common/right.jsp"/></div>
</div>
<div class="footer"><jsp:include page="common/bottom.jsp"/></div>
</div>
</body>
</html>