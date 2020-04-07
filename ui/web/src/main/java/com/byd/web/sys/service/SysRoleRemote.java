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

package com.byd.web.sys.service;

import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;
import com.byd.web.sys.entity.SysRoleEntity;

/**
 * 角色管理
 * 
 * @author cscc
 * @email 
 * @date 2016年11月8日 下午2:18:33
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface SysRoleRemote {
	
	/**
	 * 角色列表
	 */
	@RequestMapping(value = "/admin-service/sys/role/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam Map<String, Object> params);
	
	/**
	 * 角色列表
	 */
	@RequestMapping(value = "/admin-service/sys/role/select", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R select();
	
	/**
	 * 角色信息
	 */
	@RequestMapping(value = "/admin-service/sys/role/info/{roleId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info(@PathVariable("roleId") Long roleId);
	
	/**
	 * 保存角色
	 */
	@RequestMapping(value = "/admin-service/sys/role/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestBody SysRoleEntity role);
	
	/**
	 * 修改角色
	 */
	@RequestMapping(value = "/admin-service/sys/role/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestBody SysRoleEntity role);
	
	/**
	 * 删除角色
	 */
	@RequestMapping(value = "/admin-service/sys/role/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delete(@RequestBody Long[] roleIds);
	
	@RequestMapping(value = "/admin-service/sys/role/disableRole/{roleId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R disableRole(@PathVariable("roleId") long roleId);
	
	@RequestMapping(value = "/admin-service/sys/role/enableRole/{roleId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R enableRole(@PathVariable("roleId") long roleId);
	
	@RequestMapping(value = "/admin-service/sys/role/getRoleUser/{roleId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getRoleUser(@PathVariable("roleId") long roleId);
	
	@RequestMapping(value = "/admin-service/sys/role/authRoleUser", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R authRoleUser(@RequestParam(value="roleId") Long roleId, @RequestParam(name="userIds",required=false) String userIds);

	@RequestMapping(value = "/admin-service/sys/role/queryAlls", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R queryAlls(@RequestParam Map<String, Object> params);

}
