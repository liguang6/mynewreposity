package com.byd.admin.modules.masterdata.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;

/**
 * 标准工序表-基础数据
 * 
 * @author cscc
 * @email 
 * @date 2018-06-04 11:07:19
 */
@TableName("MASTERDATA_PROCESS")
@KeySequence("SEQ_MASTERDATA_PROCESS")//类注解
public class ProcessEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 工序ID
	 */
	@TableId(value = "ID", type = IdType.INPUT)
	private Long id;
	/**
	 * 部门ID
	 */
	private Long deptId;
	
	private String werks;//工厂代码
	/**
	 * 工厂
	 */
	private String werksName;
	private String workshop;//车间代码
	/**
	 * 车间
	 */
	private String workshopName;
	/**
	 * 工序代码
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
	 * 工段ID 弹性件定义
	 */
	private String sectionCode;
	/**
	 * 工段名称
	 */
	private String sectionName;
	
	/**
	 *计划节点code 
	 */
	private String planNodeCode;
	/**
	 * 计划节点名称
	 */
	private String planNodeName;

	/**
	 * 
	 */
	private String memo;
	/**
	 * 删除标识
	 */
	private String del;
	/**
	 * 工序类别
	 */
	private String processType;
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
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
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
	public String getWorkshop() {
		return workshop;
	}
	public void setWorkshop(String workshop) {
		this.workshop = workshop;
	}
	public String getWorkshopName() {
		return workshopName;
	}
	public void setWorkshopName(String workshopName) {
		this.workshopName = workshopName;
	}
	public String getProcessCode() {
		return processCode;
	}
	public void setProcessCode(String processCode) {
		this.processCode = processCode;
	}
	public String getProcessName() {
		return processName;
	}
	public void setProcessName(String processName) {
		this.processName = processName;
	}
	public String getMonitoryPointFlag() {
		return monitoryPointFlag;
	}
	public void setMonitoryPointFlag(String monitoryPointFlag) {
		this.monitoryPointFlag = monitoryPointFlag;
	}
	public String getSectionCode() {
		return sectionCode;
	}
	public void setSectionCode(String sectionCode) {
		this.sectionCode = sectionCode;
	}
	public String getSectionName() {
		return sectionName;
	}
	public void setSectionName(String sectionName) {
		this.sectionName = sectionName;
	}
	public String getPlanNodeCode() {
		return planNodeCode;
	}
	public void setPlanNodeCode(String planNodeCode) {
		this.planNodeCode = planNodeCode;
	}
	public String getPlanNodeName() {
		return planNodeName;
	}
	public void setPlanNodeName(String planNodeName) {
		this.planNodeName = planNodeName;
	}
	public String getMemo() {
		return memo;
	}
	public void setMemo(String memo) {
		this.memo = memo;
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
	public String getProcessType() {
		return processType;
	}
	public void setProcessType(String processType) {
		this.processType = processType;
	}
	
}
