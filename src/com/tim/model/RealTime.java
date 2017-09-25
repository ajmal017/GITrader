package com.tim.model;


public class RealTime  {



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
    Double max_value= null;
    public Double getVolume() {
		return volume;
	}
	public void setVolume(Double volume) {
		this.volume = volume;
	}
	public Double getAvg_volumen() {
		return avg_volume;
	}
	public void setAvg_volumen(Double avg_volumen) {
		this.avg_volume = avg_volumen;
	}
	Double min_value= null;
    Double volume= null;
    Double avg_volume= null;
    
    public Double getMax_value() {
		return max_value;
	}
	public void setMax_value(Double max_value) {
		this.max_value = max_value;
	}
	public Double getMin_value() {
		return min_value;
	}
	public void setMin_value(Double min_value) {
		this.min_value = min_value;
	}
	java.sql.Timestamp dateAdded = null;
	
	/* comodines para las consultas */
	java.sql.Timestamp _FirstTradingDate = null;
	public Double getAvg_volume() {
		return avg_volume;
	}
	public void setAvg_volume(Double avg_volume) {
		this.avg_volume = avg_volume;
	}
	public java.sql.Timestamp getDateAdded() {
		return dateAdded;
	}
	public void setDateAdded(java.sql.Timestamp dateAdded) {
		this.dateAdded = dateAdded;
	}
	public java.sql.Timestamp get_FirstTradingDate() {
		return _FirstTradingDate;
	}
	public void set_FirstTradingDate(java.sql.Timestamp _FirstTradingDate) {
		this._FirstTradingDate = _FirstTradingDate;
	}
	public java.sql.Timestamp get_LastTradingDate() {
		return _LastTradingDate;
	}
	public void set_LastTradingDate(java.sql.Timestamp _LastTradingDate) {
		this._LastTradingDate = _LastTradingDate;
	}
	java.sql.Timestamp _LastTradingDate = null;
	
	
   
    

}