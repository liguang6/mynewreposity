package com.byd.wms.business.modules.kn.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
/**
 * 库存调整
 * 
 * @author tangj
 * @email 
 * @date 2018-11-01 10:12:08
 */
public interface WmsKnStockModifyDao{

	// 库存修改：批量更新库存记录
	int batchUpdateStock(List<Map<String,Object>> list);
	// 查询批次是否已存在
	int getMatBatchCount(@Param("BATCH")String BATCH,@Param("WERKS")String WERKS,@Param("MATNR")String MATNR);
	//更新LAST_INBOUND_DATE
	int updateInboundDate(@Param("BATCH")String BATCH,@Param("WERKS")String WERKS,@Param("MATNR")String MATNR);
	//查询最大ID
	int getMatBatchId();
	//插入流水表
	void insertMatBatch(Map<String, Object> list);
	// 查询标签号是否已存在
	List<Map<String,Object>> checkLabelList(List<String> list);
	// 库存新增：批量保存
	int saveStockByBatch(List<Map<String,Object>> list);
	// 批量保存标签
	int saveCoreLabel(List<Map<String,Object>> list);
	// 获取库存修改的业务类型代码
	List<Map<String,Object>> getBusinessCode(@Param("WERKS")String werks);
	// 获取文本值 （库存修改）
	List<Map<String,Object>> getTxtList(@Param("WERKS")String werks);
	// 查找库存记录
	List<Map<String,Object>> getStockList(Map<String,Object> params);
	
	void insertWmsCoreStockLabel(List<Map<String, Object>> itemList);
	
	List<Map<String,Object>> getCoreStockLabelList(Map<String,Object> params);
	
	List<Map<String,Object>> getCoreLabelList(Map<String,Object> params);
	// 库存修改：批量更新标签记录
	int batchUpdateLabel(List<Map<String,Object>> list);
	// 导入操作 批量查出校验所需的数据 （物料、供应商、库位、储位等）2019-08-26 tangj
	List<Map<String,Object>> checkMaterialList(Map<String,Object> params);
	
	List<Map<String,Object>> checkVendorList(Map<String,Object> params);
	
	List<Map<String,Object>> checkLgortList(Map<String,Object> params);
	
	List<Map<String,Object>> checkBinList(Map<String,Object> params);
	
	List<Map<String,Object>>checkStockList(Map<String,Object> params);

	public void insertWMSDocHead(Map<String, Object> wms_doc_head);

	public void insertWMSDocDetail(List<Map<String, Object>> itemList);
}
