package com.byd.wms.business.modules.common.service;

import java.util.List;
import java.util.Map;

/**
 * 
 * @author 作者 : ren.wei3@byd.com 
 * @version 创建时间：2019年5月31日 上午9:42:01 
 *
 */
public interface WarehouseTasksService {

	public List<Map<String,Object>> searchBinForPutaway(List<Map<String,Object>> params);
	
	public String saveWHTask(List<Map<String,Object>> params);
	
	public List<Map<String,Object>> searchBinForPick (List<Map<String,Object>> params);
	
	/**
	 * 获取仓库任务
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> selectCoreWHTask(Map<String, Object> params);
	
	/**
	 * 修改仓库任务（确认、取消等操作）
	 * @param params 必须要传仓库号、仓库任务两个字段
	 */
	public int updateCoreWHTask(List<Map<String,Object>> params);
	
	/**
	 * 更新需求行状态
	 * @param params
	 * @return
	 */
	public int updateCoreWHTaskStatus(List<Map<String,Object>> params);
	
	/**
	 * 根据需求更新
	 * @param params
	 * @return
	 */
	public int updateCoreWHTaskByReq(List<Map<String,Object>> params);
	
	/**
	 * 保存拣配下架记录表
	 * @param updateList
	 */
	public void mergeWmsOutPicking(List<Map<String, Object>> updateList);
	
	/**
	 * 获取仓库任务
	 * @param params
	 * @return
	 */
	public List<Map<String, Object>> selectCoreWHTaskList(List<Map<String, Object>> list);
}
