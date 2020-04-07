package com.byd.wms.business.modules.in.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 进仓单抬头 入库交接单
 * 
 * @author TANGJIN
 * @email 
 * @date 2018-08-27 10:54:57
 */
@TableName("WMS_IN_INBOUND_HEAD")
@KeySequence("SEQ_WMS_IN_INBOUND_HEAD")
public class WmsInInboundHeadEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 进仓单号
	 */
	private String inboundNo;
	/**
	 * 进仓单类型 BUSINESS_NAME-WMS业务类型名称
	 */
	private String inboundType;
	/**
	 * 进仓单状态 字典定义：（00创建，01部分进仓，02已完成，04关闭）
	 */
	private String inbounStatus;
	/**
	 * 工厂代码
	 */
	private String werks;
	/**
	 * 仓库号
	 */
	private String whNumber;
	/**
	 * 抬头文本
	 */
	private String headText;
	/**
	 * 是否后台创建 0 否 X是 默认0
	 */
	private String isAuto;
	/**
	 * 删除标识 0 否 X是 默认0
	 */
	private String del;
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
	 * 设置：进仓单号
	 */
	public void setInboundNo(String inboundNo) {
		this.inboundNo = inboundNo;
	}
	/**
	 * 获取：进仓单号
	 */
	public String getInboundNo() {
		return inboundNo;
	}
	/**
	 * 设置：进仓单类型 BUSINESS_NAME-WMS业务类型名称
	 */
	public void setInboundType(String inboundType) {
		this.inboundType = inboundType;
	}
	/**
	 * 获取：进仓单类型 BUSINESS_NAME-WMS业务类型名称
	 */
	public String getInboundType() {
		return inboundType;
	}
	/**
	 * 设置：进仓单状态 字典定义：（00创建，01部分进仓，02已完成，04关闭）
	 */
	public void setInbounStatus(String inbounStatus) {
		this.inbounStatus = inbounStatus;
	}
	/**
	 * 获取：进仓单状态 字典定义：（00创建，01部分进仓，02已完成，04关闭）
	 */
	public String getInbounStatus() {
		return inbounStatus;
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
	 * 设置：抬头文本
	 */
	public void setHeadText(String headText) {
		this.headText = headText;
	}
	/**
	 * 获取：抬头文本
	 */
	public String getHeadText() {
		return headText;
	}
	/**
	 * 设置：是否后台创建 0 否 X是 默认0
	 */
	public void setIsAuto(String isAuto) {
		this.isAuto = isAuto;
	}
	/**
	 * 获取：是否后台创建 0 否 X是 默认0
	 */
	public String getIsAuto() {
		return isAuto;
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
