package com.byd.web.wms.out.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * WMS出库需求抬头
 * 
 * @author (changsha) byd_infomation_center
 * @email
 * @date 2018-10-08 15:12:35
 */
@TableName("WMS_OUT_REQUIREMENT_HEAD")
@KeySequence("SEQ_WMS_OUT_REQUIREMENT_HEAD")
public class WmsOutRequirementHeadEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value = "ID", type = IdType.INPUT)
	private Long id;
	/**
	 * 需求号
	 */
	private String requirementNo;
	/**
	 * 需求类别 需求类别 00 领料需求 01 出仓单 02 配送单
	 */
	private String requirementType;
	/**
	 * 状态 状态00已创建 01 已审批 02 备料中 03 已交接 04 关闭
	 */
	private String requirementStatus;
	/**
	 * 删除标识 空 否 X是 默认空
	 */
	@TableLogic
	private String del;
	/**
	 * 发货工厂
	 */
	private String werks;
	/**
	 * 发货仓库号
	 */
	private String whNumber;
	/**
	 * 需求日期
	 */
	private String requiredDate;
	/**
	 * 需求时段 字典定义：包含 00上午8:00-12:00 01 下午13:00-17:30 02 加班18:00-20:00 03夜班20:00-8:00
	 */
	private String requiredTime;
	/**
	 * 领料模式 字典定义：领料模式（01 按订单备料 02 按产线备料 05 按车间备料）
	 */
	private String requiredModel;
	/**
	 * 领料用途
	 */
	private String purpose;
	/**
	 * 接收方 填写接收车间
	 */
	private String receiver;
	/**
	 * 接收工厂 工厂调拨时必填
	 */
	private String receiveWerks;
	/**
	 * 发货地区
	 */
	private String deliveryArea;
	/**
	 * 收货地区
	 */
	private String receiptArea;
	/**
	 * 运输方式 运输方式（销售发货时必填）
	 */
	private String shipmentModel;
	/**
	 * 预计运输天数
	 */
	private Long transportDays;
	/**
	 * 审批标识 0 无需审批 X 审批 默认0
	 */
	private String checkFlag;
	/**
	 * 备注
	 */
	private String memo;
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

	private String supermarket;

	public String getSupermarket() {
		return supermarket;
	}

	public void setSupermarket(String supermarket) {
		this.supermarket = supermarket;
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
	 * 设置：需求类别 需求类别 00 领料需求 01 出仓单 02 配送单
	 */
	public void setRequirementType(String requirementType) {
		this.requirementType = requirementType;
	}

	/**
	 * 获取：需求类别 需求类别 00 领料需求 01 出仓单 02 配送单
	 */
	public String getRequirementType() {
		return requirementType;
	}

	/**
	 * 设置：状态 状态00已创建 01 已审批 02 备料中 03 已交接 04 关闭
	 */
	public void setRequirementStatus(String requirementStatus) {
		this.requirementStatus = requirementStatus;
	}

	/**
	 * 获取：状态 状态00已创建 01 已审批 02 备料中 03 已交接 04 关闭
	 */
	public String getRequirementStatus() {
		return requirementStatus;
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
	 * 设置：发货工厂
	 */
	public void setWerks(String werks) {
		this.werks = werks;
	}

	/**
	 * 获取：发货工厂
	 */
	public String getWerks() {
		return werks;
	}

	/**
	 * 设置：发货仓库号
	 */
	public void setWhNumber(String whNumber) {
		this.whNumber = whNumber;
	}

	/**
	 * 获取：发货仓库号
	 */
	public String getWhNumber() {
		return whNumber;
	}

	/**
	 * 设置：需求日期
	 */
	public void setRequiredDate(String requiredDate) {
		this.requiredDate = requiredDate;
	}

	/**
	 * 获取：需求日期
	 */
	public String getRequiredDate() {
		return requiredDate;
	}

	/**
	 * 设置：需求时段 字典定义：包含 00上午8:00-12:00 01 下午13:00-17:30 02 加班18:00-20:00
	 * 03夜班20:00-8:00
	 */
	public void setRequiredTime(String requiredTime) {
		this.requiredTime = requiredTime;
	}

	/**
	 * 获取：需求时段 字典定义：包含 00上午8:00-12:00 01 下午13:00-17:30 02 加班18:00-20:00
	 * 03夜班20:00-8:00
	 */
	public String getRequiredTime() {
		return requiredTime;
	}

	/**
	 * 设置：领料模式 字典定义：领料模式（01 按订单备料 02 按产线备料 05 按车间备料）
	 */
	public void setRequiredModel(String requiredModel) {
		this.requiredModel = requiredModel;
	}

	/**
	 * 获取：领料模式 字典定义：领料模式（01 按订单备料 02 按产线备料 05 按车间备料）
	 */
	public String getRequiredModel() {
		return requiredModel;
	}

	/**
	 * 设置：领料用途
	 */
	public void setPurpose(String purpose) {
		this.purpose = purpose;
	}

	/**
	 * 获取：领料用途
	 */
	public String getPurpose() {
		return purpose;
	}

	/**
	 * 设置：接收方 填写接收车间
	 */
	public void setReceiver(String receiver) {
		this.receiver = receiver;
	}

	/**
	 * 获取：接收方 填写接收车间
	 */
	public String getReceiver() {
		return receiver;
	}

	/**
	 * 设置：接收工厂 工厂调拨时必填
	 */
	public void setReceiveWerks(String receiveWerks) {
		this.receiveWerks = receiveWerks;
	}

	/**
	 * 获取：接收工厂 工厂调拨时必填
	 */
	public String getReceiveWerks() {
		return receiveWerks;
	}

	/**
	 * 设置：发货地区
	 */
	public void setDeliveryArea(String deliveryArea) {
		this.deliveryArea = deliveryArea;
	}

	/**
	 * 获取：发货地区
	 */
	public String getDeliveryArea() {
		return deliveryArea;
	}

	/**
	 * 设置：收货地区
	 */
	public void setReceiptArea(String receiptArea) {
		this.receiptArea = receiptArea;
	}

	/**
	 * 获取：收货地区
	 */
	public String getReceiptArea() {
		return receiptArea;
	}

	/**
	 * 设置：运输方式 运输方式（销售发货时必填）
	 */
	public void setShipmentModel(String shipmentModel) {
		this.shipmentModel = shipmentModel;
	}

	/**
	 * 获取：运输方式 运输方式（销售发货时必填）
	 */
	public String getShipmentModel() {
		return shipmentModel;
	}

	/**
	 * 设置：预计运输天数
	 */
	public void setTransportDays(Long transportDays) {
		this.transportDays = transportDays;
	}

	/**
	 * 获取：预计运输天数
	 */
	public Long getTransportDays() {
		return transportDays;
	}

	/**
	 * 设置：审批标识 0 无需审批 X 审批 默认0
	 */
	public void setCheckFlag(String checkFlag) {
		this.checkFlag = checkFlag;
	}

	/**
	 * 获取：审批标识 0 无需审批 X 审批 默认0
	 */
	public String getCheckFlag() {
		return checkFlag;
	}

	/**
	 * 设置：备注
	 */
	public void setMemo(String memo) {
		this.memo = memo;
	}

	/**
	 * 获取：备注
	 */
	public String getMemo() {
		return memo;
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
