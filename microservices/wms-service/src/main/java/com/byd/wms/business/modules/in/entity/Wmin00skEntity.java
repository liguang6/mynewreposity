package com.byd.wms.business.modules.in.entity;

import java.io.Serializable;
import java.math.BigDecimal;

import com.alibaba.fastjson.annotation.JSONField;

/** 
 * @author pengtao 
 * @version 创建时间：2018年8月16日 下午4:22:26 
 * 类说明 
 */
public class Wmin00skEntity implements Serializable {

	/**
	 * 包装箱条码。
	 */
	@JSONField  (name = "SKUID")
	public String SKUID;
	
	/**
	 * 供应商代码。
	 */
	@JSONField  (name = "LIFNR")
	public String LIFNR;
	/**
	 * 供应商名称。
	 */
	@JSONField  (name = "LIKTX")
	public String LIKTX;
	/**
	 * 工厂代码。
	 */
	@JSONField  (name = "WERKS")
	public String WERKS;
	/**
	 * 物料号。
	 */
	@JSONField  (name = "MATNR")
	public String MATNR;
	/**
	 * 物料描述。
	 */
	@JSONField  (name = "MAKTX")
	public String MAKTX;
	/**
	 * 生产批次号。
	 */
	@JSONField  (name = "BATCH")
	public String BATCH;
	/**
	 * 装箱数量。
	 */
	@JSONField  (name = "BOX_QTY")
	public BigDecimal BOX_QTY;
	/**
	 * 满箱数量。
	 */
	@JSONField  (name = "FULL_BOX_QTY")
	public BigDecimal FULL_BOX_QTY;
	/**
	 * 计量单位。
	 */
	@JSONField  (name = "UNIT")
	public String UNIT;
	/**
	 * 生产日期。
	 */
	@JSONField  (name = "PRDDT")
	public String PRDDT;
	/**
	 * 箱序号。
	 */
	@JSONField  (name = "BOX_SN")
	public String BXIDX;
	
	@JSONField  (name = "MSG")
	public String MSG;
	
	/**
	 * 送货单号
	 */
	@JSONField  (name = "ASNNO")
	public String ASNNO;
	/**
	 * 送货单行项目
	 */
	@JSONField  (name = "ASNITM")
	public String ASNITM;
	
	//采购订单号
	@JSONField  (name = "PO_NO")
	public String PO_NO;
	
	//采购订单行项目号
	@JSONField  (name = "PO_ITEM_NO")
	public String PO_ITEM_NO;
	
	@JSONField  (name = "VALID_DATE") // 有效期
	public String validdate;
	
	public String getSKUID() {
		return SKUID;
	}
	public void setSKUID(String sKUID) {
		SKUID = sKUID;
	}
	public String getLIFNR() {
		return LIFNR;
	}
	public void setLIFNR(String lIFNR) {
		LIFNR = lIFNR;
	}
	public String getLIKTX() {
		return LIKTX;
	}
	public void setLIKTX(String lIKTX) {
		LIKTX = lIKTX;
	}
	public String getWERKS() {
		return WERKS;
	}
	public void setWERKS(String wERKS) {
		WERKS = wERKS;
	}
	public String getMATNR() {
		return MATNR;
	}
	public void setMATNR(String mATNR) {
		MATNR = mATNR;
	}
	public String getMAKTX() {
		return MAKTX;
	}
	public void setMAKTX(String mAKTX) {
		MAKTX = mAKTX;
	}
	public String getBATCH() {
		return BATCH;
	}
	public void setBATCH(String bATCH) {
		BATCH = bATCH;
	}
	
	public BigDecimal getBOX_QTY() {
		return BOX_QTY;
	}
	public void setBOX_QTY(BigDecimal bOX_QTY) {
		BOX_QTY = bOX_QTY;
	}
	public BigDecimal getFULL_BOX_QTY() {
		return FULL_BOX_QTY;
	}
	public void setFULL_BOX_QTY(BigDecimal fULL_BOX_QTY) {
		FULL_BOX_QTY = fULL_BOX_QTY;
	}
	public String getUNIT() {
		return UNIT;
	}
	public void setUNIT(String uNIT) {
		UNIT = uNIT;
	}
	public String getPRDDT() {
		return PRDDT;
	}
	public void setPRDDT(String pRDDT) {
		PRDDT = pRDDT;
	}
	public String getBXIDX() {
		return BXIDX;
	}
	public void setBXIDX(String bXIDX) {
		BXIDX = bXIDX;
	}
	public String getMSG() {
		return MSG;
	}
	public void setMSG(String mSG) {
		MSG = mSG;
	}
	public String getASNNO() {
		return ASNNO;
	}
	public void setASNNO(String aSNNO) {
		ASNNO = aSNNO;
	}
	public String getASNITM() {
		return ASNITM;
	}
	public void setASNITM(String aSNITM) {
		ASNITM = aSNITM;
	}
	public String getPO_NO() {
		return PO_NO;
	}
	public void setPO_NO(String pO_NO) {
		PO_NO = pO_NO;
	}
	public String getPO_ITEM_NO() {
		return PO_ITEM_NO;
	}
	public void setPO_ITEM_NO(String pO_ITEM_NO) {
		PO_ITEM_NO = pO_ITEM_NO;
	}
	public String getValiddate() {
		return validdate;
	}
	public void setValiddate(String validdate) {
		this.validdate = validdate;
	}
	
}
