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

package com.byd.admin.modules.sys.service.impl;


import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.modules.httpclient.entity.ClientDictCode;
import com.byd.admin.modules.httpclient.entity.HttpClientResultVO;
import com.byd.admin.modules.httpclient.entity.HttpUserMenuRefVO;
import com.byd.admin.modules.httpclient.entity.UserMenuRefListBean;
import com.byd.admin.modules.sys.dao.SysUserRoleDao;
import com.byd.admin.modules.sys.entity.SysMenuEntity;
import com.byd.admin.modules.sys.entity.SysUserEntity;
import com.byd.admin.modules.sys.entity.SysUserRoleEntity;
import com.byd.admin.modules.sys.remote.YptMenuRemote;
import com.byd.admin.modules.sys.service.SysMenuService;
import com.byd.admin.modules.sys.service.SysUserRoleService;
import com.byd.admin.modules.sys.service.SysUserService;
import com.byd.utils.MapUtils;
import com.byd.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;


/**
 * 用户与角色对应关系
 * 
 * @author cscc
 * @email 
 * @date 2016年9月18日 上午9:45:48
 */
@Service("sysUserRoleService")
public class SysUserRoleServiceImpl extends ServiceImpl<SysUserRoleDao, SysUserRoleEntity> implements SysUserRoleService {

	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private YptMenuRemote yptMenuRemote;

	@Override
	public void saveOrUpdate(Long userId, List<Long> roleIdList) {
		//先删除用户与角色关系
		this.deleteByMap(new MapUtils().put("user_id", userId));
		// 需要先删除该用户所有角色关系【新增用户没有跟角色的关系】


		if(roleIdList == null || roleIdList.isEmpty()){
			return ;
		}

		// 保存角色与用户关系【新增用户没有跟角色的关系】

		//保存用户与角色关系
		List<SysUserRoleEntity> list = new ArrayList<>(roleIdList.size());
		for(Long roleId : roleIdList){
			SysUserRoleEntity sysUserRoleEntity = new SysUserRoleEntity();
			sysUserRoleEntity.setUserId(userId);
			sysUserRoleEntity.setRoleId(roleId);

			list.add(sysUserRoleEntity);
		}
		this.insertBatch(list);
	}

	@Override
	public List<Long> queryRoleIdList(Long userId) {
		return baseMapper.queryRoleIdList(userId);
	}

	@Override
	public List<SysUserRoleEntity> queryUserRoleList(String userName,String menuKey) {
		List<SysUserRoleEntity> userRoleEntities= baseMapper.queryUserRoleList(userName,menuKey);
		return userRoleEntities.isEmpty()?null:userRoleEntities;
	}

	@Override
	public List<SysUserRoleEntity> queryUserRoleListByRids(String userName,String menuKey,List<Long> roleIds) {
		List<SysUserRoleEntity> userRoleEntities= baseMapper.queryUserRoleListByRids(userName,menuKey,roleIds);
		return userRoleEntities.isEmpty()?null:userRoleEntities;
	}

	@Override
	public int deleteBatch(Long[] roleIds){
		return baseMapper.deleteBatch(roleIds);
	}

	/**
	 * 角色管理：角色授权用户
	 * @param roleId
	 * @param userIdList
	 * @return
	 */
	@Override
	@Transactional(rollbackFor=Exception.class)
	public R saveRoleUser(Long roleId, Long[] userIdList) throws Exception {

		//bug:1598,根据角色查出该角色下面的所有菜单
		List<SysMenuEntity> menuEntityList=sysMenuService.queryMenuListByRoleId(roleId);
		// 云平台接口，需要先删除该角色所有用户关系(除去当前分配了的用户)【确认】
		//先根据角色id,除去当前分配了的用户，就是需要删除的,查询出之前所有的与该角色相关的用户
		List<SysUserEntity> oldNdeleteUserInfos=sysUserService.getUserListByRoleIdUids(roleId, Arrays.asList(userIdList));
		HttpUserMenuRefVO userMenuRefDeleteVO=new HttpUserMenuRefVO();
		userMenuRefDeleteVO.setSynType("DELETE");
		List<UserMenuRefListBean> userMenuRefDelList =new ArrayList<>();
		UserMenuRefListBean userMenuRefListDelBean=null;
		List<SysUserRoleEntity> sysUserRoleEntities=new ArrayList<>();
		if((oldNdeleteUserInfos != null || oldNdeleteUserInfos.size()>0) && (menuEntityList != null && menuEntityList.size()>0)) {
			for (SysUserEntity userEntity : oldNdeleteUserInfos) {
				for (SysMenuEntity menuEntity : menuEntityList) {
					sysUserRoleEntities = baseMapper.queryUserRoleList(userEntity.getUsername(), menuEntity.getMenuKey());
					if (sysUserRoleEntities == null || sysUserRoleEntities.size() <= 1) {//当该用户有0或者一个角色有这个菜单，云平台那里才能删除该用户的该菜单。					userMenuRefListDelBean = new UserMenuRefListBean();
						userMenuRefListDelBean = new UserMenuRefListBean();
						userMenuRefListDelBean.setMenuCode(menuEntity.getMenuKey());
						userMenuRefListDelBean.setStaffAccount(userEntity.getUsername());//应云平台要求改为用户账号
						userMenuRefListDelBean.setSystemCode(ClientDictCode.WMSTOYPT_SYSTEM_CODE);
						userMenuRefDelList.add(userMenuRefListDelBean);
					}
				}
			}
		}
		userMenuRefDeleteVO.setUserMenuRefList(userMenuRefDelList);
		//删除同步云平台
		HttpClientResultVO delResult = new HttpClientResultVO(2);//0成功，1失败，2不走接口
		if(!userMenuRefDelList.isEmpty()) {
			delResult = requestYpt(ClientDictCode.HTTPCLIENT_WMSTOYPT_IPPORT + ClientDictCode.HTTPCLIENT_WMSTOYPT_USERMENUURL, JSON.toJSONString(userMenuRefDeleteVO));
			if (delResult.getErrCode() == 1) {//如果传云平台出现错误
				return R.error("云平台:" + delResult.getErrMsg());
			}
		}
		//先删除用户与角色关系
		this.deleteByMap(new MapUtils().put("role_id", roleId));

		if(userIdList == null || userIdList.length == 0){
			return R.error("授权的用户信息为空！");
		}

		//保存用户与角色关系
		List<SysUserRoleEntity> list = new ArrayList<>(userIdList.length);
		List<SysUserEntity> sysUserEntityList=new ArrayList<>();
		SysUserEntity userInfo=null;
		for(Long userId : userIdList){
			SysUserRoleEntity sysUserRoleEntity = new SysUserRoleEntity();
			sysUserRoleEntity.setUserId(userId);
			sysUserRoleEntity.setRoleId(roleId);

			list.add(sysUserRoleEntity);
			userInfo=sysUserService.selectById(userId);
			sysUserEntityList.add(userInfo);
		}

		// bug:1598,云平台接口，保存角色与用户关系【确认】
		HttpUserMenuRefVO userMenuRefAddVO=new HttpUserMenuRefVO();
		userMenuRefAddVO.setSynType("ADD");
		List<UserMenuRefListBean> userMenuRefAddList =new ArrayList<>();
		UserMenuRefListBean userMenuRefAddListBean=null;
		if((sysUserEntityList != null || sysUserEntityList.size()>0) && (menuEntityList != null && menuEntityList.size()>0)) {
			for (SysUserEntity userEntity : sysUserEntityList) {
				for (SysMenuEntity menuEntity : menuEntityList) {
					if (menuEntity.getMenuId() != 0 && menuEntity.getType() == 1) {//不需要传云平台模块和“所有菜单”
						userMenuRefAddListBean = new UserMenuRefListBean();
						userMenuRefAddListBean.setMenuCode(menuEntity.getMenuKey());
						userMenuRefAddListBean.setStaffAccount(userEntity.getUsername());
						userMenuRefAddListBean.setSystemCode(ClientDictCode.WMSTOYPT_SYSTEM_CODE);
						userMenuRefAddList.add(userMenuRefAddListBean);
					}
				}
			}
		}
		userMenuRefAddVO.setUserMenuRefList(userMenuRefAddList);
		//新增同步云平台
		HttpClientResultVO addResult = new HttpClientResultVO(2);
		if(!userMenuRefAddList.isEmpty()) {
			addResult = requestYpt(ClientDictCode.HTTPCLIENT_WMSTOYPT_IPPORT + ClientDictCode.HTTPCLIENT_WMSTOYPT_USERMENUURL, JSON.toJSONString(userMenuRefAddVO));
		}
		if (addResult.getErrCode() == 1) {//如果传云平台出现错误
			if (delResult != null && delResult.getErrCode() == 0) {//如果之前的删除方法成功，需要回滚操作。
				userMenuRefDeleteVO.setSynType("ADD");
				HttpClientResultVO delAddResult = requestYpt(ClientDictCode.HTTPCLIENT_WMSTOYPT_IPPORT + ClientDictCode.HTTPCLIENT_WMSTOYPT_USERMENUURL, JSON.toJSONString(userMenuRefDeleteVO));
//				if (delAddResult.getErrCode() == 0) {
					throw new Exception("与云平台交互发生异常！");//回滚WMS之前删除的数据,保持与云平台一致。
//				}
			}
			return R.error("云平台:" + addResult.getErrMsg());
		}else {
			if(!list.isEmpty()) {
				this.insertBatch(list);
			}
			return R.ok();
		}
	}

	/**
	 * @author rain
	 * @param url
	 * @param jsonStr
	 * @return
	 * @throws Exception
	 * @description 统一请求云平台方法
	 */
	public HttpClientResultVO requestYpt(String url, String jsonStr) throws Exception {
		Map<String,Object> paramMap =new HashMap<>();
		paramMap.put("url",url);
		paramMap.put("jsonStr",jsonStr);
		return yptMenuRemote.doPostWithJsonStr(paramMap);
	}

	@Override
	public List<Long> queryUserIdList(Long roleId) {
		return baseMapper.queryUserIdList(roleId);
	}
	
	
}
