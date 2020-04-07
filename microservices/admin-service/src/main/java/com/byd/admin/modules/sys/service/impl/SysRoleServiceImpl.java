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

import java.util.*;

import com.alibaba.fastjson.JSON;
import com.byd.admin.modules.httpclient.entity.ClientDictCode;
import com.byd.admin.modules.httpclient.entity.HttpClientResultVO;
import com.byd.admin.modules.httpclient.entity.HttpUserMenuRefVO;
import com.byd.admin.modules.httpclient.entity.UserMenuRefListBean;
import com.byd.admin.modules.sys.entity.SysMenuEntity;
import com.byd.admin.modules.sys.entity.SysUserEntity;
import com.byd.admin.modules.sys.entity.SysUserRoleEntity;
import com.byd.admin.modules.sys.remote.YptMenuRemote;
import com.byd.admin.modules.sys.service.*;
import com.byd.utils.R;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.modules.masterdata.service.DeptService;
import com.byd.admin.modules.sys.dao.SysRoleDao;
import com.byd.admin.modules.sys.entity.SysRoleEntity;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;


/**
 * 角色service
 * @author develop03
 *
 */
@Service("sysRoleService")
public class SysRoleServiceImpl extends ServiceImpl<SysRoleDao, SysRoleEntity> implements SysRoleService {
	@Autowired
	private SysRoleMenuService sysRoleMenuService;
	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private DeptService sysDeptService;

	@Autowired
	private SysRoleDao sysRoleDao;
	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private YptMenuRemote yptMenuRemote;

	@Override
	public PageUtils queryAlls(Map<String, Object> params) {
		if(params.get("flag") == null || !params.get("flag") .equals("true")){
			params.put("pageSize",String.valueOf(Integer.MAX_VALUE));
		}
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 6000;
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}
		params.put("start", start);params.put("end", end);
		List<Map<String,Object>> records = sysRoleDao.queryRoleList(params);
		System.err.println(records.toString());
		if(records.size()==0){
			pageNo = "1";
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
			params.put("start", start);params.put("end", end);
			records = sysRoleDao.queryRoleList(params);
		}
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(records);
		page.setTotal(sysRoleDao.queryRoleCount(params));
		page.setSize(Integer.valueOf(pageSize));
		if(params.get("flag") == null || !params.get("flag") .equals("true")){
			page.setSize(Integer.MAX_VALUE);
		}
		return new PageUtils(page);
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String roleName = (String)params.get("roleName");
		if(params.get("flag") != null && params.get("flag") .equals("x")){
			params.put("pageSize",String.valueOf(Integer.MAX_VALUE));
		}
		Page<SysRoleEntity> page = this.selectPage(
			new Query<SysRoleEntity>(params).getPage(),
			new EntityWrapper<SysRoleEntity>()
				.like(StringUtils.isNotBlank(roleName),"role_name", roleName)
				//.addFilterIfNeed(params.get(Constant.SQL_FILTER) != null, (String)params.get(Constant.SQL_FILTER))
		);

		return new PageUtils(page);
	}

	/**
	 * 新增角色信息
	 * @param role
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(SysRoleEntity role) {
		role.setCreateTime(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		this.insert(role);

		//保存角色与菜单关系
		sysRoleMenuService.addRoleSave(role.getRoleId(), role.getMenuIdList());

		//保存角色与部门关系
		//sysRoleDeptService.saveOrUpdate(role.getRoleId(), role.getDeptIdList());
	}

	/**
	 * 修改角色信息
	 * @param role
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public R update(SysRoleEntity role) throws Exception {
		this.updateAllColumnById(role);
		//更新角色与菜单关系
		return sysRoleMenuService.updateRoleSave(role.getRoleId(), role.getMenuIdList());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public R deleteBatch(Long[] roleIds) throws Exception {

		ArrayList< Long> needDelRoleIds = new ArrayList<Long>(roleIds.length);
		Collections.addAll(needDelRoleIds, roleIds);

		HttpUserMenuRefVO userMenuRefDeleteVO = new HttpUserMenuRefVO();
		userMenuRefDeleteVO.setSynType("DELETE");
		List<UserMenuRefListBean> userMenuRefDelList = new ArrayList<>();
		for(Long roleId:roleIds) {
			//bug:1598,根据角色查出该角色下面的所有菜单
			List<SysMenuEntity> oldMenuEntityList = sysMenuService.queryMenuListByRoleId(roleId);
			//先根据角色id，查询出之前所有的与该角色相关的用户
			List<SysUserEntity> oldUserInfos = sysUserService.getUserListByRoleId(roleId);
			UserMenuRefListBean userMenuRefListDelBean = null;
			List<SysUserRoleEntity> sysUserRoleEntities=new ArrayList<>();
			if((oldUserInfos != null || oldUserInfos.size()>0) && (oldMenuEntityList != null && oldMenuEntityList.size()>0)) {
				for (SysUserEntity userEntity : oldUserInfos) {
					for (SysMenuEntity menuEntity : oldMenuEntityList) {
						sysUserRoleEntities = sysUserRoleService.queryUserRoleListByRids(userEntity.getUsername(), menuEntity.getMenuKey(), needDelRoleIds);
						if (sysUserRoleEntities == null || sysUserRoleEntities.isEmpty()) {//当该用户有除去这些需要删除的角色，还有角色对应这个菜单，云平台那里不能删除该用户的该菜单。
							userMenuRefListDelBean = new UserMenuRefListBean();
							userMenuRefListDelBean.setMenuCode(menuEntity.getMenuKey());
							userMenuRefListDelBean.setStaffAccount(userEntity.getUsername());//应云平台要求改为用户账号
							userMenuRefListDelBean.setSystemCode(ClientDictCode.WMSTOYPT_SYSTEM_CODE);
							userMenuRefDelList.add(userMenuRefListDelBean);
						}
					}
				}
			}
		}
			userMenuRefDeleteVO.setUserMenuRefList(userMenuRefDelList);
			//删除同步云平台
			HttpClientResultVO delResult = new HttpClientResultVO(2);//0成功，1失败，2不走接口
			if (!userMenuRefDelList.isEmpty()) {
				delResult = requestYpt(ClientDictCode.HTTPCLIENT_WMSTOYPT_IPPORT + ClientDictCode.HTTPCLIENT_WMSTOYPT_USERMENUURL, JSON.toJSONString(userMenuRefDeleteVO));
				if (delResult.getErrCode() == 1) {//如果传云平台出现错误
					return R.error("云平台:" + delResult.getErrMsg());
				}
			}

		//删除角色
		this.deleteBatchIds(Arrays.asList(roleIds));
		//删除角色与菜单关联
		sysRoleMenuService.deleteBatch(roleIds);
		
		//删除角色数据权限
		sysRoleDao.deleteRoleAuth(roleIds);

		//删除角色与用户关联
		sysUserRoleService.deleteBatch(roleIds);
		return R.ok();
	}

	@Override
	public R disableRole(long roleId) throws Exception {

		HttpUserMenuRefVO userMenuRefDeleteVO = new HttpUserMenuRefVO();
		userMenuRefDeleteVO.setSynType("DELETE");
		List<UserMenuRefListBean> userMenuRefDelList = new ArrayList<>();
			//bug:1598,根据角色查出该角色下面的所有菜单
			List<SysMenuEntity> oldMenuEntityList = sysMenuService.queryMenuListByRoleId(roleId);
			//先根据角色id，查询出之前所有的与该角色相关的用户
			List<SysUserEntity> oldUserInfos = sysUserService.getUserListByRoleId(roleId);
			UserMenuRefListBean userMenuRefListDelBean = null;
			List<SysUserRoleEntity> sysUserRoleEntities=new ArrayList<>();
		if((oldUserInfos != null || oldUserInfos.size()>0) && (oldMenuEntityList != null && oldMenuEntityList.size()>0)) {
			for (SysUserEntity userEntity : oldUserInfos) {
				for (SysMenuEntity menuEntity : oldMenuEntityList) {
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
		if (!userMenuRefDelList.isEmpty()) {
			delResult = requestYpt(ClientDictCode.HTTPCLIENT_WMSTOYPT_IPPORT + ClientDictCode.HTTPCLIENT_WMSTOYPT_USERMENUURL, JSON.toJSONString(userMenuRefDeleteVO));
			if (delResult.getErrCode() == 1) {//如果传云平台出现错误
				return R.error("云平台:" + delResult.getErrMsg());
			}
		}

		SysRoleEntity role=new SysRoleEntity();
		role.setRoleId(roleId);
		role.setStatus("2");
		baseMapper.updateById(role);
		return R.ok();
	}

	@Override
	public R enableRole(long roleId) throws Exception {

		HttpUserMenuRefVO userMenuRefAddVO=new HttpUserMenuRefVO();
		userMenuRefAddVO.setSynType("ADD");
		List<UserMenuRefListBean> userMenuRefAddList =new ArrayList<>();
		//bug:1598,根据角色查出该角色下面的所有菜单
		List<SysMenuEntity> oldMenuEntityList = sysMenuService.queryMenuListByRoleId(roleId);
		//先根据角色id，查询出之前所有的与该角色相关的用户
		List<SysUserEntity> oldUserInfos = sysUserService.getUserListByRoleId(roleId);
		UserMenuRefListBean userMenuRefListDelBean = null;
		if((oldUserInfos != null || oldUserInfos.size()>0) && (oldMenuEntityList != null && oldMenuEntityList.size()>0)) {
			for (SysUserEntity userEntity : oldUserInfos) {
				for (SysMenuEntity menuEntity : oldMenuEntityList) {
					if (menuEntity.getMenuId() != 0 && menuEntity.getType() == 1) {//不需要传云平台模块和“所有菜单”
						userMenuRefListDelBean = new UserMenuRefListBean();
						userMenuRefListDelBean.setMenuCode(menuEntity.getMenuKey());
						userMenuRefListDelBean.setStaffAccount(userEntity.getUsername());//应云平台要求改为用户账号
						userMenuRefListDelBean.setSystemCode(ClientDictCode.WMSTOYPT_SYSTEM_CODE);
						userMenuRefAddList.add(userMenuRefListDelBean);
					}
				}
			}
		}
		userMenuRefAddVO.setUserMenuRefList(userMenuRefAddList);
		//新增同步云平台
		HttpClientResultVO addResult = new HttpClientResultVO(2);//0成功，1失败，2不走接口
		if (!userMenuRefAddList.isEmpty()) {
			addResult = requestYpt(ClientDictCode.HTTPCLIENT_WMSTOYPT_IPPORT + ClientDictCode.HTTPCLIENT_WMSTOYPT_USERMENUURL, JSON.toJSONString(userMenuRefAddVO));
			if (addResult.getErrCode() == 1) {//如果传云平台出现错误
				return R.error("云平台:" + addResult.getErrMsg());
			}
		}

		SysRoleEntity role=new SysRoleEntity();
		role.setRoleId(roleId);
		role.setStatus("0");
		baseMapper.updateById(role);

		return R.ok();
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
	public R updateRoleUsers(Long[] userIds, long roleId) throws Exception {
		return sysUserRoleService.saveRoleUser(roleId, userIds);
	}


}
