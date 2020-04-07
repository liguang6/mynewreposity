package com.byd.wms.business.modules.query.dao;

import java.util.List;
import java.util.Map;
import com.baomidou.mybatisplus.mapper.BaseMapper;
import com.byd.wms.business.modules.kn.entity.WmsKnStorageMoveEntity;

/**
 * 移储
 * 
 * @author cscc tangj
 * @email 
 * @date 2018-11-21 15:12:08
 */
public interface WmsKnQueryDao{
	//查询移储记录
	List<Map<String,Object>> getStorageMoveList(Map<String,Object> param);
	//查询移储记录条数
	int getStorageMoveCount(Map<String,Object> param);
	
	//查询冻结记录
	List<Map<String,Object>> getFreezeRecordList(Map<String,Object> param);
	//查询冻结记录条数
	int getFreezeRecordCount(Map<String,Object> param);
	
	//查询盘点记录
	List<Map<String,Object>> getInventoryList(Map<String,Object> param);
	//查询盘点记录条数
	int getInventoryCount(Map<String,Object> param);
}
