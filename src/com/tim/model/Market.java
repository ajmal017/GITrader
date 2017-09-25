package com.tim.model;


public class Market  {
	
    Long  marketID = null;
    String name = null;    
    String identifier = null;    
    Long active = null;
    Long trading = null;
    Long reading = null;
    public Long getReading() {
		return reading;
	}
	public void setReading(Long reading) {
		this.reading = reading;
	}
	
    String currency = null;
    /* String security_type = null;
    String exchange = null;
    String primary_exchange = null;*/
    String start_hour = null; //1800
    String end_hour = null; //1800
	public Long getMarketID() {
		return marketID;
	}
	public void setMarketID(Long marketID) {
		this.marketID = marketID;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getIdentifier() {
		return identifier;
	}
	public void setIdentifier(String identifier) {
		this.identifier = identifier;
	}
	public Long getActive() {
		return active;
	}
	public void setActive(Long active) {
		this.active = active;
	}
	public Long getTrading() {
		return trading;
	}
	public void setTrading(Long trading) {
		this.trading = trading;
	}
	/* public String getSecurity_type() {
		return security_type;
	}
	public void setSecurity_type(String security_type) {
		this.security_type = security_type;
	}*/
	public String getCurrency() {
		return currency;
	}
	public void setCurrency(String currency) {
		this.currency = currency;
	}
	/* public String getExchange() {
		return exchange;
	}
	public void setExchange(String exchange) {
		this.exchange = exchange;
	}
	public String getPrimary_exchange() {
		return primary_exchange;
	}
	public void setPrimary_exchange(String primary_exchange) {
		this.primary_exchange = primary_exchange;
	}*/
	public String getStart_hour() {
		return start_hour;
	}
	public void setStart_hour(String start_hour) {
		this.start_hour = start_hour;
	}
	public String getEnd_hour() {
		return end_hour;
	}
	public void setEnd_hour(String end_hour) {
		this.end_hour = end_hour;
	}
    
        
    
}