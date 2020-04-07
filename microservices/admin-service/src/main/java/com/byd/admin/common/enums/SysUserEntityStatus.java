package com.byd.admin.common.enums;

public enum SysUserEntityStatus {
	
	ENABLED(1)/*有效*/,DISABLED(0)/*无效*/;
	
	private Integer status;
	private SysUserEntityStatus(Integer status){
	  	this.status = status;
	}
	public Integer getStatus() {
		return status;
	}
	public void setStatus(Integer status) {
		this.status = status;
	}
	

}
