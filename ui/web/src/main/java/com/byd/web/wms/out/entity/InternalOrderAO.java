package com.byd.web.wms.out.entity;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

public class InternalOrderAO {

	
	private String werks;
	
	private String whNumber;
	
	private String requirementType;
	
	private String requirementDate;
	
	private String companyCode;
	
	private String internalOrder;
	private String autyp;//AUTYP
	
	private String use;

	private String requireDate;
	private String requireTime;
	@JsonProperty("VENDOR")
	private String VENDOR;
	@JsonProperty("STATION")
	private String STATION;

	@Override
	public String toString() {
		return "InternalOrderAO{" +
				"werks='" + werks + '\'' +
				", whNumber='" + whNumber + '\'' +
				", requirementType='" + requirementType + '\'' +
				", requirementDate='" + requirementDate + '\'' +
				", companyCode='" + companyCode + '\'' +
				", internalOrder='" + internalOrder + '\'' +
				", use='" + use + '\'' +
				", requireDate='" + requireDate + '\'' +
				", requireTime='" + requireTime + '\'' +
				", VENDOR='" + VENDOR + '\'' +
				", STATION='" + STATION + '\'' +
				", MATNR='" + MATNR + '\'' +
				", MAKTX='" + MAKTX + '\'' +
				", MEINS='" + MEINS + '\'' +
				", REQ_QTY=" + REQ_QTY +
				", LIFNR='" + LIFNR + '\'' +
				", LGORT='" + LGORT + '\'' +
				", TOTAL_STOCK_QTY='" + TOTAL_STOCK_QTY + '\'' +
				", MEMO='" + MEMO + '\'' +
				", ZZKM='" + ZZKM + '\'' +
				", messages=" + messages +
				'}';
	}

	public String getVENDOR() {
		return VENDOR;
	}

	public void setVENDOR(String VENDOR) {
		this.VENDOR = VENDOR;
	}

	public String getSTATION() {
		return STATION;
	}

	public void setSTATION(String STATION) {
		this.STATION = STATION;
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

	@JsonProperty("MATNR")
	private String MATNR;
	
	@JsonProperty("MAKTX")
	private String MAKTX;
	
	@JsonProperty("MEINS")
	private String MEINS;
	
	@JsonProperty("REQ_QTY")
	private Double REQ_QTY;
	
	@JsonProperty("LIFNR")
	private String LIFNR;
	
	@JsonProperty("LGORT")
	private String LGORT;
	
	@JsonProperty("TOTAL_STOCK_QTY")
	private String TOTAL_STOCK_QTY;
	
	@JsonProperty("MEMO")
	private String MEMO;
	
	@JsonProperty("ZZKM")
	private String ZZKM;
	@JsonProperty("receiver")
	private String receiver;
	
	private List<String> messages;

	

	public String getMATNR() {
		return MATNR;
	}

	public void setMATNR(String mATNR) {
		MATNR = mATNR;
	}

	public String getMAKTX() {
		return MAKTX;
	}

	public void setMAKTX(String mAKTX) {
		MAKTX = mAKTX;
	}

	public String getMEINS() {
		return MEINS;
	}

	public void setMEINS(String mEINS) {
		MEINS = mEINS;
	}

	public Double getREQ_QTY() {
		return REQ_QTY;
	}

	public void setREQ_QTY(Double rEQ_QTY) {
		REQ_QTY = rEQ_QTY;
	}

	public String getLIFNR() {
		return LIFNR;
	}

	public void setLIFNR(String lIFNR) {
		LIFNR = lIFNR;
	}

	public String getLGORT() {
		return LGORT;
	}

	public void setLGORT(String lGORT) {
		LGORT = lGORT;
	}

	public String getTOTAL_STOCK_QTY() {
		return TOTAL_STOCK_QTY;
	}

	public void setTOTAL_STOCK_QTY(String tOTAL_STOCK_QTY) {
		TOTAL_STOCK_QTY = tOTAL_STOCK_QTY;
	}

	public String getMEMO() {
		return MEMO;
	}

	public void setMEMO(String mEMO) {
		MEMO = mEMO;
	}

	public String getZZKM() {
		return ZZKM;
	}

	public void setZZKM(String zZKM) {
		ZZKM = zZKM;
	}

	public String getWerks() {
		return werks;
	}

	public void setWerks(String werks) {
		this.werks = werks;
	}

	public String getWhNumber() {
		return whNumber;
	}

	public void setWhNumber(String whNumber) {
		this.whNumber = whNumber;
	}

	public String getRequirementType() {
		return requirementType;
	}

	public void setRequirementType(String requirementType) {
		this.requirementType = requirementType;
	}

	public String getRequirementDate() {
		return requirementDate;
	}

	public void setRequirementDate(String requirementDate) {
		this.requirementDate = requirementDate;
	}

	public String getCompanyCode() {
		return companyCode;
	}

	public void setCompanyCode(String companyCode) {
		this.companyCode = companyCode;
	}

	public String getInternalOrder() {
		return internalOrder;
	}

	public void setInternalOrder(String internalOrder) {
		this.internalOrder = internalOrder;
	}

	public String getUse() {
		return use;
	}

	public void setUse(String use) {
		this.use = use;
	}

	public List<String> getMessages() {
		return messages;
	}

	public void setMessages(List<String> messages) {
		this.messages = messages;
	}

	public String getAutyp() {
		return autyp;
	}

	public void setAutyp(String autyp) {
		this.autyp = autyp;
	}
	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
}
