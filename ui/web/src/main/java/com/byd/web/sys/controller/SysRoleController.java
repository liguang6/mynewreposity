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

package com.byd.web.sys.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.sys.entity.SysRoleEntity;
import com.byd.web.sys.service.SysRoleRemote;

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
	private SysRoleRemote sysRoleRemote;
	
	/**
	 * 角色列表
	 */
	@RequestMapping("/list")
	public R list(@RequestParam Map<String, Object> params){
		return sysRoleRemote.list(params);
	}

	/**
	 * 数据权限查询角色列表
	 */
	@RequestMapping("/queryAlls")
	public R queryAlls(@RequestParam Map<String, Object> params){
		return sysRoleRemote.queryAlls(params);
	}
	
	/**
	 * 角色列表
	 */
	@RequestMapping("/select")
	public R select(){
		return sysRoleRemote.select();
	}
	
	/**
	 * 角色信息
	 */
	@RequestMapping("/info/{roleId}")
	public R info(@PathVariable("roleId") Long roleId){
		return sysRoleRemote.info(roleId);
	}
	
	/**
	 * 保存角色
	 */
	@RequestMapping("/save")
	public R save(@RequestBody SysRoleEntity role){
		return sysRoleRemote.save(role);
	}
	
	/**
	 * 修改角色
	 */
	@RequestMapping("/update")
	public R update(@RequestBody SysRoleEntity role){
		return sysRoleRemote.update(role);
	}
	
	/**
	 * 删除角色
	 */
	@RequestMapping("/delete")
	public R delete(@RequestBody Long[] roleIds){
		return sysRoleRemote.delete(roleIds);
	}
	
	@RequestMapping("/disableRole/{roleId}")
	public R disableRole(@PathVariable("roleId") long roleId) {
		return sysRoleRemote.disableRole(roleId);
	}
	
	@RequestMapping("/enableRole/{roleId}")
	public R enableRole(@PathVariable("roleId") long roleId) {
		return sysRoleRemote.enableRole(roleId);
		
	}
	
	@RequestMapping("/getRoleUser/{roleId}")
	public R getRoleUser(@PathVariable("roleId") long roleId) {
		return sysRoleRemote.getRoleUser(roleId);
	}
	
	@RequestMapping("/authRoleUser")
	public R authRoleUser(Long roleId,@RequestParam(name="userIds[]",required=false) Long[] userIds) {
		String uIds = "";
		for (Long long1 : userIds) {
			uIds +=long1+";";
		}
		
		return sysRoleRemote.authRoleUser(roleId,uIds);
	}
}
