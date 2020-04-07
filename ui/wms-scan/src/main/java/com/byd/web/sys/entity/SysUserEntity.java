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

package com.byd.web.sys.entity;


import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.baomidou.mybatisplus.annotations.KeySequence;
import com.baomidou.mybatisplus.annotations.TableField;
import com.baomidou.mybatisplus.annotations.TableId;
import com.baomidou.mybatisplus.annotations.TableName;
import com.baomidou.mybatisplus.enums.IdType;
import com.byd.utils.validator.group.AddGroup;
import com.byd.utils.validator.group.UpdateGroup;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * 系统用户
 * 
 * @author cscc
 * @email 
 * @date 2016年9月18日 上午9:28:55
 */
@TableName("sys_user")
@KeySequence("SEQ_SYS_USER")
public class SysUserEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	
	/**
	 * 用户ID
	 */
	@TableId(value = "USER_ID", type = IdType.INPUT)
	private Long userId;

	/**
	 * 用户名
	 */
	@NotBlank(message="用户名不能为空", groups = {AddGroup.class, UpdateGroup.class})
	private String username;

	/**
	 * 密码
	 */
	@NotBlank(message="密码不能为空", groups = AddGroup.class)
	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	/**
	 * 盐
	 */
	private String salt;

	/**
	 * 邮箱
	 */
	@NotBlank(message="邮箱不能为空", groups = {AddGroup.class, UpdateGroup.class})
	@Email(message="邮箱格式不正确", groups = {AddGroup.class, UpdateGroup.class})
	private String email;

	/**
	 * 手机号
	 */
	private String mobile;

	/**
	 * 状态  0：禁用   1：正常
	 */
	private Integer status;
	
	/**
	 * 角色ID列表
	 */
	@TableField(exist=false)
	private List<Long> roleIdList;

	/**
	 * 创建时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String createDate;

	/**
	 * 部门ID
	 */
	@NotNull(message="部门不能为空", groups = {AddGroup.class, UpdateGroup.class})
	private Long deptId;

	/**
	 * 部门名称
	 */
	@TableField(exist=false)
	private String deptName;
	
	/**
	 * 工号
	 */
	private String staffNumber;
	
	/**
	 * 全名
	 */
	private String fullName;
	
	/**
	 * 员工类型
	 */
	private String userType;
	
	/**
	 * 最近一次登录的IP
	 */
	private String lastLoginIp;

	/**
	 * 最近一次登录时间
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String lastLoginDate;
	
	/**
	 * 创建人
	 */
	private String createBy;
	
	/**
	 * 更新人
	 */
	private String updateBy;
	
	/**
	 * 更新日期
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String updateDate;
	
	/**
	 * 在职状态
	 */
	private String workingStatus;
	
	/**
	 * 性别
	 */
	private String sex;
	
	/**
	 * 生日
	 */
	@JsonFormat(pattern = "yyyy-MM-dd")
	private String birthday;
	
	/**
	 * 年龄
	 */
	private String age;
	
	/**
	 * 最高学历
	 */
	private String highestEducation;
	
	/**
	 * 应届生届别
	 */
	private String freshStudent;
	
	/**
	 * 政治面貌
	 */
	private String politicalStatus;
	
	/**
	 * 身份证号码
	 */
	private String identityCard;
	
	/**
	 * 入厂日期
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String factoryIncomingDate;
	
	/**
	 * 员工级别
	 */
	private String staffLevel;
	
	/**
	 * 级别工资
	 */
	private String basicSalary;
	
	/**
	 * 技能系数
	 */
	private String skillParameter;
	
	/**
	 * 计资方式 0 计时 1 计件
	 */
	private String salaryType;
	
	/**
	 * 岗位
	 */
	private String job;
	
	/**
	 * 入职渠道   0 内招 1 内部推荐 2 网络 3 人才市场 4 学校职介 5 门口 6其他
	 */
	private String joinChannel;
	
	/**
	 * 离职方式  0 员工提出解除 1 自离 2 公司提出解除 3未到车间报到 4 其他
	 */
	private String leaveWay;
	
	/**
	 * 离职原因 0 薪酬待遇 1 个人发展 2 家庭原因 3自离 4 住宿条件 5工作环境 6 劳动强度 7 公司提出解除 8 未到车间报到
	 */
	private String leaveReason;
	
	/**
	 * 离职日期
	 */
	@JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
	private String leaveDate;
	
	/**
	 * 原公司
	 */
	private String lastCompany;
	
	/**
	 * 原公司离职原因
	 */
	private String lastLeaveReason;
	
	/**
	 * 家庭住址
	 */
	private String familyAddress;
	
	/**
	 * 国家
	 */
	private String nation;
	
	/**
	 * 法人
	 */
	private String corporation;
	
	/**
	 * 工作地点
	 */
	private String workplace;
	/**
	 * 厂牌芯片编码
	 */
	private String cardHd;
	
	
	
	public String getStaffNumber() {
		return staffNumber;
	}


	public void setStaffNumber(String staffNumber) {
		this.staffNumber = staffNumber;
	}


	public String getFullName() {
		return fullName;
	}


	public void setFullName(String fullName) {
		this.fullName = fullName;
	}


	public String getUserType() {
		return userType;
	}


	public void setUserType(String userType) {
		this.userType = userType;
	}


	public String getLastLoginIp() {
		return lastLoginIp;
	}


	public void setLastLoginIp(String lastLoginIp) {
		this.lastLoginIp = lastLoginIp;
	}


	public String getLastLoginDate() {
		return lastLoginDate;
	}


	public void setLastLoginDate(String lastLoginDate) {
		this.lastLoginDate = lastLoginDate;
	}


	public String getCreateBy() {
		return createBy;
	}


	public void setCreateBy(String createBy) {
		this.createBy = createBy;
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


	public String getWorkingStatus() {
		return workingStatus;
	}


	public void setWorkingStatus(String workingStatus) {
		this.workingStatus = workingStatus;
	}


	public String getSex() {
		return sex;
	}


	public void setSex(String sex) {
		this.sex = sex;
	}


	public String getBirthday() {
		return birthday;
	}


	public void setBirthday(String birthday) {
		this.birthday = birthday;
	}


	public String getAge() {
		return age;
	}


	public void setAge(String age) {
		this.age = age;
	}


	public String getHighestEducation() {
		return highestEducation;
	}


	public void setHighestEducation(String highestEducation) {
		this.highestEducation = highestEducation;
	}


	public String getFreshStudent() {
		return freshStudent;
	}


	public void setFreshStudent(String freshStudent) {
		this.freshStudent = freshStudent;
	}


	public String getPoliticalStatus() {
		return politicalStatus;
	}


	public void setPoliticalStatus(String politicalStatus) {
		this.politicalStatus = politicalStatus;
	}


	public String getIdentityCard() {
		return identityCard;
	}


	public void setIdentityCard(String identityCard) {
		this.identityCard = identityCard;
	}


	public String getFactoryIncomingDate() {
		return factoryIncomingDate;
	}


	public void setFactoryIncomingDate(String factoryIncomingDate) {
		this.factoryIncomingDate = factoryIncomingDate;
	}


	public String getStaffLevel() {
		return staffLevel;
	}


	public void setStaffLevel(String staffLevel) {
		this.staffLevel = staffLevel;
	}


	public String getBasicSalary() {
		return basicSalary;
	}


	public void setBasicSalary(String basicSalary) {
		this.basicSalary = basicSalary;
	}


	public String getSkillParameter() {
		return skillParameter;
	}


	public void setSkillParameter(String skillParameter) {
		this.skillParameter = skillParameter;
	}


	public String getSalaryType() {
		return salaryType;
	}


	public void setSalaryType(String salaryType) {
		this.salaryType = salaryType;
	}


	public String getJob() {
		return job;
	}


	public void setJob(String job) {
		this.job = job;
	}


	public String getJoinChannel() {
		return joinChannel;
	}


	public void setJoinChannel(String joinChannel) {
		this.joinChannel = joinChannel;
	}


	public String getLeaveWay() {
		return leaveWay;
	}


	public void setLeaveWay(String leaveWay) {
		this.leaveWay = leaveWay;
	}


	public String getLeaveReason() {
		return leaveReason;
	}


	public void setLeaveReason(String leaveReason) {
		this.leaveReason = leaveReason;
	}


	public String getLeaveDate() {
		return leaveDate;
	}


	public void setLeaveDate(String leaveDate) {
		this.leaveDate = leaveDate;
	}


	public String getLastCompany() {
		return lastCompany;
	}


	public void setLastCompany(String lastCompany) {
		this.lastCompany = lastCompany;
	}


	public String getLastLeaveReason() {
		return lastLeaveReason;
	}


	public void setLastLeaveReason(String lastLeaveReason) {
		this.lastLeaveReason = lastLeaveReason;
	}


	public String getFamilyAddress() {
		return familyAddress;
	}


	public void setFamilyAddress(String familyAddress) {
		this.familyAddress = familyAddress;
	}


	public String getNation() {
		return nation;
	}


	public void setNation(String nation) {
		this.nation = nation;
	}


	public String getCorporation() {
		return corporation;
	}


	public void setCorporation(String corporation) {
		this.corporation = corporation;
	}


	public String getWorkplace() {
		return workplace;
	}


	public void setWorkplace(String workplace) {
		this.workplace = workplace;
	}


	public void setUserId(Long userId) {
		this.userId = userId;
	}

	
	public Long getUserId() {
		return userId;
	}
	
	/**
	 * 设置：用户名
	 * @param username 用户名
	 */
	public void setUsername(String username) {
		this.username = username;
	}

	/**
	 * 获取：用户名
	 * @return String
	 */
	public String getUsername() {
		return username;
	}
	
	/**
	 * 设置：密码
	 * @param password 密码
	 */
	public void setPassword(String password) {
		this.password = password;
	}

	/**
	 * 获取：密码
	 * @return String
	 */
	public String getPassword() {
		return password;
	}
	
	/**
	 * 设置：邮箱
	 * @param email 邮箱
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * 获取：邮箱
	 * @return String
	 */
	public String getEmail() {
		return email;
	}
	
	/**
	 * 设置：手机号
	 * @param mobile 手机号
	 */
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	/**
	 * 获取：手机号
	 * @return String
	 */
	public String getMobile() {
		return mobile;
	}
	
	/**
	 * 设置：状态  0：禁用   1：正常
	 * @param status 状态  0：禁用   1：正常
	 */
	public void setStatus(Integer status) {
		this.status = status;
	}

	/**
	 * 获取：状态  0：禁用   1：正常
	 * @return Integer
	 */
	public Integer getStatus() {
		return status;
	}
	
	/**
	 * 设置：创建时间
	 * @param createDate 创建时间
	 */
	public void setCreateDate(String createDate) {
		this.createDate = createDate;
	}

	/**
	 * 获取：创建时间
	 * @return String
	 */
	public String getCreateDate() {
		return createDate;
	}

	public List<Long> getRoleIdList() {
		return roleIdList;
	}

	public void setRoleIdList(List<Long> roleIdList) {
		this.roleIdList = roleIdList;
	}

	public String getSalt() {
		return salt;
	}

	public void setSalt(String salt) {
		this.salt = salt;
	}

	public Long getDeptId() {
		return deptId;
	}

	public void setDeptId(Long deptId) {
		this.deptId = deptId;
	}

	public String getDeptName() {
		return deptName;
	}

	public void setDeptName(String deptName) {
		this.deptName = deptName;
	}


	public String getCardHd() {
		return cardHd;
	}


	public void setCardHd(String cardHd) {
		this.cardHd = cardHd;
	}
	
}
