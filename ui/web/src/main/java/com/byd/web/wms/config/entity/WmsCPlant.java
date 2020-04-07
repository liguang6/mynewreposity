package com.byd.web.wms.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

@TableName("WMS_C_WH")
@KeySequence("SEQ_WMS_C_WH")//使用oracle 注解自增
public class WmsCPlant {
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	/*
	 * 工厂代码 plant_code
	 */
	private String werks;
	/*
	 * 是否启用供应商管理标识 空 否 X 启用 默认空
	 */
	private String vendorFlag;
	/*
	 * 是否启用智能储位 空 否 X 启用 默认空
	 */
	private String igFlag;
	/*
	 * 是否已上WMS 空 否 X 启用 默认空
	 */
	private String wmsFlag;
	/*
	 * 是否启用核销业务 空 否 X 启用 默认空
	 */
	private String hxFlag;
	/*
	 * 是否启用最小包装管理 0 否 X 启用 默认0 PACKAGE_FLAG
	 */
	private String packageFlag;
	/*
	 * 更新人
	 */
	private String editor;
	/*
	 * 更新时间
	 */
	private String editorDate;
	
	/*
	 * 是否校验人料关系 0 X 启用 默认0
	 */
	private String matManagerFlag;
	
	/*
	 * 删除标示 空 否 X是 默认空
	 */
	@TableField("del_flag")
	@TableLogic
	private String delFlag;
	@TableField(exist=false)
	private String msg;
	@TableField(exist=false)
	private String rowNo;

	/**
	 * 是否启用保质期 0 否 X 是 默认 0
	 */
	private String prfrqflag;
	
	/**
	 * 冻结/解冻是否启用SAP过账 0 否 X 是 默认 0
	 */
	private String freezepostsapflag;

	public Long getId() {
		return id;
	}
	public String getFreezepostsapflag() {
		return freezepostsapflag;
	}
	public void setFreezepostsapflag(String freezepostsapflag) {
		this.freezepostsapflag = freezepostsapflag;
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
	public String getVendorFlag() {
		return vendorFlag;
	}
	public void setVendorFlag(String vendorFlag) {
		this.vendorFlag = vendorFlag;
	}
	public String getIgFlag() {
		return igFlag;
	}
	public void setIgFlag(String igFlag) {
		this.igFlag = igFlag;
	}
	public String getWmsFlag() {
		return wmsFlag;
	}
	public void setWmsFlag(String wmsFlag) {
		this.wmsFlag = wmsFlag;
	}
	public String getHxFlag() {
		return hxFlag;
	}
	public void setHxFlag(String hxFlag) {
		this.hxFlag = hxFlag;
	}
	
	public String getEditor() {
		return editor;
	}
	public void setEditor(String editor) {
		this.editor = editor;
	}
	public String getEditorDate() {
		return editorDate;
	}
	public void setEditorDate(String editDate) {
		this.editorDate = editDate;
	}
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
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
	public String getPackageFlag() {
		return packageFlag;
	}
	public void setPackageFlag(String packageFlag) {
		this.packageFlag = packageFlag;
	}
	public String getMatManagerFlag() {
		return matManagerFlag;
	}
	public void setMatManagerFlag(String matManagerFlag) {
		this.matManagerFlag = matManagerFlag;
	}

	public String getPrfrqflag() {
		return prfrqflag;
	}

	public void setPrfrqflag(String prfrqflag) {
		this.prfrqflag = prfrqflag;
	}

}
