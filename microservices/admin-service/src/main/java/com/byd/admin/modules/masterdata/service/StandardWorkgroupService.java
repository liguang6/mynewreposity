package com.byd.admin.modules.masterdata.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;

/** 
 * @author 作者 E-mail: 
 * @version 创建时间：2019年10月25日 下午2:23:57 
 * 类说明 
 */
public interface StandardWorkgroupService {
	public PageUtils getStandardWorkgroupListByPage(Map<String, Object> params);
	public int insertStandardWorkgroup(Map<String, Object> params);
	public Map<String, Object> selectById(Map<String, Object> params);
	public int updateStandardWorkgroup(Map<String, Object> params);
	public int delStandardWorkgroup(Map<String, Object> params);

	List<Map<String,Object>> getStandardWorkgroupList(Map<String,Object> params);
}
