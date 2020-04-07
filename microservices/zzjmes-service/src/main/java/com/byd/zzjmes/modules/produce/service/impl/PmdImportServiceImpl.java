package com.byd.zzjmes.modules.produce.service.impl;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.byd.zzjmes.modules.produce.dao.PmdImportDao;
import com.byd.zzjmes.modules.produce.service.PmdImportService;
/***
 * @Desc 下料明细导入
 * @author thw
 * @date 2019-09-03 16:12:06
 */
@Service("pmdImportService")
public class PmdImportServiceImpl implements PmdImportService{
	@Autowired
	private PmdImportDao pmdImportDao;

	@Override
	public Map<String,Object> queryPmdInfo(Map<String,Object> condMap){
		Map<String,Object> resultMap = new HashMap<String, Object>();
		Map<String,Object> pmdHead = pmdImportDao.queryPmdHeader(condMap);
		int header_id=0;
		if(pmdHead!=null){
			//查询下料明细
			header_id=Integer.parseInt(pmdHead.get("id").toString());
			condMap.put("pmd_head_id", header_id);
			List<Map<String,Object>> pmdItems = pmdImportDao.queryPmdItems(condMap);
			resultMap.put("pmd_head_id", header_id);
			resultMap.put("pmdHead", pmdHead);
			resultMap.put("pmdItems", pmdItems);
			resultMap.put("pmdItemsSize", pmdItems.size());
			resultMap.put("production_qty", pmdHead.get("production_qty"));
		}
		return resultMap;
	}
	
	@Override
	@Transactional
	public int savePmdInfo(Map<String, Object> condMap) {
		String pmd_head_id = condMap.get("pmd_head_id") == null?"0":condMap.get("pmd_head_id").toString();
		if(!pmd_head_id.equals("0")){
			//存在，在已导入的下料明细清单上做增删改查
			int pmd_header_id = Integer.valueOf(pmd_head_id);
			condMap.put("pmd_head_id", pmd_header_id);
			//新增数据，需新增明细表和明细记录表
			List<Map<String,Object>> add_list = (List<Map<String,Object>>)condMap.get("pmd_list");
			if(add_list.size()>0){
				pmdImportDao.addPmdDetails(condMap);
				pmdImportDao.addPmdHistoryDetails(condMap);
			}

		}else{
			//不存在，创建下料明细抬头数据
			pmdImportDao.addPmdHeader(condMap);
			int pmd_header_id = Integer.valueOf(condMap.get("id").toString());
			condMap.put("pmd_head_id", pmd_header_id);
			//新增数据，需新增明细表和明细记录表
			List<Map<String,Object>> add_list = (List<Map<String,Object>>)condMap.get("pmd_list");
			if(add_list.size()>0){
				pmdImportDao.addPmdDetails(condMap);
				pmdImportDao.addPmdHistoryDetails(condMap);
			}
		}
		return 0;
	}

}
