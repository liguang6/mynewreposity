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

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.utils.R;
import com.byd.web.sys.entity.SysMenuEntity;
import com.byd.web.sys.service.SysMenuRemote;

/**
 * 系统菜单
 * 
 * @author cscc
 * @email 
 * @date 2016年10月27日 下午9:58:15
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController{
	@Autowired
	private SysMenuRemote sysMenuRemote;

	/**
	 * 导航菜单
	 */
	@RequestMapping("/nav")
	public R nav(){
		return sysMenuRemote.nav();
	}
	
	/**
	 * 所有菜单列表
	 */
	@RequestMapping("/list")
	public List<SysMenuEntity> list(){
		return sysMenuRemote.list();
	}
	
	/**
	 * 所有菜单列表
	 */
	@RequestMapping("/getAllAuthList")
	public List<Map<String,Object>> getAllAuthList(){
		return sysMenuRemote.getAllAuthList();
	}
	
	@RequestMapping("/list2")
	public List<SysMenuEntity> list2(@RequestParam  Map<String,Object> params){
		return sysMenuRemote.list2(params);
	}
	
	/**
	 * 选择菜单(添加、修改菜单)
	 */
	@RequestMapping("/select")
	public R select(){
		return sysMenuRemote.select();
	}
	
	/**
	 * 菜单信息
	 */
	@RequestMapping("/info/{menuId}")
	public R info(@PathVariable("menuId") Long menuId){
		return sysMenuRemote.info(menuId);
	}
	
	/**
	 * 保存
	 */
	@RequestMapping("/save")
	public R save(@RequestBody SysMenuEntity menu){
		return sysMenuRemote.save(menu);
	}
	
	/**
	 * 修改
	 */
	@RequestMapping("/update")
	public R update(@RequestBody SysMenuEntity menu){
		return sysMenuRemote.update(menu);
	}
	
	/**
	 * 删除
	 */
	@RequestMapping("/delete")
	public R delete(long menuId){
		return sysMenuRemote.delete(menuId);
	}
	
}
