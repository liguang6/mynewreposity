package com.byd.wms.business.modules.report.service.impl;

import com.alibaba.fastjson.JSONArray;
import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.common.service.WmsSapRemote;
import com.byd.wms.business.modules.report.dao.InAndOutStockQtyByDayDao;
import com.byd.wms.business.modules.report.dao.QueryComponentDao;
import com.byd.wms.business.modules.report.service.InAndOutStockQtyByDayService;
import com.byd.wms.business.modules.report.service.QueryComponentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @ClassName
 * @Author qiu.jiaming1
 * @Date 2019年6月27日
 * @Description //TODO $end$
 **/

@Service
public class InAndOutStockQtyByDayServiceImpl implements InAndOutStockQtyByDayService {

	@Autowired
	private InAndOutStockQtyByDayDao inAndOutStockQtyByDayDao;


	/**
	 * 出入库库存查询
	 */
	@Override
	public PageUtils queryStockPage(Map<String, Object> params) {
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int start = 0;int end = 0;
		int count=inAndOutStockQtyByDayDao.selectInOutStockQtyCount(params);
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}else {
			end=count;
		}
		params.put("START", start);params.put("END", end);
		List<Map<String,Object>> list=inAndOutStockQtyByDayDao.selectInOutStockQtyList(params);

		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}

	@Override
	public void insertWmsReportInoutStock() {
		inAndOutStockQtyByDayDao.insertWmsReportInoutStock();
	}


}
