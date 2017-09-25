package com.tim.model;

import java.sql.Timestamp;


public class  Simulation_Share {
	
   
	private Long  simulationID = null;
    private     Long  shareId = null;    
    private     Timestamp  lastProcessed = null;
    
    
	
	public Timestamp getLastProcessed() {
		return lastProcessed;
	}
	public void setLastProcessed(Timestamp lastProcessed) {
		this.lastProcessed = lastProcessed;
	}
	public Long getSimulationID() {
		return simulationID;
	}
	public void setSimulationID(Long simulationID) {
		this.simulationID = simulationID;
	}
	public Long getShareId() {
		return shareId;
	}
	public void setShareId(Long shareId) {
		this.shareId = shareId;
	}
	
	       
}
