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
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.admin.modules.sys.entity.SysUserEntity;

import java.util.List;
import java.util.Map;

import org.springframework.web.bind.annotation.RequestParam;


/**
 * 系统用户
 * 
 * @author cscc
 * @email 
 * @date 2016年9月18日 上午9:43:39
 */
public interface SysUserService extends IService<SysUserEntity> {

	PageUtils queryPage(Map<String, Object> params);
	PageUtils queryPage2(Map<String, Object> params);
	
	/**
	 * 查询用户的所有菜单ID
	 */
	List<Long> queryAllMenuId(Long userId);
	
	/**
	 * 保存用户
	 */
	void save(SysUserEntity user);
	
	/**
	 * 修改用户(没有用到)
	 */
	void update(SysUserEntity user);

	/**
	 * 修改密码
	 * @param userId       用户ID
	 * @param password     原密码
	 * @param newPassword  新密码
	 */
	boolean updatePassword(Long userId, String password, String newPassword);
	
	/**
	 * 更新用户的部门 和 角色信息
	 * @param userId 用户
	 * @param roles  角色ID集合
	 */
	public R updateRoleAndDept(Long userId,List<String> roles) throws Exception;
	
	public boolean checkPermission(Long userId,String permission);
	
	public Map<String,Object> getUserByUserName(String username);
	
	public String queryCardInfo(String list) throws Exception;
	public Map<String, Object> getUserInfoByCardInfo(String cardHd);
	
	public List<Map<String,Object>> getUserKanban(Map<String, Object> params);
	
	List<Map<String, Object>> getUserList(String userName);

	List<SysUserEntity> getUserListByRoleId(Long roleId);

	List<SysUserEntity> getUserListByRoleIdUids(Long roleId,List<Long> userIds);

	List<Map<String, Object>> getRoleList(String roleName);

	List<Map<String, Object>> getMenuList(String menuName);
	
	public Map<String,Object> getUserInfo(Map<String, Object> params);
	
	Map<String,Object> getUserMap(List<String> params);
	
	List<Map<String,Object>> getUserMapList(List<String> params);
	
	List<Map<String,Object>> getUserMapNList(List<String> params);
	
}
