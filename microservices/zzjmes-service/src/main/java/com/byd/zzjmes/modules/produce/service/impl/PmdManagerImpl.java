package com.byd.zzjmes.modules.produce.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.DateUtils;
import com.byd.utils.PageUtils;
import com.byd.utils.UserUtils;
import com.byd.utils.datasources.DataSourceNames;
import com.byd.utils.datasources.annotation.DataSource;
import com.byd.zzjmes.modules.produce.dao.PmdManagerDao;
import com.byd.zzjmes.modules.produce.service.PmdManagerService;

/**
 * 下料明细编辑
 * @author Yangke
 * @date 2019-09-05
 */
@Service("pmdManagerService")
public class PmdManagerImpl implements PmdManagerService {
	private Logger logger = LoggerFactory.getLogger(getClass());
	@Autowired
	private PmdManagerDao pmdManagerDao;
    @Autowired
    private UserUtils userUtils;

	@Override
	public List<Map<String, Object>> getPmdList(Map<String, Object> params) {
		return pmdManagerDao.getPmdList(params);
	}
	
	@Override
	public PageUtils getPmdListPage(Map<String, Object> params) {
		Page<Map<String, Object>> roderPage = new Page<Map<String, Object>>();	
		int pageSize = Integer.valueOf(params.get("pageSize") != null?params.get("pageSize").toString():"15");
		int pageNo=Integer.valueOf(params.get("pageNo") != null?params.get("pageNo").toString():"1");
		params.put("start", (pageNo - 1) * pageSize);
		roderPage.setRecords(pmdManagerDao.getPmdList(params));
		roderPage.setSize(pageSize);
		roderPage.setCurrent(pageNo);
		roderPage.setTotal(pmdManagerDao.getPmdListTotalCount(params));
		PageUtils page = new PageUtils(roderPage);		
		return page;
	}
	
	@Override
	public List<Map<String, Object>> getPmdMapNoList(Map<String, Object> params){
		return pmdManagerDao.getPmdMapNoList(params);
	}
	
	@DataSource(name = DataSourceNames.THIRD)
	@Override
	public List<Map<String, Object>> getMasterDataMapNoList(Map<String, Object> params){
		return pmdManagerDao.getMasterDataMapNoList(params);
	}
	
	@Override
	@Transactional
	public int editSubcontractingList(Map<String, Object> params) {
		Map<String,Object> currentUser = userUtils.getUser();
		String editStr = params.get("editStr").toString();
		logger.info("-->editStr : " + editStr);
		String[] SubcontractingArray = editStr.split(";");
		for(int i=0;i<SubcontractingArray.length;i++) {
			String[] sub = SubcontractingArray[i].split(",");
			logger.info(sub[0]+"|"+sub[1]+"|"+sub[2]+"|"+sub[3]+"|"+sub[4]+"|"+sub[5]);
			Map<String, Object> itemMap = new HashMap <String, Object>();
			itemMap.put("id", sub[0]);
			itemMap.put("outsourcing_quantity", sub[1]);
			itemMap.put("weight", sub[2]);
			itemMap.put("total_weight", sub[3]);
			itemMap.put("quantity", sub[4]);
			itemMap.put("memo", sub[5]);
			itemMap.put("edit_date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			itemMap.put("editor",currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("FULL_NAME").toString());
			pmdManagerDao.editSubcontracting(itemMap);
		}
		return 0;
	}

	@Override
	public int deletePmdList(Map<String, Object> params){
		
		String DELIDS = "";
		if(params.get("del_ids") != null)DELIDS = params.get("del_ids").toString();
		//删除	147343,148165,
		if(!"".equals(DELIDS)) {
			//批量删除大量数据时，每次处理100行数据
			DELIDS = DELIDS.substring(0, DELIDS.length()-1);
			String[] ids = DELIDS.split(",");
			int g = 100;
			String idstr = "";
			for(int i=0;i<ids.length;i++) {
				if(i%g == 0)idstr = "";
				if(i%g < g) {
					idstr += ids[i] + ((i%g == g-1||i==ids.length-1)?"":",");
				}
				if(i%g == g-1 || i==ids.length-1) {
					//System.out.println("---->idstr:" + idstr);
					Map<String, Object> delitemMap = new HashMap <String, Object>();
					delitemMap.put("ids", idstr);
					pmdManagerDao.delPmdItem(delitemMap);
				}
			}
		}

		return 0;
	}

	@Override
	@Transactional
	public int editPmdList(Map<String, Object> params) {
		Map<String,Object> currentUser = userUtils.getUser();
		String ORDER_NO = params.get("ORDER_NO").toString();
		String WERKS = params.get("WERKS").toString();
		String WORKSHOP = params.get("WORKSHOP").toString();
		String LINE = params.get("LINE").toString();		
		
		JSONArray jarr = JSON.parseArray(params.get("ARRLIST").toString());
		for(int i=0;i<jarr.size();i++){
			JSONObject outData=  jarr.getJSONObject(i);
			Map<String, Object> itemMap = new HashMap <String, Object>();
			String id = outData.getString("i_id");
			logger.info("-->editPmdList " + i + " WERKS:" + WERKS + " ORDER_NO:" + ORDER_NO + " item_id:" + id);
			itemMap.put("id", id);
			itemMap.put("order_no", ORDER_NO);
			itemMap.put("werks", WERKS);
			itemMap.put("workshop", WORKSHOP);
			itemMap.put("line", LINE);
			//itemMap.put("pmd_head_id",outData.getString("h_id"));
			itemMap.put("change_from",outData.getString("change_from"));
			itemMap.put("no",outData.getString("no"));
			itemMap.put("material_no",outData.getString("material_no"));
			itemMap.put("zzj_no",outData.getString("zzj_no"));
			itemMap.put("zzj_name",outData.getString("zzj_name"));
			itemMap.put("mat_description",outData.getString("mat_description"));
			itemMap.put("mat_type",outData.getString("mat_type"));
			itemMap.put("specification",outData.getString("specification"));
			itemMap.put("filling_size",outData.getString("filling_size"));
			itemMap.put("cailiao_type",outData.getString("cailiao_type"));
			itemMap.put("assembly_position",outData.getString("assembly_position"));
			itemMap.put("process",outData.getString("process"));
			itemMap.put("process_name",outData.getString("process_name"));
			itemMap.put("process_sequence",outData.getString("process_sequence"));
			itemMap.put("process_flow",outData.getString("process_flow"));
			
			String processFlowStr = outData.getString("process_flow");
			int lastIndex = processFlowStr.lastIndexOf("-");
			String process_product = "";
			String process_guide = "";
			if(lastIndex <= 0 ) {
				process_product = processFlowStr;
				process_guide = "";
			}else {
				process_product = processFlowStr.substring(0, processFlowStr.lastIndexOf("-"));
				process_guide = processFlowStr.substring(processFlowStr.lastIndexOf("-")+1);
				//根据用户要求，当最后一道工序为 电泳或进仓时，暂不截取
				if("电泳".equals(process_guide) || "进仓".equals(process_guide)) {
					process_product = processFlowStr;
					process_guide = "";
				}
			}
			itemMap.put("process_product",process_product);
			itemMap.put("process_guide",process_guide);
			
			itemMap.put("process_time",outData.getString("process_time"));
			itemMap.put("process_machine",outData.getString("process_machine"));
			itemMap.put("subcontracting_type",outData.getString("subcontracting_type"));
			itemMap.put("aperture",outData.getString("aperture"));
			itemMap.put("accuracy_demand",outData.getString("accuracy_demand"));
			itemMap.put("surface_treatment",outData.getString("surface_treatment"));
			itemMap.put("weight",outData.getString("weight"));
			itemMap.put("use_workshop",outData.getString("use_workshop"));
			itemMap.put("unit",outData.getString("unit"));
			itemMap.put("quantity",outData.getString("quantity"));
			itemMap.put("loss",outData.getString("loss"));
			itemMap.put("total_weight",outData.getString("total_weight"));
			itemMap.put("memo",outData.getString("memo"));
			itemMap.put("processflow_memo",outData.getString("processflow_memo"));
			itemMap.put("section",outData.getString("section"));
			itemMap.put("maiban",outData.getString("maiban"));
			itemMap.put("banhou",outData.getString("banhou"));
			itemMap.put("filling_size_max",outData.getString("filling_size_max"));
			itemMap.put("sap_mat",outData.getString("sap_mat"));
			itemMap.put("change_description",outData.getString("change_description"));
			itemMap.put("change_subject",outData.getString("change_subject"));
			itemMap.put("change_type",outData.getString("change_type"));
			itemMap.put("edit_date",DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
			itemMap.put("editor",currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("FULL_NAME").toString());
			itemMap.put("creator",outData.getString("creator"));
			itemMap.put("creat_date",outData.getString("creat_date"));
			int pmdItemId = 0;
			if(id==null) {
				id = "0";
			}
			if(id.equals("0")) {
				//新增PMD_ITEM
				itemMap.put("operate_type","00");
				pmdItemId = pmdManagerDao.addPmdItem(itemMap);
				itemMap.put("id", pmdItemId);
			}else {
				//编辑PMD_ITEM  
				itemMap.put("operate_type","01");
				pmdItemId = Integer.valueOf(id);
				pmdManagerDao.editPmdItem(itemMap);
			}
			logger.info("-->pmdItemId ： " + pmdItemId);
			pmdManagerDao.delPmdEnc(itemMap);
			if(outData.getString("change_from") !=null && !outData.getString("change_from").equals("")) {
				//  1-30:0.5;31-100:0.8;
				List<Map<String, Object>> ecnList = new ArrayList<Map<String, Object>>();
				String[] ecnStrArray=outData.getString("change_from").split(";");
				for(int j = 0; j < ecnStrArray.length; j++){
					Map<String, Object> pmdEcn = new HashMap <String, Object>();
					pmdEcn.put("zzj_pmd_items_id", pmdItemId);
					pmdEcn.put("from_no", ecnStrArray[j].substring(0, ecnStrArray[j].indexOf("-")));
					pmdEcn.put("to_no", ecnStrArray[j].substring(ecnStrArray[j].indexOf("-")+1,ecnStrArray[j].indexOf(":")));
					pmdEcn.put("quantity", ecnStrArray[j].substring(ecnStrArray[j].indexOf(":")+1));
					pmdEcn.put("creator", currentUser.get("STAFF_NUMBER").toString() +":"+ currentUser.get("FULL_NAME").toString());
					pmdEcn.put("create_date", DateUtils.format(new Date(),DateUtils.DATE_TIME_PATTERN));
					ecnList.add(pmdEcn);
				}
				pmdManagerDao.addPmdEnc(ecnList);
			}
			
			//新增 PMD_ITEM_H
			pmdManagerDao.addPmdItemHistory(itemMap);
		}
		return 0;
	}
	
	@Override
	public List<Map<String, Object>> getProductionExceptionList(Map<String, Object> params){
		return pmdManagerDao.getProductionExceptionList(params);
	}

	@Override
	public PageUtils getProductionExceptionPage(Map<String, Object> params) {
		Page<Map<String, Object>> exceptionPage = new Page<Map<String, Object>>();	
		int pageSize = Integer.valueOf(params.get("pageSize") != null?params.get("pageSize").toString():"15");
		int pageNo=Integer.valueOf(params.get("pageNo") != null?params.get("pageNo").toString():"1");
		params.put("start", (pageNo - 1) * pageSize);
		exceptionPage.setRecords(pmdManagerDao.getProductionExceptionList(params));
		exceptionPage.setSize(pageSize);
		exceptionPage.setCurrent(pageNo);
		exceptionPage.setTotal(pmdManagerDao.getProductionExceptionCount(params));
		PageUtils page = new PageUtils(exceptionPage);
		return page;
	}
	
	@Override
	public PageUtils getSubcontractingPage(Map<String, Object> params) {
		Page<Map<String, Object>> subcontractingPage = new Page<Map<String, Object>>();	
		int pageSize = Integer.valueOf(params.get("pageSize") != null?params.get("pageSize").toString():"15");
		int pageNo=Integer.valueOf(params.get("pageNo") != null?params.get("pageNo").toString():"1");
		params.put("start", (pageNo - 1) * pageSize);
		subcontractingPage.setRecords(pmdManagerDao.getSubcontractingList(params));
		subcontractingPage.setSize(pageSize);
		subcontractingPage.setCurrent(pageNo);
		subcontractingPage.setTotal(pmdManagerDao.getSubcontractingCount(params));
		
		PageUtils page = new PageUtils(subcontractingPage);
		return page;
	}

	@Override
	public List<Map<String, Object>> getSubcontractingItemList(Map<String, Object> params){
		return pmdManagerDao.getSubcontractingItemList(params);
	}
	@Override
	public int editSubcontractingItem(Map<String, Object> params){
		return pmdManagerDao.editSubcontractingItem(params);
	}

	@Override
	public PageUtils getSubcontractingHeadPage(Map<String, Object> params){
		Page<Map<String, Object>> subcontractingPage = new Page<Map<String, Object>>();	
		int pageSize = Integer.valueOf(params.get("pageSize") != null?params.get("pageSize").toString():"15");
		int pageNo=Integer.valueOf(params.get("pageNo") != null?params.get("pageNo").toString():"1");
		params.put("start", (pageNo - 1) * pageSize);
		subcontractingPage.setRecords(pmdManagerDao.getSubcontractingHeadList(params));
		subcontractingPage.setSize(pageSize);
		subcontractingPage.setCurrent(pageNo);
		subcontractingPage.setTotal(pmdManagerDao.getSubcontractingHeadCount(params));
		
		PageUtils page = new PageUtils(subcontractingPage);
		return page;
	}
	
	@Override
	public List<Map<String, Object>> getSubcontractingList(Map<String, Object> params){
		return pmdManagerDao.getSubcontractingList(params);
	}
	
	@Override
	public int exceptionConfirm(Map<String, Object> params) {
		pmdManagerDao.exceptionConfirm(params);
		return 0;
	}
	
	@Override
	public int getPmdProcessPlanCount(Map<String, Object> params) {
		return pmdManagerDao.getPmdProcessPlanCount(params);
	}
}
