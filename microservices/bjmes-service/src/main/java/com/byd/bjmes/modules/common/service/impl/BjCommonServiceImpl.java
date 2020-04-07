package com.byd.bjmes.modules.common.service.impl;

import java.util.Map;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.bjmes.modules.common.dao.BjCommonDao;
import com.byd.bjmes.modules.common.service.BjCommonService;
import com.byd.utils.PageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service("bjCommonService")
public class BjCommonServiceImpl implements BjCommonService {
    @Autowired
    private BjCommonDao bjCommonDao;

    @Override
    public PageUtils getOrderList(Map<String, Object> params) {
		Page<Map<String, Object>> roderPage = new Page<Map<String, Object>>();	
		int pageSize = Integer.valueOf(params.get("pageSize") != null?params.get("pageSize").toString():"15");
		int pageNo=Integer.valueOf(params.get("pageNo") != null?params.get("pageNo").toString():"1");
		params.put("start", (pageNo - 1) * pageSize);
		roderPage.setRecords(bjCommonDao.getOrderList(params));
		roderPage.setSize(pageSize);
		roderPage.setTotal(bjCommonDao.getOrderListTotalCount(params));
		PageUtils page = new PageUtils(roderPage);		
		return page;
	}
    
}