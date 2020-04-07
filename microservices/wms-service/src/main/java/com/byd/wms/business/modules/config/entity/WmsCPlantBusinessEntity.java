package com.byd.wms.business.modules.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 工厂业务类型配置表
 * 
 * @author cscc tangj
 * @email 
 * @date 2018-09-29 14:57:55
 */
@TableName("WMS_C_PLANT_BUSINESS")
@KeySequence("SEQ_WMS_C_PLANT_BUSINESS")
public class WmsCPlantBusinessEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value = "ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 工厂
	 */
	private String werks;
	/**
	 * 业务类型代码
	 */
	private String businessCode;
	/**
	 * SAP同步过账标识 0 同步 X异步 默认0
	 */
	private String synFlag;
	/**
	 * 排序号
	 */
	private Long sortNo;
	/**
	 * 创建人
	 */
	private String creator;
	/**
	 * 创建时间
	 */
	private String createDate;
	/**
	 * 更新人
	 */
	private String updater;
	/**
	 * 更新时间
	 */
	private String updatDate;
	/**
	 * 是否需要审批  0 否 X需要审批 默认0 需求是否需要审批
	 */
	private String approvalFlag;
	/**
	 * 是否可超需求  0 否 X可超需求 默认0 主要用户线边、工厂间物料调拨业务
	 */
	private String overstepReqFlag;
	/**
	 * 是否可超虚拟库存 0 否 X可超 默认0 主要用于核销虚发业务
	 */
	private String overstepHxFlag;
	/**
	 * 寄售中转库
	 */
	private String lgort;
	/**
	 * EWM条码共享
	 */
	private String publicwerks;
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
	 * 设置：工厂
	 */
	public void setWerks(String werks) {
		this.werks = werks;
	}
	/**
	 * 获取：工厂
	 */
	public String getWerks() {
		return werks;
	}
	/**
	 * 设置：业务类型代码
	 */
	public void setBusinessCode(String businessCode) {
		this.businessCode = businessCode;
	}
	/**
	 * 获取：业务类型代码
	 */
	public String getBusinessCode() {
		return businessCode;
	}
	/**
	 * 设置：SAP同步过账标识 0 同步 X异步 默认0
	 */
	public void setSynFlag(String synFlag) {
		this.synFlag = synFlag;
	}
	/**
	 * 获取：SAP同步过账标识 0 同步 X异步 默认0
	 */
	public String getSynFlag() {
		return synFlag;
	}
	/**
	 * 设置：排序号
	 */
	public void setSortNo(Long sortNo) {
		this.sortNo = sortNo;
	}
	/**
	 * 获取：排序号
	 */
	public Long getSortNo() {
		return sortNo;
	}
	/**
	 * 设置：更新人
	 */
	public void setUpdater(String updater) {
		this.updater = updater;
	}
	/**
	 * 获取：更新人
	 */
	public String getUpdater() {
		return updater;
	}
	/**
	 * 设置：更新时间
	 */
	public void setUpdatDate(String updatDate) {
		this.updatDate = updatDate;
	}
	/**
	 * 获取：更新时间
	 */
	public String getUpdatDate() {
		return updatDate;
	}
	public String getCreator() {
		return creator;
	}
	public void setCreator(String creator) {
		this.creator = creator;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getApprovalFlag() {
		return approvalFlag;
	}
	public void setApprovalFlag(String approvalFlag) {
		this.approvalFlag = approvalFlag;
	}
	public String getOverstepReqFlag() {
		return overstepReqFlag;
	}
	public void setOverstepReqFlag(String overstepReqFlag) {
		this.overstepReqFlag = overstepReqFlag;
	}
	public String getOverstepHxFlag() {
		return overstepHxFlag;
	}
	public void setOverstepHxFlag(String overstepHxFlag) {
		this.overstepHxFlag = overstepHxFlag;
	}
	public String getLgort() {
		return lgort;
	}
	public void setLgort(String lgort) {
		this.lgort = lgort;
	}

	public String getPublicwerks() {
		return publicwerks;
	}

	public void setPublicwerks(String publicwerks) {
		this.publicwerks = publicwerks;
	}
}
