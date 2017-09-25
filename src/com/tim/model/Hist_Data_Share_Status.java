package com.tim.model;

import java.sql.Timestamp;


public class  Hist_Data_Share_Status {
	
   
	private Long  simulationID = null;
    private     Long  shareId = null;    
    private     Timestamp  processedDate = null;
    private     Timestamp  nextExecution = null;
    
	public Timestamp getNextExecution() {
		return nextExecution;
	}
	public void setNextExecution(Timestamp nextExecution) {
		this.nextExecution = nextExecution;
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
	 public Timestamp getProcessedDate() {
			return processedDate;
		}
		public void setProcessedDate(Timestamp processedDate) {
			this.processedDate = processedDate;
		}
	       
}
