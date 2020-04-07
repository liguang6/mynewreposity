package com.byd.sap.modules.job.entity;

public class SapPoHeadEntity {
	private String id;
	/*
	 * 采购订单号 EBELN
	 */
	private String EBELN;
	/*
	 * 采购凭证类别 BSTYP F 采购订单 A询价 WMS主要同步F类
	 */
	private String BSTYP;
	/*
	 * 采购凭证类型 BSART 采购订单类型 如QH00 前海采购订单
	 */
	private String BSART;
	/*
	 * 公司代码 BUKRS
	 */
	private String BUKRS;
	/*
	 * 采购组织 EKORG
	 */
	private String EKORG;
	/*
	 * 采购组 EKGRP
	 */
	private String EKGRP;
	/*
	 * 供应商代码 LIFNR
	 */
	private String LIFNR;
	/*
	 * 凭证创建日期 AEDAT
	 */
	private String AEDAT;
	/*
	 * 批准尚未生效 X是 未批准 空 否已批准
	 */
	private String FRGRL;
	/*
	 * 凭证日期 BEDAT
	 */
	private String BEDAT;
	/*
	 * 导入日期 数据导入日期 取系统时间
	 */
	private String IMPORT_DATE;
	/*
	 * VERIFY
	 */
	private String UPDATE_DATE;
	/*
	 * 审批组
	 */
	private String FRGGR;
	/*
	 * 审批策略
	 */
	private String FRGSX;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEBELN() {
		return EBELN;
	}
	public void setEBELN(String eBELN) {
		EBELN = eBELN;
	}
	public String getBSTYP() {
		return BSTYP;
	}
	public void setBSTYP(String bSTYP) {
		BSTYP = bSTYP;
	}
	public String getBSART() {
		return BSART;
	}
	public void setBSART(String bSART) {
		BSART = bSART;
	}
	public String getBUKRS() {
		return BUKRS;
	}
	public void setBUKRS(String bUKRS) {
		BUKRS = bUKRS;
	}
	public String getEKORG() {
		return EKORG;
	}
	public void setEKORG(String eKORG) {
		EKORG = eKORG;
	}
	public String getEKGRP() {
		return EKGRP;
	}
	public void setEKGRP(String eKGRP) {
		EKGRP = eKGRP;
	}
	public String getLIFNR() {
		return LIFNR;
	}
	public void setLIFNR(String lIFNR) {
		LIFNR = lIFNR;
	}
	public String getAEDAT() {
		return AEDAT;
	}
	public void setAEDAT(String aEDAT) {
		AEDAT = aEDAT;
	}
	public String getFRGRL() {
		return FRGRL;
	}
	public void setFRGRL(String fRGRL) {
		FRGRL = fRGRL;
	}
	public String getBEDAT() {
		return BEDAT;
	}
	public void setBEDAT(String bEDAT) {
		BEDAT = bEDAT;
	}
	public String getIMPORT_DATE() {
		return IMPORT_DATE;
	}
	public void setIMPORT_DATE(String iMPORT_DATE) {
		IMPORT_DATE = iMPORT_DATE;
	}
	public String getUPDATE_DATE() {
		return UPDATE_DATE;
	}
	public void setUPDATE_DATE(String uPDATE_DATE) {
		UPDATE_DATE = uPDATE_DATE;
	}
	public String getFRGGR() {
		return FRGGR;
	}
	public void setFRGGR(String fRGGR) {
		FRGGR = fRGGR;
	}
	public String getFRGSX() {
		return FRGSX;
	}
	public void setFRGSX(String fRGSX) {
		FRGSX = fRGSX;
	}
	
	
}
