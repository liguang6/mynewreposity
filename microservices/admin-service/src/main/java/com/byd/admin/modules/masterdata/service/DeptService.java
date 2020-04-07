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

package com.byd.admin.modules.masterdata.service;


import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.admin.modules.masterdata.entity.DeptEntity;
import com.byd.utils.PageUtils;

/**
 * 部门管理
 * 
 * @author cscc
 * @email 
 * @date 2017-06-20 15:23:47
 */
public interface DeptService extends IService<DeptEntity> {

	List<DeptEntity> queryList(Map<String, Object> map);
	
	List<DeptEntity> queryDeptList(Map<String, Object> map);

	/**
	 * 查询子部门ID列表
	 * @param parentId  上级部门ID
	 */
	List<Long> queryDetpIdList(Long parentId);

	/**
	 * 获取子部门ID，用于数据过滤
	 */
	List<Long> getSubDeptIdList(Long deptId);
	public int enableDept(Long deptId);
    public int disabledDept(Long deptId);
    public int deleteDept(Long deptId);
    public int updateDept(DeptEntity dept);

    
    /**
     * 获取用户所属的部门
     * @param userId
     * @return
     */
    List<DeptEntity> selectDeptsByUserId(Long userId);

    /**
     * 获取全部门树
     * @param params
     * @return
     */
	List<DeptEntity> queryDeptListAll();
	
	void getParentDeptIdList(Long deptId,List<Long> parentIdList);
	
	
	List<Map<String,Object>> getPlantList(String werks);
	
	List<Map<String,Object>> getDeptChildNodes(Map<String, Object> params);
	
	List<Map<String,Object>> getWorkshopWorkgroupList(Map<String, Object> params);
	
	List<Map<String,Object>> getWorkshopLineList(Map<String, Object> params);
	
	List<Map<String,Object>> getTeamList(Map<String, Object> params);
	
	public PageUtils getWorkshopWorkgroupListByPage(Map<String, Object> params);
	
	public PageUtils getWorkTeamListByPage(Map<String, Object> params);
	
	List<Map<String,Object>> getTeamListByWorkshop(Map<String, Object> params);
	
}
