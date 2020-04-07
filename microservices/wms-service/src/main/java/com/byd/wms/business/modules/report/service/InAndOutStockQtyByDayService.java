package com.byd.wms.business.modules.report.service;

import com.byd.utils.PageUtils;
import com.byd.wms.business.modules.report.dao.InAndOutStockQtyByDayDao;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Map;

/**
 * @ClassName
 * @Author qiu.jiaming1
 * @Date 2019年6月27日
 * @Description //TODO $end$
 **/

public interface InAndOutStockQtyByDayService {

	//库存查询
  	PageUtils queryStockPage(Map<String, Object> params);

	/**
	 * 插入出入库库存日报表
	 */
    void insertWmsReportInoutStock();
}
