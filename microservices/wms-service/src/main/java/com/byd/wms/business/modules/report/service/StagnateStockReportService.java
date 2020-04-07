package com.byd.wms.business.modules.report.service;

import com.byd.utils.PageUtils;

import java.util.Map;

/**
 * @ClassName
 * @Author chen.yafei1
 * @Date 2019年11月27日
 * @Description //TODO $end$
 **/

public interface StagnateStockReportService {

	//库存查询
  	PageUtils querystagenateStockPage(Map<String, Object> params);

}
