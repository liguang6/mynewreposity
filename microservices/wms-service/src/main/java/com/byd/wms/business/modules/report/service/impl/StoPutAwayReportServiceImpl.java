package com.byd.wms.business.modules.report.service.impl;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.report.dao.StoPutAwayReportDao;
import com.byd.wms.business.modules.report.service.StoPutAwayReportService;
@Service
public class StoPutAwayReportServiceImpl implements StoPutAwayReportService {

	@Autowired
	private StoPutAwayReportDao stoPutAwayReportDao;
	
	@Override
	public PageUtils queryStoPutAwayReportPage(Map<String, Object> params) {
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
		
		String MATNR = (params.get("MATNR") != null && !params.get("MATNR").equals(""))? params.get("MATNR").toString() : null;
		String WMS_NO = (params.get("WMS_NO") != null && !params.get("WMS_NO").equals(""))? params.get("WMS_NO").toString() : null;
		String SAP_NO = (params.get("WMS_SAP_MAT_DOC") != null && !params.get("WMS_SAP_MAT_DOC").equals(""))? params.get("WMS_SAP_MAT_DOC").toString() : null;
		String matnrList[]= null;
		String wmsList[]= null;
		String sapList[]= null;
		if(MATNR!=null) {
			matnrList=MATNR.trim().split(",");
			params.put("matnrList", matnrList);
		}
		if(WMS_NO!=null) {
			wmsList=WMS_NO.trim().split(",");
			params.put("wmsList", wmsList);
		}
		if(SAP_NO!=null) {
			sapList=SAP_NO.trim().split(",");
			params.put("sapList", sapList);
		}
		//获取数据
		List<Map<String,Object>> list=stoPutAwayReportDao.getStoPutAwayInfoList(params);
	
		int count= stoPutAwayReportDao.getStoPutAwayInfoCount(params);
		//返回参数设置
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

}
