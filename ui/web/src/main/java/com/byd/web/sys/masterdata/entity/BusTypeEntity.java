package com.byd.web.sys.masterdata.entity;

import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;

import java.io.Serializable;
import java.util.Date;

/**
 * 车型表-基础数据
 * 
 * @author cscc
 * @email 
 * @date 2018-06-05 15:56:12
 */
@TableName("BUS_TYPE")
public class BusTypeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 车型代码 车型代码(项目车型)
	 */
	private String busTypeCode;
	/**
	 * 代号/内部名称 代号/内部名称(车系)
	 */
	private String internalName;
	private String busSeries;//平台
	/**
	 * 品牌 固定值：比亚迪牌
	 */
	private String brand;
	/**
	 * 制造商 固定值：比亚迪汽车工业有限公司
	 */
	private String manufacturer;
	/**
	 * 车辆类型(乘用车、客车、货车、专用车)
	 */
	private String vehicleType;
	/**
	 * 动力类型-纯电动、插电混合
	 */
	private String powerType;
	/**
	 * 备注
	 */
	private String memo;
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
	 * 设置：车型代码 车型代码(项目车型)
	 */
	public void setBusTypeCode(String busTypeCode) {
		this.busTypeCode = busTypeCode;
	}
	/**
	 * 获取：车型代码 车型代码(项目车型)
	 */
	public String getBusTypeCode() {
		return busTypeCode;
	}
	/**
	 * 设置：代号/内部名称 代号/内部名称(车系)
	 */
	public void setInternalName(String internalName) {
		this.internalName = internalName;
	}
	/**
	 * 获取：代号/内部名称 代号/内部名称(车系)
	 */
	public String getInternalName() {
		return internalName;
	}
	/**
	 * 设置：品牌 固定值：比亚迪牌
	 */
	public void setBrand(String brand) {
		this.brand = brand;
	}
	/**
	 * 获取：品牌 固定值：比亚迪牌
	 */
	public String getBrand() {
		return brand;
	}
	/**
	 * 设置：制造商 固定值：比亚迪汽车工业有限公司
	 */
	public void setManufacturer(String manufacturer) {
		this.manufacturer = manufacturer;
	}
	/**
	 * 获取：制造商 固定值：比亚迪汽车工业有限公司
	 */
	public String getManufacturer() {
		return manufacturer;
	}
	/**
	 * 设置：车辆类型(乘用车、客车、货车、专用车)
	 */
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	/**
	 * 获取：车辆类型(乘用车、客车、货车、专用车)
	 */
	public String getVehicleType() {
		return vehicleType;
	}
	/**
	 * 设置：动力类型-纯电动、插电混合
	 */
	public void setPowerType(String powerType) {
		this.powerType = powerType;
	}
	/**
	 * 获取：动力类型-纯电动、插电混合
	 */
	public String getPowerType() {
		return powerType;
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
	public String getBusSeries() {
		return busSeries;
	}
	public void setBusSeries(String busSeries) {
		this.busSeries = busSeries;
	}
	
}
