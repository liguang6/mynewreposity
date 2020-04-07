package com.byd.admin.modules.masterdata.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月9日 上午11:14:00 
 * 类说明 
 */
@TableName("MASTERDATA_DEVICE_TYPE")
@KeySequence("SEQ_MASTERDATA_DEVICE_TYPE")
public class DeviceTypeEntity implements Serializable {
private static final long serialVersionUID = 1L;
	
	@TableId(value = "ID", type = IdType.INPUT)
	private Long id;
	
	/**
	 * 设备类型编码
	 */
	private String deviceTypeCode;
	/**
	 * 设备类型名称
	 */
	private String deviceTypeName;
	/**
	 * 删除标识 0 否 X 删除
	 */
	private String del;
	/**
	 * 
	 */
	private String creator;
	/**
	 * 
	 */
	private String createDate;
	/**
	 * 
	 */
	private String editor;
	/**
	 * 
	 */
	private String editDate;
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getDeviceTypeCode() {
		return deviceTypeCode;
	}
	public void setDeviceTypeCode(String deviceTypeCode) {
		this.deviceTypeCode = deviceTypeCode;
	}
	public String getDeviceTypeName() {
		return deviceTypeName;
	}
	public void setDeviceTypeName(String deviceTypeName) {
		this.deviceTypeName = deviceTypeName;
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
