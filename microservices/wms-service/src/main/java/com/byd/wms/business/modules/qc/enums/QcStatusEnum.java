package com.byd.wms.business.modules.qc.enums;
/**
 * 质检状态
 * WAIT_INSPECT：未质检
 * ON_INSPECT：质检中
 * FINISH_INSPECT： 已质检
 * @author develop07
 *
 */
public enum QcStatusEnum {
	WAIT_INSPECT("00"),ON_INSPECT("01"),FINISH_INSPECT("02");
	
	private String code;
	
	private QcStatusEnum(String code){
		this.setCode(code);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	

}
