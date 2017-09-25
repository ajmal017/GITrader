package com.tim.model;

import com.tim.service.TIMApiGITrader;

public class Rule {

	public String getJSP_PAGE() {
		return JSP_PAGE;
	}
	public void setJSP_PAGE(String jSP_PAGE) {
		JSP_PAGE = jSP_PAGE;
	}
	protected Long ruleId;
	protected String name;
	protected String description; 
	protected  int active;	
	protected  String type; 	// BUY SELL	
	protected  String className="";	    //paquete y clase
	protected Long buy_minto_offset_close; 	//	 // comprar hasta un tiempo antes del cierre de mercado.
	protected Long  buy_limit_amount_day; 	//	 // comprar hasta una cantidad dada diaria
	
	protected Long  buy_max_positionday_share; 	// entrar numero de veces en el mismo valor por dia 
	
	public Long getBuy_max_positionday_share() {
		return buy_max_positionday_share;
	}
	public void setBuy_max_positionday_share(Long buy_max_positionday_share) {
		this.buy_max_positionday_share = buy_max_positionday_share;
	}
	protected  String JSP_PAGE=""; 	// 
	
	
	public Long getBuy_limit_torepeat_same_share() {
		return buy_limit_torepeat_same_share;
	}
	public void setBuy_limit_torepeat_same_share(Long buy_limit_torepeat_same_share) {
		this.buy_limit_torepeat_same_share = buy_limit_torepeat_same_share;
	}
	protected Long  buy_limit_torepeat_same_share; 	//	 // entrar en la misma posicion n veces en el trading si se cumple unas cosas
	
	
	

	
	public Long getBuy_limit_amount_day() { 
		return buy_limit_amount_day;
	}
	public void setBuy_limit_amount_day(Long buy_limit_amount_day) {
		this.buy_limit_amount_day = buy_limit_amount_day;
	}
	public Long getRuleId() {
		return ruleId;
	}
	public void setRuleId(Long ruleId) {
		this.ruleId = ruleId;
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
	public int getActive() {
		return active;
	}
	public void setActive(int active) {
		this.active = active;
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
	public Long getBuy_minto_offset_close() {
		return buy_minto_offset_close;
	}
	public void setBuy_minto_offset_close(Long buy_minto_offset_close) {
		this.buy_minto_offset_close = buy_minto_offset_close;
	}
	public  boolean isActive() {
		return false;
	}
	public boolean Execute(Market oMarket, Share Share,TIMApiGITrader oTIMApiWrapper) {
		return false;
	} // e   
	public boolean Verify(Share Share, Market oMarket) {
		return false;
	}  // e);
	public Rule() {
	}
}
