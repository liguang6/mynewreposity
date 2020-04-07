package com.byd.wms.business.modules.in.entity;

import java.io.Serializable;
import java.math.BigDecimal;

/** 
 * @author pengtao 
 * @version 创建时间：2018年8月16日 下午3:48:43 
 * 类说明 
 */
public class Wmin00an00Entity implements Serializable {

	/**
	 * 送货单号
	 */
	private String ASNNO;
	/**
	 * 送货单行项目
	 */
	private String ASNITM;
	/**
	 * 工厂代码
	 */
	private String WERKS;
	/**
	 * 库位代码
	 */
	private String LGORT;
	/**
	 * 物料号
	 */
	private String MATNR;
	/**
	 * 物料描述
	 */
	private String MAKTX;
	/**
	 * 供应商代码
	 */
	private String LIFNR;
	/**
	 * 供应商名称
	 */
	private String LIKTX;
	/**
	 * 数量
	 */
	private BigDecimal QTY;
	
	private BigDecimal SPEC;
	/**
	 * 采购单位
	 */
	private String UNIT;
	/**
	 * 箱数
	 */
	private BigDecimal BOX_COUNT;
	/**
	 * 采购订单号。无采购订单为空
	 */
	private String PONO;
	/**
	 * 行项目号。无采购订单为空
	 */
	private String POITM;
	/**
	 * 
	 */
	private String TESTFLAG;
	
	private String MSG;

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

	public String getWERKS() {
		return WERKS;
	}

	public void setWERKS(String wERKS) {
		WERKS = wERKS;
	}

	public String getLGORT() {
		return LGORT;
	}

	public void setLGORT(String lGORT) {
		LGORT = lGORT;
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

	public BigDecimal getQTY() {
		return QTY;
	}

	public void setQTY(BigDecimal qTY) {
		QTY = qTY;
	}

	public String getUNIT() {
		return UNIT;
	}

	public void setUNIT(String uNIT) {
		UNIT = uNIT;
	}

	public BigDecimal getBOX_COUNT() {
		return BOX_COUNT;
	}

	public void setBOX_COUNT(BigDecimal bOX_COUNT) {
		BOX_COUNT = bOX_COUNT;
	}

	public String getPONO() {
		return PONO;
	}

	public void setPONO(String pONO) {
		PONO = pONO;
	}

	public String getPOITM() {
		return POITM;
	}

	public void setPOITM(String pOITM) {
		POITM = pOITM;
	}

	public String getTESTFLAG() {
		return TESTFLAG;
	}

	public void setTESTFLAG(String tESTFLAG) {
		TESTFLAG = tESTFLAG;
	}

	public String getMSG() {
		return MSG;
	}

	public void setMSG(String mSG) {
		MSG = mSG;
	}

	public BigDecimal getSPEC() {
		return SPEC;
	}

	public void setSPEC(BigDecimal sPEC) {
		SPEC = sPEC;
	}
	
	
	
}
