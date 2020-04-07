package com.byd.sap.modules.job.entity;

public class ScheduleJobLogEntity {
	private Long LOG_ID;
	private Long JOB_ID;
	private String BEANNAME;
	private String METHODNAME;
	private String PARAMS;
	private String STATUS;
	private String ERROR;
	private Long TIMES;
	private String CREATETIME;
	public Long getLOG_ID() {
		return LOG_ID;
	}
	public void setLOG_ID(Long lOG_ID) {
		LOG_ID = lOG_ID;
	}
	public Long getJOB_ID() {
		return JOB_ID;
	}
	public void setJOB_ID(Long jOB_ID) {
		JOB_ID = jOB_ID;
	}
	public String getBEANNAME() {
		return BEANNAME;
	}
	public void setBEANNAME(String bEANNAME) {
		BEANNAME = bEANNAME;
	}
	public String getMETHODNAME() {
		return METHODNAME;
	}
	public void setMETHODNAME(String mETHODNAME) {
		METHODNAME = mETHODNAME;
	}
	public String getPARAMS() {
		return PARAMS;
	}
	public void setPARAMS(String pARAMS) {
		PARAMS = pARAMS;
	}
	public String getSTATUS() {
		return STATUS;
	}
	public void setSTATUS(String sTATUS) {
		STATUS = sTATUS;
	}
	public String getERROR() {
		return ERROR;
	}
	public void setERROR(String eRROR) {
		ERROR = eRROR;
	}
	public Long getTIMES() {
		return TIMES;
	}
	public void setTIMES(Long tIMES) {
		TIMES = tIMES;
	}
	public String getCREATETIME() {
		return CREATETIME;
	}
	public void setCREATETIME(String cREATETIME) {
		CREATETIME = cREATETIME;
	}
	
}
