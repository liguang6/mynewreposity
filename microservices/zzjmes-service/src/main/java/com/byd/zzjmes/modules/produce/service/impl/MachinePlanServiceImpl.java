package com.byd.zzjmes.modules.produce.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.zzjmes.modules.produce.dao.MachinePlanDao;
import com.byd.zzjmes.modules.produce.service.MachinePlanService;
/***
 * @Desc 机台计划
 * @author tangj
 * @date 2019-09-03 16:12:06
 */
@Service("machinePlanService")
public class MachinePlanServiceImpl implements MachinePlanService{
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private MachinePlanDao machinePlanDao;
	@Override
	public List<Map<String, Object>> getMachinePlanList(Map<String, Object> param) {
		return machinePlanDao.getMachinePlanList(param);
	}
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=machinePlanDao.getMachinePlanListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("start", start);params.put("limit", end);
		List<Map<String,Object>> list=machinePlanDao.getMachinePlanList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		if(page.getRecords().size()==0&&page.getCurrent()!=1) {
        	pageNo="1";
        	if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
    			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
    			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
    		}else {
    			end=count;
    		}
    		params.put("start", start);params.put("limit", end);
        	list=machinePlanDao.getMachinePlanList(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
	}
	@Override
	@Transactional
	public void save(Map<String, Object> params) {
		params.put("cur_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		Map<String,Object> headMap=machinePlanDao.getHeadByMap(params);
		String header_id = headMap!=null ? headMap.get("id").toString() : "0";
		if(!header_id.equals("0")){
			//在订单、批次、工厂、车间、线别维度，已存在抬头信息
			int machine_plan_head_id = Integer.valueOf(header_id);
			params.put("machine_plan_head_id", machine_plan_head_id);
			//新增数据，需新增明细表和明细记录表
			List<Map<String,Object>> add_list = (List<Map<String,Object>>)params.get("add_list");
			if(add_list!=null && add_list.size()>0){
				machinePlanDao.saveItems(params);
				params.put("history_list", add_list);
				machinePlanDao.saveItemsHistory(params);
			}
			//修改数据
			List<Map<String,Object>> modify_list = (List<Map<String,Object>>)params.get("modify_list");
			if(modify_list!=null && modify_list.size()>0){
				machinePlanDao.updateItems(params);
				params.put("history_list", modify_list);
				machinePlanDao.saveItemsHistory(params);
			}
		}else{
			//不存在，先保存抬头数据
			machinePlanDao.saveHead(params);
			int machine_plan_head_id = Integer.valueOf(params.get("id").toString());
			params.put("machine_plan_head_id", machine_plan_head_id);
			//新增数据，需新增明细表和明细记录表
			List<Map<String,Object>> add_list = (List<Map<String,Object>>)params.get("add_list");
			if(add_list.size()>0){
				machinePlanDao.saveItems(params);
				params.put("history_list", add_list);
				machinePlanDao.saveItemsHistory(params);
			}
		}
	}
	@Override
	@Transactional
	public void del(Map<String, Object> params) {
		params.put("cur_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
		//删除数据
		List<Map<String,Object>> delete_list = (List<Map<String,Object>>)params.get("delete_list");
		if(delete_list!=null && delete_list.size()>0){
			machinePlanDao.deleteItems(delete_list);
			params.put("history_list", delete_list);
			machinePlanDao.saveItemsHistory(params);
		}
	}

	@Override
	public Map<String, Object> getPlanByMap(Map<String, Object> params) {
		return machinePlanDao.getPlanByMap(params);
	}

	@Override
	public int getZzjPlanBatchQuantity(Map<String, Object> params) {
		return machinePlanDao.getZzjPlanBatchQuantity(params);
	}

	@Override
	public List<Map<String, Object>> getExistPmdList(Map<String, Object> params) {
		return machinePlanDao.getExistPmdList(params);
	}

	@Override
	public List<Map<String, Object>> checkMachinePlanList(Map<String, Object> params) {
		return machinePlanDao.checkMachinePlanList(params);
	}

	@Override
	public List<String> checkMachine(Map<String, Object> params) {
		return machinePlanDao.checkMachine(params);
	}

	@Override
	public List<Map<String,Object>> getOutputRecords(Map<String,Object> params){
		return machinePlanDao.getOutputRecords(params);
	}
	@Override
	public List<Map<String, Object>> getTemplateData(Map<String, Object> params) {
		return machinePlanDao.getTemplateData(params);
	}
}
