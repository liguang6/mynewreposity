package com.byd.zzjmes.modules.produce.service;

import java.util.Map;
/**
 * 下料明细导入
 * @author thw
 * @email 
 * @date 2019-09-03 15:12:08
 */
public interface PmdImportService{

	public Map<String,Object> queryPmdInfo(Map<String,Object> condMap);
	public int savePmdInfo(Map<String, Object> condMap);
	
}
