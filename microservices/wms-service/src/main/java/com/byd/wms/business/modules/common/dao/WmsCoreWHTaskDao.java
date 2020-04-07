package com.byd.wms.business.modules.common.dao;

import java.util.List;
import java.util.Map;

public interface WmsCoreWHTaskDao{

	/**
	 * 获取仓库任务
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> selectCoreWHTask(Map<String, Object> params);
	
	/**
	 * 保存仓库任务
	 * @param params
	 */
	void insertCoreWHTask(List<Map<String,Object>> params);
	
	/**
	 * 修改仓库任务（确认、取消等操作）
	 * @param params
	 */
	int updateCoreWHTask(List<Map<String,Object>> params);
	
	/**
	 * 更新需求行状态
	 * @param params
	 * @return
	 */
	int updateCoreWHTaskStatus(List<Map<String,Object>> params);
	
	/**
	 * 修改仓库任务（确认、取消等操作）
	 * @param params
	 */
	int updateCoreWHTaskByReq(List<Map<String,Object>> params);
	
	/**
	 * 保存拣配下架记录表
	 * @param updateList
	 */
	void mergeWmsOutPicking(List<Map<String, Object>> updateList);
	
	/**
	 * 获取仓库任务
	 * @param params
	 * @return
	 */
	List<Map<String, Object>> selectCoreWHTaskList(List<Map<String, Object>> list);
}
