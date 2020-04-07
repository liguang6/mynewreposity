package com.byd.wms.business.modules.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

@TableName("WMS_C_VENDOR")
@KeySequence("SEQ_WMS_C_VENDOR")//使用oracle 注解自增
public class WmsCVendor {
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	/*
	 * 工厂代码 
	 */
	private String werks;
	/*
	 * 工厂名称
	 */
	private String werksName;
	/*
	 * 供应商代码
	 */
	private String lifnr;
	/*
	 * 供应商名称1
	 */
	private String name1;
	/*
	 * 是否已上SCM 空 否 X是 默认空
	 */
	private String isScm;
	/*
	 * 供应商管理者（采购员）
	 */
	private String vendorManager;
	/*
	 * 供应商简称
	 */
	private String shortName;
	/*
	 * 删除标示 空 否 X是 默认空
	 */
	private String delFlag;
	/*
	 * 修改人
	 */
	private String editor;
	/*
	 * 修改时间
	 */
	private String editDate;
	
	@TableField(exist=false)
	private String msg;
	@TableField(exist=false)
	private String rowNo;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getWerks() {
		return werks;
	}
	public void setWerks(String werks) {
		this.werks = werks;
	}
	public String getWerksName() {
		return werksName;
	}
	public void setWerksName(String werksName) {
		this.werksName = werksName;
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
	public String getIsScm() {
		return isScm;
	}
	public void setIsScm(String isScm) {
		this.isScm = isScm;
	}
	public String getVendorManager() {
		return vendorManager;
	}
	public void setVendorManager(String vendorManager) {
		this.vendorManager = vendorManager;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
	}
	public String getEditor() {
		return editor;
	}
	public void setEditor(String editor) {
		this.editor = editor;
	}
	public String getEditDate() {
		return editDate;
	}
	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getRowNo() {
		return rowNo;
	}
	public void setRowNo(String rowNo) {
		this.rowNo = rowNo;
	}
	
	
}
