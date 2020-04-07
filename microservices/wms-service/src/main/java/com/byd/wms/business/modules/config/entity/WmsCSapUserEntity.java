package com.byd.wms.business.modules.config.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2018年8月29日 上午11:04:21 
 * 类说明 
 */
@TableName("WMS_C_SAP_USER")
@KeySequence("SEQ_WMS_C_SAP_USER")//使用oracle 注解自增
public class WmsCSapUserEntity implements Serializable {
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 工厂代码
	 */
	private String werks;
	/**
	 * SAP账号
	 */
	private String sapUser;
	/**
	 * SAP密码
	 */
	private String sapPassword;
	
	private String del;
	
	/*
	 * 修改人
	 */
	private String editor;
	/*
	 * 修改时间
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
	public String getSapUser() {
		return sapUser;
	}
	public void setSapUser(String sapUser) {
		this.sapUser = sapUser;
	}
	public String getSapPassword() {
		return sapPassword;
	}
	public void setSapPassword(String sapPassword) {
		this.sapPassword = sapPassword;
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
