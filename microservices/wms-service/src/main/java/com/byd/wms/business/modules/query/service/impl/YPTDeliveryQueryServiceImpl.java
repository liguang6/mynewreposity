package com.byd.wms.business.modules.query.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.in.dao.YPTDeliveryDao;
import com.byd.wms.business.modules.query.service.YPTDeliveryQueryService;
import com.byd.wms.business.modules.query.utils.ParamsFilterUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/** 
 * @author rain
 * @version 2019年11月20日16:55:16
 * @description 云平台送货单查询
 */
@Service
public class YPTDeliveryQueryServiceImpl implements YPTDeliveryQueryService {
	@Autowired
	private YPTDeliveryDao yptDeliveryDao;


	/**
	 * 云平台送货单列表查询
	 * @param params
	 * @return
	 */
	@Override
//	@DataSource(name = DataSourceNames.SECOND)
	public PageUtils queryPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=yptDeliveryDao.queryHeadByYPTCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);
		params.put("END", end);
		List<Map<String,Object>> list=yptDeliveryDao.queryHeadByYPT(params);

		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		return new PageUtils(page);
	}

	/**
	 * 云平台送货单明细页面查询
	 * @param params
	 * @return
	 */
	@Override
//	@DataSource(name = DataSourceNames.SECOND)
	public PageUtils queryPageDetail(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=yptDeliveryDao.queryItemByYPTCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);
		params.put("END", end);
		List<Map<String,Object>> list=yptDeliveryDao.queryItemByYPT(params);

		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		return new PageUtils(page);
	}


	@Override
	public PageUtils queryBarPage(Map<String, Object> params) {
		params= ParamsFilterUtils.paramFilter(params);
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=yptDeliveryDao.getLabelCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=yptDeliveryDao.getLabelList(params);
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
			params.put("START", start);params.put("END", end);
			list=yptDeliveryDao.getLabelList(params);
			page=new Query<Map<String,Object>>(params).getPage();
			page.setRecords(list);
			page.setTotal(count);
			page.setSize(Integer.valueOf(pageSize));
			page.setCurrent(Integer.valueOf(pageNo));
		}
		return new PageUtils(page);
	}

}
