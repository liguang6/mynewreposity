package com.byd.wms.webservice.ws.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2019年3月4日 下午2:04:26 
 * 类说明 
 */
@TableName("WMS_C_WH")
@KeySequence("SEQ_WMS_C_WH")
public class WmsCWhEntity implements Serializable {
private static final long serialVersionUID = 1L;
	
	@TableId(value = "ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 工厂代码 
	 */
	private String werks;
	/**
	 * 仓库号
	 */
	private String whNumber;
	
	@TableField(exist=false)
	private String werksName;
	/**
	 * 库存地点 可为空
	 */
	//private String lgort;
	/**
	 * 是否启用供应商管理标识 0 X 启用 默认0
	 */
	private String vendorFlag;
	/**
	 * 是否启用智能储位 0 X 启用 默认0
	 */
	private String igFlag;
	/**
	 * 是否已上WMS 0 X 启用 默认0
	 */
	private String wmsFlag;
	/**
	 * 是否启用核销业务 0 X 启用 默认0
	 */
	private String hxFlag;
	/**
	 * 是否启用最小包装管理 0 X 启用 默认0
	 */
	private String packageFlag;
	/**
	 * PDA拣配确认标识 0 X 启用 默认0
	 */
	//private String pdaPickFlag;
	/**
	 * 是否启用条码管理 0 X 启用 默认0
	 */
	private String barcodeFlag;
	/**
	 * 是否启用预留 0 X 启用 默认0
	 */
	private String resbdFlag;
	/**
	 * 是否启用费用性订单库存管理 0 X 启用 默认0
	 */
	private String cmmsFlag;
	/*
	 * 更新人
	 */
	private String editor;
	/*
	 * 更新时间
	 */
	private String editorDate;
	
	private String delFlag;
	
	/*
	 * 是否校验人料关系 0 X 启用 默认0
	 */
	private String matManagerFlag;
	
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

	public String getFreezepostsapflag() {
		return freezepostsapflag;
	}
	public void setFreezepostsapflag(String freezepostsapflag) {
		this.freezepostsapflag = freezepostsapflag;
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
	public String getWhNumber() {
		return whNumber;
	}
	public void setWhNumber(String whNumber) {
		this.whNumber = whNumber;
	}
	/*public String getLgort() {
		return lgort;
	}
	public void setLgort(String lgort) {
		this.lgort = lgort;
	}*/
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
	public String getPackageFlag() {
		return packageFlag;
	}
	public void setPackageFlag(String packageFlag) {
		this.packageFlag = packageFlag;
	}
	/*public String getPdaPickFlag() {
		return pdaPickFlag;
	}
	public void setPdaPickFlag(String pdaPickFlag) {
		this.pdaPickFlag = pdaPickFlag;
	}*/
	public String getBarcodeFlag() {
		return barcodeFlag;
	}
	public void setBarcodeFlag(String barcodeFlag) {
		this.barcodeFlag = barcodeFlag;
	}
	public String getResbdFlag() {
		return resbdFlag;
	}
	public void setResbdFlag(String resbdFlag) {
		this.resbdFlag = resbdFlag;
	}
	public String getCmmsFlag() {
		return cmmsFlag;
	}
	public void setCmmsFlag(String cmmsFlag) {
		this.cmmsFlag = cmmsFlag;
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
	public void setEditorDate(String editorDate) {
		this.editorDate = editorDate;
	}
	public String getWerksName() {
		return werksName;
	}
	public void setWerksName(String werksName) {
		this.werksName = werksName;
	}
	public String getDelFlag() {
		return delFlag;
	}
	public void setDelFlag(String delFlag) {
		this.delFlag = delFlag;
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
