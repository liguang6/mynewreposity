package com.byd.zzjmes.modules.produce.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.byd.utils.R;
import com.byd.zzjmes.modules.produce.dao.BatchPlanDao;
import com.byd.zzjmes.modules.produce.service.BatchPlanService;
/***
 * @Desc 批次计划
 * @author thw
 * @date 2019-09-03 16:12:06
 */
@Service("batchPlanService")
public class BatchPlanServiceImpl implements BatchPlanService{
	@Autowired
	private BatchPlanDao batchPlanDao;

	@Override
	public List<Map<String,Object>> getBatchList(Map<String, Object> condMap) {
		return batchPlanDao.getBatchList(condMap);
	}
	
	@Override
	public List<Map<String,Object>> getOrderBatchList(Map<String, Object> condMap){
		return batchPlanDao.getOrderBatchList(condMap);
	}
	
	@Override
	public Map<String,Object> getBacthPlanById(String id){
		return batchPlanDao.getBacthPlanById(id);
	}
	
	
	
	@Override
	@Transactional
	public int addBatchPlan(Map<String,Object> condMap){
		return  batchPlanDao.addBatchPlan(condMap);
	}
	
	@Override
	@Transactional
	public R editBatchPlan(Map<String,Object> params){
		String id = params.get("id").toString();
		String quantity = params.get("quantity").toString();
		String old_quantity = params.get("old_quantity").toString();
		String change_content = params.get("change_content")==null?"":params.get("change_content").toString();
		if (!quantity.equals(old_quantity)) {
			params.put("change_content", change_content+"批次数量由" + old_quantity + "调整为" + quantity + ";");
		}
		
		Map<String,Object> oldBatch = batchPlanDao.getBacthPlanById(id);
		
		if (!quantity.equals(old_quantity)) {
			int from_no = 0;
			int to_no = 0;
			List<Map<String,Object>> batchPlanList = batchPlanDao.getOrderBatchList(oldBatch);
			for (Map<String, Object> map : batchPlanList) {
				if( map.get("id").toString().equals(params.get("id").toString()) ) {
					from_no = Integer.valueOf(map.get("from_no").toString());
					to_no = from_no + Integer.valueOf( params.get("quantity").toString() ) -1;
					params.put("from_no", from_no);
					params.put("to_no", to_no);
					
					try {
						int result = batchPlanDao.editBatchPlan(params);
					} catch (Exception e) {
						return R.error("修改失败！" + e.getMessage());
					}
				}else {
					if(from_no>0) {
						from_no = to_no +1;
						to_no = from_no+Integer.valueOf( map.get("quantity").toString()) -1;
						
						map.put("from_no", from_no);
						map.put("to_no", to_no);
						try {
							int result = batchPlanDao.editBatchPlan(map);
						} catch (Exception e) {
							return R.error("修改失败！" + e.getMessage());
						}
					}
				}
			}
		}else {
			int result = batchPlanDao.editBatchPlan(params);
		}
		
		return  R.ok();
	}
	
	@Override
	public List<Map<String,Object>> getMachinePlanByBatch(Map<String, Object> condMap){
		return batchPlanDao.getMachinePlanByBatch(condMap);
	}
	
	
	@Override
	@Transactional
	public R deleteBatchPlan(String id){
		String idStr = id+"";
		
		Map<String,Object> oldBatch = batchPlanDao.getBacthPlanById(id);
		
		int from_no = 0;
		int to_no = 0;
		int delete_from_no = 0;
		List<Map<String,Object>> batchPlanList = batchPlanDao.getOrderBatchList(oldBatch);
		for (Map<String, Object> map : batchPlanList) {
			if( map.get("id").toString().equals(idStr) ) {
				delete_from_no = Integer.valueOf(map.get("from_no").toString());
				try {
					int result = batchPlanDao.deleteBatchPlan(Integer.valueOf(id));
				} catch (Exception e) {
					return R.error("删除失败！" + e.getMessage());
				}
			}else {
				if(from_no >0) {
					from_no = to_no+1;
				}else {
					from_no = 1;
				}
				to_no = from_no+Integer.valueOf( map.get("quantity").toString())-1;
				
				map.put("from_no", from_no);
				map.put("to_no", to_no);
				delete_from_no = 0;
				try {
					int result = batchPlanDao.editBatchPlan(map);
				} catch (Exception e) {
					return R.error("修改失败！" + e.getMessage());
				}
			}
		}
		
		return  R.ok();
	}
	
}
