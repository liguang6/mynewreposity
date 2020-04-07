package com.byd.admin.modules.masterdata.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年9月7日 上午9:58:07 
 * 类说明 
 */
@TableName("MASTERDATA_DEVICE")
@KeySequence("SEQ_MASTERDATA_DEVICE")
public class DeviceEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@TableId(value = "ID", type = IdType.INPUT)
	private Long id;
	/**
	 * 工厂
	 */
	private String werks;
	/**
	 * 工厂名称
	 */
	private String werksName;
	
	/**
	 * 设备编码
	 */
	private String deviceCode;
	/**
	 * 设备名称
	 */
	private String deviceName;
	/**
	 * 机台编码
	 */
	private String machineCode;
	/**
	 * 机台名称
	 */
	private String machineName;
	/**
	 * 规格型号
	 */
	private String specificationModel;
	/**
	 * memo
	 */
	private String memo;
	/**
	 * status
	 */
	private String status;
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
	
	public String getDeviceCode() {
		return deviceCode;
	}
	public void setDeviceCode(String deviceCode) {
		this.deviceCode = deviceCode;
	}
	public String getDeviceName() {
		return deviceName;
	}
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	public String getMachineCode() {
		return machineCode;
	}
	public void setMachineCode(String machineCode) {
		this.machineCode = machineCode;
	}
	public String getMachineName() {
		return machineName;
	}
	public void setMachineName(String machineName) {
		this.machineName = machineName;
	}
	public String getSpecificationModel() {
		return specificationModel;
	}
	public void setSpecificationModel(String specificationModel) {
		this.specificationModel = specificationModel;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
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
