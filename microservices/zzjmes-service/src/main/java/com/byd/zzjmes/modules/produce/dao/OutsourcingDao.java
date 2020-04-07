package com.byd.zzjmes.modules.produce.dao;

import java.util.List;
import java.util.Map;

/**
 * 委外发货
 * @author thw
 * @email 
 * @date 2019-09-03 15:12:08
 */
public interface OutsourcingDao{
	
	/**
	 * 根据工厂、车间、线别、订单编号、批次编号、零部件号、工序获取下料明细信息及上工序产量信息
	 * @param condMap 
	 * 	"werks":"workshop": "line": "order_no": "zzj_plan_batch": 
		"zzj_no": "process_name" 工序编号
	 * @return
	 */
	List<Map<String,Object>> getOutsourcingMatInfo(Map<String, Object> condMap);
	
	/**
	 * 根据工厂、车间、线别、订单编号、批次编号、零部件号、工序、机台计划行项目ID获取下料明细信息及上工序产量信息
	 * @param condMap
	 *  "werks":"workshop": "line": "order_no": "zzj_plan_batch": 
		"zzj_no": "process_name" 工序编号  machine_plan_items_id
	 * @return
	 */
	List<Map<String,Object>> getOutsourcingMatInfoByMachinePlan(Map<String, Object> condMap);
	
	/**
	 * 根据工厂、车间、线别工序代码获取分配的机台信息
	 * @param condMap
	 * @return
	 */
	List<Map<String,Object>> getMachineByProcess(Map<String, Object> condMap);
	
	String queryMaxOutsourcingNo(Map<String, Object> condMap);
	
	//ZZJ_SUBCONTRACTING_HEAD
	
	public int addSubcontractingHead(Map<String, Object> condMap);
	public int addSubcontractingItems(Map<String, Object> condMap);
	
	void saveJTPlanItems(List<Map> item_list);
	
}
