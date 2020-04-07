package com.byd.wms.business.modules.config.entity;

import java.io.Serializable;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
/**
 * 仓库存储类型配置 各工厂仓库存储类型设置
 * 
 * @author tangj
 * @email 
 * @date 2018年08月06日 
 */
@TableName("WMS_CORE_WH_AREA")
@KeySequence("SEQ_WMS_CORE_WH_AREA")
public class WmsCoreWhAreaEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@TableId(value = "ID",type=IdType.INPUT)
	private Long id;
	// 仓库代码
	private String whNumber;

	private String storageAreaCode;
	// 存储区名称
	private String areaName;
	private String storageModel;
	private String putRule;
	private String mixFlag;
	// 是否进行库容检查
	private String storageCapacityFlag;
	private String autoPutawayFlag;
	private String autoReplFlag;
	private String checkWeightFlag;
	private String floor;
	private String coordinate;
	private String status;
	// 空储位搜索顺序 00 按储位编码排序 01 固定储位附近（坐标排序）2019-03-20 tangj
	private String binSearchSequence;
	// 软删标示
	private String del;
	// 创建者
	private String editor;
    // 创建日期
	private String editDate;

	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public String getWhNumber() {
		return whNumber;
	}
	public void setWhNumber(String whNumber) {
		this.whNumber = whNumber;
	}
	public String getStorageAreaCode() {
		return storageAreaCode;
	}
	public void setStorageAreaCode(String storageAreaCode) {
		this.storageAreaCode = storageAreaCode;
	}
	public String getAreaName() {
		return areaName;
	}
	public void setAreaName(String areaName) {
		this.areaName = areaName;
	}
	public String getStorageModel() {
		return storageModel;
	}
	public void setStorageModel(String storageModel) {
		this.storageModel = storageModel;
	}
	public String getPutRule() {
		return putRule;
	}
	public void setPutRule(String putRule) {
		this.putRule = putRule;
	}
	public String getMixFlag() {
		return mixFlag;
	}
	public void setMixFlag(String mixFlag) {
		this.mixFlag = mixFlag;
	}
	public String getStorageCapacityFlag() {
		return storageCapacityFlag;
	}
	public void setStorageCapacityFlag(String storageCapacityFlag) {
		this.storageCapacityFlag = storageCapacityFlag;
	}
	public String getAutoPutawayFlag() {
		return autoPutawayFlag;
	}
	public void setAutoPutawayFlag(String autoPutawayFlag) {
		this.autoPutawayFlag = autoPutawayFlag;
	}
	public String getAutoReplFlag() {
		return autoReplFlag;
	}
	public void setAutoReplFlag(String autoReplFlag) {
		this.autoReplFlag = autoReplFlag;
	}
	public String getCheckWeightFlag() {
		return checkWeightFlag;
	}
	public void setCheckWeightFlag(String checkWeightFlag) {
		this.checkWeightFlag = checkWeightFlag;
	}
	public String getFloor() {
		return floor;
	}
	public void setFloor(String floor) {
		this.floor = floor;
	}
	public String getCoordinate() {
		return coordinate;
	}
	public void setCoordinate(String coordinate) {
		this.coordinate = coordinate;
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
	public String getBinSearchSequence() {
		return binSearchSequence;
	}
	public void setBinSearchSequence(String binSearchSequence) {
		this.binSearchSequence = binSearchSequence;
	}


}
