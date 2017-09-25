package com.tim.model;

public class Trading_Market {
	Long  tradingId = null;
    public Long getTradingId() {
		return tradingId;
	}
	public void setTradingId(Long tradingId) {
		this.tradingId = tradingId;
	}
	public Long getMarketId() {
		return marketId;
	}
	public void setMarketId(Long marketId) {
		this.marketId = marketId;
	}
	Long  marketId = null;
	public Long getShareId() {
		return shareId;
	}
	public void setShareId(Long shareId) {
		this.shareId = shareId;
	}
	Long  shareId = null;
}
