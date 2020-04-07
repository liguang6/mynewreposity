package com.byd.wms.business.modules.in.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 进仓单-明细
 * 
 * @author cscc
 * @email 
 * @date 2018-08-27 10:54:57
 */
@TableName("WMS_IN_INBOUND_ITEM")
@KeySequence("SEQ_WMS_IN_INBOUND_ITEM")
public class WmsInInboundItemEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 进仓单编号
	 */
	private String inboundNo;
	/**
	 * 行项目号
	 */
	private String inboundItemNo;
	/**
	 * 行文本
	 */
	private String itemText;
	/**
	 * 行项目状态 字典定义：进仓单状态（00创建，01部分进仓，02已完成，04关闭）
	 */
	private String itemStatus;
	/**
	 * WMS业务类型代码
	 */
	private String businessCode;
	/**
	 * 删除标识 0 否 X是 默认0
	 */
	private String del;
	/**
	 * 源工厂代码（发货）
	 */
	private String fWerks;
	/**
	 * 源仓库号（发货）
	 */
	private String fWhNumber;
	/**
	 * 源库位（发货）
	 */
	private String fLgort;
	/**
	 * 源批次（发货）
	 */
	private String fBatch;
	/**
	 * 工厂（进仓）
	 */
	private String werks;
	/**
	 * 仓库号（进仓）
	 */
	private String whNumber;
	/**
	 * 库位（进仓）
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
	 * 储位（进仓）
	 */
	private String binCode;
	/**
	 * 源单位
	 */
	private String fUnit;
	/**
	 * 单位(物料基本计量单位)
	 */
	private String unit;
	/**
	 * 源数量（源单位）
	 */
	private Long fQty;
	/**
	 * 进仓数量（源单位）
	 */
	private Long qty;
	/**
	 * 试装数量（源单位）
	 */
	private Long tryQty;
	/**
	 * WMS批次
	 */
	private String batch;
	/**
	 * 已进仓数量（源单位）
	 */
	private Long realQty;
	/**
	 * 特殊库存类型 字典定义：特殊库存类型 K, O, E, Q
	 */
	private String sobkz;
	/**
	 * 满箱数量（定额数量）
	 */
	private Long fullBoxQty;
	/**
	 * 箱数
	 */
	private Long boxCount;
	/**
	 * 仓管员
	 */
	private String whManager;
	/**
	 * 收货单号
	 */
	private String receiptNo;
	/**
	 * 收货单行项目号
	 */
	private String receiptItemNo;
	/**
	 * 供应商
	 */
	private String lifnr;
	/**
	 * 供应商名称
	 */
	private String liktx;
	/**
	 * 成本中心
	 */
	private String costCenter;
	/**
	 * 内部/生产订单号
	 */
	private String ioNo;
	/**
	 * WBS元素号
	 */
	private String wbs;
	/**
	 * 总账科目编号
	 */
	private String sakto;
	/**
	 * 主资产号
	 */
	private String anln1;
	/**
	 * 生产订单号
	 */
	private String moNo;
	/**
	 * 生产订单行项目号
	 */
	private String moItemNo;
	/**
	 * 生产日期
	 */
	private String productDate;
	/**
	 * 记录创建人
	 */
	private String creator;
	/**
	 * 记录创建时间
	 */
	private String createDate;

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
	 * 设置：进仓单编号
	 */
	public void setInboundNo(String inboundNo) {
		this.inboundNo = inboundNo;
	}
	/**
	 * 获取：进仓单编号
	 */
	public String getInboundNo() {
		return inboundNo;
	}
	/**
	 * 设置：行项目号
	 */
	public void setInboundItemNo(String inboundItemNo) {
		this.inboundItemNo = inboundItemNo;
	}
	/**
	 * 获取：行项目号
	 */
	public String getInboundItemNo() {
		return inboundItemNo;
	}
	/**
	 * 设置：行文本
	 */
	public void setItemText(String itemText) {
		this.itemText = itemText;
	}
	/**
	 * 获取：行文本
	 */
	public String getItemText() {
		return itemText;
	}
	/**
	 * 设置：行项目状态 字典定义：进仓单状态（00创建，01部分进仓，02已完成，04关闭）
	 */
	public void setItemStatus(String itemStatus) {
		this.itemStatus = itemStatus;
	}
	/**
	 * 获取：行项目状态 字典定义：进仓单状态（00创建，01部分进仓，02已完成，04关闭）
	 */
	public String getItemStatus() {
		return itemStatus;
	}
	/**
	 * 设置：WMS业务类型代码
	 */
	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}
	/**
	 * 获取：WMS业务类型代码
	 */
	public String getBusinessCode() {
		return businessCode;
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
	 * 设置：源工厂代码（发货）
	 */
	public void setFWerks(String fWerks) {
		this.fWerks = fWerks;
	}
	/**
	 * 获取：源工厂代码（发货）
	 */
	public String getFWerks() {
		return fWerks;
	}
	/**
	 * 设置：源仓库号（发货）
	 */
	public void setFWhNumber(String fWhNumber) {
		this.fWhNumber = fWhNumber;
	}
	/**
	 * 获取：源仓库号（发货）
	 */
	public String getFWhNumber() {
		return fWhNumber;
	}
	/**
	 * 设置：源库位（发货）
	 */
	public void setFLgort(String fLgort) {
		this.fLgort = fLgort;
	}
	/**
	 * 获取：源库位（发货）
	 */
	public String getFLgort() {
		return fLgort;
	}
	/**
	 * 设置：源批次（发货）
	 */
	public void setFBatch(String fBatch) {
		this.fBatch = fBatch;
	}
	/**
	 * 获取：源批次（发货）
	 */
	public String getFBatch() {
		return fBatch;
	}
	/**
	 * 设置：工厂（进仓）
	 */
	public void setWerks(String werks) {
		this.werks = werks;
	}
	/**
	 * 获取：工厂（进仓）
	 */
	public String getWerks() {
		return werks;
	}
	/**
	 * 设置：仓库号（进仓）
	 */
	public void setWhNumber(String whNumber) {
		this.whNumber = whNumber;
	}
	/**
	 * 获取：仓库号（进仓）
	 */
	public String getWhNumber() {
		return whNumber;
	}
	/**
	 * 设置：库位（进仓）
	 */
	public void setLgort(String lgort) {
		this.lgort = lgort;
	}
	/**
	 * 获取：库位（进仓）
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
	 * 设置：储位（进仓）
	 */
	public void setBinCode(String binCode) {
		this.binCode = binCode;
	}
	/**
	 * 获取：储位（进仓）
	 */
	public String getBinCode() {
		return binCode;
	}
	/**
	 * 设置：源单位
	 */
	public void setFUnit(String fUnit) {
		this.fUnit = fUnit;
	}
	/**
	 * 获取：源单位
	 */
	public String getFUnit() {
		return fUnit;
	}
	/**
	 * 设置：单位(物料基本计量单位)
	 */
	public void setUnit(String unit) {
		this.unit = unit;
	}
	/**
	 * 获取：单位(物料基本计量单位)
	 */
	public String getUnit() {
		return unit;
	}
	/**
	 * 设置：源数量（源单位）
	 */
	public void setFQty(Long fQty) {
		this.fQty = fQty;
	}
	/**
	 * 获取：源数量（源单位）
	 */
	public Long getFQty() {
		return fQty;
	}
	/**
	 * 设置：进仓数量（源单位）
	 */
	public void setQty(Long qty) {
		this.qty = qty;
	}
	/**
	 * 获取：进仓数量（源单位）
	 */
	public Long getQty() {
		return qty;
	}
	/**
	 * 设置：试装数量（源单位）
	 */
	public void setTryQty(Long tryQty) {
		this.tryQty = tryQty;
	}
	/**
	 * 获取：试装数量（源单位）
	 */
	public Long getTryQty() {
		return tryQty;
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
	 * 设置：已进仓数量（源单位）
	 */
	public void setRealQty(Long realQty) {
		this.realQty = realQty;
	}
	/**
	 * 获取：已进仓数量（源单位）
	 */
	public Long getRealQty() {
		return realQty;
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
	 * 设置：满箱数量（定额数量）
	 */
	public void setFullBoxQty(Long fullBoxQty) {
		this.fullBoxQty = fullBoxQty;
	}
	/**
	 * 获取：满箱数量（定额数量）
	 */
	public Long getFullBoxQty() {
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
	 * 设置：仓管员
	 */
	public void setWhManager(String whManager) {
		this.whManager = whManager;
	}
	/**
	 * 获取：仓管员
	 */
	public String getWhManager() {
		return whManager;
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
	 * 设置：收货单行项目号
	 */
	public void setReceiptItemNo(String receiptItemNo) {
		this.receiptItemNo = receiptItemNo;
	}
	/**
	 * 获取：收货单行项目号
	 */
	public String getReceiptItemNo() {
		return receiptItemNo;
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
	 * 设置：成本中心
	 */
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}
	/**
	 * 获取：成本中心
	 */
	public String getCostCenter() {
		return costCenter;
	}
	/**
	 * 设置：内部/生产订单号
	 */
	public void setIoNo(String ioNo) {
		this.ioNo = ioNo;
	}
	/**
	 * 获取：内部/生产订单号
	 */
	public String getIoNo() {
		return ioNo;
	}
	/**
	 * 设置：WBS元素号
	 */
	public void setWbs(String wbs) {
		this.wbs = wbs;
	}
	/**
	 * 获取：WBS元素号
	 */
	public String getWbs() {
		return wbs;
	}
	/**
	 * 设置：总账科目编号
	 */
	public void setSakto(String sakto) {
		this.sakto = sakto;
	}
	/**
	 * 获取：总账科目编号
	 */
	public String getSakto() {
		return sakto;
	}
	/**
	 * 设置：主资产号
	 */
	public void setAnln1(String anln1) {
		this.anln1 = anln1;
	}
	/**
	 * 获取：主资产号
	 */
	public String getAnln1() {
		return anln1;
	}
	/**
	 * 设置：生产订单号
	 */
	public void setMoNo(String moNo) {
		this.moNo = moNo;
	}
	/**
	 * 获取：生产订单号
	 */
	public String getMoNo() {
		return moNo;
	}
	/**
	 * 设置：生产订单行项目号
	 */
	public void setMoItemNo(String moItemNo) {
		this.moItemNo = moItemNo;
	}
	/**
	 * 获取：生产订单行项目号
	 */
	public String getMoItemNo() {
		return moItemNo;
	}
	/**
	 * 设置：生产日期
	 */
	public void setProductDate(String productDate) {
		this.productDate = productDate;
	}
	/**
	 * 获取：生产日期
	 */
	public String getProductDate() {
		return productDate;
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
}
