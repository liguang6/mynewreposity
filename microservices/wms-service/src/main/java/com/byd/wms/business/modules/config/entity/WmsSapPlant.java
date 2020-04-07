package com.byd.wms.business.modules.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

@TableName("WMS_SAP_PLANT")
@KeySequence("SEQ_WMS_SAP_PLANT")//使用oracle 注解自增
public class WmsSapPlant {
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	private String werks;
	private String werksName;
	private String shortName;
	private String bukrs;
	private String bukrsName;
	private String bukrsShortName;
	private String editor;
	private String editDate;
	
	@TableField(value="del")
	@TableLogic
	private String del;
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
	public String getWerksName() {
		return werksName;
	}
	public void setWerksName(String werksName) {
		this.werksName = werksName;
	}
	public String getShortName() {
		return shortName;
	}
	public void setShortName(String shortName) {
		this.shortName = shortName;
	}
	public String getBukrs() {
		return bukrs;
	}
	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}
	public String getBukrsName() {
		return bukrsName;
	}
	public void setBukrsName(String bukrsName) {
		this.bukrsName = bukrsName;
	}

	public String getBukrsShortName() {
		return bukrsShortName;
	}
	public void setBukrsShortName(String bukrsShortName) {
		this.bukrsShortName = bukrsShortName;
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
	public String getDel() {
		return del;
	}
	public void setDel(String del) {
		this.del = del;
	}
	
	
	

}
