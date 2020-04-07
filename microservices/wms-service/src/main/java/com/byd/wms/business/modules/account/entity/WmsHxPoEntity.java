package com.byd.wms.business.modules.account.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * 核销业务：SAP采购订单核销实体类
 * @author (changsha) byd_infomation_center
 * @date 2018-09-11
 */
@TableName("WMS_HX_PO")
@KeySequence("SEQ_WMS_HX_PO")
public class WmsHxPoEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	
	/**
	 * 收货工厂代码
	 */
	private String werks;
	/**
	 * WMS仓库号 WH_NUMBER
	 */
	private String whNumber;
	/**
	 * 供应商代码 LIFNR
	 */
	private String lifnr;
	/**
	 * 采购订单号 EBELN
	 */
	private String ebeln;
	/**
	 * 采购订单行项目号 EBELP
	 */
	private String ebelp;

	/**
	 * 收货库位
	 */
	private String lgort;
	/**
	 * 物料号
	 */
	private String matnr;
	/**
	 * 物料描述
	 */
	private String maktx;
	/**
	 * 单位 UNIT
	 */
	private String unit;
	/**
	 * 采购数量 MENGE
	 */
	private Double menge;
	/**
	 * 虚收101数量 XS101- 核销收货过账，更新此字段
	 */
	@TableField(value = "XS101")
	private Double xs101;
	/**
	 * 虚收102数量 XS102- 核销收货过账冲销，取消，更新此字段
	 */
	@TableField(value = "XS102")
	private Double xs102;
	/**
	 * 实收103数量 SS103-实物收料房收料，更新此字段
	 */
	@TableField(value = "SS103")
	private Double ss103;
	/**
	 * 实收104数量 SS104-实物收料房收料冲销，取消，更新此字段
	 */
	@TableField(value = "SS104")
	private Double ss104;
	/**
	 * 实收124数量 SS124-实物收料房退货，更新此字段
	 */
	@TableField(value = "SS124")
	private Double ss124;
	/**
	 * 实收125数量 SS125-实物收料房退货冲销，取消，更新此字段
	 */
	@TableField(value = "SS125")
	private Double ss125;
	/**
	 * 实收105数量 SS105-实物进仓，更新此字段
	 */
	@TableField(value = "SS105")
	private Double ss105;
	/**
	 * 实收106数量 SS106-实物进仓冲销，取消，更新此字段
	 */
	@TableField(value = "SS106")
	private Double ss106;
	/**
	 * 剩余核销数量 HX_QTY
	 */
	@TableField(value = "HX_QTY")
	private Double hxQty;
	/**
	 * 取消标识 CANCEL_FLAG  默认0 否 X 时  WMS凭证取消时更新此字段
	 */
	private String cancelFlag;
	/**
	 * 记录创建人
	 */
	private String creator;
	/**
	 * 记录创建时间
	 */
	private String createDate;
	/**
	 * 记录修改人 EDITOR
	 */
	private String editor;
	/**
	 * 记录修改时间 EDIT_DATE
	 */
	private String editDate;
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
	public String getLifnr() {
		return lifnr;
	}
	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
	}
	public String getEbeln() {
		return ebeln;
	}
	public void setEbeln(String ebeln) {
		this.ebeln = ebeln;
	}
	public String getEbelp() {
		return ebelp;
	}
	public void setEbelp(String ebelp) {
		this.ebelp = ebelp;
	}
	public String getLgort() {
		return lgort;
	}
	public void setLgort(String lgort) {
		this.lgort = lgort;
	}
	public String getMatnr() {
		return matnr;
	}
	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}
	public String getMaktx() {
		return maktx;
	}
	public void setMaktx(String maktx) {
		this.maktx = maktx;
	}
	public String getUnit() {
		return unit;
	}
	public void setUnit(String unit) {
		this.unit = unit;
	}
	public Double getMenge() {
		return menge;
	}
	public void setMenge(Double menge) {
		this.menge = menge;
	}
	public Double getXs101() {
		return xs101;
	}
	public void setXs101(Double xs101) {
		this.xs101 = xs101;
	}
	public Double getXs102() {
		return xs102;
	}
	public void setXs102(Double xs102) {
		this.xs102 = xs102;
	}
	public Double getSs103() {
		return ss103;
	}
	public void setSs103(Double ss103) {
		this.ss103 = ss103;
	}
	public Double getSs104() {
		return ss104;
	}
	public void setSs104(Double ss104) {
		this.ss104 = ss104;
	}
	public Double getSs124() {
		return ss124;
	}
	public void setSs124(Double ss124) {
		this.ss124 = ss124;
	}
	public Double getSs125() {
		return ss125;
	}
	public void setSs125(Double ss125) {
		this.ss125 = ss125;
	}
	public Double getSs105() {
		return ss105;
	}
	public void setSs105(Double ss105) {
		this.ss105 = ss105;
	}
	public Double getSs106() {
		return ss106;
	}
	public void setSs106(Double ss106) {
		this.ss106 = ss106;
	}
	public Double getHxQty() {
		return hxQty;
	}
	public void setHxQty(Double hxQty) {
		this.hxQty = hxQty;
	}
	public String getCancelFlag() {
		return cancelFlag;
	}
	public void setCancelFlag(String cancelFlag) {
		this.cancelFlag = cancelFlag;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
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
	
}
