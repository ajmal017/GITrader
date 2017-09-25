package com.tim.model;

import java.util.List;


public class Trading  {
	
    Long  tradingID = null;
    String name = null;    
    Long active = null;
    java.sql.Timestamp date_trading=null;
    
    List<Strategy> lStrategies = null;
    List<Rule> lRules = null;
    
    public List<Rule> getlRules() {
		return lRules;
	}
	public void setlRules(List<Rule> lRules) {
		this.lRules = lRules;
	}
	public Long getOffset_time_toactive() {
		return offset_time_toactive; 
	}
	public void setOffset_time_toactive(Long offset_time_toactive) {
		this.offset_time_toactive = offset_time_toactive;
	}
	String description = null;
    Long realtime_reading = null;
    Long offset_time_toactive = null;
	public Long getTradingID() {
		return tradingID;
	}
	public void tradingID(Long tradingID) {
		this.tradingID = tradingID;
	}
	public String getName() {
		return name;
	}
	public java.sql.Timestamp getDate_trading() {
		return date_trading;
	}
	public void setDate_trading(java.sql.Timestamp date_trading) {
		this.date_trading = date_trading;
	}
	public List<Strategy> getlStrategies() {
		return lStrategies;
	}
	public void setlStrategies(List<Strategy> lStrategies) {
		this.lStrategies = lStrategies;
	}
	public void setTradingID(Long tradingID) {
		this.tradingID = tradingID;
	}
	public void setName(String name) {
		this.name = name;
	}
	public Long getActive() {
		return active;
	}
	public void setActive(Long active) {
		this.active = active;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public Long getRealtime_reading() {
		return realtime_reading;
	}
	public void setRealtime_reading(Long realtime_reading) {
		this.realtime_reading = realtime_reading;
	} 
    
    
}