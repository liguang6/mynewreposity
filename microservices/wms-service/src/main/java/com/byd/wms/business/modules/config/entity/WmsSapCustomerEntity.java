package com.byd.wms.business.modules.config.entity;

import java.util.List;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

@TableName("WMS_SAP_CUSTOMER")
@KeySequence("SEQ_WMS_SAP_CUSTOMER")
public class WmsSapCustomerEntity {
	
	@TableId(value="ID",type=IdType.INPUT)
	private Long id;
	
	private String bukrs;
	
	private String kunnr;
	
	private String name1;
	
	private String sortl;
	
	private String vkorg;
	
	private String vtweg;
	
	private String spart;
	
	private String address;
	
	private String manager;
	
	private String editor;
	
	private String editDate;
	
	//导入出错的提示消息
	@TableField(exist=false)
	private List<String> msgList;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getBukrs() {
		return bukrs;
	}

	public void setBukrs(String bukrs) {
		this.bukrs = bukrs;
	}

	public String getKunnr() {
		return kunnr;
	}

	public void setKunnr(String kunnr) {
		this.kunnr = kunnr;
	}

	public String getName1() {
		return name1;
	}

	public void setName1(String name1) {
		this.name1 = name1;
	}

	public String getSortl() {
		return sortl;
	}

	public void setSortl(String sortl) {
		this.sortl = sortl;
	}

	public String getVkorg() {
		return vkorg;
	}

	public void setVkorg(String vkorg) {
		this.vkorg = vkorg;
	}

	public String getVtweg() {
		return vtweg;
	}

	public void setVtweg(String vtweg) {
		this.vtweg = vtweg;
	}

	public String getSpart() {
		return spart;
	}

	public void setSpart(String spart) {
		this.spart = spart;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String address) {
		this.address = address;
	}

	public String getManager() {
		return manager;
	}

	public void setManager(String manager) {
		this.manager = manager;
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

	public List<String> getMsgList() {
		return msgList;
	}

	public void setMsgList(List<String> msgList) {
		this.msgList = msgList;
	}
	
	
	

}
