package com.tim.model;


public class SimulationRealTime  {



    public Long getRealtimeID() {
		return realtimeID;
	}
	public void setRealtimeID(Long realtimeID) {
		this.realtimeID = realtimeID;
	}
	public Long getShareID() {
		return shareID;
	}
	public void setShareID(Long shareID) {
		this.shareID = shareID;
	}
	public Double getValue() {
		return value;
	}
	public void setValue(Double value) {
		this.value = value;
	}
	public java.sql.Timestamp getAddedDate() {
		return dateAdded;
	}
	public void setAddedDate(java.sql.Timestamp addedDate) {
		this.dateAdded = addedDate;
	}
	Long  realtimeID = null;
    Long  shareID = null;	
    Double value= null;
   
	java.sql.Timestamp dateAdded = null;
	
	
	
	
   
    

}