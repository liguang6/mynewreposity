package com.byd.admin.modules.masterdata.dao;

import java.util.List;
import java.util.Map;

/** 
 * 物料图纸
 * @author thw: 
 * @version 创建时间：2019年9月7日 上午10:32:01 
 * 类说明 
 */
public interface MatMapDao{
	
	/**
	 * 获取物料图纸存储地址URL
	 * @param condMap material_no/MATERIAL_NO：图号
	 * @return
	 */
	public String getMatMapUrl(Map<String, Object> condMap);
	
	int getPmdMapCount(Map<String, Object> params);
	List<Map<String,Object>> getPmdMapList(Map<String, Object> params);
	
}
