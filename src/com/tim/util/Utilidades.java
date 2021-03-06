package com.tim.util;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.TreeSet;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;


import org.w3c.dom.Document;

import org.w3c.dom.NodeList;

import org.w3c.dom.Node;

import org.w3c.dom.Element;


import org.apache.commons.beanutils.BeanUtils;
import org.w3c.dom.Document;
import org.xml.sax.SAXException;

import com.tim.dao.PositionDAO;
import com.tim.dao.StrategyDAO;
import com.tim.model.Position;
import com.tim.model.Rule;
import com.tim.model.Share_Strategy;
import com.tim.model.Strategy;
import com.tim.service.TIMApiGITrader;
import com.tim.util.ConfigKeys.IDENTITY_VALUE_TYPE;

public class Utilidades {
	
    public final static long SECOND_MILLIS = 1000;
    public final static long MINUTE_MILLIS = SECOND_MILLIS*60;
    public final static long DAYS_MILLIS = MINUTE_MILLIS*60*24;

    public static void closeTWSConnection(TIMApiGITrader oTWS)
    {
    try
    {
		if (oTWS.GITraderTWSIsConnected())
		{
			oTWS.GITraderDisconnectFromTWS();
		}
    }
    catch (Exception e) {}
    }
    
    public static  String getDateNowFormat()
	{
    	SimpleDateFormat Format = new SimpleDateFormat("yyyyMMdd");
//		Format.applyPattern("yyyyMMDD"); 
		Calendar  oCalendar = Calendar.getInstance();
		
		return  Format.format(oCalendar.getTime());
		
	}
    
    /* para los stocks que llevan un tick distinto del punto, cuartos, etc..(futuros), aplicamos 
     * el calculo de los valores limitados para ellos acorde
     * 2546.50 --> 2546.75 -->
     bReachedMin : Min o Max para saber si es corto o largo.
     */
    public static double TickLimit_WithMultiplier(Double OrderValue, Float Multiplier, Double ValueLimit, boolean bReachedMin)
    {
        // Generamos un array con multiplicadores desde el value de compra hasta el limite  +1 para saber el mas cercano
    	int _Steps = 0;
    	ArrayList<Double> aTicks = new ArrayList<Double>();
        _Steps = (int) (Math.abs(ValueLimit - OrderValue) / Multiplier);        
        for (int j=0;j<_Steps+2;j++)
        {
        	if (bReachedMin)
        		aTicks.add(OrderValue - (j*Multiplier));   // corto
        	else
        		aTicks.add(OrderValue + (j*Multiplier));   // largo
        	
        }
        
        	
        return findNearestValue(aTicks, ValueLimit.doubleValue());
    	
    }
    
    public static Double GetTickMaxMinValueFromArray(ArrayList<Double> ticksValue, IDENTITY_VALUE_TYPE type)
    {
    	double number =0;
    	number = Double.MAX_VALUE;
    	if (type.equals(IDENTITY_VALUE_TYPE.MAX))
    	{	
    		number = Double.MIN_VALUE;    		
    	}
    	double _tmpTick=0;
    	for(int i = 0; i < ticksValue.size(); i++) 
    	{
    		  _tmpTick= ticksValue.get(i).doubleValue();
    		  if (type.equals(IDENTITY_VALUE_TYPE.MAX))
    	      {	
    			   if (_tmpTick > number) {
    				   number = ticksValue.get(i);
    			   	}    		
    	      }
    		  else
    		  {
    			  if (_tmpTick < number) {
   				   	number = ticksValue.get(i);
   			   	   }
    			  
    		  }
    	}		  
    	
		return number;
    	
    	
    }
    
    /* FORMATO HHMM */
	public static  String getActualHourFormatPlusMinutes(String HourMinutes, Integer Minutes)
	{
			Calendar Hoy = Calendar.getInstance();
			
			return getActualHourFormatPlusMinutes(Hoy,HourMinutes,Minutes);
			
			
	}
	
	/* FORMATO HHMM */
	public static  String getActualHourFormatPlusMinutes(Calendar _Date, String HourMinutes, Integer Minutes)
	{
		try 
		
		{
			
			Calendar Hoy = Calendar.getInstance();
			
			Hoy.setTimeInMillis(_Date.getTimeInMillis());
				
			if (Minutes!=null)
			{
				
			    int hour = Integer.parseInt(HourMinutes.substring(0, 2));
			    int minute = Integer.parseInt(HourMinutes.substring(2));
			    
			    
			    Hoy.set(Calendar.HOUR_OF_DAY, hour);
			    Hoy.set(Calendar.MINUTE, minute);
			    
			    Hoy.add(Calendar.MINUTE,Minutes );
			    
				
				SimpleDateFormat Format = new SimpleDateFormat();
				Format.applyPattern("HHmm");
				
				return  Format.format(Hoy.getTime());
			}
			else
			{
				return  "-1";
			}
		}
		catch (Exception e) {return  "-1";}
		
	
	
		
	}
	
	
	/* FORMATO HHMM */
	public static  String getActualHourFormatSubstractMinutes(String HourMinutes, Integer Minutes)
	{
		try 
		
		{
		
			Calendar Hoy = null;
			if (Minutes!=null)
			{
				
			
			    Hoy = Calendar.getInstance();
			    
			    int hour = Integer.parseInt(HourMinutes.substring(0, 2));
			    int minute = Integer.parseInt(HourMinutes.substring(2));
			    
			    
			    Hoy.set(Calendar.HOUR_OF_DAY, hour);
			    Hoy.set(Calendar.MINUTE, minute);
			    
			    Hoy.add(Calendar.MINUTE,-Minutes );
			    
				
				SimpleDateFormat Format = new SimpleDateFormat();
				Format.applyPattern("HHmm");
				
				return  Format.format(Hoy.getTime());
			}
			else
			{
				return  "-1";
			}
		}
		catch (Exception e) {return  "-1";}
		
	
	
		
	}
	
	/* HOUR HHMM */
	public static  Calendar getNewCalendarWithHour(String HourMinutes)
	{
		Calendar Hoy = Calendar.getInstance();
	    
		return getNewCalendarWithHour(Hoy, HourMinutes);
	    
	}
	
	/* HOUR HHMM */
	public static  Calendar getNewCalendarWithHour(Calendar _oDate, String HourMinutes)
	{
		Calendar Hoy = Calendar.getInstance();
		
		Hoy.setTimeInMillis(_oDate.getTimeInMillis());
	    
	    int hour = Integer.parseInt(HourMinutes.substring(0, 2));
	    int minute = Integer.parseInt(HourMinutes.substring(2,4));
	    
	    
	    Hoy.set(Calendar.HOUR_OF_DAY, hour);
	    Hoy.set(Calendar.MINUTE, minute);
	    Hoy.set(Calendar.SECOND, 0);
	    Hoy.set(Calendar.MILLISECOND, 0);
		
	    
	    return Hoy;
	    
	}
	
    
	public static  String getActualHourFormat()
	{
		SimpleDateFormat Format = new SimpleDateFormat();
		Format.applyPattern("HHmm");
		Calendar  oCalendar = Calendar.getInstance();
		Date Now = new Date();
		
		
		return  Format.format(Now);
		
	}
	
	public static double _Porcentaje100(double num)
	{
		return (num * 100);		
	}
		
	
	public static double RedondeaPrice(double num) {
		double result = num * 100;
		result = Math.round(result);
		result = result / 100;
		return result;
		}
	
	public static int secondsDiff( Date earlierDate, Date laterDate )
    {
        if( earlierDate == null || laterDate == null ) return 0;
        
        return (int) ((laterDate.getTime() - earlierDate.getTime()) / SECOND_MILLIS);
    }
	
    /**
     * Get the minutes difference
     */
    public static int minutesDiff( Date earlierDate, Date laterDate )
    {
        if( earlierDate == null || laterDate == null ) return 0;
        
        return (int)((laterDate.getTime()/MINUTE_MILLIS) - (earlierDate.getTime()/MINUTE_MILLIS));
    }
    /**
     * Get the minutes difference
     */
    
    public static int daysDiff( Date earlierDate, Date laterDate )
    {
        if( earlierDate == null || laterDate == null ) return 0;
        
        return (int)((laterDate.getTime()/DAYS_MILLIS) - (earlierDate.getTime()/DAYS_MILLIS));
    }
    
    
    /* POR CADA ESTRATEGIA DE BBDD, DEVOLVEMOS LOS IMPLEMENTATIONS ASOCIADOS A CADA CLASSNAME */
    public static java.util.List<Strategy>  LoadStrategies(List StrategiesBBDD) throws Exception
    {
    	List<Strategy> lFactoryStra = new ArrayList<Strategy>();
    	for (int i=0;i<StrategiesBBDD.size();i++)	    				
    	{
    		
    		Strategy oStratAbstract =  (Strategy) StrategiesBBDD.get(i);    		
    		
    		// cargo la estrategia del classname y ya tengo los datos
    		Class oFactoryStrat =  Class.forName(oStratAbstract.getClassName());
    		//Strategy oStrategyFactory = new Strategy(); 
    		
    		Strategy oStrategyFactory = (Strategy) oFactoryStrat.newInstance();
    		// copiamos de la clase abastracta los nuevos valores de la nueva clase implementada
    		BeanUtils.copyProperties(oStrategyFactory, oStratAbstract);
//    		oStrategyFactory = copyFields(oStrategyFactory, oStratAbstract);
    		
    		lFactoryStra.add(oStrategyFactory);
    		
    		
    	}
		return lFactoryStra;
		
		
         
    }
    
    /* POR CADA REGLA DE BBDD, DEVOLVEMOS LOS IMPLEMENTATIONS ASOCIADOS A CADA CLASSNAME */
    public static java.util.List<Rule>  LoadRules(List RulesBBDD) throws Exception
    {
    	List<Rule> lFactoryStra = new ArrayList<Rule>();
    	for (int i=0;i<RulesBBDD.size();i++)	    				
    	{
    		
    		Rule oStratAbstract =  (Rule) RulesBBDD.get(i);    		
    		
    		// cargo la estrategia del classname y ya tengo los datos
    		Class oFactoryStrat =  Class.forName(oStratAbstract.getClassName());
    		//Strategy oStrategyFactory = new Strategy(); 
    		
    		Rule oStrategyFactory = (Rule) oFactoryStrat.newInstance();
    		// copiamos de la clase abastracta los nuevos valores de la nueva clase implementada
    		BeanUtils.copyProperties(oStrategyFactory, oStratAbstract);
//    		oStrategyFactory = copyFields(oStrategyFactory, oStratAbstract);
    		
    		lFactoryStra.add(oStrategyFactory);
    		
    		
    	}
		return lFactoryStra;
		
		
         
    }
    
    
   
	


    public static  boolean IsPar(int Number) { 
        return ((Number % 2) == 0);
    }
    
    
    /* NOS DEVUELVE LA ULTIMA ORDEN VALIDA. NECESITAMOS SABER LA ULTIMA ENTRE LAS 
     * COMPRAS Y LAS VENTAS. */ 
    
    public static int LastPositionIDTws(TIMApiGITrader oWrapper) throws SQLException, Exception { 
    
    	
    	int PositionID;
    	int LastPositionID = PositionDAO.LastPositionID();
    	PositionID = (LastPositionID  > oWrapper.getLastPositionID() ? LastPositionID  : oWrapper.getLastPositionID());
    	PositionID = PositionID +1;
    	return PositionID;

    	
    }
    
    public static Double findNearestValue(ArrayList<Double> arr, Double value) {
    	
    	 
    	 Double minDiff=new Double(100000.0);
    	 Double ans=null;
    	 for(int z=0;z<arr.size();z++)
    	 {
    	         Double  m=Math.abs(value-arr.get(z).doubleValue());
    	         if(m<minDiff)
    	         { 
    	                minDiff=m; 
    	                ans=arr.get(z).doubleValue(); 
    	         }
    	 }
    	 return ans;
    	
      }
	

    public  static  boolean fn_IsStrategyInShareStrategies(Long StrategyToFind,java.util.List<Share_Strategy>_lStratShare)
	{
		boolean retValue=false;
		for (int j=0;j<_lStratShare.size();j++)
		{
			Share_Strategy _oTmp = _lStratShare.get(j);
			if (_oTmp.getStrategyId().equals(StrategyToFind))
			{
				retValue=true;
				break;
			}
		}
		return retValue;
	}


    /* 
    String Months 1,3,5 jANUARY, mARCH..
    String DayOfWeek 1 Monday.. 
    , String WeekMonth 3 Third week of current month
    
    */
    
    public static String getActiveFutureByDate(String Months,String DayOfWeek, String WeekMonth,
    		Calendar _FutureDate, String ShortLongFormat)
    
    {
    	SimpleDateFormat sdfLONG = new SimpleDateFormat();
    	SimpleDateFormat sdfSHORT = new SimpleDateFormat();
    	
    	int j=0;
    	
    	SimpleDateFormat sdfSorted = new SimpleDateFormat();
    	
    	sdfLONG.applyPattern("dd/MM/yyyy");
    	sdfSHORT.applyPattern("yyyyMM");
    	
    	sdfSorted.applyPattern("yyyy/MM/dd");
    	
    	
       	//_getActiveFutureDate("1, 4, 6","5","3");
    	
    	
    	Calendar _Today = Calendar.getInstance();
    	
    	_Today.setTimeInMillis(_FutureDate.getTimeInMillis());
    	
    	
    	int [] _years = {_Today.get(Calendar.YEAR), _Today.get(Calendar.YEAR) +1}; 
    	
    	
    	
    	TreeSet<String> _FutDate = new TreeSet<String>();
    	/* Pregeneramos los intervalos en funcion de los meses que se nos pasa del a�o actual y del siguiente */
    	
    	for (int y=0;y<_years.length;y++)
    	{
    		String[] aMonths= Months.split(",");
	    	for (int k=0;k<aMonths.length;k++)
	    	{
	    		Calendar _fdate = _Today;
	    		_fdate.set(Calendar.YEAR, _years[y]);  // SPAIN MONDAY
	    		_fdate.set(Calendar.DAY_OF_WEEK, Integer.parseInt(DayOfWeek));  // SPAIN MONDAY
	    		_fdate.set(Calendar.MONTH, Integer.parseInt(aMonths[k].trim())-1);  // months -1
	    		_fdate.set(Calendar.WEEK_OF_MONTH, Integer.parseInt(WeekMonth));  // months -1	    		
	    		
	    		
	    		_FutDate.add(sdfSorted.format(_fdate.getTime()));
	    	}
    	
    	}
    	String _DateSortedValue =  _FutDate.higher(sdfSorted.format(_FutureDate.getTime()));
    	Date  _DateRetValue = new Date();
    	if (_DateSortedValue!=null)    		
    	{
    		// from yyyy/mm/dd    		
    		try {
				_DateRetValue = sdfSorted.parse(_DateSortedValue);
				if (ShortLongFormat.equals("L"))					
					return sdfLONG.format(_DateRetValue);
				else
					return sdfSHORT.format(_DateRetValue);
					
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				//e.printStackTrace();
				return "";
			}
    		
    	
    	}
    	else
    		return "";
  //  	else
    //		return "";
    }
    
    public static String getActiveFutureDate(String Months,String DayOfWeek, String WeekMonth )
    {
    	return  getActiveFutureByDate( Months, DayOfWeek, WeekMonth, Calendar.getInstance(), "L");
    	
    }
    
    public static String getExpFutMonths(String _value) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException 
    {
    	return parseXmlFutureInfo(_value,"months");
    }
    public static String getExpFutWeek(String _value) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException 
    {
    	return parseXmlFutureInfo(_value,"week");
    }
    public static String getExpFutDayOfWeek(String _value) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException 
    {
    	return parseXmlFutureInfo(_value,"day");
    }
    
    /* 
    <?xml version="1.0"><expiration><months>2,3</months><week>4</week><day>5</day></expiration> 
     */
    private static  String parseXmlFutureInfo(String _Xml, String Field) throws ParserConfigurationException, SAXException, IOException, XPathExpressionException
    {
    	DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
    	DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
    	
		InputStream is = new ByteArrayInputStream(_Xml.getBytes());
    	Document doc = dBuilder.parse(is);
    	
    	
    	XPath xPath =  XPathFactory.newInstance().newXPath();
    	String expression = "/expiration/" + Field;
    	 
    	//read a string value
    	String FieldValue = xPath.compile(expression).evaluate(doc);
    	
    	return FieldValue;
		
    		
    }
    
    
    public static void main(String[] args) throws Exception {
 		// TODO Auto-generated method stub
    	 //List<Position> Lista = PositionDAO.getTradingPositions(new Timestamp(Calendar.getInstance().getTimeInMillis()));
    	System.out.println(parseXmlFutureInfo("<?xml version='1.0' encoding='iso-8859-1'?><expiration><months>2,3</months><week>4</week><day>5</day></expiration>","months")); 
    	System.out.println(getActiveFutureDate("3,6,9,12","6","3"));
		//System.out.println(TickLimit_WithMultiplier(new Double(2750.25), new Double(0.25), new Double(2749.9), true));
		
 	}	


}
