package com.byd.web.wms.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 物料存储配置表 仓库系统上线前配置
 * 
 * @author cscc
 * @email 
 * @date 2018-08-10 16:09:55
 */
@TableName("WMS_C_MAT_STORAGE")
@KeySequence("SEQ_WMS_C_MAT_STORAGE")
public class WmsCMatStorageEntity implements Serializable {
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
	 * 存储类型代码
	 */
	private String storageAreaCode;
	/**
	 * 物料存储模式 00 固定存储 01随机存储 通过存储类型带过来
	 */
	private String storageModel;
	/**
	 * 入库控制标识
	 */
	private String inControlFlag;
	/**
	 * 出库控制标识
	 */
	private String outControlFlag;
	/**
	 * 长
	 */
	private Long length;
	/**
	 * 宽
	 */
	private Long width;
	/**
	 * 高
	 */
	private Long height;
	/**
	 * 长宽高单位
	 */
	private String sizeUnit;
	/**
	 * 体积
	 */
	private Long volum;
	/**
	 * 体积单位
	 */
	private String volumUnit;
	/**
	 * 重量
	 */
	private Long weight;
	/**
	 * 重量单位
	 */
	private String weightUnit;
	/**
	 * 存储单位 存储最小包装单位
	 */
	private String storageUnit;
	/**
	 * 数量/存储单位 包装与基本单位换算关系
	 */
	private Long qty;
	/**
	 * 最大库存
	 */
	private Long stockL;
	/**
	 * 最小库存
	 */
	private Long stockM;
	
	/**
	 * 是否启用最小包装 0 否 X 是 默认 0
	 */
	private String mpqFlag;
	/**
	 * 是否启用外部批次 0 否 X 是 默认 0
	 */
	private String externalBatchFlag;
	/**
	 * 相关联物料
	 */
	private String correlationMaterial;
	/**
	 * 排斥物料
	 */
	private String repulsiveMaterial;
	/**
	 * 数据维护账号
	 */
	private String editor;
	/**
	 * 数据维护日期
	 */
	private String editDate;
	
	/**
	 * 删除标识 默认0 否 X 是 标识删除
	 */
	private String del;
	
	/**
	 * 单存储单元数量
	 */
	private Long storageUnitQty;

	public String getStorageAreaCode() {
		return storageAreaCode;
	}
	public void setStorageAreaCode(String storageAreaCode) {
		this.storageAreaCode = storageAreaCode;
	}
	public String getStorageModel() {
		return storageModel;
	}
	public void setStorageModel(String storageModel) {
		this.storageModel = storageModel;
	}
	public String getInControlFlag() {
		return inControlFlag;
	}
	public void setInControlFlag(String inControlFlag) {
		this.inControlFlag = inControlFlag;
	}
	public String getOutControlFlag() {
		return outControlFlag;
	}
	public void setOutControlFlag(String outControlFlag) {
		this.outControlFlag = outControlFlag;
	}
	public Long getLength() {
		return length;
	}
	public void setLength(Long length) {
		this.length = length;
	}
	public Long getWidth() {
		return width;
	}
	public void setWidth(Long width) {
		this.width = width;
	}
	public Long getHeight() {
		return height;
	}
	public void setHeight(Long height) {
		this.height = height;
	}
	public String getSizeUnit() {
		return sizeUnit;
	}
	public void setSizeUnit(String sizeUnit) {
		this.sizeUnit = sizeUnit;
	}
	public Long getVolum() {
		return volum;
	}
	public void setVolum(Long volum) {
		this.volum = volum;
	}
	public String getVolumUnit() {
		return volumUnit;
	}
	public void setVolumUnit(String volumUnit) {
		this.volumUnit = volumUnit;
	}
	public Long getWeight() {
		return weight;
	}
	public void setWeight(Long weight) {
		this.weight = weight;
	}
	public String getWeightUnit() {
		return weightUnit;
	}
	public void setWeightUnit(String weightUnit) {
		this.weightUnit = weightUnit;
	}
	
	public String getMpqFlag() {
		return mpqFlag;
	}
	public void setMpqFlag(String mpqFlag) {
		this.mpqFlag = mpqFlag;
	}
	public String getExternalBatchFlag() {
		return externalBatchFlag;
	}
	public void setExternalBatchFlag(String externalBatchFlag) {
		this.externalBatchFlag = externalBatchFlag;
	}
	public String getCorrelationMaterial() {
		return correlationMaterial;
	}
	public void setCorrelationMaterial(String correlationMaterial) {
		this.correlationMaterial = correlationMaterial;
	}
	public String getRepulsiveMaterial() {
		return repulsiveMaterial;
	}
	public void setRepulsiveMaterial(String repulsiveMaterial) {
		this.repulsiveMaterial = repulsiveMaterial;
	}
	/**
	 * 设置：删除标识 默认0 否 X 是 标识删除
	 */
	public void setDel(String del) {
		this.del = del;
	}
	/**
	 * 获取：删除标识 默认0 否 X 是 标识删除
	 */
	public String getDel() {
		return del;
	}
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
	 * 设置：存储单位 存储最小包装单位
	 */
	public void setStorageUnit(String storageUnit) {
		this.storageUnit = storageUnit;
	}
	/**
	 * 获取：存储单位 存储最小包装单位
	 */
	public String getStorageUnit() {
		return storageUnit;
	}
	/**
	 * 设置：数量/存储单位 包装与基本单位换算关系
	 */
	public void setQty(Long qty) {
		this.qty = qty;
	}
	/**
	 * 获取：数量/存储单位 包装与基本单位换算关系
	 */
	public Long getQty() {
		return qty;
	}
	/**
	 * 设置：最大库存
	 */
	public void setStockL(Long stockL) {
		this.stockL = stockL;
	}
	/**
	 * 获取：最大库存
	 */
	public Long getStockL() {
		return stockL;
	}
	/**
	 * 设置：最小库存
	 */
	public void setStockM(Long stockM) {
		this.stockM = stockM;
	}
	/**
	 * 获取：最小库存
	 */
	public Long getStockM() {
		return stockM;
	}
	/**
	 * 设置：数据维护账号
	 */
	public void setEditor(String editor) {
		this.editor = editor;
	}
	/**
	 * 获取：数据维护账号
	 */
	public String getEditor() {
		return editor;
	}
	/**
	 * 设置：数据维护日期
	 */
	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}
	/**
	 * 获取：数据维护日期
	 */
	public String getEditDate() {
		return editDate;
	}
	public Long getStorageUnitQty() {
		return storageUnitQty;
	}
	public void setStorageUnitQty(Long storageUnitQty) {
		this.storageUnitQty = storageUnitQty;
	}
	
	
	
	
}
