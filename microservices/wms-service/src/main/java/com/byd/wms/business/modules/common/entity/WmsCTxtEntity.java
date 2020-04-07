package com.byd.wms.business.modules.common.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2018年8月23日 上午9:11:06 
 * 类说明 
 */
@TableName("WMS_C_TXT")
@KeySequence("SEQ_WMS_C_TXT")//使用oracle 注解自增
public class WmsCTxtEntity implements Serializable {
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 工厂代码
	 */
	private String werks;
	/**
	 * 业务类型名称
	 */
	private String businessName;
	/**
	 * 头文本生成规则
	 */
	private String txtRule;
	/**
	 * 行文本生成规则
	 */
	private String txtRuleItem;
	/**
	 * 删除
	 */
	private String del;
	/**
	 * 
	 */
	private String creator;
	
	private String createDate;
	
	private String editor;
	
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

	public String getBusinessName() {
		return businessName;
	}

	public void setBusinessName(String businessName) {
		this.businessName = businessName;
	}

	public String getTxtRule() {
		return txtRule;
	}

	public void setTxtRule(String txtRule) {
		this.txtRule = txtRule;
	}

	public String getTxtRuleItem() {
		return txtRuleItem;
	}

	public void setTxtRuleItem(String txtRuleItem) {
		this.txtRuleItem = txtRuleItem;
	}

	public String getDel() {
		return del;
	}

	public void setDel(String del) {
		this.del = del;
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
