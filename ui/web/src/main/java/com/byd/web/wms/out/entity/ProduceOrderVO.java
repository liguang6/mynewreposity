package com.byd.web.wms.out.entity;

public class ProduceOrderVO {
	
	private String orderNo;
	
	private Double requireSuitQty;
	
	private String mat;
	
	private Double qty;
	
	private String location;
	
	private String station;
	private String requireDate;
	private String requireTime;
	private String receiver;
	
	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	
	public String getRequireDate() {
		return requireDate;
	}

	public void setRequireDate(String requireDate) {
		this.requireDate = requireDate;
	}

	public String getRequireTime() {
		return requireTime;
	}

	public void setRequireTime(String requireTime) {
		this.requireTime = requireTime;
	}

	public String getOrderNo() {
		return orderNo;
	}

	public void setOrderNo(String orderNo) {
		this.orderNo = orderNo;
	}

	public Double getRequireSuitQty() {
		return requireSuitQty;
	}

	public void setRequireSuitQty(Double requireSuitQty) {
		this.requireSuitQty = requireSuitQty;
	}

	public String getMat() {
		return mat;
	}

	public void setMat(String mat) {
		this.mat = mat;
	}

	public Double getQty() {
		return qty;
	}

	public void setQty(Double qty) {
		this.qty = qty;
	}

	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	public String getStation() {
		return station;
	}

	public void setStation(String station) {
		this.station = station;
	}
	
	
	

}
