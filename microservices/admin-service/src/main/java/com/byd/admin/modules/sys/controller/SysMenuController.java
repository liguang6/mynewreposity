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

import com.alibaba.fastjson.JSON;
import com.byd.admin.common.annotation.SysLog;
import com.byd.admin.modules.httpclient.entity.ClientDictCode;
import com.byd.admin.modules.httpclient.entity.HttpClientResultVO;
import com.byd.admin.modules.httpclient.entity.HttpMenuDataVO;
import com.byd.admin.modules.httpclient.entity.MenuListBean;
import com.byd.admin.modules.sys.entity.SysMenuEntity;
import com.byd.admin.modules.sys.remote.YptMenuRemote;
import com.byd.admin.modules.sys.service.SysMenuService;
import com.byd.utils.Constant;
import com.byd.utils.R;
import com.byd.utils.UserUtils;
import com.byd.utils.exception.RRException;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 系统菜单
 * 
 * @author cscc
 * @email 
 * @date 2016年10月27日 下午9:58:15
 */
@RestController
@RequestMapping("/sys/menu")
public class SysMenuController {
	@Autowired
	private SysMenuService sysMenuService;
	@Autowired
	private UserUtils userUtils;
	@Autowired
	private YptMenuRemote yptMenuRemote;

	/**
	 * 导航菜单
	 */
	@RequestMapping("/nav")
	public R nav(String username){
		List<SysMenuEntity> menuList = sysMenuService.getUserMenuList(userUtils.getUserId());
		return R.ok().put("menuList", menuList);
	}
	
	/**
	 * 所有菜单列表
	 */
	@RequestMapping("/list")
	public List<SysMenuEntity> list(){
		
		List<SysMenuEntity> menuList = sysMenuService.selectList(null);
		for(SysMenuEntity sysMenuEntity : menuList){
			SysMenuEntity parentMenuEntity = sysMenuService.selectById(sysMenuEntity.getParentId());
			if(parentMenuEntity != null){
				sysMenuEntity.setParentName(parentMenuEntity.getName());
			}
		}

		return menuList;
	
	}
	/**
	 * 获取所有PC端功能菜单
	 * @return
	 */
	@RequestMapping("/getAllAuthList")
	public List<Map<String,Object>> getAllAuthList(){
		
		List<Map<String,Object>> menuList = sysMenuService.getAllAuthList();
		return menuList;
	
	}
	
	
	
	@RequestMapping("/list2")
	public List<SysMenuEntity> list2(@RequestParam  Map<String,Object> params){
		List<SysMenuEntity> menuList = sysMenuService.queryMenuList(params);
		
		for(SysMenuEntity sysMenuEntity : menuList){
			sysMenuEntity.setId(sysMenuEntity.getMenuId());
			sysMenuEntity.setParentCode(sysMenuEntity.getParentId()+"");
			if(sysMenuEntity.getParentId() == 0) {
				sysMenuEntity.setIsRoot(true);
			}else {
				sysMenuEntity.setIsRoot(false);
			}
			if(sysMenuEntity.getTreeLeaf().equals("0")){
				sysMenuEntity.setIsTreeLeaf(false);
			}else {
				sysMenuEntity.setIsTreeLeaf(true);
			}
		}
		return menuList;
	
	}
	
	/**
	 * 选择菜单(添加、修改菜单)
	 */
	@RequestMapping("/select")
	public R select(){
		//查询列表数据
		List<SysMenuEntity> menuList = sysMenuService.queryNotButtonList();
		
		//添加顶级菜单
		SysMenuEntity root = new SysMenuEntity();
		root.setMenuId(0L);
		root.setName("一级菜单");
		root.setParentId(-1L);
		root.setOpen(true);
		menuList.add(root);
		
		return R.ok().put("menuList", menuList);
	}
	
	/**
	 * 菜单信息
	 */
	@RequestMapping("/info/{menuId}")
	public R info(@PathVariable("menuId") Long menuId){
		SysMenuEntity menu = sysMenuService.selectById(menuId);
		return R.ok().put("menu", menu);
	}
	
	/**
	 * 保存
	 */
	@SysLog("保存菜单")
	@RequestMapping("/save")
	@Transactional
	public R save(@RequestBody SysMenuEntity menu) throws Exception {
		//数据校验
		verifyForm(menu,"ADD");

		//bug:1598,查询并且构造需要传云平台的数据，转换为Json串，走接口
		HttpMenuDataVO menuDataVO=constructMenuData(menu,"ADD");
		HttpClientResultVO result = requestYpt(ClientDictCode.HTTPCLIENT_WMSTOYPT_IPPORT+ClientDictCode.HTTPCLIENT_WMSTOYPT_MENUURL, JSON.toJSONString(menuDataVO));
		if(result.getErrCode()==1){//如果传云平台出现错误
			return R.error("云平台:"+result.getErrMsg());
		}else {
			sysMenuService.insert(menu);

			return R.ok();
		}
	}
	
	/**
	 * 修改
	 */
	@SysLog("修改菜单")
	@RequestMapping("/update")
	@Transactional
	public R update(@RequestBody SysMenuEntity menu) throws Exception {
		//数据校验
		verifyForm(menu,"UPDATE");

		//bug:1598,查询并且构造需要传云平台的数据，转换为Json串，走接口
		HttpMenuDataVO menuDataVO=constructMenuData(menu,"UPDATE");
		//修改同步云平台
		HttpClientResultVO result = requestYpt(ClientDictCode.HTTPCLIENT_WMSTOYPT_IPPORT+ClientDictCode.HTTPCLIENT_WMSTOYPT_MENUURL, JSON.toJSONString(menuDataVO));
		if(result.getErrCode()==1){//如果传云平台出现错误
			return R.error("云平台:"+result.getErrMsg());
		}else {
			sysMenuService.updateById(menu);

			return R.ok();
		}
	}
	
	/**
	 * 删除
	 */
	@SysLog("删除菜单")
	@RequestMapping("/delete")
	@Transactional
	public R delete(long menuId) throws Exception {
		if(menuId <= 31){
			return R.error("系统菜单，不能删除");
		}

		//判断是否有子菜单或按钮
		List<SysMenuEntity> menuList = sysMenuService.queryListParentId(menuId);
		if(!menuList.isEmpty()){
			return R.error("请先删除子菜单或按钮");
		}

		//bug:1598,查询并且构造需要传云平台的数据，转换为Json串，走接口
		SysMenuEntity menuEntity=sysMenuService.queryMenuListById(menuId);
		if(menuEntity==null){
			return R.error("菜单信息丢失，请重试！");
		}
		HttpMenuDataVO menuDataVO=constructMenuData(menuEntity,"DELETE");
		//删除同步云平台
		HttpClientResultVO result = requestYpt(ClientDictCode.HTTPCLIENT_WMSTOYPT_IPPORT+ClientDictCode.HTTPCLIENT_WMSTOYPT_MENUURL, JSON.toJSONString(menuDataVO));
		if(result.getErrCode()==1){//如果传云平台出现错误
			return R.error("云平台:"+result.getErrMsg());
		}else {
			sysMenuService.delete(menuId);

			return R.ok();
		}
	}

	/**
	 * @author rain
	 * @param menu
	 * @date 2019年11月1日09:55:51
	 * @description:bug:1598,此方法专门为菜单的新增、修改和删除，构造传云平台接口的数据
	 * @throws Exception
	 */
	public HttpMenuDataVO constructMenuData(SysMenuEntity menu,String operFlag) throws Exception {
		HttpMenuDataVO menuDataVO=new HttpMenuDataVO();
		List<MenuListBean> menuListBeanList=new ArrayList<>();
		MenuListBean menuListBean=new MenuListBean();
		/**
		 * 删除菜单：云平台要求，只传菜单+系统或者级别目录+系统
		 */
		if("DELETE".equals(operFlag)){
		/*
			type解释[目录:0,菜单:1,按钮:2]
			DataType解释["1":菜单，"2":模块]
			我们系统目录对应云平台模块
		 */
			if(menu.getType()==1){
				menuDataVO.setDataType("1");
				menuListBean.setMenuCode(menu.getMenuKey());
				menuListBean.setMenuName(menu.getName());
				String url=menu.getUrl()==null?"":menu.getUrl();
				if(url.indexOf("/")==0){//应云平台要求，URL前要加上"/"
					menuListBean.setMenuUrl(url);
				}else{
					menuListBean.setMenuUrl("/"+url);
				}
			}else if(menu.getType()==0) {
				menuDataVO.setDataType("2");
				if(menu.getParentId()==null){//则为一级目录(系统不存在一级菜单)
					menuListBean.setModule1Code(menu.getMenuKey());
					menuListBean.setModule1Name(menu.getName());
				}else if(menu.getParentId()==0) {//则为二级目录
					menuListBean.setModule2Code(menu.getMenuKey());
					menuListBean.setModule2Name(menu.getName());
				}
			}
		}else {
		/*
			type解释[目录:0,菜单:1,按钮:2]
			DataType解释["1":菜单，"2":模块]
			我们系统目录对应云平台模块
		 */
			if (menu.getType() == 1) {
				menuDataVO.setDataType("1");
			}
			else if (menu.getType() == 0) {
				menuDataVO.setDataType("2");
			}
			menuListBean.setMenuCode(menu.getMenuKey());
			menuListBean.setMenuName(menu.getName());
			String url = menu.getUrl() == null ? "" : menu.getUrl();
			if (url.indexOf("/") == 0) {//应云平台要求，URL前要加上"/"
				menuListBean.setMenuUrl(url);
			}
			else {
				menuListBean.setMenuUrl("/" + url);
			}
			if (menu.getParentId() == null) {//则为一级目录(系统不存在一级菜单)
				menuListBean.setModule1Code(menu.getMenuKey());
				menuListBean.setModule1Name(menu.getName());
//		menuListBean.setModule2Code("");
//		menuListBean.setModule2Name("");
			}
			else if (menu.getParentId() == 0) {//则为二级目录或者只有一级目录的菜单
				SysMenuEntity parentMenu = sysMenuService.selectById(menu.getParentId());//查询父menu
				menuListBean.setModule1Code(parentMenu.getMenuKey());
				menuListBean.setModule1Name(parentMenu.getName());
//		menuListBean.setModule2Code("");
//		menuListBean.setModule2Name("");
			}
			else {//则为菜单（系统不存在三级目录）
				SysMenuEntity parentMenu = sysMenuService.selectById(menu.getParentId());//查询父menu
				SysMenuEntity grandpaMenu = sysMenuService.selectById(parentMenu.getParentId());//查询爷menu
				menuListBean.setModule1Code(grandpaMenu.getMenuKey());
				menuListBean.setModule1Name(grandpaMenu.getName());
				menuListBean.setModule2Code(parentMenu.getMenuKey());
				menuListBean.setModule2Name(parentMenu.getName());
			}
		}
		menuDataVO.setSynType(operFlag);
		menuListBean.setSystemCode(ClientDictCode.WMSTOYPT_SYSTEM_CODE);
		menuListBean.setSystemName(ClientDictCode.WMSTOYPT_SYSTEM_NAME);
		menuListBeanList.add(menuListBean);
		menuDataVO.setMenuList(menuListBeanList);
		return menuDataVO;
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

	/**
	 * 验证参数是否正确
	 */
	private void verifyForm(SysMenuEntity menu,String operFlag){
		final int menuTypeValue=Constant.MenuType.MENU.getValue();//菜单的type常量值
		if(menu.getType() != Constant.MenuType.BUTTON.getValue()) {//bug:1598,不为按钮
			if (StringUtils.isBlank(menu.getMenuKey())) {
				throw new RRException("菜单编码不能为空");
			}else {
				SysMenuEntity dbMenuVO = sysMenuService.queryListByCode(menu.getMenuKey());
				if (dbMenuVO != null) {
					if ("ADD".equals(operFlag)) {
						throw new RRException("菜单编码在系统中已经存在！");
					}else if ("UPDATE".equals(operFlag) && !dbMenuVO.getMenuId().toString().equals(menu.getMenuId().toString())) {
						throw new RRException("菜单编码在系统中已经存在！");
					}
				}
			}
		}
		if(StringUtils.isBlank(menu.getName())&&(menu.getType() == menuTypeValue)){
			throw new RRException("菜单名称不能为空");
		}
		if(menu.getParentId() == null&&(menu.getType() == menuTypeValue)){
			throw new RRException("上级菜单不能为空");
		}

		//菜单
		if(menu.getType() == menuTypeValue){
			if(StringUtils.isBlank(menu.getUrl())){
				throw new RRException("菜单URL不能为空");
			}
		}
		
		//上级菜单类型
		int parentType = Constant.MenuType.CATALOG.getValue();
		if(menu.getParentId() != null){
			if(menu.getParentId() != 0){
				SysMenuEntity parentMenu = sysMenuService.selectById(menu.getParentId());
				parentType = parentMenu.getType();
			}
		}
		
		//目录、菜单
		if(menu.getType() == Constant.MenuType.CATALOG.getValue() ||
				menu.getType() == menuTypeValue){
			if(parentType != Constant.MenuType.CATALOG.getValue()){
				throw new RRException("上级菜单只能为目录类型");
			}
		}
		
		//按钮
		/*if(menu.getType() == Constant.MenuType.BUTTON.getValue()){
			if(parentType != Constant.MenuType.MENU.getValue()){
				throw new RRException("上级菜单只能为菜单类型");
			}
			return ;
		}*/
	}
}
