package com.byd.wms.business.modules.common.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月21日 上午9:55:26 
 * 类说明 
 */
@TableName("WMS_CORE_MAT_BATCH")
@KeySequence("SEQ_WMS_CORE_MAT_BATCH")//使用oracle 注解自增
public class WmsCoreMatBatchEntity implements Serializable {
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 批次号
	 */
	private String batch;
	/**
	 * 物料号
	 */
	private String matnr;
	/**
	 * 送货单号
	 */
	private String asnno;
	/**
	 * 供应商
	 */
	private String lifnr;
	/**
	 * 收货日期
	 */
	private String receiptDate;
	/**
	 * 送货日期
	 */
	private String deliveryDate;
	/**
	 * 源批次
	 */
	private String fBatch;
	/**
	 * 生产日期
	 */
	private String productDate;
	/**
	 * 有效期
	 */
	private String effectDate;
	/**
	 * 批次生成日期
	 */
	private String generateDate;
	/**
	 * 批次生成人
	 */
	private String generator;
	/*
	 * 工厂
	 */
	private String werks;
	/*
	 * 库存地点
	 */
	@TableField(exist=false)
	private String lgort;
	/*
	 * 是否危化品
	 */
	@TableField(exist=false)
	private String dangerFlag;
	
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBatch() {
		return batch;
	}
	public void setBatch(String batch) {
		this.batch = batch;
	}
	public String getMatnr() {
		return matnr;
	}
	public void setMatnr(String matnr) {
		this.matnr = matnr;
	}
	public String getAsnno() {
		return asnno;
	}
	public void setAsnno(String asnno) {
		this.asnno = asnno;
	}
	public String getLifnr() {
		return lifnr;
	}
	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
	}
	public String getReceiptDate() {
		return receiptDate;
	}
	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}
	public String getDeliveryDate() {
		return deliveryDate;
	}
	public void setDeliveryDate(String deliveryDate) {
		this.deliveryDate = deliveryDate;
	}
	public String getfBatch() {
		return fBatch;
	}
	public void setfBatch(String fBatch) {
		this.fBatch = fBatch;
	}
	public String getProductDate() {
		return productDate;
	}
	public void setProductDate(String productDate) {
		this.productDate = productDate;
	}
	public String getEffectDate() {
		return effectDate;
	}
	public void setEffectDate(String effectDate) {
		this.effectDate = effectDate;
	}
	public String getGenerateDate() {
		return generateDate;
	}
	public void setGenerateDate(String generateDate) {
		this.generateDate = generateDate;
	}
	public String getGenerator() {
		return generator;
	}
	public void setGenerator(String generator) {
		this.generator = generator;
	}
	public String getWerks() {
		return werks;
	}
	public void setWerks(String werks) {
		this.werks = werks;
	}
	public String getLgort() {
		return lgort;
	}
	public void setLgort(String lgort) {
		this.lgort = lgort;
	}
	public String getDangerFlag() {
		return dangerFlag;
	}
	public void setDangerFlag(String dangerFlag) {
		this.dangerFlag = dangerFlag;
	}
	
	
}
