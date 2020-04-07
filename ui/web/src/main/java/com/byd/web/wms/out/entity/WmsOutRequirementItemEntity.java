package com.byd.web.wms.out.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * WMS出库需求明细
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-10-08 15:12:35
 */
@TableName("WMS_OUT_REQUIREMENT_ITEM")
@KeySequence("SEQ_WMS_OUT_REQUIREMENT_ITEM")
public class WmsOutRequirementItemEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	
	/**
	 * 领料组 领料组（根据领料方式设定的分组）
	 */
	private String modelGroup;
	/**
	 * 工位
	 */
	private String station;
	/**
	 * 采购订单号
	 */
	private String poNo;
	/**
	 * 采购订单行项目号
	 */
	private String poItemNo;
	/**
	 * 生产订单号
	 */
	private String moNo;
	/**
	 * 生产订单行项目号
	 */
	private String moItemNo;
	/**
	 * 预留号
	 */
	private String rsnum;
	/**
	 * 预留行项目号
	 */
	private String rspos;
	/**
	 * 销售订单号
	 */
	private String soNo;
	/**
	 * 销售订单行项目号
	 */
	private String soItemNo;
	/**
	 * SAP交货单号
	 */
	private String sapOutNo;
	/**
	 * SAP交货单行项目号
	 */
	private String sapOutItemNo;
	/**
	 * 成本中心 成本中心发料时必填
	 */
	private String costCenter;
	/**
	 * 研发/内部订单号 研发/内部订单发料时必填
	 */
	private String ioNo;
	/**
	 * WBS元素号 WBS元素发料时必填
	 */
	private String wbs;
	/**
	 * 客户 销售发货时必填
	 */
	private String customer;
	/**
	 * 总账科目编号
	 */
	private String sakto;
	/**
	 * 特殊库存标识 用户处理E库存
	 */
	private String sobkz;
	/**
	 * 记录创建人
	 */
	private String creator;
	/**
	 * 记录创建时间
	 */
	private String createDate;
	/**
	 * 记录修改人
	 */
	private String editor;
	/**
	 * 记录修改时间
	 */
	private String editDate;
	
	/**
	 * 需求号
	 */
	private String requirementNo;
	/**
	 * 需求行项目号
	 */
	private String requirementItemNo;
	/**
	 * WMS业务类型名称
	 */
	private String businessName;
	/**
	 * WMS业务类型代码
	 */
	private String businessType;
	/**
	 * 行项目状态 00 已创建 01 备料中  02 部分下架 03 已下架 04 部分交接 05 已交接 06 关闭
	 */
	private String reqItemStatus;
	/**
	 * 删除标识 0 否 X是 默认0
	 */
	@TableLogic
	private String del;
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
	 * 物料基本单位
	 */
	private String meins;
	/**
	 * 需求数量
	 */
	private Double qty;
	/**
	 * 下架数量
	 */
	private Double qtyXj;
	/**
	 * 实领/发数量(交接) 实际领料数量（只在交接时填充数量，行状态标记依据，实领数量<需求数量，行项目状态为部分完成）
	 */
	private Double qtyReal;
	/**
	 * 冲销/取消数量 凭证冲销 更新此字段，用于计算需求数量
	 */
	private Double qtyCancel;
	/**
	 * 库位
	 */
	private String lgort;
	/**
	 * 接收库位
	 */
	private String receiveLgort;
	/**
	 * 预计箱数
	 */
	private Long boxCount;
	/**
	 * 排序字符串
	 */
	private String sortSeq;
	/**
	 * 供应商 物料所属供应商 可删除
	 */
	private String lifnr;

	/**
	 * 核销标识
	 */
	private String hxFlag;
	
	
	private String sapMatdocNo;
	
	private String sapMatdocItemNo;
	private String autyp;//AUTYP
	
	/**
	 * 设置：领料组 领料组（根据领料方式设定的分组）
	 */
	public void setModelGroup(String modelGroup) {
		this.modelGroup = modelGroup;
	}
	/**
	 * 获取：领料组 领料组（根据领料方式设定的分组）
	 */
	public String getModelGroup() {
		return modelGroup;
	}
	/**
	 * 设置：工位
	 */
	public void setStation(String station) {
		this.station = station;
	}
	/**
	 * 获取：工位
	 */
	public String getStation() {
		return station;
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
	public void setPoItemNo(String poItemNo) {
		this.poItemNo = poItemNo;
	}
	/**
	 * 获取：采购订单行项目号
	 */
	public String getPoLineNo() {
		return poItemNo;
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
	 * 设置：预留号
	 */
	public void setRsnum(String rsnum) {
		this.rsnum = rsnum;
	}
	/**
	 * 获取：预留号
	 */
	public String getRsnum() {
		return rsnum;
	}
	/**
	 * 设置：预留行项目号
	 */
	public void setRspos(String rspos) {
		this.rspos = rspos;
	}
	/**
	 * 获取：预留行项目号
	 */
	public String getRspos() {
		return rspos;
	}
	/**
	 * 设置：销售订单号
	 */
	public void setSoNo(String soNo) {
		this.soNo = soNo;
	}
	/**
	 * 获取：销售订单号
	 */
	public String getSoNo() {
		return soNo;
	}
	/**
	 * 设置：销售订单行项目号
	 */
	public void setSoItemNo(String soItemNo) {
		this.soItemNo = soItemNo;
	}
	/**
	 * 获取：销售订单行项目号
	 */
	public String getSoItemNo() {
		return soItemNo;
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
	 * 设置：成本中心 成本中心发料时必填
	 */
	public void setCostCenter(String costCenter) {
		this.costCenter = costCenter;
	}
	/**
	 * 获取：成本中心 成本中心发料时必填
	 */
	public String getCostCenter() {
		return costCenter;
	}
	/**
	 * 设置：研发/内部订单号 研发/内部订单发料时必填
	 */
	public void setIoNo(String ioNo) {
		this.ioNo = ioNo;
	}
	/**
	 * 获取：研发/内部订单号 研发/内部订单发料时必填
	 */
	public String getIoNo() {
		return ioNo;
	}
	/**
	 * 设置：WBS元素号 WBS元素发料时必填
	 */
	public void setWbs(String wbs) {
		this.wbs = wbs;
	}
	/**
	 * 获取：WBS元素号 WBS元素发料时必填
	 */
	public String getWbs() {
		return wbs;
	}
	/**
	 * 设置：客户 销售发货时必填
	 */
	public void setCustomer(String customer) {
		this.customer = customer;
	}
	/**
	 * 获取：客户 销售发货时必填
	 */
	public String getCustomer() {
		return customer;
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
	 * 设置：特殊库存标识 用户处理E库存
	 */
	public void setSobkz(String sobkz) {
		this.sobkz = sobkz;
	}
	/**
	 * 获取：特殊库存标识 用户处理E库存
	 */
	public String getSobkz() {
		return sobkz;
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
	 * 设置：记录修改人
	 */
	public void setEditor(String editor) {
		this.editor = editor;
	}
	/**
	 * 获取：记录修改人
	 */
	public String getEditor() {
		return editor;
	}
	/**
	 * 设置：记录修改时间
	 */
	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}
	/**
	 * 获取：记录修改时间
	 */
	public String getEditDate() {
		return editDate;
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
	 * 设置：需求号
	 */
	public void setRequirementNo(String requirementNo) {
		this.requirementNo = requirementNo;
	}
	/**
	 * 获取：需求号
	 */
	public String getRequirementNo() {
		return requirementNo;
	}
	/**
	 * 设置：需求行项目号
	 */
	public void setRequirementItemNo(String requirementItemNo) {
		this.requirementItemNo = requirementItemNo;
	}
	/**
	 * 获取：需求行项目号
	 */
	public String getRequirementItemNo() {
		return requirementItemNo;
	}
	/**
	 * 设置：WMS业务类型名称
	 */
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	/**
	 * 获取：WMS业务类型名称
	 */
	public String getBusinessName() {
		return businessName;
	}
	/**
	 * 设置：WMS业务类型代码
	 */
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	/**
	 * 获取：WMS业务类型代码
	 */
	public String getBusinessType() {
		return businessType;
	}
	/**
	 * 设置：行项目状态 00 已创建 01 备料中  02 部分下架 03 已下架 04 部分交接 05 已交接 06 关闭
	 */
	public void setReqItemStatus(String reqItemStatus) {
		this.reqItemStatus = reqItemStatus;
	}
	/**
	 * 获取：行项目状态 00 已创建 01 备料中  02 部分下架 03 已下架 04 部分交接 05 已交接 06 关闭
	 */
	public String getReqItemStatus() {
		return reqItemStatus;
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
	/**
	 * 设置：需求数量
	 */
	public void setQty(Double qty) {
		this.qty = qty;
	}
	/**
	 * 获取：需求数量
	 */
	public Double getQty() {
		return qty;
	}
	/**
	 * 设置：下架数量
	 */
	public void setQtyXj(Double qtyXj) {
		this.qtyXj = qtyXj;
	}
	/**
	 * 获取：下架数量
	 */
	public Double getQtyXj() {
		return qtyXj;
	}
	/**
	 * 设置：实领/发数量(交接) 实际领料数量（只在交接时填充数量，行状态标记依据，实领数量<需求数量，行项目状态为部分完成）
	 */
	public void setQtyReal(Double qtyReal) {
		this.qtyReal = qtyReal;
	}
	/**
	 * 获取：实领/发数量(交接) 实际领料数量（只在交接时填充数量，行状态标记依据，实领数量<需求数量，行项目状态为部分完成）
	 */
	public Double getQtyReal() {
		return qtyReal;
	}
	/**
	 * 设置：冲销/取消数量 凭证冲销 更新此字段，用于计算需求数量
	 */
	public void setQtyCancel(Double qtyCancel) {
		this.qtyCancel = qtyCancel;
	}
	/**
	 * 获取：冲销/取消数量 凭证冲销 更新此字段，用于计算需求数量
	 */
	public Double getQtyCancel() {
		return qtyCancel;
	}
	/**
	 * 设置：库位
	 */
	public void setLgort(String lgort) {
		this.lgort = lgort;
	}
	/**
	 * 获取：库位
	 */
	public String getLgort() {
		return lgort;
	}
	/**
	 * 设置：接收库位
	 */
	public void setReceiveLgort(String receiveLgort) {
		this.receiveLgort = receiveLgort;
	}
	/**
	 * 获取：接收库位
	 */
	public String getReceiveLgort() {
		return receiveLgort;
	}
	/**
	 * 设置：预计箱数
	 */
	public void setBoxCount(Long boxCount) {
		this.boxCount = boxCount;
	}
	/**
	 * 获取：预计箱数
	 */
	public Long getBoxCount() {
		return boxCount;
	}
	/**
	 * 设置：排序字符串
	 */
	public void setSortSeq(String sortSeq) {
		this.sortSeq = sortSeq;
	}
	/**
	 * 获取：排序字符串
	 */
	public String getSortSeq() {
		return sortSeq;
	}
	/**
	 * 设置：供应商 物料所属供应商 可删除
	 */
	public void setLifnr(String lifnr) {
		this.lifnr = lifnr;
	}
	/**
	 * 获取：供应商 物料所属供应商 可删除
	 */
	public String getLifnr() {
		return lifnr;
	}
	public String getHxFlag() {
		return hxFlag;
	}
	public void setHxFlag(String hxFlag) {
		this.hxFlag = hxFlag;
	}
	public String getMeins() {
		return meins;
	}
	public void setMeins(String meins) {
		this.meins = meins;
	}
	public String getSapMatdocNo() {
		return sapMatdocNo;
	}
	public void setSapMatdocNo(String sapMatdocNo) {
		this.sapMatdocNo = sapMatdocNo;
	}
	public String getSapMatdocItemNo() {
		return sapMatdocItemNo;
	}
	public void setSapMatdocItemNo(String sapMatdocItemNo) {
		this.sapMatdocItemNo = sapMatdocItemNo;
	}
	public String getAutyp() {
		return autyp;
	}
	public void setAutyp(String autyp) {
		this.autyp = autyp;
	}
}
