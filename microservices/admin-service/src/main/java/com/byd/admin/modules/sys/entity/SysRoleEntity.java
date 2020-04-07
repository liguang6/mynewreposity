/**
 * @author xiong.jianwu
 */
package com.byd.admin.modules.sys.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.fasterxml.jackson.annotation.JsonFormat;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

/**
 * 角色
 * 
 * @author cscc
 * @email
 * @date 2016年9月18日 上午9:27:38
 */
@TableName("sys_role")
@KeySequence("SEQ_SYS_ROLE")
public class SysRoleEntity implements Serializable {
	private static final long serialVersionUID = 1L;

	/**
	 * 角色ID
	 */
	@TableId(value = "ROLE_ID", type = IdType.INPUT)
	private Long roleId;

	/**
	 * 角色名称
	 */
	@NotBlank(message = "角色名称不能为空")
	private String roleName;

	/**
	 * 备注
	 */
	private String remark;

	/**
	 * 状态（0正常 1删除 2停用）
	 */
	private String status;

	/**
	 * 角色排序
	 */
	private Long roleSort;

	@TableField(exist = false)
	private List<Long> menuIdList;
	@TableField(exist = false)
	private List<Long> deptIdList;

	/**
	 * 创建人
	 */
	private String createBy;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String createTime;

	/**
	 * 设置：
	 * 
	 * @param roleId
	 */
	public void setRoleId(Long roleId) {
		this.roleId = roleId;
	}

	/**
	 * 获取：
	 * 
	 * @return Long
	 */
	public Long getRoleId() {
		return roleId;
	}

	/**
	 * 设置：角色名称
	 * 
	 * @param roleName
	 *            角色名称
	 */
	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	/**
	 * 获取：角色名称
	 * 
	 * @return String
	 */
	public String getRoleName() {
		return roleName;
	}

	/**
	 * 设置：备注
	 * 
	 * @param remark
	 *            备注
	 */
	public void setRemark(String remark) {
		this.remark = remark;
	}

	/**
	 * 获取：备注
	 * 
	 * @return String
	 */
	public String getRemark() {
		return remark;
	}

	public String getCreateTime() {
		return createTime;
	}

	public void setCreateTime(String createTime) {
		this.createTime = createTime;
	}

	public List<Long> getMenuIdList() {
		return menuIdList;
	}

	public void setMenuIdList(List<Long> menuIdList) {
		this.menuIdList = menuIdList;
	}

	public List<Long> getDeptIdList() {
		return deptIdList;
	}

	public void setDeptIdList(List<Long> deptIdList) {
		this.deptIdList = deptIdList;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Long getRoleSort() {
		return roleSort;
	}

	public void setRoleSort(Long roleSort) {
		this.roleSort = roleSort;
	}

	public String getCreateBy() {
		return createBy;
	}

	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}

}
