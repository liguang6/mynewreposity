package com.byd.web.wms.out.entity;

import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 创建生产订单领料需求 Application Object
 * @author develop07
 *
 */
public class CreateProduceOrderAO {
	private String werks;
	private String whNumber;
	private String requireDate;
	private String requireTime;
	private String summaryMode;
	private String use;
	
	private String AUFNR;//内部订单号
	private String POSNR;//订单行项目号
	private String RSNUM;//预留号
	private String RSPOS;//预留行项目号
	private String MATNR;//物料号
	private String MAKTX;//物料描述
	private String MEINS;//单位
	private Double BDMNG;//订单需求
	private Double TL_QTY;//已领数量
	private Double KL_QTY;//可领数量
	private Double REQ_QTY;//需求数量
	private String LGORT;//库位
	private String VENDOR;//供应商代码
	private String SOBKZ;//特殊库存标识
	private Double TOTAL_STOCK_QTY;//库存
	private String LINE;//产线
	private String STATION;//工位
	private Double HX_QTY;//剩余核销数量
	private String CREATOR;
	private String CREATE_DATE;

	private String aceptLgortList;
	private String requireTypes;
	
	private String receiver;
	
	public String getReceiver() {
		return receiver;
	}

	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	public String getAceptLgortList() {
		return aceptLgortList;
	}

	public void setAceptLgortList(String aceptLgortList) {
		this.aceptLgortList = aceptLgortList;
	}

	public String getRequireTypes() {
		return requireTypes;
	}

	public void setRequireTypes(String requireTypes) {
		this.requireTypes = requireTypes;
	}

	@Override
	public String toString() {
		return "CreateProduceOrderAO{" +
				"werks='" + werks + '\'' +
				", whNumber='" + whNumber + '\'' +
				", requireDate='" + requireDate + '\'' +
				", requireTime='" + requireTime + '\'' +
				", summaryMode='" + summaryMode + '\'' +
				", use='" + use + '\'' +
				", AUFNR='" + AUFNR + '\'' +
				", POSNR='" + POSNR + '\'' +
				", RSNUM='" + RSNUM + '\'' +
				", RSPOS='" + RSPOS + '\'' +
				", MATNR='" + MATNR + '\'' +
				", MAKTX='" + MAKTX + '\'' +
				", MEINS='" + MEINS + '\'' +
				", BDMNG=" + BDMNG +
				", TL_QTY=" + TL_QTY +
				", KL_QTY=" + KL_QTY +
				", REQ_QTY=" + REQ_QTY +
				", LGORT='" + LGORT + '\'' +
				", VENDOR='" + VENDOR + '\'' +
				", SOBKZ='" + SOBKZ + '\'' +
				", TOTAL_STOCK_QTY=" + TOTAL_STOCK_QTY +
				", LINE='" + LINE + '\'' +
				", STATION='" + STATION + '\'' +
				", HX_QTY=" + HX_QTY +
				", CREATOR='" + CREATOR + '\'' +
				", CREATE_DATE='" + CREATE_DATE + '\'' +
				", aceptLgortList='" + aceptLgortList + '\'' +
				", requireTypes='" + requireTypes + '\'' +
				", receiver='" + receiver + '\'' +
				'}';
	}

	@JsonProperty(value="AUFNR")
	public String getAUFNR() {
		return AUFNR;
	}
	public void setAUFNR(String aUFNR) {
		AUFNR = aUFNR;
	}
	@JsonProperty(value="MATNR")
	public String getMATNR() {
		return MATNR;
	}
	public void setMATNR(String mATNR) {
		MATNR = mATNR;
	}
	@JsonProperty(value="MAKTX")
	public String getMAKTX() {
		return MAKTX;
	}
	public void setMAKTX(String mAKTX) {
		MAKTX = mAKTX;
	}
	@JsonProperty(value="MEINS")
	public String getMEINS() {
		return MEINS;
	}
	public void setMEINS(String mEINS) {
		MEINS = mEINS;
	}
	@JsonProperty(value="BDMNG")
	public Double getBDMNG() {
		return BDMNG;
	}
	public void setBDMNG(Double bDMNG) {
		BDMNG = bDMNG;
	}
	@JsonProperty(value="TL_QTY")
	public Double getTL_QTY() {
		return TL_QTY;
	}
	public void setTL_QTY(Double tL_QTY) {
		TL_QTY = tL_QTY;
	}
	@JsonProperty(value="KL_QTY")
	public Double getKL_QTY() {
		return KL_QTY;
	}
	public void setKL_QTY(Double kL_QTY) {
		KL_QTY = kL_QTY;
	}
	@JsonProperty(value="REQ_QTY")
	public Double getREQ_QTY() {
		return REQ_QTY;
	}
	public void setREQ_QTY(Double rEQ_QTY) {
		REQ_QTY = rEQ_QTY;
	}
	@JsonProperty(value="LGORT")
	public String getLGORT() {
		return LGORT;
	}
	public void setLGORT(String lGORT) {
		LGORT = lGORT;
	}
	@JsonProperty(value="VENDOR")
	public String getVENDOR() {
		return VENDOR;
	}
	public void setVENDOR(String vENDOR) {
		VENDOR = vENDOR;
	}
	@JsonProperty(value="SOBKZ")
	public String getSOBKZ() {
		return SOBKZ;
	}
	public void setSOBKZ(String sOBKZ) {
		SOBKZ = sOBKZ;
	}
	@JsonProperty(value="TOTAL_STOCK_QTY")
	public Double getTOTAL_STOCK_QTY() {
		return TOTAL_STOCK_QTY;
	}
	public void setTOTAL_STOCK_QTY(Double tOTAL_STOCK_QTY) {
		TOTAL_STOCK_QTY = tOTAL_STOCK_QTY;
	}
	@JsonProperty(value="LINE")
	public String getLINE() {
		return LINE;
	}
	public void setLINE(String lINE) {
		LINE = lINE;
	}
	@JsonProperty(value="STATION")
	public String getSTATION() {
		return STATION;
	}
	public void setSTATION(String sTATION) {
		STATION = sTATION;
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
	public String getSummaryMode() {
		return summaryMode;
	}
	public void setSummaryMode(String summaryMode) {
		this.summaryMode = summaryMode;
	}
	public String getUse() {
		return use;
	}
	public void setUse(String use) {
		this.use = use;
	}
	@JsonProperty(value="HX_QTY")
	public Double getHX_QTY() {
		return HX_QTY;
	}
	public void setHX_QTY(Double hX_QTY) {
		HX_QTY = hX_QTY;
	}
	
	@JsonProperty(value="POSNR")
	public String getPOSNR() {
		return POSNR;
	}
	public void setPOSNR(String pOSNR) {
		POSNR = pOSNR;
	}
	@JsonProperty(value="RSNUM")
	public String getRSNUM() {
		return RSNUM;
	}
	public void setRSNUM(String rSNUM) {
		RSNUM = rSNUM;
	}
	@JsonProperty(value="RSPOS")
	public String getRSPOS() {
		return RSPOS;
	}
	public void setRSPOS(String rSPOS) {
		RSPOS = rSPOS;
	}
	public String getCREATOR() {
		return CREATOR;
	}
	public void setCREATOR(String cREATOR) {
		CREATOR = cREATOR;
	}
	public String getCREATE_DATE() {
		return CREATE_DATE;
	}
	public void setCREATE_DATE(String cREATE_DATE) {
		CREATE_DATE = cREATE_DATE;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		return super.clone();
	}
}
