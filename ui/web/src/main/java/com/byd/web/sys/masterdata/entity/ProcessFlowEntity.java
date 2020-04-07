package com.byd.web.sys.masterdata.entity;

import java.io.Serializable;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;

/**
 * 工艺流程表-基础数据
 * 
 * @author cscc
 * @email 
 * @date 2018-06-04 11:07:19
 */
@TableName("PROCESS_FLOW")
public class ProcessFlowEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId
	private Long id;
	/**
	 * 部门ID
	 */
	private Long deptId;
	/**
	 * 工厂
	 */
	private String factoryName;
	/**
	 * 车间
	 */
	private String workshopName;
	/**
	 * 线别
	 */
	private String lineName;
	/**
	 * 车型ID
	 */
	private Long busTypeId;
	/**
	 * 车型代码
	 */
	private String busTypeCode;
	/**
	 * 工艺流程类别ID
	 */
	private Long vehicleTypeId;
	/**
	 * 车辆类型
	 */
	private String vehicleType;
	/**
	 * 扫描顺序
	 */
	private Integer sortNo;
	/**
	 * 工序id
	 */
	private Long processId;
	/**
	 * 工序code
	 */
	private String processCode;
	/**
	 * 工序名称
	 */
	private String processName;
	/**
	 * 生产监控点 生产监控点 生产监控点 默认为1
	 */
	private String monitoryPointFlag;
	/**
	 * 工段
	 */
	private String sectionName;
	/**
	 * 计划节点code
	 */
	private String planNodeCode;
	/**
	 * 计划节点名称
	 */
	private String planNodeName;
	/**
	 * 维护人
	 */
	private String editor;
	
	/**
	 * 维护时间
	 */
	
	private String editDate;
	
	/**
	 * 车型名字
	 */
	@TableField(exist = false)
	private String busTypeName;
	
	/**
	 * 车辆类型名字
	 */
	@TableField(exist = false)
	private String vehicleTypeName;

	
	
	public String getBusTypeName() {
		return busTypeName;
	}
	public void setBusTypeName(String busTypeName) {
		this.busTypeName = busTypeName;
	}
	public String getVehicleTypeName() {
		return vehicleTypeName;
	}
	public void setVehicleTypeName(String vehicleTypeName) {
		this.vehicleTypeName = vehicleTypeName;
	}
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
	 * 设置：部门ID
	 */
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	/**
	 * 获取：部门ID
	 */
	public Long getDeptId() {
		return deptId;
	}
	/**
	 * 设置：工厂
	 */
	public void setFactoryName(String factoryName) {
		this.factoryName = factoryName;
	}
	/**
	 * 获取：工厂
	 */
	public String getFactoryName() {
		return factoryName;
	}
	/**
	 * 设置：车间
	 */
	public void setWorkshopName(String workshopName) {
		this.workshopName = workshopName;
	}
	/**
	 * 获取：车间
	 */
	public String getWorkshopName() {
		return workshopName;
	}
	/**
	 * 设置：线别
	 */
	public void setLineName(String lineName) {
		this.lineName = lineName;
	}
	/**
	 * 获取：线别
	 */
	public String getLineName() {
		return lineName;
	}
	/**
	 * 设置：车型ID
	 */
	public void setBusTypeId(Long busTypeId) {
		this.busTypeId = busTypeId;
	}
	/**
	 * 获取：车型ID
	 */
	public Long getBusTypeId() {
		return busTypeId;
	}
	/**
	 * 设置：车型代码
	 */
	public void setBusTypeCode(String busTypeCode) {
		this.busTypeCode = busTypeCode;
	}
	/**
	 * 获取：车型代码
	 */
	public String getBusTypeCode() {
		return busTypeCode;
	}
	/**
	 * 设置：工艺流程类别ID
	 */
	public void setVehicleTypeId(Long vehicleTypeId) {
		this.vehicleTypeId = vehicleTypeId;
	}
	/**
	 * 获取：工艺流程类别ID
	 */
	public Long getVehicleTypeId() {
		return vehicleTypeId;
	}
	/**
	 * 设置：工艺流程类别
	 */
	public void setVehicleType(String vehicleType) {
		this.vehicleType = vehicleType;
	}
	/**
	 * 获取：工艺流程类别
	 */
	public String getVehicleType() {
		return vehicleType;
	}
	/**
	 * 设置：扫描顺序
	 */
	public void setSortNo(Integer sortNo) {
		this.sortNo = sortNo;
	}
	/**
	 * 获取：扫描顺序
	 */
	public Integer getSortNo() {
		return sortNo;
	}
	/**
	 * 设置：工序名称
	 */
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	/**
	 * 获取：工序名称
	 */
	public String getProcessName() {
		return processName;
	}
	/**
	 * 设置：生产监控点 生产监控点 生产监控点 默认为1
	 */
	public void setMonitoryPointFlag(String monitoryPointFlag) {
		this.monitoryPointFlag = monitoryPointFlag;
	}
	/**
	 * 获取：生产监控点 生产监控点 生产监控点 默认为1
	 */
	public String getMonitoryPointFlag() {
		return monitoryPointFlag;
	}
	/**
	 * 设置：工段
	 */
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	/**
	 * 获取：工段
	 */
	public String getSectionName() {
		return sectionName;
	}
	/**
	 * 设置：计划节点名称
	 */
	public void setPlanNodeName(String planNodeName) {
		this.planNodeName = planNodeName;
	}
	/**
	 * 获取：计划节点名称
	 */
	public String getPlanNodeName() {
		return planNodeName;
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

	public String getProcessCode() {
		return processCode;
	}
	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}
	public Long getProcessId() {
		return processId;
	}
	public void setProcessId(Long processId) {
		this.processId = processId;
	}
	public String getPlanNodeCode() {
		return planNodeCode;
	}
	public void setPlanNodeCode(String planNodeCode) {
		this.planNodeCode = planNodeCode;
	}
	
}
