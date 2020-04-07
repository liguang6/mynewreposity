package com.byd.wms.business.modules.qc.enums;

public enum QcRecordType {
	INIT("01"),RE_JUDGE("02");
	private String code;
	private QcRecordType(String code){
		this.code = code;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
