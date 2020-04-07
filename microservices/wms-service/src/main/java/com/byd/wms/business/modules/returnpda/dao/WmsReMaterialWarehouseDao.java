package com.byd.wms.business.modules.returnpda.dao;

import java.util.List;
import java.util.Map;

/**
 * @author liguang6
 * @version 2019年1月2日12:28:34
 * 立库余料退回（312）
 */
public interface WmsReMaterialWarehouseDao {


	List<Map<String,Object>> getPorecCache(Map<String, Object> map);

	int delPorecCache(Map<String, Object> map);

	int delPorecBarInfos(Map<String, Object> map);


	List<Map<String,Object>> getPoLabelList(Map<String, Object> map);

	List<Map<String,Object>> getPoDetailListByBarcode(Map<String, Object> map);

	List<Map<String,Object>> getPoDetailListByItems(Map<String, Object> map);

	int getGridPoreDataCount(Map<String, Object> param);
	List<Map<String, Object>> getGridPoreData(Map<String, Object> params);

	int getBarGridPoreDataCount(Map<String, Object> param);
	List<Map<String, Object>> getBarGridPoreData(Map<String, Object> params);

	void insertPoReceiptPdaCache(Map<String, Object> map);	List<Map<String,Object>> getAllLabelInfos(Map<String, Object> map);
	/**
	 * 保存更新标签数据信息
	 * @param skList
	 */
	void saveOrUpdateCoreLabel(List<Map<String, Object>> skList);

	int updateLabelStatus(Map<String, Object> skList);

	//插入条码日志信息
	void insertWmsBarCodeLog(List<Map<String, Object>> list);

    Map<String,Object> valiateBinCode(Map<String,Object> params);






}
