package com.byd.sap.modules.job.entity;

import java.math.BigDecimal;

public class SapPoItemEntity {
	private String id;
	/*
	 * 采购订单号 EBELN
	 */
	private String EBELN;
	/*
	 * 行项目号 EBELP
	 */
	private String EBELP;
	/*
	 * 行项目删除标示 LOEKZ
	 */
	private String LOEKZ;
	/*
	 * 物料号 MATNR
	 */
	private String MATNR;
	/*
	 * 短文本 TXZ01 物料描述
	 */
	private String TXZ01;
	/*
	 * 公司代码 BUKRS
	 */
	private String BUKRS;
	/*
	 * 工厂代码 WERKS
	 */
	private String WERKS;
	/*
	 * 库存地点 LGORT
	 */
	private String LGORT;
	/*
	 * 需求跟踪号 BEDNR 需求跟踪号
	 */
	private String BEDNR;
	/*
	 * 物料组 MATKL
	 */
	private String MATKL;
	/*
	 * 数量 MENGE
	 */
	private BigDecimal MENGE;
	/*
	 * 采购订单的计量单位
	 */
	private String MEINS;
	/*
	 * 计量单位 LMEIN 物料基本单位
	 */
	private String LMEIN;
	/*
	 * 项目类别 PSTYP 标准 寄售 委外
	 */
	private String PSTYP;
	/*
	 * 科目分配类别 KNTTP A 资产 K 成本中心 F内部订单 标准
	 */
	private String KNTTP;
	/*
	 * 最近收货日期 LEWED
	 */
	private String LEWED;
	/*
	 * 交货已完成标示 ELIKZ
	 */
	private String ELIKZ;
	/*
	 * 交货不足限制 UNTTO %
	 */
	private String UNTTO;
	/*
	 * 过量交货限制 UEBTO %
	 */
	private String UEBTO;
	/*
	 * 退货项目 RETPO
	 */
	private String RETPO;
	/*
	 * 需求者/请求者 AFNAM
	 */
	private String AFNAM;
	/*
	 * 制作商零部件编号 MFRPN
	 */
	private String MFRPN;
	/*
	 * 制作商 MFRNR
	 */
	private String MFRNR;
	/*
	 * 导入日期 数据导入日期 取系统时间
	 */
	private String IMPORT_DATE;

	private String UPDATE_DATE;
	/*
	 * 特殊库存类型
	 */
	private String SOBKZ;
	/*
	 * 最大可收货数量（通过数量和过量交货限制计算）
	 */
	private String MAX_MENGE;
	
	/*
	 * 价格单位（基本单位）
	 */
	private String ORDERPR_UN;
	/*
	 * 转换为基本单位的分母 CONV_NUM1 转换为订单单位的分子
	 */
	private String UMREN;
	/*
	 * 转换为基本单位的分子  CONV_DEN1 转换为订单单位的分母
	 */
	private String UMREZ;
	
	
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
	public String getLOEKZ() {
		return LOEKZ;
	}
	public void setLOEKZ(String lOEKZ) {
		LOEKZ = lOEKZ;
	}
	public String getMATNR() {
		return MATNR;
	}
	public void setMATNR(String mATNR) {
		MATNR = mATNR;
	}
	public String getTXZ01() {
		return TXZ01;
	}
	public void setTXZ01(String tXZ01) {
		TXZ01 = tXZ01;
	}
	public String getBUKRS() {
		return BUKRS;
	}
	public void setBUKRS(String bUKRS) {
		BUKRS = bUKRS;
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
	public String getBEDNR() {
		return BEDNR;
	}
	public void setBEDNR(String bEDNR) {
		BEDNR = bEDNR;
	}
	public String getMATKL() {
		return MATKL;
	}
	public void setMATKL(String mATKL) {
		MATKL = mATKL;
	}
	public BigDecimal getMENGE() {
		return MENGE;
	}
	public void setMENGE(BigDecimal mENGE) {
		MENGE = mENGE;
	}
	public String getMEINS() {
		return MEINS;
	}
	public void setMEINS(String mEINS) {
		MEINS = mEINS;
	}
	public String getLMEIN() {
		return LMEIN;
	}
	public void setLMEIN(String lMEIN) {
		LMEIN = lMEIN;
	}
	public String getPSTYP() {
		return PSTYP;
	}
	public void setPSTYP(String pSTYP) {
		PSTYP = pSTYP;
	}
	public String getKNTTP() {
		return KNTTP;
	}
	public void setKNTTP(String kNTTP) {
		KNTTP = kNTTP;
	}
	public String getLEWED() {
		return LEWED;
	}
	public void setLEWED(String lEWED) {
		LEWED = lEWED;
	}
	public String getELIKZ() {
		return ELIKZ;
	}
	public void setELIKZ(String eLIKZ) {
		ELIKZ = eLIKZ;
	}
	public String getUNTTO() {
		return UNTTO;
	}
	public void setUNTTO(String uNTTO) {
		UNTTO = uNTTO;
	}
	public String getUEBTO() {
		return UEBTO;
	}
	public void setUEBTO(String uEBTO) {
		UEBTO = uEBTO;
	}
	public String getRETPO() {
		return RETPO;
	}
	public void setRETPO(String rETPO) {
		RETPO = rETPO;
	}
	public String getAFNAM() {
		return AFNAM;
	}
	public void setAFNAM(String aFNAM) {
		AFNAM = aFNAM;
	}
	public String getMFRPN() {
		return MFRPN;
	}
	public void setMFRPN(String mFRPN) {
		MFRPN = mFRPN;
	}
	public String getMFRNR() {
		return MFRNR;
	}
	public void setMFRNR(String mFRNR) {
		MFRNR = mFRNR;
	}
	public String getIMPORT_DATE() {
		return IMPORT_DATE;
	}
	public void setIMPORT_DATE(String iMPORT_DATE) {
		IMPORT_DATE = iMPORT_DATE;
	}
	public String getUPDATE_DATE() {
		return UPDATE_DATE;
	}
	public void setUPDATE_DATE(String uPDATE_DATE) {
		UPDATE_DATE = uPDATE_DATE;
	}
	public String getSOBKZ() {
		return SOBKZ;
	}
	public void setSOBKZ(String sOBKZ) {
		SOBKZ = sOBKZ;
	}
	public String getMAX_MENGE() {
		return MAX_MENGE;
	}
	public void setMAX_MENGE(String mAX_MENGE) {
		MAX_MENGE = mAX_MENGE;
	}
	public String getORDERPR_UN() {
		return ORDERPR_UN;
	}
	public void setORDERPR_UN(String oRDERPR_UN) {
		ORDERPR_UN = oRDERPR_UN;
	}
	public String getUMREN() {
		return UMREN;
	}
	public void setUMREN(String uMREN) {
		UMREN = uMREN;
	}
	public String getUMREZ() {
		return UMREZ;
	}
	public void setUMREZ(String uMREZ) {
		UMREZ = uMREZ;
	}
	
}
