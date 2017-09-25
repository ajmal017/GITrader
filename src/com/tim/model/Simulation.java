package com.tim.model;


public class Simulation  {
	
    private Long  simulationID = null;    
    private java.sql.Timestamp  dateAdded= null;
    private  java.sql.Timestamp  endDate= null;
    private  java.sql.Timestamp  startDate= null;       
    private  java.sql.Timestamp  nextExecution= null;
    private  java.sql.Timestamp  processedDate= null;
    private  String  status = null;
    private  String  description = null;
    
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public java.sql.Timestamp getNextExecution() {
		return nextExecution;
	}
	public void setNextExecution(java.sql.Timestamp nextExecution) {
		this.nextExecution = nextExecution;
	}
	public java.sql.Timestamp getProcessedDate() {
		return processedDate;
	}
	public void setProcessedDate(java.sql.Timestamp processedDate) {
		this.processedDate = processedDate;
	}
	public Long getSimulationID() {
		return simulationID;
	}
	public void setSimulationID(Long simulationID) {
		this.simulationID = simulationID;
	}
	public java.sql.Timestamp getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(java.sql.Timestamp dateAdded) {
		this.dateAdded = dateAdded;
	}
	public java.sql.Timestamp getEndDate() {
		return endDate;
	}
	public void setEndDate(java.sql.Timestamp endDate) {
		this.endDate = endDate;
	}
	public java.sql.Timestamp getStartDate() {
		return startDate;
	}
	public void setStartDate(java.sql.Timestamp startDate) {
		this.startDate = startDate;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}    
       
    
        
        
    
}