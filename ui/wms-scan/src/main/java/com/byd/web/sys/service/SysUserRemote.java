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
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.byd.utils.R;
import com.byd.web.sys.entity.SysUserEntity;

/**
 * 系统用户
 * 
 * @author cscc
 * @email 
 * @date 2016年10月31日 上午10:40:10
 */
@FeignClient(name = "ADMIN-SERVICE")
public interface SysUserRemote {
	
	/**
	 * 所有用户列表
	 */
	@RequestMapping(value = "/admin-service/sys/user/list", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R list(@RequestParam Map<String, Object> params);
	
	/**
	 * 获取登录的用户信息
	 */
	@RequestMapping(value = "/admin-service/sys/user/info", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info();
	
	/**
	 * 修改登录用户密码
	 */
	@RequestMapping(value = "/admin-service/sys/user/password", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R password(@RequestParam(value="password") String password, @RequestParam(value="newPassword") String newPassword,@RequestParam(value="username") String username);
	
	
	@RequestMapping(value = "/admin-service/sys/user/resetPwd", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R resetPwd(@RequestParam(value="userId") Long userId);
	
	/**
	 * 用户信息
	 */
	@RequestMapping(value = "/admin-service/sys/user/info/{userId}", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R info(@PathVariable("userId") Long userId);
	/**
	 * 保存用户
	 */
	@RequestMapping(value = "/admin-service/sys/user/save", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R save(@RequestBody SysUserEntity user);
	
	/**
	 * 修改用户
	 */
	@RequestMapping(value = "/admin-service/sys/user/update", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R update(@RequestBody SysUserEntity user);
	
	/**
	 * 删除用户
	 */
	@RequestMapping(value = "/admin-service/sys/user/delete", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R delete(@RequestBody Long[] userIds);
	
	
	/**
	 * 启用用户
	 * @return
	 */
	@RequestMapping(value = "/admin-service/sys/user/enable", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R enable(@RequestParam(value="userId") Long userId);
	
	/**
	 * 停用用户
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/admin-service/sys/user/disable", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R disable(@RequestParam(value="userId") Long userId);
	
	/**
	 * 删除单个用户
	 * @param userId
	 * @return
	 */
	@RequestMapping(value = "/admin-service/sys/user/deleteSingle", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R deleteSingle(@RequestParam(value="userId") Long userId);
	
	@RequestMapping(value = "/admin-service/sys/user/edit", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> editorUser(@RequestParam(value="userId") Long userId);
	
	@RequestMapping(value = "/admin-service/sys/user/auth", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public Map<String, Object> authUser(@RequestParam(value="userId") Long userId) ;
	
	/**
	 * 获取 用户 - 部门 
	 * @param userId
	 * @return 用户所属部门列表
	 */
	@RequestMapping(value = "/admin-service/sys/user/userDepts", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public List<Long>  getUserDeptTree(@RequestParam(value="userId") Long userId);
	
	/**
	 * 更新用户权限
	 * @param userId
	 * @param depts
	 * @param roles
	 * @return
	 */
	@RequestMapping(value = "/admin-service/sys/user/updateAuthAndRole", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R updateAuthAndRole(@RequestParam(value="userId") Long userId,@RequestParam(name="roles",required=false) String roles);
	
	@RequestMapping(value = "/admin-service/sys/user/updateCardid", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R updateCardId(@RequestParam(value="params") Map<String, Object> params);
	
	@RequestMapping(value = "/admin-service/sys/user/checkCardid", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R checkCardid(@RequestParam(value="params") Map<String, Object> params);

	@RequestMapping(value = "/admin-service/sys/user/checkPassword", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R checkPassword(@RequestParam(value="params")  Map<String, Object> params);
	
	@RequestMapping(value = "/admin-service/sys/user/getUserKanban", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getUserKanban(@RequestParam(value="params")  Map<String, Object> params);
	
	@RequestMapping(value = "/admin-service/sys/user/getUserList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getUserList(@RequestParam(value = "userName") String userName);

    @RequestMapping(value = "/admin-service/sys/user/getRoleList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getRoleList(@RequestParam(value = "roleName") String roleName);

    @RequestMapping(value = "/admin-service/sys/user/getMenuList", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getMenuList(@RequestParam(value = "menuName") String menuName);
	
	@RequestMapping(value = "/admin-service/sys/user/getUserInfo", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
	public R getUserInfo(@RequestParam(value = "params") Map<String,Object> params);
	
	/**
     * 根据工号或者用户名获取用户信息
     * @param params
     * @return
     */
    @RequestMapping(value = "/admin-service/sys/user/getUserMap", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getUserMap(@RequestBody List<Map<String, Object>> params) ;
    /**
     * 根据工号或者用户名获取用户信息,下拉使用
     * @param params
     * @return
     */
    @RequestMapping(value = "/admin-service/sys/user/getUserMapN", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R getUserMapN(@RequestParam Map<String, Object> params) ;
    
    /**
     * 根据工号或者用户名获取用户信息
     * @param params
     * @return
     */
    @RequestMapping(value = "/admin-service/sys/user/checkUserMap", method = RequestMethod.POST, produces = MediaType.APPLICATION_JSON_VALUE)
    public R checkUserMap(@RequestParam Map<String, Object> params) ;
}
