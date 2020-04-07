package com.byd.qms.modules.processQuality.dao;

import java.util.List;
import java.util.Map;
import org.apache.ibatis.annotations.Param;
/**
 * 关键零部件查询
 * @author tangj
 * @email 
 * @date 2019-07-30 10:12:08
 */
public interface QmsKeyPartsDao{
	// 大巴关键零部件追踪
    public List<Map<String,Object>> getListByPage(Map<String,Object> params);
    	
	public int getListCount(Map<String,Object> params);
	
	public List<Map<String,Object>> getList(Map<String,Object> params);
	
	public List<Map<String,Object>> getOrderConfigList(Map<String,Object> params);
	
	public List<Map<String,Object>> getWorkshopList(Map<String,Object> params);
	
	// 专用车关键零部件追踪
	public List<Map<String,Object>> getListByPage02(Map<String,Object> params);
	
	public int getListCount02(Map<String,Object> params);
	
	public List<Map<String,Object>> getList02(Map<String,Object> params);
	
	public Integer getTplHeadId02(@Param("tplDetailId") String tplDetailId);
	
	public List<Map<String,Object>> getOrderConfigList02(Map<String,Object> params);
	
	public List<Map<String,Object>> getLineNameList(Map<String,Object> params);
	
	// 大巴关键零部件追溯
	
    public List<Map<String,Object>> getBusByPartsBatchList(Map<String,Object> params);
	
	public int getBusByPartsBatchCount(Map<String,Object> params);
	
    // 专用车关键零部件追溯
	
    public List<Map<String,Object>> getKeyPartsTraceBackList(Map<String,Object> params);
	
	public int getKeyPartsTraceBackCount(Map<String,Object> params);
	
}
