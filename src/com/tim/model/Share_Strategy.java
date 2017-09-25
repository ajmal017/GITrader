package com.tim.model;

import java.sql.Timestamp;

public class Share_Strategy {
	public Long getShareId() {
		return ShareId;
	}
	public void setShareId(Long shareId) {
		ShareId = shareId;
	}
	public Long getStrategyId() {
		return StrategyId;
	}
	public void setStrategyId(Long strategyId) {
		StrategyId = strategyId;
	}
	public Timestamp getModified_Date() {
		return Modified_Date;
	}
	public void setModified_Date(Timestamp modified_Date) {
		Modified_Date = modified_Date;
	}
	Long  ShareId = null;
	Long  StrategyId = null;
	Timestamp  Modified_Date = null;
	
    
}
