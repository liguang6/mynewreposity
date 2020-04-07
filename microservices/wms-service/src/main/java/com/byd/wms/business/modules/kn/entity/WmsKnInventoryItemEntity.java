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
@TableName("WMS_KN_INVENTORY_ITEM")
@KeySequence("SEQ_WMS_KN_INVENTORY_ITEM")
public class WmsKnInventoryItemEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value = "ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 盘点任务号
	 */
	private String inventoryNo;
	/**
	 * 盘点任务行项目号
	 */
	private String inventoryItemNo;
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
	 * 库位
	 */
	private String lgort;
	/**
	 * 供应商代码
	 */
	private String lifnr;
	/**
	 * 供应商名称
	 */
	private String liktx;
	/**
	 * 库存数量（基于基本计量单位）
	 */
	private Long stockQty;
	/**
	 * 初盘数量
	 */
	private String inventoryQty;
	/**
	 * 复盘数量
	 */
	private String inventoryQtyRepeat;
	/**
	 * 单位（基本计量单位）
	 */
	private String meins;
	/**
	 * 冻结数量
	 */
	private Long freezeQty;
	/**
	 * 差异原因 
	 */
	private String differenceReason;
	/**
	 * 初盘人
	 */
	private String inventoryPerson;
	/**
	 * 初盘时间
	 */
	private String inventoryDate;
	/**
	 * 复盘人
	 */
	private String inventoryPersonRepeat;
	/**
	 * 复盘时间
	 */
	private String inventoryDateRepeat;
	/**
	 * 确认人 CONFIRMOR
	 */
	private String confirmor;
	/**
	 * 确认时间
	 */
	private String confirmDate;
	/**
	 * 删除标示
	 */
	private String del;
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
	public String getInventoryNo() {
		return inventoryNo;
	}
	public void setInventoryNo(String inventoryNo) {
		this.inventoryNo = inventoryNo;
	}
	public String getInventoryItemNo() {
		return inventoryItemNo;
	}
	public void setInventoryItemNo(String inventoryItemNo) {
		this.inventoryItemNo = inventoryItemNo;
	}
	public String getLgort() {
		return lgort;
	}
	public void setLgort(String lgort) {
		this.lgort = lgort;
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
	public Long getStockQty() {
		return stockQty;
	}
	public void setStockQty(Long stockQty) {
		this.stockQty = stockQty;
	}
	public String getInventoryQty() {
		return inventoryQty;
	}
	public void setInventoryQty(String inventoryQty) {
		this.inventoryQty = inventoryQty;
	}
	public String getInventoryQtyRepeat() {
		return inventoryQtyRepeat;
	}
	public void setInventoryQtyRepeat(String inventoryQtyRepeat) {
		this.inventoryQtyRepeat = inventoryQtyRepeat;
	}
	public String getMeins() {
		return meins;
	}
	public void setMeins(String meins) {
		this.meins = meins;
	}
	public Long getFreezeQty() {
		return freezeQty;
	}
	public void setFreezeQty(Long freezeQty) {
		this.freezeQty = freezeQty;
	}
	public String getDifferenceReason() {
		return differenceReason;
	}
	public void setDifferenceReason(String differenceReason) {
		this.differenceReason = differenceReason;
	}
	public String getInventoryPerson() {
		return inventoryPerson;
	}
	public void setInventoryPerson(String inventoryPerson) {
		this.inventoryPerson = inventoryPerson;
	}
	public String getInventoryDate() {
		return inventoryDate;
	}
	public void setInventoryDate(String inventoryDate) {
		this.inventoryDate = inventoryDate;
	}
	public String getInventoryPersonRepeat() {
		return inventoryPersonRepeat;
	}
	public void setInventoryPersonRepeat(String inventoryPersonRepeat) {
		this.inventoryPersonRepeat = inventoryPersonRepeat;
	}
	public String getInventoryDateRepeat() {
		return inventoryDateRepeat;
	}
	public void setInventoryDateRepeat(String inventoryDateRepeat) {
		this.inventoryDateRepeat = inventoryDateRepeat;
	}
	public String getConfirmor() {
		return confirmor;
	}
	public void setConfirmor(String confirmor) {
		this.confirmor = confirmor;
	}
	public String getConfirmDate() {
		return confirmDate;
	}
	public void setConfirmDate(String confirmDate) {
		this.confirmDate = confirmDate;
	}
	public String getDel() {
		return del;
	}
	public void setDel(String del) {
		this.del = del;
	}
	
}
