package com.byd.web.wms.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

@TableName("WMS_SAP_VENDOR")
@KeySequence("SEQ_WMS_SAP_VENDOR")//使用oracle 注解自增
public class WmsSapVendor {
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	
	
	/*
	 * 供应商代码
	 */
	private String lifnr;
	/*
	 * 供应商名称1
	 */
	private String name1;
	
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	
	public String getLifnr() {
		return lifnr;
	}
	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
	}
	public String getName1() {
		return name1;
	}
	public void setName1(String name1) {
		this.name1 = name1;
	}
	
	
	
	
}
