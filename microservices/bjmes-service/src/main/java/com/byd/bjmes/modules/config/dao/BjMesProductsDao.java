package com.byd.bjmes.modules.config.dao;

import java.util.List;
import java.util.Map;

import org.apache.ibatis.annotations.Param;

/** 
 * @author 作者 E-mail: peng.tao1@byd.com
 * @version 创建时间：2019年10月15日 下午4:10:18 
 * 类说明 
 */
public interface BjMesProductsDao {

	public List<Map<String,Object>> getListByPage(Map<String,Object> params);
	
	public int getListCount(Map<String,Object> params);
	
	public int save(Map<String,Object> params);
	
	public Map<String,Object> getById(@Param("id") Long id);
	
	public List<Map<String,Object>> getList(Map<String,Object> params);
	
	public int update(Map<String,Object> params);
	
	public int delete(@Param("id") Long id);
	
	public int deleteByParentId(@Param("id") Long id);
	
	public List<Map<String,Object>> getByParentId(@Param("id") Long id);
	
	public int saveBatch(List<Map<String, Object>> params);

	public int updateTestNode(Map<String, Object> params);
}
