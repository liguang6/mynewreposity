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


import com.baomidou.mybatisplus.service.IService;
import com.byd.admin.modules.sys.entity.SysRoleEntity;
import com.byd.utils.PageUtils;
import com.byd.utils.R;

import java.util.Map;


/**
 * 角色
 * 
 * @author cscc
 * @email 
 * @date 2016年9月18日 上午9:42:52
 */
public interface SysRoleService extends IService<SysRoleEntity> {
	PageUtils queryAlls(Map<String, Object> params);
	PageUtils queryPage(Map<String, Object> params);

	void save(SysRoleEntity role);

	R update(SysRoleEntity role) throws Exception;

	R deleteBatch(Long[] roleIds) throws Exception;

	R disableRole(long roleId) throws Exception;

	R enableRole(long roleId) throws Exception;
	/**
	 * 角色分配用户
	 * @param userIds
	 * @param roleId
	 */
	R updateRoleUsers(Long[] userIds, long roleId) throws Exception;

}
