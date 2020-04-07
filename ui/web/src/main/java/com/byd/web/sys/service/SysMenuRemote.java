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
import com.byd.web.sys.entity.SysMenuEntity;

/**
 * 系统菜单
 * 
 * @author cscc
 * @email 
 * @date 2016年10月27日 下午9:58:15
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface SysMenuRemote {

	/**
	 * 导航菜单
	 */
	@RequestMapping(value = "/admin-service/sys/menu/nav", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R nav();
	
	/**
	 * 所有菜单列表
	 */
	@RequestMapping(value = "/admin-service/sys/menu/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SysMenuEntity> list();
	
	@RequestMapping(value = "/admin-service/sys/menu/getAllAuthList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Map<String,Object>> getAllAuthList();
	
	@RequestMapping(value = "/admin-service/sys/menu/list2", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<SysMenuEntity> list2(@RequestParam  Map<String,Object> params);
	
	/**
	 * 选择菜单(添加、修改菜单)
	 */
	@RequestMapping(value = "/admin-service/sys/menu/select", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R select();
	
	/**
	 * 菜单信息
	 */
	@RequestMapping(value = "/admin-service/sys/menu/info/{menuId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info(@PathVariable("menuId") Long menuId);
	
	/**
	 * 保存
	 */
	@RequestMapping(value = "/admin-service/sys/menu/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestBody SysMenuEntity menu);
	
	/**
	 * 修改
	 */
	@RequestMapping(value = "/admin-service/sys/menu/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestBody SysMenuEntity menu);
	
	/**
	 * 删除
	 */
	@RequestMapping(value = "/admin-service/sys/menu/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delete(@RequestParam(value="menuId") long menuId);
}
