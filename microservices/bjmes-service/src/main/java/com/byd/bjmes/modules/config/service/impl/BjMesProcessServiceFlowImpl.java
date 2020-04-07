package com.byd.bjmes.modules.config.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.bjmes.modules.config.dao.BjMesProcessFlowDao;
import com.byd.bjmes.modules.config.service.BjMesProcessFlowService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/** 
 * @author 作者 tangjin 
 * @version 创建时间：2019年10月30日 下午5:00:00 
 * 类说明 加工流程
 */
@Service("bjMesProcessFlowService")
public class BjMesProcessServiceFlowImpl implements BjMesProcessFlowService{

	@Autowired
	private BjMesProcessFlowDao processFlowDao;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"-1";
		int start = 0;
		int limit=0;
		int count = processFlowDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			limit = Integer.valueOf(pageSize);
		}else {
			limit=count;
		}
		params.put("start", start);params.put("limit", limit);
		List<Map<String,Object>> list=processFlowDao.getListByPage(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		if(page.getRecords().size()==0&&page.getCurrent()!=1) {
        	pageNo="1";
        	if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
    			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
    			limit = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
    		}else {
    			limit=count;
    		}
    		params.put("start", start);params.put("limit", limit);
        	list=processFlowDao.getListByPage(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
	}
	@Override
	public void updateProcessFlow(Map<String, Object> params) {
		processFlowDao.updateProcessFlow(params);
	}
	@Override
	public List<Map<String, Object>> getNodeList(Map<String, Object> params) {
		return processFlowDao.getNodeList(params);
	}
	@Override
	@Transactional
	public void saveProcessFlow(Map<String, Object> params) {

		Gson gson=new Gson();

		List<Map<String,Object>> detail_list=new ArrayList<Map<String,Object>>();
		
		if(params.get("data_list")!=null && !params.get("data_list").toString().equals("")) {
			detail_list =
				gson.fromJson((String) params.get("data_list"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		}
		params.put("list", detail_list);
		Map<String,Object> checkMap=processFlowDao.checkProcessFlow(params);
		if(!StringUtils.isEmpty(checkMap)){
			throw new RuntimeException("节点代码:" + checkMap.get("process_flow_code") + ":记录在系统中已存在");
		}
        processFlowDao.saveProcessFlow(params);

	}

	@Override
	@Transactional
	public void saveNode(Map<String, Object> params) {

		Gson gson=new Gson();

		List<Map<String,Object>> detail_list=new ArrayList<Map<String,Object>>();

		List<Map<String,Object>> add_list=new ArrayList<Map<String,Object>>();

		List<Map<String,Object>> update_list=new ArrayList<Map<String,Object>>();
		
		if(params.get("data_list")!=null && !params.get("data_list").toString().equals("")) {
			detail_list =
				gson.fromJson((String) params.get("data_list"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		}

		add_list = detail_list.stream()
		.filter((Map<String,Object> m) -> StringUtils.isEmpty(m.get("id")))
		.collect(Collectors.toList());

		update_list = detail_list.stream()
		.filter((Map<String,Object> m) -> !StringUtils.isEmpty(m.get("id")))
		.collect(Collectors.toList());

		if(update_list.size()==0){
			String process_flow_code=params.get("process_flow_code").toString();
			processFlowDao.delByProcessFlowCode(process_flow_code);
		}else{
			params.put("update_list", update_list);
			processFlowDao.updateNodeInfo(params);
		}
		
		if(add_list.size()>0){
			params.put("add_list", add_list);
			processFlowDao.saveNodeInfo(params);
		}
	}

	@Override
	@Transactional
	public void deleteNode(Map<String, Object> params) {

		Gson gson=new Gson();
        long id=Long.parseLong(params.get("id").toString());

		List<Map<String,Object>> detail_list=new ArrayList<Map<String,Object>>();
		
		if(params.get("data_list")!=null && !params.get("data_list").toString().equals("")) {
			detail_list =
				gson.fromJson((String) params.get("data_list"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		}
		// 1. 根据id删除记录
		processFlowDao.delById(id);
        // 2. 更新兄弟加工节点的排序号 
		params.put("detail_list", detail_list);
		processFlowDao.updateNodeSeq(params);
	}

	@Override
	public void deleteProcessFlow(String process_flow_code) {
		processFlowDao.delByProcessFlowCode(process_flow_code);
	}

	
}
