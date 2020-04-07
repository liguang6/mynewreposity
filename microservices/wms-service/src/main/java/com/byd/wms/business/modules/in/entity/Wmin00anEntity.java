package com.byd.wms.business.modules.in.entity;

import java.io.Serializable;
/**
 * 
 * @author pengtao
 *
 */
public class Wmin00anEntity implements Serializable{
	/**
	 * 送货单号
	 */
	private String ASNNO;
	/**
	 *  送货单状态：1:已创建；2:送货中；3:已收货；4:取消；5:删除；7:草稿；8:部分收货；9:关闭
	 */
	private String ST;
	/**
	 * 预计送货时间
	 */
	private String ETD;
	/**
	 * 预计到达时间
	 */
	private String ETA;
	/**
	 * 供应商代码
	 */
	private String LIFNR;
	/**
	 * 供应商名称
	 */
	private String LIKTX;
	/**
	 * 送货单类型
	 */
	private String ASNTP;
	/**
	 * 联系人
	 */
	private String CNUID;
	/**
	 * 联系电话
	 */
	private String CNUPH;
	/**
	 * 送达仓库号
	 */
	private String WH_NUMBER;
	/**
	 * 送达工厂
	 */
	private String WERKS;
	/**
	 * 库位
	 */
	private String LGORT;
	/**
	 * 
	 */
	private String KITTINGFLAG;
	
	private String MSG;

	public String getASNNO() {
		return ASNNO;
	}

	public void setASNNO(String aSNNO) {
		ASNNO = aSNNO;
	}

	public String getST() {
		return ST;
	}

	public void setST(String sT) {
		ST = sT;
	}

	public String getETD() {
		return ETD;
	}

	public void setETD(String eTD) {
		ETD = eTD;
	}

	public String getETA() {
		return ETA;
	}

	public void setETA(String eTA) {
		ETA = eTA;
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

	public String getASNTP() {
		return ASNTP;
	}

	public void setASNTP(String aSNTP) {
		ASNTP = aSNTP;
	}

	public String getCNUID() {
		return CNUID;
	}

	public void setCNUID(String cNUID) {
		CNUID = cNUID;
	}

	public String getCNUPH() {
		return CNUPH;
	}

	public void setCNUPH(String cNUPH) {
		CNUPH = cNUPH;
	}

	public String getWH_NUMBER() {
		return WH_NUMBER;
	}

	public void setWH_NUMBER(String wH_NUMBER) {
		WH_NUMBER = wH_NUMBER;
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

	public String getKITTINGFLAG() {
		return KITTINGFLAG;
	}

	public void setKITTINGFLAG(String kITTINGFLAG) {
		KITTINGFLAG = kITTINGFLAG;
	}

	public String getMSG() {
		return MSG;
	}

	public void setMSG(String mSG) {
		MSG = mSG;
	}
	
	
	
	
}
