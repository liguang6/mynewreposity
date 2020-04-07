package com.byd.wms.business.modules.kn.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 库存冻结记录
 * 
 * @author cscc
 * @email 
 * @date 2018-10-11 10:12:08
 */
@TableName("WMS_KN_FREEZE_RECORD")
@KeySequence("SEQ_WMS_KN_FREEZE_RECORD")
public class WmsKnFreezeRecordEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
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
	/**
	 * 物料号
	 */
	private String matnr;
	/**
	 * 物料描述
	 */
	private String maktx;
	/**
	 * 源批次
	 */
	private String fBatch;
	/**
	 * WMS批次
	 */
	private String batch;
	/**
	 * 库位
	 */
	private String lgort;
	/**
	 * 储位代码
	 */
	private String binCode;
	/**
	 * 储位名称
	 */
	private String binName;
	/**
	 * 供应商代码
	 */
	private String lifnr;
	/**
	 * 供应商名称
	 */
	private String liktx;
	/**
	 * 特殊库存标识 字典定义：特殊库存类型 K, O, E, Q
	 */
	private String sobkz;
	/**
	 * 单位（基本计量单位）
	 */
	private String meins;
	/**
	 * 冻结数量
	 */
	private Long freezeQty;
	/**
	 * 冻结类别 00 冻结 01 解冻
	 */
	private String freezeType;
	/**
	 * 原因代码 字典定义
	 */
	private String reasonCode;
	/**
	 * 原因描述
	 */
	private String reason;
	/**
	 * 冻结人
	 */
	private String editor;
	/**
	 * 冻结时间
	 */
	private String editDate;

	/**
	 * 设置：ID
	 */
	public void setId(Long id) {
		this.id = id;
	}
	/**
	 * 获取：ID
	 */
	public Long getId() {
		return id;
	}
	/**
	 * 设置：工厂代码
	 */
	public void setWerks(String werks) {
		this.werks = werks;
	}
	/**
	 * 获取：工厂代码
	 */
	public String getWerks() {
		return werks;
	}
	/**
	 * 设置：仓库号
	 */
	public void setWhNumber(String whNumber) {
		this.whNumber = whNumber;
	}
	/**
	 * 获取：仓库号
	 */
	public String getWhNumber() {
		return whNumber;
	}
	/**
	 * 设置：物料号
	 */
	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}
	/**
	 * 获取：物料号
	 */
	public String getMatnr() {
		return matnr;
	}
	/**
	 * 设置：物料描述
	 */
	public void setMaktx(String maktx) {
		this.maktx = maktx;
	}
	/**
	 * 获取：物料描述
	 */
	public String getMaktx() {
		return maktx;
	}
	/**
	 * 设置：源批次
	 */
	public void setFBatch(String fBatch) {
		this.fBatch = fBatch;
	}
	/**
	 * 获取：源批次
	 */
	public String getFBatch() {
		return fBatch;
	}
	/**
	 * 设置：WMS批次
	 */
	public void setBatch(String batch) {
		this.batch = batch;
	}
	/**
	 * 获取：WMS批次
	 */
	public String getBatch() {
		return batch;
	}
	/**
	 * 设置：库位
	 */
	public void setLgort(String lgort) {
		this.lgort = lgort;
	}
	/**
	 * 获取：库位
	 */
	public String getLgort() {
		return lgort;
	}
	/**
	 * 设置：储位代码
	 */
	public void setBinCode(String binCode) {
		this.binCode = binCode;
	}
	/**
	 * 获取：储位代码
	 */
	public String getBinCode() {
		return binCode;
	}
	/**
	 * 设置：储位名称
	 */
	public void setBinName(String binName) {
		this.binName = binName;
	}
	/**
	 * 获取：储位名称
	 */
	public String getBinName() {
		return binName;
	}
	/**
	 * 设置：供应商代码
	 */
	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
	}
	/**
	 * 获取：供应商代码
	 */
	public String getLifnr() {
		return lifnr;
	}
	/**
	 * 设置：供应商名称
	 */
	public void setLiktx(String liktx) {
		this.liktx = liktx;
	}
	/**
	 * 获取：供应商名称
	 */
	public String getLiktx() {
		return liktx;
	}
	/**
	 * 设置：特殊库存标识 字典定义：特殊库存类型 K, O, E, Q
	 */
	public void setSobkz(String sobkz) {
		this.sobkz = sobkz;
	}
	/**
	 * 获取：特殊库存标识 字典定义：特殊库存类型 K, O, E, Q
	 */
	public String getSobkz() {
		return sobkz;
	}
	/**
	 * 设置：单位（基本计量单位）
	 */
	public void setMeins(String meins) {
		this.meins = meins;
	}
	/**
	 * 获取：单位（基本计量单位）
	 */
	public String getMnit() {
		return meins;
	}
	/**
	 * 设置：冻结数量
	 */
	public void setFreezeQty(Long freezeQty) {
		this.freezeQty = freezeQty;
	}
	/**
	 * 获取：冻结数量
	 */
	public Long getFreezeQty() {
		return freezeQty;
	}
	/**
	 * 设置：冻结类别 00 冻结 01 解冻
	 */
	public void setFreezeType(String freezeType) {
		this.freezeType = freezeType;
	}
	/**
	 * 获取：冻结类别 00 冻结 01 解冻
	 */
	public String getFreezeType() {
		return freezeType;
	}
	/**
	 * 设置：原因代码 字典定义
	 */
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}
	/**
	 * 获取：原因代码 字典定义
	 */
	public String getReasonCode() {
		return reasonCode;
	}
	/**
	 * 设置：原因描述
	 */
	public void setReason(String reason) {
		this.reason = reason;
	}
	/**
	 * 获取：原因描述
	 */
	public String getReason() {
		return reason;
	}
	/**
	 * 设置：冻结人
	 */
	public void setEditor(String editor) {
		this.editor = editor;
	}
	/**
	 * 获取：冻结人
	 */
	public String getEditor() {
		return editor;
	}
	/**
	 * 设置：冻结时间
	 */
	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}
	/**
	 * 获取：冻结时间
	 */
	public String getEditDate() {
		return editDate;
	}
}
