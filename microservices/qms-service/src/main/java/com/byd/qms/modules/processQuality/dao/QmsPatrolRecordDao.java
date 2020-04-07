package com.byd.qms.modules.processQuality.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
/**
 * 品质巡检记录
 * @author tangj
 * @email 
 * @date 2019-07-26 10:12:08
 */
public interface QmsPatrolRecordDao{

    public List<Map<String,Object>> getListByPage(Map<String,Object> params);
    	
	public int getListCount(Map<String,Object> params);
	
	public List<Map<String,Object>> getList(Map<String,Object> params);
	
	public int batchSave(Map<String,Object> params);
		
	public int batchUpdate(Map<String,Object> params);
	
	public int delete(@Param("patrolRecordNo") String patrolRecordNo);
	
	public String getMaxTempNo(Map<String,Object> params);
	
	public List<Map<String,Object>> getTemplateList(@Param("tempNo") String tempNo);
}
