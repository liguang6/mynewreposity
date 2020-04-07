/**
 * Copyright 2018 CSCC
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.byd.web.sys.masterdata.entity;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableLogic;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;

import java.io.Serializable;
import java.util.List;


/**
 * 部门管理
 * 
 * @author cscc
 * @email 
 * @date 2017-06-20 15:23:47
 */
@TableName("sys_dept")
@KeySequence("SEQ_SYS_DEPT")//类注解
public class SysDeptEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	@TableId(value = "DEPT_ID", type = IdType.INPUT)
	private Long deptId;
	private Long parentId;
	private String code;
	private String name;
	private String treeSort;
	private String treeLeaf;
	private String treeLevel;
	private String treeNames;
	private String deptType;
	@TableField(exist=false)
	private String deptTypeName;
	private String deptKind;
	private String leader;
	@TableLogic
	private String status;
	private String createBy;
	private String createDate;
	private String updateBy;
	private String updateDate;
	private String remarks;

	@TableField(exist=false)
	private String parentName;
	@TableField(exist=false)
	private Boolean isParent;
	@TableField(exist=false)
	private Long id;
	@TableField(exist=false)
	private String parentCode;
	@TableField(exist=false)
	private Boolean isRoot;
	@TableField(exist=false)
	private Boolean isTreeLeaf;
	
	@TableField(exist=false)
	private Boolean open;
	@TableField(exist=false)
	private List<?> list;
	
	
	public String getDeptTypeName() {
		return deptTypeName;
	}
	public void setDeptTypeName(String deptTypeName) {
		this.deptTypeName = deptTypeName;
	}
	public String getParentCode() {
		return parentCode;
	}
	public void setParentCode(String parentCode) {
		this.parentCode = parentCode;
	}
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	public Boolean getIsRoot() {
		return isRoot;
	}
	public void setIsRoot(Boolean isRoot) {
		this.isRoot = isRoot;
	}
	public Boolean getIsTreeLeaf() {
		return isTreeLeaf;
	}
	public void setIsTreeLeaf(Boolean isTreeLeaf) {
		this.isTreeLeaf = isTreeLeaf;
	}
	public Long getDeptId() {
		return deptId;
	}
	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}
	public Long getParentId() {
		return parentId;
	}
	public void setParentId(Long parentId) {
		this.parentId = parentId;
	}
	public String getParentName() {
		return parentName;
	}
	public void setParentName(String parentName) {
		this.parentName = parentName;
	}
	public String getCode() {
		return code;
	}
	public void setCode(String code) {
		this.code = code;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getTreeSort() {
		return treeSort;
	}
	public void setTreeSort(String treeSort) {
		this.treeSort = treeSort;
	}
	public String getTreeLeaf() {
		return treeLeaf;
	}
	public void setTreeLeaf(String treeLeaf) {
		this.treeLeaf = treeLeaf;
	}
	public String getTreeLevel() {
		return treeLevel;
	}
	public void setTreeLevel(String treeLevel) {
		this.treeLevel = treeLevel;
	}
	public String getTreeNames() {
		return treeNames;
	}
	public void setTreeNames(String treeNames) {
		this.treeNames = treeNames;
	}
	public String getDeptType() {
		return deptType;
	}
	public void setDeptType(String deptType) {
		this.deptType = deptType;
	}
	public String getDeptKind() {
		return deptKind;
	}
	public void setDeptKind(String deptKind) {
		this.deptKind = deptKind;
	}
	public String getLeader() {
		return leader;
	}
	public void setLeader(String leader) {
		this.leader = leader;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public String getCreateBy() {
		return createBy;
	}
	public void setCreateBy(String createBy) {
		this.createBy = createBy;
	}
	public String getCreateDate() {
		return createDate;
	}
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}
	public String getUpdateBy() {
		return updateBy;
	}
	public void setUpdateBy(String updateBy) {
		this.updateBy = updateBy;
	}
	public String getUpdateDate() {
		return updateDate;
	}
	public void setUpdateDate(String updateDate) {
		this.updateDate = updateDate;
	}
	public String getRemarks() {
		return remarks;
	}
	public void setRemarks(String remarks) {
		this.remarks = remarks;
	}
	public Boolean getOpen() {
		return open;
	}
	public void setOpen(Boolean open) {
		this.open = open;
	}
	public List<?> getList() {
		return list;
	}
	public void setList(List<?> list) {
		this.list = list;
	}
	public Boolean getIsParent() {
		return isParent;
	}
	public void setIsParent(Boolean isParent) {
		this.isParent = isParent;
	}
	
}
