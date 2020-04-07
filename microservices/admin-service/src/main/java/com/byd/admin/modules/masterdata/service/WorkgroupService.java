package com.byd.admin.modules.masterdata.service;

import java.util.List;
import java.util.Map;

import com.baomidou.mybatisplus.service.IService;
import com.byd.admin.modules.masterdata.entity.DeptEntity;
import com.byd.utils.PageUtils;

/**
 * 车间班组、小班组
 *
 * @author cscc
 * @email 
 * @date 2018-06-04 11:07:19
 */
public interface WorkgroupService extends IService<DeptEntity> {

	/**
	 * 
	 * @param params 分页参数
	 * @param deptIds 生产线列表
	 * @param isPlanNode 计划节点
	 * @param isMonitoryPoint 监控节点
	 * @return
	 */
    PageUtils queryPage(Map<String, Object> params,Long[] deptIds,String isPlanNode,String isMonitoryPoint);

    // 模糊查找工序数据
 	public List<Map<String,Object>> getProcessList(Map<String,String> map);
 	
}

