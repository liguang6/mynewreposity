package com.byd.wms.business.modules.query.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;
/**
 * 进仓单查询
 * @author cscc tangj
 * @email 
 * @date 2018-11-29 09:52:18
 */
public interface InboundQueryDao {

	List<Map<String,Object>> getInboundList(Map<String,Object> param);
	
	int getInboundCount(Map<String,Object> param);
	
	List<Map<String,Object>> getInboundItemList(Map<String,Object> map);
	
	int getInboundItemCount(Map<String,Object> map);
	
	int updateHead(Map<String,Object> map);
	
	int updateItem(Map<String,Object> map);
	
}
