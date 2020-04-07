package com.byd.wms.business.modules.report.dao;

import java.util.List;
import java.util.Map;

/**
 * @ClassName
 * @Author qiu.jiaming1
 * @Date 2019年6月27日
 * @Description //TODO $end$
 **/

public interface InAndOutStockQtyByDayDao {
	//出入库库存查询
	List<Map<String, Object>> selectInOutStockQtyList(Map<String, Object> params);
	
	int selectInOutStockQtyCount(Map<String, Object> params);

	List<Map<String, Object>> selectInOutStockQty();

	void insertWmsReportInoutStock();
}
