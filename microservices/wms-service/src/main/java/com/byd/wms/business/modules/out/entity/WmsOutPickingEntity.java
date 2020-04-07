package com.byd.wms.business.modules.out.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * WMS拣配下架表
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2019-01-07 17:03:25
 */
@TableName("WMS_OUT_PICKING")
@KeySequence("SEQ_WMS_OUT_PICKING")
public class WmsOutPickingEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 拣配单号
	 */
	private String pickNo;
	/**
	 * 拣配单行项目号
	 */
	private String pickItemNo;
	/**
	 * 参考WMS业务单据号
	 */
	private String refBusinessNo;
	/**
	 * 参考WMS业务单据行项目号
	 */
	private String refBusinessItemNo;
	/**
	 * WMS业务类型代码
	 */
	private String businessCode;
	/**
	 * WMS业务类型名称
	 */
	private String businessName;
	/**
	 * WMS业务类型
	 */
	private String businessType;
	/**
	 * 工厂代码
	 */
	private String werks;
	/**
	 * 仓库号
	 */
	private String whNumber;
	/**
	 * 行项目状态 00 已创建 01 已拣配 02 部分交接 03 已交接
	 */
	private String reqItemStatus;
	/**
	 * 行文本
	 */
	private String itemText;
	/**
	 * 删除标识 空 否 X是 默认空
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
	 * 供应商 物料所属供应商 可删除
	 */
	private String lifnr;
	/**
	 * 储位
	 */
	private String binCode;
	/**
	 * 库位
	 */
	private String lgort;
	/**
	 * 下架储位
	 */
	private String binCodeXj;
	/**
	 * 非限制拣配下架数量
	 */
	private Long qty;
	/**
	 * 锁定拣配下架数量
	 */
	private Long qtyLock;
	/**
	 * 批次
	 */
	private String batch;
	/**
	 * 非限制交接过账数量
	 */
	private Long handoverQty;
	/**
	 * 锁定交接过账数量
	 */
	private Long handoverQtyLock;
	/**
	 * 接收库位 receive
	 */
	private String lgortReceive;
	/**
	 * 特殊库存标识
	 */
	private String sobkz;
	/**
	 * 核销标识 默认 0 否 X 是
	 */
	private String hxFlag;
	/**
	 * 接收人
	 */
	private String receiver;
	/**
	 * 交接时间
	 */
	private String handoverDate;
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
	 * 设置：拣配单号
	 */
	public void setPickNo(String pickNo) {
		this.pickNo = pickNo;
	}
	/**
	 * 获取：拣配单号
	 */
	public String getPickNo() {
		return pickNo;
	}
	/**
	 * 设置：拣配单行项目号
	 */
	public void setPickItemNo(String pickItemNo) {
		this.pickItemNo = pickItemNo;
	}
	/**
	 * 获取：拣配单行项目号
	 */
	public String getPickItemNo() {
		return pickItemNo;
	}
	/**
	 * 设置：参考WMS业务单据号
	 */
	public void setRefBusinessNo(String refBusinessNo) {
		this.refBusinessNo = refBusinessNo;
	}
	/**
	 * 获取：参考WMS业务单据号
	 */
	public String getRefBusinessNo() {
		return refBusinessNo;
	}
	/**
	 * 设置：参考WMS业务单据行项目号
	 */
	public void setRefBusinessItemNo(String refBusinessItemNo) {
		this.refBusinessItemNo = refBusinessItemNo;
	}
	/**
	 * 获取：参考WMS业务单据行项目号
	 */
	public String getRefBusinessItemNo() {
		return refBusinessItemNo;
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
	 * 设置：WMS业务类型
	 */
	public void setBusinessType(String businessType) {
		this.businessType = businessType;
	}
	/**
	 * 获取：WMS业务类型
	 */
	public String getBusinessType() {
		return businessType;
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
	 * 设置：行项目状态 00 已创建 01 已拣配 02 部分交接 03 已交接
	 */
	public void setReqItemStatus(String reqItemStatus) {
		this.reqItemStatus = reqItemStatus;
	}
	/**
	 * 获取：行项目状态 00 已创建 01 已拣配 02 部分交接 03 已交接
	 */
	public String getReqItemStatus() {
		return reqItemStatus;
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
	 * 设置：删除标识 空 否 X是 默认空
	 */
	public void setDel(String del) {
		this.del = del;
	}
	/**
	 * 获取：删除标识 空 否 X是 默认空
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
	/**
	 * 设置：储位
	 */
	public void setBinCode(String binCode) {
		this.binCode = binCode;
	}
	/**
	 * 获取：储位
	 */
	public String getBinCode() {
		return binCode;
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
	 * 设置：下架储位
	 */
	public void setBinCodeXj(String binCodeXj) {
		this.binCodeXj = binCodeXj;
	}
	/**
	 * 获取：下架储位
	 */
	public String getBinCodeXj() {
		return binCodeXj;
	}
	/**
	 * 设置：非限制拣配下架数量
	 */
	public void setQty(Long qty) {
		this.qty = qty;
	}
	/**
	 * 获取：非限制拣配下架数量
	 */
	public Long getQty() {
		return qty;
	}
	/**
	 * 设置：锁定拣配下架数量
	 */
	public void setQtyLock(Long qtyLock) {
		this.qtyLock = qtyLock;
	}
	/**
	 * 获取：锁定拣配下架数量
	 */
	public Long getQtyLock() {
		return qtyLock;
	}
	/**
	 * 设置：批次
	 */
	public void setBatch(String batch) {
		this.batch = batch;
	}
	/**
	 * 获取：批次
	 */
	public String getBatch() {
		return batch;
	}
	/**
	 * 设置：非限制交接过账数量
	 */
	public void setHandoverQty(Long handoverQty) {
		this.handoverQty = handoverQty;
	}
	/**
	 * 获取：非限制交接过账数量
	 */
	public Long getHandoverQty() {
		return handoverQty;
	}
	/**
	 * 设置：锁定交接过账数量
	 */
	public void setHandoverQtyLock(Long handoverQtyLock) {
		this.handoverQtyLock = handoverQtyLock;
	}
	/**
	 * 获取：锁定交接过账数量
	 */
	public Long getHandoverQtyLock() {
		return handoverQtyLock;
	}
	/**
	 * 设置：接收库位 receive
	 */
	public void setLgortReceive(String lgortReceive) {
		this.lgortReceive = lgortReceive;
	}
	/**
	 * 获取：接收库位 receive
	 */
	public String getLgortReceive() {
		return lgortReceive;
	}
	/**
	 * 设置：特殊库存标识
	 */
	public void setSobkz(String sobkz) {
		this.sobkz = sobkz;
	}
	/**
	 * 获取：特殊库存标识
	 */
	public String getSobkz() {
		return sobkz;
	}
	/**
	 * 设置：核销标识 默认 0 否 X 是
	 */
	public void setHxFlag(String hxFlag) {
		this.hxFlag = hxFlag;
	}
	/**
	 * 获取：核销标识 默认 0 否 X 是
	 */
	public String getHxFlag() {
		return hxFlag;
	}
	/**
	 * 设置：接收人
	 */
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}
	/**
	 * 获取：接收人
	 */
	public String getReceiver() {
		return receiver;
	}
	/**
	 * 设置：交接时间
	 */
	public void setHandoverDate(String handoverDate) {
		this.handoverDate = handoverDate;
	}
	/**
	 * 获取：交接时间
	 */
	public String getHandoverDate() {
		return handoverDate;
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
}
