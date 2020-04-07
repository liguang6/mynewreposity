package com.byd.wms.business.modules.report.dao;

import java.util.List;
import java.util.Map;

public interface WmsReportBarcodeLogDao {
	List<Map<String, Object>> queryWmsReportBarcodeLog(Map<String, Object> params);

	int selectWmsReportBarcodeLogCount(Map<String, Object> params);
}
