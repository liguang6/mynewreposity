package com.byd.wms.business.modules.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.report.dao.AgingMonthlyReportDao;
import com.byd.wms.business.modules.report.service.AgingMonthlyReportService;
@Service
public class AgingMonthlyReportServiceImpl implements AgingMonthlyReportService {

	@Autowired
	private AgingMonthlyReportDao agingMonthlyReportDao;
	
	@Override
	public PageUtils queryAgingMonthlyPage(Map<String, Object> params) {
		// 参数解析
		String pageNo = (params.get("pageNo") != null && !params.get("pageNo").equals(""))? params.get("pageNo").toString() : "1";
		String pageSize = (params.get("pageSize") != null && !params.get("pageSize").equals(""))? params.get("pageSize").toString() : "15";		
		int start = 0;
		int end = 15;
		if (params.get("pageSize") != null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo) - 1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo) * Integer.valueOf(pageSize);
		}
		params.put("start", start);
		params.put("end", end);
		
		//获取数据
		List<Map<String,Object>> list=agingMonthlyReportDao.getAgingMonthlyInfoList(params);
	
		int count= agingMonthlyReportDao.getAgingMonthlyInfoCount(params);
		//返回参数设置
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

}
