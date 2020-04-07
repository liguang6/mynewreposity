package com.byd.wms.business.modules.qc.enums;

/**
 * 行项目状态 字典定义：（00未质检，01完成，02关闭）
 * @author develop07
 *
 */
public enum InspectionItemStatus {
	FINISHED("01"),INIT("00"),CLOSED("02");
	private String code;
	private InspectionItemStatus(String code){
		this.code = code;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
}
