package com.byd.wms.business.modules.config.formbean;

import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author ren.wei3
 *
 */
public class WmsCMatFixedStorageFormbean implements Serializable{
	private static final long serialVersionUID = 1L;
	private Long id;
	
	/**
	 * 工厂代码
	 */
	private String werks;
	/**
	 * 仓库号
	 */
	private String whNumber;
	/**
	 * 物料号
	 */
	private String matnr;
	/**
	 * 物料描述
	 */
	private String maktx;
	/**
	 * 存储类型代码
	 */
	private String storageAreaCode;
	/**
	 * 储位代码
	 */
	private String binCode;
	/**
	 * 排序字符串
	 */
	private BigDecimal seqno;
	/**
	 * 数量/存储单位 包装与基本单位换算关系
	 */
	private BigDecimal qty;
	/**
	 * 最小数量
	 */
	private BigDecimal stockM;
	/**
	 * 最大数量
	 */
	private BigDecimal stockL;
	/**
	 * 库位
	 */
	private String lgort;
	/**
	 * 库存类型
	 */
	private String sobkz;
	/**
	 * 供应商
	 */
	private String lifnr;
	/**
	 * 编辑人
	 */
	private String editor;
	/**
	 * 编辑时间
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
	public String getStorageAreaCode() {
		return storageAreaCode;
	}
	public void setStorageAreaCode(String storageAreaCode) {
		this.storageAreaCode = storageAreaCode;
	}
	public String getBinCode() {
		return binCode;
	}
	public void setBinCode(String binCode) {
		this.binCode = binCode;
	}
	public BigDecimal getSeqno() {
		return seqno;
	}
	public void setSeqno(BigDecimal seqno) {
		this.seqno = seqno;
	}
	public BigDecimal getQty() {
		return qty;
	}
	public void setQty(BigDecimal qty) {
		this.qty = qty;
	}
	public BigDecimal getStockM() {
		return stockM;
	}
	public void setStockM(BigDecimal stockM) {
		this.stockM = stockM;
	}
	public BigDecimal getStockL() {
		return stockL;
	}
	public void setStockL(BigDecimal stockL) {
		this.stockL = stockL;
	}
	public String getLgort() {
		return lgort;
	}
	public void setLgort(String lgort) {
		this.lgort = lgort;
	}
	public String getSobkz() {
		return sobkz;
	}
	public void setSobkz(String sobkz) {
		this.sobkz = sobkz;
	}
	public String getLifnr() {
		return lifnr;
	}
	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
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
