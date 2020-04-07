/**
 * Copyright 2018 cscc
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

package com.byd.admin.modules.sys.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.admin.common.annotation.SysLog;
import com.byd.admin.modules.sys.entity.SysRoleEntity;
import com.byd.admin.modules.sys.service.SysRoleMenuService;
import com.byd.admin.modules.sys.service.SysRoleService;
import com.byd.admin.modules.sys.service.SysUserRoleService;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.utils.validator.ValidatorUtils;

/**
 * 角色管理
 * 
 * @author cscc
 * @email 
 * @date 2016年11月8日 下午2:18:33
 */
@RestController
@RequestMapping("/sys/role")
public class SysRoleController {
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private SysRoleMenuService sysRoleMenuService;
	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private UserUtils userUtils;
	
	/**
	 * 角色列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		PageUtils page = sysRoleService.queryPage(params);

		return R.ok().put("page", page);
	}
	/**
	 * 数据权限查询角色列表
	 */
	@RequestMapping("/queryAlls")
	public R queryAlls(@RequestParam Map<String, Object> params){
		System.err.println(params.toString());
		PageUtils page = sysRoleService.queryAlls(params);

		return R.ok().put("page", page);
	}

	
	/**
	 * 角色列表
	 */
	@RequestMapping("/select")
	public R select(){
		List<SysRoleEntity> list = sysRoleService.selectList(null);
		
		return R.ok().put("list", list);
	}
	
	/**
	 * 角色信息
	 */
	@RequestMapping("/info/{roleId}")
	public R info(@PathVariable("roleId") Long roleId){
		SysRoleEntity role = sysRoleService.selectById(roleId);
		
		//查询角色对应的菜单
		List<Long> menuIdList = sysRoleMenuService.queryMenuIdList(roleId);
		role.setMenuIdList(menuIdList);
		return R.ok().put("role", role);
	}
	
	/**
	 * 保存角色
	 */
	@SysLog("保存角色")
	@RequestMapping("/save")
	public R save(@RequestBody SysRoleEntity role){
		ValidatorUtils.validateEntity(role);
		role.setCreateBy(userUtils.getUser().get("USERNAME").toString());
		role.setStatus("0");
		
		sysRoleService.save(role);
		
		return R.ok();
	}
	
	/**
	 * 修改角色
	 */
	@SysLog("修改角色")
	@RequestMapping("/update")
	public R update(@RequestBody SysRoleEntity role) throws Exception {
		ValidatorUtils.validateEntity(role);

		return sysRoleService.update(role);
		
	}
	
	/**
	 * 删除角色
	 */
	@SysLog("删除角色")
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] roleIds) throws Exception {

		return sysRoleService.deleteBatch(roleIds);
		
	}
	
	@SysLog("禁用角色")
	@RequestMapping("/disableRole/{roleId}")
	public R disableRole(@PathVariable("roleId") long roleId) throws Exception {

		return sysRoleService.disableRole(roleId);

	}
	
	@SysLog("启用角色")
	@RequestMapping("/enableRole/{roleId}")
	public R enableRole(@PathVariable("roleId") long roleId) throws Exception {

		return sysRoleService.enableRole(roleId);

	}
	
	@RequestMapping("/getRoleUser/{roleId}")
	public R getRoleUser(@PathVariable("roleId") long roleId) {
		List<Long> userIds=sysUserRoleService.queryUserIdList(roleId);
		return R.ok().put("list",userIds);
		
	}

	/**
	 * 角色管理：分配用户
	 * @param roleId
	 * @param userIds
	 * @return
	 * @throws Exception
	 */
	@SysLog("角色授权用户")
	@RequestMapping("/authRoleUser")
	public R authRoleUser(Long roleId,@RequestParam(name="userIds",required=false) String userIds) throws Exception {
		String[] idArr = userIds.split(";");
		Long[] uIds = new Long[idArr.length];
		for (int i=0;i<idArr.length;i++) {
			uIds[i] = Long.valueOf(idArr[i]);
		}
		R result=sysRoleService.updateRoleUsers(uIds,roleId);
		return result;
	}
}
