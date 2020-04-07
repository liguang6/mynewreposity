package com.byd.wms.webservice.ws.dao;



import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.webservice.ws.entity.WmsWebServiceKnFreezeRecordEntity;

/**
 * 库存冻结记录
 * 
 * @author cscc
 * @email 
 * @date 2018-10-11 10:12:08
 */
public interface WmsWebServiceKnFreezeRecordDao extends BaseMapper<WmsWebServiceKnFreezeRecordEntity> {
	//库存冻结：查询库存信息【非限制数量或冻结数量】
	List<Map<String,Object>> getStockInfoList(Map<String,Object> param);
	//库存冻结：查询库存信息记录条数【非限制数量或冻结数量】
	int getStockInfoCount(Map<String,Object> param);
	// 批量更新库存表
	int batchUpdateStock(List<Map<String,Object>> list);
	// 批量保存冻结记录表
	int saveFreezeRecord(List<Map<String,Object>> list);
	// 标签表LABEL_STATUS字段
	int batchUpdateCoreLabelStatus(List<Map<String,Object>> list);
	// 标签表LABEL_STATUS字段
	int updateMatBatch(List<Map<String,Object>> list);
	// app扫描标签号获取数据
	List<Map<String,Object>> getDataByLabelNo(@Param("labelNo") String labelNo); 
}
