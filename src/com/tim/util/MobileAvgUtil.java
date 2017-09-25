package com.tim.util;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import com.tim.dao.RealTimeDAO;
import com.tim.model.RealTime;
import com.tim.model.Share;
import com.tim.model.StrategySimpleMobileAverage;
import com.tim.simulation.jobs.Trading_Simulation;

public class MobileAvgUtil {

	public static void main(String[] args) {
		// TODO Auto-generated method stub

	}
	
	
	/* avgMobile --> MEDIA MOVIL 
	 * oRTimeWidthRange -> Max y Minimo de la barra 
	 * oRTimeEnTramo --> Cierre de la barra
	 */

	public static boolean _IsBuySignalMM8_5MINBar(RealTime _avgMobileSimple,
			RealTime oRTimeWidthRange,float _WidthBarRangePercent , RealTime oRTimeEnTramoBar)
	{
		LogTWM.getLog(MobileAvgUtil.class);
					

		if (_avgMobileSimple.getValue().floatValue() <= (oRTimeWidthRange.getMax_value().floatValue() - _WidthBarRangePercent)
				&& (oRTimeEnTramoBar.getValue().floatValue() >= (oRTimeWidthRange.getMin_value().floatValue() + _WidthBarRangePercent)))
		{
			LogTWM.info("MM <= Max - Ancho = " + _avgMobileSimple.getValue().floatValue() + "<="  + oRTimeEnTramoBar.getValue().floatValue() + "-" + _WidthBarRangePercent);
			LogTWM.info("Cierre  >= Min + Ancho = " + oRTimeEnTramoBar.getValue().floatValue() + ">="  + oRTimeWidthRange.getMin_value() + "+" + _WidthBarRangePercent);
		}
		
		return (_avgMobileSimple.getValue().floatValue() <= (oRTimeWidthRange.getMax_value().floatValue() - _WidthBarRangePercent)
				&& (oRTimeEnTramoBar.getValue().floatValue() >= (oRTimeWidthRange.getMin_value().floatValue() + _WidthBarRangePercent)));
	}
	
	
	public static boolean _IsSellSignalMM8_5MINBar(RealTime _avgMobileSimple,
			RealTime oRTimeWidthRange,float _WidthBarRangePercent , RealTime oRTimeEnTramoBar)
	{
		LogTWM.getLog(MobileAvgUtil.class);
		
		if (_avgMobileSimple.getValue().floatValue() >= (oRTimeWidthRange.getMin_value().floatValue() + _WidthBarRangePercent)
				&& (oRTimeEnTramoBar.getValue().floatValue() <= (oRTimeWidthRange.getMax_value().floatValue() - _WidthBarRangePercent)))
		{
			LogTWM.info("MM >= Min + Ancho = " + _avgMobileSimple.getValue().floatValue() + ">="  + oRTimeEnTramoBar.getValue().floatValue() + "+" + _WidthBarRangePercent);
			LogTWM.info("Cierre  <= Max - Ancho = " + oRTimeEnTramoBar.getValue().floatValue() + "<="  + oRTimeWidthRange.getMin_value() + "-" + _WidthBarRangePercent);
		}
		
		return (_avgMobileSimple.getValue().floatValue() >= (oRTimeWidthRange.getMin_value().floatValue() + _WidthBarRangePercent)
				&& (oRTimeEnTramoBar.getValue().floatValue() <= (oRTimeWidthRange.getMax_value().floatValue() - _WidthBarRangePercent)));
	}
	
	public static RealTime getAvgMobileMM8(Calendar _oDateFrom, Calendar _oDateTo, int Periods, int TimeBars, 
			int ShareStrategyID, int PeriodN, boolean Simulation )
	{
		
		Calendar _MM8DateFrom = Calendar.getInstance();
		Calendar _MM8DateTo = Calendar.getInstance();	
		
		_MM8DateFrom.setTime(_oDateFrom.getTime());
		_MM8DateTo.setTime(_oDateTo.getTime());
		
		
		
		
		int _ModMinute =_MM8DateTo.get(Calendar.MINUTE) % TimeBars;
		_MM8DateTo.add(Calendar.MINUTE,  - _ModMinute);
		
		/* OJO, TENEMOS QUE VER QUE LAS MEDIAS MOVILES SON DE LAS BARRAS PREVIAS Y NO DE LA ACTUAL */ 
		_MM8DateFrom.add(Calendar.MINUTE, - (Periods  * TimeBars));
		
		
		/* MODIFICACION, MEDIAS MOVILES HASTA EL PERIDO N, N-1, N-2 */
		_MM8DateTo.add(Calendar.MINUTE,  - (PeriodN * TimeBars));
		_MM8DateFrom.add(Calendar.MINUTE,  - (PeriodN * TimeBars));
		
		
		SimpleDateFormat _sdf =new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
	//	System.out.println("getAvgMobileMM8 : Periodo -" + PeriodN + ", _oDateFrom:" + _sdf.format(_MM8DateFrom.getTime()) + ",_oDateTo:" + _sdf.format(_MM8DateTo.getTime()));
		
		RealTime _avgMobileSimple = RealTimeDAO.getAvgSimpleMobile(new Timestamp(_MM8DateFrom.getTimeInMillis()),new Timestamp(_MM8DateTo.getTimeInMillis()),
				ShareStrategyID,Periods , TimeBars, Simulation);
		
		return _avgMobileSimple;
		
	}
	
	public static RealTime getAvgMobileMM8(Calendar _oDateFrom, Calendar _oDateTo, int Periods, int TimeBars, int ShareStrategyID, boolean Simulation)
	{
		return getAvgMobileMM8(_oDateFrom, _oDateTo, Periods, TimeBars, ShareStrategyID, 0, Simulation);
		
		
	}
	
	public static RealTime getMinMaxBarFromShare( Calendar _To, int TimeBars, int ShareStrategyID, int PeriodN, boolean Simulation)
	{
		Calendar _TimeBarWidthFrom = Calendar.getInstance(); 
		_TimeBarWidthFrom.setTimeInMillis(_To.getTimeInMillis());		
				
		Calendar _TimeBarWidthTo = Calendar.getInstance();
		_TimeBarWidthTo.setTimeInMillis(_To.getTimeInMillis());							 
		_TimeBarWidthFrom.add(Calendar.MINUTE, -((PeriodN+1) * TimeBars));
		_TimeBarWidthTo.add(Calendar.MINUTE, -(PeriodN * TimeBars));   // 20150228 errores en el concepto ini fin de barra
		_TimeBarWidthTo.add(Calendar.SECOND, -1);
		
		SimpleDateFormat _sdf =new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
		
		//System.out.println("getMinMaxBarFromShare : Periodo -" + PeriodN + ", _oDateFrom:" + _sdf.format(_TimeBarWidthFrom.getTime()) + ",_oDateTo:" + _sdf.format(_TimeBarWidthTo.getTime()));
		RealTime oRTimeWidthRange =null;
		if (!Simulation)
			oRTimeWidthRange = (RealTime) RealTimeDAO.getRealTimeBetweenDates(ShareStrategyID, new Timestamp(_TimeBarWidthFrom.getTimeInMillis()), 
				new Timestamp(_TimeBarWidthTo.getTimeInMillis()));
		else
			oRTimeWidthRange = (RealTime) RealTimeDAO.getRealTimeBetweenDatesSimulated(ShareStrategyID, new Timestamp(_TimeBarWidthFrom.getTimeInMillis()), 
					new Timestamp(_TimeBarWidthTo.getTimeInMillis())); 
		
		
		return oRTimeWidthRange;
	}
	
	public static RealTime getMinMaxBarFromShare( Calendar _To, int TimeBars, int ShareStrategyID, boolean Simulation )
	{
		
		
		return getMinMaxBarFromShare(_To, TimeBars,ShareStrategyID,0, Simulation);
	}
	
	

}
