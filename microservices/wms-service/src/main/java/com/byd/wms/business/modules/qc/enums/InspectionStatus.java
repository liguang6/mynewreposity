package com.byd.wms.business.modules.qc.enums;

public enum InspectionStatus {
	CREATED("00"),PART_COMPLETE("01"),COMPLETED("02"),CLOSED("03");
	private String code;
	
	private InspectionStatus(String code){
		this.code = code;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	
}
