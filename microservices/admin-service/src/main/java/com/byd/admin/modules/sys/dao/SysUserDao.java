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

package com.byd.admin.modules.sys.dao;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.admin.modules.sys.entity.SysUserEntity;

import java.util.List;
import java.util.Map;

/**
 * 系统用户
 * 
 * @author cscc
 * @email 
 * @date 2016年9月18日 上午9:34:11
 */
public interface SysUserDao extends BaseMapper<SysUserEntity> {
	
	/**
	 * 查询用户的所有权限
	 * @param userId  用户ID
	 */
	List<String> queryAllPerms(Long userId);
	
	/**
	 * 查询用户的所有菜单ID
	 */
	List<Long> queryAllMenuId(Long userId);
	
	Map<String,Object> getUserByUserName(String username);
	
	List<Map<String,Object>> getUserKanban(Map<String, Object> params);
	
	List<Map<String, Object>> getUserList(String userId);

	List<SysUserEntity> getUserListByRoleId(Long roleId);

	List<SysUserEntity> getUserListByRoleIdUids(Long roleId,List<Long> userIds);

	List<Map<String, Object>> getRoleList(String roleId);

	List<Map<String, Object>> getMenuList(String menuId);
	
	Map<String, Object> getUserInfo(Map<String, Object> params);
	
	List<Map<String, Object>> getUserMap(List<String> params);
	
	List<Map<String, Object>> getUserMapN(List<String> params);
	
}
