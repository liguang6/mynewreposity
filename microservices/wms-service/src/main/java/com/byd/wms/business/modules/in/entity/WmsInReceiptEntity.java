package com.byd.wms.business.modules.in.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * WMS收货单
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-20 16:06:38
 */
@TableName("WMS_IN_RECEIPT")
@KeySequence("SEQ_WMS_IN_RECEIPT")
public class WmsInReceiptEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	
	/**
	 * 实收数量
	 */
	private Double receiptQty;
	/**
	 * WMS批次
	 */
	private String batch;
	/**
	 * 特殊库存类型 字典定义：特殊库存类型 K, O, E, Q
	 */
	private String sobkz;
	/**
	 * SCM送货单号
	 */
	private String asnno;
	/**
	 * SCM送货单行项目号
	 */
	private String asnitm;
	/**
	 * 采购订单号
	 */
	private String poNo;
	/**
	 * 采购订单行项目号
	 */
	private String poItemNo;
	/**
	 * SAP交货单号
	 */
	private String sapOutNo;
	/**
	 * SAP交货单行项目号
	 */
	private String sapOutItemNo;
	/**
	 * SAP303凭证号
	 */
	@TableField(value="SAP_MATDOC_NO")
	private String sap303No;
	/**
	 * SAP303凭证行项目号
	 */
	@TableField(value = "SAP_MATDOC_ITEM_NO")
	private String sap303ItemNo;
	/**
	 * 供应商
	 */
	private String lifnr;
	/**
	 * 供应商描述
	 */
	private String liktx;
	/**
	 * 满箱数量（定额数量）
	 */
	private Double fullBoxQty;
	/**
	 * 箱数
	 */
	private Long boxCount;
	/**
	 * 申请人
	 */
	private String afnam;
	/**
	 * 需求跟踪号
	 */
	private String bednr;
	/**
	 * 收货日期
	 */
	private String receiptDate;
	/**
	 * 收料员
	 */
	private String receiver;
	/**
	 * 收料房存放区
	 */
	private String grArea;
	/**
	 * 可退货数量
	 */
	private Double returnableQty;
	/**
	 * 退货数量
	 */
	private Double returnQty;
	/**
	 * 可进仓数量
	 */
	private Double inableQty;
	/**
	 * 进仓数量
	 */
	private Double inQty;
	/**
	 * 试装数量
	 */
	private Double tryQty;
	/**
	 * 试装进仓数量
	 */
	private Double tryInQty;
	/**
	 * 破坏数量
	 */
	private Long destroyQty;
	/**
	 * IQC成本中心
	 */
	private String iqcCostCenter;
	/**
	 * 记录创建人
	 */
	private String creator;
	/**
	 * 记录创建时间
	 */
	private String createDate;
	
	/**
	 * 收货单号
	 */
	private String receiptNo;
	/**
	 * 行项目号
	 */
	private String receiptItemNo;
	/**
	 * 删除标识 0 否 X是 默认0
	 */
	private String del;
	/**
	 * 发货工厂代码
	 */
	private String fWerks;
	/**
	 * 发货仓库号
	 */
	private String fWhNumber;
	/**
	 * 发货库位
	 */
	private String fLgort;
	/**
	 * 发货批次（源批次）
	 */
	private String fBatch;
	/**
	 * 收货工厂
	 */
	private String werks;
	/**
	 * 收货仓库号
	 */
	private String whNumber;
	/**
	 * 收货库位
	 */
	private String lgort;
	/**
	 * 物料号
	 */
	private String matnr;
	/**
	 * 物料描述
	 */
	private String maktx;
	/**
	 * 单位
	 */
	private String unit;

	/**
	 * 设置：实收数量
	 */
	public void setReceiptQty(Double qty) {
		this.receiptQty = qty;
	}
	/**
	 * 获取：实收数量
	 */
	public Double getReceiptQty() {
		return receiptQty;
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
	 * 设置：特殊库存类型 字典定义：特殊库存类型 K, O, E, Q
	 */
	public void setSobkz(String sobkz) {
		this.sobkz = sobkz;
	}
	/**
	 * 获取：特殊库存类型 字典定义：特殊库存类型 K, O, E, Q
	 */
	public String getSobkz() {
		return sobkz;
	}
	/**
	 * 设置：SCM送货单号
	 */
	public void setAsnno(String asnno) {
		this.asnno = asnno;
	}
	/**
	 * 获取：SCM送货单号
	 */
	public String getAsnno() {
		return asnno;
	}
	/**
	 * 设置：SCM送货单行项目号
	 */
	public void setAsnitm(String asnitm) {
		this.asnitm = asnitm;
	}
	/**
	 * 获取：SCM送货单行项目号
	 */
	public String getAsnitm() {
		return asnitm;
	}
	/**
	 * 设置：采购订单号
	 */
	public void setPoNo(String poNo) {
		this.poNo = poNo;
	}
	/**
	 * 获取：采购订单号
	 */
	public String getPoNo() {
		return poNo;
	}
	/**
	 * 设置：采购订单行项目号
	 */
	public void setPoItemNo(String poLineNo) {
		this.poItemNo = poLineNo;
	}
	/**
	 * 获取：采购订单行项目号
	 */
	public String getPoItemNo() {
		return poItemNo;
	}
	/**
	 * 设置：SAP交货单号
	 */
	public void setSapOutNo(String sapOutNo) {
		this.sapOutNo = sapOutNo;
	}
	/**
	 * 获取：SAP交货单号
	 */
	public String getSapOutNo() {
		return sapOutNo;
	}
	/**
	 * 设置：SAP交货单行项目号
	 */
	public void setSapOutItemNo(String sapOutItemNo) {
		this.sapOutItemNo = sapOutItemNo;
	}
	/**
	 * 获取：SAP交货单行项目号
	 */
	public String getSapOutItemNo() {
		return sapOutItemNo;
	}
	/**
	 * 设置：SAP303凭证号
	 */
	public void setSap303No(String sap303No) {
		this.sap303No = sap303No;
	}
	/**
	 * 获取：SAP303凭证号
	 */
	public String getSap303No() {
		return sap303No;
	}
	/**
	 * 设置：SAP303凭证行项目号
	 */
	public void setSap303ItemNo(String sap303ItemNo) {
		this.sap303ItemNo = sap303ItemNo;
	}
	/**
	 * 获取：SAP303凭证行项目号
	 */
	public String getSap303ItemNo() {
		return sap303ItemNo;
	}
	/**
	 * 设置：供应商
	 */
	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
	}
	/**
	 * 获取：供应商
	 */
	public String getLifnr() {
		return lifnr;
	}
	/**
	 * 设置：供应商描述
	 */
	public void setLiktx(String liktx) {
		this.liktx = liktx;
	}
	/**
	 * 获取：供应商描述
	 */
	public String getLiktx() {
		return liktx;
	}
	/**
	 * 设置：满箱数量（定额数量）
	 */
	public void setFullBoxQty(Double fullBoxQty) {
		this.fullBoxQty = fullBoxQty;
	}
	/**
	 * 获取：满箱数量（定额数量）
	 */
	public Double getFullBoxQty() {
		return fullBoxQty;
	}
	/**
	 * 设置：箱数
	 */
	public void setBoxCount(Long boxCount) {
		this.boxCount = boxCount;
	}
	/**
	 * 获取：箱数
	 */
	public Long getBoxCount() {
		return boxCount;
	}
	/**
	 * 设置：申请人
	 */
	public void setAfnam(String afnam) {
		this.afnam = afnam;
	}
	/**
	 * 获取：申请人
	 */
	public String getAfnam() {
		return afnam;
	}
	/**
	 * 设置：需求跟踪号
	 */
	public void setBednr(String bednr) {
		this.bednr = bednr;
	}
	/**
	 * 获取：需求跟踪号
	 */
	public String getBednr() {
		return bednr;
	}
	/**
	 * 设置：收货日期
	 */
	public void setReceiptDate(String receiptDate) {
		this.receiptDate = receiptDate;
	}
	/**
	 * 获取：收货日期
	 */
	public String getReceiptDate() {
		return receiptDate;
	}
	/**
	 * 设置：收料员
	 */
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	/**
	 * 获取：收料员
	 */
	public String getReceiver() {
		return receiver;
	}
	/**
	 * 设置：收料房存放区
	 */
	public void setGrArea(String grArea) {
		this.grArea = grArea;
	}
	/**
	 * 获取：收料房存放区
	 */
	public String getGrArea() {
		return grArea;
	}
	/**
	 * 设置：可退货数量
	 */
	public void setReturnableQty(Double returnableQty) {
		this.returnableQty = returnableQty;
	}
	/**
	 * 获取：可退货数量
	 */
	public Double getReturnableQty() {
		return returnableQty;
	}
	/**
	 * 设置：退货数量
	 */
	public void setReturnQty(Double returnQty) {
		this.returnQty = returnQty;
	}
	/**
	 * 获取：退货数量
	 */
	public Double getReturnQty() {
		return returnQty;
	}
	/**
	 * 设置：可进仓数量
	 */
	public void setInableQty(Double inableQty) {
		this.inableQty = inableQty;
	}
	/**
	 * 获取：可进仓数量
	 */
	public Double getInableQty() {
		return inableQty;
	}
	/**
	 * 设置：进仓数量
	 */
	public void setInQty(Double inQty) {
		this.inQty = inQty;
	}
	/**
	 * 获取：进仓数量
	 */
	public Double getInQty() {
		return inQty;
	}
	/**
	 * 设置：试装数量
	 */
	public void setTryQty(Double tryQty) {
		this.tryQty = tryQty;
	}
	/**
	 * 获取：试装数量
	 */
	public Double getTryQty() {
		return tryQty;
	}
	/**
	 * 设置：试装进仓数量
	 */
	public void setTryInQty(Double tryInQty) {
		this.tryInQty = tryInQty;
	}
	/**
	 * 获取：试装进仓数量
	 */
	public Double getTryInQty() {
		return tryInQty;
	}
	/**
	 * 设置：破坏数量
	 */
	public void setDestroyQty(Long destroyQty) {
		this.destroyQty = destroyQty;
	}
	/**
	 * 获取：破坏数量
	 */
	public Long getDestroyQty() {
		return destroyQty;
	}
	/**
	 * 设置：IQC成本中心
	 */
	public void setIqcCostCenter(String iqcCostCenter) {
		this.iqcCostCenter = iqcCostCenter;
	}
	/**
	 * 获取：IQC成本中心
	 */
	public String getIqcCostCenter() {
		return iqcCostCenter;
	}
	/**
	 * 设置：记录创建人
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	/**
	 * 获取：记录创建人
	 */
	public String getCreator() {
		return creator;
	}
	/**
	 * 设置：记录创建时间
	 */
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	/**
	 * 获取：记录创建时间
	 */
	public String getCreateDate() {
		return createDate;
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
	 * 设置：收货单号
	 */
	public void setReceiptNo(String receiptNo) {
		this.receiptNo = receiptNo;
	}
	/**
	 * 获取：收货单号
	 */
	public String getReceiptNo() {
		return receiptNo;
	}
	/**
	 * 设置：行项目号
	 */
	public void setReceiptItemNo(String receiptItemNo) {
		this.receiptItemNo = receiptItemNo;
	}
	/**
	 * 获取：行项目号
	 */
	public String getReceiptItemNo() {
		return receiptItemNo;
	}
	/**
	 * 设置：删除标识 0 否 X是 默认0
	 */
	public void setDel(String del) {
		this.del = del;
	}
	/**
	 * 获取：删除标识 0 否 X是 默认0
	 */
	public String getDel() {
		return del;
	}
	/**
	 * 设置：发货工厂代码
	 */
	public void setFWerks(String fWerks) {
		this.fWerks = fWerks;
	}
	/**
	 * 获取：发货工厂代码
	 */
	public String getFWerks() {
		return fWerks;
	}
	/**
	 * 设置：发货仓库号
	 */
	public void setFWhNumber(String fWhNumber) {
		this.fWhNumber = fWhNumber;
	}
	/**
	 * 获取：发货仓库号
	 */
	public String getFWhNumber() {
		return fWhNumber;
	}
	/**
	 * 设置：发货库位
	 */
	public void setFLgort(String fLgort) {
		this.fLgort = fLgort;
	}
	/**
	 * 获取：发货库位
	 */
	public String getFLgort() {
		return fLgort;
	}
	/**
	 * 设置：发货批次（源批次）
	 */
	public void setFBatch(String fBatch) {
		this.fBatch = fBatch;
	}
	/**
	 * 获取：发货批次（源批次）
	 */
	public String getFBatch() {
		return fBatch;
	}
	/**
	 * 设置：收货工厂
	 */
	public void setWerks(String werks) {
		this.werks = werks;
	}
	/**
	 * 获取：收货工厂
	 */
	public String getWerks() {
		return werks;
	}
	/**
	 * 设置：收货仓库号
	 */
	public void setWhNumber(String whNumber) {
		this.whNumber = whNumber;
	}
	/**
	 * 获取：收货仓库号
	 */
	public String getWhNumber() {
		return whNumber;
	}
	/**
	 * 设置：收货库位
	 */
	public void setLgort(String lgort) {
		this.lgort = lgort;
	}
	/**
	 * 获取：收货库位
	 */
	public String getLgort() {
		return lgort;
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
	 * 设置：单位
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
	/**
	 * 获取：单位
	 */
	public String getUnit() {
		return unit;
	}
}
