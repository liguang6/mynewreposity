package com.byd.wms.business.modules.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
/**
 * 退货原因配置表
 * 
 * @author (changsha) byd_infomation_center
 * @email 
 * @date 2018-08-15 17:10:53
 */
@TableName("WMS_C_QC_RETURN_REASONS")
@KeySequence("SEQ_WMS_C_QC_RETURN_REASONS")
public class WmsCQcReturnReasonsEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	
	/**
	 * 原因分类
	 */
	private String reasonType;
	
	/**
	 * 原因代码
	 */
	private String reasonCode;
	/**
	 * 原因描述
	 */
	private String reasonDesc;
	/**
	 * 删除 默认空 否 X 标识
	 */
	private String del;
	/**
	 * 维护人
	 */
	private String editor;
	/**
	 * 维护时间
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
	 * 设置：原因代码
	 */
	public void setReasonCode(String reasonCode) {
		this.reasonCode = reasonCode;
	}
	/**
	 * 获取：原因代码
	 */
	public String getReasonCode() {
		return reasonCode;
	}
	/**
	 * 设置：原因描述
	 */
	public void setReasonDesc(String reasonDesc) {
		this.reasonDesc = reasonDesc;
	}
	/**
	 * 获取：原因描述
	 */
	public String getReasonDesc() {
		return reasonDesc;
	}
	/**
	 * 设置：删除 默认空 否 X 标识
	 */
	public void setDel(String del) {
		this.del = del;
	}
	/**
	 * 获取：删除 默认空 否 X 标识
	 */
	public String getDel() {
		return del;
	}
	/**
	 * 设置：维护人
	 */
	public void setEditor(String editor) {
		this.editor = editor;
	}
	/**
	 * 获取：维护人
	 */
	public String getEditor() {
		return editor;
	}
	/**
	 * 设置：维护时间
	 */
	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}
	/**
	 * 获取：维护时间
	 */
	public String getEditDate() {
		return editDate;
	}
	public String getReasonType() {
		return reasonType;
	}
	public void setReasonType(String reasonType) {
		this.reasonType = reasonType;
	}
	
	
}
