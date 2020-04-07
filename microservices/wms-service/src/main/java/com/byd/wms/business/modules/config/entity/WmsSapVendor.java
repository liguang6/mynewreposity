package com.byd.wms.business.modules.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.util.Date;

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

	/*
	 * 供应商英文名称2
	 */
	private String name2;

	/*
	 * 同步时间
	 */
	private Date importDate;
	
	
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

	public String getName2() {
		return name2;
	}

	public void setName2(String name2) {
		this.name2 = name2;
	}

	public Date getImportDate() {
		return importDate;
	}

	public void setImportDate(Date importDate) {
		this.importDate = importDate;
	}
}
