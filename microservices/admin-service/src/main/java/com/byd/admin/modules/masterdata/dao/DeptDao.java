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

package com.byd.admin.modules.masterdata.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.admin.modules.masterdata.entity.DeptEntity;

/**
 * 部门管理
 * 
 * @author cscc
 * @email 
 * @date 2017-06-20 15:23:47
 */
public interface DeptDao extends BaseMapper<DeptEntity> {

    /**
     * 查询子部门ID列表
     * @param parentId  上级部门ID
     */
	public List<Long> queryDetpIdList(Long parentId);
	
	public Long queryParentDeptId(Long deptId);
    
    public List<DeptEntity> queryDeptList(Map<String, Object> map);
    public int enableDept(Long deptId);
    public int disabledDept(Long deptId);
    public int deleteDept(Long deptId);
    public int updateDept(DeptEntity dept);

    
    public List<DeptEntity> selectDeptByUserId(Long userId);


	public List<DeptEntity> queryDeptListAll();
	
    // 通过工厂代码模糊查找工厂数据
	public List<Map<String,Object>> getPlantList(@Param("werks")String werks);
	
	/**
	 * 根据部门类型、部门编号，查询本部门所有下一节点部门清单 
	 * @param params 包含 DEPT_TYPE：部门类型，CODE 部门代码
	 * @return
	 */
    public List<Map<String,Object>> getDeptChildNodes(Map<String, Object> params);
    
    List<Map<String,Object>> getWorkshopWorkgroupList(Map<String, Object> params);
    
    List<Map<String,Object>> getWorkshopLineList(Map<String, Object> params);
    
    List<Map<String,Object>> getTeamList(Map<String, Object> params);
    
    List<Map<String,Object>> getTeamListNEW(Map<String, Object> params);
    
    public int getWorkshopWorkgroupListCount(Map<String,Object> params);
    
    public int getWorkTeamListCount(Map<String,Object> params);
    
    List<Map<String,Object>> getWorkshopWorkgroupListNEW(Map<String, Object> params);
    
    List<Map<String,Object>> getTeamListByWorkshop(Map<String, Object> params);
}
