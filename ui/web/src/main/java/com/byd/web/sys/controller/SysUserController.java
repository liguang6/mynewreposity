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

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.alibaba.fastjson.JSONArray;
import com.byd.utils.DateUtils;
import com.byd.utils.ExcelReader;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.web.sys.entity.SysUserEntity;
import com.byd.web.sys.service.SysUserRemote;

/**
 * 系统用户
 * 
 * @author cscc
 * @email 
 * @date 2016年10月31日 上午10:40:10
 */
@Controller
@RequestMapping("/sys/user")
public class SysUserController{
	@Autowired
	private SysUserRemote sysUserRemote;
	@Autowired
	private UserUtils userUtils;
	
	/**
	 * 所有用户列表
	 */
	@RequestMapping("/list")
	@ResponseBody
	public R list(@RequestParam Map<String, Object> params){
		return sysUserRemote.list(params);
	}
	
	/**
	 * 获取登录的用户信息
	 */
	@RequestMapping("/info")
	@ResponseBody
	public R info(){
		return sysUserRemote.info();
	}
	
	/**
	 * 修改登录用户密码
	 */
	@RequestMapping("/password")
	@ResponseBody
	public R password(String password, String newPassword,String username){
		return sysUserRemote.password(password, newPassword,username);
	}
	
	
	@RequestMapping("/resetPwd")
	@ResponseBody
	public R resetPwd(Long userId){
		return sysUserRemote.resetPwd(userId);
	}
	
	/**
	 * 用户信息
	 */
	@RequestMapping("/info/{userId}")
	@ResponseBody
	public R info(@PathVariable("userId") Long userId){
		return sysUserRemote.info(userId);
	}
	
	/**
	 * 保存用户
	 */
	@RequestMapping("/save")
	@ResponseBody
	public R save(@RequestBody SysUserEntity user){
		return sysUserRemote.save(user);
	}
	
	/**
	 * 修改用户
	 */
	@RequestMapping("/update")
	@ResponseBody
	public R update(@RequestBody SysUserEntity user){
		return sysUserRemote.update(user);
	}
	
	/**
	 * 删除用户
	 */
	@RequestMapping("/delete")
	@ResponseBody
	public R delete(@RequestBody Long[] userIds){
		return sysUserRemote.delete(userIds);
	}
	
	
	/**
	 * 启用用户
	 * @return
	 */
	@RequestMapping("/enable")
	@ResponseBody
	public R enable(Long userId){
		return sysUserRemote.enable(userId);
	}
	
	/**
	 * 停用用户
	 * @param userId
	 * @return
	 */
	@RequestMapping("/disable")
	@ResponseBody
	public R disable(Long userId){
		return sysUserRemote.disable(userId);
	}
	
	/**
	 * 删除单个用户
	 * @param userId
	 * @return
	 */
	@RequestMapping("/deleteSingle")
	@ResponseBody
	public R deleteSingle(Long userId){
		return sysUserRemote.deleteSingle(userId);
	}
	
	@RequestMapping("/import")
	@ResponseBody
	public R importFromExcel(MultipartFile excel) throws IOException{
		List<String[]> sheet =  ExcelReader.readExcel(excel);
		//transfer & load
		for(String[] row:sheet){
			String username = row[0];
			String staffNumber = row[1];
			String password = row[2];
			String fullName = row[3];
			String email = row[4];
			String identityCard = row[5];
			String mobile = row[6];
			
			SysUserEntity user = new SysUserEntity();
			Map<String,Object> currentUser = userUtils.getUser();
			user.setCreateBy(currentUser.get("USERNAME")==null?"":currentUser.get("USERNAME").toString());
			user.setUpdateBy(currentUser.get("USERNAME")==null?"":currentUser.get("USERNAME").toString());
			user.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			user.setUpdateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
			user.setUsername(username);
			user.setPassword(password);
			user.setStaffNumber(staffNumber);
			user.setFullName(fullName);
			user.setIdentityCard(identityCard);
			user.setEmail(email);
			user.setMobile(mobile);
			
			sysUserRemote.save(user);
		}
		return R.ok();
	}
	
	@RequestMapping("/edit")
	public String editorUser(Long userId,Model model) throws IllegalAccessException{
		Map<String, Object> rmap = sysUserRemote.editorUser(userId);
		model.addAttribute("user", rmap.get("user"));
		return "/sys/editor_user_1";
	}
	
	@RequestMapping("/auth")
	public String authUser(Long userId,Model model) throws IllegalAccessException{

		Map<String, Object> rmap = sysUserRemote.authUser(userId);
		model.addAttribute("user", rmap.get("user"));
		model.addAttribute("roles", rmap.get("roles"));
		return "/sys/user/auth";
	}
	
	/**
	 * 获取 用户 - 部门 
	 * @param userId
	 * @return 用户所属部门列表
	 */
	@RequestMapping("/userDepts")
	@ResponseBody
	public List<Long>  getUserDeptTree(Long userId){
		return sysUserRemote.getUserDeptTree(userId);
	}
	
	/**
	 * 更新用户权限
	 * @param userId
	 * @param depts
	 * @param roles
	 * @return
	 */
	@RequestMapping("/updateAuthAndRole")
	@ResponseBody
	public R updateAuthAndRole(Long userId,@RequestParam String roles){
		return sysUserRemote.updateAuthAndRole(userId, roles);
	}
	
	@RequestMapping("/updateCardid")
	@ResponseBody
	public R updateCardId(@RequestParam Map<String, Object> params) {
		return sysUserRemote.updateCardId(params);
	}
	@RequestMapping("/checkCardid")
	@ResponseBody
	public R checkCardId(@RequestParam Map<String, Object> params) {
		return sysUserRemote.checkCardid(params);
	}
	
	@RequestMapping("/getUserKanban")
	@ResponseBody
	public R getUserKanban(@RequestParam Map<String, Object> params) {
		return sysUserRemote.getUserKanban(params);
	}
	
	@RequestMapping("/getUserAuthWerks")
	@ResponseBody
	public R getUserAuthWerks(@RequestParam String MENU_KEY) {
		Set<Map<String,Object>> userAuthWerksSet = userUtils.getUserWerks(MENU_KEY);
		return R.ok().put("data", userAuthWerksSet);
	}
	
	@RequestMapping("/getUserAuthWh")
	@ResponseBody
	public R getUserAuthWh(@RequestParam String MENU_KEY) {
		Set<Map<String,Object>> userAuthWhSet = userUtils.getUserWh(MENU_KEY);
		return R.ok().put("data", userAuthWhSet);
	}
	
    @RequestMapping("/getUserList")
    @ResponseBody
    public R getUserList(@RequestParam String userName){
    	return sysUserRemote.getUserList(userName);
    }

    @RequestMapping("/getRoleList")
    @ResponseBody
    public R getRoleList(@RequestParam String roleName){
    	return sysUserRemote.getRoleList(roleName);
    }

    @RequestMapping("/getMenuList")
    @ResponseBody
    public R getMenuList(@RequestParam String menuName){
        return sysUserRemote.getMenuList(menuName);
    }
	
    @RequestMapping("/getUserInfo")
    @ResponseBody
    public R getUserInfo(@RequestParam Map<String,Object> params) {
    	return sysUserRemote.getUserInfo(params);
    }
    
    @RequestMapping("/getUserMap")
    @ResponseBody
    public R getUserMap(@RequestParam(value = "user") String user) {
    	Map<String, Object> userMap=new HashMap<String, Object>();
    	userMap.put("user", user);
    	return sysUserRemote.getUserMapN(userMap);
    }
    
    @RequestMapping("/checkUserMap")
    @ResponseBody
    public R checkUserMap(@RequestParam(value = "user") String user) {
    	Map<String, Object> userMap=new HashMap<String, Object>();
    	userMap.put("user", user);
    	return sysUserRemote.checkUserMap(userMap);
    }
	
}
