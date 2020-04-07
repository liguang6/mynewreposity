package com.byd.wms.business.modules.common.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月21日 上午11:32:43 
 * 类说明 
 */
@TableName("WMS_C_BATCH_PLAN_RULES")
@KeySequence("SEQ_WMS_C_BATCH_PLAN_RULES")//使用oracle 注解自增
public class WmsCBatchPlanRulesEntity implements Serializable{
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	private String businessName;
	private String businessNameText;//BUSINESS_NAME_TEXT
	/**
	 * 工厂代码
	 */
	private String werks;
	/**
	 * 批次规则代码
	 */
	private String batchRuleCode;
	private String batchRuleText;//BATCH_RULE_TEXT
	/**
	 * 库存地点
	 */
	private String lgort;
	/**
	 * 是否危化品
	 */
	private String dangerFlag;
	private String fBatchFlag;//F_BATCH_FLAG是否沿用源批次 0 否 X 是
	
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
	public String getWerks() {
		return werks;
	}
	public void setWerks(String werks) {
		this.werks = werks;
	}
	public String getBatchRuleCode() {
		return batchRuleCode;
	}
	public void setBatchRuleCode(String batchRuleCode) {
		this.batchRuleCode = batchRuleCode;
	}
	public String getLgort() {
		return lgort;
	}
	public void setLgort(String lgort) {
		this.lgort = lgort;
	}
	public String getDangerFlag() {
		return dangerFlag;
	}
	public void setDangerFlag(String dangerFlag) {
		this.dangerFlag = dangerFlag;
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
	public String getBatchRuleText() {
		return batchRuleText;
	}
	public void setBatchRuleText(String batchRuleText) {
		this.batchRuleText = batchRuleText;
	}
	public String getBusinessName() {
		return businessName;
	}
	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}
	public String getfBatchFlag() {
		return fBatchFlag;
	}
	public void setfBatchFlag(String fBatchFlag) {
		this.fBatchFlag = fBatchFlag;
	}
	public String getBusinessNameText() {
		return businessNameText;
	}
	public void setBusinessNameText(String businessNameText) {
		this.businessNameText = businessNameText;
	}
	
}
