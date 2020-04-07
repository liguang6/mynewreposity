package com.byd.wms.business.modules.common.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月21日 上午11:41:48 
 * 类说明 
 */
@TableName("WMS_C_BATCH_RULES")
@KeySequence("SEQ_WMS_C_BATCH_RULES")//使用oracle 注解自增
public class WmsCBatchRulesEntity implements Serializable{
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 批次规则代码
	 */
	private String batchRuleCode;
	/**
	 * 批次规则描述
	 */
	private String batchRuleText;
	/**
	 * 系统代号 字典定义：长沙A，深圳B
	 */
	private String sys;
	/**
	 * 批次规则
	 */
	private String batchRule;
	/**
	 * 流水码长度
	 */
	private int flowCodeLength;
	/**
	 * 合并规则描述
	 */
	private String mergeRuleText;
	/**
	 * 合并规则
	 */
	private String mergeRule;
	/**
	 * 删除标识 默认 0 否 X 是
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
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getBatchRuleCode() {
		return batchRuleCode;
	}
	public void setBatchRuleCode(String batchRuleCode) {
		this.batchRuleCode = batchRuleCode;
	}
	public String getBatchRuleText() {
		return batchRuleText;
	}
	public void setBatchRuleText(String batchRuleText) {
		this.batchRuleText = batchRuleText;
	}
	public String getSys() {
		return sys;
	}
	public void setSys(String sys) {
		this.sys = sys;
	}
	public String getBatchRule() {
		return batchRule;
	}
	public void setBatchRule(String batchRule) {
		this.batchRule = batchRule;
	}
	
	public int getFlowCodeLength() {
		return flowCodeLength;
	}
	public void setFlowCodeLength(int flowCodeLength) {
		this.flowCodeLength = flowCodeLength;
	}
	public String getMergeRuleText() {
		return mergeRuleText;
	}
	public void setMergeRuleText(String mergeRuleText) {
		this.mergeRuleText = mergeRuleText;
	}
	public String getMergeRule() {
		return mergeRule;
	}
	public void setMergeRule(String mergeRule) {
		this.mergeRule = mergeRule;
	}
	public String getDel() {
		return del;
	}
	public void setDel(String del) {
		this.del = del;
	}
	public String getEditor() {
		return editor;
	}
	public void setEditor(String editor) {
		this.editor = editor;
	}
	public String getEditDate() {
		return editDate;
	}
	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}

}
