package com.byd.zzjmes.modules.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.zzjmes.modules.report.dao.PmdReportDao;
import com.byd.zzjmes.modules.report.service.PmdReportService;

@Service("pmdReportService")
public class PmdReportServiceImpl implements PmdReportService {
	@Autowired
	PmdReportDao pmdReportDao;
	
	/**
	 * 订单产量达成明细报表
	 */
	@Override
	public PageUtils pmdOutputReachReport(Map<String, Object> params) {
		String pageNo = (params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize =(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"50";
		int start = 0;int end = 0;
		int count = pmdReportDao.getPmdOutputReachCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("start", start);params.put("limit", end);
		List<Map<String,Object>> list = pmdReportDao.getPmdOutputReachList(params);
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
        	list = pmdReportDao.getPmdOutputReachList(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
	}
	
	@Override
	public List<Map<String,Object>> getPmdOutputReachList(Map<String, Object> params){
		List<Map<String,Object>> list = pmdReportDao.getPmdOutputReachList(params);
		return list;
	}
	
	/**
	 * 批次产量达成明细报表
	 */
	@Override
	public PageUtils batchOutputReachReport(Map<String, Object> params) {
		String pageNo = (params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize =(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"50";
		int start = 0;int end = 0;
		int count = pmdReportDao.getBatchOutputReachCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("start", start);params.put("limit", end);
		List<Map<String,Object>> list = pmdReportDao.getBatchOutputReachList(params);
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
        	list = pmdReportDao.getBatchOutputReachList(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
	}
	
	@Override
	public List<Map<String,Object>> getBatchOutputReachList(Map<String, Object> params){
		List<Map<String,Object>> list = pmdReportDao.getBatchOutputReachList(params);
		return list;
	}
	
	/**
	 * 订单需求达成报表
	 */
	@Override
	public PageUtils pmdReqReachReport(Map<String, Object> params) {
		String pageNo = (params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize =(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"50";
		int start = 0;int end = 0;
		int count = pmdReportDao.getPmdReqReachCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("start", start);params.put("limit", end);
		List<Map<String,Object>> list = pmdReportDao.getPmdReqReachList(params);
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
        	list = pmdReportDao.getPmdReqReachList(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
	}
	
	/**
	 * 订单批次需求达成报表
	 */
	@Override
	public PageUtils orderBatchReachReport(Map<String, Object> params) {
		String pageNo = (params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize =(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"50";
		int start = 0;int end = 0;
		int count = pmdReportDao.getOrderBatchReachCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("start", start);params.put("limit", end);
		List<Map<String,Object>> list = pmdReportDao.getOrderBatchReachList(params);
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
        	list = pmdReportDao.getOrderBatchReachList(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
	}
	
	@Override
	public List<Map<String,Object>> getWorkgroupReachList(Map<String, Object> params){
		List<Map<String,Object>> list = pmdReportDao.getWorkgroupReachList(params);
		return list;
	}
	
	/**
	 * 组合件加工进度报表
	 */
	@Override
	public PageUtils orderAssemblyReport(Map<String, Object> params) {
		String pageNo = (params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize =(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count = pmdReportDao.getOrderAssemblyListCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("start", start);params.put("limit", end);
		List<Map<String,Object>> list = pmdReportDao.getOrderAssemblyList(params);
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
        	list = pmdReportDao.getOrderAssemblyList(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
	}
	
	@Override
	public List<Map<String,Object>> getOrderAssemblyList(Map<String, Object> params){
		List<Map<String,Object>> list = pmdReportDao.getOrderAssemblyList(params);
		return list;
	}

	@Override
	public List<Map<String,Object>> getPmdProcessTimeList(Map<String, Object> params){
		List<Map<String,Object>> list = pmdReportDao.getPmdProcessTimeList(params);
		return list;
	}

	@Override
	public List<Map<String,Object>> getPmdProcessTransferTimeList(Map<String, Object> params){
		List<Map<String,Object>> list = pmdReportDao.getPmdProcessTransferTimeList(params);
		return list;
	}

	@Override
	public PageUtils pmdProcessTimeReport(Map<String, Object> params) {
		String pageNo = (params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize =(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count = pmdReportDao.getPmdProcessTimeCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("start", start);params.put("limit", end);
		List<Map<String,Object>> list = pmdReportDao.getPmdProcessTimeList(params);
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
        	list = pmdReportDao.getPmdProcessTimeList(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
	}

	@Override
	public PageUtils pmdProcessTransferTimeReport(Map<String, Object> params) {
		String pageNo = (params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize =(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count = pmdReportDao.getPmdProcessTransferTimeCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("start", start);params.put("limit", end);
		List<Map<String,Object>> list = pmdReportDao.getPmdProcessTransferTimeList(params);
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
        	list = pmdReportDao.getPmdProcessTransferTimeList(params);
    		page=new Query<Map<String,Object>>(params).getPage();
    		page.setRecords(list);
    		page.setTotal(count);
    		page.setSize(Integer.valueOf(pageSize));
    		page.setCurrent(Integer.valueOf(pageNo));
		}
        return new PageUtils(page);
	}
	
}
