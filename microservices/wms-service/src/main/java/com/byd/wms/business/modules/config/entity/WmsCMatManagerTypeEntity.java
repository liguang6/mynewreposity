package com.byd.wms.business.modules.config.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.Date;

/**
 * 仓库人料关系模式配置
 * 
 * @author cscc tangj
 * @email 
 * @date 2018-12-25 11:31:32
 */
@TableName("WMS_C_MAT_MANAGER_TYPE")
@KeySequence("SEQ_WMS_C_MAT_MANAGER_TYPE")
public class WmsCMatManagerTypeEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * ID
	 */
	@TableId(value = "ID",type=IdType.INPUT)
	private Long id;
	/**
	 * 工厂代码
	 */
	private String werks;
	/**
	 * 仓库号
	 */
	private String whNumber;
	/**
	 * 物料管理方式 00 人  10 区域 20 库位 30 存储区 物料匹配按照代号升序查找
	 */
	private String matManagerType;
	/**
	 * 授权码 当物料管理方式为库位时，授权码只能从工厂库位里选择
	 */
	private String authorizeCode;
	/**
	 * 名称
	 */
	private String authorizeName;
	/**
	 * 类型 01 仓库员 02 QC
	 */
	private String managerType;
	/**
	 * 管理员工号
	 */
	private String managerStaff;
	/**
	 * 管理员姓名
	 */
	private String manager;
	/**
	 * 主管工号
	 */
	private String leaderStaff;
	/**
	 * 主管名称
	 */
	private String leader;
	/**
	 * 删除标示 0 否 X是 默认0
	 */
	private String del;
	/**
	 * 创建人
	 */
	private String creator;
	/**
	 * 创建时间
	 */
	private String createDate;
	/**
	 * 编辑人
	 */
	private String editor;
	/**
	 * 编辑时间
	 */
	private String editDate;
	// 物料管理方式描述
	@TableField(exist=false)
	private String matManagerTypeDesc;
	// 类型描述
	@TableField(exist=false)
	private String managerTypeDesc;
	// 校验信息字段，不存入数据库
	@TableField(exist=false)
	private String msg;


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
	 * 设置：工厂代码
	 */
	public void setWerks(String werks) {
		this.werks = werks;
	}
	/**
	 * 获取：工厂代码
	 */
	public String getWerks() {
		return werks;
	}
	/**
	 * 设置：仓库号
	 */
	public void setWhNumber(String whNumber) {
		this.whNumber = whNumber;
	}
	/**
	 * 获取：仓库号
	 */
	public String getWhNumber() {
		return whNumber;
	}
	/**
	 * 设置：物料管理方式 00 人  10 区域 20 库位 30 存储区 物料匹配按照代号升序查找
	 */
	public void setMatManagerType(String matManagerType) {
		this.matManagerType = matManagerType;
	}
	/**
	 * 获取：物料管理方式 00 人  10 区域 20 库位 30 存储区 物料匹配按照代号升序查找
	 */
	public String getMatManagerType() {
		return matManagerType;
	}
	/**
	 * 设置：授权码 当物料管理方式为库位时，授权码只能从工厂库位里选择
	 */
	public void setAuthorizeCode(String authorizeCode) {
		this.authorizeCode = authorizeCode;
	}
	/**
	 * 获取：授权码 当物料管理方式为库位时，授权码只能从工厂库位里选择
	 */
	public String getAuthorizeCode() {
		return authorizeCode;
	}
	/**
	 * 设置：名称
	 */
	public void setAuthorizeName(String authorizeName) {
		this.authorizeName = authorizeName;
	}
	/**
	 * 获取：名称
	 */
	public String getAuthorizeName() {
		return authorizeName;
	}
	/**
	 * 设置：类型 01 仓库员 02 QC
	 */
	public void setManagerType(String managerType) {
		this.managerType = managerType;
	}
	/**
	 * 获取：类型 01 仓库员 02 QC
	 */
	public String getManagerType() {
		return managerType;
	}
	/**
	 * 设置：管理员工号
	 */
	public void setManagerStaff(String managerStaff) {
		this.managerStaff = managerStaff;
	}
	/**
	 * 获取：管理员工号
	 */
	public String getManagerStaff() {
		return managerStaff;
	}
	/**
	 * 设置：管理员姓名
	 */
	public void setManager(String manager) {
		this.manager = manager;
	}
	/**
	 * 获取：管理员姓名
	 */
	public String getManager() {
		return manager;
	}
	/**
	 * 设置：主管工号
	 */
	public void setLeaderStaff(String leaderStaff) {
		this.leaderStaff = leaderStaff;
	}
	/**
	 * 获取：主管工号
	 */
	public String getLeaderStaff() {
		return leaderStaff;
	}
	/**
	 * 设置：主管名称
	 */
	public void setLeader(String leader) {
		this.leader = leader;
	}
	/**
	 * 获取：主管名称
	 */
	public String getLeader() {
		return leader;
	}
	/**
	 * 设置：删除标示 0 否 X是 默认0
	 */
	public void setDel(String del) {
		this.del = del;
	}
	/**
	 * 获取：删除标示 0 否 X是 默认0
	 */
	public String getDel() {
		return del;
	}
	/**
	 * 设置：创建人
	 */
	public void setCreator(String creator) {
		this.creator = creator;
	}
	/**
	 * 获取：创建人
	 */
	public String getCreator() {
		return creator;
	}
	/**
	 * 设置：创建时间
	 */
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	/**
	 * 获取：创建时间
	 */
	public String getCreateDate() {
		return createDate;
	}
	/**
	 * 设置：编辑人
	 */
	public void setEditor(String editor) {
		this.editor = editor;
	}
	/**
	 * 获取：编辑人
	 */
	public String getEditor() {
		return editor;
	}
	/**
	 * 设置：编辑时间
	 */
	public void setEditDate(String editDate) {
		this.editDate = editDate;
	}
	/**
	 * 获取：编辑时间
	 */
	public String getEditDate() {
		return editDate;
	}
	public String getMsg() {
		return msg;
	}
	public void setMsg(String msg) {
		this.msg = msg;
	}
	public String getMatManagerTypeDesc() {
		return matManagerTypeDesc;
	}
	public void setMatManagerTypeDesc(String matManagerTypeDesc) {
		this.matManagerTypeDesc = matManagerTypeDesc;
	}
	public String getManagerTypeDesc() {
		return managerTypeDesc;
	}
	public void setManagerTypeDesc(String managerTypeDesc) {
		this.managerTypeDesc = managerTypeDesc;
	}
	
	
}
