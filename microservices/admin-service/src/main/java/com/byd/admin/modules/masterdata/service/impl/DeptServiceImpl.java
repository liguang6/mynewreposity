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

package com.byd.admin.modules.masterdata.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.toolkit.CollectionUtils;
import com.byd.admin.common.annotation.DataFilter;
import com.byd.admin.modules.masterdata.dao.DeptDao;
import com.byd.admin.modules.masterdata.dao.WorkgroupDao;
import com.byd.admin.modules.masterdata.entity.DeptEntity;
import com.byd.admin.modules.masterdata.service.DeptService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;


@Service("deptService")
public class DeptServiceImpl extends ServiceImpl<DeptDao, DeptEntity> implements DeptService {
	
	@Autowired
	private DeptDao sysDeptDao;
	
	@Override
	@DataFilter(subDept = false, user = false)
	public List<DeptEntity> queryList(Map<String, Object> params){
		String deptType=(String)params.get("deptType");
		String parentTreeNames=(String) params.get("parentTreeNames");
		Long parantId = null;
		String parantDeptCode =(String) params.get("parentDeptCode");
		
		//上级部门的code转换成 dept_id
		if(StringUtils.isNotEmpty(parantDeptCode)){
			List<DeptEntity> depts = this.selectList(new EntityWrapper<DeptEntity>().eq("CODE", parantDeptCode));
			if(CollectionUtils.isNotEmpty(depts)){
				parantId = depts.get(0).getDeptId();
			}
		}
		List<DeptEntity> deptList = this.selectList(new EntityWrapper<DeptEntity>()
				.eq(StringUtils.isNotBlank(deptType),"dept_type", deptType)
				.like(StringUtils.isNotBlank(parentTreeNames),"tree_names", parentTreeNames)
				.eq(parantId != null,"PARENT_ID", parantId));
				//.addFilterIfNeed(StringUtils.isNotBlank((String)params.get(Constant.SQL_FILTER)), (String)params.get(Constant.SQL_FILTER))

		List<Long> parentIdList=new ArrayList<Long>();
		for(DeptEntity sysDeptEntity : deptList) {
			parentIdList.add(sysDeptEntity.getDeptId());
			getParentDeptIdList(sysDeptEntity.getDeptId(),parentIdList);
		}
		Collections.sort(parentIdList);
		int pointsDataLimit = 1000;//限制条数
		Integer size = parentIdList.size();
		//判断是否有必要分批
		if(pointsDataLimit<size){
			deptList = new ArrayList<>();
			
			int part = size/pointsDataLimit;//分批数
			System.out.println("共有 ： "+size+"条，！"+" 分为 ："+part+"批 ");
			for (int i = 0; i < part; i++) {
				//1000条
				List<Long> listPage = parentIdList.subList(0, pointsDataLimit);
				//System.out.println(listPage);
				deptList.addAll(this.selectList(new EntityWrapper<DeptEntity>()
						.eq(StringUtils.isNotBlank(deptType),"dept_type", deptType)
						.like(StringUtils.isNotBlank(parentTreeNames),"tree_names", parentTreeNames)
						.in("dept_id", listPage)
						));
				//剔除
				parentIdList.subList(0, pointsDataLimit).clear();
			}
			if(!parentIdList.isEmpty()){//表示最后剩下的数据
				deptList.addAll(this.selectList(new EntityWrapper<DeptEntity>()
						.eq(StringUtils.isNotBlank(deptType),"dept_type", deptType)
						.like(StringUtils.isNotBlank(parentTreeNames),"tree_names", parentTreeNames)
						.in("dept_id", parentIdList)
						));
			}
		}else {
			deptList = this.selectList(new EntityWrapper<DeptEntity>()
					.eq(StringUtils.isNotBlank(deptType),"dept_type", deptType)
					.like(StringUtils.isNotBlank(parentTreeNames),"tree_names", parentTreeNames)
					.in("dept_id", parentIdList)
					);
		}

		
		for(DeptEntity sysDeptEntity : deptList){
			DeptEntity parentDeptEntity =  this.selectById(sysDeptEntity.getParentId());
			if(parentDeptEntity != null){
				sysDeptEntity.setParentName(parentDeptEntity.getName());
			}else {
				sysDeptEntity.setParentName("-");
			}
		}
		return deptList;
	}
	
	@Override
	public List<DeptEntity> queryDeptList(Map<String, Object> map){
		return baseMapper.queryDeptList(map);
	}

	@Override
	public List<Long> queryDetpIdList(Long parentId) {
		return baseMapper.queryDetpIdList(parentId);
	}

	@Override
	public List<Long> getSubDeptIdList(Long deptId){
		//部门及子部门ID列表
		List<Long> deptIdList = new ArrayList<>();

		//获取子部门ID
		List<Long> subIdList = queryDetpIdList(deptId);
		getDeptTreeList(subIdList, deptIdList);

		return deptIdList;
	}

	/**
	 * 递归
	 */
	private void getDeptTreeList(List<Long> subIdList, List<Long> deptIdList){
		for(Long deptId : subIdList){
			List<Long> list = queryDetpIdList(deptId);
			if(list.size() > 0){
				getDeptTreeList(list, deptIdList);
			}

			deptIdList.add(deptId);
		}
	}

	@Override
	public int enableDept(Long deptId) {
		return baseMapper.enableDept(deptId);
	}

	@Override
	public int disabledDept(Long deptId) {
		return baseMapper.disabledDept(deptId);
	}

	@Override
	public int deleteDept(Long deptId) {
		return baseMapper.deleteDept(deptId);
	}
	
	@Override
	public int updateDept(DeptEntity dept) {
		return baseMapper.updateDept(dept);
	}


	@Override
	public List<DeptEntity> selectDeptsByUserId(Long userId) {
		return sysDeptDao.selectDeptByUserId(userId);
	}


	
	@Override
	public List<DeptEntity> queryDeptListAll() {
		return baseMapper.queryDeptListAll();
	}

	@Override
	public void getParentDeptIdList(Long deptId, List<Long> parentIdList) {
		Long pId=baseMapper.queryParentDeptId(deptId);
		
		if(pId>0) {
			parentIdList.add(pId);
			getParentDeptIdList(pId,parentIdList);
		}
	}
	
	@Override
	public List<Map<String,Object>> getPlantList(String werks){
		return sysDeptDao.getPlantList(werks);
	}
	
	@Override
	public List<Map<String,Object>> getDeptChildNodes(Map<String, Object> params){
		return baseMapper.getDeptChildNodes(params);
	}
	
	@Override
	public List<Map<String,Object>> getWorkshopLineList(Map<String, Object> params){
		return baseMapper.getWorkshopLineList(params);
	}
	
	@Override
	public List<Map<String,Object>> getWorkshopWorkgroupList(Map<String, Object> params){
		return baseMapper.getWorkshopWorkgroupList(params);
	}
	
	@Override
	public List<Map<String,Object>> getTeamList(Map<String, Object> params){
		return baseMapper.getTeamList(params);
	}

	@Override
	public PageUtils getWorkshopWorkgroupListByPage(Map<String, Object> params) {
		//处理WORKSHOP
		if(params.get("WORKSHOP")!=null ){
			String[] workshoplist=params.get("WORKSHOP").toString().split(",");
			params.put("workshoplist", workshoplist);
		}
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=sysDeptDao.getWorkshopWorkgroupListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=baseMapper.getWorkshopWorkgroupListNEW(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

	@Override
	public PageUtils getWorkTeamListByPage(Map<String, Object> params) {
		//处理 WORKGROUP
		if(params.get("WORKGROUP")!=null ){
			String[] workgrouplist=params.get("WORKGROUP").toString().split(",");
			params.put("workgrouplist", workgrouplist);
		}
		//处理WORKSHOP
		if(params.get("WORKSHOP")!=null ){
			String[] workshoplist=params.get("WORKSHOP").toString().split(",");
			params.put("workshoplist", workshoplist);
		}
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=sysDeptDao.getWorkTeamListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=baseMapper.getTeamListNEW(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

	@Override
	public List<Map<String, Object>> getTeamListByWorkshop(Map<String, Object> params) {
		return sysDeptDao.getTeamListByWorkshop(params);
	}
}
