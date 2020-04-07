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

package com.byd.admin.modules.sys.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang.ArrayUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.byd.admin.common.annotation.SysLog;
import com.byd.admin.common.enums.SysUserEntityStatus;
import com.byd.admin.modules.masterdata.entity.DeptEntity;
import com.byd.admin.modules.masterdata.service.DeptService;
import com.byd.admin.modules.sys.entity.SysRoleEntity;
import com.byd.admin.modules.sys.entity.SysUserEntity;
import com.byd.admin.modules.sys.entity.SysUserRoleEntity;
import com.byd.admin.modules.sys.service.SysRoleService;
import com.byd.admin.modules.sys.service.SysUserRoleService;
import com.byd.admin.modules.sys.service.SysUserService;
import com.byd.utils.Digests;
import com.byd.utils.MD5Util;
import com.byd.utils.PageUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.utils.validator.Assert;
import com.byd.utils.validator.ValidatorUtils;
import com.byd.utils.validator.group.AddGroup;
import com.byd.utils.validator.group.UpdateGroup;

/**
 * 系统用户
 * 
 * @author cscc
 * @email 
 * @date 2016年10月31日 上午10:40:10
 */
@Controller
@RequestMapping("/sys/user")
public class SysUserController {
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private DeptService sysDeptService;
	@Autowired
	private SysRoleService sysRoleService;
	@Autowired
	private UserUtils userUtils;
	
	/**
	 * 所有用户列表
	 */
	@RequestMapping("/list")
	@ResponseBody
	public R list(@RequestParam Map<String, Object> params){
		try {
			PageUtils page = sysUserService.queryPage2(params);
			return R.ok().put("page", page);
		}catch(Exception e){
			return R.error().put("msg", "error");
		}
		
	}
	
	/**
	 * 获取登录的用户信息
	 */
	@RequestMapping("/info")
	@ResponseBody
	public R info(){
		return R.ok().put("user", userUtils.getUser());
	}
	
	/**
	 * 修改登录用户密码
	 * @throws Exception 
	 */
	@SysLog("修改密码")
	@RequestMapping("/password")
	@ResponseBody
	public R password(String password, String newPassword,String username) throws Exception{
		Assert.isBlank(newPassword, "新密码不为能空");
		System.out.println("username："+username);
		Map<String,Object> currentUser = sysUserService.getUserByUserName(username);
		String plain = MD5Util.unescapeHtml(password);
		byte[] salt;
		salt = MD5Util.decodeHex(currentUser.get("PASSWORD").toString().substring(0,16));
		byte[] hashPassword = Digests.sha1(plain.getBytes(), salt, 1024);
		password = MD5Util.encodeHex(salt)+MD5Util.encodeHex(hashPassword);
    	System.out.println("加密密码："+password);
		//原密码
		//password = MD5Util.entryptPassword(password);
		//新密码
		//newPassword = MD5Util.entryptPassword(newPassword);
    	String plain2 = MD5Util.unescapeHtml(newPassword);
    	byte[] hashPassword2 = Digests.sha1(plain2.getBytes(), salt, 1024);
    	newPassword = MD5Util.encodeHex(salt)+MD5Util.encodeHex(hashPassword2);
		//更新密码
		boolean flag = sysUserService.updatePassword(userUtils.getUserId(), password, newPassword);
		if(!flag){
			return R.error("原密码不正确");
		}
		
		return R.ok();
	}
	
	
	@SysLog("重置密码")
	@RequestMapping("/resetPwd")
	@ResponseBody
	public R resetPwd(Long userId){
		SysUserEntity user = sysUserService.selectById(userId);
		if(user == null){
			throw new IllegalAccessError("用户不存在！");
		}
		//初始化用户的密码为用户名 
		SysUserEntity userEntity = new SysUserEntity();
		userEntity.setPassword(MD5Util.entryptPassword(user.getUsername()));
		
		sysUserService.update(userEntity,new EntityWrapper<SysUserEntity>().eq("user_id", userId));
		return R.ok();
	}
	
	/**
	 * 用户信息
	 */
	@RequestMapping("/info/{userId}")
	@ResponseBody
	public R info(@PathVariable("userId") Long userId){
		SysUserEntity user = sysUserService.selectById(userId);
		
		//获取用户所属的角色列表
		List<Long> roleIdList = sysUserRoleService.queryRoleIdList(userId);
		user.setRoleIdList(roleIdList);
		
		return R.ok().put("user", user);
	}
	
	/**
	 * 保存用户
	 */
	@SysLog("用户管理-新增")
	@RequestMapping("/save")
	@ResponseBody
	public R save(@RequestBody SysUserEntity user){
		user.setPassword(MD5Util.entryptPassword(user.getUsername()));
		ValidatorUtils.validateEntity(user, AddGroup.class);
		//检查工号 不能重复
		List<SysUserEntity> userList =  sysUserService.selectList(new EntityWrapper<SysUserEntity>().eq("staff_number", user.getStaffNumber()));
		if(!CollectionUtils.isEmpty(userList)){
			return R.error("工号已经存在");
		}
		sysUserService.save(user);
		
		return R.ok();
	}
	
	/**
	 * 修改用户
	 */
	@SysLog("修改用户")
	@RequestMapping("/update")
	@ResponseBody
	public R update(@RequestBody SysUserEntity user){
		ValidatorUtils.validateEntity(user, UpdateGroup.class);

		sysUserService.updateById(user);
		
		return R.ok();
	}
	
	/**
	 * 删除用户
	 */
	@SysLog("用户管理-删除")
	@RequestMapping("/delete")
	@ResponseBody
	public R delete(@RequestBody Long[] userIds){
		if(ArrayUtils.contains(userIds, 1L)){
			return R.error("系统管理员不能删除");
		}
		
		if(ArrayUtils.contains(userIds, userUtils.getUserId())){
			return R.error("当前用户不能删除");
		}

		sysUserService.deleteBatchIds(Arrays.asList(userIds));
		
		return R.ok();
	}
	
	
	/**
	 * 启用用户
	 * @return
	 */
	@RequestMapping("/enable")
	@ResponseBody
	public R enable(Long userId){
		updateStatus(SysUserEntityStatus.ENABLED,userId);
		return R.ok();
	}
	
	/**
	 * 停用用户
	 * @param userId
	 * @return
	 */
	@RequestMapping("/disable")
	@ResponseBody
	public R disable(Long userId){
		updateStatus(SysUserEntityStatus.DISABLED,userId);
		return R.ok();
	}
	
	private void updateStatus(SysUserEntityStatus status,Long userId){
		SysUserEntity entity = new SysUserEntity();
		entity.setStatus(status.getStatus());
		sysUserService.update(entity, new EntityWrapper<SysUserEntity>().eq("user_id", userId));
	}
	
	/**
	 * 删除单个用户
	 * @param userId
	 * @return
	 */
	@SysLog("用户管理-删除")
	@RequestMapping("/deleteSingle")
	@ResponseBody
	public R deleteSingle(Long userId){
		if(userId == 1L){
			return R.error("系统管理员不能删除");
		}
		
		if(userId==userUtils.getUserId()){
			return R.error("当前用户不能删除");
		}
		sysUserService.delete(new EntityWrapper<SysUserEntity>().eq("user_id", userId));
		return R.ok();
	}
	
	@Autowired
	private DeptService deptService;
	@SysLog("用户管理-修改")
	@RequestMapping("/edit")
	@ResponseBody
	public Map<String, Object> editorUser(Long userId) throws IllegalAccessException{
		SysUserEntity user = sysUserService.selectById(userId);
		if(user == null){
			throw new IllegalAccessException("user not exist!");
		}
		if(user.getDeptId() != null){
		  DeptEntity userdept	=deptService.selectById(user.getDeptId());
		  if(userdept!=null){
			  user.setDeptName(userdept.getName());
		  }
		}
		Map<String, Object> rmap = new HashMap<String, Object>();
		rmap.put("user", user);
		return rmap;
	}
	@RequestMapping("/auth")
	@ResponseBody
	public Map<String, Object> authUser(Long userId) throws IllegalAccessException{
		SysUserEntity user = sysUserService.selectById(userId);
		if(user == null){
			throw new IllegalAccessException("user not exist!");
		}
		List<SysUserRoleEntity> userRoles = sysUserRoleService.selectList(new EntityWrapper<SysUserRoleEntity>().eq("user_id", userId));
		List<SysRoleEntity> roles = sysRoleService.selectList(new EntityWrapper<SysRoleEntity>());
		if(!CollectionUtils.isEmpty(userRoles)){
			for(SysUserRoleEntity ur:userRoles){
				for(SysRoleEntity role:roles){
					if(role.getRoleId().longValue()==ur.getRoleId().longValue()){
						role.setRemark("checked");
					}
				}
			}
		}
		
		Map<String, Object> rmap = new HashMap<String, Object>();
		rmap.put("user", user);
		rmap.put("roles", roles);
		return rmap;
	}
	
	/**
	 * 获取 用户 - 部门 
	 * @param userId
	 * @return 用户所属部门列表
	 */
	@RequestMapping("/userDepts")
	@ResponseBody
	public List<Long>  getUserDeptTree(Long userId){
		List<DeptEntity> depts = sysDeptService.selectDeptsByUserId(userId);
		List<Long> deptIds = new ArrayList<>();
		if(!CollectionUtils.isEmpty(depts)){
			for(DeptEntity dept:depts){
				deptIds.add(dept.getDeptId());
			}
		}
		return deptIds;
	}
	
	/**
	 * 更新用户权限
	 * @param userId
	 * @param depts
	 * @param roles
	 * @return
	 */
	@SysLog("用户管理-授权")
	@RequestMapping("/updateAuthAndRole")
	@ResponseBody
	public R updateAuthAndRole(Long userId,@RequestParam(name="roles",required=false) String roles) throws Exception {
		List<String> roleslit = (List<String>) JSONArray.parse(roles);
		R result=sysUserService.updateRoleAndDept(userId, roleslit);
		return result;
	}
	
	@RequestMapping("/updateCardid")
	@ResponseBody
	public R updateCardId(@RequestParam Map<String, Object> params) throws Exception{
		if(params == null){
			return R.error("参数错误");
		}
		String stafflist=params.get("staffnumber").toString();
		String[] staffnumberArray=stafflist.split(",");
		for(int i=0;i<staffnumberArray.length;i++){
			String staffnumber_str=staffnumberArray[i];
			while(staffnumber_str.length()<8){
				staffnumber_str="0".concat(staffnumber_str);
			}
			String cardid=sysUserService.queryCardInfo(staffnumber_str);//一卡通卡号
			
			SysUserEntity userbean=new SysUserEntity();
			
			Map<String, Object> mapcond=new HashMap<String, Object>();
			mapcond.put("STAFF_NUMBER", staffnumberArray[i]);
			List<SysUserEntity> sysUserList=sysUserService.selectByMap(mapcond);
			if(sysUserList.size()>0){
				userbean.setUserId(sysUserList.get(0).getUserId());
				userbean.setCardHd(cardid);
			}
			
			sysUserService.updateById(userbean);
		}
		
		return R.ok();
	}
	// 校验一卡通是否存在staff_number的数据
	@RequestMapping("/checkCardid")
	@ResponseBody
	public R checkCardid(@RequestParam Map<String, Object> params) throws Exception{
		if(params == null){
			return R.error("参数错误");
		}
		String stafflist=params.get("staffnumber").toString();
		String[] staffnumberArray=stafflist.split(",");
		for(int i=0;i<staffnumberArray.length;i++){
			String staffnumber_str=staffnumberArray[i];
			while(staffnumber_str.length()<8){
				staffnumber_str="0".concat(staffnumber_str);
			}
			String cardid=sysUserService.queryCardInfo(staffnumber_str);//一卡通卡号
			if(cardid.equals("")) {
				return R.error(staffnumber_str+":在一卡通系统不存在!");
			}
		}
		
		return R.ok();
	}
	
	@RequestMapping("/getUserKanban")
	@ResponseBody
	public R getUserKanban(@RequestParam Map<String, Object> params) {
		Map<String,Object> user = userUtils.getUser();
		params.put("USERNAME", user.get("USERNAME"));
		String WERKS = params.get("WERKS")==null?"":params.get("WERKS").toString();
		if(StringUtils.isEmpty(WERKS)) {
			//取用户权限工厂
			Set<Map<String,Object>> userAuthWerks = userUtils.getUserWerks("SYS_MAIN");
			for (Map<String, Object> map : userAuthWerks) {
				WERKS +=map.get("CODE").toString()+",";
			}
		}
		params.put("WERKS", WERKS);
		List<Map<String,Object>> userKanbanList = sysUserService.getUserKanban(params);
		
		return R.ok().put("data", userKanbanList);
	}
    
    @CrossOrigin
    @RequestMapping("/checkPassword")
    @ResponseBody
    public R checkPassword(@RequestParam Map<String,Object> params) {
  	  String username=params.get("username").toString();
        String password=params.get("password")==null?"":(String) params.get("password");
        Map<String,Object> currentUser = sysUserService.getUserByUserName(username);
        if(currentUser == null) {
        	return R.error("用户名"+username+"不存在！");
        }
        if(!MD5Util.validPassword(password, currentUser.get("PASSWORD")==null?"":currentUser.get("PASSWORD").toString())){
      		return R.error("密码不正确！");
        }
		return R.ok();
    } 
    
    @RequestMapping("/getUserList")
    @ResponseBody
    public R getUserList(@RequestParam String userName){
    	List<Map<String,Object>> list = sysUserService.getUserList(userName);
    	return R.ok().put("list", list);
    }

    @RequestMapping("/getRoleList")
    @ResponseBody
    public R getRoleList(@RequestParam String roleName){
    	List<Map<String,Object>> list = sysUserService.getRoleList(roleName);
    	return R.ok().put("list", list);
    }

    @RequestMapping("/getMenuList")
    @ResponseBody
    public R getMenuList(@RequestParam String menuName){
    	List<Map<String,Object>> list = sysUserService.getMenuList(menuName);
    	return R.ok().put("list", list);
    }
	
    /**
     * 根据工号或者CARD_HD值查询用户信息
     * @param params
     * @return
     */
    @RequestMapping("/getUserInfo")
    @ResponseBody
    public R getUserInfo(@RequestParam Map<String, Object> params) {
    	return R.ok().put("data", sysUserService.getUserInfo(params));
    }
    
    /**
     * 根据工号或者用户名获取用户信息
     * @param params
     * @return
     */
    @RequestMapping("/getUserMap")
    @ResponseBody
    public R getUserMap(@RequestParam List<String> params) {
    	return R.ok().put("data", sysUserService.getUserMap(params));
    }
    
    /**
     * 根据工号或者用户名获取用户信息,下拉使用
     * @param params
     * @return
     */
    @RequestMapping("/getUserMapN")
    @ResponseBody
    public R getUserMapN(@RequestParam Map<String, Object> params) {
    	List<String> condList=new ArrayList<String>();
    	condList.add(params.get("user").toString());
    	return R.ok().put("data", sysUserService.getUserMapNList(condList));
    }
    
    /**
     * 根据工号或者用户名获取用户信息
     * @param params
     * @return
     */
    @RequestMapping("/checkUserMap")
    @ResponseBody
    public R checkUserMap(@RequestParam Map<String, Object> params) {
    	List<String> condList=new ArrayList<String>();
    	condList.add(params.get("user").toString());
    	return R.ok().put("data", sysUserService.getUserMapList(condList));
    }
    
}
