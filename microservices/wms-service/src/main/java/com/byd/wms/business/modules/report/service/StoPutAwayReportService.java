package com.byd.wms.business.modules.report.service;

import com.byd.utils.PageUtils;

import java.util.Map;

/**
 * @ClassName
 * @Author chen.yafei1
 * @Date 2019年12月3日
 * @Description //TODO $end$
 **/
       
public interface StoPutAwayReportService {

	//账龄月库存查询
  	PageUtils queryStoPutAwayReportPage(Map<String, Object> params);

}
