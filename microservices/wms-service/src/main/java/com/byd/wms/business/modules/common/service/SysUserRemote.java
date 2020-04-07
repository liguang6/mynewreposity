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

package com.byd.wms.business.modules.common.service;

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
    public R getUserMap(@RequestParam(value = "params") List<String> params) ;
}
