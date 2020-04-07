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

package com.byd.admin.modules.sys.service;


import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.admin.modules.sys.entity.SysMenuEntity;


/**
 * 菜单管理
 * 
 * @author cscc
 * @email 
 * @date 2016年9月18日 上午9:42:16
 */
public interface SysMenuService extends IService<SysMenuEntity> {

	/**
	 * 根据父菜单，查询子菜单
	 * @param parentId 父菜单ID
	 * @param menuIdList  用户菜单ID
	 */
	List<SysMenuEntity> queryListParentId(Long parentId, List<Long> menuIdList);

	/**
	 * 根据父菜单，查询子菜单
	 * @param parentId 父菜单ID
	 */
	List<SysMenuEntity> queryListParentId(Long parentId);

	/**
	 * 根据菜单ID查询菜单信息
	 * @param menuId 菜单ID
	 */
	SysMenuEntity queryMenuListById(Long menuId);

	/**
	 * 根据菜单menuKey查询菜单信息
	 * @param menuKey 菜单code
	 */
	SysMenuEntity queryListByCode(String menuKey);

	/**
	 * 根据角色ID查询菜单信息
	 * @param roleId 角色ID
	 */
	List<SysMenuEntity> queryMenuListByRoleId(Long roleId);

	List<SysMenuEntity> queryMenuListByRoleIdMenu(Long roleId,List<Long> menuIds);
	
	/**
	 * 获取不包含按钮的菜单列表
	 */
	List<SysMenuEntity> queryNotButtonList();
	
	/**
	 * 获取用户菜单列表
	 */
	List<SysMenuEntity> getUserMenuList(Long userId);

	/**
	 * 删除
	 */
	void delete(Long menuId);
	
	List<SysMenuEntity> queryMenuList(Map<String, Object> map);
	
	List<Map<String,Object>> getUserAuthMenuList(Long userId);
	
	List<Map<String,Object>> getAllAuthList();
	
	List<Map<String,Object>> getUserPdaAuthMenuList(Long userId);
}
