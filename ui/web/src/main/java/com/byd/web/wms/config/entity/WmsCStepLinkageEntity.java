package com.byd.web.wms.config.entity;
import java.io.Serializable;
import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 
 * 一步联动主数据配置
 *
 */
@TableName("WMS_C_STEP_LINKAGE")
@KeySequence("SEQ_WMS_C_STEP_LINKAGE")
public class WmsCStepLinkageEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId(value = "ID", type = IdType.INPUT)
	private Long id;
	/**
	 * 仓库号
	 */
	@TableField(value = "WH_NUMBER")
	private String warehouseCode;
	/**
	 * 发出工厂
	 */
	private String werksFrom;
	/**
	 * 接收工厂
	 */
	private String werksTo;
	/**
	 * 凭证类型
	 */
	private String docType;
	/**
	 * 采购组
	 */
	private String ekgrp;
	/**
	 * 税码
	 */
	private String taxCode;
	/**
	 * 事业部名称
	 */
	private String buName;
	/**
	 * 接收采购组织
	 */
	private String ekorg;
	/**
	 * 接收公司代码
	 */
	private String bukrs;
	/**
	 * 客户代码
	 */
	private String kunnr;
	/**
	 * 供应商代码
	 */
	private String lifnr;
	/**
	 * 销售凭证类型
	 */
	private String soDocType;
	/**
	 * 销售组织
	 */
	private String soGroup;
	/**
	 * 分销渠道
	 */
	private String soChannel;
	/**
	 *产品组
	 */
	private String productGroup;
	/**
	 * 装运点
	 */
	private String shippingPoint;
	/**
	 * 删除标示 空,0 否 X是 默认空
	 */
	private String del;
	private String editor;
	private String editDate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getWarehouseCode() {
		return warehouseCode;
	}
	public void setWarehouseCode(String warehouseCode) {
		this.warehouseCode = warehouseCode;
	}
	public String getWerksFrom() {
		return werksFrom;
	}
	public void setWerksFrom(String werksFrom) {
		this.werksFrom = werksFrom;
	}
	public String getWerksTo() {
		return werksTo;
	}
	public void setWerksTo(String werksTo) {
		this.werksTo = werksTo;
	}
	public String getDocType() {
		return docType;
	}
	public void setDocType(String docType) {
		this.docType = docType;
	}
	public String getEkgrp() {
		return ekgrp;
	}
	public void setEkgrp(String ekgrp) {
		this.ekgrp = ekgrp;
	}
	public String getTaxCode() {
		return taxCode;
	}
	public void setTaxCode(String taxCode) {
		this.taxCode = taxCode;
	}
	public String getBuName() {
		return buName;
	}
	public void setBuName(String buName) {
		this.buName = buName;
	}
	public String getEkorg() {
		return ekorg;
	}
	public void setEkorg(String ekorg) {
		this.ekorg = ekorg;
	}
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public String getKunnr() {
		return kunnr;
	}
	public void setKunnr(String kunnr) {
		this.kunnr = kunnr;
	}
	public String getLifnr() {
		return lifnr;
	}
	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
	}
	public String getSoDocType() {
		return soDocType;
	}
	public void setSoDocType(String soDocType) {
		this.soDocType = soDocType;
	}
	public String getSoGroup() {
		return soGroup;
	}
	public void setSoGroup(String soGroup) {
		this.soGroup = soGroup;
	}
	public String getSoChannel() {
		return soChannel;
	}
	public void setSoChannel(String soChannel) {
		this.soChannel = soChannel;
	}
	public String getProductGroup() {
		return productGroup;
	}
	public void setProductGroup(String productGroup) {
		this.productGroup = productGroup;
	}
	public String getShippingPoint() {
		return shippingPoint;
	}
	public void setShippingPoint(String shippingPoint) {
		this.shippingPoint = shippingPoint;
	}
	public String getDel() {
		return del;
	}
	public void setDel(String del) {
		this.del = del;
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
