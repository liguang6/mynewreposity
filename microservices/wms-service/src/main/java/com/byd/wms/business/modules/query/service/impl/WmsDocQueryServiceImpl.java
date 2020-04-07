package com.byd.wms.business.modules.query.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.query.dao.WmsDocQueryDao;
import com.byd.wms.business.modules.query.service.WmsDocQueryService;
import com.byd.wms.business.modules.query.utils.ParamsFilterUtils;

/**
 * 查询事务记录
 * @author cscc tangj
 * @email 
 * @date 2018-11-16 10:12:08
 */
@Service("wmsDocQueryService")
public class WmsDocQueryServiceImpl implements WmsDocQueryService {
	@Autowired
    private WmsDocQueryDao wmsDocQueryDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		params=ParamsFilterUtils.paramFilter(params);
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsDocQueryDao.getDocCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		if(params.get("MATNR_IN")!=null){
			String maIn = params.get("MATNR_IN").toString().substring(1,params.get("MATNR_IN").toString().length()-1).replaceAll(" ","");
			params.put("MATNR_IN", maIn.replaceAll("\t","").split(","));
		}
		List<Map<String,Object>> list=wmsDocQueryDao.getDocList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		return new PageUtils(page);
	}

	@Override
	public PageUtils ReceiveLogPage(Map<String, Object> params) {
		params=ParamsFilterUtils.paramFilter(params);
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsDocQueryDao.getReceiveLogsCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		if(params.get("MATNR_IN")!=null){
			String maIn = params.get("MATNR_IN").toString().substring(1,params.get("MATNR_IN").toString().length()-1).replaceAll(" ","");
			params.put("MATNR_IN", maIn.replaceAll("\t","").split(","));
		}
		params.put("orderBy", "WMS_NO,WMS_ITEM_NO");
		List<Map<String,Object>> list=wmsDocQueryDao.getReceiveLogsList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		return new PageUtils(page);
	}
	
	@Override
	public PageUtils ReceiveLabelPage(Map<String, Object> params) {
		params=ParamsFilterUtils.paramFilter(params);
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsDocQueryDao.getReceiveLabelCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		if(params.get("MATNR_IN")!=null){
			String maIn = params.get("MATNR_IN").toString().substring(1,params.get("MATNR_IN").toString().length()-1).replaceAll(" ","");
			params.put("MATNR_IN", maIn.replaceAll("\t","").split(","));
		}
		List<Map<String,Object>> list=wmsDocQueryDao.getReceiveLabelList(params);
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		return new PageUtils(page);
	}
}
