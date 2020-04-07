package com.byd.bjmes.modules.config.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.bjmes.modules.config.dao.BjMesOrderProductsDao;
import com.byd.bjmes.modules.config.dao.BjMesProductsDao;
import com.byd.bjmes.modules.config.service.BjMesOrderProductsService;
import com.byd.bjmes.modules.config.service.BjMesProductsService;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年10月15日 下午4:29:01 
 * 类说明 
 */
@Service
public class BjMesOrderProductsServiceImpl implements BjMesOrderProductsService {

	@Autowired
	private BjMesOrderProductsDao bjMesOrderProductsDao;
	
	@Override
	public int save(List<Map<String, Object>> params){
		return bjMesOrderProductsDao.save(params);
	}

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"-1";
		int start = 0;
		int limit=0;
		int count = bjMesOrderProductsDao.getListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			limit = Integer.valueOf(pageSize);
		}else {
			limit=count;
		}
		params.put("start", start);params.put("limit", limit);
		List<Map<String,Object>> list=bjMesOrderProductsDao.getListByPage(params);
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
        	list=bjMesOrderProductsDao.getListByPage(params);
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
		
		return bjMesOrderProductsDao.getList(params);
	}

	@Override
	public int update(Map<String, Object> params) { 
		
		return bjMesOrderProductsDao.update(params);
	}

	@Override
	public int delete(Long id) {
		return bjMesOrderProductsDao.delete(id);
	}

	@Override
	public PageUtils getOrderMapListByPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"-1";
		int start = 0;
		int limit=0;
		int count = bjMesOrderProductsDao.getOrderMapListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			limit = Integer.valueOf(pageSize);
		}else {
			limit=count;
		}
		params.put("start", start);params.put("limit", limit);
		List<Map<String,Object>> list=bjMesOrderProductsDao.getOrderMapListByPage(params);
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
        	list=bjMesOrderProductsDao.getOrderMapListByPage(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
	}

	@Override
	public int saveOrderMap(List<Map<String, Object>> params) {
		
		return bjMesOrderProductsDao.saveOrderMap(params);
	}

	@Override
	public int updateOrderMap(Map<String, Object> params) {
		
		return bjMesOrderProductsDao.updateOrderMap(params);
	}

	@Override
	public int deleteOrderMap(Map<String, Object> params) {
		
		return bjMesOrderProductsDao.deleteOrderMap(params);
	}

	@Override
	public List<Map<String, Object>> getOrderMapList(Map<String, Object> params) {
		return bjMesOrderProductsDao.getOrderMapList(params);
	}
	
	

}
