package com.byd.wms.business.modules.config.entity;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/**
 * 工厂质检配置表
 * 
 * @author cscc
 * @email 
 * @date 2018-08-07 11:54:43
 */
@TableName("WMS_C_QC_PLANT")
@KeySequence("SEQ_WMS_C_QC_PLANT")
public class WmsCQcPlantEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 工厂代码
	 */
	@NotNull
	private String werks;
	/**
	 * WMS业务类型代码
	 */
	@NotNull
	private String businessName;
	/**
	 * 质检标识 00 质检 01 免检 02 无需质检
	 */
	@NotNull
	private String testFlag;
	/**
	 * 删除标示 空 否 X是 默认空
	 */
	@TableField(value = "del")
    @TableLogic
	private String del;
	/**
	 * 有效开始日期
	 */
	@NotNull
	private String startDate;
	/**
	 * 有效截止日期
	 */
	@NotNull
	private String endDate;
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
	 * 备注
	 */
	private String memo;
	
	/**
	 * 导入预览的提示消息
	 */
	@TableField(exist=false)
	private List<String> msgs;
	
	

	public List<String> getMsgs() {
		return msgs;
	}
	public void setMsgs(List<String> msgs) {
		this.msgs = msgs;
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
	 * 设置：WMS业务类型代码
	 */
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	/**
	 * 获取：WMS业务类型代码
	 */
	public String getBusinessName() {
		return businessName;
	}
	/**
	 * 设置：质检标识 00 质检 01 免检 02 无需质检
	 */
	public void setTestFlag(String testFlag) {
		this.testFlag = testFlag;
	}
	/**
	 * 获取：质检标识 00 质检 01 免检 02 无需质检
	 */
	public String getTestFlag() {
		return testFlag;
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
	 * 设置：有效开始日期
	 */
	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
	/**
	 * 获取：有效开始日期
	 */
	public String getStartDate() {
		return startDate;
	}
	/**
	 * 设置：有效截止日期
	 */
	public void setEndDate(String endDate) {
		this.endDate = endDate;
	}
	/**
	 * 获取：有效截止日期
	 */
	public String getEndDate() {
		return endDate;
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
}
