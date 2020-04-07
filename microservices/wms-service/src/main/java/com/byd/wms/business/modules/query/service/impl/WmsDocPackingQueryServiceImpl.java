package com.byd.wms.business.modules.query.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.query.dao.WmsDocPackingQueryDao;
import com.byd.wms.business.modules.query.service.WmsDocPackingQueryService;
import com.byd.wms.business.modules.query.service.WmsDocQueryService;
import com.byd.wms.business.modules.query.utils.ParamsFilterUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 查询发料包装
 * @author qjm
 * @email
 * @date 2019-06-05
 */

@Service
public class WmsDocPackingQueryServiceImpl implements WmsDocPackingQueryService {
	@Autowired
	private WmsDocPackingQueryDao wmsDocPackingQueryDao;
	@Override
	public PageUtils queryPage(Map<String, Object> params) {
		params=ParamsFilterUtils.paramFilter(params);
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=wmsDocPackingQueryDao.getDocPackingCount(params);
		System.err.println(count);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=wmsDocPackingQueryDao.getDocPackingList(params);
		System.err.println("list===>>> "+list.toString());
		Page page=new Query<Map<String,Object>>(params).getPage();






		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
		System.err.println(page.toString());
		return new PageUtils(page);
	}

}
