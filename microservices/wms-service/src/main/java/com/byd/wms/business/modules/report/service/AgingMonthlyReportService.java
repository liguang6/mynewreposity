package com.byd.wms.business.modules.report.service;

import com.byd.utils.PageUtils;

import java.util.Map;

/**
 * @ClassName
 * @Author chen.yafei1
 * @Date 2019年11月28日
 * @Description //TODO $end$
 **/
       
public interface AgingMonthlyReportService {

	//账龄月库存查询
  	PageUtils queryAgingMonthlyPage(Map<String, Object> params);

}
