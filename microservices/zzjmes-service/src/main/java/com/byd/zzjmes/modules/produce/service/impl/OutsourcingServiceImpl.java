package com.byd.zzjmes.modules.produce.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.byd.zzjmes.modules.produce.dao.OutsourcingDao;
import com.byd.zzjmes.modules.produce.service.OutsourcingService;
import com.byd.zzjmes.modules.product.dao.ZzjJTOperationDao;
/***
 * @Desc 委外加工
 * @author thw
 * @date 2019-09-03 16:12:06
 */
@Service("outsourcingService")
public class OutsourcingServiceImpl implements OutsourcingService{
	@Autowired
	private OutsourcingDao outsourcingDao;
	@Autowired
	private ZzjJTOperationDao zzjJTOperationDao;
	
	
	 /**
	 * 根据工厂、车间、线别、订单编号、批次编号、零部件号、工序获取下料明细信息及上工序产量信息
	 * @param condMap 
	 * 	"werks":"workshop": "line": "order_no": "zzj_plan_batch": 
		"zzj_no": "process_name" 工序编号  machine_plan_items_id
	 * @return
	 */
	@Override
	public List<Map<String,Object>> getOutsourcingMatInfo(Map<String, Object> condMap){
		if(null == condMap.get("machine_plan_items_id") || StringUtils.isEmpty(condMap.get("machine_plan_items_id"))) {
			return outsourcingDao.getOutsourcingMatInfo(condMap);
		}else {
			return outsourcingDao.getOutsourcingMatInfoByMachinePlan(condMap);
		}
	}
	
	@Override
	@Transactional
	public Map<String,Object> saveOutsourcingInfo(Map<String, Object> condMap) {
		Map<String, Object> rtn = new HashMap<String, Object>();
		String msg = "";
		//获取委外单号
		String outsourcing_no = this.getNextOutsourcingNoByYear(condMap);
		condMap.put("outsourcing_no", outsourcing_no);
		List<Map<String,Object>> matInfoList = (List<Map<String,Object>>)condMap.get("matInfoList");
		
		//机台清单
		List<Map<String,Object>> machineList = outsourcingDao.getMachineByProcess(condMap);
		
		//插入委外虚拟机台计划
		List<Map> item_list = new ArrayList<>();
		for (Map<String, Object> map : matInfoList) {
			String machine = "";
			for (Map machineMap : machineList) {
				if(machineMap.get("process_code").equals(map.get("outsourcing_process"))) {
					machine = machineMap.get("machine_code").toString();
					break;
				}
			}
			if(StringUtils.isEmpty(machine)) {
				if(msg.indexOf("工序："+map.get("outsourcing_process")+"未分配机台，无法创建机台计划！")<0 ) {
					msg += "工序："+map.get("outsourcing_process")+"未分配机台，无法创建机台计划！";
				}
			}
			map.put("machine", machine);
			map.put("outsourcing_no", outsourcing_no);
			item_list.add(map);
		}
		if(StringUtils.isEmpty(msg)) {
			//新增机台计划
			
			outsourcingDao.saveJTPlanItems(item_list);
			
			//新增数据
			condMap.put("id", 0);
			int id = outsourcingDao.addSubcontractingHead(condMap);
			condMap.put("outsourcing_head_id", condMap.get("id"));
			if(matInfoList.size()>0){
				outsourcingDao.addSubcontractingItems(condMap);
			}
		}
		rtn.put("msg", msg);
		rtn.put("matInfoList", matInfoList);
		return rtn;
	}
	
	private String getNextOutsourcingNoByYear(Map<String, Object> params){
		String business_date = params.get("business_date").toString().substring(0, 4);
		String outsourcing_no = outsourcingDao.queryMaxOutsourcingNo(params);
		String new_outsourcing_no = "";
		if (outsourcing_no == null){
			return "WN" + business_date + "00001";
		}
		int serial = Integer.parseInt(outsourcing_no.substring(6, 12)) + 1;
		new_outsourcing_no = outsourcing_no.substring(0, 7) + pixStrZero(String.valueOf(serial),5);
		return new_outsourcing_no;
	}
	
	protected String pixStrZero(String str,int length) {
		int pxnum = length-str.length();
		if(pxnum >0) str = String.format("%0"+(pxnum)+"d", 0) + str;    
		if(pxnum <0) str = str.substring(0, length);    
	    //System.out.println(str);
		return str;
	}
}
