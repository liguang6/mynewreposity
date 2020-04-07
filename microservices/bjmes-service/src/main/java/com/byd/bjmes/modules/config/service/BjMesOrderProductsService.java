package com.byd.bjmes.modules.config.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;

/** 
 * @author 作者 
 * @version 创建时间：2019年10月30日 下午5:00:00 
 * 类说明 
 */
public interface BjMesOrderProductsService {

	PageUtils queryPage(Map<String, Object> params);

	public List<Map<String,Object>> getList(Map<String,Object> params);
	
	int save(List<Map<String, Object>> params);

	int update(Map<String, Object> params);

	public int delete(Long id);

	PageUtils getOrderMapListByPage(Map<String, Object> params);

	int saveOrderMap(List<Map<String, Object>> params);

	int updateOrderMap(Map<String, Object> params);

	public int deleteOrderMap(Map<String, Object> params);

	public List<Map<String,Object>> getOrderMapList(Map<String,Object> params);
	
}
