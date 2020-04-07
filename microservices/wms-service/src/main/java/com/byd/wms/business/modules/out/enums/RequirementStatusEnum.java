package com.byd.wms.business.modules.out.enums;

public enum RequirementStatusEnum {
	CREATED("00"),APPROVED("01"),PREPARE_FEED("02"),HANDED_OVER("03"),CLOSE("04");
	
	private String code;
	private RequirementStatusEnum(String code){
		this.setCode(code);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
}
