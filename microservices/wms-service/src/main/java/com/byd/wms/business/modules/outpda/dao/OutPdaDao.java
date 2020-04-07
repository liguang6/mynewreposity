package com.byd.wms.business.modules.outpda.dao;

import java.util.List;
import java.util.Map;

public interface OutPdaDao {
	List<Map<String, Object>> getTaskList(Map<String, Object> params);
	int getTaskCount(Map<String, Object> params);
	/**
	 * 根据需求号获取超发标识
	 * @param params
	 * @return
	 */
	public String getChaFaBiaoShi(Map<String, Object> params);
	/**
	 * 根据条码号、料号、批次、储位获取状态为07、08的条码信息
	 * 
	 * @param params(matnr:料号、batch:批次、bin_code:储位、label_no;)
	 * @return
	 */
	Map<String, Object> getLabelInfo(Map<String, Object> params);
	
//	int getLabelCount(Map<String, Object> params);
	/**
	 * 更新仓库任务表WMS_CORE_WH_TASK.CONFIRM_QUANTITY(实拣数量)、WT_STATUS(变为02)
	 * 
	 * @param params(TASK_NUM:任务号、CONFIRM_QUANTITY:实拣数量)
	 */
	public void updateTask(Map<String, Object> params);
	
	/**
	 * 更新仓库任务拣配的条码表信息，更新WMS_CORE_LABEL.LABEL_STATUS(整箱拣配的更新为09，半箱拣配状态不更新)
	 * @param params(LABEL_NO:标签号、CONFIRM_QUANTITY:实拣数量)
	 */
	public void updateLabel(Map<String, Object> params);
	
	/**
	 * 将任务对应的拣配明细保存
	 * @param params(TASK_NUM:任务号、LABEL_NO:标签号、CONFIRM_QUANTITY:实拣数量)
	 */
	public void insertJianPeiLabel(Map<String, Object> params);
}
