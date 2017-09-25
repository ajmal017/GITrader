package com.tim.model;

import java.sql.Timestamp;

import java.io.Serializable;


public class Position {
	

		
		private Long positionID =null;
		private Long shareID =null;
		private Long share_number =null;   // total de acciones de la cartera		
		private Long share_number_traded =null;  // paquete acumulado 
		private Long share_number_to_trade =null;  // paquete de acciones a realizar
		public Long getShare_number_traded() {
			return share_number_traded;
		}

		public void setShare_number_traded(Long share_number_traded) {
			this.share_number_traded = share_number_traded;
		}

		public Long getShare_number_to_trade() {
			return share_number_to_trade;
		}

		public void setShare_number_to_trade(Long share_number_to_trade) {
			this.share_number_to_trade = share_number_to_trade;
		}

		private String  state =null;
		private String  state_buy =null;
		private String  state_sell =null;	
		private String  description =null;
		private Double  price_buy =null;
		private Double  price_real_buy =null;
		private Double  limit_price_buy =null;
		private Double  price_sell =null;
		private Double  price_real_sell =null;
		private Double  limit_price_sell =null;
		
		private Timestamp  date_sell =null;
		public Timestamp getDate_sell() {
			return date_sell;
		}

		public void setDate_sell(Timestamp date_sell) {
			this.date_sell = date_sell;
		}

		public Timestamp getDate_buy() {
			return date_buy;
		}

		public void setDate_buy(Timestamp date_buy) {
			this.date_buy = date_buy;
		}

		private Timestamp date_buy =null;
		private Timestamp  date_real_sell =null;
		private Timestamp date_real_buy =null;
		
		public Timestamp getDate_real_sell() {
			return date_real_sell;
		}

		public void setDate_real_sell(Timestamp date_real_sell) {
			this.date_real_sell = date_real_sell;
		}

		public Timestamp getDate_real_buy() {
			return date_real_buy;
		}

		public void setDate_real_buy(Timestamp date_real_buy) {
			this.date_real_buy = date_real_buy;
		}

		private  java.sql.Timestamp  dateAdded =null;
		private String  type ="";   // LONG, SHORT
		
		private Long positionID_tws_sell =null;  // este se controla para saber si la vuelta del TWS, 
										//es una operacion de compra o venta...para tenerlo todo en la misma
		
		private Long realtimeID_buy_alert=null;  // 
		private Long realtimeID_sell_alert=null;  //
		
		
		private Long strategyID_buy = null;  /* USADAS PARA SABER E IDENTIFICAR QUE STRATEGIA SE DISPARO */
		public Long getStrategyID_buy() {
			return strategyID_buy;
		}

		public void setStrategyID_buy(Long strategyID_buy) {
			this.strategyID_buy = strategyID_buy;
		}

		public Long getStrategyID_sell() {
			return strategyID_sell;
		}

		public void setStrategyID_sell(Long strategyID_sell) {
			this.strategyID_sell = strategyID_sell;
		}

		private Long strategyID_sell = null;
		

		public Long getPositionID() {
			return positionID;
		}

		public Long getRealtimeID_buy_alert() {
			return realtimeID_buy_alert;
		}

		public void setRealtimeID_buy_alert(Long realtimeID_buy_alert) {
			this.realtimeID_buy_alert = realtimeID_buy_alert;
		}

		public Long getRealtimeID_sell_alert() {
			return realtimeID_sell_alert;
		}

		public void setRealtimeID_sell_alert(Long realtimeID_sell_alert) {
			this.realtimeID_sell_alert = realtimeID_sell_alert;
		}

		public void setPositionID(Long positionID) {
			this.positionID = positionID;
		}

		public Long getShareID() {
			return shareID;
		}

		public void setShareID(Long shareID) {
			this.shareID = shareID;
		}

		public Long getShare_number() {
			return share_number;
		}

		public void setShare_number(Long share_number) {
			this.share_number = share_number;
		}

		public String getState() {
			return state;
		}

		public void setState(String state) {
			this.state = state;
		}

		public String getState_buy() {
			return state_buy;
		}

		public void setState_buy(String state_buy) {
			this.state_buy = state_buy;
		}

		public String getDescription() {
			return description;
		}

		public void setDescription(String description) {
			this.description = description;
		}

		public Double getPrice_buy() {
			return price_buy;
		}

		public void setPrice_buy(Double price_buy) {
			this.price_buy = price_buy;
		}

		public Double getPrice_real_buy() {
			return price_real_buy;
		}

		public void setPrice_real_buy(Double price_real_buy) {
			this.price_real_buy = price_real_buy;
		}

		public Double getLimit_price_buy() {
			return limit_price_buy;
		}

		public void setLimit_price_buy(Double limit_price_buy) {
			this.limit_price_buy = limit_price_buy;
		}

		public Double getPrice_sell() {
			return price_sell;
		}

		public void setPrice_sell(Double price_sell) {
			this.price_sell = price_sell;
		}

		public Double getPrice_real_sell() {
			return price_real_sell;
		}

		public void setPrice_real_sell(Double price_real_sell) {
			this.price_real_sell = price_real_sell;
		}

		public Double getLimit_price_sell() {
			return limit_price_sell;
		}

		public void setLimit_price_sell(Double limit_price_sell) {
			this.limit_price_sell = limit_price_sell;
		}

		public java.sql.Timestamp getDateAdded() {
			return dateAdded;
		}

		public void setDateAdded(java.sql.Timestamp dateAdded) {
			this.dateAdded = dateAdded;
		}

		public String getType() {
			return type;
		}

		public void setType(String type) {
			this.type = type;
		}

		public Long getPositionID_tws_sell() {
			return positionID_tws_sell;
		}

		public void setPositionID_tws_sell(Long positionID_tws_sell) {
			this.positionID_tws_sell = positionID_tws_sell;
		}

		public String getState_sell() {
			return state_sell;
		}

		public void setState_sell(String state_sell) {
			this.state_sell = state_sell;
		}
		
		/* NO DATABASE DATA, USED TO MAP QUERIES IN THIS BEAN */
		private String shareSymbol=null;  // 
		private String shareName=null;  // 
		public String getShareName() {
			return shareName;
		}

		public void setShareName(String shareName) {
			this.shareName = shareName;
		}

		private Double realtime_value=null;  		
		private String strategyBuy_name=null;
		private String strategySell_name=null;
		private Double sell_percentual_stop_profit=null;
		private Double sell_percentual_stop_lost=null;
		
		private Double sell_price_stop_profit=null;
		private Double sell_price_stop_lost=null;
		
		private String trading_data_operations="";
		
		private Long simulation_mode =null;
		
		
		
		
		
		
		
		
		
		public Long getSimulation_mode() {
			return simulation_mode;
		}

		public void setSimulation_mode(Long simulation_mode) {
			this.simulation_mode = simulation_mode;
		}

		public String getTrading_data_operations() {
			return trading_data_operations;
		}

		public void setTrading_data_operations(String trading_data_operations) {
			this.trading_data_operations = trading_data_operations;
		}

		public Double getSell_price_stop_profit() {
			return sell_price_stop_profit;
		}

		public void setSell_price_stop_profit(Double sell_price_stop_profit) {
			this.sell_price_stop_profit = sell_price_stop_profit;
		}

		public Double getSell_price_stop_lost() {
			return sell_price_stop_lost;
		}

		public void setSell_price_stop_lost(Double sell_price_stop_lost) {
			this.sell_price_stop_lost = sell_price_stop_lost;
		}

		/* FLAG PARA CONTROLAR QUE DEBE SER CANCELADA */
		private Integer pending_cancelled=null;
		
		
		
		
		/* NO DATABASE DATA, USED TO MAP QUERIES IN THIS BEAN */
		
		public Integer getPending_cancelled() {
			return pending_cancelled;
		}

		public void setPending_cancelled(Integer pending_cancelled) {
			this.pending_cancelled = pending_cancelled;
		}

		public Double getSell_percentual_stop_profit() {
			return sell_percentual_stop_profit;
		}

		public void setSell_percentual_stop_profit(Double sell_percentual_stop_profit) {
			this.sell_percentual_stop_profit = sell_percentual_stop_profit;
		}

		public Double getSell_percentual_stop_lost() {
			return sell_percentual_stop_lost;
		}

		public void setSell_percentual_stop_lost(Double sell_percentual_stop_lost) {
			this.sell_percentual_stop_lost = sell_percentual_stop_lost;
		}

		public String getShareSymbol() {
			return shareSymbol;
		}

		public String getStrategyBuy_name() {
			return strategyBuy_name;
		}

		public void setStrategyBuy_name(String strategyBuy_name) {
			this.strategyBuy_name = strategyBuy_name;
		}

		public String getStrategySell_name() {
			return strategySell_name;
		}

		public void setStrategySell_name(String strategySell_name) {
			this.strategySell_name = strategySell_name;
		}

		public void setShareSymbol(String shareSymbol) {
			this.shareSymbol = shareSymbol;
		}

		public Double getRealtime_value() {
			return realtime_value;
		}

		public void setRealtime_value(Double realtime_value) {
			this.realtime_value = realtime_value;
		}

		
		
		//

}
