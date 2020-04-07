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

package com.byd.web.sys.masterdata.service;

import java.util.List;
import java.util.Map;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.byd.utils.R;
import com.byd.web.sys.masterdata.entity.SysDeptEntity;

/**
 * 部门管理
 * 
 * @author cscc
 * @email 
 * @date 2017-06-20 15:23:47
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface DeptRemote {
	/**
	 * 列表
	 */
	@RequestMapping(value = "/admin-service/masterdata/dept/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SysDeptEntity> list(@RequestParam(value = "params") Map<String, Object> params);
	
	/**
	 * 整个部门列表
	 */
	@RequestMapping(value = "/admin-service/masterdata/dept/listall", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SysDeptEntity> listall();

	/**
	 * 选择部门(添加、修改菜单)
	 */
	@RequestMapping(value = "/admin-service/masterdata/dept/select", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R select();

	/**
	 * 上级部门Id(管理员则为0)
	 */
	@RequestMapping(value = "/admin-service/masterdata/dept/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info();
	
	/**
	 * 信息
	 */
	@RequestMapping(value = "/admin-service/masterdata/dept/info/{deptId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info(@PathVariable("deptId") Long deptId);
	
	/**
	 * 保存
	 */
	@RequestMapping(value = "/admin-service/masterdata/dept/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestBody SysDeptEntity dept);
	
	/**
	 * 修改
	 */
	@RequestMapping(value = "/admin-service/masterdata/dept/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestBody SysDeptEntity dept);
	
	@RequestMapping(value = "/admin-service/masterdata/dept/enableDept", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R enableDept(@RequestParam(value = "deptId") Long deptId);
	
	@RequestMapping(value = "/admin-service/masterdata/dept/disabledDept", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R disabledDept(@RequestParam(value = "deptId") Long deptId);
	
	@RequestMapping(value = "/admin-service/masterdata/dept/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delete(@RequestParam(value = "deptId") Long deptId);
	
	/**
	 * ztree 简单格式json
	 * @return
	 */
	@RequestMapping(value = "/admin-service/masterdata/dept/ztreeDepts", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SysDeptEntity> deptListForZtree(@RequestParam(value = "params") Map<String, Object> params);
	
}
