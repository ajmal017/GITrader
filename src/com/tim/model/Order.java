package com.tim.model;


public class Order  {
	
    Long  shareID = null;
    Long  orderID = null;
    Long  checked = null;
	public Long getOrderID() {
		return orderID;
	}
	public Long getChecked() {
		return checked;
	}
	public void setChecked(Long checked) {
		this.checked = checked;
	}
	public void setOrderID(Long orderID) {
		this.orderID = orderID;
	}
	public Long getShareID() {
		return shareID;
	}
	public void setShareID(Long shareID) {
		this.shareID = shareID;
	}
        
}