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

package com.byd.admin.modules.masterdata.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.byd.admin.common.annotation.SysLog;
import com.byd.admin.modules.masterdata.entity.DeptEntity;
import com.byd.admin.modules.masterdata.service.DeptService;
import com.byd.utils.Constant;
import com.byd.utils.DateUtils;
import com.byd.utils.R;
import com.byd.utils.UserUtils;


/**
 * 部门管理
 * 
 * @author cscc
 * @email 
 * @date 2017-06-20 15:23:47
 */
@RestController
@RequestMapping("/masterdata/dept")
public class DeptController {
	@Autowired
	private DeptService sysDeptService;
	@Autowired
	private UserUtils userUtils;
	
	/**
	 * 列表
	 */
	@RequestMapping("/list")
	public List<DeptEntity> list(@RequestParam Map<String, Object> params){
		
		List<DeptEntity> deptList = sysDeptService.queryDeptList(params);
		
		for(DeptEntity sysDeptEntity : deptList){
			sysDeptEntity.setId(sysDeptEntity.getDeptId());
			sysDeptEntity.setParentCode(sysDeptEntity.getParentId()+"");
			if(sysDeptEntity.getParentId() == 0) {
				sysDeptEntity.setIsRoot(true);
			}else {
				sysDeptEntity.setIsRoot(false);
			}
			if(sysDeptEntity.getTreeLeaf().equals("0")){
				sysDeptEntity.setIsTreeLeaf(false);
			}else {
				sysDeptEntity.setIsTreeLeaf(true);
			}
			if(sysDeptEntity.getTreeLeaf().equals("0")){
				sysDeptEntity.setIsParent(true);
			}else {
				sysDeptEntity.setIsParent(false);
			}
		}
		return deptList;
	}
	
	/**
	 * 整个部门列表
	 */
	@RequestMapping("/listall")
/*	@RequiresPermissions("sys:dept:listall")*/
	public List<DeptEntity> listall(){
		
		List<DeptEntity> deptList = sysDeptService.queryDeptListAll();
		
		for(DeptEntity sysDeptEntity : deptList){
			sysDeptEntity.setId(sysDeptEntity.getDeptId());
			sysDeptEntity.setParentCode(sysDeptEntity.getParentId()+"");
			if(sysDeptEntity.getParentId() == 0) {
				sysDeptEntity.setIsRoot(true);
			}else {
				sysDeptEntity.setIsRoot(false);
			}
			if(sysDeptEntity.getTreeLeaf().equals("0")){
				sysDeptEntity.setIsTreeLeaf(false);
			}else {
				sysDeptEntity.setIsTreeLeaf(true);
			}
			if(sysDeptEntity.getTreeLeaf().equals("0")){
				sysDeptEntity.setIsParent(true);
			}else {
				sysDeptEntity.setIsParent(false);
			}
		}
		return deptList;
	}

	/**
	 * 选择部门(添加、修改菜单)
	 */
	@RequestMapping("/select")
	public R select(){
		List<DeptEntity> deptList = sysDeptService.queryList(new HashMap<String, Object>());

		//添加一级部门
		if(userUtils.getUserId() == Constant.SUPER_ADMIN){
			DeptEntity root = new DeptEntity();
			root.setDeptId(0L);
			root.setName("一级部门");
			root.setParentId(-1L);
			root.setOpen(true);
			deptList.add(root);
		}

		return R.ok().put("deptList", deptList);
	}

	/**
	 * 上级部门Id(管理员则为0)
	 */
	@RequestMapping("/info")
	public R info(){
		long deptId = 0;
		if(userUtils.getUserId() != Constant.SUPER_ADMIN){
			List<DeptEntity> deptList = sysDeptService.queryList(new HashMap<String, Object>());
			Long parentId = null;
			for(DeptEntity sysDeptEntity : deptList){
				if(parentId == null){
					parentId = sysDeptEntity.getParentId();
					continue;
				}

				if(parentId > sysDeptEntity.getParentId().longValue()){
					parentId = sysDeptEntity.getParentId();
				}
			}
			deptId = parentId;
		}

		return R.ok().put("deptId", deptId);
	}
	
	/**
	 * 信息
	 */
	@RequestMapping("/info/{deptId}")
	public R info(@PathVariable("deptId") Long deptId){
		DeptEntity dept = sysDeptService.selectById(deptId);
		
		DeptEntity parentDeptEntity =  sysDeptService.selectById(dept.getParentId());
		if(parentDeptEntity != null){
			dept.setParentName(parentDeptEntity.getName());
		}
		
		return R.ok().put("dept", dept);
	}
	
	/**
	 * 保存
	 */
	@SysLog("部门管理-新增部门")
	@RequestMapping("/save")
	public R save(@RequestBody DeptEntity dept){
		try{
		Map<String, Object> condMap=new HashMap<String, Object>();
		condMap.put("PARENT_ID", dept.getParentId());
		condMap.put("CODE", dept.getCode());
		condMap.put("DEPT_TYPE", dept.getDeptType());
		List<DeptEntity> retList=sysDeptService.selectByMap(condMap);
		if(retList!=null&&retList.size()>0){
			throw new RuntimeException("该部门已经存在，不能重复添加！");
		}
		//获取父节点部门
		DeptEntity parentDeptEntity =  sysDeptService.selectById(dept.getParentId());
		
		dept.setTreeLeaf("1");
		dept.setStatus("0");
		dept.setTreeLevel(String.valueOf(Integer.valueOf(parentDeptEntity.getTreeLevel())+1));
		dept.setTreeNames(parentDeptEntity.getTreeNames() + " " + dept.getName());
		dept.setCreateBy(userUtils.getTokenUsername());
		dept.setUpdateBy(userUtils.getTokenUsername());
		dept.setCreateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		dept.setUpdateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		
		sysDeptService.insert(dept);
		
		//更新父节点部门
		if(parentDeptEntity.getTreeLeaf().equals("1")) {
			parentDeptEntity.setTreeLeaf("0");
			sysDeptService.updateById(parentDeptEntity);
		}
		
		} catch (Exception e) {
			//e.printStackTrace();
			return R.error(e.getMessage());
		}
		
		return R.ok();
	}
	
	/**
	 * 修改
	 */
	@SysLog("部门管理-修改部门")
	@RequestMapping("/update")
	public R update(@RequestBody DeptEntity dept){
		dept.setUpdateBy(userUtils.getTokenUsername());
		dept.setUpdateDate(DateUtils.format(new Date(), DateUtils.DATE_TIME_PATTERN));
		sysDeptService.updateDept(dept);
		return R.ok();
	}
	
	@RequestMapping("/enableDept")
	public R enableDept(long deptId){
		sysDeptService.enableDept(deptId);		
		return R.ok();
	}
	
	@RequestMapping("/disabledDept")
	public R disabledDept(long deptId){
		sysDeptService.disabledDept(deptId);		
		return R.ok();
	}
	@SysLog("部门管理-删除部门")
	@RequestMapping("/delete")
	public R delete(long deptId){
		/** //判断是否有子部门
		List<Long> deptList = sysDeptService.queryDetpIdList(deptId);
		if(deptList.size() > 0){
			return R.error("请先删除子部门");
		}
		sysDeptService.deleteById(deptId);
		**/
		sysDeptService.deleteDept(deptId);		
		return R.ok();
	}
	
	/**
	 * ztree 简单格式json
	 * @return
	 */
	@RequestMapping("/ztreeDepts")
	//@RequiresPermissions("sys:dept:list")
	public List<DeptEntity> deptListForZtree(@RequestParam Map<String, Object> params){
		return sysDeptService.queryList(params);
	}
	
}
