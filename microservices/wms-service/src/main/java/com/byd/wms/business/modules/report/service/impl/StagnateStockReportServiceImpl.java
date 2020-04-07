package com.byd.wms.business.modules.report.service.impl;

import com.baomidou.mybatisplus.plugins.Page;
import com.byd.utils.PageUtils;
import com.byd.utils.Query;
import com.byd.wms.business.modules.report.dao.StagnateStockReportDao;
import com.byd.wms.business.modules.report.service.StagnateStockReportService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * @ClassName
 * @Author chen.yafei1
 * @Date 2019年11月27日
 * @Description //TODO $end$
 **/

@Service
public class StagnateStockReportServiceImpl implements StagnateStockReportService {

	@Autowired
	StagnateStockReportDao stagnateStockReportDao;
	/**
	 * 库存查询
	 */
	@Override
	public PageUtils querystagenateStockPage(Map<String, Object> params) {
		//参数解析
		String pageNo=(params.get("pageNo")!=null && !params.get("pageNo").equals(""))?params.get("pageNo").toString():"1";
		String pageSize=(params.get("pageSize")!=null && !params.get("pageSize").equals(""))?params.get("pageSize").toString():"15";
		int days=Integer.valueOf(params.get("DAYS").toString());
		params.put("DAYS", days);
		int start = 0;int end = 15;
		
		if(params.get("pageSize")!=null && !params.get("pageSize").equals("")) {
			start = (Integer.valueOf(pageNo)-1) * Integer.valueOf(pageSize);
			end = Integer.valueOf(pageNo)*Integer.valueOf(pageSize);
		}		
		params.put("start", start);
		params.put("end", end);
		
		//获取数据
		List<Map<String,Object>> list=stagnateStockReportDao.getStagnateStockInfoList(params);
	
		int count= stagnateStockReportDao.getStagnateStockInfoCount(params);
		
		Page page=new Query<Map<String,Object>>(params).getPage();
		page.setRecords(list);
		page.setTotal(count);
		page.setSize(Integer.valueOf(pageSize));
		page.setCurrent(Integer.valueOf(pageNo));
        return new PageUtils(page);
	}


}
