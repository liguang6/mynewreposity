package com.byd.admin.modules.masterdata.dao;

import java.util.List;
import java.util.Map;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年10月25日 下午2:19:54 
 * 类说明 
 */
public interface StandardWorkgroupDao {
	public List<Map<String, Object>> getStandardWorkgroupListByPage(Map<String, Object> params);
	public int getStandardWorkgroupListCount(Map<String, Object> params);
	public int insertStandardWorkgroup(Map<String, Object> params);
	public Map<String, Object> selectById(Map<String, Object> params);
	public int updateStandardWorkgroup(Map<String, Object> params);
	public int delStandardWorkgroup(Map<String, Object> params);

	List<Map<String,Object>> getStandardWorkgroupList(Map<String,Object> params);
}
