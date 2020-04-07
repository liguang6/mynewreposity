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
import com.byd.admin.modules.sys.dao.SysRoleMenuDao;
import com.byd.admin.modules.sys.entity.SysMenuEntity;
import com.byd.admin.modules.sys.entity.SysRoleMenuEntity;
import com.byd.admin.modules.sys.entity.SysUserEntity;
import com.byd.admin.modules.sys.entity.SysUserRoleEntity;
import com.byd.admin.modules.sys.remote.YptMenuRemote;
import com.byd.admin.modules.sys.service.SysMenuService;
import com.byd.admin.modules.sys.service.SysRoleMenuService;
import com.byd.admin.modules.sys.service.SysUserRoleService;
import com.byd.admin.modules.sys.service.SysUserService;
import com.byd.utils.R;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * 角色与菜单对应关系
 * 
 * @author cscc
 * @email 
 * @date 2016年9月18日 上午9:44:35
 */
@Service("sysRoleMenuService")
public class SysRoleMenuServiceImpl extends ServiceImpl<SysRoleMenuDao, SysRoleMenuEntity> implements SysRoleMenuService {

	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private YptMenuRemote yptMenuRemote;
	@Autowired
	private SysUserRoleService sysUserRoleService;

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void addRoleSave(Long roleId, List<Long> menuIdList) {
		//先删除角色与菜单关系
		deleteBatch(new Long[]{roleId});

		if(menuIdList.size() == 0){
			return ;
		}

		//保存角色与菜单关系
		List<SysRoleMenuEntity> list = new ArrayList<>(menuIdList.size());
		for(Long menuId : menuIdList){
			SysRoleMenuEntity sysRoleMenuEntity = new SysRoleMenuEntity();
			sysRoleMenuEntity.setMenuId(menuId);
			sysRoleMenuEntity.setRoleId(roleId);

			list.add(sysRoleMenuEntity);
		}
		this.insertBatch(list);
	}

	/**
	 * @author rain
	 * @date 2019年11月21日10:25:55
	 * @description 修改角色会更改角色的菜单信息，需要同步云平台。
	 * @param roleId
	 * @param menuIdList
	 * @return
	 * @throws Exception
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public R updateRoleSave(Long roleId, List<Long> menuIdList) throws Exception {

		//bug:1598,根据角色查出该角色下面在数据库的所有菜单(除去目前已经分配的就是需要删除的)
		List<SysMenuEntity> oldNdeleteMenuEntityList=sysMenuService.queryMenuListByRoleIdMenu(roleId,menuIdList);
		//先根据角色id，查询出之前所有的与该角色相关的用户
		List<SysUserEntity> oldUserInfos=sysUserService.getUserListByRoleId(roleId);
		HttpUserMenuRefVO userMenuRefDeleteVO=new HttpUserMenuRefVO();
		userMenuRefDeleteVO.setSynType("DELETE");
		List<UserMenuRefListBean> userMenuRefDelList =new ArrayList<>();
		UserMenuRefListBean userMenuRefListDelBean=null;
		List<SysUserRoleEntity> sysUserRoleEntities=new ArrayList<>();
		if((oldUserInfos != null || oldUserInfos.size()>0) && (oldNdeleteMenuEntityList != null && oldNdeleteMenuEntityList.size()>0)) {
			for (SysUserEntity userEntity : oldUserInfos) {
				for (SysMenuEntity menuEntity : oldNdeleteMenuEntityList) {
					sysUserRoleEntities = sysUserRoleService.queryUserRoleList(userEntity.getUsername(), menuEntity.getMenuKey());
					if (sysUserRoleEntities == null || sysUserRoleEntities.size() <= 1) {//当该用户有0或者一个角色有这个菜单，云平台那里才能删除该用户的该菜单。
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
		//先删除角色与菜单关系
		deleteBatch(new Long[]{roleId});

		if(menuIdList.size() == 0){
			return R.ok();
		}

		//保存角色与菜单关系
		List<SysRoleMenuEntity> list = new ArrayList<>(menuIdList.size());
		List<SysMenuEntity> sysMenuEntityList=new ArrayList<>();
		SysMenuEntity sysMenuEntity=null;
		for(Long menuId : menuIdList){
			SysRoleMenuEntity sysRoleMenuEntity = new SysRoleMenuEntity();
			sysRoleMenuEntity.setMenuId(menuId);
			sysRoleMenuEntity.setRoleId(roleId);

			list.add(sysRoleMenuEntity);
			sysMenuEntity=sysMenuService.selectById(menuId);
			sysMenuEntityList.add(sysMenuEntity);
		}
		// bug:1598,云平台接口，保存角色与菜单关系信息
		HttpUserMenuRefVO userMenuRefAddVO=new HttpUserMenuRefVO();
		userMenuRefAddVO.setSynType("ADD");
		List<UserMenuRefListBean> userMenuRefAddList =new ArrayList<>();
		UserMenuRefListBean userMenuRefAddListBean=null;
		if((oldUserInfos != null || oldUserInfos.size()>0) && (sysMenuEntityList != null && sysMenuEntityList.size()>0)) {
			for (SysUserEntity userEntity : oldUserInfos) {
				for (SysMenuEntity menuEntity : sysMenuEntityList) {
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
	public List<Long> queryMenuIdList(Long roleId) {
		return baseMapper.queryMenuIdList(roleId);
	}

	@Override
	public int deleteBatch(Long[] roleIds){
		return baseMapper.deleteBatch(roleIds);
	}

}
