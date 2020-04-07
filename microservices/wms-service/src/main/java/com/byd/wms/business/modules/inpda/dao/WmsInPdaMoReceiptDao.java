package com.byd.wms.business.modules.inpda.dao;

import java.util.List;
import java.util.Map;

/**
 * @author rain
 * @version 创建时间：2019年12月9日17:51:12
 * 类说明  PDA生产订单入库
 */
public interface WmsInPdaMoReceiptDao {

	List<Map<String,Object>> getMorecCache(Map<String, Object> map);

	int delMorecCache(Map<String, Object> map);

	int delPorecBarInfos(Map<String, Object> map);

	//根据进仓单、进仓单行项目，或者收货单、收货单行项目查询标签订单
	List<Map<String,Object>> getLabelList(Map<String, Object> map);

	List<Map<String,Object>> getInboundItemList(Map<String, Object> map);

	List<Map<String,Object>> getMoDetailListByBarcode(Map<String, Object> map);

	void insertMoReceiptPdaCache(Map<String, Object> map);

	int getGridPoreDataCount(Map<String,Object> param);
	List<Map<String, Object>> getGridPoreData(Map<String, Object> params);

	int getBarGridPoreDataCount(Map<String,Object> param);
	List<Map<String, Object>> getBarGridPoreData(Map<String, Object> params);

	/**
	 * 保存更新标签数据信息
	 * @param skList
	 */
	void saveOrUpdateCoreLabel(List<Map<String, Object>> skList);

	//插入条码日志信息
	void insertWmsBarCodeLog(List<Map<String, Object>> cptList);

	List<Map<String,Object>> getAllLabelInfos(Map<String, Object> map);






	
}
