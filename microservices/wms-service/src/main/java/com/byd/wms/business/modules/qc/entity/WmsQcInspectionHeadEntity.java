package com.byd.wms.business.modules.qc.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * 送检单抬头
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-13 15:12:12
 */
@TableName("WMS_QC_INSPECTION_HEAD")
@KeySequence("SEQ_WMS_QC_INSPECTION_HEAD")
public class WmsQcInspectionHeadEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 送检单号
	 */
	private String inspectionNo;
	/**
	 * 送检单类型 字典定义：（01 来料质检 02 库存复检）
	 */
	private String inspectionType;
	/**
	 * 送检单状态 字典定义：（00创建，01部分完成，02全部完成，04关闭）
	 */
	private String inspectionStatus;

	/**
	 * 工厂代码
	 */
	private String werks;
	/**
	 * 仓库号
	 */
	private String whNumber;
	/**
	 * 是否后台创建 空 否 X是 默认空
	 */
	private String isAuto;
	/**
	 * 删除标示 空 否 X是 默认空
	 */
	@TableLogic
	@TableField("del")
	private String del;
	/**
	 * 备注
	 */
	private String memo;
	/**
	 * 创建人
	 */
	private String creator;
	/**
	 * 创建时间
	 */
	private String createDate;
	/**
	 * 编辑人
	 */
	private String editor;
	/**
	 * 编辑时间
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
	 * 设置：送检单号
	 */
	public void setInspectionNo(String inspectionNo) {
		this.inspectionNo = inspectionNo;
	}
	/**
	 * 获取：送检单号
	 */
	public String getInspectionNo() {
		return inspectionNo;
	}
	/**
	 * 设置：送检单类型 字典定义：（01 来料质检 02 库存复检）
	 */
	public void setInspectionType(String inspectionType) {
		this.inspectionType = inspectionType;
	}
	/**
	 * 获取：送检单类型 字典定义：（01 来料质检 02 库存复检）
	 */
	public String getInspectionType() {
		return inspectionType;
	}
	/**
	 * 设置：送检单状态 字典定义：（00创建，01部分完成，02全部完成，04关闭）
	 */
	public void setInspectionStatus(String inspectionStatus) {
		this.inspectionStatus = inspectionStatus;
	}
	/**
	 * 获取：送检单状态 字典定义：（00创建，01部分完成，02全部完成，04关闭）
	 */
	public String getInspectionStatus() {
		return inspectionStatus;
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
	 * 设置：是否后台创建 空 否 X是 默认空
	 */
	public void setIsAuto(String isAuto) {
		this.isAuto = isAuto;
	}
	/**
	 * 获取：是否后台创建 空 否 X是 默认空
	 */
	public String getIsAuto() {
		return isAuto;
	}
	/**
	 * 设置：删除标示 空 否 X是 默认空
	 */
	public void setDel(String del) {
		this.del = del;
	}
	/**
	 * 获取：删除标示 空 否 X是 默认空
	 */
	public String getDel() {
		return del;
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
	 * 设置：创建人
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	/**
	 * 获取：创建人
	 */
	public String getCreator() {
		return creator;
	}
	/**
	 * 设置：创建时间
	 */
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	/**
	 * 获取：创建时间
	 */
	public String getCreateDate() {
		return createDate;
	}
	/**
	 * 设置：编辑人
	 */
	public void setEditor(String editor) {
		this.editor = editor;
	}
	/**
	 * 获取：编辑人
	 */
	public String getEditor() {
		return editor;
	}
	/**
	 * 设置：编辑时间
	 */
	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}
	/**
	 * 获取：编辑时间
	 */
	public String getEditDate() {
		return editDate;
	}
}
