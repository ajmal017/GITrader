package com.tim.model;


public class Share  {


	

    public Long getShareId() {
		return shareId;
	}

	public void setShareId(Long shareId) {
		this.shareId = shareId;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public  java.sql.Timestamp getAddedDate() {
		return addedDate;
	}

	public void setAddedDate( java.sql.Timestamp addedDate) {
		this.addedDate = addedDate;
	}
   
	
	private Long  shareId = null;
	private String name = null;
	private String symbol = null;
	private Long active = null;
	private Long active_trading = null;
	
	/* GESTION DE ERRORES DE LA CONECTIVIDAD ENTRE OPERACIONES CON LA TWS */
	private String last_error_data_read = null;
	private String last_error_data_trade = null;
	
	/* SE VERIFICA EL CONTRACT DETAIL DIARIO. OJO CON LA ESTRATEGIA DE VERIFICACION DE FUTUROS,
	 * HABRA QUE REVISARLA. */
	private java.sql.Timestamp date_contract_verified=null;
	
	
	/* AUTOEXTEND DE EXPIRATION DATE OF FUTURES */
	private String expiry_expression = null;  /* EXPIRATION XML EXPRESSION  */	
	
	/* AUTOEXTEND DE EXPIRATION DATE OF FUTURES */

	public java.sql.Timestamp getDate_contract_verified() {
		return date_contract_verified;
	}

	

	
	public String getExpiry_expression() {
		return expiry_expression;
	}

	public void setExpiry_expression(String expiry_expression) {
		this.expiry_expression = expiry_expression;
	}

	public void setDate_contract_verified(java.sql.Timestamp date_contract_verified) {
		this.date_contract_verified = date_contract_verified;
	}


	private String  security_type=null;	
	private String  exchange =null;
	private String  primary_exchange=null;
	
	
	public String getSecurity_type() {
		return security_type;
	}

	public void setSecurity_type(String security_type) {
		this.security_type = security_type;
	}

	

	public String getExchange() {
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
	}


	/* campo comodin, si se opera hoy o no */
	private Long active_trading_today = null;
	
	
	public Long getActive_trading_today() {
		return active_trading_today;
	}

	public void setActive_trading_today(Long active_trading_today) {
		this.active_trading_today = active_trading_today;
	}

	public Long getActive_trading() {
		return active_trading;
	}

	public void setActive_trading(Long active_trading) {
		this.active_trading = active_trading;
	}


	private Long number_purchase = null;
    
    /* banda horaria para los maximos y minimos */
	private Integer  offset1min_read_from_initmarket = null;
	private Integer offset2min_read_from_initmarket = null;
	
	private Float  sell_percentual_stop_lost=null; // : Para la venta, un porcentaje de perdida
	private Float  sell_percentual_stop_profit=null; // : Para la venta, cierra la posicion sobre un x por ciento de beneficio 
	private Float   sell_percentual_stop_profit_position=null; // :   Sobre la anterior, cierra un x por ciento la posicion
	
	
	/* FUTUROS */
	private java.sql.Timestamp  expiry_date=null; // :   Sobre la anterior, cierra un x por ciento la posicion
	private Float  tick_futures=null; // TICK en los futuros (cuartos, medios...etc..)
	public String getLast_error_data_read() {
		return last_error_data_read;
	}

	public void setLast_error_data_read(String last_error_data_read) {
		this.last_error_data_read = last_error_data_read;
	}

	public String getLast_error_data_trade() {
		return last_error_data_trade;
	}

	public void setLast_error_data_trade(String last_error_data_trade) {
		this.last_error_data_trade = last_error_data_trade;
	}


	private Float  multiplier=null; // Multiplicador
	
	/* FUTUROS */
    
	public Float getMultiplier() {
		return multiplier;
	}

	public void setMultiplier(Float multiplier) {
		this.multiplier = multiplier;
	}

	public Float getTick_Futures() {
		return tick_futures;
	}

	public void setTick_Futures(Float tick) {
		this.tick_futures = tick;
	}

	public Float getSell_percentual_stop_lost() {
		return sell_percentual_stop_lost;
	}

	public java.sql.Timestamp getExpiry_date() {
		return expiry_date;
	}

	public void setExpiry_date(java.sql.Timestamp expiry_date) {
		this.expiry_date = expiry_date;
	}

	

	public void setSell_percentual_stop_lost(Float sell_percentual_stop_lost) {
		this.sell_percentual_stop_lost = sell_percentual_stop_lost;
	}

	public Float getSell_percentual_stop_profit() {
		return sell_percentual_stop_profit;
	}

	public void setSell_percentual_stop_profit(Float sell_percentual_stop_profit) {
		this.sell_percentual_stop_profit = sell_percentual_stop_profit;
	}

	public Float getSell_percentual_stop_profit_position() {
		return sell_percentual_stop_profit_position;
	}

	public void setSell_percentual_stop_profit_position(
			Float sell_percentual_stop_profit_position) {
		this.sell_percentual_stop_profit_position = sell_percentual_stop_profit_position;
	}

	public String getName() {
		return name;
	}

	

	public Integer getOffset1min_read_from_initmarket() {
		return offset1min_read_from_initmarket;
	}

	public void setOffset1min_read_from_initmarket(
			Integer offset1min_read_from_initmarket) {
		this.offset1min_read_from_initmarket = offset1min_read_from_initmarket;
	}

	public Integer getOffset2min_read_from_initmarket() {
		return offset2min_read_from_initmarket;
	}

	public void setOffset2min_read_from_initmarket(
			Integer offset2min_read_from_initmarket) {
		this.offset2min_read_from_initmarket = offset2min_read_from_initmarket;
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

	public Long getNumber_purchase() {
		return number_purchase;
	}

	public void setNumber_purchase(Long number_purchase) {
		this.number_purchase = number_purchase;
	}

	public Double getPercentual_value_gap() {
		return percentual_value_gap;
	}

	public void setPercentual_value_gap(Double percentual_value_gap) {
		this.percentual_value_gap = percentual_value_gap;
	}
	
	private java.sql.Timestamp addedDate = null;
	private Double percentual_value_gap = null;  // hueco por arriba y abajo sobre los maximos y minimos
	private Double percentual_limit_buy = null;	 // precio limitado de compra / venta
	
	private Long historical_data = null; // DATOS PARA MANTENER UN HISTORICO.
			
	

	public Long getHistorical_data() {
		return historical_data;
	}

	public void setHistorical_data(Long historical_data) {
		this.historical_data = historical_data;
	}

	public Double getPercentual_limit_buy() {
		return percentual_limit_buy;
	}

	public void setPercentual_limit_buy(Double percentual_limit_buy) {
		this.percentual_limit_buy = percentual_limit_buy;
	}
   
    

}