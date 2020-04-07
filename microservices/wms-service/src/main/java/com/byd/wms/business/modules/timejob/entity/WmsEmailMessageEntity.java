package com.byd.wms.business.modules.timejob.entity;

import java.io.Serializable;

/**
 * 送检单抬头
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
public class WmsEmailMessageEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	private Long id;
	/**
	 * 工厂
	 */
	private String werks;
	/**
	 * 物料
	 */
	private String matnr;
	/**
	 * 物料描述
	 */
	private String maktx;
	/**
	 * 批次
	 */
	private String batch;
	/**
	 * 数量
	 */
	private String qty;
	/**
	 * 单位
	 */
	private String unit;
	/**
	 * 供应商
	 */
	private String lifnr;
	/**
	 * 供应商名称
	 */
	private String liktx;
	/**
	 * 收货日期
	 */
	private String receiptDate;
	/**
	 * 需求跟踪号
	 */
//	private String maktx;
	/**
	 * 仓库号
	 */
	private String whNumber;
	/**
	 * 质检时间
	 */
	private String qcDate;
	/**
	 * 质检结果
	 */
	private String qcResultText;
	/**
	 * 质检结果原因
	 */
	private String qcResult;

	public String getEffectDate() {
		return effectDate;
	}

	public void setEffectDate(String effectDate) {
		this.effectDate = effectDate;
	}

	/**
	 * 到期日期
	 */
	private String effectDate;
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

	public String getBatch() {
		return batch;
	}

	public void setBatch(String batch) {
		this.batch = batch;
	}

	public String getQty() {
		return qty;
	}

	public void setQty(String qty) {
		this.qty = qty;
	}

	public String getUnit() {
		return unit;
	}

	public void setUnit(String unit) {
		this.unit = unit;
	}

	public String getLifnr() {
		return lifnr;
	}

	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
	}

	public String getLiktx() {
		return liktx;
	}

	public void setLiktx(String liktx) {
		this.liktx = liktx;
	}

	public String getReceiptDate() {
		return receiptDate;
	}

	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}

	public String getWhNumber() {
		return whNumber;
	}

	public void setWhNumber(String whNumber) {
		this.whNumber = whNumber;
	}

	public String getQcDate() {
		return qcDate;
	}

	public void setQcDate(String qcDate) {
		this.qcDate = qcDate;
	}

	public String getQcResultText() {
		return qcResultText;
	}

	public void setQcResultText(String qcResultText) {
		this.qcResultText = qcResultText;
	}

	public String getQcResult() {
		return qcResult;
	}

	public void setQcResult(String qcResult) {
		this.qcResult = qcResult;
	}
}
