package com.byd.wms.business.modules.kn.dao;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.kn.entity.WmsKnStorageMoveEntity;

/**
 * 移储
 * 
 * @author cscc tangj
 * @email 
 * @date 2018-11-07 10:12:08
 */
public interface WmsKnStorageMoveDao extends BaseMapper<WmsKnStorageMoveEntity>{
	//查询移储记录
	List<Map<String,Object>> getStorageMoveList(Map<String,Object> param);
	//查询移储记录条数
	int getStorageMoveCount(Map<String,Object> param);
	//查询库存信息
	List<Map<String,Object>> getStockInfoList(Map<String,Object> param);
	// 保存移储记录
	int saveStorageMove(List<Map<String,Object>> list);
	// 更新源储位库存
	int updateWmsStock(List<Map<String,Object>> list);
	// 合并（目标储位）库存
	int mergeWmsStock(List<Map<String,Object>> list);
	
	int updateWmsCoreLabel(List<Map<String,Object>> list);
	
	int updateWmsCoreStockLabel(List<Map<String,Object>> list);
	
	List<Map<String,Object>> getAutoPutawayFlagList(Map<String,Object> param);
	
	List<Map<String,Object>> getCoreLabelList(Map<String,Object> param);
	
	int getWhTaskList(Map<String,Object> param);
}
