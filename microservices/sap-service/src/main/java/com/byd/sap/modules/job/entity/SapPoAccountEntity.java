package com.byd.sap.modules.job.entity;

public class SapPoAccountEntity {
	private String id;
	/*
	 * 采购订单号 EBELN
	 */
	private String EBELN;
	/*
	 * 行项目编号 EBELP
	 */
	private String EBELP;
	/*
	 * 账户分配顺序编号 ZEKKN
	 */
	private String ZEKKN;
	/*
	 * 删除标示 LOEKZ
	 */
	private String LOEKZ;
	/*
	 * 数量 MENGE
	 */
	private String MENGE;
	/*
	 * 总账科目编号 SAKTO
	 */
	private String SAKTO;
	/*
	 * 成本中心 KOSTL
	 */
	private String KOSTL;
	/*
	 * 主资产号 ANLN1
	 */
	private String ANLN1;
	/*
	 * 资产次级编号 ANLN2
	 */
	private String ANLN2;
	/*
	 * 订单号 AUFNR 内部订单 生产订单号
	 */
	private String AUFNR;
	/*
	 * 利润中心 PRCTR
	 */
	private String PRCTR;
	/*
	 * WBS元素号 PS_PSP_PNR
	 */
	private String PS_PSP_PNR;
	/*
	 * 导入日期
	 */
	private String IMPORT_DATE;
	/*
	 * 数据一致性标示
	 */
	private String VERIFY;
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getEBELN() {
		return EBELN;
	}
	public void setEBELN(String eBELN) {
		EBELN = eBELN;
	}
	public String getEBELP() {
		return EBELP;
	}
	public void setEBELP(String eBELP) {
		EBELP = eBELP;
	}
	public String getZEKKN() {
		return ZEKKN;
	}
	public void setZEKKN(String zEKKN) {
		ZEKKN = zEKKN;
	}
	public String getLOEKZ() {
		return LOEKZ;
	}
	public void setLOEKZ(String lOEKZ) {
		LOEKZ = lOEKZ;
	}
	public String getMENGE() {
		return MENGE;
	}
	public void setMENGE(String mENGE) {
		MENGE = mENGE;
	}
	public String getSAKTO() {
		return SAKTO;
	}
	public void setSAKTO(String sAKTO) {
		SAKTO = sAKTO;
	}
	public String getKOSTL() {
		return KOSTL;
	}
	public void setKOSTL(String kOSTL) {
		KOSTL = kOSTL;
	}
	public String getANLN1() {
		return ANLN1;
	}
	public void setANLN1(String aNLN1) {
		ANLN1 = aNLN1;
	}
	public String getANLN2() {
		return ANLN2;
	}
	public void setANLN2(String aNLN2) {
		ANLN2 = aNLN2;
	}
	public String getAUFNR() {
		return AUFNR;
	}
	public void setAUFNR(String aUFNR) {
		AUFNR = aUFNR;
	}
	public String getPRCTR() {
		return PRCTR;
	}
	public void setPRCTR(String pRCTR) {
		PRCTR = pRCTR;
	}
	public String getPS_PSP_PNR() {
		return PS_PSP_PNR;
	}
	public void setPS_PSP_PNR(String pS_PSP_PNR) {
		PS_PSP_PNR = pS_PSP_PNR;
	}
	public String getIMPORT_DATE() {
		return IMPORT_DATE;
	}
	public void setIMPORT_DATE(String iMPORT_DATE) {
		IMPORT_DATE = iMPORT_DATE;
	}
	public String getVERIFY() {
		return VERIFY;
	}
	public void setVERIFY(String vERIFY) {
		VERIFY = vERIFY;
	}
	
	
}
