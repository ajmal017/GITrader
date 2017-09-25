package com.tim.model;

import java.util.Calendar;

import com.tim.service.TIMApiGITrader;
import com.tim.service.TIMApiGITrader;

public class Strategy {
	
	protected  Long strategyId;
	protected  String name;
	protected  String description; 
	protected  Integer activada;
	protected  int offsetmin_fromopen_market; 	// minutes
	
	
	protected  String JSP_PAGE=""; 	// minutes
	
	protected String ACCOUNT_NAME="";   // ib optional acccount name to trade
	
	public String getACCOUNT_NAME() {
		return ACCOUNT_NAME;
	}
	public void setACCOUNT_NAME(String aCCOUNT_NAME) {
		ACCOUNT_NAME = aCCOUNT_NAME;
	}
	/*public Double getSell_percentual_stop_lost() {
		return sell_percentual_stop_lost;
	}
	public void setSell_percentual_stop_lost(Double sell_percentual_stop_lost) {
		this.sell_percentual_stop_lost = sell_percentual_stop_lost;
	}
	public Double getSell_percentual_stop_profit() {
		return sell_percentual_stop_profit;
	}
	public void setSell_percentual_stop_profit(Double sell_percentual_stop_profit) {
		this.sell_percentual_stop_profit = sell_percentual_stop_profit;
	}
	public Double getSell_percentual_position_profit() {
		return sell_percentual_position_profit;
	}
	public void setSell_percentual_position_profit(
			Double sell_percentual_position_profit) {
		this.sell_percentual_position_profit = sell_percentual_position_profit;
	}*/
	protected  String type; 	// BUY SELL	
	public String getJSP_PAGE() {
		return JSP_PAGE;
	}
	public void setJSP_PAGE(String jSP_PAGE) {
		JSP_PAGE = jSP_PAGE;
	}
	protected  String className="";	    //paquete y clase
	/* protected Double sell_percentual_stop_lost; 	// normalmente a la venta por cierre por perdida
	protected Double sell_percentual_stop_profit;    // normalmente a la venta por cierre por beneficio
	protected Double sell_percentual_position_profit;  //sobre la anterior, cerramos un x por ciento la posicion por beneficio
	*/
	protected Integer sell_all_deadline_min_toclose ;  // minutos hasta el cierre para poder operar..si no se sobrepasa, se cierran todas.
	protected Integer  tmp_sell_all_deadline_min_toclose; 
	
	
	/* MACD DATA */
	protected Integer  macd_periods;  // periodos de la media movil
	protected Integer  mcad_timebars; // tiempo de cada barra (5 minutos)
	protected Float  mcad_rateavg_entry;	// porcentaje de superacion de la media el precio
	
	/* Sirve para saber que no hay mas operaciones de entrada en el dia de la simulacion */
	protected boolean  _FULL_DAY_SCANNED;
	
	
	
	
	public boolean is_FULL_SIMULATION_DAY_SCANNED() {
		return _FULL_DAY_SCANNED;
	}
	public void FULL_SIMULATION_DAY_SCANNED(boolean _FULL_DAY_SCANNED) {
		this._FULL_DAY_SCANNED = _FULL_DAY_SCANNED;
	}
	public Integer getMacd_periods() {
		return macd_periods;
	}
	public void setMacd_periods(Integer macd_periods) {
		this.macd_periods = macd_periods;
	}
	public Integer getMcad_timebars() {
		return mcad_timebars;
	}
	public void setMcad_timebars(Integer mcad_timebars) {
		this.mcad_timebars = mcad_timebars;
	}
	public Float getMcad_rateavg_entry() {
		return mcad_rateavg_entry;
	}
	public void setMcad_rateavg_entry(Float mcad_rateavg_entry) {
		this.mcad_rateavg_entry = mcad_rateavg_entry;
	}
	public Integer getTmp_sell_all_deadline_min_toclose() {
		return tmp_sell_all_deadline_min_toclose;
	}
	public void setTmp_sell_all_deadline_min_toclose(
			Integer tmp_sell_all_deadline_min_toclose) {
		this.tmp_sell_all_deadline_min_toclose = tmp_sell_all_deadline_min_toclose;
	}
	public Integer getSell_all_deadline_min_toclose() {
		return sell_all_deadline_min_toclose;
	}
	public void setSell_all_deadline_min_toclose(
			Integer sell_all_deadline_min_toclose) {
		this.sell_all_deadline_min_toclose = sell_all_deadline_min_toclose;
	}
	public Long getStrategyId() {
		return strategyId;
	}
	public void setStrategyId(Long strategyId) {
		this.strategyId = strategyId;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Integer getActive() {
		return activada;
	}
	public void setActivada(Integer active) {
		this.activada = active;
	}
	public Integer getActivada() {
		return activada;
	}
	public int getOffsetmin_fromopen_market() {
		return offsetmin_fromopen_market;
	}
	public void setOffsetmin_fromopen_market(int offsetmin_fromopen_market) {
		this.offsetmin_fromopen_market = offsetmin_fromopen_market;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getClassName() {
		return className;
	}
	public void setClassName(String className) {
		this.className = className;
	}
	public  boolean isActive() {
		return false;
	}
	/* INICIO METODOS A IMPLEMENTAR POR LAS SIMULACIONES */
	public boolean ExecuteSimulation(Market oMarket, Share Share, Calendar SimulationDate) {
		return false;
	} // e   
	public boolean VerifySimulation(Share Share, Market oMarket,Calendar SimulationDate) {
		return false;
	}  // e);
	
	/* FIN METODOS A IMPLEMENTAR POR LAS SIMULACIONES */
	public boolean Execute(Market oMarket, Share Share,TIMApiGITrader oTIMApiWrapper) {
		return false;
	} // e   
	public boolean Verify(Share Share, Market oMarket) {
		return false;
	}  // e);
	public Strategy() {
	}
	protected String  sell_all_deadline_type_operation;  // cierre manual, se especifica que tipos de operaciones se cierran, all, sell, buy
	protected Integer  sell_all_deadline_deactivate_trading;  // cierra manual, se especifica si se sigue operando.
	public String getSell_all_deadline_type_operation() {
		return sell_all_deadline_type_operation;
	}
	public void setSell_all_deadline_type_operation(
			String sell_all_deadline_type_operation) {
		this.sell_all_deadline_type_operation = sell_all_deadline_type_operation;
	}
	public Integer getSell_all_deadline_deactivate_trading() {
		return sell_all_deadline_deactivate_trading;
	}
	public void setSell_all_deadline_deactivate_trading(
			Integer sell_all_deadline_deactivate_trading) {
		this.sell_all_deadline_deactivate_trading = sell_all_deadline_deactivate_trading;
	}
	
	
	
}
