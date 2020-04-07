package com.byd.bjmes.modules.config.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;

/** 
 * @author 作者 tangjin 
 * @version 创建时间：2019年10月30日 下午5:00:00 
 * 类说明 加工流程
 */
public interface BjMesProductsService {

	PageUtils queryPage(Map<String, Object> params);
	
	int save(Map<String, Object> params);
	
	Map<String, Object> getById(Long id);
	
	public List<Map<String,Object>> getList(Map<String,Object> params);
	
	int update(Map<String, Object> params);
    
    int delete(Long id);
    
    int deleteByParentId(Long id);
    
    List<Map<String,Object>> getByParentId(Long id);
    
	int savebatch(List<Map<String, Object>> params);
	
	public int updateTestNode(Map<String, Object> params);
}
