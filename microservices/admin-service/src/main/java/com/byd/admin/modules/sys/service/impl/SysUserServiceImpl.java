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
import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.admin.common.enums.CardConfigConstant;
import com.byd.admin.modules.httpclient.entity.ClientDictCode;
import com.byd.admin.modules.httpclient.entity.HttpClientResultVO;
import com.byd.admin.modules.httpclient.entity.HttpUserMenuRefVO;
import com.byd.admin.modules.httpclient.entity.UserMenuRefListBean;
import com.byd.admin.modules.masterdata.entity.DeptEntity;
import com.byd.admin.modules.masterdata.service.DeptService;
import com.byd.admin.modules.sys.dao.SysUserDao;
import com.byd.admin.modules.sys.entity.SysMenuEntity;
import com.byd.admin.modules.sys.entity.SysUserDeptEntity;
import com.byd.admin.modules.sys.entity.SysUserEntity;
import com.byd.admin.modules.sys.entity.SysUserRoleEntity;
import com.byd.admin.modules.sys.remote.YptMenuRemote;
import com.byd.admin.modules.sys.service.SysMenuService;
import com.byd.admin.modules.sys.service.SysUserDeptService;
import com.byd.admin.modules.sys.service.SysUserRoleService;
import com.byd.admin.modules.sys.service.SysUserService;
import com.byd.utils.*;
import org.apache.commons.lang.RandomStringUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.*;


/**
 * 系统用户
 * 
 * @author cscc
 * @email 
 * @date 2016年9月18日 上午9:46:09
 */
@Service("sysUserService")
public class SysUserServiceImpl extends ServiceImpl<SysUserDao, SysUserEntity> implements SysUserService {
	@Autowired
	private CardConfigConstant cardconfigConstant;//从spring文件取配置属性
	@Autowired
	private SysUserService sysUserService;
	@Autowired
	private SysUserRoleService sysUserRoleService;
	@Autowired
	private DeptService sysDeptService;
	@Autowired
	private SysUserDeptService sysUserDeptService;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private YptMenuRemote yptMenuRemote;
	
	

	@Override
	public List<Long> queryAllMenuId(Long userId) {
		return baseMapper.queryAllMenuId(userId);
	}
	
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String username = (String)params.get("username");
		String deptId = (String)params.get("deptId");
		String email = (String)params.get("email");
		String mobile = (String)params.get("mobile");
		String staffNumber = (String)params.get("staffNumber");
		Integer status = StringUtils.isNotBlank((String)params.get("status"))?Integer.valueOf((String)params.get("status")):null;
		
		Long deptL = StringUtils.isNotBlank(deptId)?Long.valueOf(deptId):null;
		Page<SysUserEntity> page = this.selectPage(
			new Query<SysUserEntity>(params).getPage(),
			new EntityWrapper<SysUserEntity>()
				.like(StringUtils.isNotBlank(username),"username", username)
				.eq(deptL!=null,"dept_id", deptL)
				.like(StringUtils.isNotBlank(email), "email",email)
				.like(StringUtils.isNotBlank(mobile), "mobile",mobile)
				.eq(status!=null,"status",status)
				.eq(StringUtils.isNotBlank(staffNumber), "staff_number",staffNumber)
				//.addFilterIfNeed(params.get(Constant.SQL_FILTER) != null, (String)params.get(Constant.SQL_FILTER))
		);

		for(SysUserEntity sysUserEntity : page.getRecords()){
			DeptEntity sysDeptEntity = sysDeptService.selectById(sysUserEntity.getDeptId());
			sysUserEntity.setDeptName(sysDeptEntity!=null?sysDeptEntity.getName():"");
		}

		return new PageUtils(page);
	}

	@Override
	public PageUtils queryPage2(Map<String, Object> params) {
		String username = params.get("username")==null?(params.get("username1")==null?"":params.get("username1").toString()):params.get("username").toString();
		System.out.println(username);
		String deptId = (String)params.get("deptId");
		String email = (String)params.get("email");
		String mobile = (String)params.get("mobile");
		String staffNumber = (String)params.get("staffNumber");
		Integer status = StringUtils.isNotBlank((String)params.get("status"))?Integer.valueOf((String)params.get("status")):null;
		Map<String,Object> currentUser = userUtils.getUser();
		
		Long deptL = StringUtils.isNotBlank(deptId)?Long.valueOf(deptId):null;
		Page<SysUserEntity> page = this.selectPage(
			new Query<SysUserEntity>(params).getPage(),
			new EntityWrapper<SysUserEntity>()
				.like(StringUtils.isNotBlank(username),"username", username).or().like(StringUtils.isNotBlank(username), "full_name", username)
				.eq(deptL!=null,"dept_id", deptL)
				.like(StringUtils.isNotBlank(email), "email",email)
				.like(StringUtils.isNotBlank(mobile), "mobile",mobile)
				.eq(status!=null,"status",status)
				.eq(StringUtils.isNotBlank(staffNumber), "staff_number",staffNumber)
				//.addFilterIfNeed(params.get(Constant.SQL_FILTER) != null, (String)params.get(Constant.SQL_FILTER))
		);
		
		DeptEntity ccDeptEntity = sysDeptService.selectById(Long.valueOf(currentUser.get("DEPT_ID").toString()));
		List<Long> deptlist = sysDeptService.getSubDeptIdList(Long.valueOf(currentUser.get("DEPT_ID").toString()));

		for(SysUserEntity sysUserEntity : page.getRecords()){
			DeptEntity sysDeptEntity = sysDeptService.selectById(sysUserEntity.getDeptId());
			sysUserEntity.setDeptName(sysDeptEntity!=null?sysDeptEntity.getName():"");
			
			//只能给本部门的账号赋权，超级管理员和信息中心账号除外
			if (ccDeptEntity.getCode().equals("CC") || Constant.SUPER.equals(username.toUpperCase()))
			{
				sysUserEntity.setLeaveWay("1");
			} else {
				for (Long deptid : deptlist) {
					if (sysUserEntity.getDeptId().equals(deptid)) {
						sysUserEntity.setLeaveWay("1");
						break;
					}
				}
				
				if (Long.valueOf(currentUser.get("DEPT_ID").toString()).equals(sysUserEntity.getDeptId())) {
					sysUserEntity.setLeaveWay("1");
				}
			}
			
		}

		return new PageUtils(page);
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void save(SysUserEntity user) {
		user.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		//sha256加密
		String salt = RandomStringUtils.randomAlphanumeric(20);
		user.setSalt(salt);
		user.setPassword(MD5Util.entryptPassword(user.getUsername()));
		Map<String,Object> currentUser = userUtils.getUser();
		user.setCreateBy(currentUser.get("FULL_NAME").toString());
		user.setUpdateBy(currentUser.get("FULL_NAME").toString());
		user.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		user.setUpdateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		this.insert(user);
		//保存用户与角色关系
		sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
	}

	@Override
	@Transactional(rollbackFor = Exception.class)
	public void update(SysUserEntity user) {
		if(StringUtils.isBlank(user.getPassword())){
			user.setPassword(MD5Util.entryptPassword(user.getUsername()));
		}else{
			user.setPassword(MD5Util.entryptPassword(user.getPassword()));
		}
		this.updateById(user);
		
		//保存用户与角色关系
		sysUserRoleService.saveOrUpdate(user.getUserId(), user.getRoleIdList());
	}


	@Override
	public boolean updatePassword(Long userId, String password, String newPassword) {
        SysUserEntity userEntity = new SysUserEntity();
        userEntity.setPassword(newPassword);
        return this.update(userEntity,
                new EntityWrapper<SysUserEntity>().eq("user_id", userId).eq("password", password));
    }

	/**
	 * 用户管理：用户分配角色
	 * @param userId 用户
	 * @param roles  角色ID集合
	 */
	@Override
	@Transactional(rollbackFor = Exception.class)
	public R updateRoleAndDept(Long userId,List<String> roles) throws Exception {
//		updateDept(userId,depts);
		return updateRole(userId, roles);
	}
	
	//更新sys_user_dept
	public void updateDept(Long userId,List<Long> depts){
		List<SysUserDeptEntity> sysDeptEntityList = sysUserDeptService.selectList(new EntityWrapper<SysUserDeptEntity>().eq("user_id", userId));
		List<Long> needDel = new ArrayList<Long>();
		List<SysUserDeptEntity> needAdd = new ArrayList<SysUserDeptEntity>();
		for(SysUserDeptEntity entity:sysDeptEntityList){
			if(!CollectionUtils.contains(depts.iterator(), entity.getDeptId())){
				needDel.add(entity.getId());
			}
		}
		for(Long dept:depts){
			if(!deptContains(sysDeptEntityList,dept)){
				SysUserDeptEntity e = new SysUserDeptEntity();
				e.setUserId(userId);
				e.setDeptId(dept);
				needAdd.add(e);
			}
		}
		if(!CollectionUtils.isEmpty(needDel)){
			sysUserDeptService.deleteBatchIds(needDel);
		}
		if(!CollectionUtils.isEmpty(needAdd)){
			sysUserDeptService.insertBatch(needAdd);
		}
	}

	/**
	 * 用户分配角色:更新该用户当前最新的用户角色信息
	 * @param userId
	 * @param roles
	 */
	public R updateRole(Long userId,List<String> roles) throws Exception {
		//  云平台接口，用户分配权限,保存【确认】
		List<SysUserRoleEntity> userRoles = sysUserRoleService.selectList(new EntityWrapper<SysUserRoleEntity>().eq("user_id", userId));
		SysUserEntity userInfo=sysUserService.selectById(userId);
		List<Long> needDel = new ArrayList<Long>();
		List<Long> needDelRoleIds = new ArrayList<Long>();
		List<SysUserRoleEntity> needDeleteRoles = new ArrayList<SysUserRoleEntity>();
	    List<SysUserRoleEntity> needAdd = new ArrayList<SysUserRoleEntity>();
	    for(SysUserRoleEntity ur:userRoles){
	    	//判断该用户数据库中已经分配的角色，是否在现在分配的角色中，不在择需要删除数据库该用户的该角色。
	    	if(!userRolesContainer(roles,ur.getRoleId())){
	    		needDel.add(ur.getId());
				needDeleteRoles.add(ur);
				needDelRoleIds.add(ur.getRoleId());
	    	}
	    	
	    }
	    for(int i=0;i< roles.size();i++) {
	    	Long role = Long.parseLong(roles.get(i));
	    	//现在分配的角色在数据库中不存在，则需要新增。
	    	if(!roleContainer(userRoles,role)){
	    		SysUserRoleEntity r = new SysUserRoleEntity();
	    		r.setRoleId(role);
	    		r.setUserId(userId);
	    		needAdd.add(r);
	    	}
	    }
		HttpUserMenuRefVO userMenuRefDeleteVO=new HttpUserMenuRefVO();
		HttpClientResultVO delResult=new HttpClientResultVO(2);
		if(!CollectionUtils.isEmpty(needDel)){
	    	//bug:1598,云平台菜单接口
			userMenuRefDeleteVO.setSynType("DELETE");
			List<UserMenuRefListBean> userMenuRefDelList =new ArrayList<>();
			UserMenuRefListBean userMenuRefListDelBean=null;
			List<SysUserRoleEntity> sysUserRoleEntities=new ArrayList<>();
	    	for(SysUserRoleEntity userRoleEntity:needDeleteRoles){
				List<SysMenuEntity> menuEntityList=sysMenuService.queryMenuListByRoleId(userRoleEntity.getRoleId());
				if(menuEntityList != null && menuEntityList.size()>0) {
					for (SysMenuEntity menuEntity : menuEntityList) {
						sysUserRoleEntities = sysUserRoleService.queryUserRoleListByRids(userInfo.getUsername(), menuEntity.getMenuKey(), needDelRoleIds);
						if (sysUserRoleEntities == null || sysUserRoleEntities.isEmpty()) {//当该用户有除去这些需要删除的角色，还有角色对应这个菜单，云平台那里不能删除该用户的该菜单。
							userMenuRefListDelBean = new UserMenuRefListBean();
							userMenuRefListDelBean.setMenuCode(menuEntity.getMenuKey());
							userMenuRefListDelBean.setStaffAccount(userInfo.getUsername());
							userMenuRefListDelBean.setSystemCode(ClientDictCode.WMSTOYPT_SYSTEM_CODE);
							userMenuRefDelList.add(userMenuRefListDelBean);
						}
					}
				}
			}
			userMenuRefDeleteVO.setUserMenuRefList(userMenuRefDelList);
	    	if(!userMenuRefDelList.isEmpty()) {
				//删除同步云平台
				delResult = requestYpt(ClientDictCode.HTTPCLIENT_WMSTOYPT_IPPORT + ClientDictCode.HTTPCLIENT_WMSTOYPT_USERMENUURL, JSON.toJSONString(userMenuRefDeleteVO));
			}
			if(delResult.getErrCode()==1){//如果传云平台出现错误
				return R.error("云平台:"+delResult.getErrMsg());
			}else {
				sysUserRoleService.deleteBatchIds(needDel);
			}
	    }
	    if(!CollectionUtils.isEmpty(needAdd)){
			//bug:1598,云平台菜单接口
			HttpUserMenuRefVO userMenuRefAddVO=new HttpUserMenuRefVO();
			userMenuRefAddVO.setSynType("ADD");
			List<UserMenuRefListBean> userMenuRefAddList =new ArrayList<>();
			UserMenuRefListBean userMenuRefAddListBean=null;
			for(SysUserRoleEntity userRoleEntity:needAdd){
				List<SysMenuEntity> menuEntityList=sysMenuService.queryMenuListByRoleId(userRoleEntity.getRoleId());
				if(menuEntityList != null && menuEntityList.size()>0) {
					for (SysMenuEntity menuEntity : menuEntityList) {
						if (menuEntity.getMenuId() != 0 && menuEntity.getType() == 1) {//不需要传云平台模块和“所有菜单”
							userMenuRefAddListBean = new UserMenuRefListBean();
							userMenuRefAddListBean.setMenuCode(menuEntity.getMenuKey());
							userMenuRefAddListBean.setStaffAccount(userInfo.getUsername());
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
			if(addResult.getErrCode()==1){//如果传云平台出现错误
				if(delResult !=null && delResult.getErrCode()==0){//如果之前的删除方法成功，需要回滚操作。
					userMenuRefDeleteVO.setSynType("ADD");
					HttpClientResultVO delAddResult = requestYpt(ClientDictCode.HTTPCLIENT_WMSTOYPT_IPPORT+ClientDictCode.HTTPCLIENT_WMSTOYPT_USERMENUURL, JSON.toJSONString(userMenuRefDeleteVO));
//					if(delAddResult.getErrCode()==0) {
						throw new Exception("与云平台交互发生异常！");//回滚WMS之前删除的数据,保持与云平台一致。
//					}
				}
				return R.error("云平台:"+addResult.getErrMsg());
			}else{
				sysUserRoleService.insertBatch(needAdd);
			}
	    }
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

	private boolean deptContains(List<SysUserDeptEntity> depts,Long deptId){
		for(SysUserDeptEntity dept:depts){
			if(dept.getDeptId().longValue()==deptId.longValue()){
				return true;
			}
		}
		return false;
	}
	
	private boolean userRolesContainer(List<String> roles,Long roleId){
		for(int i=0;i<roles.size();i++){
			String rolestr = roles.get(i);
			if (rolestr.indexOf(roleId.toString())!=-1) {
				return true;
			}
		}
		return false;
	}
	
	private boolean roleContainer(List<SysUserRoleEntity> userRoles,Long roleId){
		for(SysUserRoleEntity userRole:userRoles){
			if(userRole.getRoleId().longValue()==roleId.longValue()){
				return true;
			}
		}
		return false;
	}
	
	@Override
	public boolean checkPermission(Long userId, String permission) {
		boolean f=false;
		List<String>list = baseMapper.queryAllPerms(userId);
		List<String> permsList=new ArrayList<String>();
		for(String perms : list){
			if(StringUtils.isBlank(perms)){
				continue;
			}
			permsList.addAll(Arrays.asList(perms.trim().split(",")));
		}
		for(String p:permsList) {
			if(p.equals(permission)) {
				f=true;
				break;
			}
		}
		
		return f;
		
	}
	@Override
	public Map<String,Object> getUserByUserName(String username) {
		return baseMapper.getUserByUserName(username);
	}
	
	@Override
	public String queryCardInfo(String staffnumber) throws Exception {
		String JDriver="com.microsoft.sqlserver.jdbc.SQLServerDriver";//SQL数据库引擎
		String connectDB = cardconfigConstant.getCardurl();
		String user = cardconfigConstant.getCardusername();
		String password = cardconfigConstant.getCardpassword();
		
		Connection con=null;
		String cardfixid="";
		try{
			Class.forName(JDriver);//加载数据库引擎，返回给定字符串名的类
			con=DriverManager.getConnection(connectDB,user,password);//连接数据库对象		
			con.setAutoCommit(false);
			System.out.println("一卡通连接数据库成功");
			Statement stmt=con.createStatement();//创建SQL命令对象
			
				String listsql="SELECT PeoNo,PeoName,CardFixID  FROM v_MadmCard_4_byd where peono='"+staffnumber+"'";
				ResultSet rs=stmt.executeQuery(listsql);
				if(rs.next()){
					 cardfixid = rs.getString(3);//
				}
			
			stmt.executeBatch();
			stmt.close();
			con.commit();
			con.close();
		}catch(Exception e){
			System.out.println(e.toString());
			throw e;
		}
		
		return cardfixid;
	}
	/**
	 * 通过厂牌 卡号 获取 员工信息 参数 卡号 字符串 
	 */
	@Override
	public Map<String, Object> getUserInfoByCardInfo(String cardHd) {
		Map<String, Object> mapret=new HashMap<String, Object>();
		if("".equals(cardHd)){//cardHd 没值
			mapret.put("msg", "请传入卡号!");
			return mapret;
		}else{
			Map<String, Object> mapcond=new HashMap<String, Object>();
			mapcond.put("CARD_HD", cardHd);
			List<SysUserEntity> retList=sysUserService.selectByMap(mapcond);
			if(retList.size()>0){
				mapret.put("staffNumber", retList.get(0).getStaffNumber());//工号
				
				DeptEntity sysDeptEntity = sysDeptService.selectById(retList.get(0).getDeptId());
				retList.get(0).setDeptName(sysDeptEntity!=null?sysDeptEntity.getName():"");
				mapret.put("deptName", retList.get(0).getDeptName());//部门
				
				mapret.put("fullName", retList.get(0).getFullName());
				mapret.put("msg", "success");
			}else{
				mapret.put("msg", "卡号不存在!请确认是否同步了该卡号");
				return mapret;
			}
		}
		return mapret;
	}
	
	@Override
	public List<Map<String,Object>> getUserKanban(Map<String, Object> params){
		return baseMapper.getUserKanban(params);
	}
	
	@Override
	public List<Map<String, Object>> getUserList(String userName) {
		return baseMapper.getUserList(userName) ;
	}

	@Override
	public List<SysUserEntity> getUserListByRoleId(Long roleId) {
		return baseMapper.getUserListByRoleId(roleId) ;
	}

	@Override
	public List<SysUserEntity> getUserListByRoleIdUids(Long roleId,List<Long> userIds) {
		return baseMapper.getUserListByRoleIdUids(roleId,userIds) ;
	}


	@Override
	public List<Map<String, Object>> getRoleList(String roleName) {
		return baseMapper.getRoleList(roleName) ;
	}

	@Override
	public List<Map<String, Object>> getMenuList(String menuName) {
		return baseMapper.getMenuList(menuName) ;
	}
	
	@Override
	public Map<String, Object> getUserInfo(Map<String, Object> params) {
		return baseMapper.getUserInfo(params);
	}
	
	@Override
	public Map<String,Object> getUserMap(List<String> params){
		Map<String, Object> rtn = new HashMap<>();
		
		List<Map<String, Object>> list = baseMapper.getUserMap(params);
		
		for (Map<String, Object> map : list) {
			rtn.put(map.get("USERNAME").toString(), map);
		}
		return rtn;
		
	}

	@Override
	public List<Map<String, Object>> getUserMapList(List<String> params) {
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		
		retList = baseMapper.getUserMap(params);
		
		return retList;
	}

	@Override
	public List<Map<String, Object>> getUserMapNList(List<String> params) {
		List<Map<String, Object>> retList = new ArrayList<Map<String, Object>>();
		
		retList = baseMapper.getUserMapN(params);
		
		return retList;
	}
	
}

