package com.byd.wms.business.modules.out.enums;

public enum RequirementTypeEnum {
	TAKE_MATERIALS("00"),//领料需求
	OUT_WEARHOURSE("01"),//出库需求
	DISTRIBUTION("02");//配送需求
	
	private String code;
	private RequirementTypeEnum(String code){
		this.setCode(code);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
