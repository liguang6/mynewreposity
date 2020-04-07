package com.byd.wms.business.modules.common.enums;

public enum WmsDocTypeEnum {
	//检验单,，，
	QC_INSPECT("03"),
	//检验记录
	QC_RECORD("04"),
	//检验结果
	QC_RESULT("05"),
	//出库需求
	OUT_WAREHOURSE("09"),
	//出库拣配单
	OUT_PICKING("11"),
	//sto送货单
	OUT_DN("16");
	
	private String code;
	private WmsDocTypeEnum(String code){
		this.setCode(code);
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}

}
