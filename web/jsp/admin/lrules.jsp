<%@page import="com.tim.dao.RuleDAO"%>
<%@page import="com.tim.model.Rule"%>
<%@page import="com.tim.dao.StrategyDAO"%>
<%@page import="java.util.List"%> 
<%@page import="java.util.ArrayList"%>
<%@page import="com.tim.dao.MarketDAO"%>
<%@page import="com.tim.dao.MarketShareDAO"%>
<%@page import="com.tim.dao.OrderDAO"%>
<%@page import="com.tim.dao.PositionDAO"%>
<%@page import="com.tim.dao.ShareDAO"%>
<%@page import="com.tim.dao.TradingMarketDAO"%>
<%@page import="com.tim.model.Market"%>
<%@page import="com.tim.model.Position"%>
<%@page import="com.tim.model.Share"%>
<%@page import="com.tim.model.Trading"%>
<%@page import="com.tim.model.Trading_Market"%>
 
 
 
 <div class="borde_bloque"><span class="titulo_bloque"></span>
 <div class="admin">
 	
<%

java.util.List<Rule> lRules = RuleDAO.getListRules();

if (lRules!=null)
{	
	if (lRules!=null)
	{
			/* recorremos mercados */ 
	    	
			%>
	    		
    		<div class="lista_acciones_trading"><img class="img_cab"  src="<%=request.getContextPath()%>/images/stock-market-financial-dice-trading-6150113.jpg">Reglas</div>
    		<table class="tmarket" cellpadding="5" cellspacing="5">
    		<tr class="header">
    		<td>Regla</td>
    		<td>Descripción</td>    		
    		<td>Tipo</td>    		
    		
    		</tr>
	    	<%	 
			
	    	for (int j=0;j<lRules.size();j++)
	    	{
	    		
	    		Rule oRule = lRules.get(j);
	    		
	    		
	    		
	    	%>
	    	
				<tr class="data rowmarket">
		    		<td><a href="<%=request.getContextPath()%>/jsp/admin_ficha_rule.jsp?ruleidclass=<%=oRule.getClassName()%>"><%=oRule.getName() %></a></td>
		    		<td class=ancho400px><%=oRule.getDescription()  %></td>
		    		
		    		<td><%=(oRule.getType()!=null ? oRule.getType() : "") %></td>		    		
		    	</tr>		   
		     </a>
					
	    		
	    <%

	   
			}// FIN DE IF
			%>
			
			   </table>	
			   <% 
	}

} // fin de trading para hoy
%>


    		
	    		
 </div>
 </div>
 
 
  
