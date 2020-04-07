package com.byd.zzjmes.modules.produce.service;

import java.util.List;
import java.util.Map;
/**
 * 委外发货
 * @author thw
 * @email 
 * @date 2019-09-03 15:12:08
 */
public interface OutsourcingService{
	
	/**
	 * 根据工厂、车间、线别、订单编号、批次编号、零部件号、工序获取下料明细信息及上工序产量信息
	 * @param condMap 
	 * 	"werks":"workshop": "line": "order_no": "zzj_plan_batch": 
		"zzj_no": "process_name" 工序编号  machine_plan_items_id
	 * @return
	 */
	List<Map<String,Object>> getOutsourcingMatInfo(Map<String, Object> condMap);
	
	public Map<String, Object> saveOutsourcingInfo(Map<String, Object> condMap);
	
}
