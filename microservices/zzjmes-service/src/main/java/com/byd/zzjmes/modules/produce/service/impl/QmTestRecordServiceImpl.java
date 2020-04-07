package com.byd.zzjmes.modules.produce.service.impl;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.zzjmes.modules.produce.dao.PmdManagerDao;
import com.byd.zzjmes.modules.produce.dao.QmTestRecordDao;
import com.byd.zzjmes.modules.produce.service.QmTestRecordService;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
/***
 * 自制件品质检验
 * @author tangj
 * @email 
 * @date 2019-09-16 10:12:08
 */
@Service("qmTestRecordService")
public class QmTestRecordServiceImpl  implements QmTestRecordService {
	@Autowired
	private QmTestRecordDao qmTestRecordDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=qmTestRecordDao.getHeadCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("start", start);params.put("limit", end);
		List<Map<String,Object>> list=qmTestRecordDao.getHeadList(params);
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
        	list=qmTestRecordDao.getHeadList(params);
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
		
		Gson gson=new Gson();
		List<Map<String,Object>> pro_detail_list=new ArrayList<Map<String,Object>>();
		
		List<Map<String,Object>> qc_detail_list=new ArrayList<Map<String,Object>>();
		
		if(params.get("pro_detail_list")!=null && !params.get("pro_detail_list").toString().equals("")) {
			pro_detail_list =
				gson.fromJson((String) params.get("pro_detail_list"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		}
		if(params.get("qc_detail_list")!=null && !params.get("qc_detail_list").toString().equals("")) {
			qc_detail_list =
				gson.fromJson((String) params.get("qc_detail_list"),new TypeToken<List<Map<String,Object>>>() {}.getType());
		}
		
		int zzj_qm_test_head_id=0;
		if(params.get("head_id")!=null && !params.get("head_id").toString().equals("")){
			zzj_qm_test_head_id=Integer.parseInt(params.get("head_id").toString());
		}else{
			List<Map<String,Object>> head_list=qmTestRecordDao.getHeadList(params);
			if(head_list.size()>0){
				zzj_qm_test_head_id=Integer.parseInt(head_list.get(0).get("id").toString());
			}
		}
		// 行项目有一个判定为NG 抬头的检验结果就为NG
		String product_test_result="0";// 抬头生产检测结果 默认Ok
		for(Map<String,Object> proMap : pro_detail_list){
			if(proMap.get("test_result")!=null && proMap.get("test_result").toString().equals("NG")){
				product_test_result="1";
				break;
			}
		}
		Map<String,Object> smap=null;
		// 第一次录入
		if(zzj_qm_test_head_id==0){
			params.put("product_test_result", product_test_result);
			qmTestRecordDao.saveHead(params);
			zzj_qm_test_head_id=(int) params.get("id");	
			smap=new HashMap<String,Object>();
			smap.put("zzj_qm_test_head_id", zzj_qm_test_head_id);
			smap.put("detail_list", pro_detail_list);
			smap.put("result_type", params.get("result_type"));
			smap.put("product_test_result", params.get("product_test_result"));
			if(pro_detail_list.size()>0){
				qmTestRecordDao.saveItems(smap);
			}	
		}else{
			String test_result="0";// 抬头品质检测结果 默认Ok
			// 生产数据
			if(pro_detail_list.size()>0){
				smap=new HashMap<String,Object>();
				smap.put("detail_list", pro_detail_list);
				if(pro_detail_list.get(0).get("id")!=null
						&& !pro_detail_list.get(0).get("id").toString().equals("")){
						qmTestRecordDao.updateItems(smap);
				}else{
					smap.put("zzj_qm_test_head_id", zzj_qm_test_head_id);
					smap.put("result_type", params.get("result_type"));
					qmTestRecordDao.saveItems(smap);
				}				
			}	
			// 品质数据
			if(qc_detail_list.size()>0){	
				smap.put("detail_list", qc_detail_list);
				if(qc_detail_list.get(0).get("id")!=null
						&& !qc_detail_list.get(0).get("id").toString().equals("")){
					qmTestRecordDao.updateItems(smap);
				}else{
					smap.put("zzj_qm_test_head_id", Integer.parseInt(params.get("head_id").toString()));
					smap.put("result_type", params.get("result_type"));
					qmTestRecordDao.saveItems(smap);
				}		
				for(Map<String,Object> qcMap : qc_detail_list){
					if(qcMap.get("test_result")!=null && qcMap.get("test_result").toString().equals("NG")){
						test_result="1";
						break;
					}
				}
			}
			params.put("product_test_result", product_test_result);
			params.put("test_result", test_result);
			// 更新抬头 检验结果 字段
			qmTestRecordDao.updateHead(params);
		}
	}

	@Override
	@Transactional
	public void del(Map<String, Object> param) {
		//String ids=param.get("ids").toString();
		//qmTestRecordDao.deleteHead(ids);
		qmTestRecordDao.deleteItems(param);
	}

	@Override
	public List<Map<String, Object>> getTestRecordList(Map<String, Object> params) {
		return qmTestRecordDao.getTestRecordList(params);
	}

	@Override
	public List<String> checkMachine(Map<String, Object> params) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<Map<String, Object>> getOrderList(Map<String, Object> param) {
		return qmTestRecordDao.getOrderList(param);
	}

	@Override
	public List<Map<String, Object>> getPmdInfo(Map<String, Object> params) {
		return qmTestRecordDao.getPmdInfo(params);
	}

	@Override
	public List<Map<String, Object>> getHeadList(Map<String, Object> params) {
		return qmTestRecordDao.getHeadList(params);
	}

}
