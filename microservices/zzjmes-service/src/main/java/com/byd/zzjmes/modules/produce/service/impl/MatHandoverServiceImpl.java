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
import com.byd.zzjmes.modules.produce.dao.MatHandoverDao;
import com.byd.zzjmes.modules.produce.service.MatHandoverService;
/***
 * @Desc 车间内交接、车间供货
 * @author tangj
 * @date 2019-09-23 16:12:06
 */
@Service("matHandoverService")
public class MatHandoverServiceImpl implements MatHandoverService{
	@Autowired
	private MatHandoverDao matHandoverDao;

	@Override
	public List<Map<String, Object>> getMatInfo(Map<String, Object> condMap) {
		return matHandoverDao.getMatInfo(condMap);
	}

	@Override
	@Transactional
	public void save(Map<String, Object> condMap) {
		matHandoverDao.save(condMap);
	}

	@Override
	public List<Map<String, Object>> getSupplyMatInfo(Map<String, Object> condMap) {
		return matHandoverDao.getSupplyMatInfo(condMap);
	}

	@Override
	@Transactional
	public void saveSupply(Map<String, Object> condMap) {
		matHandoverDao.saveSupply(condMap);
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=matHandoverDao.getMatHandoverCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("start", start);params.put("limit", end);
		List<Map<String,Object>> list=matHandoverDao.getMatHandoverList(params);
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
        	list=matHandoverDao.getMatHandoverList(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
	}

	@Override
	public List<Map<String, Object>> getList(Map<String, Object> params) {
		return matHandoverDao.getMatHandoverList(params);
	}

	@Override
	public List<Map<String, Object>> getHandoverRuleList(Map<String, Object> condMap) {
		return matHandoverDao.getHandoverRuleList(condMap);
	}
	@Override
	public PageUtils querySupplyPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=matHandoverDao.getSupplyCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("start", start);params.put("limit", end);
		List<Map<String,Object>> list=matHandoverDao.getSupplyList(params);
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
        	list=matHandoverDao.getSupplyList(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
	}

	@Override
	public List<Map<String, Object>> getSupplyDetailList(Map<String, Object> condMap) {
		return matHandoverDao.getSupplyDetailList(condMap);
	}
}
