package com.byd.admin.modules.masterdata.service;

import java.util.List;
import java.util.Map;

import com.byd.utils.PageUtils;

/**
 * 物料/零部件图纸
 * @author thw
 * @email 
 * @date 2019-09-03 15:12:08
 */
public interface MatMapService{
	
	/**
	 * 获取物料图纸存储地址URL
	 * @param condMap material_no/MATERIAL_NO：图号
	 * @return
	 */
	public String getMatMapUrl(Map<String, Object> condMap);
	
	PageUtils queryPmdMapPage(Map<String, Object> params);
	
	List<Map<String,Object>> getPmdMapList(Map<String, Object> params);
	
}
