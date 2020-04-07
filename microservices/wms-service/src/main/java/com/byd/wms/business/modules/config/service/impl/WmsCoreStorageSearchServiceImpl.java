package com.byd.wms.business.modules.config.service.impl;

import com.baomidou.mybatisplus.mapper.EntityWrapper;
import com.baomidou.mybatisplus.plugins.Page;
import com.baomidou.mybatisplus.service.impl.ServiceImpl;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.config.dao.WmsCoreSearchSequenceDao;
import com.byd.wms.business.modules.config.dao.WmsCoreStorageSearchDao;
import com.byd.wms.business.modules.config.entity.WmsCoreSearchSequenceEntity;
import com.byd.wms.business.modules.config.entity.WmsCoreStorageSearchEntity;
import com.byd.wms.business.modules.config.service.WmsCoreSearchSequenceService;
import com.byd.wms.business.modules.config.service.WmsCoreStorageSearchService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service("wmsCoreStorageSearchService")
public class WmsCoreStorageSearchServiceImpl extends ServiceImpl<WmsCoreStorageSearchDao, WmsCoreStorageSearchEntity> implements WmsCoreStorageSearchService {

	@Autowired
	WmsCoreStorageSearchDao wmsCoreStorageSearchDao;

	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 6000;
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}
		params.put("start", start);params.put("end", end);
		List<Map<String,Object>> records = wmsCoreStorageSearchDao.queryStorageSearchList(params);
		if (records.size()==0){
			pageNo = "1";
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
			params.put("start", start);params.put("end", end);
			records = wmsCoreStorageSearchDao.queryStorageSearchList(params);
		}
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(records);
		page.setTotal(wmsCoreStorageSearchDao.getStorageSearchCount(params));
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));


		return new PageUtils(page);
	}

	@Override
	public String queryAreaName(String warehouseCode,String storageAreaCode) {

		String name = wmsCoreStorageSearchDao.queryAreaName(warehouseCode,storageAreaCode);
		return name;
	}

	@Override
	public List getStorageAreaSearch(String warehouseCode) {
		List list = wmsCoreStorageSearchDao.getStorageAreaSearch(warehouseCode);
		return list;
	}

	@Override
	public List getStorageAreaCode(String warehouseCode) {
		List list = wmsCoreStorageSearchDao.getStorageAreaCode(warehouseCode);
		return list;
	}


}
