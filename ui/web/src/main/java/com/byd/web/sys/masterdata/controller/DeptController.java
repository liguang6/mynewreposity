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

package com.byd.web.sys.masterdata.controller;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.sys.masterdata.entity.SysDeptEntity;
import com.byd.web.sys.masterdata.service.DeptRemote;

/**
 * 部门管理
 * 
 * @author cscc
 * @email 
 * @date 2017-06-20 15:23:47
 */
@RestController
@RequestMapping("/masterdata/dept")
public class DeptController {
	@Autowired
	private DeptRemote sysDeptRemote;
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public List<SysDeptEntity> list(@RequestParam Map<String, Object> params){
		return sysDeptRemote.list(params);
	}
	
	/**
	 * 整个部门列表
	 */
	@RequestMapping("/listall")
/*	@RequiresPermissions("sys:dept:listall")*/
	public List<SysDeptEntity> listall(){
		return sysDeptRemote.listall();
	}

	/**
	 * 选择部门(添加、修改菜单)
	 */
	@RequestMapping("/select")
	public R select(){
		return sysDeptRemote.select();
	}

	/**
	 * 上级部门Id(管理员则为0)
	 */
	@RequestMapping("/info")
	public R info(){
		return sysDeptRemote.info();
	}
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{deptId}")
	public R info(@PathVariable("deptId") Long deptId){
		return sysDeptRemote.info(deptId);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody SysDeptEntity dept){
		return sysDeptRemote.save(dept);
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody SysDeptEntity dept){
		return sysDeptRemote.update(dept);
	}
	
	@RequestMapping("/enableDept")
	public R enableDept(long deptId){
		return sysDeptRemote.enableDept(deptId);
	}
	
	@RequestMapping("/disabledDept")
	public R disabledDept(long deptId){
		return sysDeptRemote.disabledDept(deptId);
	}
	@RequestMapping("/delete")
	public R delete(long deptId){
		return sysDeptRemote.delete(deptId);
	}
	
	/**
	 * ztree 简单格式json
	 * @return
	 */
	@RequestMapping("/ztreeDepts")
	//@RequiresPermissions("sys:dept:list")
	public List<SysDeptEntity> deptListForZtree(@RequestParam Map<String, Object> params){
		return sysDeptRemote.deptListForZtree(params);
	}
	
}
